package unito.prog3.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

public class Email implements Serializable {

  private int id;

  private String from;
  private List<String> to;
  private String subject;
  private String body;
  private Date date;

  private boolean isRead;

  private static final long serialVersionUID = 3245324532L; //used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization. If the receiver has loaded a class for the object that has a different serialVersionUID than that of the corresponding sender's class, then deserialization will result in an InvalidClassException
  private Email replyTo;

  // Constructor
  public Email() {
    this(0,null,null,null,null,null,false);
  }
  public Email(int id) {
    this(id,null,null,null,null,null,false);
  }
  public Email(List<String> to, String subject, String body, Date date) {
    this(0,null,to,subject,body,date,false);
  }
  public Email(int id, String from, List<String> to, String subject, String body, Date date) {
    this(id,from,to,subject,body,date,false);
  }
  public Email(int id, String from, List<String> to, String subject, String body, Date date, boolean isRead) {
    this.id = id;
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.body = body;
    this.date = date;
    this.isRead = isRead;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("from:'").append(from).append("'\n")
            .append("to: ").append(to).append("\n")
            .append("subject:'").append(subject).append("\n")
            .append("date: ").append(date).append("\n\n")
            .append(body).append("\n");
    if(replyTo!=null)
      sb.append("\n--reply to--\n").append(replyTo).append("\n");
    return sb.toString();
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Email email = (Email) o;
    return id == email.id;
  }
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }


  public static @NotNull Email replySet(Email toReply, String username){
    Email email = new Email();
    email.setReplyTo(toReply);
    email.setSubject(toReply.getSubject());
    //email.setBody(toReply.getBody());

    email.setFrom(username);
    email.setDate(new Date());
    return email;
  }

  public static @NotNull Email createReply(Email toReply, String username){
    Email email = replySet(toReply, username);

    email.setTo(Arrays.asList(toReply.getFrom()));
    return email;
  }
  public static @NotNull Email createReplyAll(Email toReply, String username){
    Email email = replySet(toReply, username);

    List<String> to = new ArrayList<>(Arrays.asList(toReply.getFrom()));
    to.addAll(toReply.getTo());
    to.remove(username);

    email.setTo(to);
    return email;
  }

  public Email getReplyTo() {
    return replyTo;
  }
  public void setReplyTo(Email replyTo) {
    this.replyTo = replyTo;
  }

  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }

  public String getFrom() {
    return from;
  }
  public void setFrom(String from) {
    this.from = from;
  }

  public List<String> getTo() {
    return to;
  }
  public void setTo(List<String> to) {
    this.to = to;
  }

  public String getSubject() {
    return subject;
  }
  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }
  public void setBody(String body) {
    this.body = body;
  }

  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }

  public boolean isReaded() {
    return isRead;
  }
  public void setRead(boolean read) {
    isRead = read;
  }
}
