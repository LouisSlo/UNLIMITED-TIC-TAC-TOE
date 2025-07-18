package view;

import model.GameState;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * GamePanel handles rendering and user interaction for the Ultimate Tic Tac Toe board.
 * It draws the main board image, sub-board cells (X/O), and highlights available moves.
 * It also processes mouse clicks to place moves and handles game-over dialogs.
 */
public class GamePanel extends JPanel {
    private final GameState state;
    private final BufferedImage boardImg;
    private final BufferedImage xImg;
    private final BufferedImage oImg;
    private final Runnable onMoveMade;

    // Original board dimensions, used as reference for scaling
    private final int ORIGINAL_BOARD_W = 1025;
    private final int ORIGINAL_BOARD_H = 1025;
    private final int ORIGINAL_MARGIN = 17;

    /**
     * Constructs a GamePanel.
     * state the GameState holding game logic and current state
     * boardImg the background image of the full board
     * xImg the image for X marks
     * oImg the image for O marks
     * onMoveMade callback to invoke after a successful move (e.g., to refresh history)
     */
    public GamePanel(GameState state,
                     BufferedImage boardImg,
                     BufferedImage xImg,
                     BufferedImage oImg,
                     Runnable onMoveMade) {
        this.state = state;
        this.boardImg = boardImg;
        this.xImg = xImg;
        this.oImg = oImg;
        this.onMoveMade = onMoveMade;
        setOpaque(false);

        // Handle mouse clicks for placing moves
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }
    /**
     * Translates a mouse click into a move on the board. Scales coordinates,
     * determines sub-board and cell indices, and applies the move if valid.
     */
    private void handleMouseClick(int x, int y) {
        // Determine scale factor based on panel size versus original image size
        double scale = Math.min(getWidth() / (double) ORIGINAL_BOARD_W,
                getHeight() / (double) ORIGINAL_BOARD_H);
        int boardW = (int) (ORIGINAL_BOARD_W * scale);
        int boardH = (int) (ORIGINAL_BOARD_H * scale);
        int boardX = (getWidth() - boardW) / 2;
        int boardY = (getHeight() - boardH) / 2;
        int margin = (int) (ORIGINAL_MARGIN * scale);

        // Calculate cell dimensions
        int offsetX = boardX + margin;
        int offsetY = boardY + margin;
        double cellW = (boardW - 2.0 * margin) / 9.0;
        double cellH = (boardH - 2.0 * margin) / 9.0;

        // Convert click coords into board-local coords
        x -= offsetX;
        y -= offsetY;
        if (x < 0 || y < 0 || x >= cellW * 9 || y >= cellH * 9) return;

        // Identify sub-board and cell indices
        int col = (int) (x / cellW);
        int row = (int) (y / cellH);
        int subBoard = (row / 3) * 3 + (col / 3);
        int r = row % 3;
        int c = col % 3;

        // Attempt the move
        if (state.makeMove(subBoard, r, c)) {
            repaint();            // Redraw board
            onMoveMade.run();     // Notify history panel

            // Check for game over: win or draw
            if (state.getGameWinner() != Player.NONE) {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Player " + state.getGameWinner() + " won! Restart?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    state.reset();
                    repaint();
                } else {
                    System.exit(0);
                }
            } else if (state.isDraw()) {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "It's a draw! Restart?",
                        "Draw",
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    state.reset();
                    repaint();
                } else {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Paints the board, sub-boards, X/O marks, and highlights available moves.
     * Dynamically scales all elements to fit the panel size.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Compute scaled dimensions and margins
        double scale = Math.min(getWidth() / (double) ORIGINAL_BOARD_W,
                getHeight() / (double) ORIGINAL_BOARD_H);
        int boardW = (int) (ORIGINAL_BOARD_W * scale);
        int boardH = (int) (ORIGINAL_BOARD_H * scale);
        int boardX = (getWidth() - boardW) / 2;
        int boardY = (getHeight() - boardH) / 2;
        int margin = (int) (ORIGINAL_MARGIN * scale);

        // Gap between sub-boards (3 wide, 2 gaps horizontally)
        int subGap = (int) (boardW * 0.03 / 2);
        double cellW = (boardW - 2 * margin - subGap * 2) / 9.0;
        double cellH = (boardH - 2 * margin - subGap * 2) / 9.0;

        // Draw the main board image
        g.drawImage(boardImg, boardX, boardY, boardW, boardH, this);

        int offsetX = boardX + margin;
        int offsetY = boardY + margin;

        // Loop through each sub-board
        for (int sb = 0; sb < 9; sb++) {
            int sbRow = sb / 3;
            int sbCol = sb % 3;
            int subX = offsetX + sbCol * ((int)(3 * cellW) + subGap);
            int subY = offsetY + sbRow * ((int)(3 * cellH) + subGap);

            Player winner = state.getSubBoardWinner(sb);
            if (winner != Player.NONE) {
                // Draw a large X or O when sub-board is won
                BufferedImage img = (winner == Player.X ? xImg : oImg);
                g.drawImage(img, subX, subY,
                        (int)(3 * cellW), (int)(3 * cellH), this);
            } else {
                // Draw individual cells
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        Player p = state.getCell(sb, r, c);
                        if (p != Player.NONE) {
                            BufferedImage img = (p == Player.X ? xImg : oImg);
                            int xPos = subX + (int)(c * cellW);
                            int yPos = subY + (int)(r * cellH);
                            g.drawImage(img, xPos, yPos,
                                    (int)cellW, (int)cellH, this);
                        }
                    }
                }
            }
        }

