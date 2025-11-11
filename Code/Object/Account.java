package Code.Object;

public class Account {
    private String playerID;
    private String password;
    private int HighestScore;
    private int powerStone;
    private boolean achievement1 = false;
    private boolean achievement2 = false;
    private boolean achievement3 = false;
    private boolean onLine;

    public Account(String playerID, String password) {
        this.playerID = playerID;
        this.password=password;
        this.HighestScore=0;
        this.powerStone=10;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHighestScore(int highestScore) {
        this.HighestScore = highestScore;
    }

    public void setPowerStone(int powerStone) {
        this.powerStone = powerStone;
    }
    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
    public void setAchievement1(boolean achievement1) {
        this.achievement1 = achievement1;
    }

    public void setAchievement2(boolean achievement2) {
        this.achievement2 = achievement2;
    }
    public void setAchievement3(boolean achievement3) {
        this.achievement3 = achievement3;
    }

    public String getPlayerID(){
        return playerID;
    }

    public String getPassword() {
        return password;
    }

    public int getHighestScore(){
        return HighestScore;
    }

    public int getPowerStone() {
        return powerStone;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public boolean isAchievement1() {
        return achievement1;
    }

    public boolean isAchievement2() {
        return achievement2;
    }

    public boolean isAchievement3() {
        return achievement3;
    }
}
