package view;

import model.GameState;
import persistence.GamePersistence;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
/**
 * MainMenu provides the application's entry screen with options to Start a new game,
 * Continue from a saved game, or Quit the application.
 */
public class MainMenu extends JFrame {
    // Path to the save file used by Continue
    private static final File SAVE_FILE = new File("save.txt");
    /**
     * Constructs and displays the main menu window.
     * Sets up the background, logo, and menu buttons.
     */
    public MainMenu() {
        setTitle("Unlimited Tic Tac Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1655, 900));
        setLocationRelativeTo(null);

        // Use a custom JPanel to draw the background image scaled to fit
        setContentPane(new BackgroundPanel("/Background.jpg"));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 0, 20, 0);

        // Load and display the game logo at the top
        ImageIcon logoIcon = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/Logo.png"))
        );
        JLabel title = new JLabel(logoIcon);
        gbc.gridy = 0;
        add(title, gbc);

        // Button to start a brand new game
        addMenuButton("START", gbc, 1, () -> {
            dispose();  // close menu
            SwingUtilities.invokeLater(GameUI::new);  // open game window
        });

        // Button to continue from last saved state
        addMenuButton("CONTINUE", gbc, 2, () -> {
            if (!SAVE_FILE.exists()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No saved game found. Please save a game first.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            try {
                // Load saved state from text file
                GameState loaded = GamePersistence.loadFromText(SAVE_FILE);
                dispose();  // close menu
                new GameUI(loaded);  // open game with loaded state
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to load game:\n" + ex.getMessage(),
                        "Loading Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        // Button to exit the application
        addMenuButton("QUIT", gbc, 3, () -> System.exit(0));

        setVisible(true);
    }
    /**
     * Helper method to create and add a rounded button to the menu.
     *
     * text label shown on the button
     * gbc layout constraints to position the button
     * y grid y-coordinate for placement
     *  action to perform when button is clicked
     */
    private void addMenuButton(String text, GridBagConstraints gbc, int y, Runnable action) {
        RoundedButton button = new RoundedButton(text);
        button.setPreferredSize(new Dimension(300, 50));
        gbc.gridy = y;
        add(button, gbc);
        button.addActionListener(e -> action.run());
    }
    /**
     * BackgroundPanel is a custom JPanel that draws a scaled background image
     * to fill the entire panel area.
     */
    static class BackgroundPanel extends JPanel {
        private final Image background;
        /**
         * Loads the background image from the given resource path.
         * path resource path to the image file
         */
        public BackgroundPanel(String path) {
            background = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource(path))
            ).getImage();
        }
        /**
         * Paints the background image scaled to the panel's current size.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
    /**
     * Entry point of the application. Launches the MainMenu on the Event Dispatch Thread.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}