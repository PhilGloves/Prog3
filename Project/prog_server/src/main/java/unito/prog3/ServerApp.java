package unito.prog3;


// Classi di java fx
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
// --------------------------------

import unito.prog3.server.Server; //Nostra classe Server

//Gestione eccezioni
import java.io.IOException;


//Application e' una classe nativa di javafx per gestire la finestra
public class ServerApp extends Application {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;
    public static final String ICON_PATH = "icons/email-icon.png";
    private static final String APP_TITLE = "Server";

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ServerApp.class.getResource("server-view.fxml"));
            Scene scene;
            stage.setTitle(APP_TITLE);
            stage.getIcons().add(new Image(ServerApp.class.getResourceAsStream(ICON_PATH)));
            scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.show();
            Thread server = new Thread(new Server(fxmlLoader.getController()));
            server.setDaemon(true);
            server.start();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}