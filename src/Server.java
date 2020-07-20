import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    static Map<String, ClientHandler> usersClientHandler = new ConcurrentHashMap<>();
    static ArrayList<Map<String, ClientHandler>> rankedMapContainer = new ArrayList<>();
    static Map<String, ClientHandler> usersClientHandlerInGameUpdate = new ConcurrentHashMap<>();
    static Map<String, ClientHandler> usersClientHandlerInGameAdd = new ConcurrentHashMap<>();
    static Map<String, ClientHandler> xoClientHandler = new ConcurrentHashMap<>();
    static Map<String , ClientHandler> dostandboxesClientHandler = new ConcurrentHashMap<>();
    static Map<String, ClientHandler> guesswordClientHandler = new ConcurrentHashMap<>();
    static Map<String, User> users = new ConcurrentHashMap<>();
    static Map<String, Room> rooms = new ConcurrentHashMap<>();// <gamename,room>
    static Map<String,BestPlayerMapContainer> bestPlayerMapContainerMap =new ConcurrentHashMap<>();
    private static FileOutputStream fos_users;
    private static FileOutputStream fos_best_players;


    public static void main(String[] args) {

        try {
            /*
            Add Users to server by read from Users.txt file .
             */

//            FileOutputStream fos = new FileOutputStream("Users.txt");
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(users);
//            oos.flush();
//            oos.close();
//            fos.close();

//            FileOutputStream fos = new FileOutputStream("BestPlayer.txt");
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(bestPlayerMapContainerMap);
//            oos.flush();
//            oos.close();
//            fos.close();


            FileInputStream file1 = new FileInputStream("BestPlayer.txt");
            ObjectInputStream ois1 = new ObjectInputStream(file1);
            bestPlayerMapContainerMap = (ConcurrentHashMap) ois1.readObject();
            file1.close();
            ois1.close();

            FileInputStream file = new FileInputStream("Users.txt");
            ObjectInputStream ois = new ObjectInputStream(file);
            users = (ConcurrentHashMap) ois.readObject();
            file.close();
            ois.close();

            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("server is connected...");


            rooms.put("xo", new Room());
            rooms.put("guess word", new Room());
            rooms.put("dots and boxes", new Room());


            Map<String, ClientHandler> xoRankedClientHandler = new ConcurrentHashMap<>();
            Map<String, ClientHandler> guesswordRankedClientHandler = new ConcurrentHashMap<>();
            Map<String, ClientHandler> dotAndBoxRankedClientHandler = new ConcurrentHashMap<>();
            rankedMapContainer.add(xoRankedClientHandler);
            rankedMapContainer.add(guesswordRankedClientHandler);
            rankedMapContainer.add(dotAndBoxRankedClientHandler);


            //test
            Map<String,Integer> name_score=new HashMap<>();
            Map<String,String> name_image=new HashMap<>();

            name_score.put("Ali",65);
            name_score.put("javad",55);
            name_score.put("mohammad",50);
            name_score.put("reza",40);
            name_score.put("nahid",30);


            BestPlayerMapContainer bestPlayerMapContainerXo=new BestPlayerMapContainer();
            BestPlayerMapContainer bestPlayerMapContainerGuessWord=new BestPlayerMapContainer();
            BestPlayerMapContainer bestPlayerMapContainerDotesAndBox=new BestPlayerMapContainer();
            bestPlayerMapContainerXo.setName_score(name_score);
            bestPlayerMapContainerXo.setName_image(name_image);
            bestPlayerMapContainerMap.put("xo",bestPlayerMapContainerXo);
            bestPlayerMapContainerMap.put("guess word",bestPlayerMapContainerGuessWord);
            bestPlayerMapContainerMap.put("dots and boxes",bestPlayerMapContainerDotesAndBox);


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

    static synchronized  void write_users_in_file(){
        fos_users = null;
        ObjectOutputStream oos = null;
        try {
            fos_users = new FileOutputStream("Users.txt");
            oos = new ObjectOutputStream(fos_users);
            oos.writeObject(users);
            oos.flush();
            oos.close();
            fos_users.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    static synchronized  void write_best_players_in_file(){
        fos_best_players = null;
        ObjectOutputStream oos = null;
        try {
            fos_best_players = new FileOutputStream("BestPlayer.txt");
            oos = new ObjectOutputStream(fos_best_players);
            oos.writeObject(users);
            oos.flush();
            oos.close();
            fos_best_players.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }
}
