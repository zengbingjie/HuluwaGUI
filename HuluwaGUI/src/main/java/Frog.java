import java.util.Random;

public class Frog extends Creature {
    private final int SPEED = 1;
    public Frog(int x, int y, Field field, char identify){
        // x, y, field, name, state, identify
        super(x, y, field, "frog", CreatureState.LEFT, identify);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            move(-SPEED, 0); // stupid move
            try {
                Thread.sleep(10);
                this.field.repaint();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
