package unito.prog3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class ClientApp extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("hello-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 500, 500);
    System.out.println(BootstrapFX.bootstrapFXStylesheet());
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    stage.setTitle("Hello!");
    stage.setScene(scene);

    stage.getIcons().add(new Image(
            ClientApp.class.getResourceAsStream( "icons/email-icon.png" )));

    stage.setResizable(false);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}