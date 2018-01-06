import javax.swing.*;
import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;

public class Replayer implements Runnable {

    private File recordFile;

    private Field field;

    private BufferedReader bufferedReader;

    private RecordBuffer recordBuffer;

    public Replayer(Field field, RecordBuffer recordBuffer) {
        this.field = field;
        this.recordBuffer = recordBuffer;
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
                while ((recordLine!=null) && (!Thread.interrupted())) {
                    ArrayList tempRecords = new ArrayList();
                    int i;
                    for (i=0; i<worldSize && recordLine!=null; i++) {
                        String[] recordArgs = recordLine.split(" ");
                        String imageName = recordArgs[0];
                        int x = Integer.parseInt(recordArgs[1]);
                        int y = Integer.parseInt(recordArgs[2]);
                        tempRecords.add(new Record(imageName, x, y));
                        recordLine = bufferedReader.readLine();
                        //System.out.println(recordLine);
                    }
                    if (i==worldSize){
                        try {
                            recordBuffer.waitForReading();
                            recordBuffer.writeNew(tempRecords);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    if (recordLine==null){
                        this.field.setStateREPLAYOVER();
                        this.field.repaint();
                        break;
                    }
                    try{
                        Thread.sleep(10);
                        this.field.repaint();
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                    //recordLine = bufferedReader.readLine();
                    //System.out.println(recordLine);
                }
                this.field.setStateREPLAYOVER();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
//    }
}
