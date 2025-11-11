package Code.Client;
import Code.GUI.LobbyGUI;
import Code.GUI.LoginGUI;
import Code.GUI.MainGUI;
import Code.GUI.RegisterGUI;
import Code.GameCore.Board;
import Code.GameCore.BoardForMulti;
import Code.Object.Account;

import javax.swing.*;
import java.awt.*;
public class ClientRunning extends JFrame {

    private final int PORT = 4869;
    private final String HOST = "10.61.16.93";
    private ClientNetworkManager clientNetworkManager;
    public CardLayout cardLayout;
    public JPanel cardPanel;
    private Image logo=new ImageIcon("src/resources/images/gamelogo.jpeg").getImage();
    private Board board;
    private String playerName;
    private String currentRoom=null;
    public BoardForMulti boardForMulti;

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public String getPlayerName(){
        return playerName;
    }

    public void setPlayerName(String playerName){
        this.playerName = playerName;
    }

    public ClientNetworkManager getClientNetworkManager() {
        return clientNetworkManager;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }



  public ClientRunning() {
      setTitle("SuperPacman");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setIconImage(logo);
      setSize(600,650);
      setLocationRelativeTo(null);

      cardLayout=new CardLayout();
      cardPanel = new JPanel(cardLayout);
          //绘制客户端的GUI界面
      JPanel loginPanel=new LoginGUI(this);         //登录GUI
      cardPanel.add(loginPanel,"Login");
      JPanel registerPanel=new RegisterGUI(this);    //注册GUI
      cardPanel.add(registerPanel,"Register");
      JPanel mainPanel=new MainGUI(this);           //主界面GUI
      cardPanel.add(mainPanel,"Main");
      JPanel multiPanel=new LobbyGUI(this);         //多人大厅GUI
      cardPanel.add(multiPanel,"PlayMultiple");

      add(cardPanel);
      cardLayout.show(cardPanel,"Login");
      setVisible(true);
      clientNetworkManager=new ClientNetworkManager(this,HOST, PORT);
  }

  public static void main(String[] args) {
          new ClientRunning();
  }
}
