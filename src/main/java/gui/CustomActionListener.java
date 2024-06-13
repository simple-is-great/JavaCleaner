package gui;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static gui.componentSet.EDay;
import static gui.componentSet.EMonth;
import static gui.componentSet.EYear;
import static gui.componentSet.SDay;
import static gui.componentSet.SMonth;
import static gui.componentSet.SYear;
import static gui.componentSet.allBtn;
import static gui.componentSet.backUpTbl;
import static gui.componentSet.bigSizeBtn;
import static gui.componentSet.bigSizeTbl;
import static gui.componentSet.combinationTbl;
import static gui.componentSet.deleteBtn;
import static gui.componentSet.duplicatesBtn;
import static gui.componentSet.duplicatesTbl;
import static gui.componentSet.fileBackUpBtn;
import static gui.componentSet.fileSearchBtn;
import static gui.componentSet.fileSearchTbl;
import static gui.componentSet.leftPanel;
import static gui.componentSet.logBtn;
import static gui.componentSet.oldFileBtn;
import static gui.componentSet.oldFileTbl;
import static gui.componentSet.rightPanel;
import static gui.componentSet.searchBtn;
import static gui.componentSet.spaceMakerBtn;
import static gui.componentSet.spaceMakerTable;
import static gui.componentSet.typeField;
import static gui.defaultSet.ALL;
import static gui.defaultSet.BACKUP;
import static gui.defaultSet.BSIZE;
import static gui.defaultSet.DELETE;
import static gui.defaultSet.DUPL;
import static gui.defaultSet.FSEARCH;
import static gui.defaultSet.LOG;
import static gui.defaultSet.OLD;
import static gui.defaultSet.SEARCH;
import static gui.defaultSet.SPACE;
import static gui.defaultSet.backUpRslt;
import static gui.defaultSet.bigSizeRslt;
import static gui.defaultSet.combinationRslt;
import static gui.defaultSet.duplicatesRslt;
import static gui.defaultSet.fileSearchRslt;
import static gui.defaultSet.interactableUI;
import static gui.defaultSet.oldFileRslt;
import static gui.defaultSet.today;
import static storagecleaner.FileBackup.backupFiles;
import static storagecleaner.FileSearch.filterFilesByModifiedDate;
import static storagecleaner.SpaceSave.findBigFiles;
import static storagecleaner.SpaceSave.findDuplicates;
import static util.FileUtility.getPathList;

