package view;

import model.GameState;

import javax.swing.*;
import java.awt.*;

/**
 * MovesPanel displays the history of moves made in the game.
 * It renders a rounded, semi-transparent panel containing a scrollable list of moves.
 */

public class MovesPanel extends JPanel {
    private final GameState state;
    private final JTextArea area;

    /**
     * Constructs a new MovesPanel.
     *  state the GameState object from which to read the move history
     */
    public MovesPanel(GameState state) {
        this.state = state;

        // Make the panel transparent and use a border layout with padding
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a sub-panel with rounded corners and a semi-transparent background
        JPanel roundedPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Enable anti-aliasing for smooth edges
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw semi-transparent white background with rounded corners
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        roundedPanel.setOpaque(false);
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create and configure the title label
        JLabel title = new JLabel("Move History:");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setOpaque(false);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        roundedPanel.add(title, BorderLayout.NORTH);

        // Create and configure the text area for displaying moves
        area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setBackground(new Color(0, 0, 0, 0));

        // Place the text area inside a scroll pane
        JScrollPane scroll = new JScrollPane(area);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        roundedPanel.add(scroll, BorderLayout.CENTER);

        // Add the rounded sub-panel to this MovesPanel
        add(roundedPanel, BorderLayout.CENTER);
    }

    /**
     * Refreshes the displayed move history.
     * Call this method after any move is made to update the list.
     */
    public void refresh() {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (String move : state.getMoveHistory()) {
            sb.append(index++).append(". ").append(move).append("\n");
        }
        area.setText(sb.toString());
    }
}