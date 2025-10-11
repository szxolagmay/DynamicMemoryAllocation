import javax.swing.*;

public class AddProcessDialog extends JDialog {
    public JTextField idField, sizeField;
    public boolean confirmed = false;

    public AddProcessDialog(JFrame parent) {
        super(parent, "Add New Process", true);
        JPanel panel = new JPanel();
        panel.add(new JLabel("ID:"));
        idField = new JTextField(5);
        panel.add(idField);
        panel.add(new JLabel("Size:"));
        sizeField = new JTextField(5);
        panel.add(sizeField);

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
