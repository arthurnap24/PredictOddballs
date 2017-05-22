package neurogame.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

import neurogame.io.Logger;
import neurogame.io.SocketToParallelPort;

public class TVisGame extends Game implements KeyListener {

  private static final long serialVersionUID = 1L;

  private int width, height;
  private boolean running;
  private BufferedImage target;
  private BufferedImage standard; //also called ignore
  private BufferedImage novel;
  private BufferedImage plus;    //the image with a plus sign in the center
  private BufferedImage tooSlow; //the image with the text "Too Slow"
  private BufferedImage incorrect; //the image with the text "Incorrect"
  private BufferedImage practiceOver;
  private BufferedImage blockOver;
  private BufferedImage allBlocksDone;

  private BufferedImage countDown1;
  private BufferedImage countDown2;
  private BufferedImage countDown3;

  private Scanner scanner;
  //  int[] trials;
  int trialNumber = 0; //start at the very first of the trial array
  String type = "passive";

  //  GameStatus gameStatus = GameStatus.IN_PRACTICE;

  //Shameful hard-coding, but it's a work-around for now...

  private ArrayList<BufferedImage> images;

  //Fields for Game Logic
  private long startTime;
  private long currentTime;
  private boolean paused;
  int ITI;
  int status = 0; //for the timeline implementation of the game
  boolean readyToRepaint = false;
  private boolean startOfTrial = true;
  private boolean stimulusHidden = false;
  private boolean notificationShown = false;  
  private boolean keyPressed = false;
  private boolean correctInput = false;
  private boolean incorrectInput = false;
  private int trialType; //so keyPressed can see 
  private boolean startTimeAdjusted = true;
  private boolean waitForFeedBackDelay = false;
  //user modifiable input in Game.java

  //default trials
  int[] trials = TrialSequenceGenerator.generateSequence(.09, .10, gameTrials, true); 
  int[] practiceTrials = TrialSequenceGenerator.generateSequence(.09, .10, pracTrials, true);

  //For logging:
  PrintWriter log; //initialized in intialize() needs to get closed in the end of the game.

  //for debugging purposes
  int[] allTargets = {1,3,1,3,1,3,1,3,1,3};
  int[] allStandards = {0,3,0,3,0,3,0,3,0,3};
  int[] allNovels = {2,3,2,3,2,3,2,3,2,3};

  public TVisGame() {
    //set some fields...
    status = 3;
    running = false;
    randomTime = new Random(0); //seed is always 0, don't forget to change
    try 
    {
      plus = ImageIO.read(getClass().getResource("/logos/plus.png"));
      incorrect = ImageIO.read(getClass().getResource("/images/incorrect.png"));
      tooSlow = ImageIO.read(getClass().getResource("/images/too_slow.png"));
      practiceOver = ImageIO.read(getClass().getResourceAsStream("/images/practiceOver.png"));
      blockOver = ImageIO.read(getClass().getResourceAsStream("/images/blockOver.png"));
      allBlocksDone = ImageIO.read(getClass().getResourceAsStream("/images/allBlocksDone.png"));
      instructionImage = ImageIO.read(getClass().getResourceAsStream("/images/defaultInstruction.png"));

      countDown1 = ImageIO.read(getClass().getResourceAsStream("/logos/cd1.png"));
      countDown2 = ImageIO.read(getClass().getResourceAsStream("/logos/cd2.png"));
      countDown3 = ImageIO.read(getClass().getResourceAsStream("/logos/cd3.png"));
    } 
    catch(Exception e) 
    {}
    //set the BufferedImage's paths...
    this.setFocusable(true);
    this.setSize(width, height);
    this.setBackground(Color.BLACK);
    this.addKeyListener(this);
    initialize();
    //    if (!DEBUG) stpp = new SocketToParallelPort("127.0.0.1", 55555);
  }

