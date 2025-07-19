import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JTextArea;

public class CenteredTextArea extends JTextArea {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        String text = getText();
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getHeight()) / 2;
        g.drawString(text, x, y);

        super.setText("");
    }
}
