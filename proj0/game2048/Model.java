package game2048;

import java.util.*;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        board.setViewingPerspective(side);                      //视角设置为当前
        for(int col=0;col<board.size();col++){
            boolean local_changed = columnProcess(board,col);   //对每列进行列处理，并且返回是否改变
            if(local_changed){
                changed=true;
            }
        }
        board.setViewingPerspective(Side.NORTH);                //转到NORTH方向，这样只需完成向上操作
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    public boolean columnProcess(Board b, int col){             //列处理函数
        boolean changed=false;                                  //首先假设无改变
        int height=b.size();                                    //height变量为了剔除已合并过的值
        for(int row=height-1;row>=0;row--){                     //从上到下遍历
            if(b.tile(col,row)==null){                          //如果当前块没有值，continue
                continue;
            }
            int row_index=rowIndex(b,col,row,height);           //得到目的地的行索引
            if(row!=row_index){                                 //如果会发生移动
                changed=true;
                if(b.tile(col,row_index)!=null){                //如果将合并
                    height-=1;                                  //剔除当前top行(因为该值之后将不能继续合并)
                    score+=b.tile(col,row_index).value()*2;     //只有合并才会增加分数，分数增加为值的2倍
                }
            }
            Tile t=b.tile(col,row);
            b.move(col, row_index, t);                          //移动操作
        }
        return changed;                                         //若移动changed=true,否则false
    }

    public int rowIndex(Board b, int col, int row,int height){  //得到目的地行索引
        if(row==height-1 || b.tile(col,row)==null){             //如果当前行为top或当前块为空，不会移动，row不变
            return row;
        }else{
            int row_index=row;                                  //初值为当前行，若该行之上无空值或相同值，返回原行
            for(int j=row+1;j<height;j++){                      //该行之上遍历，直到当前最高行
                if(b.tile(col,j)==null || b.tile(col,row).value()==b.tile(col,j).value()){   //如果有空值或存在相同值(合并)，则为该行
                    row_index=j;
                }
            }
            return row_index;
        }
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for(int col=0;col<b.size();col++){
            for(int row=0;row<b.size();row++){
                if(b.tile(col,row)==null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for(int col=0;col<b.size();col++){
            for(int row=0;row<b.size();row++){
                if(b.tile(col,row)!=null){
                    if(b.tile(col,row).value()==MAX_PIECE){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if(emptySpaceExists(b)){
            return true;
        }
        for(int col=0,row=0;col<b.size()-1;col++){
            if(b.tile(col,row).value()==b.tile(col+1,row).value()){
                return true;
            }
        }
        for(int col=0,row=0;row<b.size()-1;row++){
            if(b.tile(col,row).value()==b.tile(col,row+1).value()){
                return true;
            }
        }
        for(int col=1;col<b.size();col++){
            for(int row=1;row<b.size();row++){
                if(b.tile(col,row).value()==b.tile(col-1,row).value() || b.tile(col,row).value()==b.tile(col,row-1).value()){
                    return true;
                }
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
