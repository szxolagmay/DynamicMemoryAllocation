import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;

public class MainUi extends JFrame {

    private static class Process {
        final String id;
        final int size;
        public Process(String id, int size) { this.id = id; this.size = size; }
    }

    private static class MemoryBlock {
        int start;
        int end; 
        boolean allocated;
        String processId;

        MemoryBlock(int start, int end, boolean allocated, String processId) {
            this.start = start; this.end = end; this.allocated = allocated; this.processId = processId;
        }

        int size() { return end - start + 1; }
    }

    private static class MemoryManager {
        ArrayList<MemoryBlock> memoryBlocks = new ArrayList<>();
        int memorySize;

        MemoryManager(int memorySize) {
            this.memorySize = memorySize;
            memoryBlocks.add(new MemoryBlock(0, memorySize - 1, false, null));
        }

        private ArrayList<Integer> freeBlockIndices() {
            ArrayList<Integer> idx = new ArrayList<>();
            for (int i = 0; i < memoryBlocks.size(); i++) if (!memoryBlocks.get(i).allocated) idx.add(i);
            return idx;
        }

        boolean allocateFirstFit(Process p) { return allocateUsingComparator(p, Comparator.naturalOrder()); }
        boolean allocateBestFit(Process p) {
            int best = -1; int bestSize = Integer.MAX_VALUE;
            for (int i = 0; i < memoryBlocks.size(); i++) {
                MemoryBlock b = memoryBlocks.get(i);
                if (!b.allocated && b.size() >= p.size) {
                    if (b.size() < bestSize) { best = i; bestSize = b.size(); }
                }
            }
            if (best == -1) return false;
            splitAndAllocate(best, p);
            return true;
        }

        boolean allocateWorstFit(Process p) {
            int worst = -1; int worstSize = -1;
            for (int i = 0; i < memoryBlocks.size(); i++) {
                MemoryBlock b = memoryBlocks.get(i);
                if (!b.allocated && b.size() >= p.size) {
                    if (b.size() > worstSize) { worst = i; worstSize = b.size(); }
                }
            }
            if (worst == -1) return false;
            splitAndAllocate(worst, p);
            return true;
        }

        private boolean allocateUsingComparator(Process p, Comparator<Integer> cmp) {
            for (int i = 0; i < memoryBlocks.size(); i++) {
                MemoryBlock b = memoryBlocks.get(i);
                if (!b.allocated && b.size() >= p.size) {
                    splitAndAllocate(i, p);
                    return true;
                }
            }
            return false;
        }

        private void splitAndAllocate(int index, Process p) {
            MemoryBlock b = memoryBlocks.get(index);
            int blockSize = b.size();
            if (p.size == blockSize) {
                b.allocated = true;
                b.processId = p.id;
            } else {
                int allocStart = b.start;
                int allocEnd = allocStart + p.size - 1;
                MemoryBlock allocatedBlock = new MemoryBlock(allocStart, allocEnd, true, p.id);
                MemoryBlock remaining = new MemoryBlock(allocEnd + 1, b.end, false, null);
                memoryBlocks.set(index, allocatedBlock);
                memoryBlocks.add(index + 1, remaining);
            }
            mergeAdjacentFreeBlocks();
        }

        boolean deallocate(String processId) {
            boolean found = false;
            for (MemoryBlock b : memoryBlocks) {
                if (b.allocated && b.processId != null && b.processId.equals(processId)) {
                    b.allocated = false;
                    b.processId = null;
                    found = true;
                }
            }
            if (found) mergeAdjacentFreeBlocks();
            return found;
        }

        void reset() {
            memoryBlocks.clear();
            memoryBlocks.add(new MemoryBlock(0, memorySize - 1, false, null));
        }

        private void mergeAdjacentFreeBlocks() {
            ArrayList<MemoryBlock> next = new ArrayList<>();
            for (MemoryBlock b : memoryBlocks) {
                if (!next.isEmpty() && !b.allocated && !next.get(next.size() - 1).allocated) {
                    MemoryBlock last = next.get(next.size() - 1);
                    last.end = b.end;
                } else {
                    next.add(new MemoryBlock(b.start, b.end, b.allocated, b.processId));
                }
            }
            memoryBlocks = next;
        }
    }

    private final MemoryManager memoryManager;
    private final JTable memoryTable;
    private final JTextArea summaryArea;
    private final JTextField sizeField, processIdField, deallocField;
    private final JComboBox<String> algoBox;
    private final MemoryVisualizerPanel visualizerPanel;
    private JLabel processingStatusLabel;

