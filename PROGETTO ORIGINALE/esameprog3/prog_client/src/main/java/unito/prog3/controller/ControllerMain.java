package unito.prog3.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import unito.prog3.client.Client;
import unito.prog3.models.Email;
import unito.prog3.network.Connection;

import java.io.IOException;
import java.util.*;

public class ControllerMain {

  //********************************************
  // LOGIC
  //********************************************
  private Client client;
  private List<Email> emailList;
  //private ObservableList<Email>;

  private  boolean connected = true;
  private  boolean isReply = false;

  //********************************************
  // VIEW
  //********************************************
  //top
    //Error handle
  @FXML
  private HBox error_box;
  @FXML
  private Label error_text;

  //left
  @FXML
  private Label username;  //example: username@prova.com

  //middle view
  @FXML
  private Label selected_box;
  @FXML
  private Label box_size;

  //MailsView
  private ItemMail focussed;

  @FXML
  private VBox mail_list;

  //right
  @FXML
  private VBox none_mail;
  @FXML
  private ScrollPane mails_wrapper;
  @FXML
  private HBox mail_head;
  @FXML
  private VBox mails_inner_wrapper;
  @FXML
  private Email actual; // per i reply

  //MAIN
  @FXML
  private AnchorPane mainView;
  @FXML
  private AnchorPane viewNewMail;
  //New mail
  @FXML
  private VBox newWrapper;
  @FXML
  private TextField toField;
  @FXML
  private TextField sobjectField;
  @FXML
  private TextArea bodyField;
  @FXML
  private Label errorMsg;

  @FXML
  private Label title_new;



  //******************************************************************
  // INIT Section
  public void init(Client client, String emailText, Stage stage){
    this.client = client;
    username.setText(emailText);
    selected_box.setText("Inbox");
    Thread loop = new Thread(new LoopUpdate());
    loop.start();
    stage.setOnCloseRequest((we)-> loop.interrupt());
  }

  // Utils
  private void clearMailListView()
  {
    mail_list.getChildren().clear();
  }
  private void emptyMailListView() {
    clearMailListView();
    mail_list.getChildren().add(new StackPane(){
      public StackPane empty(){
        this.alignmentProperty().set(Pos.CENTER);
        Label text = new Label("no messages");

        this.getStyleClass().add("empty-mail-list-box");
        text.getStyleClass().add("empty-mail-list-box-description");
        this.getChildren().add(text);
        return this;
      }
    }.empty());
  }

  private void refreshList(){
    new Thread(new RefreshBox()).start();
  }

  private void loseConnection(){
    connected = false;
    System.out.println("Server non raggiunto");
    Platform.runLater(()->{
      setTextError("Server down!!!");
      showErrorBox();});
  }

  // Error handle
  private void showErrorBox(){
    error_box.setVisible(true);
  }
  private void hideErrorBox(){
    error_box.setVisible(false);
  }
  private void setTextError(String text){
    error_text.setText(text);
  }

  // Email view
  private void showNoneEmail(){
    none_mail.setVisible(true);
    mails_wrapper.setVisible(false);
    mail_head.setVisible(false);
    mails_inner_wrapper.getChildren().clear();
  }
  private void clearActualMail(){
    none_mail.setVisible(false);
    mails_wrapper.setVisible(true);
    mail_head.setVisible(true);
    mails_inner_wrapper.getChildren().clear();
  }

