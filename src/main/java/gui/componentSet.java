package gui;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import static gui.defaultSet.*;

public class componentSet {
	// major panel
	protected static JPanel mainPanel = new JPanel();
	protected static RPanel
			leftPanel	= new RPanel(),
			rightPanel	= new RPanel();
	
	// Buttons
	protected static RButton
			fileSearchBtn	= new RButton(FSEARCH),
			spaceMakerBtn	= new RButton(SPACE),
			fileBackUpBtn	= new RButton(BACKUP),
			logBtn			= new RButton(LOG),
			allBtn			= new RButton(ALL),
			duplicatesBtn	= new RButton(DUPL),
			bigSizeBtn		= new RButton(BSIZE),
			oldFileBtn		= new RButton(OLD);
	
	protected static RButton[] searchBtn = {
			new RButton(SEARCH),
			new RButton(SEARCH),
			new RButton(SEARCH) };
	protected static RButton[] deleteBtn = {
			new RButton(DELETE),
			new RButton(DELETE),
			new RButton("BACKUP") };
	
	// text field: name or path input
	protected static RTextField[] typeField = {
			new RTextField(": Type Dir Path"),
			new RTextField(": Type Dir Path"),
			new RTextField(": Backup Dir Path"),
			new RTextField(": Store Dir Path") };
	protected static JTextArea log = new JTextArea("");
	
	// date input
	protected static JComboBox<String>
			SMonth	= new JComboBox<>(MONTHS),
			SDay	= new JComboBox<>(),
			SYear	= new JComboBox<>(),
			EMonth	= new JComboBox<>(MONTHS),
			EDay	= new JComboBox<>(),
			EYear	= new JComboBox<>();
	
	// table
	protected static RPanel[] tablePanel = new RPanel[] {
			new RPanel(), new RPanel(),
			new RPanel(), new RPanel(),
			new RPanel(), new RPanel() };
	protected static RPanel spaceMakerTable = new RPanel();
	
	protected static RTable
			fileSearchTbl	= new RTable(fileSearchMdl),
			combinationTbl	= new RTable(combinationMdl),
			duplicatesTbl	= new RTable(duplicatesMdl),
			bigSizeTbl		= new RTable(bigSizeMdl),
			oldFileTbl		= new RTable(oldFileMdl),
			backUpTbl		= new RTable(backUpMdl);
}
