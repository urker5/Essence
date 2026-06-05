import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
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

    private void resetFocus() {// call after every button press
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
        resetFocus();

        if (tempSet != null && remaining > 0) {// also checks if there are terms left
            String temp = writeText.getText(); // get text

            writeText.setText(""); // clear text entry

            if (temp.equals(currentDefinition)) { // if answer is correct
                incrementLabel("correct");
                decrementLabel("remaining");
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
        resetFocus();
        writeText.setText("");

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
        resetFocus();
        writeText.setText("");

        if (missed <= 0) { // if no missed terms, clear UI
            currentTerm = "";
            currentDefinition = "";
            writeText.setText("");
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
            if (numElements() <= 0) { // if no more elements, return and set label blank
                termLabel.setText("");
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

                // if (remaining == 0) { // if no more terms, clear termlabel
                // termLabel.setText("");
                // }
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
        // main write frame
        writeMain.setResizable(true);

        // panels
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

        // buttons
        enterButton.addActionListener(e -> checkAnswer());
        restartButton.addActionListener(e -> restart());
        missedButton.addActionListener(e -> restartMissed());

        // keybinds
        Action checkAnswerAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        };

        Action restartAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        };

        Action restartMissedAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                restartMissed();
            }
        };

        // remove keybinds from buttons and textarea
        enterButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "do nothing");
        restartButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "do nothing");
        missedButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "do nothing");

        writeText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "doNothing");
        writeText.getInputMap().put(KeyStroke.getKeyStroke("UP"), "do nothing");
        writeText.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "do nothing");

        // add keybinds for textarea
        writeText.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"),
                checkAnswerAction);
        writeText.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), restartAction);
        writeText.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"),
                restartMissedAction);

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