  /***********************************************************
   *                     PRIVATE METHODS
   ***********************************************************/
  public void readImageList()
  {
    ClassLoader classLoader = getClass().getClassLoader();
    images = new ArrayList<BufferedImage>();
    try 
    {
      BufferedInputStream listFile = new BufferedInputStream(classLoader.
          getResourceAsStream("images/imageList.txt"));
      scanner = new Scanner(listFile);
      while(scanner.hasNextLine()) 
      {
        String name = "/images/" + scanner.nextLine();
        if(!name.contains("A.bmp") && !name.contains("J.bmp"))
        {
          BufferedImage img = ImageIO.read(getClass().getResource(name));
          images.add(img);
        }
      }
      scanner.close();
    } catch(Exception e) {}	
  }
  /**
   * updates the status: 
   * 0 = target, 1 = ignore, 2 = random novel
   */
  public void updateStatus() {
    status = (int)(Math.random()*3);
  }
  /**
   * @return the index of the next image to appear after the 
   * 		   time interval.
   */
  private int newImageIndex() 
  {
    return (int)(Math.random()*images.size());
  }
  /**
   * sets the value of the target, standard, and novel images.
   */
  public void initialize() 
  {
    //create the log file:
    System.out.println("logFile= "+ logFile);
    try
    {
      log = new PrintWriter(logFile);
      log.println("ID:");
      log.println("Date:");
      log.println("Protocol Name:");
      log.println("Trial,Trigger,RT,Acc");
      //TODO: REMEMBER THAT YOU HAVE  TO REINSTANTIATE A NEW PRINTWRITER!!!
      log.close(); 
    }
    catch (Exception e) {}

    //initialize the target, standard(ignore), novel:
    double targetProb = percentTarget / 100;
    double novelProb = percentNovel / 100;
    System.out.println("targetProb="+targetProb+", novelProb="+novelProb);
    //    trials = TrialSequenceGenerator.generateSequence(targetProb, novelProb, gameTrials); 
    readImageList();
    try 
    {
      target = ImageIO.read(getClass().getResource("/images/target.png")); //X=target
      standard = ImageIO.read(getClass().getResource("/images/standard.png")); //O=standard

    } 
    catch(IOException e) 
    {}
    updateStatus();
  }
  /******************************************************
   *  				PUBLIC METHODS
   ******************************************************/
  /**
   * the game loop of the task.
   */
  @Override
  public void start() 
  {
    startTime = System.currentTimeMillis();
    running = true;
    int realDelayDuration = delayDuration;
    boolean realGameHasErrorFeedback = hasErrorFeedback;
    //these if statements correspond to practice blocks
    if (!hasPracticeErrorFeedback)
    {
      //straight to ITIs every incorrect response.
      hasErrorFeedback = false;
      notificationDuration = 0; //always 500 ms
      delayDuration = 0;
    }
    else if (hasPracticeErrorFeedback)
    {
      //regardless of whether hasErrorFeedback is set to false, practice will have error feedback
      hasErrorFeedback = true;
    }

    //null input types means passive oddball game
    if (targetInputType == InputType.NULL && 
        standardInputType == InputType.NULL && 
        novelInputType == InputType.NULL)
    {
      passiveGame = true;
      //change length to (2*pracTrials-1)
      trials = TrialSequenceGenerator.generateSequence(percentTarget/100, percentNovel/100,
          pracTrials, true); //2*Practrials-1 to get pracTrials stimuli
      countDown();
      gameLogicNoInputs();
    }
    else
    {
      //2*Practrials-1 to get pracTrials stimuli
      trials = TrialSequenceGenerator.generateSequence(percentTarget/100, percentNovel/100, 
          pracTrials, true); 
      countDown();
      gameLogicVisualOddballVariants();
    }
    practiceIsOver = true;
    gameStatus = GameStatus.PRACTICE_OVER;
    repaint();    
    if (realGameHasErrorFeedback)
    {
      hasErrorFeedback = true; //just set to true if there is
      delayDuration = realDelayDuration;
      notificationDuration = 500;
    }
    if (!realGameHasErrorFeedback)
    {
      hasErrorFeedback = false;
      delayDuration = 0;
      notificationDuration = 0;
    }
  }

