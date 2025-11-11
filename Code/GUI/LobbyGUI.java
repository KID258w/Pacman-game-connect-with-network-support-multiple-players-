package Code.GUI;

import Code.Client.ClientRunning;
import Code.GameCore.GameAudioPlayer;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;

public class LobbyGUI extends JPanel {

    private ClientRunning client;
    private final Image background=new ImageIcon("src/resources/images/multiplayermenu.png").getImage();
    private JLabel roomId;
    private JTextField roomIdField;
    private JButton createRoomBtn;
    private JButton joinRoomBtn;
    private JButton leaveRoomBtn;
    private JButton startGameBtn;
    private JButton backBtn;
    private JButton yellowBtn;
    private JButton redBtn;
    private JButton blueBtn;
    private JButton greenBtn;

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(background, 0,0, null);
    }

    public LobbyGUI(ClientRunning client) {
        // 创建UI元素
        setLayout(null);
        this.client = client;
        this.createRoomBtn = new JButton("Create Room");
        this.joinRoomBtn = new JButton("Join Room");
        this.leaveRoomBtn = new JButton("Leave Room");
        this.startGameBtn = new JButton("Start Game");
        this.roomId = new JLabel("Room ID:");roomId.setForeground(Color.WHITE);
        this.roomIdField = new JTextField(3);
        this.backBtn = new JButton("Back to MainMenu");
        this.yellowBtn = new JButton("Play Yellow");
        this.redBtn = new JButton("Play Red");
        this.blueBtn = new JButton("Play Blue");
        this.greenBtn = new JButton("Play Green");


        createRoomBtn.setBounds(80, 325, 150, 30);
        startGameBtn.setBounds(80, 375, 150, 30);
        joinRoomBtn.setBounds(370, 325, 150, 30);
        leaveRoomBtn.setBounds(370, 375, 150, 30);
        roomId.setBounds(190,125,100,20);
        roomIdField.setBounds(250, 125, 100, 20);
        backBtn.setBounds(40, 530, 150, 30);
        yellowBtn.setBounds(20, 275, 120, 30);
        redBtn.setBounds(160, 275, 120, 30);
        blueBtn.setBounds(320, 275, 120, 30);
        greenBtn.setBounds(450, 275, 120, 30);

        add(createRoomBtn);
        add(joinRoomBtn);
        add(leaveRoomBtn);
        add(roomId);
        add(roomIdField);
        add(createRoomBtn);
        add(startGameBtn);
        add(backBtn);
        add(yellowBtn);
        add(redBtn);
        add(blueBtn);
        add(greenBtn);

        createRoomBtn.addActionListener(e -> {
            try {
                if(this.client.getCurrentRoom()==null) {
                    int roomIdTest = Integer.parseInt(roomIdField.getText());
                    String roomID = String.valueOf(roomIdTest);
                    this.roomIdField.setText("");
                    JSONObject obj = new JSONObject();
                    obj.put("type", "create_room");
                    obj.put("room_id", roomID);
                    this.client.getClientNetworkManager().sendMessage(obj.toString());
                }
                else{
                    JOptionPane.showMessageDialog(this,"You are already in a room.",
                            "info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Please enter a valid room ID", "Error"
                        , JOptionPane.ERROR_MESSAGE);
            }

        });

        joinRoomBtn.addActionListener(e -> {
            try {
                if(this.client.getCurrentRoom()==null) {
                    int roomIdTest = Integer.parseInt(roomIdField.getText());
                    String roomID = String.valueOf(roomIdTest);
                    this.roomIdField.setText("");
                    JSONObject obj = new JSONObject();
                    obj.put("type", "join_room");
                    obj.put("room_id", roomID);
                    this.client.getClientNetworkManager().sendMessage(obj.toString());
                }
                else{
                    JOptionPane.showMessageDialog(this,"You are already in a room.",
                            "info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Please enter a valid room ID", "Error"
                        , JOptionPane.ERROR_MESSAGE);
            }

        });


        startGameBtn.addActionListener(e -> {
           if(this.client.getCurrentRoom()==null) {
               JOptionPane.showMessageDialog(this,"You are not in a room","info",
                       JOptionPane.INFORMATION_MESSAGE);
           }

           else{
               JSONObject obj = new JSONObject();
               obj.put("type", "start_game");
               obj.put("room_id", this.client.getCurrentRoom());
               this.client.getClientNetworkManager().sendMessage(obj.toString());
           }

        });

         leaveRoomBtn.addActionListener(e -> {
             if(this.client.getCurrentRoom()==null) {
                 JOptionPane.showMessageDialog(this,"You are not in a room","info",
                         JOptionPane.INFORMATION_MESSAGE);
             }
            else{
                JSONObject obj = new JSONObject();
                obj.put("type", "leave_room");
                obj.put("room_id", this.client.getCurrentRoom());
                this.client.getClientNetworkManager().sendMessage(obj.toString());
             }
         });

         yellowBtn.addActionListener(e -> {
           JSONObject obj = new JSONObject();
           obj.put("type", "change_color");
           obj.put("color", "yellow");
           this.client.getClientNetworkManager().sendMessage(obj.toString());
         });

         redBtn.addActionListener(e -> {
             JSONObject obj = new JSONObject();
             obj.put("type", "change_color");
             obj.put("color", "red");
             this.client.getClientNetworkManager().sendMessage(obj.toString());
         });

         blueBtn.addActionListener(e -> {
             JSONObject obj = new JSONObject();
             obj.put("type", "change_color");
             obj.put("color", "blue");
             this.client.getClientNetworkManager().sendMessage(obj.toString());
         });

         greenBtn.addActionListener(e -> {
             JSONObject obj = new JSONObject();
             obj.put("type", "change_color");
             obj.put("color", "green");
             this.client.getClientNetworkManager().sendMessage(obj.toString());
         });

        backBtn.addActionListener(e -> {
            if(this.client.getCurrentRoom()!=null) {
                JOptionPane.showMessageDialog(this.client,"You are in a room now.Please leave room first.",
                        "info", JOptionPane.INFORMATION_MESSAGE);
            }
           else{
               // GameAudioPlayer.playMenuBgm(GameAudioPlayer.menuBgmPath);
               this.client.cardLayout.show(this.client.cardPanel,"Main");
           }
        });
    }
}