  private void showMail( Email email){
    clearActualMail();

    VBox mailBox = createViewBoxMail(email);
    mails_inner_wrapper.getChildren().add(mailBox);

    Email replyTo = email.getReplyTo();
    while (replyTo != null) {
      mails_inner_wrapper.getChildren().add(createViewBoxMailReply(replyTo));
      replyTo = replyTo.getReplyTo();
    }
    actual = email;
  }
  //=================VIEW_SHOW_MAIL===================
  private VBox createViewBoxMail(Email mail){
    VBox mailBox = new VBox();
    mailBox.getStyleClass().add("mail-card");

    //HEAD
    HBox head = createBoxMailHead();
    Label iconHead = createIconHead(""+mail.getFrom().charAt(0));
    VBox coreHead = createCoreHead(mail.getFrom(),mail.getTo().toString());
    Label date = new Label(mail.getDate().toString());
    date.getStyleClass().add("mail-card-date");

    head.getChildren().addAll(iconHead, coreHead, date);

    Label title = new Label(mail.getSubject());
    title.getStyleClass().add("mail-card-subject");

    VBox bodyBox = createBoxMailBody(mail.getBody());

    mailBox.getChildren().addAll(head, title, bodyBox);
    return mailBox;
  }
  private VBox createViewBoxMailReply(Email mail){
    VBox mailBox = new VBox();
    mailBox.getStyleClass().add("mail-card");

    //HEAD
    HBox head = createBoxMailHead();
    Label iconHead = createIconHead(""+mail.getFrom().charAt(0));
    VBox coreHead = createCoreHead(mail.getFrom(),mail.getTo().toString());
    Label date = new Label(mail.getDate().toString());
    date.getStyleClass().add("mail-card-date");
    FontAwesomeIconView trash = deleteReply();
    trash.setId(String.valueOf(mail.getId()));

    head.getChildren().addAll(iconHead, coreHead, trash, date);

    Label title = new Label(mail.getSubject());
    title.getStyleClass().add("mail-card-subject");

    VBox bodyBox = createBoxMailBody(mail.getBody());

    mailBox.getChildren().addAll(head, title, bodyBox);
    return mailBox;
  }
  private HBox createBoxMailHead(){
    HBox head = new HBox();
    head.getStyleClass().add("mail-card-head");
    head.setAlignment(Pos.CENTER_LEFT);

    AnchorPane.setTopAnchor(head, 0.0);
    AnchorPane.setLeftAnchor(head, 0.0);
    AnchorPane.setRightAnchor(head, 0.0);
    AnchorPane.setBottomAnchor(head, 501.0);
    return head;
  }
  private Label createIconHead(String letter){
    Label iconHead = new Label();
    iconHead.setPrefHeight(50);
    iconHead.setPrefWidth(50);
    iconHead.setAlignment(Pos.CENTER);
    iconHead.getStyleClass().add("mail-card-icon");
    iconHead.setText(letter.toUpperCase());
    return iconHead;
  }
  private VBox createCoreHead(String fromMail, String toMail){
    VBox coreHead = new VBox();
    HBox.setHgrow(coreHead, Priority.ALWAYS);
    coreHead.setAlignment(Pos.CENTER_LEFT);

    Label from = new Label(fromMail);
    Label to = new Label(toMail);

    coreHead.getStyleClass().add("mail-card-head-mid");
    from.getStyleClass().add("mail-card-from");
    to.getStyleClass().add("mail-card-to");

    coreHead.getChildren().addAll(from, to);
    return coreHead;
  }
  private VBox createBoxMailBody(String bodyMail){
    VBox bodyBox = new VBox();
    bodyBox.getStyleClass().add("mail-card-body");
    Label body = new Label(bodyMail);
    body.setWrapText(true);
    body.getStyleClass().add("mail-card-body-text");
    body.setTextAlignment(TextAlignment.JUSTIFY);
    bodyBox.getChildren().add(body);
    return bodyBox;
  }

  private FontAwesomeIconView deleteReply(){
    FontAwesomeIconView trash = new FontAwesomeIconView();
    trash.setGlyphName("TRASH");
    trash.setFill(Paint.valueOf("#e14343"));
    trash.setSize("20");
    trash.setCursor(Cursor.HAND);
    //trash.setId("");
    trash.setOnMouseClicked(e -> {
      try {
        System.out.println(actual.getId());
        System.out.println(trash.getId());
        client.deleteReply(actual,selected_box.getText(), Integer.valueOf(trash.getId()));
        System.out.println("risposta??");
        showNoneEmail();
        refreshList();
      } catch (IOException | ClassNotFoundException ex) {
        loseConnection();
      }
    });
    return trash;
  }

