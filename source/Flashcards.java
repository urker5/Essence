import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Flashcards {

    private JFrame mainFrame;
    private Set currentSet;

    private LinkedHashMap<String, String> tempSet;
    private LinkedHashMap<String, String> missedSet;

    private JFrame flashMain;

    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;

    private CustomTextPane flashText;

    private JLabel remainingLabel;
    private JLabel correctLabel;
    private JLabel missedLabel;

    private int remaining;
    private int correct;
    private int missed;

    private JButton correctButton;
    private JButton flipButton;
    private JButton wrongButton;
    private JButton restartButton;
    private JButton missedButton;

    private Font defaultFont;

    private SpringLayout springLayout;

    private String currentTerm;
    private String currentDefinition;

    private boolean isOnTerm;

    public Flashcards(Set currSet, JFrame mainFrame, String currentFileName) {

        this.mainFrame = mainFrame;
        this.currentSet = currSet;

        // initialize UI elements
        flashMain = new JFrame("Flashcards - " + currentFileName + ".set");

        topPanel = new JPanel();
        leftPanel = new JPanel();
        centerPanel = new JPanel();
        rightPanel = new JPanel();
        bottomPanel = new JPanel();

        flashText = new CustomTextPane(true);

        defaultFont = new Font("Dialog", Font.BOLD, 14);

        remainingLabel = new JLabel("   Remaining: 0   ");
        correctLabel = new JLabel("Correct: 0   ");
        missedLabel = new JLabel("   Missed: 0");

        remaining = 0;
        correct = 0;
        missed = 0;

        correctButton = new JButton(" \u2713 "); // checkmark
        flipButton = new JButton("Flip Card");
        wrongButton = new JButton("  x  ");
        restartButton = new JButton("Restart");
        missedButton = new JButton("Study Missed");

        springLayout = new SpringLayout();

        missedSet = new LinkedHashMap<String, String>();

        // hide mainframe from UI class
        mainFrame.setVisible(false);
    }

    private void resetFocus() {// set focus back to textpane for keylistener, needs to be called after ANY
                               // button is pressed
        flashText.requestFocusInWindow();
    }

    private void cardSetText(String text) {
        flashText.setText(text);

        // center text
        StyledDocument doc = flashText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    private void checkClearCard() {
        if (remaining == 0) {
            flashText.setText("");
        }
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
                checkClearCard(); // if remaining =0, clear card
                return;

            default:
                return;

        }
    }

    private void correctCard() {
        resetFocus(); // called after any button is pressed

        if (remaining > 0) { // only populate next card if there are terms left
            decrementLabel("remaining");
            incrementLabel("correct");
            populateNextCard();
        } else { // clear current term and definition
            currentTerm = "";
            currentDefinition = "";
        }

    }

    private void wrongCard() {
        resetFocus(); // called after any button is pressed

        if (remaining > 0) { // only populate next card if there are terms left
            decrementLabel("remaining");
            incrementLabel("missed");

            missedSet.put(currentTerm, currentDefinition); // if missed, add term to missed set

            populateNextCard();
        } else { // clear current term and definition
            currentTerm = "";
            currentDefinition = "";

        }
    }

    private void flipCard() {
        resetFocus(); // called after any button is pressed

        if (currentTerm != null && remaining > 0) { // second condition prevents revealing card when no terms remaining
            if (isOnTerm) {
                cardSetText(currentDefinition);
                isOnTerm = false; // on definition now
            } else {
                cardSetText(currentTerm);
                isOnTerm = true; // on term now
            }

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

            populateNextCard(); // remember this! fogetting this is called in this function could cause issues
        } else {
            System.out.println("null set in flashcards:restart()");
        }

    }

    private void restartMissed() { // almost identical to restart() except missedSet instead of currentSet
        resetFocus(); // called after any button is pressed

        if (missed <= 0) { // if no missed terms, clear UI
            currentTerm = "";
            currentDefinition = "";
            cardSetText("");
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

            populateNextCard(); // remember this! fogetting this is called in this function could cause issues

        } else {
            System.out.println("Null missedSet in Flashcards:restartMissed()");
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

    private void populateNextCard() {
        if (tempSet != null) {
            if (numElements() <= 0) { // if no more elements, return
                return;
            } else { // else, update current term and definition from random value in tempset
                List<String> keyList = new ArrayList<>(tempSet.keySet());
                Random random = new Random();
                int termNumber = random.nextInt(tempSet.size());

                currentTerm = keyList.get(termNumber);
                currentDefinition = tempSet.get(currentTerm);

                // remove from tempSet so wont get chosen again
                tempSet.remove(currentTerm);

                // update the UI
                cardSetText(currentTerm);
                isOnTerm = true; // needed for flip card

                if (remaining == 0) { // if no more terms, clear card
                    cardSetText("");
                }
            }

        }
    }

    public void runMain() {
        // main flashcard frame
        flashMain.setResizable(true);

        // panels: all five needed to make it look like a flashcard and not just a

        centerPanel.setBackground(Color.lightGray);

        topPanel.setPreferredSize(new Dimension(100, 100));
        leftPanel.setPreferredSize(new Dimension(125, 100));
        centerPanel.setPreferredSize(new Dimension(100, 100));
        rightPanel.setPreferredSize(new Dimension(125, 100));
        bottomPanel.setPreferredSize(new Dimension(100, 100));

        flashMain.add(topPanel, BorderLayout.NORTH);
        flashMain.add(leftPanel, BorderLayout.WEST);
        flashMain.add(centerPanel, BorderLayout.CENTER);
        flashMain.add(rightPanel, BorderLayout.EAST);
        flashMain.add(bottomPanel, BorderLayout.SOUTH);

        // labels
        remainingLabel.setFont(defaultFont);
        correctLabel.setFont(defaultFont);
        missedLabel.setFont(defaultFont);

        // textpane
        flashText.setEditable(false);
        flashText.setBackground(Color.LIGHT_GRAY);
        flashText.setFont(new Font("Courier New", 0, 40));
        flashText.setCaretColor(Color.LIGHT_GRAY); // hides the cursor

        // buttons
        correctButton.addActionListener(e -> correctCard());
        wrongButton.addActionListener(e -> wrongCard());
        flipButton.addActionListener(e -> flipCard());
        restartButton.addActionListener(e -> restart());
        missedButton.addActionListener(e -> restartMissed());

        // keybinds
        Action flipCardAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                flipCard();
            }
        };

        Action wrongCardAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                wrongCard();
            }
        };

        Action correctCardAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                correctCard();
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

        flashText.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), flipCardAction);
        flashText.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), correctCardAction);
        flashText.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), wrongCardAction);
        flashText.getInputMap().put(KeyStroke.getKeyStroke("UP"), restartAction);
        flashText.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), restartMissedAction);

        // flashText.addKeyListener(new KeyListener() {
        // @Override
        // public void keyPressed(KeyEvent e) {
        // int keyCode = e.getKeyCode();

        // if (keyCode == KeyEvent.VK_SPACE) {// if enter, flip card
        // flipCard();
        // } else if (keyCode == KeyEvent.VK_RIGHT) { // if right arrow, correctButton
        // correctCard();
        // } else if (keyCode == KeyEvent.VK_LEFT) { // if left arrow, missedButton
        // wrongCard();
        // } else if (keyCode == KeyEvent.VK_UP) {// if up arrow, restart
        // restart();
        // } else if (keyCode == KeyEvent.VK_DOWN) {// if down arrow, study missed
        // restartMissed();
        // }
        // }

        // @Override
        // public void keyTyped(KeyEvent e) {

        // }

        // @Override
        // public void keyReleased(KeyEvent e) {

        // }
        // });

        // layout and packing
        centerPanel.setLayout(springLayout);
        bottomPanel.setLayout(springLayout);
        rightPanel.setLayout(springLayout);

        centerPanel.add(flashText);

        bottomPanel.add(wrongButton);
        bottomPanel.add(flipButton);
        bottomPanel.add(correctButton);

        topPanel.add(correctLabel);
        topPanel.add(remainingLabel);
        topPanel.add(missedLabel);

        rightPanel.add(restartButton);
        rightPanel.add(missedButton);

        // center layout
        springLayout.putConstraint(SpringLayout.WEST, flashText, 150, SpringLayout.WEST, centerPanel);
        springLayout.putConstraint(SpringLayout.EAST, flashText, -150, SpringLayout.EAST, centerPanel);
        springLayout.putConstraint(SpringLayout.NORTH, flashText, 100, SpringLayout.NORTH, centerPanel);

        // bottom layout

        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, flipButton, 0, SpringLayout.HORIZONTAL_CENTER,
                bottomPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, flipButton, 0, SpringLayout.VERTICAL_CENTER,
                bottomPanel);

        // set everything based on flipbutton as reference
        springLayout.putConstraint(SpringLayout.EAST, wrongButton, -30, SpringLayout.WEST, flipButton);
        springLayout.putConstraint(SpringLayout.NORTH, wrongButton, -5, SpringLayout.NORTH, flipButton);

        springLayout.putConstraint(SpringLayout.WEST, correctButton, 30, SpringLayout.EAST, flipButton);
        springLayout.putConstraint(SpringLayout.NORTH, correctButton, 0, SpringLayout.NORTH, wrongButton);

        // right panel
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, restartButton, 0, SpringLayout.HORIZONTAL_CENTER,
                rightPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, restartButton, -20, SpringLayout.VERTICAL_CENTER,
                rightPanel);

        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, missedButton, 0, SpringLayout.HORIZONTAL_CENTER,
                rightPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, missedButton, 20, SpringLayout.VERTICAL_CENTER,
                rightPanel);

        // set visible

        flashMain.pack();
        flashMain.setSize(new Dimension(900, 600));
        flashMain.setVisible(true);

        restart(); // loads tempSet so needs to be called when ui boots, also calls populateCard

        // when closed, show main program
        flashMain.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(true);
            }
        });
    }
}
