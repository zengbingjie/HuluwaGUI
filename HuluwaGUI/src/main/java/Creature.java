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

    private void battleWith(Creature enemy){
        if (this.getCamp()==Camp.GOOD){
            Random random = new Random();
            if (random.nextInt(10)>4) { // 60% chance for huluwas to win~
                enemy.setState(CreatureState.DEAD);
                enemy.resetImage();
            } else {
                this.setState(CreatureState.DEAD);
                this.resetImage();
            }
        } else {
            Random random = new Random();
            if (random.nextInt(10)>6) { // 60% chance for huluwas to win~
                enemy.setState(CreatureState.DEAD);
                enemy.resetImage();
            } else {
                this.setState(CreatureState.DEAD);
                this.resetImage();
            }
        }
    }

    public void move(int x, int y) {
        if (!this.field.gameHasOver()) {
            MoveChecker moveChecker = new MoveChecker(this.field);
            int nx = this.x() + x;
            int ny = this.y() + y;
            if (moveChecker.isWithinBoard(nx, ny)) {
                if (moveChecker.isMoveable(this.identify, nx, ny)) {
                    this.setX(nx);
                    this.setY(ny);
                } else { // maybe kill someone
                    if (this.getCamp() != Camp.CONDUCTOR) { // grandpa and snake do not kill🙏
                        Creature enemy = moveChecker.getEnemy(this.getCamp(), this.state, nx, ny);
                        if (enemy != null) {
                            this.battleWith(enemy);
                        }
                    }
                }
            }
        }
    }

    // Creature default movements
    public void run() {
        while (!Thread.interrupted()) {
            Random rand = new Random();
            if (this.isAlive()) {
                this.move(rand.nextInt(10), rand.nextInt(10));
            }
            try {

                Thread.sleep(100);
                this.field.repaint();

            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public char getIdentify() { return this.identify; }
    public void setState(CreatureState state) {
        this.state = state;
    }

    public void resetImage() {
        URL loc = this.getClass().getClassLoader().getResource(this.name + this.state.toString() + ".png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }
    public Camp getCamp() {
        if (this.identify>='1' && this.identify<='7'){
            return Camp.GOOD;
        } else if ((this.identify>='a' && this.identify<='f') || this.identify=='x'){
            return Camp.EVIL;
        } else { // grandpa and snake
            return Camp.CONDUCTOR;
        }
    }
    public boolean isAlive() {
        return this.state!=CreatureState.DEAD;
    }
}