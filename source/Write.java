import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

public class Write {

    private JFrame mainFrame;
    private Set currentSet;

    private LinkedHashMap<String, String> tempSet;
    private LinkedHashMap<String, String> missedSet;

    private JFrame writeMain;

    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;

    private JTextArea writeText;

    private JLabel termLabel;

    private JLabel remainingLabel;
    private JLabel correctLabel;
    private JLabel missedLabel;

    private int remaining;
    private int correct;
    private int missed;

    private JButton enterButton;
    private JButton restartButton;
    private JButton missedButton;

    private Font defaultFont;

    private SpringLayout springLayout;

    private String currentTerm;
    private String currentDefinition;

    public Write(Set currSet, JFrame mainFrame, String currentFileName) {

        this.mainFrame = mainFrame;
        this.currentSet = currSet;

        // initialize UI elements
        writeMain = new JFrame("Write - " + currentFileName + ".set");

        topPanel = new JPanel();
        leftPanel = new JPanel();
        centerPanel = new JPanel();
        rightPanel = new JPanel();
        bottomPanel = new JPanel();

        writeText = new JTextArea(1, 20);

        termLabel = new JLabel("");
        enterButton = new JButton("Enter");
        restartButton = new JButton("Restart");
        missedButton = new JButton("Study Missed");

        defaultFont = new Font("Dialog", Font.BOLD, 14);

        remainingLabel = new JLabel("   Remaining: 0   ");
        correctLabel = new JLabel("Correct: 0   ");
        missedLabel = new JLabel("   Missed: 0");

        remaining = 0;
        correct = 0;
        missed = 0;

        springLayout = new SpringLayout();

        missedSet = new LinkedHashMap<String, String>();

        // hide mainframe from UI class
        mainFrame.setVisible(false);
    }

    private void resetFocus() {// set focus back to textarea for keylistener, needs to be called after
                               // ANY button is pressed
        writeText.requestFocusInWindow();
    }

    private void incrementLabel(String labelName) {
        switch (labelName) {
            case "correct":
                correct += 1;
                correctLabel.setText("Correct: " + correct + "   ");
                return;

            case "missed":
                missed += 1;
                missedLabel.setText("   Missed: " + missed);
                return;

            case "remaining":
                remaining += 1;
                remainingLabel.setText("   Remaining: " + remaining + "   ");
                return;

            default:
                return;

        }

    }

    private void decrementLabel(String labelName) {
        switch (labelName) {
            case "correct":
                correct -= 1;
                correctLabel.setText("Correct: " + correct + "   ");
                return;

            case "missed":
                missed -= 1;
                missedLabel.setText("   Missed: " + missed);
                return;

            case "remaining":
                remaining -= 1;
                remainingLabel.setText("   Remaining: " + remaining + "   ");
                return;

            default:
                return;

        }
    }

    private void checkAnswer() {
        resetFocus(); // called after any button is pressed

        if (tempSet != null) {
            String temp = writeText.getText();
            System.out.println(temp);
            writeText.setText(""); // clear text entry

            if (temp.equals(currentDefinition)) { // if answer is correct
                incrementLabel("correct");
            } else { // if answer is wrong
                incrementLabel("missed");
                decrementLabel("remaining");

                missedSet.put(currentTerm, currentDefinition);
            }

            // should run no matter the result
            tempSet.remove(currentTerm);
            populateTerm();
        }

    }

    private void restart() { // copies values from currentSet into tempset for studying, should be called
                             // when UI first boots
        resetFocus(); // called after any button is pressed

        if (!currentSet.isNull()) {

            tempSet = new LinkedHashMap<String, String>();

            remaining = 0;

            // loops through currentset and copies to tempSet
            for (String term : currentSet.getTerms()) {
                tempSet.put(term, currentSet.get(term));
                remaining++; // keeps track of total number of elements
            }

            // update tracking labels and tracking variabls

            missed = 0;
            correct = 0;

            missedLabel.setText("   Missed: 0");
            correctLabel.setText("Correct: 0   ");
            remainingLabel.setText("   Remaining: " + remaining + "   ");

            populateTerm();
        } else {
            System.out.println("null set in write:restart()");
        }

    }

    private void restartMissed() { // almost identical to restart() except missedSet instead of currentSet
        resetFocus(); // called after any button is pressed

        if (missed <= 0) { // if no missed terms, clear UI
            currentTerm = "";
            currentDefinition = "";
        }

        if (missedSet != null) {
            tempSet = new LinkedHashMap<String, String>();

            remaining = 0;

            // move missedSet to tempSet
            for (String term : missedSet.keySet()) {
                tempSet.put(term, missedSet.get(term));
                remaining++; // keeps track of total number of elements
            }

            // update tracking labels and tracking variabls

            missed = 0;
            correct = 0;

            missedLabel.setText("   Missed: 0");
            correctLabel.setText("Correct: 0   ");
            remainingLabel.setText("   Remaining: " + remaining + "   ");

            missedSet.clear(); // don't forget to clear the missed set!

            populateTerm();
        } else {
            System.out.println("Null missedSet in Write:restartMissed()");
        }
    }

