import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {
    private MemoryManager memoryManager;
    private JTable memoryTable;
    private JTextArea summaryArea;
    private JTextField idField, sizeField, deallocField;
    private JComboBox<String> algoBox;
    private MemoryVisualizerPanel visualizerPanel;

    public MainFrame() {
        super("Dynamic Memory Allocator - OS Project");

        memoryManager = new MemoryManager(128);
        setLayout(new BorderLayout(10, 5));

        // ===== Input Controls Panel =====
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Request"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Process ID:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(5);
        inputPanel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Memory Size:"), gbc);
        gbc.gridx = 1;
        sizeField = new JTextField(5);
        inputPanel.add(sizeField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        inputPanel.add(new JLabel("Algorithm:"), gbc);
        gbc.gridx = 3;
        algoBox = new JComboBox<>(new String[] {"First Fit", "Best Fit", "Worst Fit"});
        inputPanel.add(algoBox, gbc);

        JButton allocBtn = new JButton("Allocate");
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        allocBtn.setPreferredSize(new Dimension(95,35));
        inputPanel.add(allocBtn, gbc);

        JButton resetBtn = new JButton("Reset All");
        gbc.gridx = 5; gbc.gridheight = 2;
        resetBtn.setPreferredSize(new Dimension(95,35));
        inputPanel.add(resetBtn, gbc);

        // ===== Free/Deallocate Panel =====
        JPanel freePanel = new JPanel();
        freePanel.add(new JLabel("Deallocate by ID:"));
        deallocField = new JTextField(5);
        freePanel.add(deallocField);
        JButton deallocBtn = new JButton("Deallocate");
        freePanel.add(deallocBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.WEST);
        topPanel.add(freePanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ===== Memory Table =====
        String[] colNames = {"Block", "Start", "End", "Allocated", "Process ID", "Size"};
        memoryTable = new JTable(new DefaultTableModel(colNames, 0));
        JScrollPane scroll = new JScrollPane(memoryTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Block List"));

        // ===== Visualization Panel =====
        visualizerPanel = new MemoryVisualizerPanel(memoryManager);

        // ===== Summary Area =====
        summaryArea = new JTextArea(4, 30);
        summaryArea.setEditable(false);
        summaryArea.setBorder(BorderFactory.createTitledBorder("Summary"));

        // ===== Center & Bottom Layout =====
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.add(scroll);
        centerPanel.add(visualizerPanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(summaryArea, BorderLayout.NORTH);

        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // ===== Action Listeners =====
        allocBtn.addActionListener(e -> {
            String id = idField.getText();
            int size;
            try { size = Integer.parseInt(sizeField.getText()); } catch(Exception ex){ size = -1; }
            if(id.isEmpty() || size <= 0) {
                JOptionPane.showMessageDialog(this, "Enter valid Process ID and Memory Size.");
                return;
            }

            boolean result = false;
            switch((String)algoBox.getSelectedItem()) {
                case "First Fit": result = memoryManager.allocateFirstFit(new Process(id, size)); break;
                case "Best Fit": result = memoryManager.allocateBestFit(new Process(id, size)); break;
                case "Worst Fit": result = memoryManager.allocateWorstFit(new Process(id, size)); break;
            }
            if(!result) JOptionPane.showMessageDialog(this, "Allocation FAILED (insufficient or fragmented memory)");
            updateTableAndView();
        });

        deallocBtn.addActionListener(e -> {
            if(!memoryManager.deallocate(deallocField.getText()))
                JOptionPane.showMessageDialog(this, "No such process allocated!");
            updateTableAndView();
        });

        resetBtn.addActionListener(e -> {
            memoryManager.reset();
            updateTableAndView();
        });

        updateTableAndView();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900,600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateTableAndView() {
        DefaultTableModel model = (DefaultTableModel)memoryTable.getModel();
        model.setRowCount(0);
        int cnt = 1;
        int totalFree = 0, used = 0;
        for(MemoryBlock block : memoryManager.memoryBlocks) {
            model.addRow(new Object[]{
                cnt++, block.start, block.end, block.allocated ? "YES" : "NO",
                block.processId, block.size()
            });
            if(block.allocated) used += block.size(); else totalFree += block.size();
        }
        summaryArea.setText(
            "Total Memory : " + memoryManager.memorySize +
            "\nUsed         : " + used +
            "\nFree         : " + totalFree +
            "\nBlocks       : " + memoryManager.memoryBlocks.size()
        );
        visualizerPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    // ==== Visualizer Inner Panel ====
    static class MemoryVisualizerPanel extends JPanel {
        MemoryManager mm;
        Color[] palette = {Color.CYAN, Color.GREEN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.YELLOW};
        public MemoryVisualizerPanel(MemoryManager mm) {
            this.mm = mm;
            setPreferredSize(new Dimension(700, 80));
            setBorder(BorderFactory.createTitledBorder("Memory Visualization"));
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int W = getWidth()-40, H = getHeight()-30, x=20, y=25, M = mm.memorySize;
            for(MemoryBlock block : mm.memoryBlocks) {
                int w = (int)Math.round((block.size()/(double)M)*W);
                g.setColor(block.allocated ?
                    palette[Math.abs(block.processId.hashCode())%palette.length] : Color.lightGray);
                g.fillRect(x, y, w, H);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, w, H);
                g.drawString(
                    block.allocated ? block.processId+" ("+block.size()+")" : "FREE (" + block.size()+")",
                    x+5, y+20);
                x += w;
            }
        }
    }
}

// You still need these from before (memory manager and block classes!)
class MemoryBlock {
    public int start, end;
    public boolean allocated;
    public String processId;
    public MemoryBlock(int start, int end) {
        this.start = start; this.end = end; this.allocated = false; this.processId = "";
    }
    public int size() { return end-start+1; }
}

class Process {
    public String id; public int size;
    public Process(String id, int size) { this.id = id; this.size = size; }
}

class MemoryManager {
    public java.util.List<MemoryBlock> memoryBlocks = new java.util.ArrayList<>();
    public int memorySize;
    public MemoryManager(int memorySize) {
        this.memorySize = memorySize;
        reset();
    }
    public void reset() {
        memoryBlocks.clear();
        memoryBlocks.add(new MemoryBlock(0, memorySize - 1));
    }
    public boolean allocateFirstFit(Process p) { return allocImpl(p, 0); }
    public boolean allocateBestFit(Process p)  { return allocImpl(p, 1); }
    public boolean allocateWorstFit(Process p) { return allocImpl(p, 2); }
    private boolean allocImpl(Process p, int mode) {
        MemoryBlock target = null; int idx = -1;
        java.util.List<Integer> indexes = new java.util.ArrayList<>();
        for(int i=0;i<memoryBlocks.size();i++)
            if(!memoryBlocks.get(i).allocated && memoryBlocks.get(i).size()>=p.size)
                indexes.add(i);
        if(indexes.isEmpty()) return false;
        if(mode==0) idx=indexes.get(0); // FirstFit
        else if(mode==1) idx=indexes.stream().min((a,b)->memoryBlocks.get(a).size()-memoryBlocks.get(b).size()).orElse(indexes.get(0)); // BestFit
        else if(mode==2) idx=indexes.stream().max((a,b)->memoryBlocks.get(a).size()-memoryBlocks.get(b).size()).orElse(indexes.get(0)); // WorstFit
        target = memoryBlocks.get(idx);
        int originalEnd = target.end;
        target.end = target.start+p.size-1; target.allocated=true; target.processId=p.id;
        if(target.end < originalEnd)
            memoryBlocks.add(idx+1, new MemoryBlock(target.end+1, originalEnd));
        return true;
    }
    public boolean deallocate(String pid) {
        boolean found = false;
        for(MemoryBlock b : memoryBlocks) {
            if(b.allocated && b.processId.equals(pid)) {
                b.allocated = false; b.processId="";
                found = true;
            }
        }
        // Merge adjacent free blocks
        for(int i=0; i<memoryBlocks.size()-1;)
            if(!memoryBlocks.get(i).allocated && !memoryBlocks.get(i+1).allocated) {
                memoryBlocks.get(i).end = memoryBlocks.get(i+1).end;
                memoryBlocks.remove(i+1);
            } else i++;
        return found;
    }
}
