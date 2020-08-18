package server;

import design.Constants;
import design.Piece;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/* create a server agent that will serve a particular player*/
class ServerAgent extends Thread  implements Constants {

    private DataOutputStream toPlayer;
    private DataInputStream fromPlayer;
    private Socket socket;
    private Server server;
    private String position;
    private Piece[] pieces = new Piece[28];
    private boolean isReady;
    private boolean myTurn;
    private boolean isDone;

    //constructor
    public ServerAgent(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        myTurn = false;
        isDone = false;
    }

    @Override //override the run method of the Thread class
    public void run() {
        //instantiate data streams
        try {
            toPlayer = new DataOutputStream(socket.getOutputStream());
            fromPlayer = new DataInputStream(socket.getInputStream());

            //read requests from the user
            String request = fromPlayer.readUTF();
            int pos = server.getAgent(this);
            position = Integer.toString(pos) + request;

            //notify other players that iam online
            server.broadCast(this, request);
            server.notifyMe(this);
            isReady = true;

            // receive domino pieces
            pos = server.getAgent(this) + 1;
            server.shareDominoPieces(this);
            startSession(pos);
            try {
                Thread.sleep(1000);
            }catch(InterruptedException ex){
                System.out.println(ex.toString());
            }
            //which player is going to start playing
            server.recieveTurn(this);
            if(myTurn) {
                server.playAgent();
            }

        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public void receiveCoordinates() throws IOException {
        String txt;
        do{
            double x = fromPlayer.readDouble();
            double y = fromPlayer.readDouble();
            double rotation = fromPlayer.readDouble();
            int l = fromPlayer.readInt();
            int r = fromPlayer.readInt();
            int p = fromPlayer.readInt(); // position of the player
            int side = fromPlayer.readInt();
            boolean flip = fromPlayer.readBoolean();
            txt = fromPlayer.readUTF();
            //broadcast these coordinates with other players
            server.broadCastCoordinates(this, x, y, rotation, l, r, p, side,  flip, txt);
        }while(txt.equalsIgnoreCase(DRAG));
        server.notifyPlayers(this);
    }

    public void assignTurn(int pos) throws IOException {
        toPlayer.writeBoolean(myTurn);
        toPlayer.writeInt(pos);
    }

    public void startSession(int position) throws IOException {
        //wait until players join the session
        while (server.getSession() == false) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex.toString());
            }
        }
        String str = "session started";
        toPlayer.writeUTF(str);
        //send pieces to the client
        sendPieces(position);

    }

    public void sendPieces(int position) throws IOException {
        //send the pieces to the player
        toPlayer.writeInt(position);
        for(Piece p : pieces){
            int left = p.getLeft();
            int right = p.getRight();
            try{
                toPlayer.writeInt(left);
                toPlayer.writeInt(right);
            }catch(IOException ex){
                System.out.println(ex.toString());
            }
        }
    }

    public void sendMessage(String message) {
        try {
            toPlayer.writeUTF(message);
            toPlayer.flush();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
    public void sendPosition(int position) {
        try{
            toPlayer.writeInt(position);
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
    public void sendCoordinates(double x, double y, double rotation, int l, int r, int p, int side, boolean flip, String txt){
        try{
            toPlayer.writeDouble(x);
            toPlayer.writeDouble(y);
            toPlayer.writeDouble(rotation);
            toPlayer.writeInt(l);
            toPlayer.writeInt(r);
            toPlayer.writeInt(p);
            toPlayer.writeInt(side);
            toPlayer.writeBoolean(flip);
            toPlayer.writeUTF(txt);
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
    public void sendFlip(boolean flip) throws IOException{
        toPlayer.writeUTF(RELEASE);
        toPlayer.writeBoolean(flip);
    }

    public String getPosition() {
        return position;
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public boolean getAgentStatus() {
        return isReady;
    }
    public boolean getMyTurn(){
        return myTurn;
    }
    public void setMyTurn(boolean myTurn){
        this.myTurn = myTurn;
    }
}

