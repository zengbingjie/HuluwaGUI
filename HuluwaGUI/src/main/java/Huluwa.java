import java.util.Random;

public class Huluwa extends Creature {

    private int rank; // 1~7

    private final int SPEED = 2;

    private HuluwaMoveStrategy moveStrategy = new HuluwaMoveStrategy();

    public Huluwa(int x, int y, Field field, int rank){
        // x, y, field, name, state, identify
        super(x, y, field, COLOR.values()[rank-1].toString(), CreatureState.RIGHT, Integer.toString(rank).charAt(0));

        this.rank = rank;
    }

    private void fight() {
        MoveStrategyResult deltaPosition = moveStrategy.howToMove(this.field, this.x(), this.y());
        if (deltaPosition.getX()>0){
            this.setState(CreatureState.RIGHT);
            this.resetImage();
        } else if (deltaPosition.getX()<0){
            this.setState(CreatureState.LEFT);
            this.resetImage();
        } else {
            // do nothing. not change direction
        }
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
enum COLOR {
    red, orange, yellow, green, cyan, blue, purple
}