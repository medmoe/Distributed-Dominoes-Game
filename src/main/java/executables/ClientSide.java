package executables;

import design.TheInterface;
import client.ServerInputManager;
import client.ServerOutputManager;
import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ClientSide extends Application {

    private DataInputStream fromServer = null;
    private DataOutputStream toServer = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override // override the start method in the application class
    public void start(Stage primary) {
        //connect to the server
        try {
            Socket socket = new Socket("localhost", 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
            TheInterface mainStage = new TheInterface(primary);
            // manage communications with the server on separate threads
            ServerOutputManager serverOutput = new ServerOutputManager(toServer);
            ServerInputManager serverInput = new ServerInputManager(fromServer, mainStage, serverOutput);
            serverInput.start();
            //create the initial scene
            createInitialScene(primary, serverOutput);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

    }

    public void createInitialScene(Stage primary, ServerOutputManager output) {
        Stage initial = new Stage();
        TextField promptUser = new TextField();
        promptUser.setAccessibleHelp("Please print your name");
        HBox pane = new HBox();
        pane.getChildren().addAll(promptUser);
        Scene scene = new Scene(pane, 350, 150);

        initial.setTitle("prompt user");
        initial.setScene(scene);
        initial.show();

        promptUser.setOnAction(e -> {
            String name = promptUser.getText().trim();
            output.sendMessage(name);
            initial.hide();
            primary.show();

        });

    }
}



