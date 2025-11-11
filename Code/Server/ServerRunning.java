package Code.Server;

import Code.GameCore.ServerGameEngine;
import Code.Object.Account;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRunning {

    private final int PORT=4869;
    private ServerSocket serverSocket;
    private List<ClientHandler> connectedClients;
    private Map<String,Room> rooms;
    private ExecutorService threadPool;
    private GameDatabase gameDatabase;
    private Map<String, Account>accounts;
    public static void main(String[] args) {

        new ServerRunning().startServer();
    }

    public void setAccounts(Map<String, Account> accounts) {
        this.accounts = accounts;
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    public ServerRunning() {
        connectedClients=new CopyOnWriteArrayList<ClientHandler>();
        rooms=new ConcurrentHashMap<String, Room>();
        threadPool=Executors.newCachedThreadPool();
        gameDatabase=new GameDatabase(this);
        accounts=new ConcurrentHashMap<>();
        this.gameDatabase.loadAccounts();
        System.out.println("accounts: "+accounts.size());
        accounts.forEach((id,account)->{
           System.out.println("id: "+account.getPlayerID()+"  password: "+account.getPassword());
           System.out.println("highest score: "+account.getHighestScore()+"  power stone: "+account.getPowerStone());
           System.out.println("achievement1: "+account.isAchievement1()+"  achievement2: "+account.isAchievement2()
           +"  achievement3: "+account.isAchievement3());
           System.out.println("online statement: "+account.isOnLine());
        });
    }

    public void startServer() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("Server started at :" + address.getHostAddress());
            serverSocket=new ServerSocket(PORT);
            System.out.println("Server has started listening on port:" + PORT);
            new Thread(()->{acceptClient();}).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClient() {
        while(!this.serverSocket.isClosed()) {
            try {
                Socket clientSocket=this.serverSocket.accept();
                ClientHandler clientHandler=new ClientHandler(clientSocket);
                connectedClients.add(clientHandler);
                threadPool.execute(clientHandler);
                System.out.println("Client accepted");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
  /*
    public void login(ClientHandler client,String playerName) {
        System.out.println("Login attempt");
        client.setPlayerID(playerName);
        System.out.println("Login successful: "+playerName);
        JSONObject obj=new JSONObject();
        obj.put("type","login_succeed");
        obj.put("player_id",playerName);
        client.out.println(obj.toString());
    }

 */

    public void createRoom(ClientHandler client,String roomID) {
        System.out.println("Creating a new room");
        Room test=rooms.get(roomID);
        if(test==null) {
            Room room = new Room(roomID);
            room.join(client);
            room.setHost(client.getPlayerID());
            rooms.put(roomID, room);
            System.out.println(client.getPlayerID() + " Room created: " + room.getRoomID());
            JSONObject obj = new JSONObject();
            obj.put("type", "create_room_succeed");
            obj.put("room_id", room.getRoomID());
            client.out.println(obj.toString());
        }
        else {
            System.out.println("Room already exists");
            JSONObject obj = new JSONObject();
            obj.put("type", "create_room_failed");
            obj.put("message", "Room already exists.");
            client.out.println(obj.toString());
        }
    }

    public void leaveRoom(ClientHandler client,String roomID) {
        System.out.println("Leaving a room");
        Room room=rooms.get(roomID);
        room.leave(client);
        JSONObject obj = new JSONObject();
        obj.put("type", "leave_room_succeed");
        client.out.println(obj.toString());
        System.out.println("Room leaved: " + room.getRoomID());
    }

    public void joinRoom(ClientHandler client,String roomID) {
        System.out.println("Joining a room");
        Room room=rooms.get(roomID);
        if(room==null) {
            JSONObject obj=new JSONObject();
            obj.put("type","join_room_failed");
            obj.put("message","Room not found");
            client.out.println(obj.toString());
            System.out.println("Room joined failed not found");
            return;
        }

        if(room.member>=4){
            JSONObject obj=new JSONObject();
            obj.put("type","join_room_failed");
            obj.put("message","Room member is full");
            client.out.println(obj.toString());
            System.out.println("Room member is full");
            return;
        }

        if(room.isInGame()){
            JSONObject obj=new JSONObject();
            obj.put("type","join_room_failed");
            obj.put("message","Room is in game");
            client.out.println(obj.toString());
            System.out.println("Room member is in game");
            return;
        }

        room.join(client);
        System.out.println(client.getPlayerID()+" Room joined: "+room.getRoomID());
        JSONObject obj=new JSONObject();
        obj.put("type","join_room_succeed");
        obj.put("room_id",room.getRoomID());
        client.out.println(obj.toString());
    }

    public void removeRoom(String roomID) {
        this.rooms.remove(roomID);
    }

    public void startGameInRoom(ClientHandler client,String roomID) {
        System.out.println("Starting game for room " + roomID);
        Room room=rooms.get(roomID);
        if(!room.getHost().equals(client.getPlayerID())) {
            JSONObject obj=new JSONObject();
            obj.put("type","start_game_failed");
            obj.put("message","You are not the host of this room.");
            client.out.println(obj.toString());
            return;
        }

        if(room.member<2){
            JSONObject obj=new JSONObject();
            obj.put("type","start_game_failed");
            obj.put("message","Room member is not enough.");
            client.out.println(obj.toString());
            return;
        }

        else{
            room.startGame();
        }

    }


    public class ClientHandler implements Runnable {
        private String playerID;
        private int color=1;
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            try {
                this.clientSocket = clientSocket;
                this.in =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.out=new PrintWriter(clientSocket.getOutputStream(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int getColor() {
            return color;
        }

        public String getPlayerID(){
            return playerID;
        }

        public void setPlayerID(String playerID) {
            this.playerID=playerID;
        }

        public PrintWriter getOut(){
            return out;
        }

        public void disconnected() {
            try {
                if (this.clientSocket != null && !this.clientSocket.isClosed()) {
                    this.in.close();
                    this.out.close();
                    this.clientSocket.close();
                    connectedClients.remove(this);
                    System.out.println("Client disconnected");
                }
            } catch (IOException e) {

            }
        }

        public void run() {
           String message;
           try {
               while ((message = in.readLine()) != null) {
                   JSONObject obj=new JSONObject(message);
                   String type=obj.getString("type");
                   switch(type) {

                       case"register":{
                           System.out.println("Registering an account");
                           String playerID=obj.getString("player_id");
                           String password=obj.getString("password");
                           System.out.println(playerID+" "+password);
                           if(ServerRunning.this.gameDatabase.createAccount(playerID,password)){
                             JSONObject obj1=new JSONObject();
                             obj1.put("type","register_succeed");
                             this.out.println(obj1.toString());
                               System.out.println("Registered account successfully");
                           }
                           else{
                               JSONObject obj1=new JSONObject();
                               obj1.put("type","register_failed");
                               obj1.put("message","Account already exists");
                               this.out.println(obj1.toString());
                               System.out.println("Registering account failed");
                           }

                           break;
                       }

                       case"login":{
                           System.out.println("Login attempt");
                        String playerID=obj.getString("player_id");
                        String password=obj.getString("password");
                        if(ServerRunning.this.gameDatabase.accountLogin(this, playerID,password)){
                         JSONObject obj1=new JSONObject();
                         obj1.put("type","login_succeed");
                         obj1.put("player_id",playerID);
                         this.out.println(obj1.toString());
                         System.out.println("Login account successfully");
                        }
                        else{
                            JSONObject obj1=new JSONObject();
                            obj1.put("type","login_failed");
                            obj1.put("message","Login failed, username/password is incorrect.");
                            this.out.println(obj1.toString());
                        }
                        //login(this, playerID);
                        break;
                       }

                       case"logout":{
                           String playerID=obj.getString("player_id");
                           ServerRunning.this.gameDatabase.accountLogout(this,playerID);
                           break;
                       }

                       case "change_color":{
                           String color=obj.getString("color");
                           switch(color) {
                               case "yellow":this.color=1;break;
                               case "red":this.color=2;break;
                               case "blue":this.color=3;break;
                               case "green":this.color=4;break;
                           }
                           JSONObject object=new JSONObject();
                           object.put("type","change_color_succeed");
                           this.out.println(object.toString());
                           break;
                       }

                       case "create_room": {
                           String roomID = obj.getString("room_id");
                           createRoom(this, roomID);
                           break;
                       }
                       case "leave_room":{
                           String roomID = obj.getString("room_id");
                           leaveRoom(this,roomID);
                           break;
                       }
                       case "join_room": {
                           String roomID =obj.getString("room_id");
                           joinRoom(this, roomID);
                           break;
                       }

                       case"start_game":{
                           String roomID =obj.getString("room_id");
                           startGameInRoom(this,roomID);
                           break;
                       }
                       case "command":{
                           //
                           System.out.println("Room: "+obj.getString("room_id"));
                           System.out.println("Player: "+obj.getString("player_id"));
                           System.out.println("action: "+obj.getString("direction"));
                           //
                            Room room=rooms.get(obj.getString("room_id"));
                            if(room!=null){
                                List<ClientHandler>players=room.getPlayers();
                                for(ClientHandler clientHandler:players){
                                    if(clientHandler.getPlayerID().equals(obj.getString("player_id"))&& room.isInGame()){
                                        room.gameEngine.updatePlayers(obj);
                                    }
                                }
                            }
                           break;
                       }
                       case"control":{
                           //
                            System.out.println("Room: "+obj.getString("room_id"));
                            System.out.println("Player: "+obj.getString("player_id"));
                            System.out.println("who: "+obj.getString("who"));
                           //
                           Room room=rooms.get(obj.getString("room_id"));
                           if(room!=null) {
                               ServerGameEngine gameEngine = room.gameEngine;
                               gameEngine.getPlayers().forEach((id,p)->{
                                  if(obj.getString("player_id").equals(id)){
                                      if(obj.getString("who").equals("ai")&&!p.isAiControl()){
                                          p.setReqDx(0);p.setReqDy(0);
                                          p.setAiControl(true);
                                      }
                                      else if(obj.getString("who").equals("human")&&p.isAiControl()){
                                          p.setAiControl(false);
                                      }

                                  }
                               });
                           }
                            break;
                       }
                       case"update_score":{
                           //
                           String playerID=obj.getString("player_id");
                           int score=obj.getInt("score");
                           System.out.println("Updating score...Player: "+playerID+" "+score);
                           //
                           ServerRunning.this.gameDatabase.updateHighestScore(playerID,score);
                           break;
                       }
                       case"rank_request":{
                           JSONObject obj1=ServerRunning.this.gameDatabase.requestForRank();
                           //
                           JSONArray array=obj1.getJSONArray("rankData");
                           for(int i=0;i<array.length();i++){
                            JSONObject obj2=array.getJSONObject(i);
                               System.out.println(obj2.getString("player_id")+" "+obj2.getInt("highest_score"));
                           }
                           //
                           this.out.println(obj1.toString());
                           break;
                       }
                       case"update_achievement":{
                           String playerID=obj.getString("player_id");
                           //
                           System.out.println(playerID);
                           System.out.println(obj.getString("which"));
                           //
                           ServerRunning.this.gameDatabase.updateAchievement(playerID,obj);
                           break;
                       }
                       case"achievement_request":{
                           JSONObject obj1=ServerRunning.this.gameDatabase.requestForAchievement(this.playerID);
                           //
                            System.out.println(obj1.toString());
                           //
                           this.out.println(obj1.toString());
                           break;
                       }
                   }
               }
           }
           catch (IOException e) {
               disconnected();
           }
           finally {
               disconnected();
           }
        }
    }

    public class Room {
        String host=null;
        String roomID;
        int member;
        List<ClientHandler> players;
        boolean inGame=false;
        ServerGameEngine gameEngine;
        Timer timer;

        public Room(String roomID) {
         this.roomID=roomID;
         this.member=0;
         this.players=new CopyOnWriteArrayList<ClientHandler>();
          }

        public List<ClientHandler> getPlayers(){
            return players;
          }

          public Timer getTimer(){
            return timer;
          }

          public void setInGame(boolean inGame){
            this.inGame=inGame;
          }

        public boolean isInGame() {
            return inGame;
        }

        public String getHost(){
         return host;
     }

        public void setHost(String host){
         this.host=host;
     }

        public String getRoomID(){
         return roomID;
     }

        public void join(ClientHandler client) {
         this.players.add(client);
         member++;
         }

         public void leave(ClientHandler client) {
            this.players.remove(client);
            member--;
            checkEmpty();
         }

         public void checkEmpty(){
            if(member==0){
                  ServerRunning.this.removeRoom(this.roomID);
            }
         }

        public void startGame(){
           JSONObject obj=new JSONObject();
           obj.put("type","start_game_succeed");
           this.gameEngine=new ServerGameEngine(this);
           JSONArray playersArray=this.gameEngine.getInitMessage();
           obj.put("players",playersArray);
           for (ClientHandler player : players) {
             player.out.println(obj.toString());
          }
            this.inGame=true;
            this.gameEngine.setGameRunning(true);
           //游戏进行时
           this.timer=new Timer(50,e -> {
               this.gameEngine.update();
               JSONObject state=this.gameEngine.getGameState();
               if(state!=null) {
                   for (ClientHandler player : players) {
                       player.out.println(state.toString());
                   }
               }
           });
           this.timer.start();
          }
    }
}
