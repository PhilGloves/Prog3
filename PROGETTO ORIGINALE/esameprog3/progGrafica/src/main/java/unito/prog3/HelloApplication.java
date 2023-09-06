package unito.prog3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import unito.prog3.controller.ControllerStart;

import java.io.IOException;

public class HelloApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 500, 500);
    System.out.println(BootstrapFX.bootstrapFXStylesheet());
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    stage.setTitle("Hello!");
    stage.setScene(scene);

    stage.getIcons().add(new Image(
            HelloApplication.class.getResourceAsStream( "icons/email-icon.png" )));

    stage.setResizable(false);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}