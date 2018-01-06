import java.util.Random;

public class Scorpion extends Creature {

    private final int SPEED = 2;

    private ScorpionMoveStrategy moveStrategy = new ScorpionMoveStrategy();

    public Scorpion(int x, int y, Field field){
        // x, y, field, name, state, identify
        super(x, y, field, "scorpion", CreatureState.LEFT, 'x', 150);
    }

    private void fight() {
        MoveStrategyResult deltaPosition = moveStrategy.howToMove(this.field, this.x(), this.y());
        this.move(deltaPosition.getX()*SPEED,deltaPosition.getY()*SPEED);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (this.isAlive()) {
                this.fight();
            }
            try {
                Thread.sleep(10);
                this.field.repaint();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
