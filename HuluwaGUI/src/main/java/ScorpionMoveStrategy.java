import java.util.Random;

public class ScorpionMoveStrategy implements MoveStrategy {

    private static int count = 0;
    private int lastRsltX = -1;
    private int lastRsltY = 0;

    @Override
    public MoveStrategyResult howToMove(Field field, int myX, int myY) {

        MoveStrategyResult rslt = new MoveStrategyResult();
        rslt.setX(lastRsltX);
        rslt.setY(lastRsltY);
        if (++count >20){
            count = 0;
            Random random = new Random();
            int p = random.nextInt(100);
            if (p < 25){
                rslt.setX(-1); rslt.setY(0);
            } else if (p < 50) {
                rslt.setX(1); rslt.setY(0);
            } else if (p < 95) {
                rslt.setX(0); rslt.setY(-1);
            } else {
                rslt.setX(0); rslt.setY(1);
            }
        }
        lastRsltX = rslt.getX();
        lastRsltY = rslt.getY();
        return rslt;

    }

}
