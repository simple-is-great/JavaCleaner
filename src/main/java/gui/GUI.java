package gui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import static gui.componentSet.EDay;
import static gui.componentSet.EMonth;
import static gui.componentSet.EYear;
import static gui.componentSet.SDay;
import static gui.componentSet.SMonth;
import static gui.componentSet.SYear;
import static gui.componentSet.allBtn;
import static gui.componentSet.bigSizeBtn;
import static gui.componentSet.deleteBtn;
import static gui.componentSet.duplicatesBtn;
import static gui.componentSet.fileBackUpBtn;
import static gui.componentSet.fileSearchBtn;
import static gui.componentSet.leftPanel;
import static gui.componentSet.log;
import static gui.componentSet.logBtn;
import static gui.componentSet.mainPanel;
import static gui.componentSet.oldFileBtn;
import static gui.componentSet.rightPanel;
import static gui.componentSet.searchBtn;
import static gui.componentSet.spaceMakerBtn;
import static gui.componentSet.spaceMakerTable;
import static gui.componentSet.tablePanel;
import static gui.componentSet.typeField;
import static gui.defaultSet.ALL_W;
import static gui.defaultSet.COMP_H;
import static gui.defaultSet.CUR_H;
import static gui.defaultSet.DAY_W;
import static gui.defaultSet.DELETE;
import static gui.defaultSet.GAB;
import static gui.defaultSet.INPOS;
import static gui.defaultSet.LEFT_W;
import static gui.defaultSet.L_BTN_W;
import static gui.defaultSet.MONTH_W;
import static gui.defaultSet.RIGHT_W;
import static gui.defaultSet.SEARCH;
import static gui.defaultSet.SUB_GAB;
import static gui.defaultSet.YEAR_W;
import static gui.defaultSet.initDate;
import static gui.defaultSet.initTable;
import static gui.defaultSet.interactableUI;


public class GUI extends JFrame {
	/* non_final variable */
	// Current X-axis, Y-axis position
	private int posx = 0;
	private int posy = 0;
	
