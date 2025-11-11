package Code.GUI;

import Code.Client.ClientRunning;
import Code.GameCore.Board;
import Code.GameCore.GameAudioPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class RankGUI extends JPanel {
    private ClientRunning client;
    private Image background=new ImageIcon("src/resources/images/topscores.png").getImage();
    private JSONObject rankDate=null;
    private JButton backButton=new JButton("Back to MainMenu");


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, null);
        if(rankDate!=null){
            Font smallFont = new Font("Helvetica", Font.BOLD, 50);
            g.setFont(smallFont);
            g.setColor(Color.WHITE);
            JSONArray rankList=this.rankDate.getJSONArray("rankData");
            for(int i=0;i<rankList.length();i++){
             String playerID=rankList.getJSONObject(i).getString("player_id");
             int score=rankList.getJSONObject(i).getInt("highest_score");
             g.drawString(playerID,120,90+80*(i+1));
             g.drawString(Integer.toString(score),360,90+80*(i+1));
            }
        }
    }

    public RankGUI(ClientRunning client, JSONObject rankDate) {
        setLayout(null);
        this.client = client;
        /*
        JSONObject obj=new JSONObject();
        JSONArray arr=new JSONArray();
        for(int i=0;i<5;i++){
            JSONObject object=new JSONObject();
            object.put("player_id","testName");
            object.put("highest_score",100);
            arr.put(object);
        }
        obj.put("rankData",arr);
       */ this.rankDate=rankDate;
        //

       // this.rankDate = rankDate;

        backButton.setBounds(40, 530, 150, 30);
        add(backButton);
        repaint();

        backButton.addActionListener(e -> {
            GameAudioPlayer.stopBgm();
            this.client.cardLayout.show(this.client.cardPanel, "Main");
            this.client.cardPanel.remove(this);
            this.client.cardPanel.revalidate();
            this.client.cardPanel.repaint();
           // GameAudioPlayer.playMenuBgm(GameAudioPlayer.menuBgmPath);
        });

    }


}