    private void populateTerm() {
        if (tempSet != null) {
            if (numElements() <= 0) { // if no more elements, return
                // possible set text to ""
                return;
            } else { // else, update current term and definition from random value in tempset
                List<String> keyList = new ArrayList<>(tempSet.keySet());
                Random random = new Random();
                int termNumber = random.nextInt(tempSet.size());

                // update current vars
                currentTerm = keyList.get(termNumber);
                currentDefinition = tempSet.get(currentTerm);

                // update the UI
                termLabel.setText(currentTerm);

                if (remaining == 0) { // if no more terms, clear termlabel
                    termLabel.setText("");
                }
            }

        }
    }

    private int numElements() {
        if (tempSet != null) {
            int elements = 0;
            for (String term : tempSet.keySet()) {
                elements++;
            }
            return elements;
        }

        return 0;
    }

    public void runMain() {
        // main flashcard frame
        writeMain.setResizable(true);

        // panels
        centerPanel.setBackground(Color.lightGray); // temp for debug

        topPanel.setPreferredSize(new Dimension(100, 100));
        leftPanel.setPreferredSize(new Dimension(125, 100));
        centerPanel.setPreferredSize(new Dimension(100, 100));
        rightPanel.setPreferredSize(new Dimension(125, 100));
        bottomPanel.setPreferredSize(new Dimension(100, 100));

        writeMain.add(topPanel, BorderLayout.NORTH);
        writeMain.add(leftPanel, BorderLayout.WEST);
        writeMain.add(centerPanel, BorderLayout.CENTER);
        writeMain.add(rightPanel, BorderLayout.EAST);
        writeMain.add(bottomPanel, BorderLayout.SOUTH);

        // labels
        remainingLabel.setFont(defaultFont);
        correctLabel.setFont(defaultFont);
        missedLabel.setFont(defaultFont);
        termLabel.setFont(new Font("Courier New", 0, 16));

        // textentry
        writeText.setLineWrap(true);
        writeText.setFont(new Font("Courier New", 0, 16));
        writeText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "doNothing");

        // buttons
        enterButton.addActionListener(e -> checkAnswer());
        restartButton.addActionListener(e -> restart());
        missedButton.addActionListener(e -> restartMissed());

        // keylistener (needs to be added to textarea because that is the focused
        // component)
        writeText.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_ENTER) {// if enter, check answer
                    checkAnswer();
                } else if (keyCode == KeyEvent.VK_UP) {// if up arrow, restart
                    restart();
                } else if (keyCode == KeyEvent.VK_DOWN) {// if down arrow, study missed
                    restartMissed();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        // layout and packing
        centerPanel.setLayout(springLayout);
        bottomPanel.setLayout(springLayout);
        rightPanel.setLayout(springLayout);

        centerPanel.add(writeText);
        centerPanel.add(enterButton);
        centerPanel.add(termLabel);

        topPanel.add(correctLabel);
        topPanel.add(remainingLabel);
        topPanel.add(missedLabel);

        rightPanel.add(restartButton);
        rightPanel.add(missedButton);

        // center layout
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, writeText, 0, SpringLayout.HORIZONTAL_CENTER,
                centerPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, writeText, 0, SpringLayout.VERTICAL_CENTER,
                centerPanel);

        springLayout.putConstraint(SpringLayout.EAST, termLabel, -20, SpringLayout.WEST, writeText);
        springLayout.putConstraint(SpringLayout.NORTH, termLabel, 0, SpringLayout.NORTH, writeText);

        springLayout.putConstraint(SpringLayout.WEST, enterButton, 20, SpringLayout.EAST, writeText);
        springLayout.putConstraint(SpringLayout.NORTH, enterButton, 0, SpringLayout.NORTH, writeText);

        // right panel
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, restartButton, 0,
                SpringLayout.HORIZONTAL_CENTER,
                rightPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, restartButton, -20,
                SpringLayout.VERTICAL_CENTER,
                rightPanel);

        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, missedButton, 0,
                SpringLayout.HORIZONTAL_CENTER,
                rightPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, missedButton, 20,
                SpringLayout.VERTICAL_CENTER,
                rightPanel);

        // set visible

        writeMain.pack();
        writeMain.setSize(new Dimension(900, 600));
        writeMain.setVisible(true);
        restart(); // loads tempSet so needs to be called when ui boots, also calls populateTerm

        // when closed, show main program
        writeMain.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(true);
            }
        });
    }
}
