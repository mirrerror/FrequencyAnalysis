package md.mirrerror;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class FrequencyAnalysisTool extends JFrame {
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JTextArea englishFrequencyArea;
    private final JButton analyzeButton;
    private final JTable substitutionsTable;
    private final DefaultTableModel substitutionsModel;

    public FrequencyAnalysisTool() {
        setTitle("Frequency Analysis Attack Tool");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputArea = new JTextArea("Enter encrypted text here...", 10, 50);
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        englishFrequencyArea = new JTextArea(10, 50);
        englishFrequencyArea.setEditable(false);
        englishFrequencyArea.setBorder(BorderFactory.createTitledBorder("English Letter Frequencies"));

        String[] columnNames = {"From", "To"};
        substitutionsModel = new DefaultTableModel(columnNames, 0);
        substitutionsTable = new JTable(substitutionsModel);
        JScrollPane substitutionsScrollPane = new JScrollPane(substitutionsTable);

        JButton addButton = new JButton("Add Substitution");
        JButton removeButton = new JButton("Remove Substitution");
        analyzeButton = new JButton("Analyze Frequency");

        addButton.addActionListener(e -> {
            String from = JOptionPane.showInputDialog(FrequencyAnalysisTool.this, "Enter character to substitute (From):");
            String to = JOptionPane.showInputDialog(FrequencyAnalysisTool.this, "Enter character to replace with (To):");
            if (from != null && to != null && from.length() == 1 && to.length() == 1) {
                substitutionsModel.addRow(new Object[]{from.toUpperCase(), to.toLowerCase()});
            } else {
                JOptionPane.showMessageDialog(FrequencyAnalysisTool.this, "Please enter valid characters.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeButton.addActionListener(e -> {
            int selectedRow = substitutionsTable.getSelectedRow();
            if (selectedRow >= 0) {
                substitutionsModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(FrequencyAnalysisTool.this, "Please select a substitution to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        analyzeButton.addActionListener(new AnalyzeButtonListener());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JScrollPane(inputArea));

        JPanel analyzePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        analyzePanel.add(analyzeButton);
        inputPanel.add(analyzePanel);

        inputPanel.add(new JScrollPane(outputArea));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        JPanel substitutionsPanel = new JPanel();
        substitutionsPanel.setLayout(new BorderLayout());
        substitutionsPanel.add(new JLabel("Substitutions:"), BorderLayout.NORTH);
        substitutionsPanel.add(substitutionsScrollPane, BorderLayout.CENTER);
        substitutionsPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(substitutionsPanel, BorderLayout.CENTER);
        add(new JScrollPane(englishFrequencyArea), BorderLayout.SOUTH);

        displayEnglishFrequencies();
    }

    private void displayEnglishFrequencies() {
        Map<Character, Double> englishFrequencies = getEnglishFrequencies();
        List<Map.Entry<Character, Double>> sortedFrequencies = new ArrayList<>(englishFrequencies.entrySet());

        sortedFrequencies.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        StringBuilder englishOutput = new StringBuilder();
        for (Map.Entry<Character, Double> entry : sortedFrequencies) {
            englishOutput.append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("%\n");
        }
        englishFrequencyArea.setText(englishOutput.toString());
        englishFrequencyArea.setCaretPosition(0);
    }

    private Map<Character, Double> getEnglishFrequencies() {
        Map<Character, Double> englishFrequencies = new HashMap<>();
        englishFrequencies.put('A', 8.17);
        englishFrequencies.put('B', 1.49);
        englishFrequencies.put('C', 2.78);
        englishFrequencies.put('D', 4.25);
        englishFrequencies.put('E', 12.70);
        englishFrequencies.put('F', 2.23);
        englishFrequencies.put('G', 2.02);
        englishFrequencies.put('H', 6.09);
        englishFrequencies.put('I', 7.00);
        englishFrequencies.put('J', 0.15);
        englishFrequencies.put('K', 0.77);
        englishFrequencies.put('L', 4.03);
        englishFrequencies.put('M', 2.41);
        englishFrequencies.put('N', 6.75);
        englishFrequencies.put('O', 7.51);
        englishFrequencies.put('P', 1.93);
        englishFrequencies.put('Q', 0.10);
        englishFrequencies.put('R', 5.99);
        englishFrequencies.put('S', 6.33);
        englishFrequencies.put('T', 9.06);
        englishFrequencies.put('U', 2.76);
        englishFrequencies.put('V', 0.98);
        englishFrequencies.put('W', 2.36);
        englishFrequencies.put('X', 0.15);
        englishFrequencies.put('Y', 1.97);
        englishFrequencies.put('Z', 0.07);
        return englishFrequencies;
    }

    private class AnalyzeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String encryptedText = inputArea.getText();
            Map<Character, Integer> frequencyMap = analyzeFrequency(encryptedText);
            StringBuilder output = new StringBuilder("Character Frequencies (%):\n");
            int totalLetters = frequencyMap.values().stream().mapToInt(Integer::intValue).sum();

            for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
                double percentage = (entry.getValue() / (double) totalLetters) * 100;
                output.append(entry.getKey()).append(": ").append(String.format("%.2f", percentage)).append("%\n");
            }

            outputArea.setText(output.toString());
            applySubstitutions(encryptedText);
        }

        private Map<Character, Integer> analyzeFrequency(String text) {
            Map<Character, Integer> frequencyMap = new HashMap<>();
            for (char c : text.toCharArray()) {
                if (Character.isLetter(c)) {
                    c = Character.toUpperCase(c);
                    frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
                }
            }
            return frequencyMap;
        }

        private void applySubstitutions(String encryptedText) {
            Map<Character, Character> substitutions = new HashMap<>();
            for (int i = 0; i < substitutionsModel.getRowCount(); i++) {
                String from = (String) substitutionsModel.getValueAt(i, 0);
                String to = (String) substitutionsModel.getValueAt(i, 1);
                substitutions.put(from.charAt(0), to.charAt(0));
            }

            StringBuilder substitutedText = new StringBuilder("Substituted Text:\n");
            for (char c : encryptedText.toCharArray()) {
                char substitutedChar;
                if (substitutions.containsKey(Character.toUpperCase(c))) {
                    substitutedChar = substitutions.get(Character.toUpperCase(c));
                    substitutedText.append(Character.toLowerCase(substitutedChar));
                } else if (Character.isLetter(c)) {
                    substitutedText.append(Character.toUpperCase(c));
                } else {
                    substitutedText.append(c);
                }
            }

            outputArea.append("\n" + substitutedText);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrequencyAnalysisTool tool = new FrequencyAnalysisTool();
            tool.setVisible(true);
        });
    }
}
