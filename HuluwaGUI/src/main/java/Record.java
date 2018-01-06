import java.util.ArrayList;

public class Record {

    private String imgName;
    private int x;
    private int y;

    public Record(String imgName, int x, int y) {
        this.imgName = imgName;
        this.x = x;
        this.y = y;
    }

    public String getImgName() {
        return this.imgName;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int checkReplayOverResult(ArrayList tempRecords) {
        boolean goodCampAlive = false, evilCampAlive = false;
        for (int i = 0; i < tempRecords.size(); i++) {
            Record item = (Record) tempRecords.get(i);
            String s = item.getImgName();
            if ((s.startsWith("red") || s.startsWith("orange") || s.startsWith("yellow") || s.startsWith("green") || s.startsWith("cyan") || s.startsWith("blue") || s.startsWith("purple")) && (!s.endsWith("DEAD.png"))){
                goodCampAlive = true;
            } else if ((s.startsWith("frog") || s.startsWith("scorpion")) && (!s.endsWith("DEAD.png"))){
                evilCampAlive = true;
            }
        }
        if (goodCampAlive && (!evilCampAlive)){
            return 1; // huluwa win
        } else if (goodCampAlive && evilCampAlive){
            return 0; // peace
        } else {
            return -1; // evil win
        }
    }


}
