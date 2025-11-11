package Code.GameCore;


import Code.Server.ServerRunning.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerGameEngine {
 private Room room;
 private Map map;
 private ConcurrentHashMap<String,Pacman> players;
 private CopyOnWriteArrayList<Ghost> ghosts;
 private boolean isGameRunning=false;

    public ConcurrentHashMap<String, Pacman> getPlayers() {
        return players;
    }

    public void setGameRunning(boolean isGameRunning) {
     this.isGameRunning = isGameRunning;
 }

 public ServerGameEngine(Room room) {
     this.room = room;
     //生成地图
     this.map=new Map();
     this.players=new ConcurrentHashMap();
     this.ghosts=new CopyOnWriteArrayList();
     //创建玩家位置,生成不同位置
     int i=1;
     for(ClientHandler clientHandler:room.getPlayers()){
         String id=clientHandler.getPlayerID();
         int color=clientHandler.getColor();
         switch(i){
             case 1:{
                 Pacman pacman=PacmanFactory.createPacman(PacmanFactory.getPacmanType(color),0,
                         0,1,0);
                 this.players.put(id,pacman);
                 break;
             }
             case 2:{
                 Pacman pacman=PacmanFactory.createPacman(PacmanFactory.getPacmanType(color),14* map.getBLOCK_SIZE(),
                         0,1,0);
                 this.players.put(id,pacman);
                 break;
             }
             case 3:{
                 Pacman pacman=PacmanFactory.createPacman(PacmanFactory.getPacmanType(color),14* map.getBLOCK_SIZE(),
                         14* map.getBLOCK_SIZE(),1,0);
                 this.players.put(id,pacman);
                 break;
             }
             case 4:{
                 Pacman pacman=PacmanFactory.createPacman(PacmanFactory.getPacmanType(color),0,
                         14* map.getBLOCK_SIZE(),1,0);
                 this.players.put(id,pacman);
                 break;
             }
         }
         i++;
     }
     //生成幽灵
     ghosts.add(new Ghost(6* map.getBLOCK_SIZE(),6*map.getBLOCK_SIZE(),2,null));
     ghosts.add(new Ghost(8* map.getBLOCK_SIZE(),6*map.getBLOCK_SIZE(),2,null));
     ghosts.add(new Ghost(6* map.getBLOCK_SIZE(),8*map.getBLOCK_SIZE(),2,null));
     ghosts.add(new Ghost(8* map.getBLOCK_SIZE(),8*map.getBLOCK_SIZE(),2,null));

 }

 public JSONArray getInitMessage(){
     JSONArray jsonArray=new JSONArray();
     players.forEach((id,p)->{
         JSONObject jsonObject=new JSONObject();
         jsonObject.put("player_id",id);
         jsonObject.put("color",p.getColor());
         jsonObject.put("x",p.getX());
         jsonObject.put("y",p.getY());
         jsonObject.put("viewDx",p.getViewDx());
         jsonObject.put("viewDy",p.getViewDy());
         jsonArray.put(jsonObject);
     });
    return jsonArray;
 }

 public void update(){
     if(!isGameRunning) return;

     //1.更新玩家位置
     players.forEach((id,p)->{
         if(!p.isAiControl()){
             p.movePacman(this.map,this.room);
         }
         else{
             p.aiMove(this.map,this.room);
         }
     });

     //2.更新幽灵位置
     for(Ghost ghost:this.ghosts){
         ghost.moveGhost(this.map);
     }

     //3.检测碰撞
     checkCollisions();

     //4.检测所有玩家存活
     checkPlayersAlive();

     //5.检测豆子是否吃光
    if(map.isLevelCompleted()){
         endGame();
     }
 }

 public void checkCollisions(){         //检查全体玩家与幽灵的碰撞
     players.forEach((playerId, pacman) -> {
           for(Ghost ghost:this.ghosts) {
               if(!pacman.isPowerUp()) {
                   if (ghost.checkCollision(pacman.getX(), pacman.getY())) {
                       handlePlayerDeath(playerId);
                   }
               }
           }
         });
 }

 public void handlePlayerDeath(String playerId) {
     Pacman pacman = players.get(playerId);
     if (pacman == null) return;

     // 减少生命值
     pacman.setLives(pacman.getLives() - 1);

     if (pacman.getLives() < 0) {
         // 玩家彻底死亡
         players.remove(playerId);
         JSONObject jsonObject=new JSONObject();
         jsonObject.put("type","player_death");
         jsonObject.put("player_id",playerId);
         List<ClientHandler>playerList=room.getPlayers();
         for(ClientHandler clientHandler:playerList){
             clientHandler.getOut().println(jsonObject.toString());
         }
     } else {
         // 重置位置（复活）
         resetPlayerPosition(playerId);
     }
 }

    public void resetPlayerPosition(String playerId) {   // 重置玩家位置（复活）
        Pacman pacman = players.get(playerId);
        if (pacman != null) {
            pacman.setX(0); // 初始位置
            pacman.setY(0);
            pacman.setDx(0);
            pacman.setDy(0);
        }
    }

    public void checkPlayersAlive() {
        if (players.isEmpty()) {
            endGame();
        }
    }

    public void endGame() {
        isGameRunning = false;
        this.room.setInGame(false);
        this.room.getTimer().stop();
        JSONObject msg = new JSONObject();
        msg.put("type", "game_over");
        List<ClientHandler>playerList=room.getPlayers();
        for(ClientHandler clientHandler:playerList){
            clientHandler.getOut().println(msg.toString());
        }
    }

 public JSONObject getGameState(){
     if(!isGameRunning) return null;
     JSONObject jsonObject=new JSONObject();
     //1.玩家数据
     JSONArray playerData=new JSONArray();
     players.forEach((id,p)->{
         JSONObject jsonObject1=new JSONObject();
         jsonObject1.put("player_id",id);
         jsonObject1.put("x",p.getX());jsonObject1.put("y",p.getY());
         jsonObject1.put("viewDx",p.getViewDx());jsonObject1.put("viewDy",p.getViewDy());
         jsonObject1.put("score",p.getScore());
         playerData.put(jsonObject1);
     });

     //2.幽灵数据
     JSONArray ghostData=new JSONArray();
     for(Ghost ghost:this.ghosts){
         JSONObject jsonObject1=new JSONObject();
         jsonObject1.put("x",ghost.getX());
         jsonObject1.put("y",ghost.getY());
         ghostData.put(jsonObject1);
     }

     //3.地图数据
     String mapData=this.map.serializeMap();

     jsonObject.put("type","game_state");
     jsonObject.put("players",playerData);
     jsonObject.put("ghosts",ghostData);
     jsonObject.put("map",mapData);
     return jsonObject;
 }

 public void updatePlayers(JSONObject obj){
     if(!isGameRunning) return;
     String playerID=obj.getString("player_id");
     String direction=obj.getString("direction");
     Pacman pacman=this.players.get(playerID);
     if(pacman!=null){
      switch(direction){
          case "up":{
              pacman.setReqDx(0);
              pacman.setReqDy(-1);
              break;
          }
          case "down":{
              pacman.setReqDx(0);
              pacman.setReqDy(1);
              break;
          }
          case "left":{
              pacman.setReqDx(-1);
              pacman.setReqDy(0);
              break;
          }
          case "right":{
              pacman.setReqDx(1);
              pacman.setReqDy(0);
              break;
          }
      }

     }
 }

}
