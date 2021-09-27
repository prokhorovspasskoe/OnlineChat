package client_api.client_gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {
    public static void main(String[] args) {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/main_chat.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Online chat");
        primaryStage.show();
    }
}
