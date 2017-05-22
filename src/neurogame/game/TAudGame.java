package neurogame.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;

import neurogame.game.Game.GameStatus;

public class TAudGame extends Game implements KeyListener 
{
  private static final long serialVersionUID = 1L;
  private boolean running;
  private BufferedInputStream soundFile;
  private Clip clip;
  private ArrayList<BufferedInputStream> novelSounds; //jar files cant make File type
  private Scanner scanner;
  private BufferedImage plus;	
  private BufferedImage tooSlow; //the image with the text "Too Slow"
  private BufferedImage incorrect; //the image with the text "Incorrect"
  private BufferedImage practiceOver;
  private BufferedImage blockOver;
  private BufferedImage allBlocksDone;
  private BufferedImage countDown1;
  private BufferedImage countDown2;
  private BufferedImage countDown3;
  private int currentBlock = 0;
  
  //For logging:
  PrintWriter log; //initialized in start()
  
  public TAudGame() 
  {
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
      clip = AudioSystem.getClip();
    } 
    catch(Exception e) 
    {
      e.printStackTrace();
    }
    randomTime = new Random(0);
    SwingUtilities.invokeLater(new Runnable() 
    {
      @Override
      public void run() 
      {
        repaint();
      }
    });
    readSoundList();
    this.setFocusable(true);
    this.addKeyListener(this);
    this.setBackground(Color.BLACK);
  }

  //gets the novel sounds ready
  public void readSoundList() 
  {
    ClassLoader classLoader = getClass().getClassLoader();
    novelSounds = new ArrayList<BufferedInputStream>();
    try 
    {
      //in a jar file, you can't make File types
      BufferedInputStream listFile = new BufferedInputStream(classLoader.
          getResourceAsStream("sounds/soundList.txt"));
      scanner = new Scanner(listFile);
      while(scanner.hasNextLine()) 
      {
        String name = "/sounds/" + scanner.nextLine();
        if(!name.contains("440 HzNew.wav") && !name.contains("600 HzNew.wav")) 
        {
          BufferedInputStream novel = new BufferedInputStream(getClass().
              getResourceAsStream(name));
          novelSounds.add(novel);
        }
      }
      scanner.close();
    } 
    catch(Exception e) {}	
  }

  private int newSoundIndex() 
  {
    return (int)(Math.random()*novelSounds.size());
  }

  //update the clip to play
  private void initializeSound() 
  {
    int trialType = trials[trialNumber];
    //System.out.println("trialType="+trialType);
    try 
    {
      String soundName = "/sounds/600 HzNew.wav";
      if(trialType == 1) 
      {
        soundName = "/sounds/600 HzNew.wav";
      } 
      else if(trialType == 0)
      {
        soundName = "/sounds/440 HzNew.wav";
      }
      soundFile = new BufferedInputStream(getClass().getResourceAsStream(soundName));
      if(trialType == 2) 
      {
        int soundIndex = newSoundIndex();
        soundFile = novelSounds.get(soundIndex);
        System.out.println("soundIndex: " + soundIndex + ", size of ArrayList: " + novelSounds.size());
      }
      AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
      clip = AudioSystem.getClip();
      clip.open(ais);
    } 
    catch(Exception e) 
    {
      System.out.print("Status: " + status + ", ");
      System.out.println("size of novelSounds ArrayList: " + novelSounds.size());
      e.printStackTrace();
    }
  }

  
  private ActiveOddballThread aot;
  private boolean practiceFinished = false;
  private boolean realGameHasErrorFeedback = false;
  //passive as default
  public void start() 
  {
    try
    {
      //rewrites the file first
      log = new PrintWriter(logFile);
      log.println("ID:");
      log.println("Date:");
      log.println("Protocol Name:");
      log.println("Trial,Trigger,RT,Acc");
      //TODO: REMEMBER THAT YOU HAVE  TO REINSTANTIATE A NEW PRINTWRITER!!!
      log.close(); 
    }
    catch (Exception e) {}
    
    realGameHasErrorFeedback = hasErrorFeedback;
    hasErrorFeedback = hasPracticeErrorFeedback;
    trials = TrialSequenceGenerator.generateSequence(percentTarget/100, percentNovel/100, pracTrials, false);
    //problem, active oddball don't paint incorrect or too slow.
    if (targetInputType == InputType.NULL && 
        standardInputType == InputType.NULL && 
        novelInputType == InputType.NULL)
    {
      passiveGame = true;
    }
    if(passiveGame)
    {
//      countDown();
      PassiveOddballThread pot = new PassiveOddballThread();
      pot.start();
    }
    else
    {
//      countDown();
      aot = new ActiveOddballThread();
      aot.start();
    }
  }
 
  private void startGame()
  {
    trialNumber = 0;
    hasErrorFeedback = realGameHasErrorFeedback;
    System.out.println("currentBlock="+currentBlock);
    if (currentBlock <= numBlocks)
    {
      trials = TrialSequenceGenerator.generateSequence(percentTarget/100, percentNovel/100, gameTrials, false);
      if (targetInputType == InputType.NULL && 
          standardInputType == InputType.NULL && 
          novelInputType == InputType.NULL)
      {
        passiveGame = true;
      }
      if (passiveGame)
      {
        PassiveOddballThread pot = new PassiveOddballThread();
//        countDown();
        pot.start();
      }
      else
      {
        aot = new ActiveOddballThread();
//        countDown();
        aot.start();
      }
    }
  }

  private int ITI;
  long startTime;
  long currentTime;
  private int[] trials;
  private int trialNumber = 0;

  //good already
  public void passiveOddballLogic()
  {
    running = true;
    startTime = System.currentTimeMillis();
    ITI = intervalMin + (int)(Math.random()*(intervalMax-intervalMin+1));
    while(running)
    {
      if (trialNumber >= trials.length)
      {
        running = false;
        if (gameStatus == GameStatus.IN_PRACTICE) gameStatus = GameStatus.PRACTICE_OVER;
        if (gameStatus == GameStatus.IN_BLOCK)
        {
          if (currentBlock < numBlocks) 
            gameStatus = GameStatus.WAIT_FOR_NEXT_BLOCK;
          else if (currentBlock == numBlocks) 
            gameStatus = GameStatus.END_OF_BLOCKS;
        }
        repaint();
        break;
      }
      try
      {
        System.out.println("ITI is: " + ITI);
        initializeSound();
        clip.start();
        System.out.println("Clip is played.");
        Thread.sleep(stimulusDuration);
        clip.stop();
        System.out.println("Clip is stopped.");
        Thread.sleep(ITI);
        clip.flush();
        trialNumber++;
        ITI = intervalMin + (int)(Math.random()*(intervalMax-intervalMin+1));
      }
      catch(Exception e)
      {}
    }
  }

  /**
   * status:
   * 0 = target, 1 = standard(ignore), 2 = novel(distraction)
   */
  @Override
  public void updateStatus()
  {
    status = (int)(Math.random()*4);
  }

  @Override
  public void paintComponent(Graphics g) 
  {
    super.paintComponent(g);
//    g.drawImage(plus, centerX(plus), centerY(plus), null);
    
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
    
    if (gameStatus == GameStatus.PRACTICE_OVER)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(practiceOver, centerX(practiceOver), centerY(practiceOver), null);
      practiceFinished = true;
    }
    else if (gameStatus == GameStatus.WAIT_FOR_NEXT_BLOCK)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(blockOver, centerX(blockOver), centerY(blockOver), null);
    }
    else if (gameStatus == GameStatus.END_OF_BLOCKS)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(allBlocksDone, centerX(allBlocksDone), centerY(allBlocksDone), null);
    }
    else if (gameStatus == GameStatus.SHOW_INSTRUCTION)
    {
      g.drawImage(null, 0, 0, null);
      g.drawImage(instructionImage, centerX(instructionImage), centerY(instructionImage), 
                  null);
    }
    else 
    {
      if(imageType == 2)
      {
        g.drawImage(plus, centerX(plus), centerY(plus), null);
        if (gameStatus != GameStatus.IN_PRACTICE)
        {
          OddballsEngine.logger.scheduleSocketOut(Game.NOTIF_INC);
        }
      }
      else if(imageType == 1)
      {
        g.drawImage(incorrect, centerX(incorrect), centerY(incorrect), null);
      }
      else if(imageType == 0)
      {
        g.drawImage(tooSlow, centerX(tooSlow), centerY(tooSlow), null);
        if (gameStatus != GameStatus.IN_PRACTICE)
        {
          OddballsEngine.logger.scheduleSocketOut(Game.NOTIF_SLOW);
        }
        if (gameStatus == GameStatus.IN_BLOCK && !logFile.equals("/.txt"))
        {
          try
          {
            String logFileEntry = trialNumber+","+currentStim+",NaN,NaN";
            log = new PrintWriter(new FileWriter(logFile, true));
            log.println(logFileEntry);
            log.close();
          }
          catch (Exception ex) {}
        }
      }
    }
  }

  boolean changeImage = false;
  private boolean showError = false;
  private boolean firstTime = true;
  @Override
  public void keyPressed(KeyEvent e)
  {
    int keyCode = e.getKeyCode();
    
    byte trigger = getKeyPressTrigger(keyCode);
    if (gameStatus != GameStatus.IN_PRACTICE)
    {
      OddballsEngine.logger.scheduleSocketOut(trigger);
    }
    if (gameStatus == GameStatus.PRACTICE_OVER ||
        gameStatus == GameStatus.WAIT_FOR_NEXT_BLOCK ||
        gameStatus == GameStatus.SHOW_INSTRUCTION)
    {
      //press space bar to continue to the game
      if (keyCode == KeyEvent.VK_SPACE)
      {
        if (gameStatus == GameStatus.SHOW_INSTRUCTION && pracTrials > 0)
        {
          gameStatus = GameStatus.IN_PRACTICE;
          imageType = 2;
          repaint();
          start();
          return;
        }
        realGameHasErrorFeedback = hasErrorFeedback;
        if (firstTime)
        {
          try
          {
            //rewrites the file first
            log = new PrintWriter(logFile);
            log.println("ID:");
            log.println("Date:");
            log.println("Protocol Name:");
            log.println("Trial,Trigger,RT,Acc");
            //TODO: REMEMBER THAT YOU HAVE  TO REINSTANTIATE A NEW PRINTWRITER!!!
            log.close(); 
            firstTime = false;
          }
          catch (Exception exception) {}
        }
        imageType = 2;
        repaint();
        currentBlock++;
        gameStatus = GameStatus.IN_BLOCK;
        startGame();
        return;
      }
    }
    else if (gameStatus == GameStatus.END_OF_BLOCKS)
    {
      //close the log PrintWriter if it is initialized in the first place...
      if (log != null) log.close();
      System.exit(0);
    }
    if (!passiveGame)
    {
      int trialType = trials[trialNumber];
      if (!responseTimedOut && !keyPressed)
      {
        long responseTime = System.currentTimeMillis() - startTime;
        if (keyCode == standardKey && trialType == 0 && standardInputType == InputType.REQUIRED||
            keyCode == targetKey && trialType == 1 && targetInputType == InputType.REQUIRED ||
            keyCode == novelKey && trialType == 2 && novelInputType == InputType.REQUIRED)
        {
          keyPressed = true;
          aot.interrupt();
          clip.stop();
          clip.flush();
          if (gameStatus == GameStatus.IN_BLOCK && !logFile.equals("/.txt"))
          {
            try
            {
              //correct
              String logFileEntry = trialNumber+","+currentStim+","+responseTime +",1";
              log = new PrintWriter(new FileWriter(logFile, true));
              log.println(logFileEntry);
              log.close();
            }
            catch (Exception ex) {}
          }
        }
        else
        {
          //you can only be correct when you press the correct button and the inputType is 2
          //you get it wrong all the time when the inputType is 1
          //you are never wrong and never correct when the inputType is 0
          if (trialType == 1 && targetInputType != InputType.NULL ||
              trialType == 0 && standardInputType != InputType.NULL ||
              trialType == 2 && novelInputType != InputType.NULL)
          {
            keyPressed = true;
            aot.interrupt();
            clip.stop();
            clip.flush();
            imageType = 1;
            if (gameStatus == GameStatus.IN_BLOCK && !logFile.equals("/.txt"))
            {
              try
              {
                //incorrect
                String logFileEntry = trialNumber+","+"trigger here"+","+responseTime +","+"0";
                log = new PrintWriter(new FileWriter(logFile, true));
                log.println(logFileEntry);
                log.close();
              }
              catch (Exception ex) {}
            }
            if(hasFeedbackDelay)
            {
              showError = true;
            }
            else if(!hasFeedbackDelay)
            {
              repaint();
            }
          }
        }
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e)
  {}

  @Override
  public void keyTyped(KeyEvent e)
  {}

  
  private class PassiveOddballThread extends Thread
  {
    @Override
    public void run()
    {
      countDown();
      repaint();  //show the plus sign
      passiveOddballLogic();
    }
  }
  
  
  private boolean responseTimedOut = false;
  private boolean keyPressed = false;
  private int imageType = 2; //0 for too slow, 1 for incorrect, 2 for plus
  private int notificationDuration = 500;
  
  
  private int currentStim = -1; //some non-sense value to start with
  //where to run thread
  private class ActiveOddballThread extends Thread
  {
    private int realNotificationDuration = 500;
    @Override
    public void run()
    {
      countDown();  
      repaint();    //show the plus sign
      running = true;
      startTime = System.currentTimeMillis();
      ITI = intervalMin + (int)(Math.random()*(intervalMax-intervalMin+1));
      while(running)
      {
        //stop the game when there are no more trials left
        if (trialNumber >= trials.length)
        {
          running = false;
          if (gameStatus == GameStatus.IN_PRACTICE) gameStatus = GameStatus.PRACTICE_OVER;
          if (gameStatus == GameStatus.IN_BLOCK)
          {
            if (currentBlock < numBlocks) 
             gameStatus = GameStatus.WAIT_FOR_NEXT_BLOCK;
            else if (currentBlock == numBlocks) 
              gameStatus = GameStatus.END_OF_BLOCKS;
          }
          repaint();
          break;
        }
        int trialType = trials[trialNumber];
        System.out.println("trialType="+trialType);
        //if either one is null, passive game logic will run before this code is reached
        if(trialType == 1 && (targetInputType == InputType.NULL) ||        
            trialType == 0 && (standardInputType == InputType.NULL) ||
            trialType == 2 && (novelInputType == InputType.NULL) || !hasErrorFeedback) 
        {
          //feedback delay doesn't have to be 0 if null
          notificationDuration = 0;
        }
        try
        {
          if(keyPressed)
          {
            //for feedback delay
            if(hasFeedbackDelay)
            {
              sleep(delayDuration);
              repaint();  
            }
            System.out.println("keyPressed, show incorrect.");
            sleep(notificationDuration);
            imageType = 2;
            repaint();
            sleep(ITI);
            trialNumber++;
            ITI = intervalMin + (int)(Math.random()*(intervalMax-intervalMin+1));
            keyPressed = false;
            responseTimedOut = false;
          }
          else
          {
            //            System.out.println("interrupt makes while loop loop over again.");
            //          System.out.println("trialNumber = " + trialNumber);
            //            System.out.println("ITI is: " + ITI);
            initializeSound();
            clip.start();
            if (gameStatus != GameStatus.IN_PRACTICE)
            {
              if (trialType == 0)
              {
                OddballsEngine.logger.scheduleSocketOut(Game.STIM_STD);
                currentStim = Game.STIM_STD;
              }
              else if (trialType == 1)
              {
                OddballsEngine.logger.scheduleSocketOut(Game.STIM_TAR);
                currentStim = Game.STIM_TAR;
              }
              else if (trialType == 2)
              {
                OddballsEngine.logger.scheduleSocketOut(Game.STIM_NOV);
                currentStim = Game.STIM_NOV;
              }
              System.out.println("IN TAudGame line 593: "+currentStim);
            }
            System.out.println("Stimulus is played.");

            sleep(stimulusDuration);
            clip.stop();  //stimulus duration is over!
            System.out.println("Stimulus is stopped.");
            clip.flush();

            sleep(responseTimeOut-stimulusDuration); //you give user a little bit more time to press key
            System.out.println("response timed out!");
            responseTimedOut = true; 
            
            //too slow will be shown if no key pressed
            if (hasFeedbackDelay)
            {
              System.out.println("Waiting for feedback delay.");
              sleep(delayDuration);
            }
            //only paint Too Slow if input type is 2 and !keyPressed
            if (!keyPressed && 
                (trials[trialNumber] == 0 && standardInputType == InputType.REQUIRED ||
                trials[trialNumber] == 1 && targetInputType == InputType.REQUIRED ||
                trials[trialNumber] == 2 && novelInputType == InputType.REQUIRED))
            {
              System.out.println("Show too slow");
              imageType = 0;
              repaint();
            }
            else  if (!keyPressed && 
                (trials[trialNumber] == 0 && standardInputType == InputType.IGNORE ||
                trials[trialNumber] == 1 && targetInputType == InputType.IGNORE ||
                trials[trialNumber] == 2 && novelInputType == InputType.IGNORE))
            {
              //supposed to ignore the ignore inputTypes
              notificationDuration = 0; //it's not too slow or incorrect.
            }
            
            sleep(notificationDuration); //show the notification
            imageType = 2;
            repaint(); //show the plus sign again (imageType = 2)

            System.out.println("Wait for ITI, which is "+ITI+"ms");
            sleep(ITI);
            trialNumber++;

            ITI = intervalMin + (int)(Math.random()*(intervalMax-intervalMin+1));
            keyPressed = false;
            responseTimedOut = false;
            notificationDuration = realNotificationDuration;
          }
        }
        catch(Exception e)
        {
        }
        
        //reset startTime
        startTime = System.currentTimeMillis();
      }
    }
  }

  @Override
  public void initialize()
  {
    
  }

}
