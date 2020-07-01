/****  PONG  ***
 * 
 * 
 * 		******	    ****    *      *    *****
 * 		*     *   *      *  **     *   *     *
 * 		*      *  *      *  * *    *  *
 * 		*     *   *      *  *  *   *  *
 * 		******    *      *  *  *   *  *  
 * 		*	  *      *  *   *  *  *  ****
 * 		*	  *      *  *   *  *  *      *
 * 		*	  *      *  *    * *  *      *
 * 		*	  *      *  *     **   *    *
 * 		*	    ****    *      *    ****
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
 */

import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class PongV1 {
	private static JFrame frame = new JFrame("PONG");
	private static int leftScoreValue = 0;
	private static int rightScoreValue = 0;
	private static int serveDirection;

	// define global game-play parameters
	private static final int SCREEN_WIDTH = 1024, SCREEN_HEIGHT = 512;
	private static final int BALL_SIZE = 10;
	private static final int PADDLE_HEIGHT = 32, PADDLE_WIDTH = 5;
	private static final int PADDLE_JUMP = 8; // size of paddle movement per frame
	private static final int PADDLE_SPACE = 70; // distance from paddle to edge of screen
	private static final int BALL_SPEED = 12; // combined x and y speeds
	private static final int REFRESH_RATE = 30; // in milliseconds
	private static final boolean SOUND_ON = true;

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
		// randomly determine who serves first
		Random rand = new Random();
		serveDirection = (rand.nextBoolean() ? 1 : -1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new PongPanel());
		frame.pack();
		frame.setVisible(true);
		printScores();
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
					PongV1.reset();
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
					leftPaddlePosition -= PADDLE_JUMP;
				else if (testS == true && leftPaddlePosition + PADDLE_HEIGHT < SCREEN_HEIGHT)
					leftPaddlePosition += PADDLE_JUMP;
				if (testUP == true && rightPaddlePosition > 0)
					rightPaddlePosition -= PADDLE_JUMP;
				else if (testDOWN == true && rightPaddlePosition + PADDLE_HEIGHT < SCREEN_HEIGHT)
					rightPaddlePosition += PADDLE_JUMP;
				
				// ball hits right wall - left scores a point
				if (ballX >= SCREEN_WIDTH - BALL_SIZE) {
					if(SOUND_ON)
						Sounds.playPointSound();
					timer.stop();
					PongV1.leftScored();
				}
				// ball hits left wall - right scores a point
				else if (ballX <= 0) {
					if(SOUND_ON)
						Sounds.playPointSound();
					timer.stop();
					PongV1.rightScored();
				}
				// ball hits top or bottom
				else if (ballY <= 0 || ballY >= SCREEN_HEIGHT - BALL_SIZE) {
					ballMoveY = ballMoveY * -1;
					if(SOUND_ON)
						Sounds.playWallSound();					
				}
				// ball hits left paddle
				else if ((ballX <= PADDLE_SPACE + PADDLE_WIDTH && ballX >= PADDLE_SPACE + PADDLE_WIDTH + ballMoveX)
						&& (ballY + BALL_SIZE >= leftPaddlePosition && ballY <= leftPaddlePosition + PADDLE_HEIGHT)) {
					if(SOUND_ON)
						Sounds.playPaddleSound();
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
					if(SOUND_ON)
						Sounds.playPaddleSound();
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
		 * after the constructor to take focus for key events Solution found here (Peter
		 * Quiring):
		 * https://stackoverflow.com/questions/6723257/how-to-set-focus-on-jtextfield/
		 * 6723316
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

	/*
	 * 
	 * Sound Components
	 * much of which came from:
	 * https://stackoverflow.com/questions/34611134/java-beep-sound-produce-sound-of-some-specific-frequencies
	 * 
	 */
	
	public static class Sounds {
		public static float SAMPLE_RATE = 16000f;
	    public static void tone(int hz, int msecs, double vol) throws LineUnavailableException {
	    	byte[] buf = new byte[1];
	        AudioFormat af = new AudioFormat(SAMPLE_RATE,8,1,true,false);     
	        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
	        sdl.open(af);
	        sdl.start();
	        for (int i=0; i < msecs*8; i++) {
	              double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
	              buf[0] = (byte)(Math.sin(angle) * 127.0 * vol);
	              sdl.write(buf,0,1);
	        }
	        sdl.drain();
	        sdl.stop();
	        sdl.close();
	    }
	    
	    public static void playWallSound() {
	    	new Thread(new Runnable() {
	    		public void run() {
	    			try {
	    				Sounds.tone(400,60,0.1);
	    			} catch (Exception e) {
	    			}
	    		}
	    	}).start();
	    }
	    public static void playPaddleSound() {
	    	new Thread(new Runnable() {
	    		public void run() {
	    			try {
	    				Sounds.tone(800,60,0.1);
	    			} catch (Exception e) {
	    			}
	    		}
	    	}).start();
	    }
	    public static void playPointSound() {
	    	new Thread(new Runnable() {
	    		public void run() {
	    			try {
	    				Sounds.tone(100,600,0.5);
	    			} catch (Exception e) {
	    			}
	    		}
	    	}).start();
	    }
	}
}