    public MainUi() {
        setTitle("Memory Visualization");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        memoryManager = new MemoryManager(1000); // memory in KB
        Color pink = new Color(255, 204, 204);
        Color borderPink = new Color(255, 180, 180);
        Color white = Color.WHITE;

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(white);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(white);
        leftPanel.setBorder(BorderFactory.createTitledBorder("New Request"));

        algoBox = new JComboBox<>(new String[]{"First Fit", "Best Fit", "Worst Fit"});
        algoBox.setBackground(white);
        algoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        sizeField = createPlaceholderField("Enter your file size (KB)");
        processIdField = createPlaceholderField("Enter the process ID");
        deallocField = createPlaceholderField("Enter the process ID to deallocate");

        JButton allocBtn = new JButton("Allocate");
        JButton deallocBtn = new JButton("Deallocate");
        for (JButton btn : new JButton[]{allocBtn, deallocBtn}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(255, 120, 120));
            btn.setBorder(new LineBorder(borderPink, 2, true));
            btn.setFocusPainted(false);
        }

        leftPanel.add(createRoundedSection("Select your algorithm:", pink, borderPink, algoBox));
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(createRoundedSection("To allocate:", pink, borderPink, sizeField, processIdField, allocBtn));
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(createRoundedSection("To deallocate:", pink, borderPink, deallocField, deallocBtn));

        // CENTER PANEL
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(white);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Memory Visualization"));

        visualizerPanel = new MemoryVisualizerPanel(memoryManager);
        visualizerPanel.setPreferredSize(new Dimension(0, 200));
        visualizerPanel.setBackground(pink);
        visualizerPanel.setBorder(new LineBorder(borderPink, 2, true));

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Block", "Start (KB)", "End (KB)", "Allocated", "Process ID", "Size (KB)"}, 0);
        memoryTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(memoryTable);
        tableScroll.getViewport().setBackground(pink);
        tableScroll.setBorder(new LineBorder(borderPink, 2, true));

        JLabel blockListLabel = new JLabel("Block List");
        blockListLabel.setFont(blockListLabel.getFont().deriveFont(Font.BOLD, 14f));

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBackground(pink);
        resetBtn.setBorder(new LineBorder(borderPink, 2, true));
        resetBtn.setFocusPainted(false);
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel blockListPanel = new JPanel(new BorderLayout());
        blockListPanel.setBackground(white);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(white);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(blockListLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(tableScroll);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(white);
        bottomPanel.add(resetBtn);

        blockListPanel.add(topPanel, BorderLayout.CENTER);
        blockListPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel visualizationAndTable = new JPanel(new GridLayout(2, 1, 5, 5));
        visualizationAndTable.setBackground(white);
        visualizationAndTable.add(visualizerPanel);
        visualizationAndTable.add(blockListPanel);
        centerPanel.add(visualizationAndTable, BorderLayout.CENTER);

        // RIGHT PANEL (Info + Status + Summary) 
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(white);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Memory Summary"));

        // Info Box
        JTextArea infoBox = new JTextArea(
                "Dynamic memory allocation is the process of assigning the memory space during the execution time or the run time."
        );
        infoBox.setWrapStyleWord(true);
        infoBox.setLineWrap(true);
        infoBox.setEditable(false);
        infoBox.setBackground(pink);
        infoBox.setBorder(new LineBorder(borderPink, 2, true));
        infoBox.setFont(new Font("Arial", Font.PLAIN, 13));
        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(pink);
        statusPanel.setBorder(new LineBorder(borderPink, 2, true));
        processingStatusLabel = new JLabel("Status: Idle");
        processingStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        processingStatusLabel.setForeground(Color.DARK_GRAY);
        statusPanel.add(processingStatusLabel);

        // Summary Area
        summaryArea = new JTextArea();
        summaryArea.setBackground(pink);
        summaryArea.setBorder(new LineBorder(borderPink, 2, true));
        summaryArea.setEditable(false);
        summaryArea.setPreferredSize(new Dimension(250, 150));

        rightPanel.add(infoBox);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(statusPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(summaryArea);

        // Layout
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel);

        // Button Actions
        allocBtn.addActionListener(e -> {
            doAllocate();
            resetPlaceholder(sizeField, "Enter your file size (KB)");
            resetPlaceholder(processIdField, "Enter the process ID");
        });
        deallocBtn.addActionListener(e -> {
            doDeallocate();
            resetPlaceholder(deallocField, "Enter the process ID to deallocate");
        });
        resetBtn.addActionListener(e -> {
            memoryManager.reset();
            updateTableAndView();
            clearAndResetPlaceholders();
        });

        updateTableAndView();
        setVisible(true);
    }

