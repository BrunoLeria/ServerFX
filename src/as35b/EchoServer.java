/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package as35b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 *
 * @author Usuario
 */
public class EchoServer extends Application {

    protected static boolean serverContinue = true;
    private final static String newline = "\n";
    private ServerSocket serverSocket = null;

    @FXML
    private final TextArea ta;

    public EchoServer() {
        this.ta = new TextArea();
    }
    
    @FXML
    public void append(){
        System.out.println("Botton");
        ta.appendText("Teste");
    }
    

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();        

        System.out.println("Tela Pronta");

        try {
            serverSocket = new ServerSocket(8899);
            showMessage("Connection Socket Created");
            System.out.println("Connection Socket Created");
            try {
                serverSocket.setSoTimeout(10000);
                showMessage("Waiting for Connection");
                System.out.println("Waiting for Connection");
                
                Platform.runLater(() ->{
                    try {
                        runThread(serverSocket.accept());
                    } catch (IOException ex) {
                        Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (IOException e) {
                showMessage("Accept failed." + e.getMessage());
                System.exit(1);
            }
        } catch (IOException e) {
            showMessage("Could not listen on port: 10008." + e.getMessage());
            System.exit(1);
        } 
    }

    /**
     * The main method is only needed for the IDE with limited JavaFX
     * support.Not needed for running from the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void showMessage(String message) {
        Platform.runLater(() -> {
            ta.appendText(message);
        });
    }

    public void runThread(Socket clientSocket) {
        System.out.println("serverSocket.accept()");
        new Thread(() -> {
            showMessage("New Communication Thread Started");
            System.out.println("New Communication Thread Started");
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader in = new BufferedReader(inputStreamReader);
                String inputLine;
                System.out.println("inputLine");
                while ((inputLine = in.readLine()) != null) {
                    showMessage("Server: " + inputLine);
                    System.out.println("Server: " + inputLine);
                    if (inputLine.equals("?")) {
                        inputLine = "\"Bye.\" ends Client, "
                                + "\"End Server.\" ends Server";
                    }

                    out.println(inputLine);

                    if (inputLine.equals("Bye.")) {
                        break;
                    }

                    if (inputLine.equals("End Server.")) {
                        serverContinue = false;
                    }
                }                    
                in.close();
                clientSocket.close();
                    
            } catch (IOException e) {
                showMessage("Problem with Communication Server");
                System.exit(1);
            }
        }).start();
    }
}

