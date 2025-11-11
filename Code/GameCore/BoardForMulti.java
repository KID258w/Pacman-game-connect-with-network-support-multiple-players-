package Code.GameCore;

import Code.Client.ClientRunning;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BoardForMulti extends JPanel {
    private ClientRunning client;
    private MultipleGameEngine multipleGameEngine;
    private JButton aiControl;
    private JButton cancelControl;
    private TAdapter TAdapter;

    public MultipleGameEngine getMultipleGameEngine() {
        return multipleGameEngine;
    }

    public BoardForMulti(ClientRunning client, JSONArray players) {
        setLayout(null);
        this.client = client;
        this.multipleGameEngine = new MultipleGameEngine(players);
        this.aiControl = new JButton("AI Control");
        this.cancelControl = new JButton("Cancel AI Control");
        this.aiControl.setBackground(Color.RED);this.cancelControl.setBackground(Color.RED);
        this.aiControl.setOpaque(true);this.cancelControl.setOpaque(true);
        this.aiControl.setBounds(400,100,100,30);
        this.cancelControl.setBounds(400,200,150,30);

        add(this.aiControl);add(this.cancelControl);

        this.TAdapter = new TAdapter() {};
        addKeyListener(this.TAdapter);
        setFocusable(true);

        this.aiControl.addActionListener(e -> {
            JSONObject obj = new JSONObject();
           obj.put("type", "control");
            obj.put("room_id",this.client.getCurrentRoom());
            obj.put("player_id",this.client.getPlayerName());
            obj.put("who", "ai");
            this.client.getClientNetworkManager().sendMessage(obj.toString());
            this.removeKeyListener(this.TAdapter);
        });

        this.cancelControl.addActionListener(e -> {
            JSONObject obj = new JSONObject();
            obj.put("type", "control");
            obj.put("room_id",this.client.getCurrentRoom());
            obj.put("player_id",this.client.getPlayerName());
            obj.put("who", "human");
            this.client.getClientNetworkManager().sendMessage(obj.toString());
            this.addKeyListener(this.TAdapter);
            setFocusable(true);
            requestFocusInWindow();
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.multipleGameEngine.draw(g2d);
    }

    class TAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent e) {
            int key= e.getKeyCode();
            switch (key){
                case KeyEvent.VK_UP: {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "command");
                    obj.put("room_id", BoardForMulti.this.client.getCurrentRoom());
                    obj.put("player_id",BoardForMulti.this.client.getPlayerName());
                    obj.put("direction","up");
                    BoardForMulti.this.client.getClientNetworkManager().sendMessage(obj.toString());
                    break;
                }
                case KeyEvent.VK_LEFT:{
                    JSONObject obj = new JSONObject();
                    obj.put("type", "command");
                    obj.put("room_id", BoardForMulti.this.client.getCurrentRoom());
                    obj.put("player_id",BoardForMulti.this.client.getPlayerName());
                    obj.put("direction","left");
                    BoardForMulti.this.client.getClientNetworkManager().sendMessage(obj.toString());
                    break;
                }
                case KeyEvent.VK_RIGHT:
                {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "command");
                    obj.put("room_id", BoardForMulti.this.client.getCurrentRoom());
                    obj.put("player_id",BoardForMulti.this.client.getPlayerName());
                    obj.put("direction","right");
                    BoardForMulti.this.client.getClientNetworkManager().sendMessage(obj.toString());
                    break;
                }
                    case KeyEvent.VK_DOWN:{
                        JSONObject obj = new JSONObject();
                        obj.put("type", "command");
                        obj.put("room_id", BoardForMulti.this.client.getCurrentRoom());
                        obj.put("player_id",BoardForMulti.this.client.getPlayerName());
                        obj.put("direction","down");
                        BoardForMulti.this.client.getClientNetworkManager().sendMessage(obj.toString());
                        break;
                    }
            }


        }
    }

}
