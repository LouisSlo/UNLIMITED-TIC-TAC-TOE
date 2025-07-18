package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * RoundedButton is a custom JButton with smooth color transition on hover
 * and fully rounded corners for a modern look.
 */
public class RoundedButton extends JButton {
    private Color baseColor = new Color(50, 50, 50);      // default background color
    private Color hoverColor = new Color(100, 100, 100);  // background on hover
    private Color currentColor = baseColor;               // current displayed color

    /**
     * Creates a button with specified text, custom styling, and hover animation.
     * text the label displayed on the button
     */
    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);      // disable default button background
        setFocusPainted(false);           // remove focus outline
        setForeground(Color.WHITE);
        setFont(new Font("Serif", Font.BOLD, 22));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Animate background color on hover enter/exit
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateTo(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                animateTo(baseColor);
            }
        });
    }

    /**
     * Smoothly interpolates currentColor toward targetColor using a timer.
     * targetColor the color to transition to
     */
    private void animateTo(Color targetColor) {
        Timer timer = new Timer(40, null);
        timer.addActionListener(e -> {
            int r = (currentColor.getRed() * 9 + targetColor.getRed()) / 10;
            int g = (currentColor.getGreen() * 9 + targetColor.getGreen()) / 10;
            int b = (currentColor.getBlue() * 9 + targetColor.getBlue()) / 10;
            currentColor = new Color(r, g, b);
            repaint();

            // Stop when close enough to target
            if (Math.abs(r - targetColor.getRed()) < 2 &&
                    Math.abs(g - targetColor.getGreen()) < 2 &&
                    Math.abs(b - targetColor.getBlue()) < 2) {
                currentColor = targetColor;
                repaint();
                timer.stop();
            }
        });
        timer.start();
    }

    /**
     * Paints the button background with rounded corners and currentColor.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill rounded rectangle
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

        super.paintComponent(g2);
        g2.dispose();
    }
    /**
     * Override to remove the default button border.
     */
    @Override
    protected void paintBorder(Graphics g) {
        // no border
    }
}