    private void clearAndResetPlaceholders() {
        resetPlaceholder(sizeField, "Enter your file size (KB)");
        resetPlaceholder(processIdField, "Enter the process ID");
        resetPlaceholder(deallocField, "Enter the process ID to deallocate");
    }

    private void resetPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
    }

    private void doAllocate() {
        String id = processIdField.getText().trim();
        int size;
        try { size = Integer.parseInt(sizeField.getText().trim()); }
        catch (Exception ex) { size = -1; }
        if (id.isEmpty() || size <= 0) {
            JOptionPane.showMessageDialog(this, "Enter valid Process ID and Memory Size.");
            return;
        }

        boolean result = false;
        switch ((String) algoBox.getSelectedItem()) {
            case "First Fit": result = memoryManager.allocateFirstFit(new Process(id, size)); break;
            case "Best Fit": result = memoryManager.allocateBestFit(new Process(id, size)); break;
            case "Worst Fit": result = memoryManager.allocateWorstFit(new Process(id, size)); break;
        }

        if (!result) {
            JOptionPane.showMessageDialog(this, "Allocation FAILED (insufficient or fragmented memory)");
        } else {
            processingStatusLabel.setText("Status: Processing " + id + " (" + size + " KB)...");
            Timer timer = new Timer(size * 1000, e -> processingStatusLabel.setText("Status: Idle"));
            timer.setRepeats(false);
            timer.start();
        }

        updateTableAndView();
    }

    private void doDeallocate() {
        String pid = deallocField.getText().trim();
        if (pid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Process ID to deallocate.");
            return;
        }
        boolean ok = memoryManager.deallocate(pid);
        if (!ok) JOptionPane.showMessageDialog(this, "No such process allocated!");
        updateTableAndView();
    }

    private void updateTableAndView() {
        DefaultTableModel model = (DefaultTableModel) memoryTable.getModel();
        model.setRowCount(0);
        int cnt = 1;
        int totalFree = 0, used = 0;
        for (MemoryBlock block : memoryManager.memoryBlocks) {
            model.addRow(new Object[]{
                    cnt++, block.start + " KB", block.end + " KB", block.allocated ? "YES" : "NO",
                    block.processId == null ? "-" : block.processId, block.size() + " KB"
            });
            if (block.allocated) used += block.size(); else totalFree += block.size();
        }
        summaryArea.setText(
                "Total Memory : " + memoryManager.memorySize + " KB" +
                        "\nUsed         : " + used + " KB" +
                        "\nFree         : " + totalFree + " KB" +
                        "\nBlocks       : " + memoryManager.memoryBlocks.size()
        );
        visualizerPanel.repaint();
    }

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

    private static class MemoryVisualizerPanel extends JPanel {
        private final MemoryManager manager;

        MemoryVisualizerPanel(MemoryManager manager) { this.manager = manager; setPreferredSize(new Dimension(100, 180)); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(255, 235, 235));
                g2.fillRect(0, 0, w, h);
                int pad = 10, barY = pad, barH = h - 2 * pad, gap = 6;
                int x = pad;
                double scale = (double) (w - 2 * pad - (manager.memoryBlocks.size() - 1) * gap) / manager.memorySize;
                if (scale <= 0) scale = 1.0 / manager.memorySize;
                for (int i = 0; i < manager.memoryBlocks.size(); i++) {
                    MemoryBlock b = manager.memoryBlocks.get(i);
                    int bw = Math.max(6, (int) Math.round(b.size() * scale));
                    g2.setColor(b.allocated ? new Color(200, 80, 80) : new Color(220, 200, 220));
                    g2.fillRoundRect(x, barY, bw, barH, 8, 8);
                    g2.setColor(Color.GRAY);
                    g2.drawRoundRect(x, barY, bw, barH, 8, 8);
                    g2.setColor(b.allocated ? Color.WHITE : Color.DARK_GRAY);
                    String lbl = (b.allocated ? b.processId : b.size() + " KB free");
                    FontMetrics fm = g2.getFontMetrics();
                    int lblx = x + Math.max(4, (bw - fm.stringWidth(lbl)) / 2);
                    int lbly = barY + (barH + fm.getAscent()) / 2 - 2;
                    g2.drawString(lbl, lblx, lbly);
                    x += bw + gap;
                }
            } finally { g2.dispose(); }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUi::new);
    }
}
