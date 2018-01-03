

//鶴翼形
public class HuluwaWarFormation implements Formation {
    private StringBuilder level;
    private int n;
    private int m;
    @Override
    public void format(StringBuilder level, int n, int m){
        this.level = level;
        this.n = n;
        this.m = m;
        //put huluwas identifies '1' - '7'
        int redRow = n-4, redCol = 3;
        int row = redRow, col = redCol;
        for (int i=0; i<7; i++){
            if (isValidPosition(row, col)) {
                level.setCharAt(row * (m+1/*\n*/) + col, Integer.toString(i + 1).charAt(0));
            }
            if (i%2==0){
                row = redRow - (i/2+1);
                col--;
            } else {
                row = redRow + (i/2+1);
            }
        }
        //put grandpa identify 'g'
        row = redRow;
        col = 0;
        if (isValidPosition(row, col)) {
            level.setCharAt(row * (m+1/*\n*/) + col, 'g');
        }
        //put scorpion
        row = redRow;
        col = m-5;
        if (isValidPosition(row, col)){
            level.setCharAt(row * (m+1/*\n*/) + col, 'x');
        }
        //put frogs
        if (isValidPosition(row-1, col+1)){level.setCharAt((row-1) * (m+1/*\n*/) + (col+1), 'a');}
        if (isValidPosition(row+1, col+1)){level.setCharAt((row+1) * (m+1/*\n*/) + (col+1), 'b');}
        if (isValidPosition(row-2, col+2)){level.setCharAt((row-2) * (m+1/*\n*/) + (col+2), 'c');}
        if (isValidPosition(row+2, col+2)){level.setCharAt((row+2) * (m+1/*\n*/) + (col+2), 'd');}
        if (isValidPosition(row-1, col+3)){level.setCharAt((row-1) * (m+1/*\n*/) + (col+3), 'e');}
        if (isValidPosition(row+1, col+3)){level.setCharAt((row+1) * (m+1/*\n*/) + (col+3), 'f');}
        //put snake
        if (isValidPosition(row, col+4)){level.setCharAt(row * (m+1/*\n*/) + (col+4), 's');}
    }

    private boolean isValidPosition(int row, int col){
        if ((0 < row*(m+1)+col) && (row*(m+1)+col < (m+1)*n) && (level.charAt(row*(m+1)+col)==' ')) {
            return true;
        } else {
            return false;
        }
    }

}
