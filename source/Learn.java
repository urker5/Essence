import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import javax.swing.JOptionPane;
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

    private String currentTerm;
    private String currentDefinition;

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

    private String getDefinition(String term) { // returns the definition of given term
        return fullSet.get(term);
    }

    private void restart() {
        // initializes fullset, unfamiliar set, and labels (unfamiliar, missed, etc),
        // updates UI text labels to initial

        if (!currentSet.isNull()) {

            fullSet = new LinkedHashMap<String, String>();
            unfamiliarSet = new LinkedHashMap<String, String>();
            missedSet.clear(); // empty missed set

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

            populateTerm();

        } else {
            System.out.println("null set in learn:restart()");
        }
    }

    private void restartMissed() {

        if (missed <= 0) { // if no missed terms, clear UI
            currentTerm = "";
            currentDefinition = "";
            writeText.setText("");

            termLabel.setText("No Missed Terms to Study");
        }

        if (missedSet != null) {
            // populate familiarSet from missedSet
            familiarSet.clear();

            familiar = 0;

            for (String term : missedSet.keySet()) {

                familiarSet.put(term, getDefinition(term));
                familiar++;
            }

            // update tracking labels and variables
            missed = 0;
            correct = 0;

            familiarLabel.setText("Familiar: " + familiar + "   ");
            missedLabel.setText("   Missed: 0");
            correctLabel.setText("Correct: 0   ");

            missedSet.clear(); // clear missedSet

            populateTerm(); // should create a write question

        } else {
            System.out.println("null set in learn:restartMissed()");
        }

    }

    private void populateTerm() {
        // gets the current term and definition from the appropriate set (unfamiliar or
        // familiar) and creates either a write or multiple choice question. If both
        // sets are empty, no current term or definition

        if (!unfamiliarSet.isEmpty()) {
            currentTerm = getRandomTerm("unfamiliar");
            currentDefinition = getDefinition(currentTerm);

            toggleWriteVisibility(false);
            toggleMultipleChoiceVisibilty(true);
            createMultipleChoiceQuestion();

            termLabel.setText(currentTerm);

        } else if (!familiarSet.isEmpty()) {
            currentTerm = getRandomTerm("familiar");
            currentDefinition = getDefinition(currentTerm);

            toggleMultipleChoiceVisibilty(false);
            toggleWriteVisibility(true);
            createWriteQuestion();

            termLabel.setText(currentTerm);

        } else {
            currentTerm = "";
            currentDefinition = "";
            System.out.println("familiar and unfamiliar set should be empty");

            // all terms are either correct or missed
            toggleWriteVisibility(false);
            termLabel.setText("Round Completed");
        }
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

    private String getRandomTerm(String setName) { // returns random term from passed in set name
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

    private String getRandomDefinition(String setName) {
        List<String> keyList;
        Random random;
        int termNumber;

        switch (setName) {

            case "unfamiliar":
                keyList = new ArrayList<>(unfamiliarSet.keySet());

                random = new Random();
                termNumber = random.nextInt(unfamiliarSet.size());
                return getDefinition(keyList.get(termNumber));

            case "familiar":
                keyList = new ArrayList<>(familiarSet.keySet());

                random = new Random();
                termNumber = random.nextInt(familiarSet.size());
                return getDefinition(keyList.get(termNumber));

            case "full":
                keyList = new ArrayList<>(fullSet.keySet());

                random = new Random();
                termNumber = random.nextInt(fullSet.size());
                return getDefinition(keyList.get(termNumber));

            default:
                return "";

        }
    }

    private boolean checkListDuplicate(String[] list, String checkTerm) {// looops through parameter list and if
                                                                         // parameter term is found, returns false
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(checkTerm)) {
                return false;
            }
        }

        return true;
    }

    private void randomizeArray(String arr[]) {// idk if iv'e tested this yet, could be worth testing

        // Creating a object for Random class
        Random r = new Random();

        // Start from the last element and swap one by one. We don't
        // need to run for the first element that's why i > 0
        for (int i = arr.length - 1; i > 0; i--) {

            // Pick a random index from 0 to i
            int j = r.nextInt(i);

            // Swap arr[i] with the element at random index
            String temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

    }

    private boolean checkMultipleChoiceAnswer(String answer) {
        return answer.equals(currentDefinition);
    }

    private void toggleMultipleChoiceVisibilty() {// old version? delete?

        if (option1.isVisible()) {
            option1.setVisible(false);
            option2.setVisible(false);
            option3.setVisible(false);
            option4.setVisible(false);
        } else {
            option1.setVisible(true);
            option2.setVisible(true);
            option3.setVisible(true);
            option4.setVisible(true);
        }

    }

    private void toggleMultipleChoiceVisibilty(boolean visible) {

        if (!visible) {
            option1.setVisible(false);
            option2.setVisible(false);
            option3.setVisible(false);
            option4.setVisible(false);
        } else {
            option1.setVisible(true);
            option2.setVisible(true);
            option3.setVisible(true);
            option4.setVisible(true);
        }
    }

    private void toggleWriteVisibility() {// unused/old version? delete?
        if (writeText.isVisible()) {
            writeText.setVisible(false);
            enterButton.setVisible(false);
        } else {
            writeText.setVisible(true);
            enterButton.setVisible(true);
        }
    }

    private void toggleWriteVisibility(boolean visible) {
        if (visible) {
            writeText.setVisible(true);
            enterButton.setVisible(true);
        } else {
            writeText.setVisible(false);
            enterButton.setVisible(false);
        }
    }

    // create actionlistener, routes each buttonclick to action, updates sets and UI
    // labels
    ActionListener aListen = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

            if (checkMultipleChoiceAnswer(e.getActionCommand())) {
                System.out.println("you got the right answr");
                // update sets and labels
                familiarSet.put(currentTerm, currentDefinition);
                unfamiliarSet.remove(currentTerm);

                decrementLabel("unfamiliar");
                incrementLabel("familiar");

            } else {
                System.out.println("wrong answer: " + e.getActionCommand());
            }

            populateTerm(); // always called, is this right?

        }
    };

    private void createMultipleChoiceQuestion() {
        // get 4 random items, one from unfamiliar and the rest random but no duplicates
        if (fullSet != null) {

            String[] options = { "", "", "", "" }; // needed to check for duplicates

            String tempTerm;

            options[0] = getDefinition(currentTerm);

            for (int i = 1; i < options.length; i++) {

                tempTerm = getRandomDefinition("full");

                // if duplicate, run loop again
                while (!checkListDuplicate(options, tempTerm)) {
                    tempTerm = getRandomDefinition("full");
                }

                // once no duplicate, add term
                options[i] = tempTerm;

            }

            // make buttons visible and assign random names
            randomizeArray(options);
            buttonGroup.clearSelection();

            option1.setText(options[0]);
            option2.setText(options[1]);
            option3.setText(options[2]);
            option4.setText(options[3]);

        } else {
            System.out.println("null set in  Learn:createMutlipleChoiceQuestion");
        }

    }

    public void checkWriteAnswer() {
        String temp = writeText.getText(); // get text
        writeText.setText(""); // clear text entry

        if (familiar > 0) {

            if (temp.equals(currentDefinition)) {
                System.out.println("Correct!");

                incrementLabel("correct");

            } else {
                System.out.println("wroooooong: " + temp);

                incrementLabel("missed");

                missedSet.put(currentTerm, currentDefinition);
                System.out.println(missedSet.toString());
            }

            familiarSet.remove(currentTerm);
            decrementLabel("familiar");

            populateTerm(); // ????, populateTerm already calls createWriteQuestion
            // createWriteQuestion();
        }
    }

    private void createWriteQuestion() {
        System.out.println("creating write question");

        if (familiarSet != null) {

            writeText.setText("");
            writeText.requestFocusInWindow();
        } else {
            System.out.println("null set in Learn:createWriteQuestion");
        }
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
        // enterButton.addActionListener(e -> checkAnswer());
        restartButton.addActionListener(e -> restart());
        missedButton.addActionListener(e -> restartMissed());

        // buttongroup and radiobuttons
        buttonGroup.add(option1);
        buttonGroup.add(option2);
        buttonGroup.add(option3);
        buttonGroup.add(option4);

        // add actionlistener
        option1.addActionListener(aListen);
        option2.addActionListener(aListen);
        option3.addActionListener(aListen);
        option4.addActionListener(aListen);

        // keybinds
        Action checkWriteAnswerAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                checkWriteAnswer();
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
                checkWriteAnswerAction);
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

        // option1.setText("option 1");
        // option2.setText("option 2");
        // option3.setText("option 3");
        // option4.setText("option 4");

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
