package util;

public class Helper {
    public static int getUnitSize(int boardWidth, int boardHeight) {
        return (boardWidth < boardHeight ? boardWidth : boardHeight) / 40;
    }
}
