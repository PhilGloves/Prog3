package unito.prog3.server;

import unito.prog3.controller.Controller;
import unito.prog3.file.FileHandler;
import unito.prog3.models.Email;
import unito.prog3.models.User;
import unito.prog3.network.Connection;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server implements Runnable {

  // Dichiarazione di variabili e oggetti per il server
  private final ServerSocket server;
  private List<User> users;
  private Controller controller;

  // Costruttore del Server
  private Server() throws IllegalAccessException, IOException {
    String key = "Port";
    int port = (int) FileHandler.getConfiguration().get(key);
    this.server = new ServerSocket(port);
    this.users = FileHandler.getUsers();
  }

  // Costruttore del Server che accetta un Controller
  public Server(Controller controller) throws IllegalAccessException, IOException {
    this(); // Chiama il costruttore senza argomenti per inizializzare il server
    this.controller = controller;
  }

  // Metodo per aggiungere un utente alla lista degli utenti registrati
  private synchronized void addUser(User user) throws IOException, IllegalAccessException {
    if (!users.contains(user)) {
      users.add(user);
      FileHandler.addUser(user);
    }
  }

  // Metodo per verificare se un utente è presente nella lista degli utenti registrati
  private synchronized boolean containUser(User user) {
    return users.contains(user);
  }

  // Metodo per ottenere un utente dalla lista degli utenti registrati
  private synchronized User getUser(User user) {
    return users.get(users.indexOf(user));
  }

  // Metodo per scrivere un messaggio di log tramite il controller
  private void writeLog(String text) {
    synchronized (controller) {
      controller.writeLog(text);
    }
  }

  @Override
  public void run() {
    Thread.currentThread().setName("Server");
    writeLog("Server Online");
    while (true) {
      try {
        Socket client = server.accept();
        new Thread(new ClientHandle(client)).start();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }


  /*==========================================================*/
  /* Client Handling */
  class ClientHandle implements Runnable {
    private boolean connected = true;

    private static String INBOX = "inbox";
    private static String SENT = "sent";

    private User actual;
    private Socket client;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ClientHandle(Socket client) throws IOException {
      this.client = client;
      output = new ObjectOutputStream(client.getOutputStream());
      input = new ObjectInputStream(client.getInputStream());
    }

    @Override
    public void run() {
      System.out.println("Nuovo Thread");
      Object read;
      while (connected){
        read = null;
        try {
          checkRequest(read = request());
          handleRequest((Connection) read);
        } catch (IOException e) {
          connected= false;
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
      writeLog("- logout: "+actual.getUsername());
      System.out.println("Logout");
    }

    /** UTILS */
    // Risposta Conferma ecc..
    private Connection request() throws IOException, ClassNotFoundException {
      return (Connection) input.readObject();
    }
    // Risposta Conferma ecc..
    private void response(Connection resp) throws IOException {
      output.writeObject(resp);
    }

    private void checkRequest(Object read) throws InvalidObjectException {
      if(read == null)
        throw new IllegalArgumentException("Argument is NULL");
      if(!(read instanceof Connection))
        throw new InvalidObjectException("Argument is not a Connection");
    }
    private void handleRequest(Connection req) throws IOException {
      switch (req){
        case LOGIN -> {
          login();
        }
        case INBOX -> {
          box(Connection.INBOX);
        }
        case SENTBOX -> {
          box(Connection.SENTBOX);
        }
        case SEND -> {
          send();
        }
        case DELETE -> {
          delete(Connection.DELETE);
        }
        case DELETEREPLY -> {
          delete(Connection.DELETEREPLY);
        }
        case READ -> {
          read();
        }
        case REPLY -> {
          reply();
        }
      }
    }

    private User receiveUser() throws IOException, ClassNotFoundException {
      return (User) input.readObject();
    }
    private Email receiveEmail() throws IOException, ClassNotFoundException {
      return (Email) input.readObject();
    }


    // req file name
    private String nameBox(Connection con){
      switch (con){
        case SENTBOX -> {
          return "sent";
        }
        default -> {
          return "inbox";
        }
      }
    }


    /** REQUESTS */
    private void reply() throws IOException {
      System.out.println("reply");
      try {
        Email email = receiveEmail();
        writeLog("- "+actual.getUsername()+" try reply to: "+
                email.getTo());
        for (String to:
                email.getTo()) {
          if(!containUser(new User(to))){
            throw new IllegalAccessException();}
        }

        email.setId(FileHandler.incrementID());
        // mine
        email.setRead(true);
        FileHandler.insertReplyEmail(actual.simpleUsername(),
                SENT,
                email);
        FileHandler.insertReplyEmail(actual.simpleUsername(),
                INBOX,
                email);
        // other
        email.setRead(false);
        for (String mail:
                email.getTo()) {
          FileHandler.insertReplyEmail(
                  User.simplify(mail),
                  INBOX,
                  email);
          System.out.println("here?");
        }
        FileHandler.insertReplyEmail(
                User.simplify(email.getTo().get(0)),
                SENT,
                email);
        response(Connection.OK);
        writeLog("- "+actual.getUsername()+"'s email replied ");
      } catch (IllegalAccessException | ClassNotFoundException e) {
        response(Connection.FAIL);
        writeLog("- "+actual.getUsername()+"'s email not replied ");
      }
    }

    private void read() throws IOException {
      System.out.println("read");
      try {
        Email email = receiveEmail();
        FileHandler.setReadEmail(
                actual.simpleUsername(),
                email
        );
        response(Connection.OK);
      } catch (IllegalAccessException | ClassNotFoundException e) {
        response(Connection.FAIL);
      }
    }

    private void delete(Connection req) throws IOException{
      System.out.println("delete");
      try {
        Email email = receiveEmail();
        if(req.equals(Connection.DELETEREPLY)) {
          Integer id = (Integer) input.readObject();
          FileHandler.removeEmail(
                  actual.simpleUsername(),
                  nameBox(request()),
                  email,
                  id
          );

        }else {
          FileHandler.removeEmail(
                  actual.simpleUsername(),
                  nameBox(request()),
                  email,
                  null
          );
        }
        response(Connection.OK);
      } catch (IllegalAccessException | ClassNotFoundException e) {
        response(Connection.FAIL);
      }
    }
    private void send() throws IOException {
      System.out.println("send");
      try {
        Email email = receiveEmail();
        writeLog("- "+actual.getUsername()+" try send to: "+
                email.getTo());
        for (String to:
             email.getTo()) {
          if(!containUser(new User(to)))
            throw new IllegalAccessException();
        }
        email.setId(FileHandler.incrementID());
        FileHandler.insertEmail(actual.simpleUsername(),
                SENT,
                email);
        for (String mail:
             email.getTo()) {
          FileHandler.insertEmail(
                  User.simplify(mail),
                  INBOX,
                  email
          );
        }
        response(Connection.OK);
        writeLog("- "+actual.getUsername()+"'s email sent ");
      } catch (IllegalAccessException | ClassNotFoundException e) {
        System.out.println(e.getMessage());
        response(Connection.FAIL);
        writeLog("- "+actual.getUsername()+"'s email not sent ");
      }
    }
    private void box(Connection req) throws IOException {
      System.out.println("box");
      String box = nameBox(req);

      try {
        response(Connection.OK);
        output.writeObject(FileHandler.getMailByBox(
                actual.simpleUsername(),
                box
        ));
      } catch (IOException | IllegalAccessException e) {
        response(Connection.FAIL);
      }
    }



    private void login() throws IOException {
      System.out.println("login");
      User userRec;
      User userChecker;
      try {
        userRec = receiveUser();
        writeLog("- Try login: "+ userRec.getUsername());
        if(!containUser(userRec)){
          throw new ClassNotFoundException();
        }
        userChecker = getUser(userRec);
        if(!userChecker.passwordChecker(userRec)){
          throw new ClassNotFoundException();
        }
        actual = userChecker;
        response(Connection.OK);
        writeLog("- Logged: "+ userRec.getUsername());
      }catch (ClassNotFoundException e) {
        response(Connection.FAIL);
        writeLog("- Fail Login");
      }
    }

  } //END CLASS

} //END CLASS
