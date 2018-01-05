import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.Image;
import java.net.URL;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Field extends JPanel {

    private final int OFFSET = 10;
    private final int SPACE = 100;
    private final int N = 7; // row
    private final int M = 11; // col

    private ExecutorService exec;

    private GameState gameState;

    private Recorder recorder;
    Replayer replayer;

    private ArrayList record;

    private java.util.Timer checkGameOverTimer = new Timer();

    private ArrayList<Huluwa> huluwas = new ArrayList<>();
    private ArrayList<Frog> frogs = new ArrayList<>();
    private Grandpa grandpa;
    private Snake snake;
    private Scorpion scorpion;
    private ArrayList world;

    private int w = 0;
    private int h = 0;

    private StringBuilder level = new StringBuilder("");

    public Field() {
        gameState = GameState.BEGIN;
        recorder = new Recorder(this);
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

        world = new ArrayList();
        world.addAll(huluwas);
        world.addAll(frogs);
        world.add(grandpa);
        world.add(snake);
        world.add(scorpion);

    }


    public void buildWorld(Graphics g) {
        // set background
        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (gameState == GameState.BEGIN) {
            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("beginBackground.png")).getImage(), 0, 0, getWidth(), getHeight(), this);
        } else if (gameState == GameState.PLAYING) {
            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("background.png")).getImage(), 0, 0, getWidth(), getHeight(), this);
        } else if (gameState == GameState.PEACE){
            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("peaceBackground.png")).getImage(), 0, 0, getWidth(), getHeight(), this);
        } else if (gameState == GameState.OVER) {
            if (huluwasAllDead()) {
                g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("badWinBackground.png")).getImage(), 0, 0, getWidth(), getHeight(), this);
            } else {
                g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("huluwaWinBackground.png")).getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        } else if (gameState == GameState.REPLAYING){
            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("background.png")).getImage(), 0, 0, getWidth(), getHeight(), this);
            //System.out.println(record.size());
            for (int i = 0; i < record.size(); i++) {
                Record item = (Record) record.get(i);
                //System.out.println(item.getImgName());
                g.drawImage(new ImageIcon(getClass().getClassLoader().getResource(item.getImgName())).getImage(), item.getX(), item.getY(), this);
            }
        } else if (gameState == GameState.REPLAYOVER){
            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("background.png")).getImage(), 0, 0, getWidth(), getHeight(), this);
            //System.out.println(record.size());
            for (int i = 0; i < record.size(); i++) {
                Record item = (Record) record.get(i);
                //System.out.println(item.getImgName());
                g.drawImage(new ImageIcon(getClass().getClassLoader().getResource(item.getImgName())).getImage(), item.getX(), item.getY(), this);
            }
        }
        if ((gameState != GameState.BEGIN) && (gameState != GameState.REPLAYING) && (gameState != GameState.REPLAYOVER)) {
            for (int i = 0; i < world.size(); i++) {

                Thing2D item = (Thing2D) world.get(i);

                if (item instanceof Creature) {
                    g.drawImage(item.getImage(), item.x(), item.y(), this);
                } else if (item instanceof Grandpa) {
                    g.drawImage(item.getImage(), item.x(), item.y(), this);
                } else {
                    //g.drawImage(item.getImage(), item.x(), item.y(), this);
                }
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

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) { // Start game
                if (gameState == GameState.BEGIN) {
                    gameState = GameState.PLAYING;
                    checkGameOverTimer = new Timer();
                    checkGameOverTimer.scheduleAtFixedRate(new TimerTask() { // after 30 sec, must end the war
                        @Override
                        public void run() {
                            exec.shutdownNow();
                            gameState = GameState.PEACE;
                            checkGameOverTimer.cancel();
                        }
                    }, 30000L, 30000L);
                    exec.execute(recorder); // record file
                    for (Huluwa hlw : huluwas)
                        exec.execute(hlw);
                    for (Frog frog : frogs) {
                        exec.execute(frog);
                    }
                    exec.execute(grandpa);
                    exec.execute(snake);
                    exec.execute(scorpion);
                }
            } else if (key == KeyEvent.VK_R) { // Restart game
                exec.shutdownNow();
                checkGameOverTimer.cancel();
                if (gameState == GameState.OVER){
                    // not save record file
                    recorder.clearCurrentRecord();
                }
                restartLevel();
            } else if (key == KeyEvent.VK_L) { // Load Record
                if (gameState == GameState.BEGIN){
                    // load the file
                    initReplayer();
                    if (replayer.loadRecord()){
                        JOptionPane.showMessageDialog(null, "Load successfully.");
                        // replay the file automatically
                        gameState = GameState.REPLAYING;
                        exec.execute(replayer);
                    } else {
                        JOptionPane.showMessageDialog(null, "Load cancelled.");
                    }
                }
            } else if (key == KeyEvent.VK_S) { // Save Record
                if (gameState == GameState.OVER){
                    // save
                    if (recorder.saveRecordFile()){
                        JOptionPane.showMessageDialog(null, "Save successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Sorry, failed.");
                    }
                    // restart automatically
                    restartLevel();
                }
            }

            repaint();
        }
    }


    public void restartLevel() {

        huluwas.clear();
        frogs.clear();

        initWorld();
        gameState = GameState.BEGIN;
    }

    public ArrayList getWorld() {
        return world;
    }

    public ArrayList getRecord() { return record; }

    public int getSPACE() {
        return SPACE;
    }

    public boolean gameIsOver(){
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
        // is over
        exec.shutdownNow();
        gameState = GameState.OVER;
        checkGameOverTimer.cancel();
        this.repaint();
        return true;
    }

    private boolean huluwasAllDead() {
        for (int i = 0; i < huluwas.size(); i++) {
            Thing2D item = (Thing2D) huluwas.get(i);
            if (item instanceof Huluwa){
                if (((Huluwa) item).isAlive()){
                    return false;
                }
            }
        }
        return true;
    }

    public void setStateREPLAYOVER() {
        this.gameState = GameState.REPLAYOVER;
    }

    private void  initReplayer() {
        record = new ArrayList();
        replayer = new Replayer(this);
    }
}