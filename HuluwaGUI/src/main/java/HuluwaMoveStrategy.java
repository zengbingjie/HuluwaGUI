import java.util.ArrayList;
import java.util.Random;

public class HuluwaMoveStrategy implements MoveStrategy {

    private int lastMyX = 0;
    private int lastMyY = 0;

    private static int keepStillCount = 0;

    @Override
    public MoveStrategyResult howToMove(Field field, int myX, int myY) {

        MoveStrategyResult rslt = new MoveStrategyResult();

        if (keepStillCount>300){
            keepStillCount++;
            if (keepStillCount<400) {
                Random random = new Random();
                if (random.nextInt(100)>10) {
                    rslt.setX(1);
                    rslt.setY(0);
                } else {
                    rslt.setX(0);
                    rslt.setY(1);
                }
                lastMyX = myX;
                lastMyY = myY;
                return rslt;
            } else {
                keepStillCount = 0;
            }
        }

        if (myX==lastMyX && myY==lastMyY){
            keepStillCount++;
            if (keepStillCount>300){
                rslt.setX(1);
                rslt.setY(0);
                lastMyX = myX;
                lastMyY = myY;
                return  rslt;
            }
        }

        int shortestDistance = field.getBoardHeight()+field.getBoardWidth();
        boolean enemyIsAllDead = true;
        ArrayList world = field.getWorld();
        // find its nearest enemy
        for (int i = 0; i < world.size(); i++) {
            Thing2D item = (Thing2D) world.get(i);
            if (item instanceof Creature){
                //char id = ((Creature) item).getIdentify();
                int x = ((Creature) item).x();
                int y = ((Creature) item).y();
                if (((Creature) item).getCamp()==Camp.EVIL && ((Creature) item).isAlive()) { // is enemy of huluwas😡
                    enemyIsAllDead = false;
                    if (Math.abs(x-myX)+Math.abs(y-myY)<shortestDistance){
                        rslt.setX(x);
                        rslt.setY(y);
                        shortestDistance = Math.abs(x-myX)+Math.abs(y-myY);
                    }
                }
            }
        }
        if (enemyIsAllDead){
            rslt.setX(0);
            rslt.setY(0);
        } else {
            Random random = new Random();
            if ((random.nextInt(2)) > 0) { // move in x direction
                int direction = rslt.getX() > myX ? (1) : (-1);
                rslt.setX(direction);
                rslt.setY(0);
            } else { // move in y direction
                int direction = rslt.getY() > myY ? (1) : (-1);
                rslt.setX(0);
                rslt.setY(direction);
            }
        }
        lastMyX = myX;
        lastMyY = myY;
        return rslt;
    }
}
