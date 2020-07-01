import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;


    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        String purpose="";

        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

            purpose = dis.readUTF();

            switch (purpose) {
                case "Register":
                    register();
                    break;
                case "Login":
                    login();
                    break;
                case "giveData":
                    giveData();
                    break;
                case "AddFriend":
                    addFriend();
                    break;
                case "sendMessage":
                    sendMessage();
                    break;
                case "messageListener":
                    receiveMessage();
                    break;
                case "offline":
                    offline();
                    break;
            }
        } catch (IOException  io) {
            io.printStackTrace();
            System.out.println("user disconnected by crash...");
        }

        System.out.println("purpose done ... ");
    }

    /*
        methods which use in switch run method of client handler.
     */

    private void register(){
        try {
            while (true) {
                String message = dis.readUTF();
                if (message.equals("UserEnteredCorrectly")) {
                    User newUser = new User();
                    newUser.setUser_name(dis.readUTF());
                    newUser.setPassword(dis.readUTF());
                    Server.users.put(newUser.getUser_name(), newUser);
                    break;
                } else if (Server.users.containsKey(message)) {
                    dos.writeUTF("Duplicated");
                    dos.flush();
                }
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void login(){
        System.out.println("user trying to logging in");
        String userName ;
        String password ;
        try {
            while (true) {
                userName = dis.readUTF();
                password = dis.readUTF();
                if (!Server.users.containsKey(userName)) {
                    dos.writeUTF("ERROR:this userName don't exist...");
                    dos.flush();
                    continue;
                } else {
                    User currentUser = Server.users.get(userName);
                    if (!password.equals(currentUser.getPassword())) {
                        dos.writeUTF("ERROR:password isn't correct...");
                        dos.flush();
                        continue;
                    }
                }
                dos.writeUTF("OK");
                dos.flush();
                break;
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void giveData(){
        try {
            String current_userName = dis.readUTF();
            User current_user = Server.users.get(current_userName);
            System.out.println(current_user.getFriendName_to_message().keySet());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(current_user.getFriendName_to_message());
            oos.flush();
            oos.writeObject(current_user.getFriendName_to_messageTime());
            oos.flush();
            oos.writeObject(current_user.getFriendsName_to_messageType());
            oos.flush();
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void addFriend(){
        try{
            String targetName = dis.readUTF();
            if (!Server.users.containsKey(targetName)) {
                dos.writeUTF("ERROR");
                dos.flush();
            } else {
                dos.writeUTF("OK");
                dos.flush();
                User user = Server.users.get(dis.readUTF());
                if(user.friendName_to_message==null){
                    user.friendName_to_message = new ConcurrentHashMap<>();
                    user.friendName_to_messageTime = new ConcurrentHashMap<>();
                    user.friendsName_to_messageType = new ConcurrentHashMap<>();
                }
                user.friendName_to_message.put(targetName,new ArrayList<String>());
                user.friendName_to_messageTime.put(targetName,new ArrayList<Date>());
                user.friendsName_to_messageType.put(targetName,new ArrayList<Integer>());
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void sendMessage(){
        System.out.println("user try to send a message ...");
        try {
            String current_name = dis.readUTF();
            String target_name = dis.readUTF();
            String message = dis.readUTF();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Date time = (Date)ois.readObject();
            if(Server.usersClientHandler.containsKey(target_name)){
                ClientHandler target = Server.usersClientHandler.get(target_name);
                target.dos.writeUTF(current_name);
                target.dos.flush();
                target.dos.writeUTF(message);
                target.dos.flush();
                ObjectOutputStream oos = new ObjectOutputStream(target.socket.getOutputStream());
                oos.writeObject(time);
                oos.flush();
                target.dis = new DataInputStream(target.socket.getInputStream());
                System.out.println("message sent to online user ...");
            }
            Server.users.get(target_name).receive_message(message,time,current_name);
            Server.users.get(current_name).friendsName_to_messageType.get(target_name).add(1);
            Server.users.get(current_name).friendName_to_messageTime.get(target_name).add(time);
            Server.users.get(current_name).friendName_to_message.get(target_name).add(message);
        }catch (IOException | ClassNotFoundException io){
            io.printStackTrace();
        }
    }

    private void receiveMessage(){
        String current_username="";
        try {
            current_username = dis.readUTF();
            Server.usersClientHandler.put(current_username,this);
            while (true) {
                if(socket.isClosed())
                    break;
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void offline(){
        try{
            String name = dis.readUTF();
            Server.usersClientHandler.get(name).socket.close();
            Server.usersClientHandler.remove(name);
        }catch (IOException io){
            io.printStackTrace();
        }
    }
}
