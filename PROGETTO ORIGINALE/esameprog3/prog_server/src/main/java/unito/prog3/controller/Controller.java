package unito.prog3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class Controller {
  @FXML
  private TextArea logs;

  public void writeLog(String textLog){
    logs.appendText("- "+textLog+"\n");
  }
}