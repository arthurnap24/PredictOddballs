 package neurogame.game;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JPanel;

import neurogame.io.SocketToParallelPort;

public abstract class Game extends JPanel
{
  public static final byte STIM_STD = 1;
  public static final byte STIM_NOV = 2;
  public static final byte STIM_TAR = 3;
  
  public static final byte KEY_D = 4;
  public static final byte KEY_K = 5;
  public static final byte KEY_SPACE= 6;
  public static final byte KEY_1 = 7;
  public static final byte KEY_2 = 8;
  public static final byte KEY_3 = 9;
  
  public static final byte NOTIF_INC = 10;
  public static final byte NOTIF_SLOW = 11;

  public enum InputType
  {
    REQUIRED,
    IGNORE,
    NULL
  }
  
  public enum GameStatus
  {
    SHOW_INSTRUCTION,
    IN_PRACTICE,
    PRACTICE_OVER,
    IN_BLOCK,
    WAIT_FOR_NEXT_BLOCK,
    END_OF_BLOCKS
  }

  //never used
  public enum LogInfoStatus
  {
    FETCH_TRIAL,
    FETCH_RT_ACC,
    WRITE_READY
  }

	private static final long serialVersionUID = 1L;
	
	//FOR EEG: Host is just the localhost of course...
	SocketToParallelPort socket;
	
	protected int width, height;
	protected boolean running;
	protected Random randomTime;
	protected int status;
	
	//User Modifiable:
	public int stimulusDuration = 300;
  public int responseTimeOut = 800;
  public int intervalMin = 1000;
  public int intervalMax = 1500;
  public boolean passiveGame = false;
//  public boolean goNoGoOddballGame = false;
//  public boolean vigilanceOddballGame = false;
//  public boolean activeOddballGame = true;
  public boolean hasErrorFeedback = true;
  public int gameTrials = 4;
  public double percentTarget = 9;
  public double percentNovel = 10;
  boolean hasFeedbackDelay = true;
  public int delayDuration = 200;
  public int pracTrials = 2; //number of trials inside the practiceTrials
  public boolean hasPracticeErrorFeedback = true;
//  public InputType targetInputType = InputType.REQUIRED;
//  public InputType standardInputType = InputType.REQUIRED;
//  public InputType novelInputType = InputType.REQUIRED;
  public InputType targetInputType = InputType.NULL;
  public InputType standardInputType = InputType.NULL;
  public InputType novelInputType = InputType.NULL;
  public int numBlocks = 2;
  public GameStatus gameStatus = GameStatus.SHOW_INSTRUCTION;
  
  int targetKey = KeyEvent.VK_NUMPAD1;
  int standardKey = KeyEvent.VK_NUMPAD2;
  int novelKey = KeyEvent.VK_NUMPAD3;
  public BufferedImage instructionImage = null;
  public String logFile = "";
  
  //for game logic:
  public int notificationDuration = 500;//TODO: If no error feedback, notification duration must be 0
  boolean practiceIsOver = false;
  
	public abstract void start();
	/**
	 * makes the thread sleep for the sake of game loop.
	 */
	protected void sleep() {
		try {
			int intMillis = newIntervalMillis();
			Thread.sleep(intMillis);
		} catch(InterruptedException e) {}
	}
	/**
	 * @return the time interval (in milliseconds) for the next 
	 * 		   image to appear.
	 */
	protected int newIntervalMillis() {
		return 1000 + randomTime.nextInt(500);
	}
	/**
	 * updates the status the status of games.
	 */
	public abstract void updateStatus();
	
	public abstract void initialize();
	
	/**
	 * @param image
	 * @return the x coordinate of the top left box in which the
	 * 			image will be printed
	 */
	protected int centerX(BufferedImage image) {
		return (this.getWidth() - image.getWidth(null)) / 2;
	}
	/**
	 * @param image
	 * @return the y coordinate of the top left box in which the
	 * 			image will be printed
	 */
	protected int centerY(BufferedImage image) {
		return (this.getHeight() - image.getHeight(null)) / 2;
	}
	
  public void showInstructionFile()
  {
    gameStatus = GameStatus.SHOW_INSTRUCTION;
    repaint();
  }
  int intermission = 0;
  boolean inIntermission = true;
  public void countDown()
  {
    inIntermission = true;
    
    long timeSinceIntermission = System.currentTimeMillis();
    long currTime = System.currentTimeMillis();
    
    intermission = 3;
    repaint();
    while (inIntermission)
    {
      currTime = System.currentTimeMillis();
      if (currTime - timeSinceIntermission == 1000)
      {
        intermission--;
        if (intermission == 0)
        {
          inIntermission = false;
          break;
        }
        repaint();
        timeSinceIntermission = System.currentTimeMillis();
      }
    }
  }
  
  public byte getKeyPressTrigger(int keyCode)
  {
    byte keyPressedTrigger = 4;

    if (keyCode == KeyEvent.VK_D) keyPressedTrigger = Game.KEY_D;
    else if (keyCode == KeyEvent.VK_K) keyPressedTrigger = Game.KEY_K;
    else if (keyCode == KeyEvent.VK_SPACE) keyPressedTrigger = Game.KEY_SPACE;
    else if (keyCode == KeyEvent.VK_NUMPAD1) keyPressedTrigger = Game.KEY_1;
    else if (keyCode == KeyEvent.VK_NUMPAD2) keyPressedTrigger = Game.KEY_2;
    else if (keyCode == KeyEvent.VK_NUMPAD3) keyPressedTrigger = Game.KEY_3;
    return keyPressedTrigger;
  }
  
}
