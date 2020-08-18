package design;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;

public class Piece extends Pane {

    private final double WIDTH = 60;
    private final double HEIGHT= 30;
    private int left;
    private int right;
    private Rectangle rightSide;
    private Rectangle leftSide;
    private Rectangle rect;
    private Line line;
    public Piece next;
    private String direction;

    //constructors
    public Piece(){
        setPrefSize(WIDTH, HEIGHT);
    }
    public Piece(int left, int right) {
        this.left = left;
        this.right = right;
        direction = "left";
        rect = new Rectangle(0, 0, WIDTH, HEIGHT);
        leftSide = new Rectangle(0, 0, HEIGHT, HEIGHT);
        leftSide.setFill(Color.TRANSPARENT);
        rightSide = new Rectangle(HEIGHT, 0, HEIGHT, HEIGHT);
        rightSide.setFill(Color.TRANSPARENT);
        setPrefSize(WIDTH, HEIGHT);
        rect.setStroke(Color.BLACK);
        rect.setFill(Color.WHITE);
        double x = rect.getX() + (HEIGHT);
        double startY = rect.getY();
        double endY = startY + (WIDTH / 2);
        line = new Line(x, startY, x, endY);
        getChildren().addAll(rect, line, leftSide, rightSide);
        addSpots(left, right, rect);
        setStyle("-fx-border-color: #ff0000;");

    }

    public void addSpots(int left, int right, Rectangle rect) {
        double radius = 2;
        //define the y positions of the spots
        final double Y_TOP = rect.getY() + HEIGHT / 5;
        double margin = Y_TOP - rect.getY();
        final double Y_CENTER = rect.getY() + HEIGHT / 2;
        final double Y_BOTTOM = (rect.getY() + HEIGHT) - margin;
        // define the x positions of the spots
        double x1 = rect.getX() + margin;
        double x3 = rect.getX() + HEIGHT - margin;
        double x2 = x1 + (x3 - x1) / 2;
        double x4 = rect.getX() + HEIGHT + margin;
        double x6 = rect.getX() + WIDTH - margin;
        double x5 = x4 + (x6 - x4) / 2;

        //draw the spots according to the given data
        drawSpots(left, radius, Y_TOP, Y_CENTER, Y_BOTTOM, x1, x3, x2);
        drawSpots(right, radius, Y_TOP, Y_CENTER, Y_BOTTOM, x4, x6, x5);

    }

    private void drawSpots(int side, double radius, double y_TOP, double y_CENTER, double y_BOTTOM, double x1, double x3, double x2) {
        switch (side) {

            case 1:
                drawOne(x2, y_CENTER, radius);
                break;
            case 2:
                drawTwo(x1, y_TOP, x3, y_BOTTOM, radius);
                break;
            case 3:
                drawOne(x2, y_CENTER, radius);
                drawTwo(x1, y_TOP, x3, y_BOTTOM, radius);
                break;
            case 4:
                drawTwo(x1, y_TOP, x3, y_BOTTOM, radius);
                drawTwo(x3, y_TOP, x1, y_BOTTOM, radius);
                break;
            case 5:
                drawTwo(x1, y_TOP, x3, y_BOTTOM, radius);
                drawTwo(x3, y_TOP, x1, y_BOTTOM, radius);
                drawOne(x2, y_CENTER, radius);
                break;
            case 6:
                drawTwo(x1, y_TOP, x3, y_BOTTOM, radius);
                drawTwo(x3, y_TOP, x1, y_BOTTOM, radius);
                drawTwo(x2, y_TOP, x2, y_BOTTOM, radius);
                break;

        }
    }

    public void drawOne(double x, double y, double radius) {
        Circle circle = new Circle(x, y, radius);
        getChildren().add(circle);
    }

    public void drawTwo(double x1, double y1, double x2, double y2, double radius) {
        Circle circle1 = new Circle(x1, y1, radius);
        Circle circle2 = new Circle(x2, y2, radius);
        getChildren().addAll(circle1, circle2);
    }

    public void flipPiece(boolean flip) {

        Node[] array = new Node[20];

        getChildren().toArray(array);
        int i = 0;
        while (array[i] != null) {
            // hide the spots and the line. C refers to circle and L to line
            if (array[i].toString().charAt(0) == 'C' || array[i].toString().charAt(0) == 'L') {
                if(flip) {
                    array[i].setVisible(true);
                }else{
                    array[i].setVisible(false);

                }
            }
            i++;
        }

    }
    public void paintLeft(){
        leftSide.setFill(Color.rgb(253, 40, 3, 0.3));
    }
    public boolean isLeftPainted(){
        return leftSide.getFill().equals(Color.rgb(253,40,3,0.3));
    }
    public void paintRight(){
        rightSide.setFill(Color.rgb(253, 40, 3, 0.3));
    }
    public boolean isRightPainted(){
        return rightSide.getFill().equals(Color.rgb(253,40,3,0.3));
    }
    public void removeColor(){
        leftSide.setFill(Color.TRANSPARENT);
        rightSide.setFill(Color.TRANSPARENT);
    }
    public boolean areSidesEqual(){
        return left == right;
    }
    public void swapSides(){
        int hold = left;
        left = right;
        right = hold;
        Platform.runLater(() -> removeSpots());
        Platform.runLater(() ->addSpots(left, right, rect));

    }
    private void removeSpots(){
        getChildren().remove(4, getChildren().size());
    }
    //getters
    public int getLeft(){
        return left;
    }
    public int getRight(){
        return right;
    }
    public Rectangle getRightSide(){
        return rightSide;
    }
    public Rectangle getLeftSide(){
        return leftSide;
    }

    public String getDirection() {
        return direction;
    }

    //setters
    public void setLeft(int left){
        this.left = left;
    }
    public void setRight(int right){
        this.right = right;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}


