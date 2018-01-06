import org.junit.Test;
import static org.junit.Assert.*;

public class MoveCheckerTest { 

@Test
public void testIsMoveable() throws Exception {
    Field field = new Field();
    // two huluwas
    Creature c1 = (Creature) field.getWorld().get(0);
    Thing2D item1 = (Thing2D) field.getWorld().get(0);
    Thing2D item2 = (Thing2D) field.getWorld().get(1);
    item1.setX(100);
    item1.setY(100);
    item2.setX(200);
    item2.setY(200);
    assertEquals(false, new MoveChecker(field).isMoveable(c1.getIdentify(), 205, 205));
}

} 
