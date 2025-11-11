package Code.GUI;

import Code.Client.ClientRunning;
import Code.GameCore.Board;
import Code.GameCore.GameAudioPlayer;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JPanel{

    private ClientRunning client;
    private Image background=new ImageIcon("src/resources/images/mainMenuBackGround.png").getImage();
    public JButton startSingleModeButton;
    public JButton exitButton;
    public JButton startMultiModeButton;
    public JButton rankSystemButton;
    public JButton achievementSystemButton;
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(background, 0, 0, null);
    }

    public MainGUI(ClientRunning client){

        this.client = client;
        this.startSingleModeButton=new JButton("Single Mode");
        this.exitButton=new JButton("Exit");
        this.startMultiModeButton=new JButton("Multi Mode");
        this.rankSystemButton=new JButton("Rank System");
        this.achievementSystemButton=new JButton("Achievement System");

        this.achievementSystemButton.setBackground(Color.RED);
        this.startSingleModeButton.setBackground(Color.RED);
        this.startMultiModeButton.setBackground(Color.RED);
        this.exitButton.setBackground(Color.RED);
        this.rankSystemButton.setBackground(Color.RED);

        this.startSingleModeButton.setOpaque(true);
        this.startMultiModeButton.setOpaque(true);
        this.exitButton.setOpaque(true);
        this.achievementSystemButton.setOpaque(true);
        this.rankSystemButton.setOpaque(true);

        this.startSingleModeButton.setForeground(Color.WHITE);
        this.startMultiModeButton.setForeground(Color.WHITE);
        this.exitButton.setForeground(Color.WHITE);
        this.achievementSystemButton.setForeground(Color.WHITE);
        this.rankSystemButton.setForeground(Color.WHITE);

        this.startSingleModeButton.setBorderPainted(false);
        this.startMultiModeButton.setBorderPainted(false);
        this.exitButton.setBorderPainted(false);
        this.achievementSystemButton.setBorderPainted(false);
        this.rankSystemButton.setBorderPainted(false);



        JPanel panel=new JPanel();panel.setOpaque(false);
        BoxLayout boxLayout=new BoxLayout(panel,BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.add(startSingleModeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(startMultiModeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(rankSystemButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(achievementSystemButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);

        startSingleModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startMultiModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rankSystemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        achievementSystemButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(panel,BorderLayout.CENTER);

        startSingleModeButton.addActionListener(e -> {
            GameAudioPlayer.stopBgm();
            SwingUtilities.invokeLater(() -> {
                LoadingImage.showLogo();
                GameAudioPlayer.playShortSound(GameAudioPlayer.gameStartBgmPath);
                Timer timer = new Timer(3000, ex ->{
                    this.client.setBoard(new Board(this.client));
                    this.client.cardPanel.add(this.client.getBoard(),"PlaySingle");
                    this.client.cardLayout.show(this.client.cardPanel,"PlaySingle");
                    this.client.getBoard().requestFocusInWindow();
                });
                timer.start();
                timer.setRepeats(false);
            });
        });

        startMultiModeButton.addActionListener(e -> {
           SwingUtilities.invokeLater(() -> {
               GameAudioPlayer.stopBgm();
             this.client.cardLayout.show(this.client.cardPanel,"PlayMultiple");
           });
        });

        rankSystemButton.addActionListener(e -> {
            //
            //SwingUtilities.invokeLater(() -> {
                GameAudioPlayer.stopBgm();
                JSONObject obj = new JSONObject();
                obj.put("type", "rank_request");
                this.client.getClientNetworkManager().sendMessage(obj.toString());
                // JPanel RankGUI=new RankGUI(this.client,new JSONObject());
                // this.client.cardPanel.add(RankGUI,"Rank System");
                // this.client.cardLayout.show(this.client.cardPanel,"Rank System");
            GameAudioPlayer.playMenuBgm(GameAudioPlayer.topScoresBgmName);
            //});
        });

        achievementSystemButton.addActionListener(e -> {
            GameAudioPlayer.stopBgm();
            JSONObject obj = new JSONObject();
            obj.put("type", "achievement_request");
            this.client.getClientNetworkManager().sendMessage(obj.toString());
          // SwingUtilities.invokeLater(() -> {
            //  JPanel AchievementGUI=new AchievementGUI(this.client,new JSONObject());
             // this.client.cardPanel.add(AchievementGUI,"Achievement System");
              //this.client.cardLayout.show(this.client.cardPanel,"Achievement System");
          // });
            //JSONObject obj = new JSONObject();
            //obj.put("type", "achievement_request");
            //this.client.getClientNetworkManager().sendMessage(obj.toString());
        });



        exitButton.addActionListener(e -> {
            JSONObject obj = new JSONObject();
            obj.put("type", "logout");
            obj.put("player_id",this.client.getPlayerName());
            this.client.getClientNetworkManager().sendMessage(obj.toString());
            this.client.setPlayerName(null);
            //
            System.out.println("logout: " + this.client.getPlayerName());
             //
            this.client.cardLayout.show(this.client.cardPanel,"Login");
            GameAudioPlayer.stopBgm();
        });

    }




}
