package Code.GameCore;

import java.awt.*;
import java.util.ArrayList;

public class Ghost {

    private int x,y;
    private int dx,dy;
    private int speed;
    private Image Ghostimage;

    private static final int[]VALID_SPEEDS={3,4,6,8,12};   //为方格的因数

    public Ghost(int x, int y, int speedLevel,Image GhostImage) {
        this.x = x;
        this.y = y;
        if(speedLevel<=5){
        this.speed = VALID_SPEEDS[speedLevel-1];
        }
        else{
            this.speed = 12;
        }
        this.Ghostimage = GhostImage;
        this.dx = 1;
        this.dy = 0;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
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

    public void setSpeed(int speedLevel) {
        this.speed = VALID_SPEEDS[speedLevel-1];

    }
    public void moveGhost(Map map) {
      if(x% map.getBLOCK_SIZE()==0&&y%map.getBLOCK_SIZE()==0) {
          ArrayList<int[]> possibleDirections=new ArrayList<>();
          if(!map.isWall(x,y,0)&&dx!=1) possibleDirections.add(new int[]{-1,0});
          if(!map.isWall(x,y,1)&&dy!=1) possibleDirections.add(new int[]{0,-1});
          if(!map.isWall(x,y,2)&&dx!=-1) possibleDirections.add(new int[]{1,0});
          if(!map.isWall(x,y,3)&&dy!=-1) possibleDirections.add(new int[]{0,1});

          if(!possibleDirections.isEmpty()){
              int[]directions=possibleDirections.get((int)(Math.random()*possibleDirections.size()));
              dx=directions[0];dy=directions[1];
          }
          else{
              dx=-dx;dy=-dy;
          }
      }

       x+=dx*speed;
       y+=dy*speed;

        // 边界检查（防止越界）
        if (x < 0) x = 0;
        if (x >= map.getSCREEN_SIZE() - map.getBLOCK_SIZE()) x = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
        if (y < 0) y = 0;
        if (y >= map.getSCREEN_SIZE() - map.getBLOCK_SIZE()) y = map.getSCREEN_SIZE() - map.getBLOCK_SIZE();
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(Ghostimage,x+1,y+1,null);
    }

    public boolean checkCollision(int px,int py) {     //检查吃豆人与幽灵的碰撞
        return Math.abs(px-x)<18&&Math.abs(py-y)<18;
    }

}
