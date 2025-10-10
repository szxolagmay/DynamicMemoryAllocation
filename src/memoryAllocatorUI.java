import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class memoryAllocatorUI extends JFrame {
private memoryManager memoryManager = new memoryManager(400);


    private JPanel memoryPanel;
    private JTextField sizeField, addressField, newSizeField;
    private JButton allocBtn, freeBtn, resizeBtn, resetBtn;
    private ArrayList<Block> blocks = new ArrayList<>();
    private final int MEMORY_SIZE = 400; // simulated memory units
    private int used = 0;

    public memoryAllocatorUI() {
        setTitle("Dynamic Memory Allocator Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TOP INPUT PANEL
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Memory Controls"));

        sizeField = new JTextField();
        addressField = new JTextField();
        newSizeField = new JTextField();

        allocBtn = new JButton("Allocate");
        freeBtn = new JButton("Free");
        resizeBtn = new JButton("Resize");
        resetBtn = new JButton("Reset");

        inputPanel.add(new JLabel("Block Size:"));
        inputPanel.add(sizeField);
        inputPanel.add(allocBtn);
        inputPanel.add(new JLabel(""));

        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(freeBtn);
        inputPanel.add(new JLabel(""));

        inputPanel.add(new JLabel("New Size:"));
        inputPanel.add(newSizeField);
        inputPanel.add(resizeBtn);
        inputPanel.add(new JLabel(""));

        add(inputPanel, BorderLayout.NORTH);

        // MEMORY VISUAL PANEL
        memoryPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int x = 10, y = 20, width = 700, height = 20;
                g.drawRect(x, y, width, height);
                int offset = 0;
                for (Block b : blocks) {
                    int blockWidth = (int) ((b.size / (double) MEMORY_SIZE) * width);
                    g.setColor(b.color);
                    g.fillRect(x + offset, y, blockWidth, height);
                    g.setColor(Color.BLACK);
                    g.drawRect(x + offset, y, blockWidth, height);
                    g.drawString("Addr " + b.address + " (" + b.size + ")", x + offset + 5, y + 15);
                    offset += blockWidth;
                }
            }
        };
        memoryPanel.setPreferredSize(new Dimension(750, 100));
        memoryPanel.setBorder(BorderFactory.createTitledBorder("Heap Visualization"));
        add(memoryPanel, BorderLayout.CENTER);

        // RESET BUTTON 
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(resetBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // ACTIONS 
        allocBtn.addActionListener(e -> allocate());
        freeBtn.addActionListener(e -> free());
        resizeBtn.addActionListener(e -> resize());
        resetBtn.addActionListener(e -> reset());

        setVisible(true);
    }

    private void allocate() {
        try {
            int size = Integer.parseInt(sizeField.getText());
            if (used + size > MEMORY_SIZE) {
                JOptionPane.showMessageDialog(this, "Not enough space in heap!");
                return;
            }
            int address = blocks.isEmpty() ? 0 : blocks.get(blocks.size() - 1).address + blocks.get(blocks.size() - 1).size;
            blocks.add(new Block(address, size, randomColor()));
            used += size;
            memoryPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid size!");
        }
    }

    private void free() {
        try {
            int address = Integer.parseInt(addressField.getText());
            blocks.removeIf(b -> {
                if (b.address == address) {
                    used -= b.size;
                    return true;
                }
                return false;
            });
            memoryPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid address!");
        }
    }

    private void resize() {
        try {
            int address = Integer.parseInt(addressField.getText());
            int newSize = Integer.parseInt(newSizeField.getText());
            for (Block b : blocks) {
                if (b.address == address) {
                    used -= b.size;
                    if (used + newSize > MEMORY_SIZE) {
                        JOptionPane.showMessageDialog(this, "Not enough space to resize!");
                        used += b.size;
                        return;
                    }
                    b.size = newSize;
                    used += newSize;
                    memoryPanel.repaint();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Address not found!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers!");
        }
    }

    private void reset() {
        blocks.clear();
        used = 0;
        memoryPanel.repaint();
    }

    private Color randomColor() {
        return new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(memoryAllocatorUI::new);
    }

    // INNER CLASS
    static class Block {
        int address, size;
        Color color;

        Block(int address, int size, Color color) {
            this.address = address;
            this.size = size;
            this.color = color;
        }
    }
}
