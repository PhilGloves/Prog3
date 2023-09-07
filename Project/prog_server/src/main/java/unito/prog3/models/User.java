package unito.prog3.models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String username;
    private String password;
    private static final long serialVersionUID = 123456789L;
    // Genera un nuovo serialVersionUID
    //used during deserialization to verify that the sender
    // and receiver of a serialized object have loaded classes
    // for that object that are compatible with respect to serialization.
    // If the receiver has loaded a class for the object that has a different
    // serialVersionUID than that of the corresponding sender's class,
    // then deserialization will result in an InvalidClassException


    // Costruttori

    public User() {
        this(null, null);
    }

    public User(String username) {
        this(username, null);
    }

    public User(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        this.username = username;
        this.password = password;
    }

    // Metodi Get

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Metodi Set

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Altri metodi

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                (password != null ? ", password='" + password + '\'' : "") +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public boolean passwordChecker(User user){
        return this.password.equals(user.password);
    }

    public String simpleUsername(){
        return username.split("@")[0];
    }

    public static String simplify(String username){
        return username.split("@")[0];
    }
}
