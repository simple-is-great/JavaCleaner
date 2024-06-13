package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import static gui.defaultSet.*;

public class RTable extends JTable {
	// for setting gab
	private final int gab = 7;

    public RTable(TableModel model) {
        super(model);
        customizeTableHeader();
        setDefaultRenderer(Object.class, new CellRenderer());
    }

    private void customizeTableHeader() {
        JTableHeader header = getTableHeader();
        
    	setForeground(Color.DARK_GRAY);
    	setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    	setSelectionBackground(Color.LIGHT_GRAY);
    	setSelectionForeground(Color.DARK_GRAY);
        
    	header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    	header.setForeground(Color.DARK_GRAY);
    	
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        setDefaultEditor(Object.class, null);
    }

    // Set gab
    private class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (cellComponent instanceof JLabel) {
                JLabel label = (JLabel) cellComponent;
                label.setBorder(BorderFactory.createEmptyBorder(gab, gab, gab, gab));
            }

            return cellComponent;
        }
    }
    
    // Set UI
    public void tableUI(RPanel p) {
    	this.setRowHeight(COMP_H - 5);
    	JTableHeader head = this.getTableHeader();
    	head.setPreferredSize(new Dimension(head.getPreferredSize().width, COMP_H));
    	this.updateUI();
    	
    	this.getColumnModel().getColumn(0).setPreferredWidth(RIGHT_W / 10 * 5);
    	this.getColumnModel().getColumn(1).setPreferredWidth(RIGHT_W / 10 * 3);
    	this.getColumnModel().getColumn(2).setPreferredWidth(RIGHT_W / 10 * 2);
    	
    	JScrollPane scrollPane = new JScrollPane(this);
    	
		p.add(this.getTableHeader(), BorderLayout.NORTH);
		p.add(scrollPane, BorderLayout.CENTER);
	}
    
    // Reset Table
	public void resetTable(Object[][] data) {
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		model.setNumRows(0);
		model.setDataVector(data, COL);
		
		this.getColumnModel().getColumn(0).setPreferredWidth(RIGHT_W / 10 * 5);
    	this.getColumnModel().getColumn(1).setPreferredWidth(RIGHT_W / 10 * 3);
    	this.getColumnModel().getColumn(2).setPreferredWidth(RIGHT_W / 10 * 2);
    	
		this.updateUI();
	}
}
