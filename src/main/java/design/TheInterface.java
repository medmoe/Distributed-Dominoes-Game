package design;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TheInterface {

    private final int WIDTH = 1200;
    private final int HEIGHT = 800;
    private Text lbl1;
    private Text lbl2;
    private Text lbl3;
    private Text lbl4;
    private HBox top;
    private HBox bottom;
    private VBox left;
    private VBox right;
    private Pane board;
    private BorderPane main;
    private Piece p ;
    //create circles that indicate playing turn
    private Circle[] circles ;



    public TheInterface(Stage primary) {
        p = new Piece();
        circles =  new Circle[4];
        //set the layout of the chatting area
        TextArea chatAreaOutput = new TextArea();
        chatAreaOutput.setPrefHeight(HEIGHT * 0.8);
        TextArea chatAreaInput = new TextArea();
        chatAreaInput.setPrefHeight(HEIGHT * 0.16);
        chatAreaInput.setLayoutY(HEIGHT * 0.8);
        ScrollPane scrollPane = new ScrollPane(chatAreaOutput);
        scrollPane.setPrefHeight(HEIGHT * 0.8);
        scrollPane.setLayoutY(0);

        lbl1 = new Text("Player1: ");
        lbl1.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        lbl1.setLayoutX(200);

        lbl2 = new Text("Player2: ");
        lbl2.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        lbl2.setLayoutY(HEIGHT - 200);

        lbl3 = new Text("Player3: ");
        lbl3.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        lbl4 = new Text("Player4: ");
        lbl4.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        lbl4.setLayoutX(WIDTH * 0.7 - 100);




        //create the containers that holds labels and domino pieces
        top = new HBox(5);
        top.setPrefHeight(70);
        top.setFillHeight(false);
        top.setPadding(new Insets(20 ,20, 20, 20));
        bottom = new HBox(5);
        bottom.setPrefHeight(70);
        bottom.setFillHeight(false);
        bottom.setPadding(new Insets(20 ,20, 20, 20));
        left = new VBox(10);
        left.setPrefWidth(70);
        left.setFillWidth(false);
        left.setPadding(new Insets(20 ,20, 20, 20));
        right = new VBox(10);
        right.setPrefWidth(70);
        right.setFillWidth(false);
        right.setPadding(new Insets(20 ,20, 20, 20));

        //create the center of the board

        board = new Pane();
        board.setPrefWidth(WIDTH * 0.7 - 210);
        board.setPrefHeight(HEIGHT - 175);
        board.setStyle("-fx-background-color: #008000; -fx-border-color: red;");
        Pane base = new Pane();
        base.getChildren().add(board);

        //create circles that indicates playing turn
        for(int i = 0; i< circles.length; i++){
            circles[i] = new Circle(0, 0, 5);
            circles[i].setFill(Color.TRANSPARENT);
        }

        //add labels and circles to the containers
        top.getChildren().addAll(lbl1, circles[0]);
        bottom.getChildren().addAll(lbl2, circles[1]);
        left.getChildren().addAll(lbl3, circles[2]);
        right.getChildren().addAll(lbl4, circles[3]);

        main = new BorderPane();
        main.setCenter(base);
        main.setTop(top);
        main.setBottom(bottom);
        main.setLeft(left);
        main.setRight(right);

        main.setPrefSize(WIDTH * 0.7, HEIGHT);
        main.setStyle("-fx-border-color: black;");

        Pane paneSmall = new Pane();
        paneSmall.setPrefSize(WIDTH * 0.3, HEIGHT);
        paneSmall.setLayoutX(WIDTH * 0.7);
        paneSmall.setStyle("-fx-border-color: black");

        Button btn = new Button("Send");
        btn.setLayoutY(HEIGHT * 0.96);
        btn.setLayoutX(WIDTH * 0.25);
        paneSmall.getChildren().addAll(btn, scrollPane, chatAreaInput);

        Pane mainPane = new Pane();
        mainPane.getChildren().addAll(main, paneSmall);

        Scene scene = new Scene(mainPane, WIDTH, HEIGHT);

        primary.setTitle("Domino Client");
        primary.setScene(scene);
    }

    //getters
    public Text getLabel1() {
        return lbl1;
    }

    public Text getLabel2() {
        return lbl2;
    }

    public Text getLabel3() {
        return lbl3;
    }

    public Text getLabel4() {
        return lbl4;
    }

    public HBox getTop() {
        return top;
    }

    public HBox getBottom() {
        return bottom;
    }

    public VBox getLeft() {
        return left;
    }

    public VBox getRight() {
        return right;
    }

    public BorderPane getBorderPane() {
        return main;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Pane getBoard() {
        return board;
    }

    //create a method that paint the circles
    public void paintCircles(int index){
        for(int i = 0; i< circles.length; i++){
            if(i == index){
                circles[i].setFill(Color.GREEN);
            }else{
                circles[i].setFill(Color.RED);
            }
        }
    }
    public double getCenterX(){
        return main.getBoundsInParent().getMaxX() / 2;
    }
    public double getCenterY(){
        return main.getBoundsInParent().getMaxY() / 2;
    }
}

