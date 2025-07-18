import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

public class Flashcards {

    private JFrame mainFrame;
    private Set currentSet;

    private JFrame flashMain;

    private JPanel topPanel;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;

    private JTextArea flashText;

    private JLabel remainingLabel;
    private JLabel correctLabel;
    private JLabel missedLabel;

    private JButton nextButton;
    private JButton flipButton;
    private JButton previousButton;
    private JButton restartButton;
    private JButton missedButton;

    private SpringLayout springLayout;

    public Flashcards(Set currentSet, JFrame mainFrame, String currentFileName) {

        this.mainFrame = mainFrame;
        this.currentSet = currentSet;

        // initialize UI elements
        flashMain = new JFrame("Flashcards - " + currentFileName + ".set");

        topPanel = new JPanel();
        leftPanel = new JPanel();
        centerPanel = new JPanel();
        rightPanel = new JPanel();
        bottomPanel = new JPanel();

        flashText = new JTextArea();

        remainingLabel = new JLabel("   Remaining: 0   ");
        correctLabel = new JLabel("Correct: 0   ");
        missedLabel = new JLabel("   Missed: 0");

        nextButton = new JButton("  >  ");
        flipButton = new JButton("Flip Card");
        previousButton = new JButton("  <  ");
        restartButton = new JButton("Restart");
        missedButton = new JButton("Study Missed");

        springLayout = new SpringLayout();

        // hide mainframe from UI class
        mainFrame.setVisible(false);
    }

    public void populateCard(ArrayList<String> terms) {
    }

    public void nextCard() {

    }

    public void previousCard() {

    }

    public void flipCard() {

    }

    public void restart() {

    }

    public void restartMissed() {

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
        flashText.setLineWrap(true);
        flashText.setBackground(Color.LIGHT_GRAY);
        // flashText.setBounds(150, 100, 400, 200);
        flashText.setFont(new Font("Courier New", 0, 40));
        flashText.setCaretColor(Color.LIGHT_GRAY); // hides the cursor

        // buttons
        nextButton.addActionListener(e -> nextCard());
        previousButton.addActionListener(e -> previousCard());
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

        // when closed, show main program
        flashMain.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(true);
            }
        });
    }
}
