import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaStorageCleanerWindow extends JFrame implements ActionListener {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    private JTextField sourceField;
    private JTextField destField;
    private JTextField sourceOut;
    private JTextField destOut;
    private JPanel input;
    private JPanel output;

    public JavaStorageCleanerWindow(){
        super("Java Storage Cleaner");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        input = new JPanel();
        output = new JPanel();
        JLabel sourceLabel = new JLabel("Enter source to copy: ");
        JLabel destLabel = new JLabel("Enter destination to copy: ");
        sourceField = new JTextField();
        destField = new JTextField();
        sourceOut = new JTextField();
        destOut = new JTextField();
        input.setLayout(new GridLayout(2, 1));
        input.add(sourceLabel);
        input.add(sourceField);
        input.add(destField);
        output.setLayout(new GridLayout(2, 1));
        output.add(destLabel);
        output.add(sourceOut);
        output.add(destOut);
        add(input);
        add(output);
        JButton copy = new JButton("Copy");
        add(copy);
        copy.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCmd = e.getActionCommand();
        switch (actionCmd) {
            case "Copy":
                String source = sourceField.getText();
                String dest = destField.getText();
                sourceOut.setText(source);
                destOut.setText(dest);
                break;
        }
    }
}
