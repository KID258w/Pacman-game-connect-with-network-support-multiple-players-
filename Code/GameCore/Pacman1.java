package Code.GameCore;

import javax.swing.*;
import java.awt.*;

public class Pacman1 {

    private final int PAC_ANIM_DELAY = 2;      //动画延时
    private final int PACMAN_ANIM_COUNT = 4;   //画面总数
    private final int SPEED = 6;
    private int x,y;                 //位置
    private int dx,dy;              //移动方向
    private int reqDx,reqDy;        //移动方向请求
    private int viewDx,viewDy;     //视野方向
    private int animPos=0;           //动画位置
    private int animCount=PAC_ANIM_DELAY;        //动画帧数
    private int animDir=1;       //动画播放方向
    private int score,lives;      //分数与生命

    private Image pacman1;
    private Image pacman2up,pacman3up,pacman4up;
    private Image pacman2down,pacman3down,pacman4down;
    private Image pacman2left,pacman3left,pacman4left;
    private Image pacman2right,pacman3right,pacman4right;

    public Pacman1(int x, int y, int viewDx, int viewDy) {
      loadImage();
      this.dx=0;this.dy=0;
      this.x=x;this.y=y;
      this.viewDx=viewDx;this.viewDy=viewDy;
      this.dx=0;this.dy=0;
      this.reqDx=0;this.reqDy=0;
      this.score=0;this.lives=3;
    }

    private void loadImage() {

       pacman1= new ImageIcon("src/resources/images/pacman_yellow.png").getImage();
       pacman2up= new ImageIcon("src/resources/images/up1_yellow.png").getImage();
       pacman3up= new ImageIcon("src/resources/images/up2_yellow.png").getImage();
       pacman4up= new ImageIcon("src/resources/images/up3_yellow.png").getImage();
       pacman2down= new ImageIcon("src/resources/images/down1_yellow.png").getImage();
       pacman3down= new ImageIcon("src/resources/images/down2_yellow.png").getImage();
       pacman4down= new ImageIcon("src/resources/images/down3_yellow.png").getImage();
       pacman2left= new ImageIcon("src/resources/images/left1_yellow.png").getImage();
       pacman3left= new ImageIcon("src/resources/images/left2_yellow.png").getImage();
       pacman4left= new ImageIcon("src/resources/images/left3_yellow.png").getImage();
       pacman2right= new ImageIcon("src/resources/images/right1_yellow.png").getImage();
       pacman3right= new ImageIcon("src/resources/images/right2_yellow.png").getImage();
       pacman4right= new ImageIcon("src/resources/images/right3_yellow.png").getImage();

    }

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

   public void drawPacmanUp(Graphics2D g2d){
        switch (animPos){
            case 1:
                g2d.drawImage(pacman2up,x+1,y+1,null);break;
                case 2:
                    g2d.drawImage(pacman3up,x+1,y+1,null);break;
                    case 3:
                        g2d.drawImage(pacman4up,x+1,y+1,null);break;
                     default:
                         g2d.drawImage(pacman1,x+1,y+1,null);break;
        }
   }

   public void drawPacmanDown(Graphics2D g2d){
        switch (animPos){
            case 1:
                g2d.drawImage(pacman2down,x+1,y+1,null);break;
                case 2:
                    g2d.drawImage(pacman3down,x+1,y+1,null);break;
                    case 3:
                        g2d.drawImage(pacman4down,x+1,y+1,null);break;
                        default:
                            g2d.drawImage(pacman1,x+1,y+1,null);break;
        }
   }

   public void drawPacmanLeft(Graphics2D g2d){
        switch (animPos){
            case 1:
                g2d.drawImage(pacman2left,x+1,y+1,null);break;
                case 2:
                    g2d.drawImage(pacman3left,x+1,y+1,null);break;
                    case 3:
                        g2d.drawImage(pacman4left,x+1,y+1,null);break;
                        default:
                            g2d.drawImage(pacman1,x+1,y+1,null);break;
        }
   }

   public void drawPacmanRight(Graphics2D g2d){
        switch (animPos){
            case 1:
                 g2d.drawImage(pacman2right,x+1,y+1,null);break;
                 case 2:
                     g2d.drawImage(pacman3right,x+1,y+1,null);break;
                     case 3:
                         g2d.drawImage(pacman4right,x+1,y+1,null);break;
                         default:
                             g2d.drawImage(pacman1,x+1,y+1,null);break;
        }
   }

   public void drawPacman(Graphics2D g2d){      //绘制吃豆人动画

        if(viewDy==-1){
         drawPacmanUp(g2d);
        }
        else if(viewDy==1){
            drawPacmanDown(g2d);
        }
        else if(viewDx==-1){
            drawPacmanLeft(g2d);
        }
        else{
            drawPacmanRight(g2d);
        }
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

    public void movePacman(Map map) {
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
            }
            if(map.eatLemon(x,y)){
                score=score+5;
            }
            if (map.eatCherry(x,y)){
                score=score+10;
            }
            if (map.eatPeach(x,y)){
                score=score+15;
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

}
