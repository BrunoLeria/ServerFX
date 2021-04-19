package chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONObject;

public class ClientApp extends Application {

    private TextArea messages = new TextArea();
    private NetworkConnection connection = createClient();    
    private JSONObject json = new JSONObject();

    private Parent createContent() {
        messages.setFont(Font.font(36));
        messages.setPrefHeight(550);
        messages.setEditable(false);

        TextField input = new TextField();
        input.setOnAction(event -> {
            String message = "Client: " + input.getText();
            input.clear();

            messages.appendText(message + "\n");

            json.append("message", message);

            DataPacket packet = new DataPacket(
                    new Encryptor().enc(json.toString().getBytes())
            );


            try {
                connection.send(packet);
            }
            catch (Exception e) {
                messages.appendText("Failed to send\n");
            }
        });

        VBox root = new VBox(20, messages, input);
        root.setPrefSize(600, 600);
        return root;
    }

    @Override
    public void init() throws Exception {
        connection.startConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    private Client createClient() {
        return new Client("127.0.0.1", 8899, data -> {
            DataPacket packet = (DataPacket) data;
            byte[] original = new Encryptor().dec(packet.getRawBytes());

            Platform.runLater(() -> {
                messages.appendText(new String(original) + "\n");
            });
        });             
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
