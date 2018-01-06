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
import java.util.concurrent.TimeUnit;


public class Field extends JPanel {

    private final int OFFSET = 10;
    private final int SPACE = 70;
    private final int N = 8; // row, should be no less than 8 because of the formation
    private final int M = 12; // col, should be no less than 9 because of the formation

    private ExecutorService exec;

    private GameState gameState;

    private RecordBuffer recordBuffer; // for repaint and replayer
    //private ArrayList record;

    private Recorder recorder;
    private Replayer replayer;

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
        initReplayer();
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
        g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("background.png")).getImage(), 0, 0, getWidth(), getHeight(), this);

        if ((gameState != GameState.BEGIN) && (gameState != GameState.REPLAYING) && (gameState != GameState.REPLAYOVER)) {
            for (int i = 0; i < world.size(); i++) {

                Thing2D item = (Thing2D) world.get(i);

                if (item instanceof Creature) {
                    if (((Creature) item).isAlive()) {
                        g.drawImage(item.getImage(), item.x(), item.y(),SPACE, SPACE, this);
                    } else {
                        g.drawImage(item.getImage(), item.x(), item.y(), this);
                    }
                } else {
                    //g.drawImage(item.getImage(), item.x(), item.y(), this);
                }
            }
        }

        if (gameState == GameState.BEGIN) {

            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("beginBackground.png")).getImage(), 0, 0, getWidth(), getHeight(), this);

        } else if (gameState == GameState.PLAYING) {

            // nothing
        } else if (gameState == GameState.PEACE){

            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("overHint.png")).getImage(), 20, 20, getWidth()/4, getHeight()/4, this);
            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("peaceHint.png")).getImage(), getWidth()/4, getHeight()/6, getWidth()/2, getHeight()/6, this);

        } else if (gameState == GameState.OVER) {

            g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("overHint.png")).getImage(), 20, 20, getWidth()/4, getHeight()/4, this);
            if (huluwasAllDead()) {
                g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("badWinHint.png")).getImage(), getWidth()/4, getHeight()/6, getWidth()/2, getHeight()/6, this);
            } else {
                g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("huluwaWinHint.png")).getImage(), getWidth()/4, getHeight()/6, getWidth()/2, getHeight()/6, this);
            }

        } else if (gameState == GameState.REPLAYING || gameState == GameState.REPLAYOVER){

            ArrayList tempRecords = new ArrayList();
            try {
                recordBuffer.waitForSaving();
                tempRecords =  recordBuffer.getRecords();
                //System.out.println(tempRecords.size());
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            for (int i = 0; i < tempRecords.size(); i++) {
                Record item = (Record) tempRecords.get(i);
                //System.out.println(item.getImgName());
                if (item.getImgName().endsWith("DEAD.png")){
                    g.drawImage(new ImageIcon(getClass().getClassLoader().getResource(item.getImgName())).getImage(), item.getX(), item.getY(), this);
                } else {
                    g.drawImage(new ImageIcon(getClass().getClassLoader().getResource(item.getImgName())).getImage(), item.getX(), item.getY(), SPACE, SPACE, this);
                }
            }
            if (gameState == GameState.REPLAYOVER){
                exec.shutdownNow();
                g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("replayOverHint.png")).getImage(), 20, 20, getWidth()/4, getHeight()/8, this);
                int replayOverResult = Record.checkReplayOverResult(tempRecords);
                if (replayOverResult==1) { // huluwa win
                    g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("huluwaWinHint.png")).getImage(), getWidth()/4, getHeight()/6, getWidth()/2, getHeight()/6, this);
                } else if (replayOverResult==0) { // peace
                    g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("peaceHint.png")).getImage(), getWidth()/4, getHeight()/6, getWidth()/2, getHeight()/6, this);
                } else { // evil win
                    g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("badWinHint.png")).getImage(), getWidth()/4, getHeight()/6, getWidth()/2, getHeight()/6, this);
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
                        public void run() { // force to end the war after 30 sec
                            if (gameState == GameState.PLAYING) {
                                exec.shutdownNow();
                                gameState = GameState.PEACE;
                            }
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
                    repaint();
                }
            } else if (key == KeyEvent.VK_R) { // Restart game
                if (gameState == GameState.REPLAYING) {
                    exec.shutdownNow();
                    gameState = GameState.BEGIN;
                    restartLevel();
                    repaint();
                } else {
                    exec.shutdownNow();
                    checkGameOverTimer.cancel();
                    if (gameState == GameState.OVER) {
                        // not save record file
                        recorder.clearCurrentRecord();
                    }
                    restartLevel();
                    repaint();
                }
            } else if (key == KeyEvent.VK_L) { // Load Record
                if (gameState == GameState.BEGIN){
                    // load the file
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
                    repaint();
                }
            }


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
        try {
            exec.shutdown();
            exec.awaitTermination(500, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            //System.err.println("termination interrupted");
        }
        finally {
            if (!exec.isTerminated()) {
                //System.err.println("killing non-finished tasks");
            }
            exec.shutdownNow();
        }

        //exec.shutdownNow();
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
        ArrayList records = new ArrayList();
        recordBuffer = new RecordBuffer(this, records);
        replayer = new Replayer(this, recordBuffer);
    }

}