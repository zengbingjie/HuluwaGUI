import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Recorder implements Runnable {

    //private static int fileCount = 0;

    private String stringBuffer = "";

    private Field field;
    private BufferedWriter bufferedWriter;

    Recorder(Field field) {
        this.field = field;
    }

    public boolean saveRecordFile() {
        // initialize file
        //String fileName = "Records/record" + Integer.toString(++fileCount) + ".txt";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "Records/record" + df.format(new Date()) + ".txt";
        File file = new File(fileName);
        boolean isSuccess = false;
        try {
            isSuccess = file.createNewFile();
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        // write record
        try {
            bufferedWriter.write(stringBuffer);
        } catch ( Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public void clearCurrentRecord() {
        stringBuffer = "";
    }

    @Override
    public void run() {
        // record how many creatures there are in the world
        ArrayList world = field.getWorld();
        stringBuffer = Integer.toString(world.size()) + "\n";
        while (!Thread.interrupted()) {
            world = field.getWorld(); // the amount of creatures is not supposed to change, just in case
            for (int i = 0; i < world.size(); i++) {
                Thing2D item = (Thing2D) world.get(i);
                if (item instanceof Creature){
                    stringBuffer += ((Creature) item).getRecord();
                }
            }
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
