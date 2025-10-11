import javax.swing.*;

public class AddMemoryBlockDialog extends JDialog {
    public JTextField startField, endField;
    public boolean confirmed = false;

    public AddMemoryBlockDialog(JFrame parent) {
        super(parent, "Add Memory Block", true);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Start:"));
        startField = new JTextField(5);
        panel.add(startField);
        panel.add(new JLabel("End:"));
        endField = new JTextField(5);
        panel.add(endField);

        JButton ok = new JButton("Add");
        ok.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        panel.add(ok);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }
}
