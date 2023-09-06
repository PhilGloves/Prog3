package unito.prog3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import unito.prog3.server.Server;

import java.io.IOException;

public class ServerApp extends Application {
  @Override
  public void start(Stage stage){
    try{
      FXMLLoader fxmlLoader = new FXMLLoader(
              ServerApp.class.getResource("server-view.fxml"));
      Scene scene;
      stage.setTitle("Server");
      stage.getIcons().add(new Image(
              ServerApp.class.getResourceAsStream( "icons/email-icon.png" )));

      scene = new Scene(fxmlLoader.load(), 500, 500);
      stage.setScene(scene);
      stage.show();
      Thread server = new Thread(new Server(fxmlLoader.getController()));
      server.setDaemon(true);
      server.start();
    } catch (IOException | IllegalAccessException e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch();
  }
}