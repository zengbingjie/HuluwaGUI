import java.awt.Image;
import java.net.URL;
import java.util.Random;
import javax.swing.ImageIcon;

public class Creature extends Thing2D implements Runnable {
    protected Field field;

    private String name;
    private char identify;
    private CreatureState state;
    private int hp;

    public Creature(int x, int y, Field field, String name, CreatureState state, char identify, int hp) {
        super(x, y);

        this.field = field;
        this.name = name;
        this.identify = identify;
        this.state = state;
        this.hp = hp;

        URL loc = this.getClass().getClassLoader().getResource(this.name + this.state.toString() + ".png");
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }

    private void battleWith(Creature enemy){
        if (this.getCamp()==Camp.GOOD){
            Random random = new Random();
            if (random.nextInt(100)>43) { // 57% chance for huluwas to win~
                enemy.behurt();
            } else {
                this.behurt();
            }
        } else {
            Random random = new Random();
            if (random.nextInt(100)>57) { // 43% chance for huluwas to win~
                enemy.behurt();
            } else {
                this.behurt();
            }
        }
    }

    public void move(int x, int y) {
        if (!this.field.gameIsOver()) {
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

    public synchronized void setState(CreatureState state) {
        this.state = state;
    }

    public synchronized void resetImage() {
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

    private void behurt() {
        this.hp -= 5;
        if (this.hp < 0){
            this.hp = 0;
            this.setState(CreatureState.DEAD);
            this.resetImage();
        }
    }

    public String getRecord() {
        // image name + position x + position y
        return ((this.name + this.state.toString() + ".png ") + (Integer.toString(this.x())) + " " + (Integer.toString(this.y())) + "\n");
    }
}