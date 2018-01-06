import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class RecordBufferTest {

@Test
public void testWaitForSaving() throws Exception {
    Field field = new Field();
    ArrayList records = new ArrayList();
    RecordBuffer rb = new RecordBuffer(field, records);
    if (rb.getFinishReading()==true) {
        rb.waitForReading();
        assertEquals(true, rb.getFinishReading());
        rb.writeNew(new ArrayList());
        assertEquals(true, rb.getIsSaved());
        rb.waitForSaving();
        assertEquals(true, rb.getIsSaved());
    }
} 
/*
@Test
public void testWaitForReading() throws Exception { 

} 

@Test
public void testGetRecords() throws Exception {

} 

@Test
public void testWriteNew() throws Exception { 

} 

*/
} 
