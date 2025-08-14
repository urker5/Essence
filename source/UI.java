import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class UI implements ActionListener {

    private JPanel centerPanel;
    private JFrame mainFrame;
    private JTextField termBox;
    private JTextField definitionBox;
    private JButton addButton;
    private JButton deleteButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scroll;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem loadFile;
    private JMenuItem saveFile;
    private JMenuItem newFile;

    private JMenu moduleMenu;
    private JMenuItem flashcardsItem;
    private JMenuItem learnItem;
    private JMenuItem writeItem;

    private SpringLayout springlayout;
    private Set currentSet;
    private String currentFileName;
    private boolean okPressed = false;

    public UI() {
        mainFrame = new JFrame("Essence");
        centerPanel = new JPanel();
        termBox = new JTextField("term", 10);
        definitionBox = new JTextField("definition", 10);
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        table = new JTable();
        tableModel = new DefaultTableModel();
        scroll = new JScrollPane(table);
        springlayout = new SpringLayout();
        currentSet = new Set();
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        loadFile = new JMenuItem("Open");
        saveFile = new JMenuItem("Save");
        newFile = new JMenuItem("New");
        moduleMenu = new JMenu("Modules");
        flashcardsItem = new JMenuItem("Flashcards");
        learnItem = new JMenuItem("Learn");
        writeItem = new JMenuItem("Write");

    }

    public void selectDefinitionBox() {
        definitionBox.requestFocus();
        definitionBox.setCaretPosition(0);
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public void updateTitle() { // should be called every time after currentet.createSet or currentSet.openSet
        mainFrame.setTitle("Essence - " + currentFileName + ".set");
    }

    public void updateFilenameForOpenFileDialog(JTextField textField, JFrame frame, boolean saving) {
        String temp = textField.getText();
        if (temp != null && !temp.equals("")) {
            currentFileName = temp;
        }

        okPressed = true;

        frame.dispose();

        if (currentFileName != null && okPressed) {
            currentSet.createSet(currentFileName);
            updateTitle();

        }
        okPressed = false;

        if (saving) {
            loadTableToSet();
            populateTableFromSet();
            currentSet.saveSet(currentFileName);
        } else {
            clearTable();

        }
    }

    public void openFileDialogBox(boolean saving) {
        JFrame frame = new JFrame("New File");
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        JLabel label = new JLabel("Name your New Set: ");
        JTextField textBox = new JTextField("", 10);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(270, 150);
        frame.setResizable(false);

        ok.addActionListener(e -> updateFilenameForOpenFileDialog(textBox, frame, saving));
        cancel.addActionListener(e -> frame.dispose());

        SpringLayout layout = new SpringLayout();
        frame.setLayout(layout);

        frame.add(label);
        frame.add(textBox);
        frame.add(ok);
        frame.add(cancel);

        layout.putConstraint(SpringLayout.WEST, label, 6, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, label, 50, SpringLayout.NORTH, frame);

        layout.putConstraint(SpringLayout.WEST, textBox, 5, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.NORTH, textBox, 50, SpringLayout.NORTH, frame);

        layout.putConstraint(SpringLayout.WEST, ok, 5, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.NORTH, ok, 10, SpringLayout.SOUTH, textBox);

        layout.putConstraint(SpringLayout.WEST, cancel, 5, SpringLayout.EAST, ok);
        layout.putConstraint(SpringLayout.NORTH, cancel, 10, SpringLayout.SOUTH, textBox);

        frame.setVisible(true);
    }

    public boolean isDuplicate(String term) {
        String temp;

        for (int i = 0; i < table.getRowCount(); i++) {
            temp = (String) table.getValueAt(i, 0);

            if (temp.equals(term)) {
                return true;
            }
        }
        return false;
    }

    public void addRow() {
        String term = termBox.getText();
        String def = definitionBox.getText();

        // clear text from text boxes
        termBox.setText("");
        definitionBox.setText("");

        // select text entry
        termBox.requestFocus();
        termBox.setCaretPosition(0);

        if (!isDuplicate(term)) {

            tableModel.addRow(new String[] { term, def });
            currentSet.addTerm(term, def);
            return;

        }

    }

    public void addRow(String term, String definition) {
        if (!isDuplicate(term)) {
            tableModel.addRow(new String[] { term, definition });
            currentSet.addTerm(term, definition);
            return;
        }
    }

    public void deleteSelectedRows() {
        int[] rows = table.getSelectedRows();
        int rowIndex;

        for (int i = 0; i < rows.length; i++) {
            rowIndex = rows[i] - i;
            currentSet.deleteTerm((String) tableModel.getValueAt(rowIndex, 0));
            tableModel.removeRow(rowIndex);

        }
    }

    public void loadTableToSet() {
        currentSet.clear();

        String term;
        String definition;

        for (int i = 0; i < table.getRowCount(); i++) { // Loop through the rows
            term = (String) table.getValueAt(i, 0);
            definition = (String) table.getValueAt(i, 1);

            currentSet.addTerm(term, definition);
        }

    }

    public void populateTableFromSet() {
        // clear table first
        clearTable();

        if (!currentSet.isNull()) {
            for (String term : currentSet.getTerms()) {
                addRow(term, currentSet.get(term));
            }
        }
    }

    public void newFile() {
        openFileDialogBox(false);
        // due to timeing issues, the file is created and opened in function:
        // updateFilenameForOpenFileDialog
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/sets"));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            currentFileName = fileChooser.getSelectedFile().getName();
            // remove .set from filename
            currentFileName = currentFileName.substring(0, currentFileName.length() - 4);
            currentSet.openSet(currentFileName);
            updateTitle();

            populateTableFromSet();

            // clear text fields
            termBox.setText("");
            definitionBox.setText("");
        }

    }

    public void saveFile() {
        if (currentFileName != null) {
            currentSet.saveSet(currentFileName);
        } else {
            // same code as nuwfile but set saving = true for table population
            openFileDialogBox(true);

        }
    }

    public void openFlashcards() {
        Flashcards flashSesh = new Flashcards(currentSet, mainFrame, currentFileName);
        flashSesh.runMain();
    }

    public void openLearn() {
        Learn leanSesh = new Learn(currentSet, mainFrame, currentFileName);
        leanSesh.runMain();
    }

    public void openWrite() {
        Write writeSesh = new Write(currentSet, mainFrame, currentFileName);
        writeSesh.runMain();
    }

    public void runMain() {
        // main frame

        mainFrame.setSize(1000, 550);
        mainFrame.setResizable(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // panel
        centerPanel.setPreferredSize(new Dimension(1000, 550));

        mainFrame.add(centerPanel, BorderLayout.CENTER);

        // textbox: cycles textboxes and addrow when enter pressed in textbox
        termBox.addActionListener(e -> selectDefinitionBox());
        definitionBox.addActionListener(e -> addRow());

        // buttons
        addButton.addActionListener(e -> addRow());
        deleteButton.addActionListener(e -> deleteSelectedRows());

        // table and scrollpane
        tableModel.addColumn("Term");
        tableModel.addColumn("Definition");

        // save when user edits a cell
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    loadTableToSet();
                }
            }
        });

        // add model to table
        table.setModel(tableModel);

        // file menubar
        mainFrame.setJMenuBar(menuBar);

        menuBar.add(fileMenu);

        fileMenu.add(newFile);
        fileMenu.add(loadFile);
        fileMenu.add(saveFile);

        newFile.addActionListener(e -> newFile());
        loadFile.addActionListener(e -> openFile());
        saveFile.addActionListener(e -> saveFile());

        // module menubar
        menuBar.add(moduleMenu);

        moduleMenu.add(flashcardsItem);
        moduleMenu.add(learnItem);
        moduleMenu.add(writeItem);

        flashcardsItem.addActionListener(e -> openFlashcards());
        learnItem.addActionListener(e -> openLearn());
        writeItem.addActionListener(e -> openWrite());

        // file keyboard shortcuts
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        loadFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        // module keyboard shortcuts
        flashcardsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        learnItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        writeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

        // configure layout
        centerPanel.setLayout(springlayout);

        centerPanel.add(termBox);
        centerPanel.add(definitionBox);
        centerPanel.add(addButton);
        centerPanel.add(scroll);
        centerPanel.add(deleteButton);

        springlayout.putConstraint(SpringLayout.WEST, termBox, 5, SpringLayout.WEST, centerPanel);
        springlayout.putConstraint(SpringLayout.WEST, definitionBox, 5, SpringLayout.WEST, centerPanel);

        springlayout.putConstraint(SpringLayout.NORTH, termBox, 5, SpringLayout.NORTH, centerPanel);
        springlayout.putConstraint(SpringLayout.NORTH, definitionBox, 5, SpringLayout.SOUTH, termBox);

        springlayout.putConstraint(SpringLayout.WEST, addButton, 5, SpringLayout.EAST, termBox);
        springlayout.putConstraint(SpringLayout.NORTH, addButton, 10, SpringLayout.NORTH, termBox);

        springlayout.putConstraint(SpringLayout.WEST, scroll, 30, SpringLayout.EAST, addButton);

        springlayout.putConstraint(SpringLayout.NORTH, deleteButton, 5, SpringLayout.SOUTH, addButton);
        springlayout.putConstraint(SpringLayout.WEST, deleteButton, 0, SpringLayout.WEST, addButton);

        // set visible
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

    }

}