  //******************************************************************
  // BOX Section
  @FXML
  public void boxClicked(MouseEvent event){
    HBox box = (HBox) event.getSource();
    Label label = (Label) box.getChildren().get(1);
    selected_box.setText(label.getText());
    refreshList();
  }

  // ViewNEWMail
  @FXML
  public void showViewNewMail(){
    newWrapper.translateXProperty().set(0);
    viewNewMail.setVisible(true);
    animate(1);
    title_new.setText("New Mail");
  }
  @FXML
  public void hideViewNew(){
    animate(0);
    viewNewMail.setVisible(false);
    cleanNewMail();
  }
  private void animate(int t){
    Animation animation = new Timeline(
          new KeyFrame(Duration.millis(100),
                  new KeyValue(viewNewMail.opacityProperty(), t)),
          new KeyFrame(Duration.millis(150),
                  new KeyValue(viewNewMail.translateYProperty(), 0)),
          new KeyFrame(Duration.millis(200),
                  new KeyValue(viewNewMail.opacityProperty(), t)));
    animation.play();
  }
  private void cleanNewMail(){
    toField.setText("");
    sobjectField.setText("");
    bodyField.setText("");
    errorMsg.setText("");
    isReply=false;
  }

  // SEND email
  @FXML
  public void sendMail(){
    String tos      = toField.getText();
    String subject  = sobjectField.getText();
    String body     = bodyField.getText();

    //Field isBlank
    if(tos.isBlank() || subject.isBlank() || body.isBlank()){
      errorMsg.setText("Fields are blank");
      return;
    }
    //Field emails check
    String to[]    = tos.split(",");
    for (var singleTo:
         to) {
      if(!singleTo.endsWith("@prova.com") ||
              singleTo.split("@").length>2)
      {
        errorMsg.setText("Incorrect emails");
        return;
      }
    }

    Email email = new Email(0, client.getUser().getUsername(),
            Arrays.asList(to), subject, body, new Date());
    try {
      if(isReply){
        email.setReplyTo(actual);
        sendReply(email);
      }
      else
        sendEmail(email);
    } catch (IOException | ClassNotFoundException e) {
      loseConnection();
    }
  }
  private void sendEmail(Email email) throws IOException, ClassNotFoundException {
    if(client.send(email).equals(Connection.OK))
      hideViewNew();
    else
      errorMsg.setText("Some email doesnt exist");
  }
  private void sendReply(Email email) throws IOException, ClassNotFoundException {
    if(client.replay(email).equals(Connection.OK))
      hideViewNew();
    else
      errorMsg.setText("Some email doesnt exist");
  }


  // FORWARD
  @FXML
  public void forwardActual(){
    showViewNewMail();

    title_new.setText("Forward");
    sobjectField.setText(actual.getSubject());
    bodyField.setText("\n\n\n\n---Forward to---\n"+actual.toString());
  }
  // REPLY/ALL
  @FXML
  public void replyActual(){
    showViewNewMail();

    title_new.setText("Reply");
    toField.setText(actual.getFrom());
    sobjectField.setText(actual.getSubject());
    isReply = true;
  }
  @FXML
  public void replyAllActual(){
    showViewNewMail();

    title_new.setText("Reply All");
    String to = giveTos();
    toField.setText(to);
    sobjectField.setText(actual.getSubject());
    isReply = true;
  }
  private String giveTos(){
    StringJoiner sj = new StringJoiner(",");
    sj.add(actual.getFrom());
    List<String> to = actual.getTo();
    to.remove(client.getUser().getUsername());
    for (String singleTo:
            to) {
      sj.add(singleTo);
    }
    return sj.toString();
  }
  // DELETE
  @FXML
  public void deleteActual(){
    try {
      client.delete(actual,selected_box.getText());
      showNoneEmail();
      refreshList();
    } catch (IOException | ClassNotFoundException e) {
      loseConnection();
    }

  }

  //===========VIEW ITEMS
  private class ItemMail extends HBox{

    private final Email mail;
    private FontAwesomeIconView dotG;
    public ItemMail ( Email mail){
      super();
      this.mail = mail;
      initItem();
    }

