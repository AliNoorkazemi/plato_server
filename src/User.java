import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Serializable {

    private String user_name;
    private String password;
    private Map<String,ArrayList<String>> friendName_to_message ;
    private Map<String,ArrayList<Date>> friendName_to_messageTime;
    private Map<String,ArrayList<Boolean>> friendsName_to_messageBoolean;

    public Map<String, ArrayList<String>> getFriendName_to_message() {
        if(friendName_to_message==null)
            return new ConcurrentHashMap<>();
        return friendName_to_message;
    }

    public void setFriendName_to_message(Map<String, ArrayList<String>> friendName_to_message) {
        this.friendName_to_message = friendName_to_message;
    }

    public Map<String, ArrayList<Date>> getFriendName_to_messageTime() {
        if(friendName_to_messageTime==null)
            return new ConcurrentHashMap<>();
        return friendName_to_messageTime;
    }

    public void setFriendName_to_messageTime(Map<String, ArrayList<Date>> friendName_to_messageTime) {
        this.friendName_to_messageTime = friendName_to_messageTime;
    }

    public Map<String, ArrayList<Boolean>> getFriendsName_to_messageBoolean() {
        if(friendsName_to_messageBoolean==null)
            return new ConcurrentHashMap<>();
        return friendsName_to_messageBoolean;
    }

    public void setFriendsName_to_messageBoolean(Map<String, ArrayList<Boolean>> friendsName_to_messageBoolean) {
        this.friendsName_to_messageBoolean = friendsName_to_messageBoolean;
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
            friendsName_to_messageBoolean = new ConcurrentHashMap<String , ArrayList<Boolean>>();
        }
        if(friendName_to_message.containsKey(sender)){
            friendName_to_message.get(sender).add(message);
            friendName_to_messageTime.get(sender).add(date);
            friendsName_to_messageBoolean.get(sender).add(true);
        }else{
            ArrayList<String> new_message_list = new ArrayList<String>();
            new_message_list.add(message);
            friendName_to_message.put(sender,new_message_list);
            ArrayList<Boolean> new_boolean_list = new ArrayList<Boolean>();
            new_boolean_list.add(true);
            friendsName_to_messageBoolean.put(sender,new_boolean_list);
            ArrayList<Date> new_date_list = new ArrayList<Date>();
            new_date_list.add(date);
            friendName_to_messageTime.put(sender,new_date_list);
        }
    }
}
