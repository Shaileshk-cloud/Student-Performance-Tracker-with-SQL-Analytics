import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainGUI extends JFrame {
    private JTable studentTable, rankTable, individualMarksTable;
    private StudentService studentService;
    private AnalyticsService analyticsService;

    public MainGUI() {
        setTitle("Comprehensive Student Analytics System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        studentService = new StudentService();
        analyticsService = new AnalyticsService();

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Students", createStudentPanel());
        tabs.add("Marks", createMarksPanel());
        tabs.add("Rankings", createAnalyticsPanel());
        tabs.add("Individual Marks", createIndividualMarksPanel());

        add(tabs);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        studentTable = new JTable();
        JButton loadBtn = new JButton("Load Students");
        loadBtn.addActionListener(e -> loadStudents());

        JPanel inputPanel = new JPanel();
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(10);
        JTextField classField = new JTextField(5);
        JButton addBtn = new JButton("Add Student");

        inputPanel.add(new JLabel("ID:")); inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Class:")); inputPanel.add(classField);
        inputPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String cls = classField.getText();
            if (studentService.insertStudent(id, name, cls)) {
                JOptionPane.showMessageDialog(this, "Student added.");
                loadStudents();
            }
        });

        panel.add(loadBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        rankTable = new JTable();
        JButton loadBtn = new JButton("Load Rankings");
        loadBtn.addActionListener(e -> loadRankings());
        panel.add(loadBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(rankTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new FlowLayout());
        JTextField studentField = new JTextField(5);
        JTextField subjectField = new JTextField(5);
        JTextField marksField = new JTextField(5);

        JButton addButton = new JButton("Add Marks");

        form.add(new JLabel("Student ID:"));
        form.add(studentField);

        form.add(new JLabel("Subject ID:"));
        form.add(subjectField);

        form.add(new JLabel("Marks:"));
        form.add(marksField);

        form.add(addButton);

        addButton.addActionListener(e -> {
            try {
                int studentId = Integer.parseInt(studentField.getText());
                int subjectId = Integer.parseInt(subjectField.getText());
                int marks = Integer.parseInt(marksField.getText());

                if (studentService.insertMark(studentId, subjectId, marks)) {
                    JOptionPane.showMessageDialog(this, "Marks inserted successfully.");
                    studentField.setText("");
                    subjectField.setText("");
                    marksField.setText("");
                    loadRankings();
                } else {
                    JOptionPane.showMessageDialog(this, "Insertion failed. Check input.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
            }
        });

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createIndividualMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        individualMarksTable = new JTable();

        JPanel top = new JPanel(new FlowLayout());
        JTextField studentIdField = new JTextField(5);
        JButton loadBtn = new JButton("Load Marks");

        top.add(new JLabel("Student ID:"));
        top.add(studentIdField);
        top.add(loadBtn);

        loadBtn.addActionListener(e -> {
            try {
                int studentId = Integer.parseInt(studentIdField.getText());
                List<String[]> data = studentService.getIndividualMarks(studentId);
                String[] cols = {"Subject ID", "Marks"};
                DefaultTableModel model = new DefaultTableModel(cols, 0);
                for (String[] row : data) model.addRow(row);
                individualMarksTable.setModel(model);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid student ID");
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(individualMarksTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadStudents() {
        List<String[]> data = studentService.getAllStudents();
        String[] cols = {"ID", "Name", "Class"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (String[] row : data) model.addRow(row);
        studentTable.setModel(model);
    }

    private void loadRankings() {
        List<String[]> data = analyticsService.getStudentRanks();
        String[] cols = {"Student ID", "Name", "Total Marks", "Rank"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (String[] row : data) model.addRow(row);
        rankTable.setModel(model);
    }
}