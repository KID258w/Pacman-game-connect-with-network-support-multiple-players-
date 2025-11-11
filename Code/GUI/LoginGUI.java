package Code.GUI;

import Code.Client.ClientRunning;
import org.json.JSONObject;


import javax.swing.*;
import java.awt.*;
public class LoginGUI extends JPanel{

    private ClientRunning client;
    private Image logo=new ImageIcon("src/resources/images/AccountLogo.png").getImage();
    public JButton loginButton;
    public JButton toRegisterButton;
    public JTextField userText;
    public JPasswordField passwordText;

    public LoginGUI(ClientRunning client){
        this.client = client;

        JPanel northPanel=new JPanel(){
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                if(logo!=null){
                    g.drawImage(logo,0,0,null);
                }
            }
        };
        northPanel.setPreferredSize(new Dimension(650,350));
        add(northPanel,BorderLayout.NORTH);

        JPanel centerPanel=new JPanel();
        BoxLayout boxLayout=new BoxLayout(centerPanel,BoxLayout.Y_AXIS);
        centerPanel.setLayout(boxLayout);

        JLabel instruction1=new JLabel("Welcome to SuperPacman!");
        JLabel instruction2=new JLabel("Enter your username and password to login.");
        JPanel panel1=new JPanel(new FlowLayout());
        JLabel userLabel=new JLabel("Username:");
        this.userText=new JTextField(10);
        panel1.add(userLabel);panel1.add(userText);

        JPanel panel2=new JPanel(new FlowLayout());
        JLabel passwordLabel=new JLabel("Password:");
        this.passwordText=new JPasswordField(10);
        panel2.add(passwordLabel);panel2.add(passwordText);

        JPanel panel3=new JPanel(new FlowLayout());
        this.loginButton=new JButton("Login");
        this.toRegisterButton=new JButton("to Register");
        panel3.add(loginButton);panel3.add(toRegisterButton);

        centerPanel.add(instruction1);
        centerPanel.add(instruction2);
        centerPanel.add(panel1);centerPanel.add(panel2);centerPanel.add(panel3);
        add(centerPanel,BorderLayout.CENTER);

        instruction1.setAlignmentX(Component.CENTER_ALIGNMENT);
        instruction2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.loginButton.addActionListener(e -> {
            String playerID=this.userText.getText();
            String password=new String(this.passwordText.getPassword());
            this.userText.setText("");this.passwordText.setText("");
            JSONObject obj=new JSONObject();
            obj.put("type","login");
            obj.put("player_id",playerID);
            obj.put("password",password);
            this.client.getClientNetworkManager().sendMessage(obj.toString());

        });    //添加登录界面按钮的事件处理
        this.toRegisterButton.addActionListener(e -> {
            this.client.cardLayout.show(this.client.cardPanel,"Register");
        });
    }
}
