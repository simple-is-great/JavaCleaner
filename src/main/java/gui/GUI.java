package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class GUI extends JFrame implements ActionListener{
	/* final variable */
	// Basic properties
	public static final int
			WIDTH	= 1080,
			HEIGHT	= 720,
			GAB		= 20,
			SUB_GAB	= 10;
	
	// Left, right panel size
	public static final int LEFT_W	= 200;
	public static final int RIGHT_W	= WIDTH - (GAB*3) - LEFT_W;
	// Current Height
	public static final int CUR_H	= HEIGHT - (GAB*2);
	
	// Interactable component width
	public static final int L_BTN_W	= LEFT_W - (SUB_GAB*2);
	public static final int TEXT_W	= L_BTN_W;
	
	// Interactable component height
	public static final int INPOS	= 115;
	public static final int COMP_H	= 35;
	
	// JComboBox size
	public static final int
			MONTH_W = 60,
			DAY_W	= 45,
			YEAR_W	= 55;
	
	// JButton value for ActionListener
	public static final String
			FSEARCH	= "File Search",
			SPACE	= "Space Maker",
			BACKUP	= "File Backup",
			SEARCH	= "SEARCH",
			DELETE	= "DELETE",
			DUPL	= "Duplicates",
			BSIZE	= "Big Size",
			OLD		= "Old Files";
	
	/* non_final variable */
	// Current X-axis, Y-axis position
	private int posx = 0;
	private int posy = 0;
	
	// Major panel
	private JPanel mainPanel = new JPanel();
	private RPanel
			leftPanel	= new RPanel(),
			rightPanel	= new RPanel();
	
	// Buttons
	private RButton
			fileSearchBtn	= new RButton(FSEARCH),
			spaceMakerBtn	= new RButton(SPACE),
			fileBackupBtn	= new RButton(BACKUP),
			duplicatesBtn	= new RButton(DUPL),
			bigSizeBtn		= new RButton(BSIZE),
			oldFileBtn		= new RButton(OLD);
	
	private RButton[] searchBtn = new RButton[] {
			new RButton(SEARCH),
			new RButton(SEARCH),
			new RButton(SEARCH) };
	private RButton[] deleteBtn = new RButton[] {
			new RButton(DELETE),
			new RButton(DELETE),
			new RButton("BACKUP") };
	
	// name or path input
	private RTextField[] typeField = new RTextField[] {
			new RTextField(": Type File Name"),
			new RTextField(": Type Dir Path"),
			new RTextField(": Backup Dir Path"),
			new RTextField(": Store Dir Path") };
	
	// date input
	private String months[] = {
			"(Month)", "Jan", "Feb",
			"Mar", "Apr", "May", "Jun", "Jul",
			"Aug", "Sep", "Oct", "Nov", "Dec" };
	private JComboBox<String>
			SMonth	= new JComboBox<>(months),
			SDay	= new JComboBox<>(),
			SYear	= new JComboBox<>(),
			FMonth	= new JComboBox<>(months),
			FDay	= new JComboBox<>(),
			FYear	= new JComboBox<>();
	
	
	/* execute code */
	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.setVisible(true);
	}
	
	public GUI() {
		super("Java Storage Cleaner");
		
		/* set JFrame */
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* set mainPanel */
		mainPanel.setLayout(new BorderLayout(GAB, 0));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(GAB, GAB, GAB, GAB));
		mainPanel.setBackground(Color.GRAY);
		
		/* set leftPanel */
		leftPanel.setLayout(null);
		leftPanel.setPreferredSize(new Dimension(LEFT_W, 0));
		
		// set leftPanel title
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
		JLabel LTitle1 = new JLabel("JAVA STORAGE");
		JLabel LTitle2 = new JLabel("CLEANER");
		LTitle1.setFont(f);
		LTitle2.setFont(f);
		LTitle1.setForeground(Color.DARK_GRAY);
		LTitle2.setForeground(Color.DARK_GRAY);
		LTitle1.setHorizontalAlignment(SwingConstants.RIGHT);
		LTitle2.setHorizontalAlignment(SwingConstants.RIGHT);
		LTitle1.setBounds(0, GAB, L_BTN_W, COMP_H);
		LTitle2.setBounds(0, GAB*2 + SUB_GAB, L_BTN_W, COMP_H);
		posy = GAB*2 + SUB_GAB + COMP_H + GAB;
		
		// set leftPanel buttons
		leftButtonUI(fileSearchBtn);
		leftButtonUI(spaceMakerBtn);
		leftButtonUI(fileBackupBtn);
		
		leftPanel.add(LTitle1);
		leftPanel.add(LTitle2);
		leftPanel.add(fileSearchBtn);
		leftPanel.add(spaceMakerBtn);
		leftPanel.add(fileBackupBtn);
		
		/* set rightPanel */
		rightPanel.setLayout(new CardLayout());
		
		// set changeable page
		JPanel page[] = {
				new JPanel(), new JPanel(),
				new JPanel(), new JPanel() };
		for(JPanel p : page) {
			p.setLayout(null);
			p.setBackground(mainPanel.getBackground());
		}
		for(int i = 1; i < page.length; i++)
			searchBtn[i-1].setActionCommand(SEARCH + Integer.toString(i));
		for(int i = 1; i < page.length; i++)
			deleteBtn[i-1].setActionCommand(DELETE + Integer.toString(i));
		
		// set 0th page
		initPage(page[0]);
		
		// set same component of 1, 2, 3 page
		for(int i = 0; i < page.length-1; i++)
			samePage(page[i+1], searchBtn[i], deleteBtn[i], typeField[i]);
		
		fileSearchPage(page[1]);
		spaceMakerPage(page[2]);
		fileBackupPage(page[3]);
		
		for(int i = 0; i < page.length; i++)
			rightPanel.add(page[i], Integer.toString(i));
		
		/* add to JFrame */
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		add(mainPanel);
	}
	
	private void leftButtonUI(JButton b) {
		Font f = new Font(Font.SANS_SERIF, Font.BOLD, 16);
		b.setFont(f);
		b.setForeground(Color.DARK_GRAY);
		b.setHorizontalAlignment(SwingConstants.LEFT);
		
		posy += SUB_GAB;
		b.setBounds(SUB_GAB, posy, L_BTN_W, COMP_H);
		posy += COMP_H;
		
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		//b.setOpaque(true);
		
		b.addActionListener(this);
	}
	private void initPage(JPanel p) {
		JLabel txt1 = new JLabel("Welcome to Java Storage Cleaner");
		JLabel txt2 = new JLabel("Start clean your PC use left tab");
		txt1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 35));
		txt2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		txt1.setForeground(Color.WHITE);
		txt2.setForeground(Color.WHITE);
		txt1.setHorizontalAlignment(SwingConstants.CENTER);
		txt2.setHorizontalAlignment(SwingConstants.CENTER);
		txt1.setBounds(0, 100, RIGHT_W, 100);
		txt2.setBounds(0, 150, RIGHT_W, 100);
		
		p.add(txt1);
		p.add(txt2);
	}
	private void samePage(JPanel p, JButton search, JButton delete, RTextField text) {
		JLabel RTitle = new JLabel(" Java Storage Cleaner");
		RTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		RTitle.setForeground(Color.WHITE);
		RTitle.setHorizontalAlignment(SwingConstants.LEFT);
		RTitle.setBounds(0, 0, RIGHT_W, 100);
		
		interactableUI(text);
		text.setBounds(0, INPOS, L_BTN_W, COMP_H);
		text.setBorder(BorderFactory.createEmptyBorder(0, SUB_GAB, 0, SUB_GAB));
		
		// show search, delete btn
		interactableBtnUI(search);
		interactableBtnUI(delete);
		search.setBounds(RIGHT_W - 100, INPOS, 100, COMP_H);
		delete.setBounds(RIGHT_W - 100, CUR_H - 63, 100, COMP_H);
		
		// JTable
		RPanel table = new RPanel();
		table.setBounds(0, INPOS + COMP_H + GAB, RIGHT_W,
				CUR_H - (INPOS + COMP_H + GAB) - 63 - GAB);
		
		p.add(RTitle);
		p.add(text);
		p.add(search);
		p.add(delete);
		p.add(table);
	}
	private void interactableUI(JComponent c) {
		c.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		c.setForeground(Color.LIGHT_GRAY);
		c.setBackground(Color.DARK_GRAY);
	}
	private void interactableBtnUI(JButton b) {
		interactableUI(b);
		
		b.setHorizontalAlignment(SwingConstants.CENTER);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		//b.setOpaque(true);
		
		b.addActionListener(this);
	}
	private void setDateUI(JComboBox<String> c) {
		interactableUI(c);
		
		c.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    public int getWidth() { return 0; }
                };
            }
        });
	}
	private void initDate() {
		SDay.addItem("(Day)");
		FDay.addItem("(Day)");
		SYear.addItem("(Year)");
		FYear.addItem("(Year)");
		
		for(int i = 1; i <= 31; i++)
			SDay.addItem(Integer.toString(i));
		for(int i = 1; i <= 31; i++)
			FDay.addItem(Integer.toString(i));
		for(int i = 2024; i >= 1970; i--)
			SYear.addItem(Integer.toString(i));
		for(int i = 2024; i >= 1970; i--)
			FYear.addItem(Integer.toString(i));
		
		setDateUI(SMonth);
		setDateUI(SDay);
		setDateUI(SYear);
		setDateUI(FMonth);
		setDateUI(FDay);
		setDateUI(FYear);
	}
	private void fileSearchPage(JPanel p) {
		// date select
		RPanel DatePanel = new RPanel(15);
		DatePanel.setLayout(null);
		DatePanel.setBackground(Color.DARK_GRAY);
		DatePanel.setBounds(L_BTN_W + (GAB*4), INPOS,
				(SUB_GAB*6) + (MONTH_W + DAY_W + YEAR_W)*2 + GAB*2, COMP_H+1);
		
		JLabel dash = new JLabel("~");
		dash.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		dash.setForeground(Color.LIGHT_GRAY);
		dash.setHorizontalAlignment(SwingConstants.CENTER);
		
		initDate();
		
		SMonth.setBounds(SUB_GAB, 0, MONTH_W, COMP_H);
		SDay.setBounds	(SUB_GAB*2 + MONTH_W, 0, DAY_W, COMP_H);
		SYear.setBounds	(SUB_GAB*3 + MONTH_W + DAY_W, 0, YEAR_W, COMP_H);
		
		posx = SUB_GAB*3 + MONTH_W + DAY_W + YEAR_W;
		dash.setBounds(posx, 0, GAB, COMP_H);
		posx += GAB * 2;
		
		FMonth.setBounds(posx, 0, MONTH_W, COMP_H);
		FDay.setBounds	(posx + SUB_GAB + MONTH_W, 0, DAY_W, COMP_H);
		FYear.setBounds	(posx + SUB_GAB*2 + MONTH_W + DAY_W, 0, YEAR_W, COMP_H);
		
		DatePanel.add(SMonth);
		DatePanel.add(SDay);
		DatePanel.add(SYear);
		
		DatePanel.add(dash);
		
		DatePanel.add(FMonth);
		DatePanel.add(FDay);
		DatePanel.add(FYear);
		
//		p.add(SDatePanel);
//		p.add(dashPanel);
//		p.add(FDatePanel);
		p.add(DatePanel);
	}
	private void spaceMakerPage(JPanel p) {
		interactableBtnUI(duplicatesBtn);
		interactableBtnUI(bigSizeBtn);
		interactableBtnUI(oldFileBtn);
		
		duplicatesBtn.setBounds(SUB_GAB, 0, INPOS+10, COMP_H);
		bigSizeBtn.setBounds(SUB_GAB*2 + INPOS + 10, 0, INPOS-5, COMP_H);
		oldFileBtn.setBounds(SUB_GAB*3 + INPOS*2 + 5, 0, INPOS-5, COMP_H);
		
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(null);
		menuPanel.setBackground(Color.GRAY);
		menuPanel.setBounds(INPOS*2 + GAB*4, INPOS, (SUB_GAB*4) + (INPOS*3) - 5, COMP_H);
		
		menuPanel.add(duplicatesBtn);
		menuPanel.add(bigSizeBtn);
		menuPanel.add(oldFileBtn);
		
		p.add(menuPanel);
	}
	private void fileBackupPage(JPanel p) {
		interactableUI(typeField[3]);
		typeField[3].setBounds(L_BTN_W + GAB, INPOS, L_BTN_W, COMP_H);
		typeField[3].setBorder(BorderFactory.createEmptyBorder(0, SUB_GAB, 0, SUB_GAB));
		
		deleteBtn[2].setBounds(RIGHT_W - 105, CUR_H - 63, 105, COMP_H);
		
		p.add(typeField[3]);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String btnStr = e.getActionCommand();
		CardLayout menu = (CardLayout) rightPanel.getLayout();
		
		switch(btnStr) {
			case FSEARCH:
				clickUI1(fileSearchBtn);
				menu.show(rightPanel, "1");
				break;
			case SPACE:
				clickUI1(spaceMakerBtn);
				menu.show(rightPanel, "2");
				break;
			case BACKUP:
				clickUI1(fileBackupBtn);
				menu.show(rightPanel, "3");
				break;
			case DUPL:
				clickUI2(duplicatesBtn);
				System.out.println("Duplicates");
				break;
			case BSIZE:
				clickUI2(bigSizeBtn);
				System.out.println("Big Size");
				break;
			case OLD:
				clickUI2(oldFileBtn);
				System.out.println("Old File");
				break;
			case SEARCH + "1":
				
				break;
			case SEARCH + "2":
				break;
			case SEARCH + "3":
				break;
			case DELETE + "1":
				break;
			case DELETE + "2":
				break;
			case DELETE + "3":
				break;
			default:
				System.err.println("Unexpected error");
				break;
		}
	}
	
	private void clickUI1(JButton b) {
		Color c = leftPanel.getBackground();
		
		if(fileSearchBtn.getBackground() != c)
			fileSearchBtn.setBackground(c);
		if(spaceMakerBtn.getBackground() != c)
			spaceMakerBtn.setBackground(c);
		if(fileBackupBtn.getBackground() != c)
			fileBackupBtn.setBackground(c);
		
		b.setBackground(Color.LIGHT_GRAY);
	}
	
	private void clickUI2(JButton b) {
		Color c = Color.DARK_GRAY;
		
		if(duplicatesBtn.getBackground() != c)
			interactableUI(duplicatesBtn);
		if(bigSizeBtn.getBackground() != c)
			interactableUI(bigSizeBtn);
		if(oldFileBtn.getBackground() != c)
			interactableUI(oldFileBtn);
		
		b.setBackground(Color.LIGHT_GRAY);
		b.setForeground(c);
	}
}
