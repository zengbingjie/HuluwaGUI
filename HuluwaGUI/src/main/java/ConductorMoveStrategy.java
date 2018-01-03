import java.util.Random;

public class ConductorMoveStrategy implements MoveStrategy {

    private static int count = 0;
    private int direction = 1;

    @Override
    public MoveStrategyResult howToMove(Field field, int myX, int myY) {
        count++;
        MoveStrategyResult rslt = new MoveStrategyResult();
        if (count%5==0) { // walk slower
            if (count >= 100) { // change walking direction
                count = 0;
                Random random = new Random();
                if ((random.nextInt(2)) > 0) {
                    direction *= (-1);
                }
            }
            rslt.setY(direction);
        }
        return rslt;
    }
}
