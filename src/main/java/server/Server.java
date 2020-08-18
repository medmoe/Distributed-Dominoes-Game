package server;

import design.Constants;
import design.Piece;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* create a class Server that contains methods that manage comunications among
users.
 */
public class Server extends Thread  implements Constants {

    private Socket socket;
    private ArrayList<ServerAgent> agents;
    private Piece[] pieces;
    private int piecesCounter;
    private int portNumber;
    private boolean isSessionStarted = false;
    private boolean isRoundFinished = false; //indicates the end of the round
    private int clock = 0;

    //constructor
    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(portNumber);
            agents = new ArrayList();
            //create domino pieces
            pieces = new Piece[28];
            createDominoPieces(pieces);
            //shuffle the pieces randomly
            List<Piece> list = Arrays.asList(pieces);
            Collections.shuffle(list);
            list.toArray(pieces);
            int players = 0;
            while (players < 4) {
                socket = server.accept();
                ServerAgent agent = new ServerAgent(socket, this);
                agents.add(agent);
                agent.start();
                players++;
            }
            while (isSessionStarted == false) {
                boolean check = true; //indicate whether the player is ready to play
                for (ServerAgent agent : agents) {
                    check = check && agent.getAgentStatus();
                }
                if (check) {
                    isSessionStarted = true;
                } else {
                    isSessionStarted = false;
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
    public void recieveTurn(ServerAgent a) throws IOException{
        int index = agents.indexOf(a);
        if(index == 0){
            a.setMyTurn(true);
            broadCastPlayerTurn(index);
        }
    }
    public void playAgent() throws IOException {
        for(ServerAgent a: agents){
            System.out.println(agents.indexOf(a) + " *** "+ a.getMyTurn());
            if(a.getMyTurn()){
                a.receiveCoordinates();
            }
        }
    }
    public void notifyPlayers(ServerAgent agent) throws IOException{
        agents.get(agents.indexOf(agent)).setMyTurn(false);
        switch(agents.indexOf(agent)){
            case 0:
                agents.get(3).setMyTurn(true);
                broadCastPlayerTurn(3);
                playAgent();
                break;
            case 3:
                agents.get(1).setMyTurn(true);
                broadCastPlayerTurn(1);
                playAgent();
                break;
            case 1:
                agents.get(2).setMyTurn(true);
                broadCastPlayerTurn(2);
                playAgent();
                break;
            case 2:
                agents.get(0).setMyTurn(true);
                broadCastPlayerTurn(0);
                playAgent();
                break;
        }

    }

    public void createDominoPieces(Piece[] pieces) {
        int j;
        int k = 0;
        for (int i = 0; i < 7; i++) {
            j = i;
            while (j < 7) {
                Piece p = new Piece(i, j);
                pieces[k] = p;
                j++;
                k++;
            }
        }
    }

    //get the current position of the current agent
    public int getAgent(ServerAgent agent) {
        return agents.indexOf(agent);
    }

    public void broadCast(ServerAgent agent, String message) {
        for (ServerAgent a : agents) {
            a.sendMessage(message);

        }
    }

    public void broadCastCoordinates(ServerAgent agent, double x, double y, double rotation, int l, int r, int p, int side, boolean flip, String txt) {
        for (ServerAgent a : agents) {
            if (!(a.equals(agent))) {
                a.sendCoordinates(x, y, rotation, l, r, p, side,flip, txt);
            }
        }
    }
    public void broadCastPlayerTurn(int position) throws IOException{
        for(ServerAgent agent: agents){
            agent.assignTurn(position);
        }
    }
    public void notifyMe(ServerAgent agent) {
        for (ServerAgent a : agents) {
            if (!(a.equals(agent))) {

                agent.sendMessage(a.getPosition());

            }
        }
    }
    public void shareDominoPieces(ServerAgent agent) {
        for (ServerAgent a : agents) {
            if (a.equals(agent)) {
                for (int i = 0; i < pieces.length; i++) {
                    agent.getPieces()[i] = pieces[i];
                }
            }
        }
    }

    //getters
    public boolean getSession() {
        return isSessionStarted;
    }

}

