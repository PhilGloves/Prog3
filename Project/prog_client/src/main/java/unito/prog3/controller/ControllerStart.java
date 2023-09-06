package unito.prog3.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import unito.prog3.ClientApp;
import unito.prog3.client.Client;
import unito.prog3.models.User;
import unito.prog3.network.Connection;

import java.io.IOException;

public class ControllerStart {

  // SOCKET
  private Client client;

  @FXML
  private TextField username;
  @FXML
  private PasswordField password;
  @FXML
  private Label errorMessage;

  public ControllerStart(){

  }

  //******************************************************************
  // Login Section
  @FXML
  public void switchToMain(ActionEvent event) {
    System.out.println(username.getText());
    System.out.println(password.getText());

    try {
      if(correctUsername(username.getText())
              && checkAccount(username.getText(), password.getText())){
        openMain(event);
      }else {
        errorMessage.setText("Credenziali non valide");
      }
    } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
      errorMessage.setText("Server Down");
    }
  }

  private boolean checkAccount(String username, String password) throws IOException, ClassNotFoundException, IllegalAccessException {
    client = Client.getInstance();
    return client.login(new User(username,password))
                  .equals(Connection.OK);
  }

  private boolean correctUsername(String username){
    if(!username.endsWith("@prova.com") ||
        username.split("@").length>2)
      return false;
    return true;
  }

  private void openMain(ActionEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("main-view.fxml"));
    Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    Scene scene = new Scene(fxmlLoader.load(), 1210, 725);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    stage.setTitle("Welcome!");
    stage.setScene(scene);
    stage.setResizable(false);
    ((ControllerMain)fxmlLoader.getController()).init(client, username.getText(), stage);
    stage.setX(250);
    stage.show();
  }

}