package client_api.client_gui;

import client_api.ChatMassageService;
import client_api.LoggingToFile;
import client_api.MessageProcessor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class MainChatController implements Initializable, MessageProcessor {
    public javafx.scene.control.TextField inputField;
    public javafx.scene.control.Button btnSendMessage;
    public TextArea mainChatArea;
    public VBox loginPanel;
    public PasswordField passwordField;
    public TextField loginField;
    public VBox changePasswordPanel;
    public PasswordField oldPassField;
    public PasswordField newPasswordField;
    public VBox changeNickPanel;
    public TextField newNickField;
    public TextField registerLoginField;
    public PasswordField registerPasswordField;
    public TextField registerNickNameField;
    public VBox registerPanel;
    private ChatMassageService chatMassageService;
    public VBox mainChatPanel;
    public ListView<String> contactList;
    private String nickName;
    public static final String REGEX = "%&%";
    LoggingToFile loggingToFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> list = FXCollections.observableArrayList("ALL");
        contactList.setItems(list);
        contactList.getSelectionModel().select(0);
        this.chatMassageService = new ChatMassageService(this);
        chatMassageService.connect();
    }

    @Override
    public void processMessage(String message) {
        Platform.runLater(() -> parseMessage(message));
        //mainChatArea.appendText(message);
    }

    public void mockAction(ActionEvent actionEvent) {
    }

    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void sendMessage(ActionEvent actionEvent) {
        String text = inputField.getText();
        if(text.isEmpty()) return;
        String recipient = contactList.getSelectionModel().getSelectedItem();
        String message = "";
        if(recipient.equals("ALL")) message = "/" + recipient + REGEX + text;
        else message = "/w" + REGEX + recipient + REGEX + text;
        chatMassageService.send(message);
        inputField.clear();
        loggingToFile.writeLogFile(message);
    }

    public void sendAuth(ActionEvent actionEvent) {
        if(loginField.getText().isBlank() || passwordField.getText().isBlank()) return;
//        chatMassageService.connect();
        chatMassageService.send("/auth" + REGEX + loginField.getText() + REGEX + passwordField.getText());
        loggingToFile = new LoggingToFile(loginField.getText() + ".txt");
    }

    public void sendRegister(ActionEvent actionEvent) {
        if(registerLoginField.getText().isBlank() || registerPasswordField.getText().isBlank() || registerNickNameField.getText().isBlank()) return;
        chatMassageService.send("/register" + REGEX + registerLoginField.getText() + REGEX + registerPasswordField.getText() + REGEX + registerNickNameField.getText());
        loggingToFile = new LoggingToFile(registerLoginField.getText() + ".txt");
    }

    public void sendChangeNick(ActionEvent actionEvent) {
        if(newNickField.getText().isBlank()) return;
        chatMassageService.send("/change_nick" + REGEX + newNickField.getText());
    }

    public void sendChangePass(ActionEvent actionEvent) {
        if(newPasswordField.getText().isBlank() || oldPassField.getText().isBlank()) return;
        chatMassageService.send("/change_pass" + REGEX + oldPassField.getText() + REGEX + newPasswordField.getText());}

    public void sendEternalLogout(ActionEvent actionEvent) {
        chatMassageService.send("/remove");
    }

    private void parseMessage(String message){
        String[] parsedMessage = message.split(REGEX);
        switch (parsedMessage[0]){
            case  "authok:":
                this.nickName = parsedMessage[1];
                loginPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                for (int i = 0; i < 100; i++) {
                    mainChatArea.appendText(loggingToFile.readLogFile());
                }
                break;
            case "ERROR:":
                showError(parsedMessage[1]);
                break;
            case "/list:":
                parsedMessage[0] = "ALL";
                ObservableList<String> list = FXCollections.observableArrayList(parsedMessage);
                contactList.setItems(list);
                contactList.getSelectionModel().select(0);
                parsedMessage[0] = "";
                break;
            case "/change_nick_ok":
                changeNickPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                break;
            case "/change_password_ok":
                changePasswordPanel.setVisible(false);
                mainChatPanel.setVisible(true);
                break;
            case "/register_ok":
                mainChatPanel.setVisible(false);
                registerPanel.setVisible(true);
            default:
                mainChatArea.appendText(parsedMessage[0] + System.lineSeparator());
                loggingToFile.writeLogFile(parsedMessage[0]);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR!");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void returnToChat(ActionEvent actionEvent) {
        changePasswordPanel.setVisible(false);
        changeNickPanel.setVisible(false);
        mainChatPanel.setVisible(true);
    }

    public void showChangeNick(ActionEvent actionEvent) {
        mainChatPanel.setVisible(false);
        changeNickPanel.setVisible(true);
    }

    public void showChangePassword(ActionEvent actionEvent) {
        mainChatPanel.setVisible(false);
        changePasswordPanel.setVisible(true);
    }
}
