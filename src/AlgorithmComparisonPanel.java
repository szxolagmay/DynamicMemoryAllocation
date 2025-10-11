import java.awt.*;
import java.util.Map;
import javax.swing.*;

public class AlgorithmComparisonPanel extends JPanel {
    public AlgorithmComparisonPanel(Map<String, Integer> stats) {
        setBorder(BorderFactory.createTitledBorder("Algorithm Comparison"));
        setLayout(new GridLayout(stats.size(), 2));
        for (String algo : stats.keySet()) {
            add(new JLabel(algo));
            add(new JLabel(stats.get(algo) + " metric"));
        }
    }
}
