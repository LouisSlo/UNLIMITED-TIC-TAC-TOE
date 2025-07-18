package view;

import model.GameState;
import persistence.GamePersistence;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Main game window: renders the board, manages input and menu actions.
 */
public class GameUI extends JFrame {
    private final GamePanel gamePanel;
    private final GameState gameState;
    private MovesPanel movesPanel;

    private static final File SAVE_FILE = new File("save.txt");

    /**
     * Default constructor starts a new game.
     */
    public GameUI() {
        this(new GameState());
    }

    /**
     * Constructor for loading a saved game state
     */
    public GameUI(GameState loadedState) {
        this.gameState = loadedState;

        setTitle("UNLIMITED TIC TAC TOE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1655, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load board, markers and background images
        BufferedImage boardImg = null, xImg = null, oImg = null, backgroundImg = null;
        try {
            boardImg = ImageIO.read(getClass().getResourceAsStream("/Plansza4.png"));
            xImg = ImageIO.read(getClass().getResourceAsStream("/x.png"));
            oImg = ImageIO.read(getClass().getResourceAsStream("/0.png"));
            backgroundImg = ImageIO.read(getClass().getResourceAsStream("/background_game5.png"));
        } catch (IOException | NullPointerException e) {
            System.err.println("Błąd wczytywania obrazów: " + e.getMessage());
            System.exit(1);
        }

        // Background panel scales the image
        BufferedImage finalBackgroundImg = backgroundImg;
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBackgroundImg != null) {
                    g.drawImage(finalBackgroundImg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Game and history panels
        gamePanel = new GamePanel(gameState, boardImg, xImg, oImg, this::onMoveMade);
        gamePanel.setPreferredSize(new Dimension(750, 740));

        movesPanel = new MovesPanel(gameState);
        // Split pane holds game and history
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gamePanel, movesPanel);
        split.setDividerLocation(1300); // bliżej planszy
        split.setOpaque(false);
        backgroundPanel.add(split, BorderLayout.CENTER);

        setupKeyboardControls(); // enable numpad keys
        setupMenu(); // add top menu
        setVisible(true);
    }

    /**
     * Callback after a move: refresh move history panel.
     */
    private void onMoveMade() {
        movesPanel.refresh();
    }

    /**
     * Configure keyboard input for numpad moves.
     */
    private void setupKeyboardControls() {
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int cell = switch (key) {
                    case KeyEvent.VK_NUMPAD1, KeyEvent.VK_1 -> 6;
                    case KeyEvent.VK_NUMPAD2, KeyEvent.VK_2 -> 7;
                    case KeyEvent.VK_NUMPAD3, KeyEvent.VK_3 -> 8;
                    case KeyEvent.VK_NUMPAD4, KeyEvent.VK_4 -> 3;
                    case KeyEvent.VK_NUMPAD5, KeyEvent.VK_5 -> 4;
                    case KeyEvent.VK_NUMPAD6, KeyEvent.VK_6 -> 5;
                    case KeyEvent.VK_NUMPAD7, KeyEvent.VK_7 -> 0;
                    case KeyEvent.VK_NUMPAD8, KeyEvent.VK_8 -> 1;
                    case KeyEvent.VK_NUMPAD9, KeyEvent.VK_9 -> 2;
                    default -> -1;
                };
                if (cell != -1) gamePanel.simulateMove(cell);
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }

    /**
     * Build and style the application menu (Save, Load, Restart, Main Menu).
     */
    private void setupMenu() {
        // Global menu item colors for JMenuItem
        UIManager.put("MenuItem.background", new Color(60, 60, 60));
        UIManager.put("MenuItem.foreground", Color.WHITE);
        UIManager.put("MenuItem.selectionBackground", new Color(100, 100, 100));
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);

        //Pasek menu
        JMenuBar bar = new JMenuBar();
        bar.setOpaque(true);
        bar.setBackground(Color.BLACK);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.DARK_GRAY));

        //przycisk MENU
        JMenu file = new JMenu("MENU");
        file.setOpaque(true);
        file.setBackground(new Color(45, 45, 45)); // tło rozwijanego menu
        file.setForeground(Color.WHITE);           // kolor tekstu
        file.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // padding

        // Pozycje w menu
        JMenuItem save = new JMenuItem("SAVE");
        save.addActionListener(e -> {
            try {
                GamePersistence.saveAsText(gameState, SAVE_FILE);
                JOptionPane.showMessageDialog(this, "Zapisano grę.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd zapisu: " + ex.getMessage());
            }
        });

        JMenuItem load = new JMenuItem("LOAD GAME");
        load.addActionListener(e -> {
            try {
                GameState loaded = GamePersistence.loadFromText(SAVE_FILE);
                dispose();
                new GameUI(loaded);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "LOAD GAME ERROR " + ex.getMessage());
            }
        });

        JMenuItem restart = new JMenuItem("RESTART");
        restart.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "RESTART THE GAME?", "RESTART", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                dispose();
                new GameUI();
            }
        });

        JMenuItem back = new JMenuItem("MAIN MENU");
        back.addActionListener(e -> {
            dispose();
            new MainMenu();
        });

        // Składanie menu
        file.add(save);
        file.add(load);
        file.add(restart);
        file.add(back);
        bar.add(file);
        setJMenuBar(bar);
    }

    public GameState getGameState() {
        return gameState;
    }
}