import javax.management.ObjectName;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    public User user;
    public boolean is_rankedGame_started = false;


    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        String purpose = "";

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
                case "game":
                    game();
                    break;
                case "changeRoom":
                    changeRoom();
                    break;
                case "addRoom":
                    AddRoom();
                    break;
                case "offline":
                    offline();
                    break;
                case "XoListener":
                    Xolistener();
                    break;
                case "XoSendData":
                    XoSendData();
                    break;
                case "guess word listener":
                    guess_word_listener();
                    break;
                case "guess word send data":
                    guess_word_send_data();
                    break;
                case "listening round two":
                    guess_word_round_two_listener();
                    break;
                case "change profile":
                    change_profile();
                    break;
                case "changeGameScore":
                    changeGameScore();
                    break;
                case "searchForRankedGame":
                    searchForRankedGame();
                    break;
                case "removePlayerFromRankedGame":
                    removePlayerFromRankedGame();
                    break;
                case "getBestPlayer":
                    getBestPlayer();
                    break;
                case "changeBestPlayer":
                    changeBestPlayer();
                    break;
                case "dots and boxes message listener":
                    dost_and_boxes_listening();
                    break;
                case "dots and boxes sender":
                    dots_and_boxes_send();
                    break;

            }
        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("user disconnected by crash...");
        }

        System.out.println("purpose done ... ");
    }

    private void dots_and_boxes_send(){
        String current_username ;
        try{
            current_username = dis.readUTF();
            ArrayList<String> members = new ArrayList<>();
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                members.add(dis.readUTF());
            }
            int position = dis.readInt();
            System.out.println("the position to change is : " + position);
            String location_line = dis.readUTF();
            int color = dis.readInt();
            for (int i = 0; i < size; i++) {
                if ( members.get(i).equals(current_username))
                    continue;
                ClientHandler clientHandler = Server.dostandboxesClientHandler.get(members.get(i));
                clientHandler.dos.writeUTF("change");
                clientHandler.dos.flush();
                clientHandler.dos.writeInt(position);
                System.out.println("the position to change that send to members is : " + position);
                clientHandler.dos.flush();
                clientHandler.dos.writeUTF(location_line);
                clientHandler.dos.flush();
                clientHandler.dos.writeInt(color);
                clientHandler.dos.flush();
            }
            boolean is_prize = dis.readBoolean();
            if ( !is_prize) {
                int index = members.indexOf(current_username);
                if (index == members.size() - 1) {
                    ClientHandler clientHandler = Server.dostandboxesClientHandler.get(members.get(0));
                    clientHandler.dos.writeUTF("your turn");
                    clientHandler.dos.flush();
                } else {
                    ClientHandler clientHandler = Server.dostandboxesClientHandler.get(members.get(index + 1));
                    clientHandler.dos.writeUTF("your turn");
                    clientHandler.dos.flush();
                }
            }
            boolean is_game_finished = dis.readBoolean();
            if (is_game_finished ){
                Map<String,Integer> scores = new HashMap<>();
                for (int i = 0; i < size ; i++) {
                    ClientHandler clientHandler = Server.dostandboxesClientHandler.get(members.get(i));
                    clientHandler.dos.writeUTF("finish");
                    clientHandler.dos.flush();
                    scores.put(members.get(i),clientHandler.dis.readInt());
                }
                List<String> sorted_names = scores.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                for (int i = 0; i < size; i++) {
                    ClientHandler clientHandler = Server.dostandboxesClientHandler.get(members.get(i));
                    clientHandler.dos.writeUTF(sorted_names.get(0));
                    clientHandler.dos.flush();
                }
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void dost_and_boxes_listening(){
        String current_username ;
        try{
            current_username = dis.readUTF();
            Server.dostandboxesClientHandler.put(current_username,this);
            while (true){

            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void changeBestPlayer() {

    }

    private void getBestPlayer() {
        try {
            String whichGame = dis.readUTF();

            BestPlayerMapContainer bestPlayerMapContainer = Server.bestPlayerMapContainerMap.get(whichGame);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(bestPlayerMapContainer.getName_score());
            oos.flush();
            oos.writeObject(bestPlayerMapContainer.getName_image());
            oos.flush();

            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void change_profile() {
        try {
            String current_username = dis.readUTF();
            dos.writeUTF(Server.users.get(current_username).getPassword());
            dos.flush();
            System.out.println("profile are listening ... ");
            while (true) {
                String new_username = dis.readUTF();
                String new_password = dis.readUTF();
                if (Server.users.containsKey(new_username) && !new_username.equals(current_username)) {
                    dos.writeUTF("error");
                    dos.flush();
                    continue;
                } else {
                    dos.writeUTF("ok");
                    dos.flush();
                    User changed_user = Server.users.get(current_username);
                    Server.usersClientHandler.remove(current_username);
                    Server.users.values().stream()
                            .filter((user) -> user.getFriendName_to_message().containsKey(current_username))
                            .forEach((user) -> {
                                user.getFriendName_to_message().put(new_username, user.getFriendName_to_message().remove(current_username));
                                user.getFriendName_to_messageTime().put(new_username, user.getFriendName_to_messageTime().remove(current_username));
                                user.getFriendsName_to_messageType().put(new_username, user.getFriendsName_to_messageType().remove(current_username));
                                user.getFriendName_to_profile().put(new_username,user.getFriendName_to_profile().remove(current_username));
                            });
                    changed_user.setUser_name(new_username);
                    changed_user.setPassword(new_password);
                    Server.users.remove(current_username);
                    Server.users.put(new_username, changed_user);
                    break;
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void removePlayerFromRankedGame() {
        try {
            int whichGame = dis.readInt();
            Map<String, ClientHandler> thisRankedMap = Server.rankedMapContainer.get(whichGame);
            String player = dis.readUTF();
            thisRankedMap.remove(player);
            System.out.println("re: " + player + " " + thisRankedMap.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void searchForRankedGame() {
        try {
            System.out.println("user is looking for rankedGame...");
            int whichGame = Integer.parseInt(dis.readUTF());
            int score = Integer.parseInt(dis.readUTF());
            String username = dis.readUTF();
            Map<String, ClientHandler> thisRankedMap = Server.rankedMapContainer.get(whichGame);
            System.out.println("se: " + thisRankedMap.size());
            //search for a player who is waiting for play RankedGame...
            for (String name : thisRankedMap.keySet()
            ) {
                ClientHandler clientHandler = thisRankedMap.get(name);
                int opponent_score = Server.users.get(name).getGameScore().get(whichGame);
                if (opponent_score >= score - 50 && opponent_score <= 50 + score) {
                    dos.writeUTF("startRankedGamePlayer2");
                    dos.flush();
                    dos.writeUTF(name);
                    clientHandler.dos.writeUTF("startRankedGamePlayer1");
                    clientHandler.dos.flush();
                    clientHandler.dos.writeUTF(username);
                    clientHandler.dos.flush();
                    clientHandler.is_rankedGame_started = true;
                    thisRankedMap.remove(name);
                    return;
                }

            }


            dos.writeUTF("wait");
            dos.flush();
            //add this user to waiting rankedGame queue
            thisRankedMap.put(username, this);
            while (!is_rankedGame_started) {
                String str = dis.readUTF();
                if (str.equals("cancel")) {
                    thisRankedMap.remove(username);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void changeGameScore() {
        try {
            String username = dis.readUTF();
            User user = Server.users.get(username);
            int gameIndex = dis.readInt();
            int score = dis.readInt();
            user.getGameScore().set(gameIndex, score);
            String which_game = null;
            if (gameIndex == 0)
                which_game = "xo";
            else if (gameIndex == 1)
                which_game = "guess word";
            else
                which_game = "dots and boxes";

            BestPlayerMapContainer bestPlayerMapContainer = Server.bestPlayerMapContainerMap.get(which_game);
            if (bestPlayerMapContainer.getName_score().size() < 10) {
                bestPlayerMapContainer.getName_score().put(username, score);
                 bestPlayerMapContainer.getName_image().put(username, Server.users.get(username).getProfile());
                Server.write_best_players_in_file();
            } else {
                Map.Entry<String, Integer> minEntry = bestPlayerMapContainer.getName_score().entrySet().stream().min(Map.Entry.comparingByValue()).get();
                if (minEntry.getValue() < score) {
                    bestPlayerMapContainer.getName_score().remove(minEntry.getKey());
                    bestPlayerMapContainer.getName_image().remove(minEntry.getKey());
                    bestPlayerMapContainer.getName_score().put(username, score);
                    bestPlayerMapContainer.getName_image().put(username, Server.users.get(username).getProfile());
                    Server.write_best_players_in_file();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void guess_word_round_two_listener() {
        String current_username = "";
        try {
            current_username = dis.readUTF();
            Server.guesswordClientHandler.put(current_username, this);
            while (true) {
                if (dis.readUTF().equals("exit"))
                    break;
            }
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            Server.guesswordClientHandler.remove(current_username);
            System.out.println();
        }
    }

    private void guess_word_send_data() {
        try {
            String targetName = dis.readUTF();
            String word = dis.readUTF();
            ClientHandler clientHandler = Server.guesswordClientHandler.get(targetName);
            clientHandler.dos.writeUTF(word);
            clientHandler.dos.flush();
            while (true) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void guess_word_listener() {
        String current_user_name = "";
        try {
            current_user_name = dis.readUTF();
            Server.guesswordClientHandler.put(current_user_name, this);
            System.out.println("guess word listening started....");
            String opponent = dis.readUTF();
            String result = dis.readUTF();
            ClientHandler clientHandler = Server.guesswordClientHandler.get(opponent);
            System.out.println(result);
            if (result.equals("win"))
                clientHandler.dos.writeUTF("lose");
            else if (result.equals("lose"))
                clientHandler.dos.writeUTF("win");
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            Server.guesswordClientHandler.remove(current_user_name);
            System.out.println("guess word listening finished ...");
        }
    }

    private void XoSendData() throws IOException {
        String targetName = dis.readUTF();
        int index = dis.readInt();

        ClientHandler clientHandler = Server.xoClientHandler.get(targetName);
        clientHandler.dos.writeInt(index);
        clientHandler.dos.flush();
    }

    private void Xolistener() {
        try {
            String current_username = dis.readUTF();
            Server.xoClientHandler.put(current_username, this);
            System.out.println("xo game listening started....");
            while (true) {

            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void AddRoom() {
        try {
            String current_username = dis.readUTF();
            Server.usersClientHandlerInGameAdd.put(current_username, this);
            System.out.println("room add listening started....");
            while (true) {

            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void changeRoom() {
        try {
            String current_username = dis.readUTF();
            Server.usersClientHandlerInGameUpdate.put(current_username, this);
            System.out.println("room Change listening started....");
            while (true) {

            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void game() {
        System.out.println("user is trying connect to game part...");
        try {
            String purpose_in_game_part = dis.readUTF();
            String whichGame = dis.readUTF();
            System.out.println(whichGame);
            switch (purpose_in_game_part) {
                case "getAllRooms":

                    Room roomMap = Server.rooms.get(whichGame);
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    oos.writeObject(roomMap.getRoomName_to_joined_user());
                    oos.flush();
                    oos.writeObject(roomMap.getRoomName_to_maxPlayer());
                    oos.flush();

                    break;
                case "addRoom":

                    String roomName = dis.readUTF();

                    if (Server.rooms.get(whichGame).getRoomName_to_maxPlayer().containsKey(roomName))
                        dos.writeBoolean(false);

                    else {
                        dos.writeBoolean(true);
                        Integer maxPlayers = dis.readInt();
                        String joined_user = dis.readUTF();
                        ArrayList<String> userList = new ArrayList<>();
                        userList.add(joined_user);

                        Server.rooms.get(whichGame).getRoomName_to_joined_user().put(roomName, userList);
                        Server.rooms.get(whichGame).getRoomName_to_maxPlayer().put(roomName, maxPlayers);

                        for (String user : Server.usersClientHandlerInGameAdd.keySet()) {
                            ClientHandler target = Server.usersClientHandlerInGameAdd.get(user);

                            target.dos.writeUTF(whichGame);
                            target.dos.flush();
                            target.dos.writeUTF(roomName);
                            target.dos.flush();
                            target.dos.writeInt(userList.size());
                            target.dos.flush();
                            for (int i = 0; i < userList.size(); i++) {
                                target.dos.writeUTF(userList.get(i));
                                target.dos.flush();
                            }
                            target.dos.writeUTF(String.valueOf(maxPlayers));
                            target.dos.flush();
                            System.out.println("add to " + user);
                        }

                    }

                    break;
                case "update":
                    System.out.println("user is in update...");
                    String room_name = dis.readUTF();
                    String user_thats_want_to_join = dis.readUTF();
                    Server.rooms.get(whichGame).getRoomName_to_joined_user().get(room_name).add(user_thats_want_to_join);
                    boolean is_dotsAndBoxes_start = false;
                    if (whichGame.equals("dots and boxes")) {
                        if (Server.rooms.get(whichGame).getRoomName_to_joined_user().get(room_name).size() == Server.rooms.get(whichGame).getRoomName_to_maxPlayer().get(room_name)) {
                            is_dotsAndBoxes_start = true;
                        }
                    }
                    for (String username : Server.usersClientHandlerInGameUpdate.keySet()
                    ) {
                        ClientHandler target = Server.usersClientHandlerInGameUpdate.get(username);
                        target.dos.writeUTF(whichGame);
                        target.dos.flush();
                        target.dos.writeUTF(room_name);
                        target.dos.flush();
                        target.dos.writeUTF(user_thats_want_to_join);
                        target.dos.flush();
                        if (whichGame.equals("dots and boxes")) {
                            if (is_dotsAndBoxes_start) {
                                target.dos.writeUTF("start");
                                target.dos.flush();
                            } else {
                                target.dos.writeUTF("resume");
                                target.dos.flush();
                            }
                        }
                    }

                    break;
                case "removeRoom":
                    String room_name_removed = dis.readUTF();
                    Server.rooms.get(whichGame).getRoomName_to_joined_user().remove(room_name_removed);
                    Server.rooms.get(whichGame).getRoomName_to_maxPlayer().remove(room_name_removed);

                    for (String username : Server.usersClientHandlerInGameUpdate.keySet()
                    ) {
                        ClientHandler target = Server.usersClientHandlerInGameUpdate.get(username);
                        target.dos.writeUTF(whichGame);
                        target.dos.flush();
                        target.dos.writeUTF(room_name_removed);
                        target.dos.flush();
                        target.dos.writeUTF("remove");
                        target.dos.flush();
                    }

                    break;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
        methods which use in switch run method of client handler.
     */


    private void register() {
        try {
            while (true) {
                String message = dis.readUTF();
                if (message.equals("UserEnteredCorrectly")) {
                    User newUser = new User();
                    newUser.setUser_name(dis.readUTF());
                    newUser.setPassword(dis.readUTF());

                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    String prof = (String) ois.readObject();

                    newUser.setProfile(prof);

                    Server.users.put(newUser.getUser_name(), newUser);
                    user = newUser;
                    break;
                } else if (Server.users.containsKey(message)) {
                    dos.writeUTF("Duplicated");
                    dos.flush();
                } else {
                    dos.writeUTF("ok");
                    dos.flush();
                }
            }
        } catch (IOException | ClassNotFoundException io) {
            io.printStackTrace();
        }
    }

    private void login() {
        System.out.println("user trying to logging in");
        String userName;
        String password;
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
                this.user = Server.users.get(userName);

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(Server.users.get(userName).getProfile());
                dos.flush();

                break;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void giveData() {
        try {
            String current_userName = dis.readUTF();
            User current_user = Server.users.get(current_userName);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(current_user.getFriendName_to_message());
            oos.flush();
            oos.writeObject(current_user.getFriendName_to_messageTime());
            oos.flush();
            oos.writeObject(current_user.getFriendsName_to_messageType());
            oos.flush();
            oos.writeObject(current_user.getGameScore());
            oos.flush();
            oos.writeObject(current_user.getFriendName_to_profile());
            oos.flush();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    //haji alan koja haro okey konm???????????????????????????
    private void addFriend() {
        try {
            String targetName = dis.readUTF();
            if (!Server.users.containsKey(targetName)) {
                dos.writeUTF("ERROR");
                dos.flush();
            } else {
                dos.writeUTF("OK");
                dos.flush();
                User user = Server.users.get(dis.readUTF());
                if (user.friendName_to_message == null) {
                    user.friendName_to_message = new ConcurrentHashMap<>();
                    user.friendName_to_messageTime = new ConcurrentHashMap<>();
                    user.friendsName_to_messageType = new ConcurrentHashMap<>();
                    user.friendName_to_profile=new ConcurrentHashMap<>();
                }

                user.friendName_to_message.put(targetName, new ArrayList<String>());
                user.friendName_to_messageTime.put(targetName, new ArrayList<Date>());
                user.friendsName_to_messageType.put(targetName, new ArrayList<Integer>());
                user.friendName_to_profile.put(targetName,Server.users.get(targetName).getProfile());

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(Server.users.get(targetName).getProfile());
                oos.flush();

                dos = new DataOutputStream(socket.getOutputStream());
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void sendMessage() {
        System.out.println("user try to send a message ...");
        try {
            String current_name = dis.readUTF();
            String target_name = dis.readUTF();
            String message = dis.readUTF();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            Date time = (Date) ois.readObject();
            if (Server.usersClientHandler.containsKey(target_name)) {
                ClientHandler target = Server.usersClientHandler.get(target_name);
                target.dos.writeUTF(current_name);
                target.dos.flush();
                target.dos.writeUTF(message);
                target.dos.flush();
                ObjectOutputStream oos = new ObjectOutputStream(target.socket.getOutputStream());
                boolean is_first=target.dis.readBoolean();
                System.out.println("sad");
                if (is_first) {
                    oos.writeObject(Server.users.get(current_name).getProfile());
                    System.out.println("isfirs");
                    oos.flush();
                    Server.users.get(target_name).getFriendName_to_profile().put(current_name,Server.users.get(current_name).getProfile());
                }
                oos.writeObject(time);
                oos.flush();
                target.dis = new DataInputStream(target.socket.getInputStream());
                System.out.println("message sent to online user ...");
            }
            Server.users.get(target_name).receive_message(message, time, current_name ,Server.users.get(current_name).getProfile());
            Server.users.get(current_name).friendsName_to_messageType.get(target_name).add(1);
            Server.users.get(current_name).friendName_to_messageTime.get(target_name).add(time);
            Server.users.get(current_name).friendName_to_message.get(target_name).add(message);
            if (! Server.users.get(current_name).getFriendName_to_profile().containsKey(target_name))
                Server.users.get(current_name).getFriendName_to_profile().put(target_name,Server.users.get(target_name).getProfile());
        } catch (IOException | ClassNotFoundException io) {
            io.printStackTrace();
        }
    }


    private void receiveMessage() {
        String current_username = "";
        try {
            current_username = dis.readUTF();
            Server.usersClientHandler.put(current_username, this);
            System.out.println("message listening started....");
            while (true) {
                if (socket.isClosed())
                    break;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void offline() {
        try {
            String name = dis.readUTF();
            Server.usersClientHandler.get(name).socket.close();
            Server.usersClientHandler.remove(name);
            Server.write_users_in_file();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
