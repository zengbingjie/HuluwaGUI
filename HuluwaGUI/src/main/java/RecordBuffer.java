import java.util.ArrayList;

public class RecordBuffer {

    private Field field;
    private ArrayList records;
    private boolean isSaved = false;
    private boolean finishReading = true;

    public RecordBuffer(Field field, ArrayList records){
        this.field = field;
        this.records = records;
    }

    public synchronized void waitForSaving() throws InterruptedException {
        while (!isSaved){
            wait();
        }
    }


    public synchronized void waitForReading() throws InterruptedException {
        while (!finishReading){
            wait();
        }
    }


    public synchronized ArrayList getRecords() throws InterruptedException
    {
        finishReading = true;
        notifyAll();
        return this.records;
    }

    public synchronized void writeNew(ArrayList newRecords) throws InterruptedException {
        records.clear();
        records.addAll(newRecords);
        isSaved = true;
        notifyAll();
    }

    public boolean getIsSaved(){ // just for RecordBufferTest
        return this.isSaved;
    }

    public boolean getFinishReading(){ // just for RecordBufferTest
        return this.finishReading;
    }
}
