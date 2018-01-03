import javax.swing.JFrame;


public final class Ground extends JFrame {

    private final int OFFSET = 30;


    public Ground() {
        InitUI();
    }

    public void InitUI() {
        Field field = new Field();
        add(field);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(field.getBoardWidth() + 2 * OFFSET,
                field.getBoardHeight() + 2 * OFFSET);
        setLocationRelativeTo(null);
        setTitle("葫芦娃大战 @BingjieZeng");
    }

}