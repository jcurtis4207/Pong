/****  PONG  ***
 * 
 * 
 * 		******	    ****    *      *    *****
 * 		*     *   *      *  **     *   *     *
 * 		*      *  *      *  * *    *  *
 * 		*     *   *      *  *  *   *  *
 * 		******    *      *  *  *   *  *  
 * 		*         *      *  *   *  *  *  ****
 * 		*         *      *  *   *  *  *      *
 * 		*         *      *  *    * *  *      *
 * 		*         *      *  *     **   *    *
 * 		*           ****    *      *    ****
 * 
 * 
 * as seen on the Atari, circa 1975
 * Some basic info found at http://www-classes.usc.edu/engr/ee-s/477p/s00/pong.html
 * with added tweaks to match original gameplay
 * 
 * Left Paddle controlled with W and S
 * Right Paddle controlled with Up and Down arrows
 * Serve with Space
 * Reset with Escape (the ball gets unpredictable when it hits a corner)
 * 
 * Updated 5/17/2020 to add setup screen
 * 
 */

import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PongV2 {
	private static JFrame frame = new JFrame("PONG");
	private static int leftScoreValue = 0;
	private static int rightScoreValue = 0;
	private static int serveDirection;

	// define global game-play parameters
	private static int SCREEN_WIDTH, SCREEN_HEIGHT;
	private static int BALL_SIZE;
	private static int PADDLE_HEIGHT, PADDLE_WIDTH;
	private static int PADDLE_SPEED; // size of paddle movement per frame
	private static int PADDLE_SPACE; // distance from paddle to edge of screen
	private static int BALL_SPEED; // combined x and y speeds
	private static int REFRESH_RATE; // in milliseconds

	// updates scores and creates new panel
	private static void leftScored() {
		leftScoreValue++;
		serveDirection = 1;
		if (leftScoreValue < 11) {
			printScores();
			frame.getContentPane().removeAll();
			PongPanel panel = new PongPanel();
			frame.getContentPane().add(panel);
			panel.initFocus();
			frame.pack();
			frame.setVisible(true);
		} else
			gameOver();
	}

	private static void rightScored() {
		rightScoreValue++;
		serveDirection = -1;
		if (rightScoreValue < 11) {
			printScores();
			frame.getContentPane().removeAll();
			PongPanel panel = new PongPanel();
			frame.getContentPane().add(panel);
			panel.initFocus();
			frame.pack();
			frame.setVisible(true);
		} else
			gameOver();
	}

	private static void reset() {
		frame.getContentPane().removeAll();
		PongPanel panel = new PongPanel();
		frame.getContentPane().add(panel);
		panel.initFocus();
		frame.pack();
		frame.setVisible(true);
	}

	// prints scores to console
	private static void printScores() {
		if (leftScoreValue == 0 && rightScoreValue == 0) {
			System.out.println("SCORE");
			System.out.println("-----");
		}
		System.out.println(leftScoreValue + "   " + rightScoreValue);
	}

	// displays victory screen
	private static void gameOver() {
		frame.getContentPane().add(new WinPanel());
		frame.pack();
		frame.setVisible(true);
		printScores();
	}

	// displays opening panel with 0-0 score
	public static void main(String[] args) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SetupPanel());
		frame.pack();
		frame.setVisible(true);
	}
	
	/*
	 * 
	 * Setup Screen
	 * 
	 */
	public static class SetupPanel extends JPanel{
		private JLabel titleLabel;
		private JLabel[] controlLabels, readouts, instructions;
		private JSlider[] sliders;
		private JButton resetButton, playButton;
		private JPanel titlePanel, namePanel, sliderPanel, readoutPanel, instructionPanel, buttonPanel;
		
		public SetupPanel() {
			// Setup Title
			titleLabel = new JLabel("PONG");
			titleLabel.setForeground(Color.white);
			titleLabel.setFont(new Font("Helvetica", Font.BOLD, 24));
			titlePanel = new JPanel();
			titlePanel.setBackground(Color.black);
			titlePanel.add(titleLabel);
			
			// Setup panels and arrays
			SliderListener sliderListener = new SliderListener();
			namePanel = new JPanel();
			namePanel.setBackground(Color.black);
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.PAGE_AXIS));
			sliderPanel = new JPanel();
			sliderPanel.setBackground(Color.black);
			sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
			readoutPanel = new JPanel();
			readoutPanel.setBackground(Color.black);
			readoutPanel.setLayout(new BoxLayout(readoutPanel, BoxLayout.PAGE_AXIS));
			controlLabels = new JLabel[8];
			sliders = new JSlider[8];
			readouts = new JLabel[8];
			
			// Setup control names
			controlLabels[0] = new JLabel("Screen Size");
			controlLabels[1] = new JLabel("Ball Size");
			controlLabels[2] = new JLabel("Paddle Height");
			controlLabels[3] = new JLabel("Paddle Width");
			controlLabels[4] = new JLabel("Paddle Spacing");
			controlLabels[5] = new JLabel("Paddle Speed");
			controlLabels[6] = new JLabel("Ball Speed");
			controlLabels[7] = new JLabel("Refresh Rate");
			
			// Setup sliders and readouts
			sliders[0] = new JSlider(300,1000, 500);
			sliders[0].setMajorTickSpacing(100);
			sliders[0].setSnapToTicks(true);
			sliders[0].addChangeListener(sliderListener);
			readouts[0] = new JLabel(""+sliders[0].getValue()*2+"x"+sliders[0].getValue());
			
			sliders[1] = new JSlider(2,20,10);
			sliders[1].addChangeListener(sliderListener);
			readouts[1] = new JLabel(""+sliders[1].getValue());
			
			sliders[2] = new JSlider(10,50,32);
			sliders[3] = new JSlider(2,10,5);
			sliders[2].addChangeListener(sliderListener);
			sliders[3].addChangeListener(sliderListener);
			readouts[2] = new JLabel(""+sliders[2].getValue());
			readouts[3] = new JLabel(""+sliders[3].getValue());
			
			sliders[4] = new JSlider(20,200,70);
			sliders[4].addChangeListener(sliderListener);
			readouts[4] = new JLabel(""+sliders[4].getValue());
			
			sliders[5] = new JSlider(5,15,8);
			sliders[5].addChangeListener(sliderListener);
			readouts[5] = new JLabel(""+sliders[5].getValue());
			
			sliders[6] = new JSlider(5,20,12);
			sliders[6].addChangeListener(sliderListener);
			readouts[6] = new JLabel(""+sliders[6].getValue());
			
			sliders[7] = new JSlider(10,100,30);
			sliders[7].addChangeListener(sliderListener);
			readouts[7] = new JLabel(""+sliders[7].getValue()+" ms");

			for(int i = 0; i < sliders.length; i++) {
				controlLabels[i].setForeground(Color.white);
				readouts[i].setForeground(Color.white);
				sliders[i].setBackground(Color.black);
				namePanel.add(controlLabels[i]);
				sliderPanel.add(sliders[i]); 
				readoutPanel.add(readouts[i]);
				namePanel.add(Box.createRigidArea(new Dimension(5,10)));
				sliderPanel.add(Box.createRigidArea(new Dimension(5,10)));
				readoutPanel.add(Box.createRigidArea(new Dimension(5,10)));
			}
			
			// Setup instructions
			instructions = new JLabel[5];
			instructions[0] = new JLabel("Controls:");
			instructions[0].setFont(new Font("Arial", Font.PLAIN, 16));
			instructions[1] = new JLabel("'W' and 'S' to move left paddle");
			instructions[2] = new JLabel("Up and Down arrows to move right paddle");
			instructions[3] = new JLabel("'Space' to serve");
			instructions[4] = new JLabel("'Esc' to reset the round");
			instructionPanel = new JPanel();
			instructionPanel.setBackground(Color.black);
			instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.PAGE_AXIS));
			instructionPanel.add(Box.createRigidArea(new Dimension(50,20)));
			for(int i = 0; i < instructions.length; i++) {
				instructions[i].setForeground(Color.white);
				instructionPanel.add(instructions[i]);
				instructionPanel.add(Box.createRigidArea(new Dimension(5,10)));
			}
		
			// Setup buttons
			resetButton = new JButton("Reset to Default");
			playButton = new JButton("PLAY!");
			playButton.addActionListener(new ButtonListener());
			buttonPanel = new JPanel();
			buttonPanel.setBackground(Color.black);
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.add(resetButton);
			buttonPanel.add(Box.createRigidArea(new Dimension(20,10)));
			buttonPanel.add(playButton);
			
			// Add panels and define dimensions
			titlePanel.setPreferredSize(new Dimension(385,40));
			namePanel.setPreferredSize(new Dimension(100,200));
			sliderPanel.setPreferredSize(new Dimension(200,200));
			readoutPanel.setPreferredSize(new Dimension(75,200));
			instructionPanel.setPreferredSize(new Dimension(385,150));
			add(titlePanel); 
			add(namePanel); 
			add(sliderPanel); 
			add(readoutPanel); 
			add(instructionPanel); 
			add(buttonPanel);
			setBackground(Color.black);
			setPreferredSize(new Dimension(400, 460));
		}
		
		// Set readout to current value of each slider
		private class SliderListener implements ChangeListener{
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == sliders[0]) {
					readouts[0].setText(""+sliders[0].getValue()*2+"x"+sliders[0].getValue());
				}
				if(e.getSource() == sliders[1]) {
					readouts[1].setText(""+sliders[1].getValue());
				}
				if(e.getSource() == sliders[2]) {
					readouts[2].setText(""+sliders[2].getValue());
				}
				if(e.getSource() == sliders[3]) {
					readouts[3].setText(""+sliders[3].getValue());
				}
				if(e.getSource() == sliders[4]) {
					readouts[4].setText(""+sliders[4].getValue());
				}
				if(e.getSource() == sliders[5]) {
					readouts[5].setText(""+sliders[5].getValue());
				}
				if(e.getSource() == sliders[6]) {
					readouts[5].setText(""+sliders[6].getValue());
				}
				if(e.getSource() == sliders[7]) {
					readouts[7].setText(""+sliders[7].getValue()+" ms");
				}
			}
		}
		
		// Either reset setup screen or start game
		private class ButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				if(event.getSource() == resetButton) {
					// erase setup panel and start a new one
					frame.getContentPane().removeAll();
					frame.getContentPane().add(new SetupPanel());
					frame.pack();
					frame.setVisible(true);
				}
				if(event.getSource() == playButton) {
					// set global parameters
					SCREEN_HEIGHT = sliders[0].getValue();
					SCREEN_WIDTH = sliders[0].getValue()*2;
					BALL_SIZE = sliders[1].getValue();
					PADDLE_HEIGHT = sliders[2].getValue();
					PADDLE_WIDTH = sliders[3].getValue();
					PADDLE_SPACE = sliders[4].getValue();
					PADDLE_SPEED = sliders[5].getValue();
					BALL_SPEED = sliders[6].getValue();
					REFRESH_RATE = sliders[7].getValue();
				
					// randomly determine who serves first
					Random rand = new Random();
					serveDirection = (rand.nextBoolean() ? 1 : -1);
					// start game
					frame.getContentPane().removeAll();
					PongPanel panel = new PongPanel();
					frame.getContentPane().add(panel);
					panel.initFocus();
					frame.pack();
					frame.setVisible(true);
					printScores();
				}
			}
		}
	}

	/*
	 * 
	 * Main Game Screen
	 * 
	 */

	public static class PongPanel extends JPanel {
		private int leftPaddlePosition, rightPaddlePosition;
		private int ballX, ballY;
		private int ballMoveX, ballMoveY;
		private boolean testW, testS, testUP, testDOWN = false;
		private Timer timer;

		public PongPanel() {
			// set initial ball velocity
			Random rand = new Random();
			ballMoveX = rand.nextInt(BALL_SPEED/2)+BALL_SPEED/2; ballMoveY = BALL_SPEED -
			ballMoveX; ballMoveX *= serveDirection;
			 
			// set starting paddle and ball position
			leftPaddlePosition = (SCREEN_HEIGHT / 2) - (PADDLE_HEIGHT / 2);
			rightPaddlePosition = leftPaddlePosition;
			ballX = (SCREEN_WIDTH / 2) - (BALL_SIZE / 2);
			ballY = rand.nextInt(SCREEN_HEIGHT - BALL_SIZE);
			addKeyListener(new PaddleListener());

			// defines background
			setBackground(Color.black);
			setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
			setFocusable(true);
			timer = new Timer(REFRESH_RATE, new TimerListener());
		}

		// Draws the paddles, ball, and net
		public void paintComponent(Graphics page) {
			super.paintComponent(page);
			page.setColor(Color.WHITE);
			page.setFont(new Font("Helvetica", Font.PLAIN, 48));
			// draws the net
			for (int i = 2; i <= SCREEN_HEIGHT; i++) {
				page.fillRect(SCREEN_WIDTH / 2 - 1, i, 2, 10);
				i += 15;
			}
			// draws paddles
			page.fillRect(PADDLE_SPACE, leftPaddlePosition, PADDLE_WIDTH, PADDLE_HEIGHT);
			page.fillRect(SCREEN_WIDTH - PADDLE_SPACE - PADDLE_WIDTH, rightPaddlePosition, PADDLE_WIDTH, PADDLE_HEIGHT);
			// draws score-board
			page.drawString(String.valueOf(leftScoreValue), (SCREEN_WIDTH / 2) - 80 - 14, 50);
			page.drawString(String.valueOf(rightScoreValue), (SCREEN_WIDTH / 2) + 80 - 14, 50);
			// draws serve text
			page.setFont(new Font("Helvetica", Font.PLAIN, 30));
			if (!timer.isRunning())
				page.drawString("Press Space to Serve", (SCREEN_WIDTH / 2) - 140, SCREEN_HEIGHT - 50);
			// draws the ball once it is served
			if (timer.isRunning())
				page.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
		}

		private class PaddleListener implements KeyListener {
			// Responds to the user pressing keys to adjust paddle positions
			public void keyPressed(KeyEvent event) {
				switch (event.getKeyCode()) {
				// move left paddle with W and S
				case KeyEvent.VK_W:
					testW = true;
					break;
				case KeyEvent.VK_S:
					testS = true;
					break;
				// move right paddle with Up and Down arrows
				case KeyEvent.VK_UP:
					testUP = true;
					break;
				case KeyEvent.VK_DOWN:
					testDOWN = true;
					break;
				// serve with space bar
				case KeyEvent.VK_SPACE:
					timer.start();
					break;
				// reset with escape
				case KeyEvent.VK_ESCAPE:
					timer.stop();
					PongV2.reset();
					break;
				}
			}

			public void keyReleased(KeyEvent event) {
				switch (event.getKeyCode()) {
				case KeyEvent.VK_W:
					testW = false;
					break;
				case KeyEvent.VK_S:
					testS = false;
					break;
				case KeyEvent.VK_UP:
					testUP = false;
					break;
				case KeyEvent.VK_DOWN:
					testDOWN = false;
					break;
				}
			}

			// Provide empty definitions for unused event methods.
			public void keyTyped(KeyEvent event) {
			}
		}

		private class TimerListener implements ActionListener {
			// each frame, based on REFRESH_RATE, redraws ball and paddle positions
			public void actionPerformed(ActionEvent event) {
				ballX += ballMoveX;
				ballY += ballMoveY;
				Random rand = new Random();

				// redraw paddles if they move, and keep from going off screen
				if (testW == true && leftPaddlePosition > 0)
					leftPaddlePosition -= PADDLE_SPEED;
				else if (testS == true && leftPaddlePosition + PADDLE_HEIGHT < SCREEN_HEIGHT)
					leftPaddlePosition += PADDLE_SPEED;
				if (testUP == true && rightPaddlePosition > 0)
					rightPaddlePosition -= PADDLE_SPEED;
				else if (testDOWN == true && rightPaddlePosition + PADDLE_HEIGHT < SCREEN_HEIGHT)
					rightPaddlePosition += PADDLE_SPEED;
				
				// ball hits right wall - left scores a point
				if (ballX >= SCREEN_WIDTH - BALL_SIZE) {
					timer.stop();
					PongV2.leftScored();
				}
				// ball hits left wall - right scores a point
				else if (ballX <= 0) {
					timer.stop();
					PongV2.rightScored();
				}
				// ball hits top or bottom
				else if (ballY <= 0 || ballY >= SCREEN_HEIGHT - BALL_SIZE) {
					ballMoveY = ballMoveY * -1;		
				}
				// ball hits left paddle
				else if ((ballX <= PADDLE_SPACE + PADDLE_WIDTH && ballX >= PADDLE_SPACE + PADDLE_WIDTH + ballMoveX)
						&& (ballY + BALL_SIZE >= leftPaddlePosition && ballY <= leftPaddlePosition + PADDLE_HEIGHT)) {
					int ballRelativeY = (ballY + BALL_SIZE / 2) - leftPaddlePosition;
					// hits paddle top
					if (ballRelativeY < (PADDLE_HEIGHT / 5)) {
						ballMoveY = (BALL_SPEED / 2) * -1;
						ballMoveX = ballMoveY + BALL_SPEED;
					}
					// hits paddle top mid
					else if ((ballRelativeY >= (PADDLE_HEIGHT / 5)) && (ballRelativeY < (2 * PADDLE_HEIGHT / 5))) {
						ballMoveX = BALL_SPEED / 2;
						ballMoveX += ballMoveX / 2;
						ballMoveY = ballMoveX - BALL_SPEED;
					}
					// hits paddle middle
					else if ((ballRelativeY >= (2 * PADDLE_HEIGHT / 5))
							&& (ballRelativeY < PADDLE_HEIGHT - (2 * PADDLE_HEIGHT / 5))) {
						ballMoveX = BALL_SPEED;
						ballMoveY = (rand.nextBoolean() ? 1 : -1);
					}
					// hits paddle bottom mid
					else if ((ballRelativeY >= PADDLE_HEIGHT - (2 * PADDLE_HEIGHT / 5))
							&& (ballRelativeY < PADDLE_HEIGHT - (PADDLE_HEIGHT / 5))) {
						ballMoveX = BALL_SPEED / 2;
						ballMoveX += ballMoveX / 2;
						ballMoveY = BALL_SPEED - ballMoveX;
					}
					// hits paddle bottom
					else {
						ballMoveY = BALL_SPEED / 2;
						ballMoveX = BALL_SPEED - ballMoveY;
					}

				}
				// ball hits right paddle
				else if ((ballX + BALL_SIZE >= SCREEN_WIDTH - PADDLE_SPACE - PADDLE_WIDTH
						&& ballX + BALL_SIZE <= SCREEN_WIDTH - PADDLE_SPACE - PADDLE_WIDTH + ballMoveX)
						&& (ballY + BALL_SIZE >= rightPaddlePosition && ballY <= rightPaddlePosition + PADDLE_HEIGHT)) {
					int ballRelativeY = (ballY + BALL_SIZE / 2) - rightPaddlePosition;
					// hits paddle top
					if (ballRelativeY < (PADDLE_HEIGHT / 5)) {
						ballMoveY = BALL_SPEED / -2;
						ballMoveX = (ballMoveY + BALL_SPEED) * -1;
					}
					// hits paddle top mid
					else if ((ballRelativeY >= (PADDLE_HEIGHT / 5)) && (ballRelativeY < (2 * PADDLE_HEIGHT / 5))) {
						ballMoveX = BALL_SPEED / -2;
						ballMoveX += ballMoveX / 2;
						ballMoveY = (ballMoveX + BALL_SPEED) * -1;
					}
					// hits paddle middle
					else if ((ballRelativeY >= (2 * PADDLE_HEIGHT / 5))
							&& (ballRelativeY < PADDLE_HEIGHT - (2 * PADDLE_HEIGHT / 5))) {
						ballMoveX = BALL_SPEED * -1;
						ballMoveY = (rand.nextBoolean() ? 1 : -1);
					}
					// hits paddle bottom mid
					else if ((ballRelativeY >= PADDLE_HEIGHT - (2 * PADDLE_HEIGHT / 5))
							&& (ballRelativeY < PADDLE_HEIGHT - (PADDLE_HEIGHT / 5))) {
						ballMoveX = BALL_SPEED / -2;
						ballMoveX += ballMoveX / 2;
						ballMoveY = BALL_SPEED + ballMoveX;
					}
					// hits paddle bottom
					else {
						ballMoveY = BALL_SPEED / 2;
						ballMoveX = ballMoveY - BALL_SPEED;
					}
				}
				repaint();
			}
		}

		/*
		 * When a new panel gets created after a point is scored, This method is needed
		 * after the constructor to take focus for key events Solution found here (Peter Quiring):
		 * https://stackoverflow.com/questions/6723257/how-to-set-focus-on-jtextfield/6723316
		 */
		public void initFocus() {
			requestFocus();
		}
	}

	/*
	 * 
	 * Game Over Screen
	 * 
	 */

	public static class WinPanel extends JPanel {
		private int ballX, ballY;
		private int ballMoveX, ballMoveY;
		private Timer timer;

		public WinPanel() {
			// set initial ball velocity
			Random rand = new Random();
			ballMoveX = rand.nextInt(3) + 8;
			ballMoveY = rand.nextInt(6) + 3;

			// set starting ball position
			ballX = (SCREEN_WIDTH / 2) - (BALL_SIZE / 2);
			ballY = rand.nextInt(SCREEN_HEIGHT - BALL_SIZE);

			// defines background
			setBackground(Color.black);
			setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
			setFocusable(true);
			timer = new Timer(REFRESH_RATE, new TimerListener());
			timer.start();
		}

		// Draws the paddles, ball, and net
		public void paintComponent(Graphics page) {
			super.paintComponent(page);
			page.setColor(Color.WHITE);
			page.setFont(new Font("Helvetica", Font.PLAIN, 48));
			// draws the net
			for (int i = 2; i <= SCREEN_HEIGHT; i++) {
				page.fillRect(SCREEN_WIDTH / 2 - 1, i, 2, 10);
				i += 15;
			}
			// draws score-board
			page.drawString(String.valueOf(leftScoreValue), (SCREEN_WIDTH / 2) - 80 - 14, 50);
			page.drawString(String.valueOf(rightScoreValue), (SCREEN_WIDTH / 2) + 80 - 14, 50);
			// draws the ball
			page.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
		}

		private class TimerListener implements ActionListener {
			// each frame, based on REFRESH_RATE redraws ball position
			public void actionPerformed(ActionEvent event) {
				ballX += ballMoveX;
				ballY += ballMoveY;

				// ball hits right wall
				if (ballX >= SCREEN_WIDTH - BALL_SIZE)
					ballMoveX *= -1;
				// ball hits left wall
				else if (ballX <= 0)
					ballMoveX *= -1;
				// ball hits top or bottom
				else if (ballY <= 0 || ballY >= SCREEN_HEIGHT - BALL_SIZE)
					ballMoveY = ballMoveY * -1;

				repaint();
			}
		}
	}
}
