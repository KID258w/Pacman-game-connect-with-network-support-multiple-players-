package Code.GameCore;

import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultipleGameEngine extends GameEngine {
    private Code.GameCore.Map map;
    private Map<String, Pacman> players;
    private List<Ghost> ghosts;

    public Code.GameCore.Map getMap(){
        return map;
    }

    public Map<String, Pacman> getPlayers() {
        return players;
    }

    public MultipleGameEngine(JSONArray players) {
        this.map = new Code.GameCore.Map();     //地图初始化

        this.players = new ConcurrentHashMap<>();    //玩家位置初始化
        for (int i = 0; i < players.length(); i++) {
            JSONObject player = players.getJSONObject(i);
            String playerID = player.getString("player_id");
            int color = player.getInt("color");
            int x = player.getInt("x");int y = player.getInt("y");
            int viewDx = player.getInt("viewDx");int viewDy = player.getInt("viewDy");
            Pacman pacman=PacmanFactory.createPacman(PacmanFactory.getPacmanType(color),x,y,viewDx,viewDy);
            this.players.put(playerID, pacman);
        }

        this.ghosts=new CopyOnWriteArrayList<>();     //幽灵初始化
        ghosts.add(new Ghost(6* map.getBLOCK_SIZE(),6*map.getBLOCK_SIZE(),2,ghostsImage[0]));
        ghosts.add(new Ghost(8* map.getBLOCK_SIZE(),6*map.getBLOCK_SIZE(),2,ghostsImage[1]));
        ghosts.add(new Ghost(6* map.getBLOCK_SIZE(),8*map.getBLOCK_SIZE(),2,ghostsImage[2]));
        ghosts.add(new Ghost(8* map.getBLOCK_SIZE(),8*map.getBLOCK_SIZE(),2,ghostsImage[3]));

        super.setInGame(true);
    }

    public void erasePlayerImage(String playerID) {
     Pacman pacman=players.get(playerID);
     pacman.setDead(true);
    }

    public void update(JSONObject data) {
      if(!super.isInGame())return;
        //首先更新一下动画
        players.forEach((id,p)->{
        p.updateAnimation();
        });

        //1.同步玩家
        JSONArray players=data.getJSONArray("players");
        for(int i=0;i<players.length();i++){
            JSONObject player=players.getJSONObject(i);
            Pacman localPacman=this.players.get(player.getString("player_id"));
            localPacman.setX(player.getInt("x"));localPacman.setY(player.getInt("y"));
            localPacman.setViewDx(player.getInt("viewDx"));localPacman.setViewDy(player.getInt("viewDy"));
            localPacman.setScore(player.getInt("score"));
        }


        //2.同步幽灵
        JSONArray ghosts = data.getJSONArray("ghosts");
        for (int i = 0; i < ghosts.length(); i++) {
            JSONObject ghost = ghosts.getJSONObject(i);
            Ghost localGhost=this.ghosts.get(i);
            localGhost.setX(ghost.getInt("x"));
            localGhost.setY(ghost.getInt("y"));
        }

        //3.同步地图
        String map=data.getString("map");
        this.map.setScreenData(Code.GameCore.Map.deserializeMap(map));

    }

    public void draw(Graphics2D g2d){
        this.map.drawMaze(g2d);    //绘制游戏地图
        for (Pacman pacman : this.players.values()) {   //绘制玩家
            if(!pacman.isDead()){
                pacman.drawPacman(g2d);
            }

        }
        for (Ghost ghost : this.ghosts) {       //绘制幽灵
            ghost.draw(g2d);
        }

        Font smallFont = new Font("Helvetica", Font.BOLD, 14);   //显示分数
        g2d.setFont(smallFont);
        g2d.setColor(Color.WHITE);
        int i=0;
        for(Map.Entry<String,Pacman> entry:this.players.entrySet()) {
            String playerID=entry.getKey();
            Pacman pacman=entry.getValue();
            String s = "Score: " + pacman.getScore();
            g2d.drawString(s, 3+i * 75, map.getSCREEN_SIZE() + 16);
            g2d.drawString("id: "+playerID, 3+i * 75, map.getSCREEN_SIZE() + 30);
            i++;
        }
    }
}