public class CustomActionListener implements ActionListener{
	@Override
	public void actionPerformed(ActionEvent e) {
		String btnStr = e.getActionCommand();
		CardLayout menu		= (CardLayout) rightPanel.getLayout();
		CardLayout space	= (CardLayout) spaceMakerTable.getLayout();
		
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
				clickUI1(fileBackUpBtn);
				menu.show(rightPanel, "3");
				break;
			case LOG:
				clickUI1(logBtn);
				menu.show(rightPanel, "4");
				break;
			case ALL:
				clickUI2(allBtn);
				space.show(spaceMakerTable, "0");
				break;
			case DUPL:
				clickUI2(duplicatesBtn);
				space.show(spaceMakerTable, "1");
				break;
			case BSIZE:
				clickUI2(bigSizeBtn);
				space.show(spaceMakerTable, "2");
				break;
			case OLD:
				clickUI2(oldFileBtn);
				space.show(spaceMakerTable, "3");
				break;
			case SEARCH + "1":
				clickUI3(searchBtn[0]);
				LocalDate[] date = convertDate();
				try {
					fileSearchRslt = 
							filterFilesByModifiedDate(typeField[0].getText(), date[0], date[1]);
				} catch (IOException e1) {
					errorWindow(2);
					System.err.println("FileSearch.java: Files.walk() error");
				}
				fileSearchTbl.resetTable(getListData(fileSearchRslt));
				
				clickUI3(searchBtn[0]);
				break;
			case SEARCH + "2":
				clickUI3(searchBtn[1]);
			
				if(Files.isDirectory(Paths.get(typeField[1].getText()))) {	
					duplicatesRslt = findDuplicates(typeField[1].getText());
					
					bigSizeRslt = findBigFiles(typeField[1].getText());
					try {
						oldFileRslt = filterFilesByModifiedDate(typeField[1].getText(), 
								LocalDate.of(1970, 1, 1),
								LocalDate.of(today.getYear()-3, 1, 1));
					} catch (IOException e1) {
						errorWindow(2);
						System.err.println("FileSearch.java: Files.walk() error");
					}
										
					duplicatesTbl.resetTable(getMapData(duplicatesRslt));
					bigSizeTbl.resetTable(getListData(bigSizeRslt));
					oldFileTbl.resetTable(getListData(oldFileRslt));
					
					combinationRslt.removeAll(combinationRslt);
					for(List<Path> l : duplicatesRslt.values())
						combinationRslt.addAll(l);
					combinationRslt.add(null);
					combinationRslt.addAll(bigSizeRslt);
					combinationRslt.add(null);
					combinationRslt.addAll(oldFileRslt);
					combinationTbl.resetTable(getListData(combinationRslt));
				}
				else errorWindow(2);
				
				clickUI3(searchBtn[1]);
				break;
			case SEARCH + "3":
				clickUI3(searchBtn[2]);
			
				Path path1 = Paths.get(typeField[2].getText());
				Path path2 = Paths.get(typeField[3].getText());
				if(Files.isDirectory(path1) && Files.isDirectory(path2)) {
					backUpRslt = getPathList(Paths.get(typeField[2].getText()));
					backUpTbl.resetTable(getListData(backUpRslt));
				}
				else errorWindow(2);

				clickUI3(searchBtn[2]);
				break;
			case DELETE + "1":
				clickUI3(deleteBtn[0]);
			
				deleteFile(fileSearchTbl.getSelectedRows(), fileSearchRslt);
				searchBtn[0].doClick();
				
				clickUI3(deleteBtn[0]);
				break;
			case DELETE + "2":
				clickUI3(deleteBtn[1]);
			
				deleteFile(combinationTbl.getSelectedRows(), combinationRslt);
				searchBtn[1].doClick();
				
				clickUI3(deleteBtn[1]);
				break;
			case "BACKUP":
				clickUI3(deleteBtn[2]);
				
				Path path3 = Paths.get(typeField[2].getText());
				Path path4 = Paths.get(typeField[3].getText());
				
				List<Path> storedDir = getPathList(Paths.get(typeField[3].getText()));
				if(Files.isDirectory(path3) && Files.isDirectory(path4)) {
                    try {
                        backupFiles(typeField[2].getText(), typeField[3].getText(), false);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    boolean equal = true;
					for(int i = 0; i < backUpRslt.size(); i++) {
						try {
							if(Files.size(backUpRslt.get(i)) != Files.size(storedDir.get(i))) {
								equal = false;
								break;
							}
						} catch (IOException e1) {
							System.err.println("Files.size(): no dir");
						}
					}
					
					if(equal) {
						errorWindow(3);
						backUpTbl.resetTable(getListData(storedDir));
					}
					else errorWindow(4);
				}
				else errorWindow(2);
				
				clickUI3(deleteBtn[2]);
				break;
			default:
				System.err.println("Unexpected input");
				break;
		}
	}
	
	private void clickUI1(JButton b) {
		Color c = leftPanel.getBackground();
		
		if(fileSearchBtn.getBackground() != c)
			fileSearchBtn.setBackground(c);
		if(spaceMakerBtn.getBackground() != c)
			spaceMakerBtn.setBackground(c);
		if(fileBackUpBtn.getBackground() != c)
			fileBackUpBtn.setBackground(c);
		if(logBtn.getBackground() != c)
			logBtn.setBackground(c);
		
		b.setBackground(Color.LIGHT_GRAY);
	}
	private void clickUI2(JButton b) {
		Color c = Color.DARK_GRAY;
		
		if(allBtn.getBackground() != c)
			interactableUI(allBtn);
		if(duplicatesBtn.getBackground() != c)
			interactableUI(duplicatesBtn);
		if(bigSizeBtn.getBackground() != c)
			interactableUI(bigSizeBtn);
		if(oldFileBtn.getBackground() != c)
			interactableUI(oldFileBtn);
		
		b.setBackground(Color.LIGHT_GRAY);
		b.setForeground(c);
	}
	private void clickUI3(JButton b) {
		if(b.getBackground() == Color.LIGHT_GRAY) {
			b.setBackground(Color.DARK_GRAY);
			b.setForeground(Color.LIGHT_GRAY);
		}
		else {
			b.setBackground(Color.LIGHT_GRAY);
			b.setForeground(Color.DARK_GRAY);
		}
	}
	