  private void startGame()
  {
    trials = TrialSequenceGenerator.generateSequence(percentTarget/100, percentNovel/100, 
        gameTrials, true); //have plus signs already
    System.out.println("trials.length = " + trials.length);
    status = 0;
    running = true;
    Thread gameThread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        //show 3, 2, 1...
        countDown();
        if (passiveGame)
        {
          resetGameLogicVariables(); //NEEDS TO BE HERE!!!
          gameLogicNoInputs();
        }
        else if (!passiveGame)
        {
          resetGameLogicVariables();
          gameLogicVisualOddballVariants();
        }
      }
    }); 
    gameThread.start();
  }

  /**
   * stops the game loop of the task when the task is finished.
   */
  public void stop() {
    running = false;
  }

  //TODO: move on top of class
  private int currentBlock = 0; //practice gameStatus

  //*********************************************
  // If no user input is needed, use this logic.
  //*********************************************
  private void gameLogicNoInputs()
  {
    startTime = System.currentTimeMillis();
    repaint();
    trialNumber = 0;
    while(running) 
    {
      currentTime = System.currentTimeMillis();
      if(paused && currentTime - startTime >= ITI)
      {
        trialNumber++;
        if(trialNumber >= trials.length-1)
        {
          running = false;
          if (gameStatus == GameStatus.IN_BLOCK)
          {
            if (currentBlock < numBlocks)
              gameStatus = GameStatus.WAIT_FOR_NEXT_BLOCK;
            if (currentBlock == numBlocks)
              gameStatus = GameStatus.END_OF_BLOCKS;
            repaint();
          }
          break;
        }
        repaint();
        startTime = System.currentTimeMillis();
        paused = false;
      }
      else if(!paused && currentTime - startTime >= stimulusDuration)
      {
        showPlusSignPassive();
      }
      if(trialNumber >= trials.length)
      {
        running = false;
        return;
      }
    }
  }

  @Override 
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (intermission == 3) 
    {
      g.drawImage(countDown3, centerX(countDown3), centerY(countDown3), null);
      return;
    }
    else if (intermission == 2)
    {
      g.drawImage(countDown2, centerX(countDown2), centerY(countDown2), null);
      return;
    }
    else if (intermission == 1)
    {
      g.drawImage(countDown1, centerX(countDown1), centerY(countDown1), null);
      return;
    }

    if (gameStatus == GameStatus.WAIT_FOR_NEXT_BLOCK)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(blockOver, centerX(blockOver), centerY(blockOver), null);
      return;
    }
    else if (gameStatus == GameStatus.END_OF_BLOCKS)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(allBlocksDone, centerX(allBlocksDone), centerY(allBlocksDone), null);
      return;
    }
    else if (gameStatus == GameStatus.PRACTICE_OVER)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(practiceOver, centerX(practiceOver), centerY(practiceOver), null);
      return; //just draw the picture.
    }
    else if (gameStatus == GameStatus.SHOW_INSTRUCTION)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(instructionImage, centerX(instructionImage), centerY(instructionImage), null);
      return;
    }
    if (trialNumber < trials.length)
    {
      trialType = trials[trialNumber];
    }
    //if not all input types are 0.
    if(!passiveGame)
    {
      if(waitForFeedBackDelay)
      {
        g.drawImage(plus, centerX(plus), centerY(plus), null);
        waitForFeedBackDelay = false;
      }
      else
      {
        if(status == 0)
        {
          if(trialType == 0)
          {
            g.drawImage(standard, centerX(standard), centerY(standard), null);
            //            System.out.println("standard image drawn");
          }
          //target
          else if(trialType == 1)
          {
            g.drawImage(target, centerX(target), centerY(target), null);
          }
          //novel
          else if(trialType == 2)
          {
            novel = images.get(newImageIndex());
            g.drawImage(novel, centerX(novel), centerY(novel), null);
          }
          //plus sign
          else if(trialType == 3)
          {
            g.drawImage(plus, centerX(plus), centerY(plus), null);
          }
        }
        else if (status == 1)
        {
          g.drawImage(null, 0, 0, null); //draw nothing (just black)
        }
        else if (status == 2)
        {
          if(incorrectInput)
          {
            g.drawImage(incorrect, centerX(incorrect), centerY(incorrect), null);
            if (gameStatus != GameStatus.IN_PRACTICE)
            {
              OddballsEngine.logger.scheduleSocketOut(Game.NOTIF_INC);
            }
            notificationShown = true;
          }
          else
          {
            g.drawImage(tooSlow, centerX(tooSlow), centerY(tooSlow), null);
            
            if (gameStatus != GameStatus.IN_PRACTICE)
            {
              OddballsEngine.logger.scheduleSocketOut(Game.NOTIF_SLOW);
            }
            
            if (gameStatus == GameStatus.IN_BLOCK && !logFile.equals("/.txt") && 
                !logFile.equals(""))
            {
              try
              {
                log = new PrintWriter(new FileWriter(logFile, true));
              }
              catch(Exception i) {}
              log.println((trialNumber/2)+","+currentStim+",NaN,NaN");
              log.close();
            }
          }
          //show the notification tooSlow or Incorrect
        }
        else if (status == 3)
        {
          g.drawImage(plus, centerX(plus), centerY(plus), null);
        }
      }
    }
    //passive Oddball game
    else if (passiveGame)
    {
      //standard
      if (trialType == 0)
      {
        g.drawImage(standard, centerX(standard), centerY(standard), null);
      }
      //target
      else if (trialType == 1)
      {
        g.drawImage(target, centerX(target), centerY(target), null);
      }
      //novel
      else if (trialType == 2)
      {
        novel = images.get(newImageIndex());
        g.drawImage(novel, centerX(novel), centerY(novel), null);
      }
      //plus sign
      else if (trialType == 3)
      {
        g.drawImage(plus, centerX(plus), centerY(plus), null);
      }
    }
  }

  boolean stimTriggerSent = false;
  int currentStim = -2;
  //*******************************************************
  // If the active oddball (3AFC) is the desired task, and
  // other variants is desired
  //*******************************************************
  private void gameLogicVisualOddballVariants()
  {
    startTime = System.currentTimeMillis();
    //I should control the loop first thing
    //current status is 0
    trialNumber = 0;
    trialType = trials[trialNumber]; //part of the initialization
    resetGameLogicVariables(); //needed here!
    while (running) 
    {
      if (!stimTriggerSent && trialType != 3)
      {
        System.out.println("trialType="+trialType);
        //        OddballsEngine.logger.scheduleSocketOut((byte)trialType);
        if (trialType == 0)
        {
          System.out.println("standard");
          if (gameStatus != GameStatus.IN_PRACTICE)
          {
            OddballsEngine.logger.scheduleSocketOut(Game.STIM_STD);
            currentStim = Game.STIM_STD;
          }
        }
        else if (trialType == 1) 
        {
          System.out.println("target");
          if (gameStatus != GameStatus.IN_PRACTICE)
          {  
            OddballsEngine.logger.scheduleSocketOut(Game.STIM_TAR);
            currentStim = Game.STIM_TAR;
          }
        }
        else if (trialType == 2) 
        {
          System.out.println("novel");
          if (gameStatus != GameStatus.IN_PRACTICE)
          {
            OddballsEngine.logger.scheduleSocketOut(Game.STIM_NOV);
            currentStim = Game.STIM_NOV; 
          }
        }
        stimTriggerSent = true;
      }
      //TIMELINE IMPLEMENTATION:
      currentTime = System.currentTimeMillis();
      if (trialType == 1 && targetInputType == InputType.IGNORE ||
          trialType == 0 && standardInputType == InputType.IGNORE ||
          trialType == 2 && novelInputType == InputType.IGNORE)
      {
        //only works if you have error feedback and the stimulus type is ignore
        if (!keyPressed && correctInput && !startTimeAdjusted)
        {
          startTime = currentTime - (responseTimeOut + notificationDuration);
          startTimeAdjusted = true;
        }
      }
      //for active oddball
      if (keyPressed)
      {
        //put timeline to where the plus sign must be shown, but keyPressed() forces the repaint
        //if there's no need for error feedback, then we just move the timeline to the ITI.
        if (correctInput || !hasErrorFeedback)
        {
          System.out.println("correct input on the first press all the time?");
          startTime = currentTime - (responseTimeOut + notificationDuration);
        }
        else if (incorrectInput && !startTimeAdjusted)
        {
          startTime = currentTime - responseTimeOut + delayDuration;
          System.out.println("Is it equal?:" + ((currentTime - startTime) == (responseTimeOut + delayDuration)));
          startTimeAdjusted = true;
        }
      }
      long timeSinceStart = currentTime - startTime;

      //If the game is not supposed to update its status, then we skip this game loop
      controlTimeLine(timeSinceStart);
      if (!readyToRepaint)
      {
        continue;
      }

      if (status == 0)
      {
        //current status is 0
        //show the current trial
        repaint();
        readyToRepaint = false;
        //user can still press input keys
      }
      else if (status == 1)
      {
        //current status is 1
        //draw a null image (just black to hide the current trial)
        repaint();
        readyToRepaint = false;
        //user can still press input keys
      }
      else if (status == 2)
      {
        //current status is 2
        //user can't press input keys anymore
        repaint();
        readyToRepaint = false;
        //print the too slow
      }
      //500 ms is the duration of the too slow or incorrect notifcation 
      else if(status == 3)
      {
        //current status is 3
        //show the notification either too slow or incorrect
        repaint();
        readyToRepaint = false;
        //compute the ITI now
      }
    }
  }

  /*
   * controls the loop speed of the game loop since the while loop is faster
   * than the currentTimeMillis(). Meaning the state of the game is known
   * to the for loop for several loops before it recognizes that the
   * state is not valid anymore.
   *
   * is also responsible for changing the state of the game
   *
   * return true if the loop should continue, false otherwise
   */
  //for logic:

  private boolean firstTime = true;
  private int realNotificationDuration = notificationDuration;
  private void controlTimeLine(long timeSinceStart)
  {
    //let targets be null
    if(trialType == 1 && (targetInputType == InputType.NULL) ||        
        trialType == 0 && (standardInputType == InputType.NULL) ||
        trialType == 2 && (novelInputType == InputType.NULL) || !hasErrorFeedback) 
    {
      //feedback delay doesn't have to be 0 if null
      notificationDuration = 0;
    }

    if(timeSinceStart == 0 && startOfTrial)
    {
      System.out.println("In controlTimeLine(): trialNumber="+trialNumber+", trials length="+trials.length);
      if(trialNumber >= trials.length-1)
      {
        System.out.println("exit the game");
        if (gameStatus == GameStatus.IN_BLOCK)
          gameStatus = GameStatus.WAIT_FOR_NEXT_BLOCK;
        if (currentBlock == numBlocks)
          gameStatus = GameStatus.END_OF_BLOCKS;

        running = false;
        repaint();
        return;
      }
      System.out.println("Trial Number: " + trialNumber);
      status = 0;
      readyToRepaint = true;
      startOfTrial = false;
      System.out.println("Show new trial.");
    }
    else if(timeSinceStart == stimulusDuration && !stimulusHidden)
    {
      status = 1;
      readyToRepaint = true;
      stimulusHidden = true;
      System.out.println("hide the trial.");
    }
    else if((incorrectInput && timeSinceStart == responseTimeOut + delayDuration 
        || !incorrectInput && timeSinceStart == responseTimeOut) 
        && !notificationShown)
    {
      waitForFeedBackDelay = false;
      status = 2;
      readyToRepaint = true;
      notificationShown = true;
      //needs to be here because a user's key press can bring back the timeline here as well
      //during incorrect
      if (trialType == 1 && targetInputType == InputType.IGNORE ||
          trialType == 0 && standardInputType == InputType.IGNORE ||
          trialType == 2 && novelInputType == InputType.IGNORE)
      {
        //even if the stimulus type is ignore, if no error feedback is needed, treat it as correct response
        if(!keyPressed)
        {
          correctInput = true;
          readyToRepaint = false;
          trialNumber++;
        }
      }
      System.out.println("Notification is shown.");
    }
    //500 ms is the duration of the too slow or incorrect notifcation 
    else if((incorrectInput && timeSinceStart == responseTimeOut + notificationDuration + delayDuration ||
        !incorrectInput && timeSinceStart == responseTimeOut + notificationDuration) && 
        !paused)
    {
      status = 3;
      ITI = intervalMin + (int)(Math.random()*(intervalMax-intervalMin+1));
      paused = true;
      //this is needed so that when plus sign forces a repaint, we get the plus sign
      //instead of letting this if statement increment the trialNumber as well
      if(!correctInput)
      {
        trialNumber++; //must be here to show the plus sign
      }
      keyPressed = false;
      readyToRepaint = true;
      System.out.println("ITI.");
    }
    //set up the next trial
    else if((incorrectInput && timeSinceStart > responseTimeOut + delayDuration + notificationDuration + ITI) ||
        (!incorrectInput && timeSinceStart > responseTimeOut + notificationDuration + ITI) )
    {
      status = 0;
      startTime = System.currentTimeMillis();
      trialNumber++;
      resetGameLogicVariables();
      if (firstTime)
      {
        firstTime = false;
      }
    }
  }

  //resets all the game logic variables used in the controlTimeLine method
  private void resetGameLogicVariables()
  {
    startOfTrial = true; //need to start the new trial
    stimulusHidden = false;
    notificationShown = false;
    paused = false;
    keyPressed = false;
    startTimeAdjusted = false;
    correctInput = false;
    incorrectInput = false;
    readyToRepaint = true;
    notificationDuration = realNotificationDuration;
    stimTriggerSent = false;
  }

  //TODO: used for logging, append info to it
  private LogInfoStatus logStatus = LogInfoStatus.FETCH_TRIAL;
  String logRowEntry = "";
