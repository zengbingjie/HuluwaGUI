import java.util.Random;

public class Scorpion extends Creature {

    private final int SPEED = 2;

    public Scorpion(int x, int y, Field field){
        // x, y, field, name, state, identify
        super(x, y, field, "scorpion", CreatureState.LEFT, 'x');
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (this.isAlive()) {
                move(-SPEED, 0); // stupid move
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
