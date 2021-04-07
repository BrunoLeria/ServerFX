/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package as35b;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.util.Date;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class ServerController extends Application {

    private TextField tf = new TextField();
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    // Text area for displaying contents
    TextArea ta = new TextArea();

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {

        // Create a server socket
        BorderPane borderPaneForText = new BorderPane();
        Button btnSend = new Button("|>");
        btnSend.setOnAction(e -> {

            Platform.runLater(() -> {
                try {

                    output.writeUTF(tf.getText());
                    showMessage("server: " + tf.getText() + "\n");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        });

        tf.setAlignment(Pos.BOTTOM_RIGHT);

        borderPaneForText.setCenter(tf);
        borderPaneForText.setRight(btnSend);
        borderPaneForText.setPadding(new Insets(5, 5, 5, 5));

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(new ScrollPane(ta));
        mainPane.setCenter(borderPaneForText);

        // Create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 450, 270);
        primaryStage.setTitle("Server"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        ta.setEditable(false);
        new Thread(() -> {
            try {
                // Create a server socket
                serverSocket = new ServerSocket(8000);
                Platform.runLater(()
                        -> ta.appendText("Server started at " + new Date() + '\n'));

                // Listen for a connection request
                Socket socket = serverSocket.accept();

                // Create data input and output streams
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    // Receive message from the client
                    String message = input.readUTF();

                    output.writeUTF(message);
                    Platform.runLater(() -> {
                        ta.appendText("Client: " + message + "\n");
                    });
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public void showMessage(String message){
        Platform.runLater( () -> {
            ta.appendText(message);

        });
    }
}
