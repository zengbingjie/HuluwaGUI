//package test;

import org.junit.Test;
import static org.junit.Assert.*;

public class HuluwaWarFormationTest { 

@Test
public void testFormat() throws Exception {
    StringBuilder level = new StringBuilder("");
    for (int i=0; i<8; i++){
        for (int j=0; j<12; j++){
            level.append(" ");
        }
        level.append("\n");
    }
    new HuluwaWarFormation().format(level, 8,12);
    assertEquals("            \n" +
            "6           \n" +
            " 4       c  \n" +
            "  2     a e \n" +
            "g  1   x   s\n" +
            "  3     b f \n" +
            " 5       d  \n" +
            "7           \n", level.toString());
}

}