//  private boolean userEnteredInput = false;
  //*******************************************************
  //User must be allowed to set which keys to press for
  //which image.
  //*******************************************************
  @Override
  public void keyPressed(KeyEvent arg0)
  {
    int keyCode = arg0.getKeyCode();

    byte trigger = getKeyPressTrigger(keyCode);
    if (gameStatus != GameStatus.IN_PRACTICE) 
    {
      OddballsEngine.logger.scheduleSocketOut(trigger);
    }
    if (running)
    {
      //S for standard (Os)
      //N for novel (Weird shapes)
      //T for target (Xs)
      //respone hasn't timed out if notification is not shown yet
      if (!notificationShown && !paused)
      {
        int accuracy = 0; //0 = wrong, 1 = correct
        long responseTime = currentTime - startTime;
        keyPressed = true;
        //when user presses the right input key (don't forget to let users choose which keys to press)
        if (keyCode == standardKey && trialType == 0 && standardInputType == InputType.REQUIRED ||
            keyCode == targetKey && trialType == 1 && targetInputType == InputType.REQUIRED ||
            keyCode == novelKey && trialType == 2 && novelInputType == InputType.REQUIRED)
        {
          userIsCorrect();
          accuracy = 1;
        }
        else
        {
          //this all means incorrect, and if you typed a response for ignore input types as well
          if (trialType == 1 && targetInputType != InputType.NULL ||
              trialType == 0 && standardInputType != InputType.NULL ||
              trialType == 2 && novelInputType != InputType.NULL)
          {
            userIsIncorrect();
            accuracy = 0;
          }
          System.out.println("TrialNumber=" +trialNumber+ ", trialType="+trials[trialNumber]+
              " userIsIncorrect or responded to ignores, delayDuration="+delayDuration);
        }

        //TODO: trialNumber/2 because of the plus signs
        System.out.println("logFile="+logFile);
        logRowEntry = logRowEntry+((trialNumber/2)+",")+currentStim+","+responseTime +","+accuracy;
        if (gameStatus == GameStatus.IN_BLOCK && !logFile.equals("/.txt") &&
            !logFile.equals(""))
        {
          try
          {
            log = new PrintWriter(new FileWriter(logFile, true));
          }
          catch(Exception i) {}
          log.println(logRowEntry);
          log.close();
        }
        logRowEntry = "";
      }
    }
    else if (!running)
    {
      //must not be running to work so even if space bar is a keyboard input, it's fine.
      if (keyCode == KeyEvent.VK_SPACE) 
      {
        //from here to line 768 is needed to enable pracTrials to be 0
        if (gameStatus == GameStatus.SHOW_INSTRUCTION)
        {
          gameStatus = GameStatus.IN_PRACTICE;
          if (pracTrials == 0)
          {
            gameStatus = GameStatus.IN_BLOCK;
            currentBlock++;
            Thread t = new Thread(new Runnable()
            {
              @Override
              public void run()
              {
                startGame();
              }
            });
            t.start();
          }
          else
          {
            Thread t = new Thread(new Runnable()
            {
              @Override
              public void run()
              {
                start();
              }
            });
            t.start();
          }
        }
        else if ((gameStatus == GameStatus.PRACTICE_OVER || gameStatus == GameStatus.WAIT_FOR_NEXT_BLOCK))
        {
          gameStatus = GameStatus.IN_BLOCK;
          System.out.println("key pressed to go to the real game");
          startGame(); //starts a new thread.
          currentBlock++;
        }
      }
      if (gameStatus == GameStatus.END_OF_BLOCKS)
      {
        //close the log PrintWriter if it is initialized in the first place...
        if (log != null) log.close();
        System.exit(0);
      }
    }
  }

  private void userIsCorrect()
  {
    correctInput = true;
    trialNumber++;
    repaint();
  }

  private void userIsIncorrect()
  {
    //if no feedback, then this will not work and show incorrect
    if(hasErrorFeedback)
    {
      incorrectInput = true;
      if (hasFeedbackDelay)
      {
        stimulusHidden = true;
        //        if (delayDuration > 0)
        //        {
        waitForFeedBackDelay = true;
        //        }
        //        System.out.println("delayDuration="+delayDuration);
        repaint(); //must show plus sign
        readyToRepaint = false;
      }
      else
      {
        //        stimulusHidden = true;
        status = 2;
        repaint();
        //        readyToRepaint = false; 
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent arg0)
  {}

  @Override
  public void keyTyped(KeyEvent arg0)
  {}

  private void showPlusSignPassive()
  {
    startTime = System.currentTimeMillis();
    trialNumber++;  //proceed to the next trial from the trial sequence
    paused = true;
    repaint();
    ITI = intervalMin + (int)(Math.random()*(intervalMax-intervalMin+1));
  }
}
