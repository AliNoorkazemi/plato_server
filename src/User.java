import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String user_name;
    private String password;
    private ArrayList<String> friends;


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
}
