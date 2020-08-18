package client;

import java.io.DataOutputStream;
import java.io.IOException;

public class ServerOutputManager {
    private DataOutputStream toServer;

    public ServerOutputManager(DataOutputStream toServer) {
        this.toServer = toServer;
    }
    public void sendMessage(String s){
        try{
            toServer.writeUTF(s);
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
    public void sendCoordinates(double x, double y, double rotation, int left, int right, int position, int side, boolean flip, String txt){
        try{
            toServer.writeDouble(x);
            toServer.writeDouble(y);
            toServer.writeDouble(rotation);
            toServer.writeInt(left);
            toServer.writeInt(right);
            toServer.writeInt(position);
            toServer.writeInt(side);
            toServer.writeBoolean(flip);
            toServer.writeUTF(txt);
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
}
