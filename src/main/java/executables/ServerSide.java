package executables;

import server.Server;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;

public class ServerSide extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override // override the start method in the application class
    public void start(Stage primary) {
        int portNumber = 8000;

        TextArea ta = new TextArea();
        ScrollPane scrollPane = new ScrollPane(ta);
        Scene scene = new Scene(scrollPane, 360, 320);

        primary.setTitle("Domino Server");
        primary.setScene(scene);
        primary.show();

        new Server(portNumber).start();

    }
}





