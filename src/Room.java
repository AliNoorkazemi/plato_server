import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room implements Serializable {

   private Map<String,ArrayList<String>> roomName_to_joined_user;
   private Map<String,Integer> roomName_to_maxPlayer;

    public Map<String, ArrayList<String>> getRoomName_to_joined_user() {
        if(roomName_to_joined_user==null)
            roomName_to_joined_user=new ConcurrentHashMap<>();
        return roomName_to_joined_user;
    }

    public void setRoomName_to_joined_user(Map<String, ArrayList<String>> roomName_to_joined_user) {
        if(roomName_to_joined_user==null)
            roomName_to_joined_user=new ConcurrentHashMap<>();
        this.roomName_to_joined_user = roomName_to_joined_user;
    }

    public Map<String, Integer> getRoomName_to_maxPlayer() {
        if(roomName_to_maxPlayer==null)
            roomName_to_maxPlayer=new ConcurrentHashMap<>();
        return roomName_to_maxPlayer;
    }

    public void setRoomName_to_maxPlayer(Map<String, Integer> roomName_to_maxPlayer) {
        if(roomName_to_maxPlayer==null)
            roomName_to_maxPlayer=new ConcurrentHashMap<>();
        this.roomName_to_maxPlayer = roomName_to_maxPlayer;
    }
}
