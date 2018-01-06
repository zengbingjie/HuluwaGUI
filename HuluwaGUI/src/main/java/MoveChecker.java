import java.util.ArrayList;

public class MoveChecker {
    private Field field;
    public MoveChecker(Field field){
        this.field = field;
    }
    public boolean isWithinBoard(int x, int y){
        return ((x>=0) && (x<(field.getBoardWidth()-field.getSPACE())) && (y>=0) && (y<(field.getBoardHeight()-field.getSPACE())));
    }
    public boolean isMoveable(char identify, int x, int y){
        int space = field.getSPACE(), w = field.getBoardWidth(), h = field.getBoardHeight();
        int xStart = (x-space/2)<0 ? 0: (x-space/2);
        int yStart = (y-space/4*3)<0 ? 0: (y-space/4*3);
        int xEnd = (x+space/2)>=w ? (w-1) : (x+space/2);
        int yEnd = (y+space/4*3)>=h ? (h-1) : (y+space/4*3);
        ArrayList world = field.getWorld();
        for (int i = 0; i < world.size(); i++) {
            Thing2D item = (Thing2D) world.get(i);
            if (item instanceof Creature){
                if ((((Creature) item).getIdentify()!=identify) && (((Creature) item).isAlive()) && (((Creature) item).x()>=xStart) && (((Creature) item).x()<=xEnd) && (((Creature) item).y()>=yStart) && (((Creature) item).y()<=yEnd)){
                    return false;
                }
            }
        }
        return true;
    }
    public Creature getEnemy(Camp camp, CreatureState state, int x, int y){
        assert (state!=CreatureState.DEAD && camp!=Camp.CONDUCTOR);
        Camp enemyCamp = (camp==Camp.GOOD ? Camp.EVIL : Camp.GOOD);
        int space = field.getSPACE(), w = field.getBoardWidth(), h = field.getBoardHeight();
        int xStart=0, xEnd=0, yStart=0, yEnd=0;
        yStart = (y-space/2)<0 ? 0: (y-space/2);
        yEnd = (y+space/2)>=h ? (h-1) : (y+space/2);
        if (state == CreatureState.RIGHT) {
            xStart = x;
            xEnd = (x+space/2)>=w ? (w-1) : (x+space/2);
        }
        if (state==CreatureState.LEFT){
            xStart = (x-space/2)<0 ? (0) : (x-space/2);
            xEnd = x;
        }
        ArrayList world = field.getWorld();
        for (int i = 0; i < world.size(); i++) {
            Thing2D item = (Thing2D) world.get(i);
            if (item instanceof Creature){
                if ((((Creature) item).getCamp()==enemyCamp) && (((Creature) item).isAlive()) && (((Creature) item).x()>=xStart) && (((Creature) item).x()<=xEnd) && (((Creature) item).y()>=yStart) && (((Creature) item).y()<=yEnd)){
                    return ((Creature) item);
                }
            }
        }
        return null;
    }
}
