package gui;

import static gui.defaultSet.COMP_H;
import static gui.defaultSet.GAB;
import static gui.defaultSet.L_BTN_W;
import static gui.defaultSet.SUB_GAB;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.SwingConstants;

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

        super.paintComponent(g);
    }
    
    public int leftButtonUI(int posy) {
		Font f = new Font(Font.SANS_SERIF, Font.BOLD, 18);
		this.setFont(f);
		this.setForeground(Color.DARK_GRAY);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		
		posy += GAB*3;
		this.setBounds(SUB_GAB, posy, L_BTN_W, COMP_H + (COMP_H / 3));
		posy += (COMP_H + (COMP_H / 3));
		
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		
		this.addActionListener(new CustomActionListener());
		
		return posy;
	}
    
	public void interactableBtnUI() {
		this.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		this.setForeground(Color.LIGHT_GRAY);
		this.setBackground(Color.DARK_GRAY);
		
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setBorderPainted(false);
		this.setFocusPainted(false);
				
		this.addActionListener(new CustomActionListener());
	}
}
