package Code.GameCore;

import Code.Client.ClientRunning;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {

    private ClientRunning client;
    private GameEngine gameEngine;
    private Timer timer;

    public ClientRunning getClient(){
        return client;
    }

    public Timer getTimer() {
        return timer;
    }

    public GameEngine getGameEngine(){
        return gameEngine;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameEngine.update(this);
        repaint();
    }

    public Board(ClientRunning client) {
        this.client = client;
        addKeyListener(new TAdapter());
        setFocusable(true);              //接收键盘输入
        this.gameEngine = new GameEngine();
        this.timer = new Timer(40, this);
        this.timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        gameEngine.draw(g2d);
        if(!gameEngine.isInGame()){
            g2d.setColor(Color.WHITE);
            g2d.drawString("Press S to start", 150,200);

        }
    }

    class TAdapter extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
       switch (key) {
           case KeyEvent.VK_UP: Board.this.gameEngine.getPacman().setReqDx(0);Board.this.gameEngine.getPacman().setReqDy(-1); break;
           case KeyEvent.VK_LEFT:Board.this.gameEngine.getPacman().setReqDx(-1);Board.this.gameEngine.getPacman().setReqDy(0); break;
           case KeyEvent.VK_RIGHT:Board.this.gameEngine.getPacman().setReqDx(1);Board.this.gameEngine.getPacman().setReqDy(0); break;
           case KeyEvent.VK_DOWN:Board.this.gameEngine.getPacman().setReqDx(0);Board.this.gameEngine.getPacman().setReqDy(1); break;
           case KeyEvent.VK_SPACE:
               if(timer.isRunning()) {
                   timer.stop();
                   GameAudioPlayer.stopBgm();
               }
               else {
                   timer.start();
                   GameAudioPlayer.playDefaultBgm(GameAudioPlayer.defaultBgmPath);
               }
               break;
           case KeyEvent.VK_ESCAPE:
               if(timer.isRunning()){
                   timer.stop();
                   GameAudioPlayer.stopBgm();
               }

               int choice=JOptionPane.showOptionDialog(Board.this,"Are you sure you want to quit?",
                       "Inform", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,
                       null, new String[]{"yes","no"},null);
               if(choice==0) {
                   Board.this.gameEngine.setInGame(false);
                   client.cardLayout.show(client.cardPanel, "Main");
                   client.cardPanel.remove(Board.this);
                   client.cardPanel.revalidate();
                   client.cardPanel.repaint();
                   //GameAudioPlayer.playMenuBgm(GameAudioPlayer.menuBgmPath);
                   break;
               }
               else{
                  timer.start();
                  GameAudioPlayer.playDefaultBgm(GameAudioPlayer.defaultBgmPath);
                   break;
               }

           case KeyEvent.VK_S:Board.this.gameEngine.setInGame(true);
           GameAudioPlayer.playDefaultBgm(GameAudioPlayer.defaultBgmPath);
           break;
       }
    }
    }
}