	public GUI() {
		super("Java Storage Cleaner");

//		/* Redirect output and error message */
//		try (FileOutputStream fos = new FileOutputStream("output.log");
//			 DataOutputStream dos = new DataOutputStream(fos)) {
//
//			// redirect System.out, System.err using PrintStream
//			ObjectOutputStream objectOut = new ObjectOutputStream(dos);
//
//			// System.out을 ObjectOutputStream으로 변경
//			System.setOut(new PrintStream(objectOut, true));
//			System.setErr(new PrintStream(objectOut, true));
//
//		} catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        /* set JFrame */
		setSize(defaultSet.WIDTH, defaultSet.HEIGHT);
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
		posy -= SUB_GAB;
		posy = fileSearchBtn.leftButtonUI(posy);
		posy = spaceMakerBtn.leftButtonUI(posy);
		posy = fileBackUpBtn.leftButtonUI(posy);
		logBtn.leftButtonUI(posy);
		
		leftPanel.add(LTitle1);
		leftPanel.add(LTitle2);
		leftPanel.add(fileSearchBtn);
		leftPanel.add(spaceMakerBtn);
		leftPanel.add(fileBackUpBtn);
		leftPanel.add(logBtn);
		
		/* set rightPanel */
		rightPanel.setLayout(new CardLayout());
		
		// set changeable page
		JPanel page[] = {
				new JPanel(), new JPanel(),
				new JPanel(), new JPanel(),
				new JPanel() };
		for(JPanel p : page) {
			p.setLayout(null);
			p.setBackground(mainPanel.getBackground());
		}
		for(int i = 1; i < 4; i++)
			searchBtn[i-1].setActionCommand(SEARCH + Integer.toString(i));
		for(int i = 1; i < 3; i++)
			deleteBtn[i-1].setActionCommand(DELETE + Integer.toString(i));
		
		// set 0th page
		initPage(page[0]);
		
		// set same component of 1, 2, 3 page
		initTable();
		for(int i = 0; i < 3; i++)
			samePage(page[i+1], searchBtn[i], deleteBtn[i], typeField[i]);
		
		fileSearchPage(page[1]);
		spaceMakerPage(page[2]);
		fileBackupPage(page[3]);
		logPage(page[4]);
		
		
		for(int i = 0; i < page.length; i++)
			rightPanel.add(page[i], Integer.toString(i));
		
		/* add to JFrame */
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		add(mainPanel);
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
	private void samePage(JPanel p, RButton search, RButton delete, RTextField text) {
		JLabel RTitle = new JLabel(" Java Storage Cleaner");
		RTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		RTitle.setForeground(Color.WHITE);
		RTitle.setHorizontalAlignment(SwingConstants.LEFT);
		RTitle.setBounds(0, 0, RIGHT_W, 100);
		
		interactableUI(text);
		text.setBounds(0, INPOS, L_BTN_W, COMP_H);
		text.setBorder(BorderFactory.createEmptyBorder(0, SUB_GAB, 0, SUB_GAB));
		
		// show search, delete btn
		search.interactableBtnUI();
		delete.interactableBtnUI();
		search.setBounds(RIGHT_W - 100, INPOS, 100, COMP_H);
		delete.setBounds(RIGHT_W - 100, CUR_H - 63, 100, COMP_H);
		
		p.add(RTitle);
		p.add(text);
		p.add(search);
		p.add(delete);
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
		
		EMonth.setBounds(posx, 0, MONTH_W, COMP_H);
		EDay.setBounds	(posx + SUB_GAB + MONTH_W, 0, DAY_W, COMP_H);
		EYear.setBounds	(posx + SUB_GAB*2 + MONTH_W + DAY_W, 0, YEAR_W, COMP_H);
		
		DatePanel.add(SMonth);
		DatePanel.add(SDay);
		DatePanel.add(SYear);
		
		DatePanel.add(dash);
		
		DatePanel.add(EMonth);
		DatePanel.add(EDay);
		DatePanel.add(EYear);
		
		p.add(DatePanel);
		p.add(tablePanel[0]);
	}
	private void spaceMakerPage(JPanel p) {
		allBtn.interactableBtnUI();
		duplicatesBtn.interactableBtnUI();
		bigSizeBtn.interactableBtnUI();
		oldFileBtn.interactableBtnUI();
		
		allBtn.setBounds(SUB_GAB, 0, ALL_W, COMP_H);
		duplicatesBtn.setBounds(SUB_GAB*2 + ALL_W, 0, INPOS+10, COMP_H);
		bigSizeBtn.setBounds(SUB_GAB*3 + ALL_W + INPOS+10, 0, INPOS-5, COMP_H);
		oldFileBtn.setBounds(SUB_GAB*4 + ALL_W + INPOS*2+5, 0, INPOS-5, COMP_H);
		
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(null);
		menuPanel.setBackground(Color.GRAY);
		menuPanel.setBounds(INPOS*2 + GAB*4 - (SUB_GAB + ALL_W),
				INPOS, (SUB_GAB*5) + ALL_W + (INPOS*3) - 5, COMP_H);
		
		// initial table
		allBtn.setBackground(Color.LIGHT_GRAY);
		allBtn.setForeground(Color.DARK_GRAY);
		
		menuPanel.add(allBtn);
		menuPanel.add(duplicatesBtn);
		menuPanel.add(bigSizeBtn);
		menuPanel.add(oldFileBtn);
		
		// Card Layout Table
		spaceMakerTable.setLayout(new CardLayout());
		spaceMakerTable.setBounds(0, INPOS + COMP_H + GAB, RIGHT_W,
				CUR_H - (INPOS + COMP_H + GAB) - 63 - GAB);
		for(int i = 0; i < 4; i++)
			spaceMakerTable.add(tablePanel[i+1], Integer.toString(i));

		p.add(menuPanel);
		p.add(spaceMakerTable);
	}
	private void fileBackupPage(JPanel p) {
		interactableUI(typeField[3]);
		typeField[3].setBounds(L_BTN_W + GAB, INPOS, L_BTN_W, COMP_H);
		typeField[3].setBorder(BorderFactory.createEmptyBorder(0, SUB_GAB, 0, SUB_GAB));
		
		deleteBtn[2].setBounds(RIGHT_W - 105, CUR_H - 63, 105, COMP_H);
		
		p.add(typeField[3]);
		p.add(tablePanel[5]);
	}
	private void logPage(JPanel p) {
		JLabel RTitle = new JLabel(" Java Storage Cleaner");
		RTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		RTitle.setForeground(Color.WHITE);
		RTitle.setHorizontalAlignment(SwingConstants.LEFT);
		RTitle.setBounds(0, 0, RIGHT_W, 100);
		
		log.setEditable(false);
		interactableUI(log);
		log.setBackground(leftPanel.getBackground());
		log.setForeground(Color.DARK_GRAY);
		
		JScrollPane scrollPane = new JScrollPane(log);
		scrollPane.setBounds(0, INPOS, RIGHT_W, CUR_H - INPOS - 63 - GAB);
				
		p.add(RTitle);
		p.add(scrollPane);
	}
}