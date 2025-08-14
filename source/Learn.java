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

public class Learn {

    private JFrame mainFrame;
    private Set currentSet;

    private LinkedHashMap<String, String> unfamiliarSet;
    private LinkedHashMap<String, String> familiarSet;
    private LinkedHashMap<String, String> missedSet;

    private JFrame writeMain;

    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;

    private JTextArea writeText;

    private JLabel termLabel;

    private JLabel unfamiliarLabel;
    private JLabel familiarLabel;
    private JLabel correctLabel;
    private JLabel missedLabel;

    private int unfamiliar;
    private int familiar;
    private int correct;
    private int missed;

    private JButton enterButton;
    private JButton restartButton;
    private JButton missedButton;

    private Font defaultFont;

    private SpringLayout springLayout;

    private String currentTerm;
    private String currentDefinition;

    private boolean familiarMode;

    public Learn(Set currSet, JFrame mainFrame, String currentFileName) {

        this.mainFrame = mainFrame;
        this.currentSet = currSet;

        // initialize UI elements
        writeMain = new JFrame("Learn - " + currentFileName + ".set");

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

        unfamiliarLabel = new JLabel("Unfamiliar: 0   ");
        familiarLabel = new JLabel("Familiar: 0   ");
        correctLabel = new JLabel("Correct: 0   ");
        missedLabel = new JLabel("   Missed: 0");

        unfamiliar = 0;
        familiar = 0;
        correct = 0;
        missed = 0;

        springLayout = new SpringLayout();

        familiarSet = new LinkedHashMap<String, String>();
        missedSet = new LinkedHashMap<String, String>();

        familiarMode = false;

        // hide mainframe from UI class
        mainFrame.setVisible(false);
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

            case "unfamiliar":
                unfamiliar += 1;
                unfamiliarLabel.setText("Unfamiliar: " + unfamiliar + "   ");
                return;

            case "familiar":
                familiar += 1;
                familiarLabel.setText("Familiar: " + familiar + "   ");
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

            case "unfamiliar":
                unfamiliar -= 1;
                unfamiliarLabel.setText("Unfamiliar: " + unfamiliar + "   ");
                return;

            case "familiar":
                familiar -= 1;
                familiarLabel.setText("Familiar: " + familiar + "   ");
                return;

            default:
                return;

        }
    }

    private void checkAnswer() {

    }

    private void restart() {

    }

    private void restartMissed() { // almost identical to restart() except missedSet instead of currentSet

    }

    private void populateTerm() {

    }

    private int numElements() {
        if (unfamiliarSet != null) {
            int elements = 0;
            for (String term : unfamiliarSet.keySet()) {
                elements++;
            }
            return elements;
        }

        return 0;
    }

    public void createMultipleChoiceQuestion() {

    }

    public void createWriteQuestion() {

    }

    public void runMain() {
        // main flashcard frame
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
        unfamiliarLabel.setFont(defaultFont);
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

        topPanel.add(unfamiliarLabel);
        topPanel.add(familiarLabel);
        topPanel.add(correctLabel);
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
        restart(); // loads unfamiliarSet so needs to be called when ui boots, also calls
                   // populateTerm

        // when closed, show main program
        writeMain.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(true);
            }
        });
    }
}
