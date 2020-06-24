import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String current_userName;


    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            List<String> userNames = Server.users.values().stream().map(User::getUser_name).collect(Collectors.toList());
            System.out.println(userNames);
            String purpose = dis.readUTF();
            if (purpose.equals("Register")) {
                System.out.println("user is trying to registering...");
                while (true) {
                    String message = dis.readUTF();
                    if (message.equals("UserEnteredCorrectly")) {
                        User newUser = new User();
                        newUser.setUser_name(dis.readUTF());
                        newUser.setPassword(dis.readUTF());
                        Server.users.put(newUser.getUser_name(), newUser);
                        Server.usersClientHandler.put(newUser.getUser_name(), this);
//                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Users.txt"));
//                    oos.writeObject(Server.users);
                        break;
                    } else if (Server.users.values().stream().map(User::getUser_name).collect(Collectors.toList()).contains(message)) {
                        dos.writeUTF("Duplicated");
                        dos.flush();
                    }
                }
            }else if(purpose.equals("Login")){
                System.out.println("user trying to logging in");
                String userName=null;
                String password=null;
                while (true){
                    userName = dis.readUTF();
                    password = dis.readUTF();
                    if(!Server.users.values().stream().map(User::getUser_name).collect(Collectors.toList()).contains(userName)){
                        dos.writeUTF("ERROR:this userName don't exist...");
                        dos.flush();
                        continue;
                    }else{
                        User currentUser = Server.users.get(userName);
                        if(!password.equals(currentUser.getPassword())){
                            dos.writeUTF("ERROR:password isn't correct...");
                            dos.flush();
                            continue;
                        }
                    }
                    dos.writeUTF("OK");
                    dos.flush();
                    Server.usersClientHandler.put(userName,this);
                    break;
                }
            }else if(purpose.equals("Service")){
                System.out.println("user trying to get service form server ...");
                current_userName = dis.readUTF();
                User current_user = Server.users.get(current_userName);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(current_user.getFriendName_to_message());
                oos.flush();
                oos.writeObject(current_user.getFriendName_to_messageTime());
                oos.flush();
                oos.writeObject(current_user.getFriendsName_to_messageBoolean());
                oos.flush();
                while(true){
                    switch(dis.readUTF()){
                        case "sent": {
                            String message = dis.readUTF();
                            String targetName = dis.readUTF();
                            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                            Date date = (Date) ois.readObject();
                            dis = new DataInputStream(socket.getInputStream());
                            User target = Server.users.get(targetName);
                            if (Server.usersClientHandler.containsKey(targetName)) {
                                ClientHandler targetHandler = Server.usersClientHandler.get(targetName);
                                targetHandler.dos.writeUTF("message");
                                targetHandler.dos.flush();
                                targetHandler.dos.writeUTF(current_userName);
                                targetHandler.dos.flush();
                                targetHandler.dos.writeUTF(message);
                                targetHandler.dos.flush();
                                ObjectOutputStream oos_to_target = new ObjectOutputStream(targetHandler.socket.getOutputStream());
                                oos_to_target.writeObject(date);
                                oos_to_target.flush();
                                targetHandler.dos = new DataOutputStream(targetHandler.socket.getOutputStream());
                            } else {
                                target.receive_message(message, date, current_userName);
                            }
                        }
                            break;
                        case "AddFriend":
                            String targetName = dis.readUTF();
                            dos.writeUTF("Answer to Add Friend");
                            dos.flush();
                            if(!Server.users.containsKey(targetName)){
                                dos.writeUTF("ERROR");
                            }else{
                                dos.writeUTF("OK");
                            }
                            dos.flush();
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException io) {
            io.printStackTrace();
        }
        System.out.println("user disconnected ...");
//        Server.usersClientHandler.remove(current_userName);
    }
}
