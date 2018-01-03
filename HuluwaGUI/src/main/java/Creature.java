import java.awt.Image;
import java.net.URL;
import java.util.Random;
import javax.swing.ImageIcon;

public class Creature extends Thing2D implements Runnable {
    protected Field field;

    private String name;
    private char identify;
    private CreatureState state;

    public Creature(int x, int y, Field field, String name, CreatureState state, char identify) {
        super(x, y);

        this.field = field;
        this.name = name;
        this.identify = identify;
        this.state = state;

        URL loc = this.getClass().getClassLoader().getResource(this.name + this.state.toString() + ".png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }

    public void move(int x, int y) {
        MoveChecker moveChecker = new MoveChecker(this.field);
        int nx = this.x() + x;
        int ny = this.y() + y;
        if (moveChecker.isWithinBoard(nx, ny)) {
            if (moveChecker.isMoveable(this.identify, nx, ny)) {
                this.setX(nx);
                this.setY(ny);
            }
        }
    }

    // Creature default movements
    public void run() {
        while (!Thread.interrupted()) {
            Random rand = new Random();

            this.move(rand.nextInt(10), rand.nextInt(10));
            try {

                Thread.sleep(100);
                this.field.repaint();

            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public char getIdentify() { return this.identify; }
}