import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class RButton extends JButton{
	private int radius = 15;
	
	public RButton(String text) {
		super(text);
		setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
	}
	
	public RButton(String text, int rad) {
		this(text);
		this.radius = rad;
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background color setting
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // text style
//        graphics.setColor(getForeground());
//        FontMetrics fm = graphics.getFontMetrics();
//        Rectangle rect = new Rectangle(0, 0, getWidth(), getHeight());
//        int x = (rect.width - fm.stringWidth(getText())) / 2;
//        int y = (rect.height - fm.getHeight()) / 2 + fm.getAscent();
//        graphics.drawString(getText(), x, y);

        super.paintComponent(g);
    }
}
