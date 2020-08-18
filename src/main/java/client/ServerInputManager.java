package client;

    import design.*;
    import java.io.DataInputStream;
    import java.io.IOException;

    import javafx.application.Platform;
    import javafx.geometry.Bounds;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.Pane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Text;

public class ServerInputManager extends Thread implements Constants{

    private DataInputStream fromServer;
    private TheInterface mainStage;
    private Piece[] pieces = new Piece[28];
    private ServerOutputManager serverOutputManager;

    private boolean isDelivered = false;//indicate whether the player is ready to play
    private boolean myTurn; //indicate player's turn

    private PiecesList piecesList;
    //create directions
    private String headDirection = LEFT;
    private String tailDirection = RIGHT;

    private int headCounter = 0;
    private int tailCounter = 0;

    private int player;

    //constructor
    public ServerInputManager(DataInputStream fromServer, TheInterface mainStage, ServerOutputManager serverOutputManager) {
        this.fromServer = fromServer;
        this.mainStage = mainStage;
        piecesList = new PiecesList();
        this.serverOutputManager = serverOutputManager;

    }

    @Override
    public void run() {
        //identify the position of the player
        try {
            while (true) {
                String str = fromServer.readUTF();
                if (str.equalsIgnoreCase("session started")) {
                    player = fromServer.readInt();
                    distributePieces(player);
                    break;
                } else {
                    setPlayerPosition(str);
                }
            }
            while(true) {
                assignTurns();
                //Thread.sleep(1000);
                if (!(myTurn)) {
                    moveDominoPiece(player);
                }
            }



        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public void moveDominoPiece(int player) throws IOException {
        String txt;
            do {
                double x = fromServer.readDouble();//indicate x coordinate
                double y = fromServer.readDouble();//indicate y coordinate
                double rotation = fromServer.readDouble();
                int l = fromServer.readInt();// indicate left surface of the piece
                int r = fromServer.readInt();// indicate right surface of the piece
                int p = fromServer.readInt(); //indicate position of the player
                int side = fromServer.readInt();//indicates the side where to add the piece in the list
                boolean flip = fromServer.readBoolean();
                txt = fromServer.readUTF();
                int index = indexStarter(p);
                int length = index + 7;

                for (int i = index; i < length; i++) {
                    if (pieces[i].getRight() == r && pieces[i].getLeft() == l ||
                            pieces[i].getRight() == l && pieces[i].getLeft() == r) {
                        if (pieces[i].getRight() == l && pieces[i].getLeft() == r) {
                            pieces[i].swapSides();
                        }
                        Platform.runLater(new DrawPiece(x, y, rotation, i, side, flip));
                    }
                }
            } while (txt.equalsIgnoreCase(DRAG));
    }

    public void assignTurns() throws IOException {
        myTurn = fromServer.readBoolean();
        int pos = fromServer.readInt();
        mainStage.paintCircles(pos);

    }

    private class DrawPiece implements Runnable {

        private double x;
        private double y;
        private double rotation;
        private int i;
        private int side;
        private boolean flip;

        DrawPiece(double x, double y, double rotation, int i, int side, boolean flip) {
            this.x = x;
            this.y = y;
            this.i = i;
            this.rotation = rotation;
            this.side = side;
            this.flip = flip;
        }

        @Override
        public void run() {

            pieces[i].setTranslateX(x);
            pieces[i].setTranslateY(y);
            pieces[i].setRotate(rotation);
            pieces[i].flipPiece(flip);
            if (piecesList.isEmpty()) {
                addPieceToList(side);
            } else {
                if (piecesList.length() == 1) {
                    addPieceToList(side);
                    piecesList.getTail().setDirection(RIGHT);
                    piecesList.getHead().setDirection(LEFT);
                } else {
                    tailDirection = piecesList.getTail().getDirection();
                    headDirection = piecesList.getHead().getDirection();
                    addPieceToList(side);
                    if(side == HEAD){
                        if(isFarFromBorder(piecesList.getHead(), mainStage.getBoard(), headDirection)){
                            piecesList.getHead().setDirection(headDirection);
                        }else{
                            switch (headDirection){
                                case LEFT:
                                case RIGHT:
                                    piecesList.getHead().setDirection(TOP);
                                    break;
                                case TOP:
                                    if(headCounter < 2) {
                                        piecesList.getHead().setDirection(TOP);
                                        headCounter++;
                                    }else{
                                        piecesList.getHead().setDirection(RIGHT);
                                        headCounter = 0;
                                    }
                                    break;
                            }
                        }
                    }else if(side == TAIL){
                        if(isFarFromBorder(piecesList.getTail(), mainStage.getBoard(), tailDirection)){
                            piecesList.getTail().setDirection(tailDirection);
                        }else{
                            switch (tailDirection){
                                case LEFT:
                                case RIGHT:
                                    piecesList.getTail().setDirection(BOTTOM);
                                    break;
                                case BOTTOM:
                                    if(tailCounter < 2){
                                        piecesList.getTail().setDirection(BOTTOM);
                                        tailCounter++;
                                    }else{
                                        piecesList.getTail().setDirection(LEFT);
                                        tailCounter = 0;
                                    }
                                    break;
                            }
                        }
                    }
                    System.out.println(piecesList.getHead().getDirection() + " * * * "+ piecesList.getTail().getDirection());
                }
            }

        }
        public void addPieceToList(int side){
            if (side == HEAD) {
                piecesList.addToBegin(pieces[i]);
            } else if (side == TAIL) {
                piecesList.addToEnd(pieces[i]);
            }
        }
    }

    public void distributePieces(int player) throws IOException{
        for(int i = 0; i < pieces.length; i++) {
            int left = fromServer.readInt();
            int right = fromServer.readInt();
            Piece p = new Piece(left, right);
            p.setOnMouseDragged(e -> dragPiece(p, e, player));
            p.setOnMouseReleased(e -> {
                try {
                    releasePiece(p, player);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            pieces[i] = p;
            if(i < 7){
                if(player != PLAYER_1){
                    p.flipPiece(false);
                }
                Platform.runLater(() ->{
                    mainStage.getTop().getChildren().add(p);
                    p.setRotate(90);
                });
            }else if(i < 14){
                if(player != PLAYER_2){
                    p.flipPiece(false);
                }
                Platform.runLater(() ->{
                    mainStage.getBottom().getChildren().add(p);
                    p.setRotate(90);
                });
            }else if(i < 21){
                if(player != PLAYER_3){
                    p.flipPiece(false);
                }
                Platform.runLater(() -> mainStage.getLeft().getChildren().add(p));
            }else{
                if(player != PLAYER_4){
                    p.flipPiece(false);
                }
                Platform.runLater(() -> mainStage.getRight().getChildren().add(p));
            }
        }

    }
    public int indexStarter(int player){
        switch (player){
            case 1:
                return 0;
            case 2:
                return 7;
            case 3:
                return 14;
            case 4:
                return 21;
        }
        return 0;
    }
    public void dragPiece(Piece p, MouseEvent e, int player) {
        if(myTurn) {
            if(canPlay(player)) {
                double x = 0, y = 0;
                switch (player) {
                    case 1:
                        x = e.getSceneX() - p.getLayoutX() - p.getHeight();
                        y = e.getSceneY() - p.getLayoutY();
                        p.setTranslateX(x);
                        p.setTranslateY(y);
                        break;
                    case 2:
                        x = e.getSceneX() - p.getLayoutX() - p.getWidth() / 2;
                        y = e.getSceneY() - mainStage.getBottom().getLayoutY() - p.getHeight();
                        p.setTranslateX(x);
                        p.setTranslateY(y);
                        break;
                    case 3:
                    case 4:
                        x = e.getX() + p.getTranslateX() - p.getWidth() / 2;
                        y = e.getY() + p.getTranslateY() - p.getHeight() / 2;
                        p.setTranslateX(x);
                        p.setTranslateY(y);
                        break;
                }
                serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);
                if (!(piecesList.isEmpty())) {
                    if (piecesList.length() == 1) {
                        if (isCollidingFromLeft(p, piecesList.getHead())) {
                            setOnLeft(p, piecesList.getHead());
                            if (!(p.areSidesEqual())) {
                                p.setRotate(0);
                            }
                            serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);
                        } else if (isCollidingFromRight(p, piecesList.getHead())) {
                            setOnRight(p, piecesList.getHead());
                            if (!(p.areSidesEqual())) {
                                p.setRotate(0);
                            }
                            serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);

                        } else {
                            clear(p);
                        }
                    } else {
                        if (checkCollision(p, piecesList.getHead())) {
                            switch (piecesList.getHead().getDirection()) {
                                case LEFT:
                                    if (isCollidingFromLeft(p, piecesList.getHead())) {
                                        setOnLeft(p, piecesList.getHead());
                                        if (!(p.areSidesEqual())) {
                                            p.setRotate(0);
                                        }
                                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);
                                    }
                                    break;
                                case TOP:
                                    if (isCollidingFromTop(p, piecesList.getHead())) {
                                        setOnTop(p, piecesList.getHead());
                                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);
                                    }
                                    break;
                                case RIGHT:
                                    if (isCollidingFromRight(p, piecesList.getHead())) {
                                        setOnLeft(p, piecesList.getHead());
                                        if (!(p.areSidesEqual())) {
                                            p.setRotate(180);
                                        }
                                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);

                                    }
                                    break;
                            }
                        } else if (checkCollision(p, piecesList.getTail())){
                            switch (piecesList.getTail().getDirection()) {
                                case RIGHT:
                                    if (isCollidingFromRight(p, piecesList.getTail())) {
                                        setOnRight(p, piecesList.getTail());
                                        if (!(p.areSidesEqual())) {
                                            p.setRotate(0);
                                        }
                                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);
                                    }
                                    break;
                                case BOTTOM:
                                    if (isCollidingFromBottom(p, piecesList.getTail())) {
                                        setOnBottom(p, piecesList.getTail());
                                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);

                                    }
                                    break;
                                case LEFT:
                                    if (isCollidingFromLeft(p, piecesList.getTail())) {
                                        setOnRight(p, piecesList.getTail());
                                        if (!(p.areSidesEqual())) {
                                            p.setRotate(180);
                                        }
                                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, DRAG);
                                    }
                                    break;
                            }
                        } else {
                            clear(p);
                        }

                    }
                }
            }else{
                serverOutputManager.sendCoordinates(0, 0, p.getRotate(), p.getLeft(), p.getRight(), player, 0, false, RELEASE);
            }
        }
    }
    public boolean canPlay(int player){
        if(piecesList.isEmpty()){
            return true;
        }else if(piecesList.length() == 1){
            int index = indexStarter(player);
            System.out.println(index + " *** " + player);
            while(index < index + 7){
                if(piecesList.getHead().areSidesEqual()){
                    if(pieces[index].getLeft() == piecesList.getHead().getLeft() ||
                        pieces[index].getRight() == piecesList.getHead().getLeft()){
                        return true;
                    }
                }else{
                    if(pieces[index].areSidesEqual()){
                        if(pieces[index].getLeft() == piecesList.getHead().getLeft() ||
                            pieces[index].getLeft() == piecesList.getHead().getRight()){
                            return true;
                        }
                    }else{
                        if(pieces[index].getLeft() == piecesList.getHead().getLeft() ||
                            pieces[index].getLeft() == piecesList.getHead().getRight() ||
                            pieces[index].getRight() == piecesList.getHead().getLeft() ||
                            pieces[index].getRight() == piecesList.getHead().getRight()){
                            return true;
                        }
                    }
                }
                index ++;
            }
        }else{
            int index = indexStarter(player);
            while(index < index + 7){
                if(pieces[index].areSidesEqual() && piecesList.getHead().getLeft() == piecesList.getTail().getRight()) {
                    if(pieces[index].getLeft() == piecesList.getTail().getRight()){
                        return true;
                    }
                }else if(pieces[index].areSidesEqual() && piecesList.getHead().getLeft() != piecesList.getTail().getRight()) {
                    if (pieces[index].getLeft() == piecesList.getHead().getLeft() ||
                            pieces[index].getLeft() == piecesList.getTail().getRight()) {
                        return true;
                    }
                }else if(!(pieces[index].areSidesEqual()) && piecesList.getHead().getLeft() == piecesList.getTail().getRight()){
                    if (pieces[index].getLeft() == piecesList.getHead().getLeft() ||
                            pieces[index].getRight() == piecesList.getHead().getLeft()){
                        return true;
                    }

                }else if(!(pieces[index].areSidesEqual()) && piecesList.getHead().getLeft() != piecesList.getTail().getRight()){
                    if(pieces[index].getLeft() == piecesList.getHead().getLeft() ||
                        pieces[index].getLeft() == piecesList.getTail().getRight() ||
                        pieces[index].getRight() == piecesList.getHead().getLeft() ||
                        pieces[index].getRight() == piecesList.getTail().getRight()){
                        return true;
                    }
                }
                index++;
            }
        }
        return false;
    }
    public boolean checkCollision(Piece piece, Piece listNode){

        Bounds obj1 = piece.localToScene(piece.getBoundsInLocal());
        Bounds obj2 = listNode.localToScene(listNode.getBoundsInLocal());
        return obj1.intersects(obj2);
    }

    public String changeDirection(String direction, int node, int border) {
        //node = -1 indicate the head
        //node = 1 indicate the tail
        //border = -1 indicates left border
        //border = 1 indicates right border
        switch (direction) {
            case LEFT:
            case RIGHT:
                if(node == -1){
                    return TOP;
                }else{
                    return BOTTOM;
                }
            case TOP:
            case BOTTOM:
                if(border == -1){
                    return RIGHT;
                }else{
                    return LEFT;
                }
        }
        return "";
    }


    public boolean isFarFromBorder(Piece p, Pane board, String direction) {
        Bounds obj1 = p.localToScene(p.getBoundsInLocal());
        Bounds obj2 = board.localToScene(board.getBoundsInLocal());
        switch (direction) {
            case LEFT:
                return obj1.getMinX() > (obj2.getMinX() + p.getWidth()*2);
            case RIGHT:
                return obj1.getMaxX() < (obj2.getMaxX() - p.getWidth()*2);
            case TOP:
                return obj1.getMinY() > (obj2.getMinY() + p.getWidth()*2);
            case BOTTOM:
                return obj1.getMaxY() < (obj2.getMaxY() + p.getWidth()*2);
        }
        return true;
    }

    /**
     * The next four methods are responsible for identifying the collision of domino piece with
     * edges of the list of domino pieces.
     */
    public boolean isCollidingFromRight(Piece p1, Piece p2) {
        Bounds obj1 = p1.localToScene(p1.getBoundsInLocal());
        Bounds obj2 = p2.localToScene(p2.getBoundsInLocal());
        if (obj1.intersects(obj2)) {
            return obj1.getMinX() == obj2.getMaxX();
        }
        return false;
    }

    public boolean isCollidingFromLeft(Piece p1, Piece p2) {
        Bounds obj1 = p1.localToScene(p1.getBoundsInLocal());
        Bounds obj2 = p2.localToScene(p2.getBoundsInLocal());
        if (obj1.intersects(obj2)) {
            return obj1.getMaxX() == obj2.getMinX();
        }
        return false;
    }

    public boolean isCollidingFromTop(Piece p1, Piece p2) {
        Bounds obj1 = p1.localToScene(p1.getBoundsInLocal());
        Bounds obj2 = p2.localToScene(p2.getBoundsInLocal());
        if (obj1.intersects(obj2)) {
            return obj1.getMaxY() == obj2.getMinY();
        }
        return false;
    }

    public boolean isCollidingFromBottom(Piece p1, Piece p2) {
        Bounds obj1 = p1.localToScene(p1.getBoundsInLocal());
        Bounds obj2 = p2.localToScene(p2.getBoundsInLocal());
        if (obj1.intersects(obj2)) {
            return obj1.getMinY() == obj2.getMaxY();
        }
        return false;
    }

    /**
     *The next following four methods are responsible for placing dragged domino piece
     * according to the edge of the list
     */
    public void setOnLeft(Piece p, Piece listNode) {
        if (p.getLeft() == listNode.getLeft()) {
            p.swapSides();
            p.paintRight();
        } else if (p.getRight() == listNode.getLeft()) {
            p.paintRight();
        }
    }

    public void setOnRight(Piece p, Piece listNode) {
        if (p.getLeft() == listNode.getRight()) {
            p.paintLeft();
        } else if (p.getRight() == listNode.getRight()) {
            p.swapSides();
            p.paintLeft();
        }
    }

    public void setOnTop(Piece p, Piece listNode) {
        if (p.getLeft() == listNode.getLeft()) {
            p.swapSides();
            p.paintRight();
        } else if (p.getRight() == listNode.getLeft()) {
            p.paintRight();
        }
    }



    public void setOnBottom(Piece p, Piece listNode) {
        if (p.getLeft() == listNode.getRight()) {
            p.paintLeft();
        } else if (p.getRight() == listNode.getRight()) {
            p.swapSides();
            p.paintLeft();
        }

    }

    public void releasePiece(Piece p, int player) throws IOException{
        if (p.isLeftPainted() || p.isRightPainted() || piecesList.isEmpty()) {
            double x = p.getTranslateX();
            double y = p.getTranslateY();
            int left = p.getLeft();
            int right = p.getRight();
            if (piecesList.isEmpty()) {
                if (!(p.areSidesEqual())) {
                    p.setRotate(0);
                }
                serverOutputManager.sendCoordinates(x, y, p.getRotate(), left, right, player, HEAD, true, RELEASE);
                piecesList.addToBegin(p);
                piecesList.getHead().setDirection(LEFT);
            } else {

                if (piecesList.length() == 1) {
                    if(p.isLeftPainted()){
                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), left, right, player, TAIL, true, RELEASE);
                        piecesList.addToEnd(p);
                        piecesList.getTail().setDirection(RIGHT);
                        clear(p);
                    }else if(p.isRightPainted()){
                        serverOutputManager.sendCoordinates(x, y, p.getRotate(), left, right, player, HEAD, true, RELEASE);
                        piecesList.getHead().setDirection(RIGHT);
                        piecesList.addToBegin(p);
                        piecesList.getHead().setDirection(LEFT);
                        clear(p);
                    }
                } else {
                    addPieceToList(p, player);
                }
            }
        } else {
            p.setTranslateX(0);
            p.setTranslateY(0);
            int left = p.getLeft();
            int right = p.getRight();
            switch (player){
                case 1:
                case 2:
                    p.setRotate(90);
                    serverOutputManager.sendCoordinates(p.getTranslateX(), p.getTranslateY(), p.getRotate(), left, right, player, 0,  false, DRAG);
                    break;

                case 3:
                case 4:
                    p.setRotate(0);
                    serverOutputManager.sendCoordinates(p.getTranslateX(), p.getTranslateY(), p.getRotate(), left, right, player, 0, false, DRAG);

                    break;

            }
        }
    }

    private void addPieceToList(Piece p, int player)throws IOException{
        if(p.isLeftPainted()){
            String direction = piecesList.getTail().getDirection();
            serverOutputManager.sendCoordinates(p.getTranslateX(), p.getTranslateY(), p.getRotate(), p.getLeft(), p.getRight(), player, TAIL, true, RELEASE);
            piecesList.addToEnd(p);
            piecesList.getTail().setDirection(direction);// update the direction of the tail
            if(piecesList.getTail().getDirection().equalsIgnoreCase(BOTTOM)){
                tailCounter++;
            }
            clear(p);
            boolean boo = isFarFromBorder(piecesList.getTail(), mainStage.getBoard(), direction);
            if(boo){
                if(piecesList.getTail().getDirection().equalsIgnoreCase(BOTTOM) && tailCounter == 1){
                    piecesList.getTail().setDirection(changeDirection(piecesList.getTail().getDirection(), TAIL, assignBorder(boo, tailDirection)));
                    tailCounter = 0;
                }
            }else{
                tailDirection = piecesList.getTail().getDirection();
                piecesList.getTail().setDirection(changeDirection(piecesList.getTail().getDirection(), TAIL, 0));

            }
        }else if(p.isRightPainted()){
            String direction = piecesList.getHead().getDirection();
            serverOutputManager.sendCoordinates(p.getTranslateX(), p.getTranslateY(), p.getRotate(), p.getLeft(), p.getRight(), player, HEAD, true, RELEASE);
            piecesList.addToBegin(p);
            piecesList.getHead().setDirection(direction);// update the direction of the head
            if(piecesList.getHead().getDirection().equalsIgnoreCase(TOP)){
                headCounter++;
            }
            clear(p);
            boolean boo = isFarFromBorder(piecesList.getHead(), mainStage.getBoard(), direction);
            if(boo){

                if(piecesList.getHead().getDirection().equalsIgnoreCase(TOP) && headCounter == 1){
                    piecesList.getHead().setDirection(changeDirection(piecesList.getHead().getDirection(), HEAD, assignBorder(boo, headDirection)));
                    headCounter = 0;
                }
            }else{
                headDirection = piecesList.getHead().getDirection();
                piecesList.getHead().setDirection(changeDirection(piecesList.getHead().getDirection(), HEAD, 0));

            }
        }

    }
    public int assignBorder(boolean bool, String direction){
        if(bool && direction.equalsIgnoreCase(LEFT)){
            return -1;
        }else if(bool && direction.equalsIgnoreCase(RIGHT)){
            return 1;
        }
        return 0;
    }
    public void clear (Piece piece){
        piece.getRightSide().setFill(Color.TRANSPARENT);
        piece.getLeftSide().setFill(Color.TRANSPARENT);
    }

    public void setPlayerPosition(String position){

        if (position.contains("0")) {
            setPlayerName(mainStage.getLabel1(), position);
        } else if (position.contains("1")) {
            setPlayerName(mainStage.getLabel2(), position);
        } else if (position.contains("2")) {
            setPlayerName(mainStage.getLabel3(), position);
        } else if (position.contains("3")) {
            setPlayerName(mainStage.getLabel4(), position);
        }

    }

    public void setPlayerName(Text label, String position){
        position = position.substring(1);
        String player = label.getText();
        player = player + "\n" + position;
        String finalPlayer = player;
        Platform.runLater(() -> {
            label.setText(finalPlayer);
        });
    }
}

