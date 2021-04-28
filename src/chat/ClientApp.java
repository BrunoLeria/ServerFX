package chat;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.json.JSONObject;

public class ClientApp extends Application {
   
    @FXML
    private TextField user = new TextField();
    @FXML
    private ChoiceBox type;
    @FXML
    private TextField port = new TextField("8899");;
    @FXML
    private TextField ip = new TextField("127.0.0.1");
    private NetworkConnection connection;
    private URL url;
    private Stage pStage;

    @Override
    public void init(){
        //TODO
    }  

    @Override
    public void start(Stage primaryStage) throws Exception {
        url = new File("src/chat/Client.fxml").toURI().toURL();
        
        type = new ChoiceBox();
        
        type.getItems().add("Usuario");
        type.getItems().add("Médico");
        type.getItems().add("Admin");   
        
        primaryStage.setScene(new Scene(FXMLLoader.load(url)));
        
        setPrimaryStage(primaryStage);
        pStage = primaryStage;
        
        primaryStage.show();            
    }
    
    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    public void iniciar() throws Exception{
        int porta = Integer.parseInt(port.getText());
        
        
        
        connection.startConnection();
        
        cadastrar();
        
        connection = new Client(ip.getText(), porta, data -> {
            DataPacket packet = (DataPacket) data;
            byte[] original = new Encryptor().dec(packet.getRawBytes());

            Platform.runLater(() -> {
                user.appendText(new String(original) + "\n");
            });
        });

        JSONObject recebido = new JSONObject(connection.getInputStream());
        switch(recebido.get("cod").toString()){
            case "11":
                url = new File("src/chat/Formulario.fxml").toURI().toURL();
                getPrimaryStage().setScene(new Scene(FXMLLoader.load(url)));
                getPrimaryStage().show();   
                JSONObject resposta = new JSONObject();
                break;
            case "5":
                connection.closeConnection();
                System.exit(1);
                break;
            case "8":
                if(recebido.get("covid").toString().equals("true")){
                    url = new File("src/chat/CovidPos.fxml").toURI().toURL();
                    getPrimaryStage().setScene(new Scene(FXMLLoader.load(url)));
                    getPrimaryStage().show();
                }else{
                    url = new File("src/chat/CovidNeg.fxml").toURI().toURL();
                    getPrimaryStage().setScene(new Scene(FXMLLoader.load(url)));
                    getPrimaryStage().show();
                }
                break;
            default:
                connection.closeConnection();                       
                System.exit(1);
                break;
        }      
    }
    
    private void cadastrar(){
        try {
            JSONObject cadastro = new JSONObject();
	    cadastro.put("nome", user.getText());
	    cadastro.put("tipo", type.getValue());
            cadastro.put("cod", "1");
            String cadastroString = cadastro.toString();
            
            JOptionPane.showMessageDialog(null,"Dados recebidos:" + cadastroString);
            
            connection.send(cadastro);
            System.out.println(cadastroString);
            
        }catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    
    public Stage getPrimaryStage() {
        return pStage;
    }

    private void setPrimaryStage(Stage pStage) {
        this.pStage = pStage;
    }

    
    
    public static void main(String[] args) {
        launch(args);
    }
    
    
}
