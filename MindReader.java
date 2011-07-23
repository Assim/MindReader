/**
 * MindReader.java
 * Copyright 2010 Assim Al-Marhuby
 * assim1991@gmail.com
 * http://assim.me/
 *
 * LICENSE:
 * Mind Reader by Assim Al-Marhuby is licensed under a
 * Creative Commons Attribution-NonCommercial 3.0 Unported License.
 * The copy of the license can be found at:
 * <http://creativecommons.org/licenses/by-nc/3.0/>.
 */

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * Runs the Mind Reader game
 */
public class MindReader
{
	/** Maximum numbers that the Mind Reader can guess, if value is 100, then it will ask you to think of a number from 1 to 100. */
	public static final int MAX_NUMBER = 100;

	/** Numbers to be displayed in each question. */
	public static final int NUMBERS_PER_QUESTION = 25;

	/** Current question number. */
	private int currentQuestion = 0;

	/** Holds all possible numbers that the user has in mind. */
	private ArrayList<Integer> possibleNumbers = new ArrayList<Integer>();

	/** Holds all denied numbers that is not in the user's mind. */
	private ArrayList<Integer> deniedNumbers = new ArrayList<Integer>();

	/** Holds current question numbers that are shown in the question (will be used for possible/denied numbers calculation). */
	private ArrayList<Integer> currentQuestionNumbers = new ArrayList<Integer>();

	/** Holds current question numbers that are visible to the user and some numbers might not be used for possible/denied numbers calculation. */
	private ArrayList<Integer> currentQuestionNumbersWithPlaceholder = new ArrayList<Integer>();

	/** The application frame. */
	private JFrame frame;

	/** Label to hold the text before starting the game. */
	private JLabel introduction = new JLabel("Think of a number from 1 to 100, and then click on start.");

	/** Start button. */
	private JButton start = new JButton("Start");

	/** Label for holding the current question number. */
	private JLabel questionNo = new JLabel();

	/** Label for holding the question. */
	private JLabel question = new JLabel("Is the number you're thinking of mentioned below?");

	/** Label for holding the visible numbers on the question. */
	private JLabel questionNumbers = new JLabel();

	/** Yes button. */
	private JButton yes = new JButton("Yes");

	/** No button. */
	private JButton no = new JButton("No");

	/** Label for holding the result text */
	private JLabel resultText = new JLabel("The number that you was thinking of was:");

	/** Label for holding the result number */
	private JLabel result = new JLabel();

	/**
	 * Starts up the application.
	 */
	public static void main(String[] args)
	{
		// Start application by building GUI
		new MindReader().buildGUI();
	}

	/**
	 * Builds the GUI and all the components.
	 */
	public void buildGUI()
	{
		// The application's main frame
		frame = new JFrame("Mind Reader");
		frame.getContentPane().setLayout(new FlowLayout());

		// Add start button listener
		start.addActionListener(new StartButtonListener());

		// Add components to frame
		frame.getContentPane().add(BorderLayout.CENTER, introduction);
		frame.getContentPane().add(BorderLayout.SOUTH, start);

		// Set default close operation
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set size and make it visible
		frame.setSize(650,100);
		frame.setVisible(true);
	}

	/**
	 * Starts the Mind Reader by performing all the initializations, this method is called when the "Start" button is pressed.
	 */
	public void start()
	{
		// Remove main page components
		start.setVisible(false);
		introduction.setVisible(false);

		// Initiate possibleNumbers by adding all the numbers to possibleNumbers
		for(int i = 0, n = 1; i < MAX_NUMBER; i++, n++)
		{
			possibleNumbers.add(i, n);
		}

		// Shuffle possibleNumbers
		Collections.shuffle(possibleNumbers);

		// Show first question
		question();
	}

