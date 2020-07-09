import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    static Map<String, ClientHandler> usersClientHandler = new ConcurrentHashMap<>();
    static Map<String, ClientHandler> usersClientHandlerInGameUpdate = new ConcurrentHashMap<>();
    static Map<String, ClientHandler> usersClientHandlerInGameAdd = new ConcurrentHashMap<>();
    static Map<String, ClientHandler> xoClientHandler = new ConcurrentHashMap<>();
    static Map<String, User> users = new ConcurrentHashMap<>();
    static Map<String,Room> rooms=new ConcurrentHashMap<>();// <gamename,room>
    public static void main(String[] args) {

        try {
            /*
            Add Users to server by read from Users.txt file .
             */
            FileInputStream file = new FileInputStream("Users.txt");
            ObjectInputStream ois = new ObjectInputStream(file);
            users = (ConcurrentHashMap) ois.readObject();
            file.close();
            ois.close();

            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("server is connected...");


            rooms.put("xo",new Room());

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("a user connected...");
                Thread clientHandler = new Thread(new ClientHandler(socket));
                clientHandler.start();
            }


        } catch (IOException | ClassNotFoundException io) {
            io.printStackTrace();
        }
    }
}