    private void initItem() {
      settingStyle();
      settingItems();
      this.setOnMouseClicked(e->
        {
          if(focussed == null || focussed != this){
            if(focussed != null)
              focussed.getStyleClass().remove(
                      "item-mail-focussed");
            focussed = this;
          }
          dotG.setVisible(false);
          this.getStyleClass().add("item-mail-focussed");

          showMail(mail);
          if(!mail.isReaded() && !selected_box.getText().equals("Sent")){
            new Thread(new NotifyIsRead(mail)).start();
          }
        });
      if(selected_box.getText().equals("Sent"))
        dotG.setVisible(false);
    }

    private void settingItems() {
      VBox read = createBoxRead();
      read.getChildren().add(creatDot());

      VBox core = createBoxCore();

      this.getChildren().addAll(read,core);
    }

    private VBox createBoxCore(){
      VBox core = new VBox();

      core.getStyleClass().add("item-mail-view");
      core.setMaxHeight(100.0);
      core.setMaxWidth(250.0);

      //Label content
      Label fromBox     = new Label(mail.getFrom());
      Label subjectBox  = new Label(mail.getSubject());
      Label contentBox  = new Label(mail.getBody());

      fromBox.getStyleClass().add("item-mail-from");
      subjectBox.getStyleClass().add("item-mail-title");
      contentBox.getStyleClass().add("item-mail-preview");
      contentBox.setMaxHeight(100.0 / 2);

      core.getChildren().addAll(fromBox,subjectBox,contentBox);

      HBox.setHgrow(core, Priority.ALWAYS);
      return core;
    }
    private VBox createBoxRead(){
      VBox read = new VBox();
      read.setAlignment(Pos.TOP_CENTER);
      read.getStyleClass().add("item-mail-toread");
      read.setPrefWidth(20);
      return read;
    }
    private FontAwesomeIconView creatDot(){
      FontAwesomeIconView dot = new FontAwesomeIconView();
      dot.setGlyphName("CIRCLE");
      dot.setFill(Paint.valueOf("#00b2ff"));
      dot.setSize("15");
      dot.setVisible(mail.isReaded()?false:true);
      dotG = dot;
      return dot;
    }

    private void settingStyle() {
      this.setMinHeight(75);
      this.setMaxWidth(250);
      this.setPrefWidth(200);
      this.setMinWidth(150);
      this.getStyleClass().add("item-mail");
    }

    public Email getMail(){
      return mail;
    }
  }

  //=================================================================
  //==============THREADS===========================

  //============REFRESH====================
  private class LoopUpdate implements Runnable{
    @Override
    public void run() {
      boolean run = true;
      while (run){
        try {
          refreshList();
          Thread.sleep(5000);
        } catch (InterruptedException e){
          run = false;
        }
      }
    }
  }

  private class RefreshBox implements Runnable{
    @Override
    public void run() {
      Platform.runLater(()->hideErrorBox());
      try {
        if(!connected){
          client.riConnect();
          connected=true;
        }
        loadMails();
      } catch (IOException | ClassNotFoundException e) {
        loseConnection();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    private void loadMails() throws IOException, ClassNotFoundException {
      emailList = client.box(selected_box.getText());
      if(emailList == null || emailList.size()==0)
        showEmptyList();
      else {
        Collections.reverse(emailList);
        Platform.runLater(()->{
          box_size.setText(emailList.size()+" mails");
          clearMailListView();
          for (var mail: emailList) {
            mail_list.getChildren().add(
                    new ItemMail(mail)
            );
          }
        });
      }
    }
    private void showEmptyList() {
      Platform.runLater(()->{
        box_size.setText("");
        emptyMailListView();
      });
    }
  }


  //==========READ==================
  public class NotifyIsRead implements Runnable{
    private final Email email;

    public NotifyIsRead(Email email) {
      this.email = email;
    }
    @Override
    public void run() {
      try {
        client.read(email);
      } catch (IOException | ClassNotFoundException e) {
        loseConnection();
      }

    }
  }


}
