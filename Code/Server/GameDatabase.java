package Code.Server;
import Code.Object.Account;
import java.sql.*;
import Code.Server.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameDatabase {
    private static final String DB_URL="jdbc:sqlite:game_accounts.db";
    private Connection connection;
    private ServerRunning server;

    public GameDatabase(ServerRunning server) {
        this.server = server;
        try{
            String createTable = "CREATE TABLE IF NOT EXISTS game_accounts ("+
                    "player_id TEXT PRIMARY KEY,"+
                    "password TEXT NOT NULL,"+
                    "highest_score INTEGER DEFAULT 0,"+
                    "item_count INTEGER DEFAULT 10,"+
                    "achievement_1 BOOLEAN DEFAULT FALSE,"+
                    "achievement_2 BOOLEAN DEFAULT FALSE,"+
                    "achievement_3 BOOLEAN DEFAULT FALSE)";
            this.connection = DriverManager.getConnection(DB_URL);
            Statement stmt = connection.createStatement();
            stmt.execute(createTable);
            stmt.close();
            System.out.println("Database is created or connected");
        } catch (SQLException e) {
            System.out.println("Database can not created");
            throw new RuntimeException(e);
        }
    }

    public void loadAccounts() {
        this.server.getAccounts().clear();   //清空服务器目前的账号列表
        String query = "SELECT * FROM game_accounts";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Account a = new Account(rs.getString("player_id"), rs.getString("password"));
                a.setHighestScore(rs.getInt("highest_score"));
                a.setPowerStone(rs.getInt("item_count"));
                a.setAchievement1(rs.getBoolean("achievement_1"));
                a.setAchievement2(rs.getBoolean("achievement_2"));
                a.setAchievement3(rs.getBoolean("achievement_3"));
                a.setOnLine(false);
                this.server.getAccounts().put(a.getPlayerID(), a);
            }
            stmt.close();rs.close();
            System.out.println("accounts is loaded");
        }
        catch (SQLException e) {
            System.out.println("Failed to load accounts");
            e.printStackTrace();
        }

    }

    public boolean createAccount(String playerID, String password) {
        if (this.server.getAccounts().containsKey(playerID)) return false;
        try{
           PreparedStatement pstmt=connection.prepareStatement("INSERT INTO game_accounts(player_id,password) VALUES (?,?)");
           pstmt.setString(1,playerID);
           pstmt.setString(2,password);
           int result=pstmt.executeUpdate();
           if(result>0) {
               Account a = new Account(playerID, password);
               a.setHighestScore(0);
               a.setPowerStone(10);
               a.setAchievement1(false);
               a.setAchievement2(false);
               a.setAchievement3(false);
               this.server.getAccounts().put(playerID, a);
           }
           pstmt.close();
           return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean accountLogin(ServerRunning.ClientHandler client, String playerID, String password){
        if (!this.server.getAccounts().containsKey(playerID)) return false;
        if(this.server.getAccounts().get(playerID).getPassword().equals(password)) {
            if(this.server.getAccounts().get(playerID).isOnLine()) return false;
            this.server.getAccounts().get(playerID).setOnLine(true);
            client.setPlayerID(playerID);
            //
            System.out.println("Account: "+client.getPlayerID()+" logged in "+
                    "Statement: "+this.server.getAccounts().get(playerID).isOnLine());
            //
            return true;
        }
        return false;
    }

    public void accountLogout(ServerRunning.ClientHandler client, String playerID){
        this.server.getAccounts().get(playerID).setOnLine(false);
        client.setPlayerID(null);
        //
        System.out.println("Current client: "+client.getPlayerID());
        System.out.println("Account: "+this.server.getAccounts().get(playerID).getPlayerID()+" logged out "+
                "Statement: "+this.server.getAccounts().get(playerID).isOnLine());
        //
    }

    public void updateHighestScore(String playerID, int score){
        String checkSql="SELECT highest_score FROM game_accounts WHERE player_id=?";
        String updateSql="UPDATE game_accounts SET highest_score=? WHERE player_id=?";
        try {
            PreparedStatement pstmt1 = this.connection.prepareStatement(checkSql);
            PreparedStatement pstmt2 = this.connection.prepareStatement(updateSql);
            pstmt1.setString(1,playerID);
            pstmt2.setInt(1,score);pstmt2.setString(2,playerID);
            ResultSet rs = pstmt1.executeQuery();
            int oldScore=0;
            if(rs.next()) {
                oldScore = rs.getInt("highest_score");
            }
            if(oldScore<score){
                int result=pstmt2.executeUpdate();
                if(result>0) {
                 System.out.println("Account: "+playerID+" Highest score updated to "+score);
                 this.server.getAccounts().get(playerID).setHighestScore(score);   //更新内存的账号信息
                }
            }
            pstmt1.close();pstmt2.close();rs.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to update highest score");
            e.printStackTrace();
        }
    }

    public JSONObject requestForRank(){
        String rankSql="SELECT *FROM game_accounts ORDER BY highest_score DESC LIMIT 5";
        JSONObject rank=new JSONObject();
        JSONArray rankArray=new JSONArray();
        try{
            Statement statement=this.connection.createStatement();
            ResultSet rs=statement.executeQuery(rankSql);
            while (rs.next()) {
                JSONObject obj=new JSONObject();
              String playerID=rs.getString("player_id");
              int score=rs.getInt("highest_score");
              obj.put("player_id",playerID);
              obj.put("highest_score",score);
              rankArray.put(obj);
            }
            rank.put("type","rank_request");
            rank.put("rankData",rankArray);
            rs.close();statement.close();
            return rank;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAchievement(String playerID, JSONObject achievement){
     String sql1="UPDATE game_accounts SET achievement_1=? WHERE player_id=?";
     String sql2="UPDATE game_accounts SET achievement_2=? WHERE player_id=?";
     try {
         if (achievement.getString("which").equals("achievement_1")) {
             if(!this.server.getAccounts().get(playerID).isAchievement1()) {
                 PreparedStatement preparedStatement = this.connection.prepareStatement(sql1);
                 preparedStatement.setBoolean(1,true);
                 preparedStatement.setString(2,playerID);
                 int result = preparedStatement.executeUpdate();
                 if (result > 0) {
                     System.out.println("Account: "+playerID+" Achievement1 updated to true");
                     this.server.getAccounts().get(playerID).setAchievement1(true);
                 }
                 preparedStatement.close();
             }
         }
         else {
             if(!this.server.getAccounts().get(playerID).isAchievement2()) {
                 PreparedStatement preparedStatement = this.connection.prepareStatement(sql2);
                 preparedStatement.setBoolean(1,true);
                 preparedStatement.setString(2,playerID);
                 int result = preparedStatement.executeUpdate();
                 if (result > 0) {
                     System.out.println("Account: "+playerID+" Achievement2 updated to true");
                     this.server.getAccounts().get(playerID).setAchievement2(true);
                 }
                 preparedStatement.close();
             }
         }

     }
     catch (SQLException e) {
         e.printStackTrace();
     }
    }

    public JSONObject requestForAchievement(String playerID){
        String sql="SELECT *FROM game_accounts WHERE player_id=?";
        try{
            PreparedStatement preparedStatement=this.connection.prepareStatement(sql);
            preparedStatement.setString(1,playerID);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next()) {
             JSONObject obj=new JSONObject();
             obj.put("type","achievement_request");
             obj.put("achievement_1",rs.getBoolean("achievement_1"));
             obj.put("achievement_2",rs.getBoolean("achievement_2"));
             rs.close();preparedStatement.close();
             return obj;
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePowerStone(String playerID,int newCount){
        String sql="UPDATE game_accounts SET item_count = ? WHERE player_id = ?";
        try{
            PreparedStatement pstmt=this.connection.prepareStatement(sql);
            pstmt.setInt(1,newCount);
            pstmt.setString(2,playerID);
            int result=pstmt.executeUpdate();
            if(result>0) {
                System.out.println("Power stone updated");
                return true;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Power stone not updated");

        }
            return false;
    }


    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("数据库连接已关闭");
            }
        } catch (SQLException e) {
            System.err.println("关闭数据库连接时出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        GameDatabase gdb = new GameDatabase(new ServerRunning());
        gdb.updatePowerStone("KID",10);

    }

}
