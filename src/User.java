import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Serializable {

    private String user_name;
    private String password;
    private byte[] profile;

    public byte[] getProfile() {
        return profile;
    }

    public void setProfile(byte[] profile) {
        this.profile = profile;
    }

    Map<String,ArrayList<String>> friendName_to_message ;
    Map<String,ArrayList<Date>> friendName_to_messageTime;
    Map<String,ArrayList<Integer>> friendsName_to_messageType;

    public Map<String, ArrayList<Integer>> getFriendsName_to_messageType() {
        if(friendsName_to_messageType==null)
            return new ConcurrentHashMap<>();
        return friendsName_to_messageType;
    }

    public Map<String, ArrayList<String>> getFriendName_to_message() {
        if(friendName_to_message==null)
            return new ConcurrentHashMap<>();
        return friendName_to_message;
    }

    public Map<String, ArrayList<Date>> getFriendName_to_messageTime() {
        if(friendName_to_messageTime==null)
            return new ConcurrentHashMap<>();
        return friendName_to_messageTime;
    }

    public String getPassword() {
        return password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void receive_message(String message,Date date,String sender ){
        if(friendName_to_message==null) {
            friendName_to_message = new ConcurrentHashMap<String, ArrayList<String>>();
            friendName_to_messageTime = new ConcurrentHashMap<String , ArrayList<Date>>();
            friendsName_to_messageType = new ConcurrentHashMap<>();
        }
        if(friendName_to_message.containsKey(sender)){
            friendName_to_message.get(sender).add(message);
            friendName_to_messageTime.get(sender).add(date);
            friendsName_to_messageType.get(sender).add(0);
        }else{
            ArrayList<String> new_message_list = new ArrayList<String>();
            new_message_list.add(message);
            friendName_to_message.put(sender,new_message_list);
            ArrayList<Integer> new_type_list = new ArrayList<>();
            new_type_list.add(0);
            friendsName_to_messageType.put(sender,new_type_list);
            ArrayList<Date> new_date_list = new ArrayList<Date>();
            new_date_list.add(date);
            friendName_to_messageTime.put(sender,new_date_list);
        }
    }
}
