package unito.prog3.models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

  private String username;
  private String password;
  private static final long serialVersionUID = 3245324532L; //used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object that are compatible with respect to serialization. If the receiver has loaded a class for the object that has a different serialVersionUID than that of the corresponding sender's class, then deserialization will result in an InvalidClassException

  public User() {
    this(null,null);
  }
  public User(String username) {
    this(username, null);
  }
  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public String toString() {
    return "User{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            "}\n";
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(username, user.username);
  }
  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  public boolean passwordChecker(User user){
    return this.password.equals(user.password);
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public String simpleUsername(){
    return username.split("@")[0];
  }
  public static String simplify(String username){
    return username.split("@")[0];
  }

  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
}
