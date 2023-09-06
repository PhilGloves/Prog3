package unito.prog3.client;

import unito.prog3.models.Email;
import unito.prog3.models.User;
import unito.prog3.network.Connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Client {
  private User user;
  private static String URL = "127.0.0.1";
  private static int PORT = 1998;

  private Socket server;
  private ObjectInputStream input;
  private ObjectOutputStream output;

  public Client() throws IOException {

    server = new Socket(URL,PORT);

    input = new ObjectInputStream(server.getInputStream());
    output = new ObjectOutputStream(server.getOutputStream());
  }

  public void setUser(User user) {
    this.user = user;
  }
  public User getUser() {
    return user;
  }

  public static boolean isNull(Object obj) throws IllegalAccessException {
    if(obj == null)
      throw new IllegalAccessException("Argument is null!!");
    return false;
  }
  public static boolean areNull(Object obj,Object obj2,Object obj3) throws IllegalAccessException {
    isNull(obj);
    isNull(obj2);
    isNull(obj3);
    return false;
  }
  public static boolean areNull(Object obj,Object obj2) throws IllegalAccessException {
    isNull(obj);
    isNull(obj2);
    return false;
  }

  // Risposta Conferma ecc..
  public Connection response() throws IOException, ClassNotFoundException {
    return (Connection) input.readObject();
  }
  public List<Email> boxResponse() throws IOException, ClassNotFoundException {
    return (List<Email>) input.readObject();
  }
  // invio user al server login reg.
  public void sendUser(User user) throws IOException {
    output.writeObject(user);
  }


  public void forward(){}

  public void replay() throws IOException {
    System.out.println("replay");
    output.writeObject(Connection.REPLAY);
  }
  public Connection replay(Email email) throws IOException, ClassNotFoundException {
    System.out.println("replay");
    output.writeObject(Connection.REPLAY);
    output.writeObject(email);

    return response();
  }


  public Connection read(Email email) throws IOException, ClassNotFoundException {
    System.out.println("read");
    output.writeObject(Connection.READ);
    output.writeObject(email);

    return response();
  }

  public Connection delete(Email email, String box) throws IOException, ClassNotFoundException {
    System.out.println("delete");
    output.writeObject(Connection.DELETE);
    output.writeObject(email);
    output.writeObject(
            box.equalsIgnoreCase("inbox")
                    ? Connection.INBOX
                    : Connection.SENTBOX);

    return response();
  }

  public Connection send(Email email) throws IOException, ClassNotFoundException {
    System.out.println("send");
    email.setFrom(user.getUsername());
    output.writeObject(Connection.SEND);
    output.writeObject(email);
    return response();
  }

  public List<Email> box(String box) throws IOException, ClassNotFoundException {
    System.out.println("box");
    output.writeObject(
      box.equalsIgnoreCase("inbox")
            ? Connection.INBOX
            : Connection.SENTBOX);

    List<Email> emails =
            response().equals(Connection.OK)
            ? (List<Email>) input.readObject()
            :null;

    return emails;
  }

  public Connection registration(User user) throws IOException, IllegalAccessException, ClassNotFoundException {
    System.out.println("registration");
    isNull(user);

    output.writeObject(Connection.REGISTRATION);
    sendUser(user);
    return response();
  }

  public Connection login(User user) throws IOException, IllegalAccessException, ClassNotFoundException {
    System.out.println("login");
    isNull(user);

    output.writeObject(Connection.LOGIN);
    sendUser(user);
    return response();
  }

}