	/**
	 * Shows the next question. 
	 * This is called at the end of the start() method or after a question has been answered.
	 */
	public void question()
	{
		// Increment question number
		currentQuestion++;

		// If this is the first question, set up GUI components for question
		if(currentQuestion == 1)
		{
			// Add components to frame, otherwise just edit values
			frame.getContentPane().add(BorderLayout.CENTER, questionNo);
			frame.getContentPane().add(BorderLayout.CENTER, question);
			frame.getContentPane().add(BorderLayout.CENTER, questionNumbers);
			frame.getContentPane().add(BorderLayout.SOUTH, yes);
			frame.getContentPane().add(BorderLayout.SOUTH, no);

			// Add listeners to buttons
			yes.addActionListener(new YesButtonListener());
			no.addActionListener(new NoButtonListener());
		}

		// Question No (for output)
		String questionNoString = "Question "+currentQuestion+":";
		questionNo.setText(questionNoString);

		// Check if possibleNumbers more than NUMBERS_PER_QUESTION
		if(possibleNumbers.size() > NUMBERS_PER_QUESTION)
		{
			// Loop possibleNumbers based on numbersPerQuestion
			for(int i = 0; i < NUMBERS_PER_QUESTION; i++)
			{
				// Get number from current index
				int n = possibleNumbers.get(i);

				// Copy current element from possibleNumbers to currentQuestionNumbers
				currentQuestionNumbers.add(n);
			}

			// Create string to show question numbers in output
			String questionNumbersString = "";

			// Loop all the currentQuestionNumbersWithPlaceholder and add them to the output string
			int iterationCounter = 1;
			for (int i : currentQuestionNumbers)
			{
				// Check if this is the last number
				if(iterationCounter != NUMBERS_PER_QUESTION)
				{
					// If not last number, add a comma after the number
					questionNumbersString += i + ", ";
				}
				else
				{
					// If last number, don't add a comma after the number
					questionNumbersString += i;
				}

				iterationCounter++;
			}

			// Add question numbers to a label
			questionNumbers.setText(questionNumbersString);
		}
		else
		{
			// Take half of the numbers from possibleNumbers
			int numbersToTake = (int) (possibleNumbers.size() * 0.5);
			for(int i = 0; i < numbersToTake; i++)
			{
				// Get number from current index
				int n = possibleNumbers.get(i);

				// Copy current element from possibleNumbers to currentQuestionNumbers
				currentQuestionNumbers.add(n);

				// Also copy to placeholder array
				currentQuestionNumbersWithPlaceholder.add(n);
			}

			// Shuffle denied numbers
			Collections.shuffle(deniedNumbers);

			// Take placeholder numbers from deniedNumbers to complete NUMBERS_PER_QUESTION
			int placeholderNumbersToTake = NUMBERS_PER_QUESTION - numbersToTake;
			for(int i = 0; i < placeholderNumbersToTake; i++)
			{
				// Get number from current index
				int n = deniedNumbers.get(i);

				// Copy current element from deniedNumbers to currentQuestionNumbersWithPlaceholder
				currentQuestionNumbersWithPlaceholder.add(n);
			}

			// Shuffle currentQuestionNumbersWithPlaceholder
			Collections.shuffle(currentQuestionNumbersWithPlaceholder);

			// Create string to show question numbers in output
			String questionNumbersString = "";

			// Loop all the currentQuestionNumbersWithPlaceholder and add them to the output string
			int iterationCounter = 1;
			for (int i : currentQuestionNumbersWithPlaceholder)
			{
				// Check if this is the last number
				if(iterationCounter != NUMBERS_PER_QUESTION)
				{
					// If not last number, add a comma after the number
					questionNumbersString += i + ", ";
				}
				else
				{
					// If last number, don't add a comma after the number
					questionNumbersString += i;
				}

				iterationCounter++;
			}

			// Add question numbers to a label
			questionNumbers.setText(questionNumbersString);
		}
	}

	/**
	 * Takes an answer and processes it accordingly.
	 *
	 * @param answer True if user answered the question as "Yes", false if the user answered the question as "No". 
	 */
	public void answer(boolean answer)
	{
		// If user clicked "Yes"
		if(answer == true)
		{
			// Remove all elements from possibleNumbers and deniedNumbers
			possibleNumbers.clear();
			deniedNumbers.clear();

			// Copy all numbers from currentQuestionNumbers to possibleNumbers
			for(int i = 0; i < currentQuestionNumbers.size(); i++)
			{
				// Get number from current index
				int n = currentQuestionNumbers.get(i);

				// Add number to possibleNumbers
				possibleNumbers.add(n);
			}

			// Shuffle possibleNumbers
			Collections.shuffle(possibleNumbers);

			// Add all numbers to deniedNumbers except those in possibleNumbers
			for(int i = 0, number = 1; i < MAX_NUMBER; i++, number++)
			{
				// If number is in possibleNumbers
				if(possibleNumbers.contains(number))
				{
					// Stop current iteration and don't add to deniedNumbers
					continue;
				}

				// Add to deniedNumbers
				deniedNumbers.add(number);
			}

			// Shuffle deniedNumbers
			Collections.shuffle(deniedNumbers);
		}
		else
		{
			// If user answered "No"

			// Remove the currentQuestionNumbers from possibleNumbers
			for(int i = 0; i < currentQuestionNumbers.size(); i++)
			{
				// Remove first element because when we remove one element.
				// The next element will be the first element in the next iteration.
				possibleNumbers.remove(0);
			}

			// Move from currentQuestionNumbers to deniedNumbers
			for(int i = 0; i < currentQuestionNumbers.size(); i++)
			{
				// Get number from current index
				int n = currentQuestionNumbers.get(i);

				// Copy current element from currentQuestionNumbers to deniedNumbers
				deniedNumbers.add(n);
			}
		}

		// Check if one element remains from possibleNumbers
		if(possibleNumbers.size() == 1)
		{
			// Show result
			result();
		}
		else
		{
			// Clean up arrays for next question
			currentQuestionNumbers.clear();
			currentQuestionNumbersWithPlaceholder.clear();

			// Show next question
			question();
		}

	}

	/**
	 * Shows the result to the user.
	 * The answer() method will call this method when possible numbers has only 1 remaining number.
	 */ 
	public void result()
	{
		// Remove question components
		questionNo.setVisible(false);
		question.setVisible(false);
		questionNumbers.setVisible(false);
		yes.setVisible(false);
		no.setVisible(false);

		// The first element from possibleNumbers is the result
		// Normally, there would be only 1 number in possibleNumber when we reach here
		int resultNumber = possibleNumbers.get(0);
		result.setText(String.valueOf(resultNumber));

		// Add components for result
		frame.getContentPane().add(BorderLayout.CENTER, resultText);
		frame.getContentPane().add(BorderLayout.CENTER, result);
	}

	/**
	 * A button listener for starting the game by invoking the start() method when the user clicks on the "Start" button.
	 */
	public class StartButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
			// Execute start() method when start button is clicked
			start();
		}
	}

	/**
	 * A button listener for telling the answer() method that the user clicked on the "Yes" button.
	 */
	public class YesButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
			// Execute answer() method with true (yes) as parameter when "Yes" button is clicked
			answer(true);
		}
	}

	/**
	 * A button listener for telling the answer() method that the user clicked on the "No" button.
	 */
	public class NoButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
			// Execute answer() method with false (no) as parameter when "No" button is clicked
			answer(false);
		}
	}
}
