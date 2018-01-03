import java.util.Random;

public class Snake extends Creature {

    private final int SPEED = 1;
    private final ConductorMoveStrategy moveStrategy = new ConductorMoveStrategy();

    public Snake(int x, int y, Field field){
        // x, y, field, name, state, identify
        super(x, y, field, "snake", CreatureState.LEFT,  's');
    }

    private void conduct(){
        MoveStrategyResult deltaPosition = moveStrategy.howToMove(this.field, this.x(), this.y());
        this.move(deltaPosition.getX()*SPEED,deltaPosition.getY()*SPEED);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            this.conduct();
            try {
                Thread.sleep(10);
                this.field.repaint();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
