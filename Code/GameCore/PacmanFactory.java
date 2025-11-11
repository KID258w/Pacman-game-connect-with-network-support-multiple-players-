package Code.GameCore;

public class PacmanFactory {
    public enum PacmanType {
        RED, BLUE, GREEN, YELLOW
    }

    public static Pacman createPacman(PacmanType type, int x, int y, int viewDx, int viewDy) {
        switch (type) {
            case RED:
                return new RedPacman(x, y, viewDx, viewDy,0);
            case BLUE:
                return new BluePacman(x, y, viewDx, viewDy,0);
            case GREEN:
                return new GreenPacman(x, y, viewDx, viewDy,1);
                case YELLOW:
                    return new YellowPacman(x, y, viewDx, viewDy,0);
            default:
                throw new IllegalArgumentException("Unknown Pacman type");
        }
    }
    public static PacmanType getPacmanType(int i){
        switch (i){
            case 1:return PacmanType.YELLOW;
            case 2:return PacmanType.RED;
            case 3:return PacmanType.BLUE;
            case 4:return PacmanType.GREEN;
            default:return PacmanType.YELLOW;
        }

    }


}
