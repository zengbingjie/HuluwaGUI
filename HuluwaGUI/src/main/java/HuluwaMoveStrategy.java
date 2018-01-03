import java.util.ArrayList;
import java.util.Random;

public class HuluwaMoveStrategy implements MoveStrategy {
    @Override
    public MoveStrategyResult howToMove(Field field, int myX, int myY) {
        MoveStrategyResult rslt = new MoveStrategyResult();
        int shortestDistance = field.getBoardHeight()+field.getBoardWidth();
        ArrayList world = field.getWorld();
        for (Object item: world){
            if (item instanceof Creature){
                char id = ((Creature) item).getIdentify();
                int x = ((Creature) item).x();
                int y = ((Creature) item).y();
                if ((id>='a' && id<='f') || id=='s' || id=='x'){ // is enemy of huluwas😡
                    if (Math.abs(x-myX)+Math.abs(y-myY)<shortestDistance){
                        rslt.setX(x);
                        rslt.setY(y);
                        shortestDistance = Math.abs(x-myX)+Math.abs(y-myY);
                    }
                }
            }
        }
        Random random = new Random();
        if ((random.nextInt(2))>0) { // move in x direction
            int direction = rslt.getX()>myX ? (1) : (-1);
            rslt.setX(direction);
            rslt.setY(0);
        } else { // move in y direction
            int direction = rslt.getY()>myY ? (1) : (-1);
            rslt.setX(0);
            rslt.setY(direction);
        }
        return rslt;
    }
}
