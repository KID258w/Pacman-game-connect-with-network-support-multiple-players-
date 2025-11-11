package Code.GameCore;

import Code.Client.ClientRunning;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameEngine {
    private Map map;
    private Pacman pacman;
    private ArrayList<Ghost> ghosts;
    private final Image livesImage=new ImageIcon("src/resources/images/left2_yellow.png").getImage();
    protected static final Image[] ghostsImage=new Image[4];
    static {
        ghostsImage[0]=new ImageIcon("src/resources/images/blinky.png").getImage();
        ghostsImage[1]=new ImageIcon("src/resources/images/clyde.png").getImage();
        ghostsImage[2]=new ImageIcon("src/resources/images/inky.png").getImage();
        ghostsImage[3]=new ImageIcon("src/resources/images/pinky.png").getImage();
    }
    private boolean inGame=false;
    private boolean dying;
    private int level;


    public Pacman getPacman(){
        return pacman;
    }

    public GameEngine() {
        initGame();
    }

    public void initGame(){

        map=new Map();
        pacman=new YellowPacman(0,0,0,1,3);
        level=1;
        //Ghost用
        ghosts=new ArrayList<>();
        ghosts.add(new Ghost(6* map.getBLOCK_SIZE(),6*map.getBLOCK_SIZE(),level,ghostsImage[0]));
        ghosts.add(new Ghost(8* map.getBLOCK_SIZE(),6*map.getBLOCK_SIZE(),level,ghostsImage[1]));
        ghosts.add(new Ghost(6* map.getBLOCK_SIZE(),8*map.getBLOCK_SIZE(),level,ghostsImage[2]));
        ghosts.add(new Ghost(8* map.getBLOCK_SIZE(),8*map.getBLOCK_SIZE(),level,ghostsImage[3]));
        dying=false;
    }

    public void update(Board board){      //判断游戏是否继续
        if(!inGame) return;
        if(dying){
            GameAudioPlayer.playShortSound(GameAudioPlayer.deathSoundPath);
            if(this.pacman.getLives()==0){
              inGame=false;
              board.getTimer().stop();
              GameAudioPlayer.stopBgm();
                JOptionPane.showMessageDialog(board,"Your final score is "+pacman.getScore(),"GAME OVER",
                        JOptionPane.INFORMATION_MESSAGE);
                ClientRunning client=board.getClient();
                JSONObject obj=new JSONObject();       //发送游戏结束时的分数给服务器
                obj.put("type","update_score");
                obj.put("player_id",client.getPlayerName());
                obj.put("score",pacman.getScore());
                client.getClientNetworkManager().sendMessage(obj.toString());
                if(pacman.getScore()>=500){
                   JSONObject object=new JSONObject();
                    object.put("type","update_achievement");
                    object.put("player_id",client.getPlayerName());
                    object.put("which","achievement_2");
                    client.getClientNetworkManager().sendMessage(object.toString());
                }
                client.cardLayout.show(client.cardPanel,"Main");
                client.cardPanel.remove(board);
                client.cardPanel.revalidate();
                client.cardPanel.repaint();
               // GameAudioPlayer.playMenuBgm(GameAudioPlayer.menuBgmPath);
            }
            else {
                int newLives = pacman.getLives() - 1;
                this.pacman.setLives(newLives);
                resetLevel(false);
            }
            return;
        }
        pacman.updateAnimation();
        pacman.movePacman(map);
        for(Ghost ghost:ghosts){
            ghost.moveGhost(map);
            if(ghost.checkCollision(pacman.getX(), pacman.getY())){
                if(!pacman.isPowerUp()){
                    dying=true;
                    break;
                }

            }
        }

        if(map.isLevelCompleted()){    //胜利判断
            level++;
            for(Ghost ghost:ghosts){
                ghost.setSpeed(level);
            }
            pacman.setScore(pacman.getScore()+50);
            resetLevel(true);
        }

    }

    public void resetLevel(Boolean isFinished){
        if(isFinished){
            map=new Map();
        }
       pacman.setX(0);pacman.setY(0);
       pacman.setDx(0);pacman.setDy(0);
       pacman.setViewDx(1);pacman.setViewDy(0);
       //Ghost
        ghosts.get(0).setX(6* map.getBLOCK_SIZE());ghosts.get(0).setY(6* map.getBLOCK_SIZE());
        ghosts.get(0).setDx(1);ghosts.get(0).setDy(0);
        ghosts.get(1).setX(8* map.getBLOCK_SIZE());ghosts.get(1).setY(6* map.getBLOCK_SIZE());
        ghosts.get(1).setDx(1);ghosts.get(1).setDy(0);
        ghosts.get(2).setX(6* map.getBLOCK_SIZE());ghosts.get(2).setY(8* map.getBLOCK_SIZE());
        ghosts.get(2).setDx(1);ghosts.get(2).setDy(0);
        ghosts.get(3).setX(8* map.getBLOCK_SIZE());ghosts.get(3).setY(8* map.getBLOCK_SIZE());
        ghosts.get(3).setDx(1);ghosts.get(3).setDy(0);
        dying=false;
    }

    public void draw(Graphics2D g2d){
        map.drawMaze(g2d);
        pacman.drawPacman(g2d);
        //Ghost
        for(Ghost ghost:ghosts){
            ghost.draw(g2d);
        }
        Font smallFont = new Font("Helvetica", Font.BOLD, 14);
        g2d.setFont(smallFont);
        g2d.setColor(new Color(96, 128, 255));
        String s = "Score: " + pacman.getScore();
        g2d.drawString(s, map.getSCREEN_SIZE() / 2 + 96, map.getSCREEN_SIZE() + 16);

        for (int i = 0; i < pacman.getLives(); i++) {
            g2d.drawImage(livesImage, i * 28 + 8, map.getSCREEN_SIZE()+1, null);
        }
    }

    public boolean isInGame() {
        return inGame;
    }
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
}
