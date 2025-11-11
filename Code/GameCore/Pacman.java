package Code.GameCore;

import Code.Server.ServerRunning.*;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public abstract class Pacman {
    protected final int PAC_ANIM_DELAY = 2;      //动画延时
    protected final int PACMAN_ANIM_COUNT = 4;   //画面总数
    protected int SPEED;
    protected int x,y;                 //位置
    protected int dx,dy;              //移动方向
    protected int reqDx,reqDy;        //移动方向请求
    protected int viewDx,viewDy;     //视野方向
    protected int animPos=0;           //动画位置
    protected int animCount=PAC_ANIM_DELAY;        //动画帧数
    protected int animDir=1;       //动画播放方向
    protected int score,lives;      //分数与生命
    protected int color;            //颜色
    protected boolean powerUp;
    protected boolean dead=false;
    protected boolean aiControl=false;
    public abstract void loadImage();
    public abstract void drawPacmanUp(Graphics2D g2d);
    public abstract void drawPacmanDown(Graphics2D g2d);
    public abstract void drawPacmanLeft(Graphics2D g2d);
    public abstract void drawPacmanRight(Graphics2D g2d);
    public abstract void drawPacman(Graphics2D g2d);

    public void updateAnimation(){     //更新吃豆人动画
        this.animCount--;
        if(this.animCount<=0){
            animCount=PAC_ANIM_DELAY;
            animPos=animPos+animDir;
            if(animPos==(PACMAN_ANIM_COUNT-1)||animPos==0){
                animDir=-animDir;
            }
        }
    }

    public boolean isAiControl() {
        return aiControl;
    }

    public void setAiControl(boolean aiControl) {
        this.aiControl = aiControl;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    public int getColor() {
        return color;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore(){
        return score;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLives(){
        return lives;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }
    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getViewDx(){
        return viewDx;
    }

    public int getViewDy()
    {
        return viewDy;
    }

    public void setViewDx(int dx) {
        this.viewDx = dx;
    }

    public void setViewDy(int dy) {
        this.viewDy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setReqDx(int reqDx) {
        this.reqDx = reqDx;
    }
    public void setReqDy(int reqDy) {
        this.reqDy = reqDy;
    }

    private int getDirectionFlag(int dx, int dy) {
        if (dx == -1) return 0; // 左 (1 << 0 = 1)
        if (dy == -1) return 1; // 上 (1 << 1 = 2)
        if (dx == 1) return 2;  // 右 (1 << 2 = 4)
        if (dy == 1) return 3;  // 下 (1 << 3 = 8)
        return -1;
    }

    public void movePacman(Map map) {              //单机使用
        // 检查是否在网格交叉点
        if (x % map.getBLOCK_SIZE() == 0 && y % map.getBLOCK_SIZE() == 0) {
            // 检查是否可以改变方向
            if (!map.isWall(x, y, getDirectionFlag(reqDx, reqDy))) {
                dx = reqDx;
                dy = reqDy;
                viewDx = dx;
                viewDy = dy;
            }
            // 检查当前方向是否可以继续移动
            if (map.isWall(x, y, getDirectionFlag(dx, dy))) {
                dx = 0;
                dy = 0;
            }
            if(map.eatDot(x,y)){
                score++;
                GameAudioPlayer.playShortSound(GameAudioPlayer.eatDotSoundPath);
            }
            if(map.eatLemon(x,y)){
                score=score+5;
                GameAudioPlayer.playShortSound(GameAudioPlayer.eatFruitSoundPath);

            }
            if (map.eatCherry(x,y)){
                score=score+10;
                GameAudioPlayer.playShortSound(GameAudioPlayer.eatFruitSoundPath);
            }
            if (map.eatPeach(x,y)){
                score=score+15;
                GameAudioPlayer.playShortSound(GameAudioPlayer.eatFruitSoundPath);
            }
            if (map.eatPill(x,y)){    //药丸效果
                this.powerUp=true;
                GameAudioPlayer.playLongSound(GameAudioPlayer.PowerUpSoundPath, 3000);

                // 3秒后恢复默认BGM（确保音效播完再切歌）
                Timer timer = new Timer(3000, e -> {
                    this.powerUp=false;
                    if (!GameAudioPlayer.isBgmPlaying) {  // 避免重复播放
                        GameAudioPlayer.playDefaultBgm(GameAudioPlayer.defaultBgmPath);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }

        }
        //移动吃豆人
        this.x += dx * SPEED;
        this.y += dy * SPEED;
        //屏幕环绕以防bug
        if (x < 0) x = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
        if (x >= map.getSCREEN_SIZE()) x = 0;
        if (y < 0) y = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
        if (y >= map.getSCREEN_SIZE()) y = 0;
    }

    public void movePacman(Map map, Room room) {     //联机使用
        // 检查是否在网格交叉点
        if (x % map.getBLOCK_SIZE() == 0 && y % map.getBLOCK_SIZE() == 0) {
            // 检查是否可以改变方向
            if (!map.isWall(x, y, getDirectionFlag(reqDx, reqDy))) {
                dx = reqDx;
                dy = reqDy;
                viewDx = dx;
                viewDy = dy;
            }
            // 检查当前方向是否可以继续移动
            if (map.isWall(x, y, getDirectionFlag(dx, dy))) {
                dx = 0;
                dy = 0;
            }
            if(map.eatDot(x,y)){
                score++;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","dot");
                for(ClientHandler clientHandler:room.getPlayers()){
                 clientHandler.getOut().println(obj.toString());
                }
            }
            if(map.eatLemon(x,y)){
                score=score+5;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","fruit");
                for(ClientHandler clientHandler:room.getPlayers()){
                    clientHandler.getOut().println(obj.toString());
                }
            }
            if (map.eatCherry(x,y)){
                score=score+10;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","fruit");
                for(ClientHandler clientHandler:room.getPlayers()){
                    clientHandler.getOut().println(obj.toString());
                }
            }
            if (map.eatPeach(x,y)){
                score=score+15;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","fruit");
                for(ClientHandler clientHandler:room.getPlayers()){
                    clientHandler.getOut().println(obj.toString());
                }
            }
            if (map.eatPill(x,y)){    //待补充药丸效果

            }

        }
        //移动吃豆人
        this.x += dx * SPEED;
        this.y += dy * SPEED;
        //屏幕环绕以防bug
        if (x < 0) x = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
        if (x >= map.getSCREEN_SIZE()) x = 0;
        if (y < 0) y = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
        if (y >= map.getSCREEN_SIZE()) y = 0;
    }

    public void aiMove(Map map, Room room) {
        if(x% map.getBLOCK_SIZE()==0&&y%map.getBLOCK_SIZE()==0) {
            ArrayList<int[]> possibleDirections=new ArrayList<>();
            if(!map.isWall(x,y,0)&&dx!=1) possibleDirections.add(new int[]{-1,0});
            if(!map.isWall(x,y,1)&&dy!=1) possibleDirections.add(new int[]{0,-1});
            if(!map.isWall(x,y,2)&&dx!=-1) possibleDirections.add(new int[]{1,0});
            if(!map.isWall(x,y,3)&&dy!=-1) possibleDirections.add(new int[]{0,1});

            if(!possibleDirections.isEmpty()){
                int[]directions=possibleDirections.get((int)(Math.random()*possibleDirections.size()));
                dx=directions[0];viewDx=directions[0];
                dy=directions[1];viewDy=directions[1];
            }
            else{
                dx=-dx;dy=-dy;
            }
            if(map.eatDot(x,y)){
                score++;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","dot");
                for(ClientHandler clientHandler:room.getPlayers()){
                    clientHandler.getOut().println(obj.toString());
                }
            }
            if(map.eatLemon(x,y)){
                score=score+5;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","fruit");
                for(ClientHandler clientHandler:room.getPlayers()){
                    clientHandler.getOut().println(obj.toString());
                }
            }
            if (map.eatCherry(x,y)){
                score=score+10;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","fruit");
                for(ClientHandler clientHandler:room.getPlayers()){
                    clientHandler.getOut().println(obj.toString());
                }
            }
            if (map.eatPeach(x,y)){
                score=score+15;
                JSONObject obj = new JSONObject();
                obj.put("type","play_music");
                obj.put("function","fruit");
                for(ClientHandler clientHandler:room.getPlayers()){
                    clientHandler.getOut().println(obj.toString());
                }
            }
            if (map.eatPill(x,y)){    //药丸效果

            }


        }
        x+=dx*SPEED;
        y+=dy*SPEED;

        // 边界检查（防止越界）
        if (x < 0) x = 0;
        if (x >= map.getSCREEN_SIZE() - map.getBLOCK_SIZE()) x = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
        if (y < 0) y = 0;
        if (y >= map.getSCREEN_SIZE() - map.getBLOCK_SIZE()) y = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
    }
}
