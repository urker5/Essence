import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.SpringLayout;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Flashcards {

    private JFrame mainFrame;
    private Set currentSet;

    private LinkedHashMap<String, String> tempSet;

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

    private JButton nextButton;
    private JButton flipButton;
    private JButton previousButton;
    private JButton restartButton;
    private JButton missedButton;

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

        remainingLabel = new JLabel("   Remaining: 0   ");
        correctLabel = new JLabel("Correct: 0   ");
        missedLabel = new JLabel("   Missed: 0");

        remaining = 0;
        correct = 0;
        missed = 0;

        nextButton = new JButton(" \u2713 "); // checkmark
        flipButton = new JButton("Flip Card");
        previousButton = new JButton("  x  ");
        restartButton = new JButton("Restart");
        missedButton = new JButton("Study Missed");

        springLayout = new SpringLayout();

        // hide mainframe from UI class
        mainFrame.setVisible(false);
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

        if (remaining > 0) { // only populate next card if there are terms left
            decrementLabel("remaining");
            incrementLabel("missed");
            populateNextCard();
        } else { // clear current term and definition
            currentTerm = "";
            currentDefinition = "";

        }
    }

    private void flipCard() {
        if (currentTerm != null && remaining != 0) { // second condition prevents revealing card when no terms remaining
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

            populateNextCard();
        } else {
            System.out.println("null set in flashcards:restart()");
        }

    }

    private void restartMissed() {

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
        // flashMain.setSize(900, 600);
        flashMain.setResizable(false);

        // panels: all five needed to make it look like a flashcard and not just a
        // random label
        // topPanel.setBackground(Color.red);
        // leftPanel.setBackground(Color.blue);
        centerPanel.setBackground(Color.lightGray);
        // rightPanel.setBackground(Color.yellow);
        // bottomPanel.setBackground(Color.magenta);

        topPanel.setPreferredSize(new Dimension(100, 100));
        leftPanel.setPreferredSize(new Dimension(100, 100));
        centerPanel.setPreferredSize(new Dimension(100, 100));
        rightPanel.setPreferredSize(new Dimension(100, 100));
        bottomPanel.setPreferredSize(new Dimension(100, 100));

        flashMain.add(topPanel, BorderLayout.NORTH);
        flashMain.add(leftPanel, BorderLayout.WEST);
        flashMain.add(centerPanel, BorderLayout.CENTER);
        flashMain.add(rightPanel, BorderLayout.EAST);
        flashMain.add(bottomPanel, BorderLayout.SOUTH);

        // textarea
        flashText.setEditable(false);
        // flashText.setLineWrap(true);
        flashText.setBackground(Color.LIGHT_GRAY);
        // flashText.setBounds(150, 100, 400, 200);
        flashText.setFont(new Font("Courier New", 0, 40));
        flashText.setCaretColor(Color.LIGHT_GRAY); // hides the cursor

        // buttons
        nextButton.addActionListener(e -> correctCard());
        previousButton.addActionListener(e -> wrongCard());
        flipButton.addActionListener(e -> flipCard());
        restartButton.addActionListener(e -> restart());
        missedButton.addActionListener(e -> restartMissed());

        // layout and packing
        centerPanel.setLayout(springLayout);
        // centerPanel.setLayout(null);
        bottomPanel.setLayout(springLayout);

        centerPanel.add(flashText);

        bottomPanel.add(previousButton);
        bottomPanel.add(flipButton);
        bottomPanel.add(nextButton);

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
        springLayout.putConstraint(SpringLayout.WEST, previousButton, 330, SpringLayout.WEST, bottomPanel);
        springLayout.putConstraint(SpringLayout.NORTH, previousButton, 5, SpringLayout.NORTH, bottomPanel);

        springLayout.putConstraint(SpringLayout.WEST, flipButton, 20, SpringLayout.EAST, previousButton);
        springLayout.putConstraint(SpringLayout.NORTH, flipButton, 5, SpringLayout.NORTH, previousButton);

        springLayout.putConstraint(SpringLayout.WEST, nextButton, 20, SpringLayout.EAST, flipButton);
        springLayout.putConstraint(SpringLayout.NORTH, nextButton, 0, SpringLayout.NORTH, previousButton);

        springLayout.putConstraint(SpringLayout.WEST, restartButton, 10, SpringLayout.WEST, rightPanel);
        springLayout.putConstraint(SpringLayout.NORTH, restartButton, 50, SpringLayout.NORTH, rightPanel); // notworking

        springLayout.putConstraint(SpringLayout.WEST, missedButton, 0, SpringLayout.EAST, restartButton);
        springLayout.putConstraint(SpringLayout.NORTH, missedButton, 10, SpringLayout.NORTH, restartButton);

        // set visible

        flashMain.pack();
        flashMain.setSize(new Dimension(900, 600));
        flashMain.setVisible(true);
        restart(); // loads tempSet so needs to be called when ui boots
        populateNextCard(); // needs tempSet already loaded to add card, initializes isOnTerm

        // when closed, show main program
        flashMain.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(true);
            }
        });
    }
}
