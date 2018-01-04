import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Image;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;

public class Field extends JPanel {

    private final int OFFSET = 10;
    private final int SPACE = 60;
    private final int N = 10; // row
    private final int M = 15; // col

    private ExecutorService exec;

    private ArrayList<Huluwa> huluwas = new ArrayList<>();
    private ArrayList<Frog> frogs = new ArrayList<>();
    private Grandpa grandpa;
    private Snake snake;
    private Scorpion scorpion;
    ArrayList world;

    private int w = 0;
    private int h = 0;
    private boolean completed = false;

    private StringBuilder level = new StringBuilder("");

    public Field() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();
    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }

    private void initPlayRole() {
        for (int i=0; i<N; i++){
            for (int j=0; j<M; j++){
                level.append(" ");
            }
            level.append("\n");
        }
        new HuluwaWarFormation().format(level, N, M);
        //System.out.println(level.toString());
    }

    public final void initWorld() {

        exec = Executors.newCachedThreadPool();

        initPlayRole();

        int x = OFFSET;
        int y = OFFSET;

        for (int i = 0; i < level.length(); i++) {

            char item = level.charAt(i);

            if (item == '\n') { // add a new line
                y += SPACE;
                if (this.w < x) {
                    this.w = x;
                }

                x = OFFSET;
            } else if (item == ' ') { // empty space
                x += SPACE;
            }
            else if ((item >= '1') && (item <= '7')) { // huluwas
                huluwas.add(new Huluwa(x, y, this, item-'0'));
                x += SPACE;
            } else if (item == 'g') { // grandpa
                grandpa = new Grandpa(x, y,this);
                x += SPACE;
            } else if ((item >= 'a') && (item <= 'f')) { // frogs
                frogs.add(new Frog(x, y, this, item));
                x += SPACE;
            } else if (item == 's') { // snake
                snake = new Snake(x,y,this);
                x += SPACE;
            } else if (item == 'x') { // scorpion
                scorpion = new Scorpion(x, y, this);
                x += SPACE;
            } else {
                System.out.println("unexpected item on field!");
            }

            h = y;
        }

    }


    public void buildWorld(Graphics g) {
        // set background
        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("background.png")).getImage(), 0, 0,getWidth(), getHeight(), this);

        world = new ArrayList();
        world.addAll(huluwas);
        world.addAll(frogs);
        world.add(grandpa);
        world.add(snake);
        world.add(scorpion);

        for (int i = 0; i < world.size(); i++) {

            Thing2D item = (Thing2D) world.get(i);

            if (item instanceof Creature) {
                g.drawImage(item.getImage(), item.x(), item.y(),this);
            } else if (item instanceof Grandpa){
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            }
            else {
                //g.drawImage(item.getImage(), item.x(), item.y(), this);
            }

            if (completed) {
                g.setColor(new Color(0, 0, 0));
                g.drawString("War Ended.", w/2-20, h/2);
            }

        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        buildWorld(g);
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (completed) {
                return;
            }

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                for (Huluwa hlw : huluwas)
                    exec.execute(hlw);
                for (Frog frog: frogs){
                    exec.execute(frog);
                }
                exec.execute(grandpa);
                exec.execute(snake);
                exec.execute(scorpion);
            } else if (key == KeyEvent.VK_R) {
                exec.shutdownNow();
                restartLevel();
            }

            repaint();
        }
    }


    public void restartLevel() {

        huluwas.clear();
        frogs.clear();

        initWorld();
        if (completed) {
            completed = false;
        }
    }

    public ArrayList getWorld() {
        return world;
    }

    public int getSPACE() {
        return SPACE;
    }

    public boolean gameHasOver(){
        boolean goodCampAllDead = true, evilCampAllDead = true;
        for (int i = 0; i < world.size(); i++) {
            Thing2D item = (Thing2D) world.get(i);
            if (item instanceof Creature){
                if (((Creature) item).getCamp()==Camp.GOOD && ((Creature) item).isAlive()){
                    goodCampAllDead = false;
                } else if (((Creature) item).getCamp()==Camp.EVIL && ((Creature) item).isAlive()){
                    evilCampAllDead = false;
                } else {
                    // do nothing
                }
                if (goodCampAllDead==false && evilCampAllDead==false){
                    return false;
                }
            }
        }
        return true;
    }
}