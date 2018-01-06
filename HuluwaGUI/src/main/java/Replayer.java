import javax.swing.*;
import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;

public class Replayer implements Runnable {

    private File recordFile;

    private Field field;

    private BufferedReader bufferedReader;

    public Replayer(Field field){
        this.field = field;
    }

    public boolean loadRecord() {
        JFileChooser fileChooser = new JFileChooser("Records/");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            this.recordFile = fileChooser.getSelectedFile();
            if (recordFile != null) {
                if (recordFile.getName().startsWith("record")) { // simple examination of the file name
                    try {
                        bufferedReader = new BufferedReader(new FileReader(recordFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false; // load failed
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
//        while (!Thread.interrupted()) {
            try {
                String recordLine = bufferedReader.readLine(); // the first number: the size of world
                //System.out.println(recordLine);
                int worldSize = Integer.parseInt(recordLine);
                recordLine = bufferedReader.readLine();
                //System.out.println(recordLine);
                while (recordLine!=null) {
                    this.field.getRecord().clear();
                    for (int i=0; i<worldSize && recordLine!=null; i++) {
                        String[] recordArgs = recordLine.split(" ");
                        String imageName = recordArgs[0];
                        int x = Integer.parseInt(recordArgs[1]);
                        int y = Integer.parseInt(recordArgs[2]);
                        this.field.getRecord().add(new Record(imageName, x, y));
                        recordLine = bufferedReader.readLine();
                    }
                    if (recordLine==null){
                        this.field.setStateREPLAYOVER();
                        break;
                    }
                    try{
                        Thread.sleep(10);
                        this.field.repaint();
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                    recordLine = bufferedReader.readLine();
                    //System.out.println(recordLine);
                }
                this.field.setStateREPLAYOVER();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
//    }
}
