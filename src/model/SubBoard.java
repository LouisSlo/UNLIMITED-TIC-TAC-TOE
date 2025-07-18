package model;

/**
 * Represents a 3×3 tic-tac-toe sub-board.
 * Tracks individual cells, determines if it's won or full.
 */
public class SubBoard {
    public static final int SIZE = 3;            // dimension of sub-board
    private final Cell[] cells;                  // flat array of SIZE*SIZE cells
    private Player winner = Player.NONE;         // current winner of this sub-board
    private boolean full = false;                // whether all cells are filled

    /**
     * Initializes empty cells for this sub-board.
     */
    public SubBoard() {
        cells = new Cell[SIZE * SIZE];
        for (int i = 0; i < SIZE * SIZE; i++) {
            cells[i] = new Cell();
        }
    }

    /**
     * Returns the Cell at the given index (0-8).
     */
    public Cell getCell(int idx) {
        return cells[idx];
    }

    /**
     * Gets the current winner of this sub-board (NONE if no winner yet).
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Manually sets the sub-board winner (used when loading state).
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Returns true if the sub-board has no empty cells.
     */
    public boolean isFull() {
        return full;
    }

    /**
     * Updates winner and full status based on current cells.
     * Checks all three-in-a-row lines; if found, sets winner.
     * Otherwise marks full only if no empty cells remain.
     */
    public void updateStatus() {
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // columns
                {0, 4, 8}, {2, 4, 6}              // diagonals
        };
        // Check each win line
        for (int[] line : lines) {
            Player p = cells[line[0]].getOwner();
            if (p != Player.NONE
                    && p == cells[line[1]].getOwner()
                    && p == cells[line[2]].getOwner()) {
                winner = p;
                return;
            }
        }
        // No winner: check if all cells are occupied
        full = true;
        for (Cell c : cells) {
            if (c.isEmpty()) {
                full = false;
                break;
            }
        }
    }
}
