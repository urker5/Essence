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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

public class Learn {

    private JFrame mainFrame;
    private Set currentSet;

    private LinkedHashMap<String, String> fullSet;
    private LinkedHashMap<String, String> unfamiliarSet;
    private LinkedHashMap<String, String> familiarSet;
    private LinkedHashMap<String, String> missedSet;

    private JFrame learnMain;

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

    private ButtonGroup buttonGroup;

    private JRadioButton option1;
    private JRadioButton option2;
    private JRadioButton option3;
    private JRadioButton option4;

    private SpringLayout springLayout;

    // private String currentTerm;
    // private String currentDefinition;

    private boolean familiarMode;

    public Learn(Set currSet, JFrame mainFrame, String currentFileName) {

        this.mainFrame = mainFrame;
        this.currentSet = currSet;

        // initialize UI elements
        learnMain = new JFrame("Learn - " + currentFileName + ".set");

        topPanel = new JPanel();
        leftPanel = new JPanel();
        centerPanel = new JPanel();
        rightPanel = new JPanel();
        bottomPanel = new JPanel();

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

        // initialize write components
        writeText = new JTextArea(1, 20);

        termLabel = new JLabel("");
        enterButton = new JButton("Enter");
        restartButton = new JButton("Restart");
        missedButton = new JButton("Study Missed");

        defaultFont = new Font("Dialog", Font.BOLD, 14);

        // initialize multiple choice components
        buttonGroup = new ButtonGroup();

        option1 = new JRadioButton();
        option2 = new JRadioButton();
        option3 = new JRadioButton();
        option4 = new JRadioButton();

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

    private String getDefinition(String term) { // returns the definition of given term
        return fullSet.get(term);
    }

    private void restart() {

        if (!currentSet.isNull()) {

            fullSet = new LinkedHashMap<String, String>();
            unfamiliarSet = new LinkedHashMap<String, String>();

            unfamiliar = 0;

            // loops through currentset and copies to fullSet
            for (String term : currentSet.getTerms()) {

                // full and unfamiliar set need to be the same at the beginning
                fullSet.put(term, currentSet.get(term));
                unfamiliarSet.put(term, currentSet.get(term));
                unfamiliar++; // keeps track of total number of elements
            }

            // update tracking labels and tracking variabls
            familiar = 0;
            missed = 0;
            correct = 0;

            unfamiliarLabel.setText("Unfamiliar: " + unfamiliar + "   ");
            familiarLabel.setText("Familiar: " + familiar + "   ");
            missedLabel.setText("   Missed: 0");
            correctLabel.setText("Correct: 0   ");

            createMultipleChoiceQuestion();
        } else {
            System.out.println("null set in learn:restart()");
        }
    }

    private void restartMissed() {

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

    private String getRandomTerm(LinkedHashMap<String, String> set, String setName) { // used to support cMCQ
        List<String> keyList;
        Random random;
        int termNumber;

        switch (setName) {

            case "unfamiliar":
                keyList = new ArrayList<>(unfamiliarSet.keySet());

                random = new Random();
                termNumber = random.nextInt(unfamiliarSet.size());
                return keyList.get(termNumber);

            case "familiar":
                keyList = new ArrayList<>(familiarSet.keySet());

                random = new Random();
                termNumber = random.nextInt(familiarSet.size());
                return keyList.get(termNumber);

            case "full":
                keyList = new ArrayList<>(fullSet.keySet());

                random = new Random();
                termNumber = random.nextInt(fullSet.size());
                return keyList.get(termNumber);

            default:
                return "";

        }
    }

    private void createMultipleChoiceQuestion() {
        // get 4 random items, one from unfamiliar and the rest random but no duplicates
        if (fullSet != null) {

            String[] options = { "", "", "", "" }; // needed to check for duplicates

            boolean completed = false;

            // options[0] = keyList.get(termNumber);

        }

    }

    private void createWriteQuestion() {

    }

    public void runMain() {
        // initialize all ui elements
        // main learn frame
        learnMain.setResizable(true);

        // panels
        topPanel.setPreferredSize(new Dimension(100, 100));
        leftPanel.setPreferredSize(new Dimension(125, 100));
        centerPanel.setPreferredSize(new Dimension(100, 100));
        rightPanel.setPreferredSize(new Dimension(125, 100));
        bottomPanel.setPreferredSize(new Dimension(100, 100));

        learnMain.add(topPanel, BorderLayout.NORTH);
        learnMain.add(leftPanel, BorderLayout.WEST);
        learnMain.add(centerPanel, BorderLayout.CENTER);
        learnMain.add(rightPanel, BorderLayout.EAST);
        learnMain.add(bottomPanel, BorderLayout.SOUTH);

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

        // buttongroup and radiobuttons
        buttonGroup.add(option1);
        buttonGroup.add(option2);
        buttonGroup.add(option3);
        buttonGroup.add(option4);

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

        // center
        centerPanel.add(termLabel);

        // // write
        centerPanel.add(writeText);
        centerPanel.add(enterButton);

        // // multiple choice
        centerPanel.add(option1);
        centerPanel.add(option2);
        centerPanel.add(option3);
        centerPanel.add(option4);

        // top panel
        topPanel.add(unfamiliarLabel);
        topPanel.add(familiarLabel);
        topPanel.add(correctLabel);
        topPanel.add(missedLabel);

        // right panel
        rightPanel.add(restartButton);
        rightPanel.add(missedButton);

        // center layout
        // // general and write
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, writeText, 0, SpringLayout.HORIZONTAL_CENTER,
                centerPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, writeText, 0, SpringLayout.VERTICAL_CENTER,
                centerPanel);

        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, termLabel, 0, SpringLayout.HORIZONTAL_CENTER,
                centerPanel);
        springLayout.putConstraint(SpringLayout.SOUTH, termLabel, -50, SpringLayout.NORTH, writeText);

        springLayout.putConstraint(SpringLayout.WEST, enterButton, 20, SpringLayout.EAST, writeText);
        springLayout.putConstraint(SpringLayout.NORTH, enterButton, 0, SpringLayout.NORTH, writeText);

        // // multiple choice
        springLayout.putConstraint(SpringLayout.EAST, option1, 20, SpringLayout.WEST, writeText);
        springLayout.putConstraint(SpringLayout.SOUTH, option1, -20, SpringLayout.NORTH, writeText);

        springLayout.putConstraint(SpringLayout.WEST, option2, 5, SpringLayout.EAST, option1);
        springLayout.putConstraint(SpringLayout.NORTH, option2, 0, SpringLayout.NORTH, option1);

        springLayout.putConstraint(SpringLayout.WEST, option3, 5, SpringLayout.EAST, option2);
        springLayout.putConstraint(SpringLayout.NORTH, option3, 0, SpringLayout.NORTH, option2);

        springLayout.putConstraint(SpringLayout.WEST, option4, 5, SpringLayout.EAST, option3);
        springLayout.putConstraint(SpringLayout.NORTH, option4, 0, SpringLayout.NORTH, option3);

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

        // temp ui layout testing
        termLabel.setText("term");

        option1.setText("option 1");
        option2.setText("option 2");
        option3.setText("option 3");
        option4.setText("option 4");

        // writeText.setVisible(false);
        // enterButton.setVisible(false);

        // set visible
        learnMain.pack();
        learnMain.setSize(new Dimension(900, 600));
        learnMain.setVisible(true);
        restart(); // loads unfamiliarSet so needs to be called when ui boots

        // when closed, show main program
        learnMain.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(true);
            }
        });
    }
}
