package unito.prog3.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import unito.prog3.models.Email;
import unito.prog3.models.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileHandler {

  public static final String SOURCE = "data/";

  public static final String INBOX  = "/inbox.json";
  public static final String SENT   = "/sent.json";

  public static final String USERS  = "users.json";
  public static final String CONF = "conf.json";

  private static final Map<File, Lock> FILE_LOCKS = new ConcurrentHashMap<>();

  /* UTILS */
  private static File getFileByUB(String username,
                                  String box){
    String fileName = SOURCE+username+"/"+box.toLowerCase()+".json";
    return new File(fileName);
  }
  private static File getFileConf(){
    String fileName = SOURCE+CONF;
    return new File(fileName);
  }
  private static File getFileUsers(){
    String fileName = SOURCE+USERS;
    return new File(fileName);
  }


  //Simple method no thread
  private static List<Email> readEmails(File file) {
    List<Email> emails;
    ObjectMapper mapper = new ObjectMapper();
    try {
      emails = mapper.readValue(file,
              new TypeReference<>(){});
    } catch (IOException e) {
      emails = new ArrayList<>();
    }
    return emails;
  }
  private static void writeEmails(File file, List<Email> emails) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(file, emails);
  }

  private static Map<String,Object> readConf() {
    Map<String, Object> conf;
    ObjectMapper mapper = new ObjectMapper();
    try {
      conf = mapper.readValue(getFileConf(),
              new TypeReference<>(){});
    } catch (IOException e) {
      conf = new HashMap<>();
    }
    return conf;
  }
  private static void writeConf(Map<String,Object> conf) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(getFileConf(), conf);
  }

  private static List<User> readUsers() {
    List<User> users;
    ObjectMapper mapper = new ObjectMapper();
    try {
      users = mapper.readValue(getFileUsers(),
              new TypeReference<>(){});
    } catch (IOException e) {
      users = new ArrayList<>();
    }
    return users;
  }
  private static void writeUsers(List<User> users) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(getFileUsers(), users);
  }

  private static boolean isNull(Object obj) throws IllegalAccessException {
    if(obj == null)
      throw new IllegalAccessException("Argument is null!!");
    return false;
  }
  private static boolean areNull(Object obj,Object obj2,Object obj3) throws IllegalAccessException {
    isNull(obj);
    isNull(obj2);
    isNull(obj3);
    return false;
  }
  private static boolean areNull(Object obj,Object obj2) throws IllegalAccessException {
    isNull(obj);
    isNull(obj2);
    return false;
  }

  /* Public Method */

  public static List<Email> getMailByBox(String username,
                                         String box) throws IllegalAccessException {
    areNull(username, box);
    List<Email> emails;
    File json = getFileByUB(username, box);

    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      emails = readEmails(json);
    }
    return emails;
  }
  public static void removeEmail(String username,
                                 String box,
                                 Email email,
                                 Integer idReply) throws IllegalAccessException, IOException {
    areNull(username, box, email);

    List<Email> emails;
    File json = getFileByUB(username, box);
    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      emails = readEmails(json);
      email = emails.get(emails.indexOf(email));
      emails.remove(email);

      if(idReply == null){
        if(email.getReplyTo() != null)
          emails.add(email.getReplyTo());
      }else{
        Email iter = email;
        boolean notFound = true;
        while (iter != null && iter.getReplyTo() != null && notFound){
          System.out.println(iter.getId());
          if(iter.getReplyTo().getId() == idReply.intValue()){
            iter.setReplyTo(iter.getReplyTo()
                                .getReplyTo());
            notFound = false;
          }
          iter = iter.getReplyTo();
        }
        emails.add(email);
      }
      writeEmails(json, emails);
    }
  }
  public static void insertEmail(String username,
                                 String box,
                                 Email email) throws IllegalAccessException, IOException {
    areNull(username, box, email);

    List<Email> emails;
    File json = getFileByUB(username, box);
    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      emails = readEmails(json);
      emails.add(email);
      writeEmails(json, emails);
    }
  }
  public static void setReadEmail(String username,
                                 Email email) throws IllegalAccessException, IOException {
    areNull(username, email);

    List<Email> emails;
    File json = getFileByUB(username, "inbox");
    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      emails = readEmails(json);
      emails.get(emails.indexOf(email)).setRead(true);
      writeEmails(json, emails);
    }
  }

  public static void insertReplyEmail(String username,
                                 String box,
                                 Email email) throws IllegalAccessException, IOException {
    areNull(username, box, email);
    isNull(email.getReplyTo());

    List<Email> emails;
    File json = getFileByUB(username, box);
    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      emails = readEmails(json);
      emails.remove(email.getReplyTo());
      emails.add(email);
      writeEmails(json, emails);
    }
  }


  /* ACCESSO solo main thread server */

  /*get configuration da server per caricare i dati
  * possiamo usare anche informazioni come per esempio
  * l'indirizzo e la porta alla quale ascoltare cosi
  * da evitare una ricompilazione */
  public static Map<String, Object> getConfiguration() throws IllegalAccessException {
    File json = getFileConf();
    Map<String, Object> conf;
    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
       conf = readConf();
    }
    return conf;
  }

  public static int incrementID()
          throws IOException, IllegalAccessException {
    File json = getFileConf();
    Map<String, Object> conf;
    String key = "ID";
    int actual;

    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      conf = readConf();
      actual = (Integer)conf.get(key);
      conf.replace(key, actual+1);
      writeConf(conf);
    }
    return actual;
  }

  public static void createFiles(String username) throws IllegalAccessException, IOException {
    isNull(username);

    String userPath = SOURCE+username;
    Files.createDirectories(Paths.get(userPath));

    new File(userPath+INBOX).createNewFile();
    new File(userPath+SENT).createNewFile();
  }

  public static List<User> getUsers() throws IllegalAccessException {
    List<User> users;
    File json = getFileUsers();

    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      users = readUsers();
    }
    return users;
  }
  public static void addUser(User user) throws IllegalAccessException, IOException {
    List<User> users;
    File json = getFileUsers();

    synchronized (FILE_LOCKS.computeIfAbsent(
            json, k-> new ReentrantLock()
    )){
      users = readUsers();
      users.add(user);
      writeUsers(users);
      createFiles(user.simpleUsername());
    }
  }


}
