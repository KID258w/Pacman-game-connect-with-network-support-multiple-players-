package Code.GUI;
import Code.Client.ClientRunning;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
public class RegisterGUI extends JPanel{

    private ClientRunning client;
    private Image logo=new ImageIcon("src/resources/images/AccountLogo.png").getImage();
    public JButton toLoginButton;
    public JButton registerButton;
    public JTextField userText;
    public JPasswordField passwordText;
    public JPasswordField confirmPasswordText;

    public RegisterGUI(ClientRunning client){
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
        JLabel instruction2=new JLabel("Create a account to login.");
        JPanel panel1=new JPanel(new FlowLayout());
        JLabel userLabel=new JLabel("Username:");
        this.userText=new JTextField(10);
        panel1.add(userLabel);panel1.add(userText);

        JPanel panel2=new JPanel(new FlowLayout());
        JLabel passwordLabel=new JLabel("Password:");
        this.passwordText=new JPasswordField(10);
        panel2.add(passwordLabel);panel2.add(passwordText);

        JPanel panel3=new JPanel(new FlowLayout());
        JLabel confirmPasswordLabel=new JLabel("Confirm Password:");
        this.confirmPasswordText=new JPasswordField(10);
        panel3.add(confirmPasswordLabel);panel3.add(confirmPasswordText);

        JPanel panel4=new JPanel(new FlowLayout());
        this.registerButton=new JButton("Register");
        this.toLoginButton=new JButton("to Login");
        panel4.add(registerButton);panel4.add(toLoginButton);

        centerPanel.add(instruction1);
        centerPanel.add(instruction2);
        centerPanel.add(panel1);centerPanel.add(panel2);
        centerPanel.add(panel3);centerPanel.add(panel4);
        add(centerPanel,BorderLayout.CENTER);

        instruction1.setAlignmentX(Component.CENTER_ALIGNMENT);
        instruction2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.registerButton.addActionListener(e -> {
            String playerID=this.userText.getText();
            String password=new String(this.passwordText.getPassword());
            String confirmPassword=new String(this.confirmPasswordText.getPassword());
            this.userText.setText("");this.passwordText.setText("");
            this.confirmPasswordText.setText("");
            if(!password.equals(confirmPassword)){
                JOptionPane.showMessageDialog(this,"Passwords do not match","Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            else{
                JSONObject obj=new JSONObject();
                obj.put("type","register");
                obj.put("player_id",playerID);
                obj.put("password",password);
                this.client.getClientNetworkManager().sendMessage(obj.toString());
            }


        });

        this.toLoginButton.addActionListener(e -> {
           this.client.cardLayout.show(this.client.cardPanel,"Login");
        });


    }
}
