import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MainUi extends JFrame {

    private Object memoryManager = null; // leave null for now or replace with your manager

    public MainUi() {
        setTitle("Memory Visualization");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // COLORS
        Color pink = new Color(255, 204, 204);
        Color borderPink = new Color(255, 180, 180);
        Color white = Color.WHITE;

        // MAIN LAYOUT
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(white);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT PANEL (New Request)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(white);
        leftPanel.setBorder(BorderFactory.createTitledBorder("New Request"));

        JComboBox<String> algoChoice = new JComboBox<>(new String[] { "First Fit", "Best Fit", "Worst Fit" });
        algoChoice.setBackground(white);
        algoChoice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField fileSizeField = createPlaceholderField("Enter your file size");
        JTextField processIdField = createPlaceholderField("Enter the process ID");
        JTextField deallocField = createPlaceholderField("Enter the process ID");

        JButton allocBtn = new JButton("Allocate");
        JButton deallocBtn = new JButton("Deallocate");

        // Styling for allocate/deallocate buttons
        for (JButton btn : new JButton[] { allocBtn, deallocBtn }) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(255, 120, 120));
            btn.setBorder(new LineBorder(borderPink, 2, true));
            btn.setFocusPainted(false);
        }

        // left side rounded boxes
        leftPanel.add(createRoundedSection("Select your algorithm:", pink, borderPink, algoChoice));
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(createRoundedSection("To allocate:", pink, borderPink, fileSizeField, processIdField, allocBtn));
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(createRoundedSection("To deallocate:", pink, borderPink, deallocField, deallocBtn));

        // CENTER PANEL (Visualization + Table)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(white);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Memory Visualization"));

        // Visualization Panel 
        MemoryVisualizerPanel visualizerPanel = new MemoryVisualizerPanel(memoryManager);
        visualizerPanel.setPreferredSize(new Dimension(0, 200));
        visualizerPanel.setBackground(pink);
        visualizerPanel.setBorder(new LineBorder(borderPink, 2, true));

        // Block List Table
        JTable table = new JTable(new Object[][] {}, new String[] { "Block", "Start", "End", "Status", "Process ID", "Size" });
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getViewport().setBackground(pink);
        tableScroll.setBorder(new LineBorder(borderPink, 2, true));

        JLabel blockListLabel = new JLabel("Block List");
        blockListLabel.setFont(blockListLabel.getFont().deriveFont(Font.BOLD, 14));

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBackground(pink);
        resetBtn.setBorder(new LineBorder(borderPink, 2, true));
        resetBtn.setFocusPainted(false);
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Block List Panel 
        JPanel blockListPanel = new JPanel();
        blockListPanel.setLayout(new BoxLayout(blockListPanel, BoxLayout.Y_AXIS));
        blockListPanel.setBackground(white);
        blockListPanel.add(blockListLabel);
        blockListPanel.add(Box.createVerticalStrut(5));
        blockListPanel.add(tableScroll);
        blockListPanel.add(Box.createVerticalStrut(10));
        blockListPanel.add(resetBtn);

        // Combine Visualization + Table 
        JPanel visualizationAndTable = new JPanel(new GridLayout(2, 1, 5, 5));
        visualizationAndTable.setBackground(white);
        visualizationAndTable.add(visualizerPanel); // top: memory visualization
        visualizationAndTable.add(blockListPanel); // bottom: table + reset button

        centerPanel.add(visualizationAndTable, BorderLayout.CENTER);

        // RIGHT PANEL (Memory Summary)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(white);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Memory Summary"));

        JTextArea summaryArea = new JTextArea();
        summaryArea.setBackground(pink);
        summaryArea.setBorder(new LineBorder(borderPink, 2, true));

        rightPanel.add(summaryArea, BorderLayout.CENTER);

        // Combine panels 
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Panel sizing
        leftPanel.setPreferredSize(new Dimension(250, 0));
        rightPanel.setPreferredSize(new Dimension(250, 0));

        add(mainPanel);
        setVisible(true);
    }

    // Helper: Create Placeholder TextField
    private static JTextField createPlaceholderField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setForeground(Color.GRAY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBorder(new LineBorder(new Color(255, 180, 180), 2, true));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    // Helper: Create Rounded Section Box
    private static JPanel createRoundedSection(String title, JComponent... components) {
        Color defaultBg = new Color(255, 204, 204);
        Color defaultBorder = new Color(255, 180, 180);
        return createRoundedSection(title, defaultBg, defaultBorder, components);
    }

    private static JPanel createRoundedSection(String title, Color bg, Color borderColor, JComponent... components) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(bg);
        box.setBorder(new LineBorder(borderColor, 2, true));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(title);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(Box.createVerticalStrut(10));
        box.add(label);
        for (JComponent c : components) {
            box.add(Box.createVerticalStrut(10));
            box.add(c);
        }
        box.add(Box.createVerticalStrut(10));
        return box;
    }

    // Accepts Object so it won't require a specific MemoryManager type.
    // Replace with your own implementation to draw actual memory blocks.
    private static class MemoryVisualizerPanel extends JPanel {
        private final Object memoryManager;

        public MemoryVisualizerPanel(Object memoryManager) {
            this.memoryManager = memoryManager;
            // prefer a reasonable minimum height
            setPreferredSize(new Dimension(100, 180));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // background (slightly different for the visualizer area)
                g2.setColor(new Color(255, 235, 235));
                g2.fillRect(0, 0, w, h);

                // If memoryManager is null, draw placeholder text
                if (memoryManager == null) {
                    g2.setColor(Color.DARK_GRAY);
                    String msg = "Memory visualizer (no memoryManager assigned)";
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (w - fm.stringWidth(msg)) / 2;
                    int ty = h / 2;
                    g2.drawString(msg, tx, ty);
                    return;
                }

                // Example drawing: draw a few colored blocks (placeholder)
                int pad = 10;
                int barY = pad;
                int barH = h - 2 * pad;
                int gap = 6;

                // simulated block sizes (percent)
                double[] blocks = new double[] { 0.2, 0.15, 0.25, 0.1, 0.3 };
                int x = pad;
                for (int i = 0; i < blocks.length; i++) {
                    int bw = (int) ((w - 2 * pad - (blocks.length - 1) * gap) * blocks[i]);
                    g2.setColor(new Color(200 - i * 20, 150 + i * 10, 170 + i * 10));
                    g2.fillRoundRect(x, barY, Math.max(10, bw), barH, 8, 8);
                    g2.setColor(Color.GRAY);
                    g2.drawRoundRect(x, barY, Math.max(10, bw), barH, 8, 8);

                    // label inside block
                    g2.setColor(Color.WHITE);
                    String lbl = (int) (blocks[i] * 100) + "%";
                    FontMetrics fm = g2.getFontMetrics();
                    int lblx = x + Math.max(4, (bw - fm.stringWidth(lbl)) / 2);
                    int lbly = barY + (barH + fm.getAscent()) / 2 - 2;
                    g2.drawString(lbl, lblx, lbly);

                    x += bw + gap;
                }
            } finally {
                g2.dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUi::new);
    }
}
