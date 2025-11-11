package Code.GameCore;

import javax.swing.*;
import java.awt.*;

public class GreenPacman extends Pacman {

    private Image pacman1;
    private Image pacman2up,pacman3up,pacman4up;
    private Image pacman2down,pacman3down,pacman4down;
    private Image pacman2left,pacman3left,pacman4left;
    private Image pacman2right,pacman3right,pacman4right;

    public GreenPacman(int x, int y,int viewDx,int viewDy,int lives){
        loadImage();
        this.dx=0;this.dy=0;
        this.x=x;this.y=y;
        this.viewDx=viewDx;this.viewDy=viewDy;
        this.dx=0;this.dy=0;
        this.reqDx=0;this.reqDy=0;
        this.score=0;
        this.lives=lives;
        this.color=4;
        this.SPEED=6;
        this.powerUp=false;
    }

    @Override
    public void loadImage() {
        pacman1= new ImageIcon("src/resources/images/pacman_green.png").getImage();
        pacman2up= new ImageIcon("src/resources/images/up1_green.png").getImage();
        pacman3up= new ImageIcon("src/resources/images/up2_green.png").getImage();
        pacman4up= new ImageIcon("src/resources/images/up3_green.png").getImage();
        pacman2down= new ImageIcon("src/resources/images/down1_green.png").getImage();
        pacman3down= new ImageIcon("src/resources/images/down2_green.png").getImage();
        pacman4down= new ImageIcon("src/resources/images/down3_green.png").getImage();
        pacman2left= new ImageIcon("src/resources/images/left1_green.png").getImage();
        pacman3left= new ImageIcon("src/resources/images/left2_green.png").getImage();
        pacman4left= new ImageIcon("src/resources/images/left3_green.png").getImage();
        pacman2right= new ImageIcon("src/resources/images/right1_green.png").getImage();
        pacman3right= new ImageIcon("src/resources/images/right2_green.png").getImage();
        pacman4right= new ImageIcon("src/resources/images/right3_green.png").getImage();
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

}