        // Highlight available moves in the active sub-board(s)
        int active = state.getNextActiveSubBoard();
        for (int sb = 0; sb < 9; sb++) {
            if (active == -1 || active == sb) {
                if (state.getSubBoardWinner(sb) != Player.NONE) continue;

                int sbRow = sb / 3;
                int sbCol = sb % 3;
                int subX = offsetX + sbCol * ((int)(3 * cellW) + subGap);
                int subY = offsetY + sbRow * ((int)(3 * cellH) + subGap);

                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        if (state.getCell(sb, r, c) == Player.NONE) {
                            int xPos = subX + (int)(c * cellW);
                            int yPos = subY + (int)(r * cellH);
                            g.setColor(new Color(255, 255, 0, 100));
                            g.fillRect(xPos, yPos,
                                    (int)cellW, (int)cellH);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the underlying GameState for external access.
     */
    public GameState getGameState() {
        return state;
    }

    /**
     * Simulates a move at the given cell index (0-8) in the active or first available sub-board.
     * Useful for keyboard-driven input.
     */
    public void simulateMove(int cellIndex) {
        if (state.getGameWinner() != Player.NONE) return;

        int sb = state.getNextActiveSubBoard();
        if (sb == -1) {
            // Find the first sub-board where this cell is free
            for (int i = 0; i < 9; i++) {
                if (state.getSubBoardWinner(i) == Player.NONE &&
                        state.getCell(i, cellIndex / 3, cellIndex % 3) == Player.NONE) {
                    sb = i;
                    break;
                }
            }
            if (sb == -1) return;
        }

        int r = cellIndex / 3;
        int c = cellIndex % 3;
        if (state.makeMove(sb, r, c)) {
            repaint();
            onMoveMade.run();

             // Handle win or draw messages
            if (state.getGameWinner() != Player.NONE) {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Player " + state.getGameWinner() + " won the game! Restart?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    state.reset();
                    onMoveMade.run(); // update history
                    repaint();
                } else {
                    System.exit(0);
                }
            } else if (state.isDraw()) {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Game ended in a draw! Restart?",
                        "Draw",
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    state.reset();
                    repaint();
                } else {
                    System.exit(0);
                }
            }
        }
    }
}
