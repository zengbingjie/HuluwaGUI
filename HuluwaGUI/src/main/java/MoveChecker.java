import java.util.ArrayList;

public class MoveChecker {
    private Field field;
    public MoveChecker(Field field){
        this.field = field;
    }
    public boolean isWithinBoard(int x, int y){
        return ((x>=0) && (x<field.getBoardWidth()) && (y>=0) && (y<field.getBoardHeight()));
    }
    public boolean isMoveable(char identify, int x, int y){
        int space = field.getSPACE(), w = field.getBoardWidth(), h = field.getBoardHeight();
        int xStart = (x-space/2)<0 ? 0: (x-space/2);
        int yStart = (y-space/2)<0 ? 0: (y-space/2);
        int xEnd = (x+space/2)>=w ? (w-1) : (x+space/2);
        int yEnd = (y+space/2)>=h ? (h-1) : (y+space/2);
        ArrayList world = field.getWorld();
        for (int i = 0; i < world.size(); i++) {
            Thing2D item = (Thing2D) world.get(i);
            if (item instanceof Creature){
                if ((((Creature) item).getIdentify()!=identify) && (((Creature) item).x()>=xStart) && (((Creature) item).x()<=xEnd) && (((Creature) item).y()>=yStart) && (((Creature) item).y()<=yEnd)){
                    return false;
                }
            }
        }
        return true;
    }
}
