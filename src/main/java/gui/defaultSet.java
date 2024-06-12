package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;

import static gui.componentSet.*;

public class defaultSet {
	/* Variable */
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
	public static final int ALL_W	= 70;
	
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
			LOG		= "Check Log",
			SEARCH	= "SEARCH",
			DELETE	= "DELETE",
			ALL		= "ALL",
			DUPL	= "Duplicates",
			BSIZE	= "Big Size",
			OLD		= "Old Files";
	
	// Date
	public static LocalDate today = LocalDate.now();
	public static final String MONTHS[] = {
			"(Month)", "Jan", "Feb",
			"Mar", "Apr", "May", "Jun", "Jul",
			"Aug", "Sep", "Oct", "Nov", "Dec" };
	
	/* Table */
	// Table head
	public static final String[] COL = { " Name", " Date Modified", " Size" };
	
	// Table data
	public static List<Path>
	fileSearchRslt	= new ArrayList<>(),
	combinationRslt = new ArrayList<>(),
	bigSizeRslt		= new ArrayList<>(),
	oldFileRslt		= new ArrayList<>(),
	backUpRslt		= new ArrayList<>();

	public static Map<String, List<Path>>
	duplicatesRslt	= new HashMap<>();
	
	// Table model
	public static DefaultTableModel
	fileSearchMdl	= new DefaultTableModel(null, COL),
	combinationMdl	= new DefaultTableModel(null, COL),
	duplicatesMdl	= new DefaultTableModel(null, COL),
	bigSizeMdl		= new DefaultTableModel(null, COL),
	oldFileMdl		= new DefaultTableModel(null, COL),
	backUpMdl		= new DefaultTableModel(null, COL);
	
	
	/* static method */
	protected static void interactableUI(JComponent c) {
		c.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		c.setForeground(Color.LIGHT_GRAY);
		c.setBackground(Color.DARK_GRAY);
	}
	
	// Init date component: JComboBox
	// JComboBox는 wrapper class를 만들지 않아 init method를 따로 만들었습니다. 
	protected static void initDate() {
		SDay.addItem("(Day)");
		EDay.addItem("(Day)");
		SYear.addItem("(Year)");
		EYear.addItem("(Year)");
		
		int todayYear = today.getYear();
		
		for(int i = 1; i <= 31; i++)
			SDay.addItem(Integer.toString(i));
		for(int i = 1; i <= 31; i++)
			EDay.addItem(Integer.toString(i));
		for(int i = todayYear; i >= 1970; i--)
			SYear.addItem(Integer.toString(i));
		for(int i = todayYear; i >= 1970; i--)
			EYear.addItem(Integer.toString(i));
		
		setDateUI(SMonth);
		setDateUI(SDay);
		setDateUI(SYear);
		setDateUI(EMonth);
		setDateUI(EDay);
		setDateUI(EYear);
	}
	private static void setDateUI(JComboBox<String> c) {
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
		
	// Init table component
	protected static void initTable() {
		for(RPanel p : tablePanel) {
			p.setBounds(0, INPOS + COMP_H + GAB, RIGHT_W,
					CUR_H - (INPOS + COMP_H + GAB) - 63 - GAB);
			p.setLayout(new BorderLayout());
		}
		
		fileSearchTbl.	tableUI(tablePanel[0]);
		combinationTbl.	tableUI(tablePanel[1]);
		duplicatesTbl.	tableUI(tablePanel[2]);
		bigSizeTbl.		tableUI(tablePanel[3]);
		oldFileTbl.		tableUI(tablePanel[4]);
		backUpTbl.		tableUI(tablePanel[5]);
	}
}
