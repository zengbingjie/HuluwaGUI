import java.util.Random;

public class Huluwa extends Creature {

    private int rank; // 1~7

    private final int SPEED = 1;

    public Huluwa(int x, int y, Field field, int rank){
        // x, y, field, name, state, identify
        super(x, y, field, COLOR.values()[rank-1].toString(), CreatureState.RIGHT, Integer.toString(rank).charAt(0));

        this.rank = rank;
    }

    private void fight() {
        MoveStrategyResult deltaPosition = new HuluwaMoveStrategy().howToMove(this.field, this.x(), this.y());
        this.move(deltaPosition.getX()*SPEED,deltaPosition.getY()*SPEED);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            this.fight();
            try {
                Thread.sleep(10);
                this.field.repaint();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
enum COLOR {
    red, orange, yellow, green, cyan, blue, purple
}