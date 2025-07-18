package model;
import java.io.Serializable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the full game state for Ultimate Tic Tac Toe, including cell owners,
 * sub-board winners, current player, and move history. Supports move logic,
 * win/draw checks, reset, and serialization.
 */
public class GameState implements Serializable {
    private final Player[][][] cells = new Player[9][3][3];       // cell owners per sub-board
    private final Player[] winners = new Player[9];               // winner of each sub-board
    private Player currentPlayer = Player.X;                      // whose turn it is
    private int nextActiveSubBoard = -1;                          // index of forced next sub-board
    private final List<String> moveHistory = new ArrayList<>();   // textual history of moves
    /**
     * Initializes a new empty game state.
     */
    public GameState() {
        reset();
    }
    /**
     * Checks if the game is a draw (all boards full or decided, no winner).
     */
    public boolean isDraw() {
        if (getGameWinner() != Player.NONE) return false;
        for (int sb = 0; sb < 9; sb++) {
            if (winners[sb] == Player.NONE && !isSubBoardFull(sb)) {
                return false; // moves still possible
            }
        }
        return true; // all sub-boards decided or full
    }
    /**
     * Resets the game state to a new empty board.
     */
    public void reset() {
        for (int sb = 0; sb < 9; sb++) {
            winners[sb] = Player.NONE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    cells[sb][r][c] = Player.NONE;
                }
            }
        }
        currentPlayer = Player.X;
        nextActiveSubBoard = -1;
        moveHistory.clear();
    }
    // Simple setters for loading state
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
    public void setNextActiveSubBoard(int subBoard) {
        this.nextActiveSubBoard = subBoard;
    }
    public void setSubBoardWinner(int subBoard, Player winner) {
        winners[subBoard] = winner;
    }
    public void setCell(int subBoard, int row, int col, Player player) {
        cells[subBoard][row][col] = player;
    }
    /**
     * Attempts to place the current player's mark in the specified cell.
     * Returns true if the move was valid and updates game state.
     */
    public boolean makeMove(int subBoard, int row, int col) {
        if (isGameOver() || winners[subBoard] != Player.NONE ||
                cells[subBoard][row][col] != Player.NONE ||
                (nextActiveSubBoard != -1 && subBoard != nextActiveSubBoard)) {
            return false;
        }
        cells[subBoard][row][col] = currentPlayer;
        if (checkWin(cells[subBoard], currentPlayer)) {
            winners[subBoard] = currentPlayer;
        }
        Player globalWinner = getGameWinner();
        if (globalWinner != Player.NONE) {
            nextActiveSubBoard = -2; // game over
        } else {
            int next = row * 3 + col;
            nextActiveSubBoard = (!isSubBoardFull(next) && winners[next] == Player.NONE) ? next : -1;
            currentPlayer = currentPlayer.opposite();
        }
        moveHistory.add(currentPlayer.opposite() + " → board " + subBoard + ", box " + (row*3+col));
        return true;
    }
    /**
     * Checks if a sub-board is completely filled.
     */
    public boolean isSubBoardFull(int sb) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (cells[sb][r][c] == Player.NONE) return false;
            }
        }
        return true;
    }
    /**
     * Checks if the overall game has a winner.
     */
    public boolean isGameOver() {
        return checkWin(getVirtualBoard(), Player.X) || checkWin(getVirtualBoard(), Player.O);
    }
    // Simple getters
    public Player getCell(int sb, int r, int c) { return cells[sb][r][c]; }
    public Player getSubBoardWinner(int sb) { return winners[sb]; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public int getNextActiveSubBoard() { return nextActiveSubBoard; }
    public List<String> getMoveHistory() { return moveHistory; }
    /**
     * Builds a 3x3 virtual board of sub-board winners for global win check.
     */
    private Player[][] getVirtualBoard() {
        Player[][] virt = new Player[3][3];
        for (int sb = 0; sb < 9; sb++) {
            virt[sb/3][sb%3] = winners[sb];
        }
        return virt;
    }
    /**
     * Checks if the given 3x3 board has three in a row for player p.
     */
    private boolean checkWin(Player[][] board, Player p) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0]==p && board[i][1]==p && board[i][2]==p) return true;
            if (board[0][i]==p && board[1][i]==p && board[2][i]==p) return true;
        }
        return (board[0][0]==p && board[1][1]==p && board[2][2]==p) ||
                (board[0][2]==p && board[1][1]==p && board[2][0]==p);
    }
    /**
     * Copies state from another GameState (used for loading).
     */
    public void copyFrom(GameState other) {
        for (int sb = 0; sb < 9; sb++) {
            winners[sb] = other.winners[sb];
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    cells[sb][r][c] = other.cells[sb][r][c];
                }
            }
        }
        currentPlayer = other.currentPlayer;
        nextActiveSubBoard = other.nextActiveSubBoard;
    }
    /**
     * Determines the overall game winner by checking the virtual board.
     */
    public Player getGameWinner() {
        Player[][] virt = getVirtualBoard();
        for (int i = 0; i < 3; i++) {
            if (virt[i][0]!=Player.NONE && virt[i][0]==virt[i][1] && virt[i][0]==virt[i][2]) return virt[i][0];
            if (virt[0][i]!=Player.NONE && virt[0][i]==virt[1][i] && virt[0][i]==virt[2][i]) return virt[0][i];
        }
        if (virt[0][0]!=Player.NONE && virt[0][0]==virt[1][1] && virt[0][0]==virt[2][2]) return virt[0][0];
        if (virt[0][2]!=Player.NONE && virt[0][2]==virt[1][1] && virt[0][2]==virt[2][0]) return virt[0][2];
        return Player.NONE;
    }
    /**
     * Serializes the entire GameState to a file.
     */
    public void saveToFile(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        }
    }
    /**
     * Loads a serialized GameState from a file.
     */
    public static GameState loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (GameState) in.readObject();
        }
    }
}
