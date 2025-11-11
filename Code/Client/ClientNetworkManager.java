package Code.Client;
import Code.GUI.AchievementGUI;
import Code.GUI.RankGUI;
import Code.GameCore.BoardForMulti;
import Code.GameCore.GameAudioPlayer;
import Code.GameCore.Pacman;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientNetworkManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientRunning client;

    public ClientNetworkManager(ClientRunning client,String host, int port) {
        try{
            this.client=client;
            this.socket=new Socket(host,port);
            this.out=new PrintWriter(socket.getOutputStream(),true);
            this.in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(()->receiveMessage()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String msg){
            out.println(msg);
    }

    public void receiveMessage(){
         while(true){
             try {
                 String msg=in.readLine();
                 JSONObject obj=new JSONObject(msg);
                 String type=obj.getString("type");
                 switch (type){
                     case "register_succeed":{
                         JOptionPane.showMessageDialog(this.client,"Successfully register.","Info",
                                 JOptionPane.INFORMATION_MESSAGE);
                      break;
                     }
                     case "register_failed":{
                      JOptionPane.showMessageDialog(this.client,obj.get("message"),"Info",JOptionPane.INFORMATION_MESSAGE);
                      break;
                     }
                     case"login_succeed": {
                         this.client.setPlayerName(obj.getString("player_id"));
                         System.out.println("login: "+this.client.getPlayerName());
                         JOptionPane.showMessageDialog(this.client,
                                 "Login Succeed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                         this.client.cardLayout.show(this.client.cardPanel,"Main");
                         GameAudioPlayer.playMenuBgm(GameAudioPlayer.menuBgmPath);
                         break;
                     }

                     case "login_failed": {
                         JOptionPane.showMessageDialog(this.client,obj.get("message"),"Info",
                                 JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }
                     case"change_color_succeed": {
                         JOptionPane.showMessageDialog(this.client,
                                 "Change Color Succeed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }

                     case "create_room_succeed": {
                         this.client.setCurrentRoom(obj.getString("room_id"));
                         System.out.println("create_room: " + this.client.getCurrentRoom());
                         JOptionPane.showMessageDialog(this.client,
                                 "Create Room Succeed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }

                     case"create_room_failed": {
                         JOptionPane.showMessageDialog(this.client,
                                 "Create Room Failed." + obj.getString("message"), "Info", JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }

                     case "join_room_succeed": {
                         this.client.setCurrentRoom(obj.getString("room_id"));
                         System.out.println("join_room: " + this.client.getCurrentRoom());
                         JOptionPane.showMessageDialog(this.client,
                                 "Join Room Succeed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }
                     case"join_room_failed": {
                         JOptionPane.showMessageDialog(this.client,
                                 "Join Room Failed." + obj.getString("message"), "Info", JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }
                     case "leave_room_succeed": {
                         this.client.setCurrentRoom(null);
                         JOptionPane.showMessageDialog(this.client,"Leave Room Succeed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }
                     case "start_game_failed": {
                         JOptionPane.showMessageDialog(this.client,
                                 "Start Game Failed." + obj.getString("message"), "Info", JOptionPane.INFORMATION_MESSAGE);
                         break;
                     }
                     case "start_game_succeed": {
                         GameAudioPlayer.stopBgm();
                         SwingUtilities.invokeLater(()->{
                             GameAudioPlayer.playShortSound(GameAudioPlayer.gameStartBgmPath);
                             GameAudioPlayer.playDefaultBgm(GameAudioPlayer.defaultBgmPath);
                             JOptionPane.showMessageDialog(this.client,
                                 "Start Game Succeed.", "Info", JOptionPane.INFORMATION_MESSAGE);
                         });
                         //
                         JSONArray arr=obj.getJSONArray("players");
                         for(int i=0;i<arr.length();i++)
                         {
                          JSONObject obj1=arr.getJSONObject(i);
                          System.out.println(obj1.getString("player_id"));
                          System.out.println(obj1.getInt("color"));
                          System.out.println(obj1.getInt("x"));
                          System.out.println(obj1.getInt("y"));
                          System.out.println(obj1.getInt("viewDx"));
                          System.out.println(obj1.getInt("viewDy"));
                         }
                         //
                         this.client.boardForMulti=new BoardForMulti(this.client,arr);
                         this.client.cardPanel.add(this.client.boardForMulti,"MultiplePlaying");
                         this.client.cardLayout.show(this.client.cardPanel,"MultiplePlaying");
                         break;
                     }
                     case "game_state":{
                         //
                         JSONArray arr=obj.getJSONArray("ghosts");
                         for(int i=0;i<arr.length();i++)
                         {
                             JSONObject obj1=arr.getJSONObject(i);
                             System.out.println(obj1.getInt("x"));
                             System.out.println(obj1.getInt("y"));
                         }
                         //
                         this.client.boardForMulti.getMultipleGameEngine().update(obj);
                         this.client.boardForMulti.repaint();
                         break;
                     }

                     case"play_music":{
                         switch (obj.getString("function")){
                             case "dot":GameAudioPlayer.playShortSound(GameAudioPlayer.eatDotSoundPath);break;
                             case "fruit":GameAudioPlayer.playShortSound(GameAudioPlayer.eatFruitSoundPath);break;
                         }
                         break;
                     }

                     case "player_death":
                     {
                         GameAudioPlayer.playShortSound(GameAudioPlayer.deathSoundPath);
                         this.client.boardForMulti.getMultipleGameEngine().erasePlayerImage(obj.getString("player_id"));
                         if(obj.getString("player_id").equals(this.client.getPlayerName())){
                             JOptionPane.showMessageDialog(this.client,"You are dead.", "Info",
                                     JOptionPane.INFORMATION_MESSAGE);
                         }
                         break;
                     }

                     case"game_over":{
                         GameAudioPlayer.stopBgm();
                         Map<String, Pacman>players=this.client.boardForMulti.getMultipleGameEngine().getPlayers();
                         players.forEach((id,p)->{
                             if(id.equals(this.client.getPlayerName())){
                                 int score=p.getScore();
                                 JOptionPane.showMessageDialog(this.client,
                                         "Game Over. Your final Score: "+score, "Info",
                                         JOptionPane.INFORMATION_MESSAGE);
                             }
                         });
                         this.client.boardForMulti.getMultipleGameEngine().setInGame(false);
                         client.cardLayout.show(client.cardPanel, "PlayMultiple");
                         client.cardPanel.remove(this.client.boardForMulti);
                         client.cardPanel.revalidate();
                         client.cardPanel.repaint();

                         //
                          JSONObject obj1=new JSONObject();
                          obj1.put("type","update_achievement");
                          obj1.put("player_id",this.client.getPlayerName());
                          obj1.put("which","achievement_1");
                          this.sendMessage(obj1.toString());
                         //
                         break;
                     }
                     case"rank_request":{
                         JPanel RankGUI=new RankGUI(this.client,obj);
                         this.client.cardPanel.add(RankGUI,"Rank");
                         this.client.cardLayout.show(this.client.cardPanel, "Rank");
                         break;
                     }
                     case"achievement_request":{
                        JPanel achievementGUI=new AchievementGUI(this.client,obj);
                        this.client.cardPanel.add(achievementGUI,"Achievement");
                        this.client.cardLayout.show(this.client.cardPanel, "Achievement");
                        break;
                     }
                 }
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }
    }
}
