package Code.GUI;

import Code.Client.ClientRunning;
import Code.GameCore.GameAudioPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class AchievementGUI extends JPanel {
    private Image background=new ImageIcon("src/resources/images/mainMenuBackGroundDim.png").getImage();
    private Image achievement1=new ImageIcon("src/resources/images/achievement1.png").getImage();
    private Image achievement2=new ImageIcon("src/resources/images/achievement2.png").getImage();
    private ClientRunning client;
    private JSONObject achievementData=null;
    private JButton backButton=new JButton("Back to MainMenu");

    public void paintComponent(Graphics g){
     super.paintComponent(g);
     g.drawImage(background,0,0,null);
     g.drawImage(achievement1,40,130,null);
     g.drawImage(achievement2,40,280,null);
     if(this.achievementData!=null){
         Font smallFont = new Font("Helvetica", Font.BOLD, 50);
         g.setFont(smallFont);
         g.setColor(Color.WHITE);
         g.drawString("Achievement",125,70);
         //
         Font font = new Font("Helvetica", Font.BOLD, 20);
         g.setFont(font);
         g.drawString("Party Game!",200,150);g.drawString("Finish a multiple game.",150,180);
         if(this.achievementData.getBoolean("achievement_1")){
             g.drawString("unlock",210,210);
         }
         else{
             g.setColor(Color.RED);
             g.drawString("lock",210,210);
         }
         g.setColor(Color.WHITE);
         g.drawString("Eat eat eat...",200,300);g.drawString("Get 500 in a single mode.",150,330);
         if(this.achievementData.getBoolean("achievement_2")){
             g.drawString("unlock",210,360);
         }
         else{
             g.setColor(Color.RED);
             g.drawString("lock",210,360);
         }
         //
        // JSONArray achievementList=this.achievementData.getJSONArray("achievementData");
        // for(int i=0;i<achievementList.length();i++){
             //String playerID=rankList.getJSONObject(i).getString("player_id");
             //int score=rankList.getJSONObject(i).getInt("highest_score");
            // boolean result=achievementList.getJSONObject(i).getBoolean("result");

            // g.drawString(playerID,120,90+80*(i+1));
            // g.drawString(Integer.toString(score),360,90+80*(i+1));
       //  }
     }
    }

    public AchievementGUI(ClientRunning client, JSONObject achievementData) {
         setLayout(null);
         this.client=client;
         this.achievementData=achievementData;

        backButton.setBounds(40, 530, 150, 30);
        add(backButton);
        repaint();

        this.backButton.addActionListener(e -> {
            this.client.cardLayout.show(this.client.cardPanel, "Main");
            this.client.cardPanel.remove(this);
            this.client.cardPanel.revalidate();
            this.client.cardPanel.repaint();
        });

    }
}