	// Delete files
	private void deleteFile(int[] selected, List<Path> l) {
		if(selected.length == 0) {
			errorWindow(0);
			return;
		}
		else if(!errorWindow(1)) return;
		
		for(int i : selected) {
			try {
				// System.out.println(i + " : " + l.get(i).toString());
				Files.delete(l.get(i));
			} catch (IOException e) {
				System.err.println("deleteFile(): Files.delete() error");
			}
		}
	}
	private boolean errorWindow(int mode) {
		boolean result = false;
		
		if(mode == 0)
			JOptionPane.showMessageDialog(null, "THERE IS NO SELECTED FILE!",
					"SELECT ERROR", JOptionPane.ERROR_MESSAGE );
		else if(mode == 1) {
			int answer = JOptionPane.showConfirmDialog
					(null, "DO YOU REALLY WANT TO DELETE?", "DELETE", JOptionPane.YES_NO_OPTION);
			if(answer == JOptionPane.YES_OPTION) result = true;
		}
		else if(mode == 2)
			JOptionPane.showMessageDialog(null, "PLEASE TYPE CORRECT DIRECTORY PATH!",
					"DIRECTORY ERROR", JOptionPane.ERROR_MESSAGE );
		else if(mode == 3)
			JOptionPane.showMessageDialog(null, "BACKUP SUCCESSED!",
					"BACKUP", JOptionPane.INFORMATION_MESSAGE );
		else if(mode == 4)
			JOptionPane.showMessageDialog(null, "BACKUP FAILED!",
					"BACKUP", JOptionPane.ERROR_MESSAGE );

		return result;
	}
	
	// Date method for Argument
	private LocalDate[] convertDate() {
		int todayYear = today.getYear();
		
		int startM = SMonth.getSelectedIndex();
		int startD = SDay.getSelectedIndex();
		int startY = todayYear+1 - SYear.getSelectedIndex();
		
		int endM = EMonth.getSelectedIndex();
		int endD = EDay.getSelectedIndex();
		int endY = todayYear+1 - EYear.getSelectedIndex();
		
		LocalDate start	= isDate(startM, startD, startY, true);
		LocalDate end	= isDate(endM, endD, endY, false);
		
		LocalDate newEnd = null;
		try {
			newEnd = LocalDate.ofYearDay(end.getYear(), end.getDayOfYear() + 1);
		} catch (DateTimeException e) {
			newEnd = LocalDate.ofYearDay(end.getYear() + 1, 1);
		}

		if(start.compareTo(end) == 1)
			start = LocalDate.of(end.getYear(), 1, 1);

		return new LocalDate[] { start, newEnd };
	}
	private LocalDate isDate(int m, int d, int y, boolean mode) {
		int todayYear = today.getYear();
		
		if (mode) {
			if(m == 0) m = 1;
			if(d == 0) d = 1;
			if(y == todayYear+1) y = todayYear;
			
			if(d > 30 && (m == 2 || m == 4 || m == 6 || m == 9 || m == 11))
				d = 30;
			
			if(m == 2 && d > 29) {
				if(((y % 4 == 0 && y % 100 != 0) || y % 400 == 0)) d = 29;
				else if(d > 28) d = 28;
			}

			return LocalDate.of(y, m, d);
		}
		else {
			if(y == todayYear+1 && m == 0 && d == 0) {
				return today;
			}
			
			if(m == 0) m = 1;
			if(d == 0) d = 1;
			if(y == todayYear+1) y = todayYear;
			
			if(d > 30 && (m == 2 || m == 4 || m == 6 || m == 9 || m == 11))
				d = 30;
			
			if(m == 2 && d > 29) {
				if(((y % 4 == 0 && y % 100 != 0) || y % 400 == 0)) d = 29;
				else if(d > 28) d = 28;
			}

			return LocalDate.of(y, m, d);
		}
 
	}
	
	// Get data from Map & List
	private Object[][] getMapData(Map<String, List<Path>> m) {
		Object[][][] data = new Object[m.keySet().size()][][];
		
		int i = 0;
		for(List<Path> l : m.values())
			data[i++] = getListData(l);
		
		int totalSize = 0;
		for(Object[][] obj : data) totalSize += obj.length;
		Object[][] dataModi = new Object[totalSize][];
		
		i = 0;
		for(Object[][] obj : data) {
			for(Object[] subObj : obj)
				dataModi[i++] = subObj;
		}
		
		return dataModi;
	}
	private Object[][] getListData(List<Path> l) {
		Object[][] data = new Object[l.size()][];
		for(int i = 0; i < l.size(); i++) {
			if(l.get(i) == null) data[i] = new Object[] { "", "", "" };
			else data[i] = getPathInfo(l.get(i));
		}
		
		return data;
	}
	private Object[] getPathInfo(Path p) {
		LocalDate date = null;
		try {
            BasicFileAttributes attrs =
            		Files.readAttributes(p, BasicFileAttributes.class);
            Instant instant = attrs.lastModifiedTime().toInstant();
            date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            System.err.println("getPathInfo(): Files.readAttributes(): " + p.toString());
        }
		
		Object[] info = null;
		try {
			info = new Object[] { p.getFileName().toString(),
					date.toString(), Long.toString(Files.size(p)) };
		} catch (IOException e) {
			System.err.println("getPathInfo(): Files.size(): " + p.toString());
		}

		return info;
    }
}
