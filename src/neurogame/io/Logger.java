package neurogame.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Logger
{

  public enum LogType
  {
    GAME, ODDBALL, BANDIT
  };

  private static final String LOG_EXTENSION = ".csv";
  private static final String PATH = "logs/";
//  private static final String PARALLEL_CONNECTION_PROGRAM = "ParallelPortTrigger/timer.exe";
  private static final String PARALLEL_CONNECTION_PROGRAM = "./timerexec/timer.exe";
  private static Calendar calendar;

  private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd.HH-mm");

  private User user;

  private File logFile;
  private BufferedWriter writer;

  public SocketToParallelPort socket;
  private static final String HOST = "127.0.0.1";
  private static final int PORT = 55555;

  private byte socketByteLast;
  private byte socketByteSend;

  private double startSec;
  private byte gameCount = 0;
  private boolean sentEndGameSignal;


  public Logger()
  {
    try
    {
      System.out.println("Starting External Process: " + PARALLEL_CONNECTION_PROGRAM);
      Runtime.getRuntime().exec(PARALLEL_CONNECTION_PROGRAM);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    socket = new SocketToParallelPort(HOST, PORT);
    socketByteLast = -1;
    socketByteSend = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    updateSocket();
  }
  

  /*
  public Logger(InputController controller, User user, LogType type, double timeSec_start, int foo)
  {
    this.user = user;
    this.controller = controller;

    String fileName = generateFileName(type);

    logFile = new File(PATH, fileName);
    logFile.getParentFile().mkdir();

    // Enemy Type:
    // NONE = 0
    // ENEMY_STRAIGHT = 1;
    // ENEMY_FOLLOW = 2;
    // ENEMY_SINUSOIDAL = 3;
    // ZAPPER = 4;

    // Missile Target:
    // 0: no missile
    // -1 No target
    // 1 through Enemy.MAX_ENEMY_COUNT: Index of enemy that is within verticle
    // hit area.

    String out = "Seconds, PlayerX, PlayerY, Health, Ammo, JoystickX, JoystickY, JoystickButton, Trigger, WallAbove, WallBelow, ";
    for (int i = 0; i < Enemy.MAX_ENEMY_COUNT; i++)
    {
      int n = i + 1;
      out += "Enemy " + n + " Type,Enemy " + n + " Proximity,Enemy " + n + " Angle, ";
    }
    for (int i = 0; i < Star.MAX_STAR_COUNT; i++)
    {
      int n = i + 1;
      out += "Star " + n + " Proximity, Star " + n + " Angle, ";
    }

    // SimpleDateFormat dateFormat = new SimpleDateFormat
    // ("EEEE: MMMM d yyyy 'at' h:mm:ss a zzz");

    // Date curDate = new Date();

    //long nanoTime = System.nanoTime();

    //startSec = nanoTime * NeuroGame.systemTimeUnitToSec
    startSec = timeSec_start;
    String milliSecOfDay = getCurrentTimStr();
    // out +=
    // "AmmoProximity, AmmoAngle, Missile Target, Missile Proximity to Target\nStart Date/Time: "
    // + dateFormat.format(curDate) + "\n";
    out += "AmmoProximity, AmmoAngle, Missile Target, Missile Proximity to Target\n" + milliSecOfDay + "\n";

    try
    {
      writer = new BufferedWriter(new FileWriter(logFile));
      writer.write(out);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      System.exit(0);
    }

    try
    {
      Runtime.getRuntime().exec(PARALLEL_CONNECTION_PROGRAM);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    socket = new SocketToParallelPort(HOST, PORT);
    socketByteLast = -1;
    socketByteSend = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    updateSocket();
  }
  */
  
  public static String getCurrentTimStr()
  {
    // StartTime: HHMMSSmmm

    // long millis = nanoSec / 1000000;
    // Calendar cal = Calendar.getInstance();
    // cal.setTimeInMillis(millis);

    calendar = GregorianCalendar.getInstance();
    Date curDate = new Date();
    calendar.setTime(curDate);
    int hours = calendar.get(Calendar.HOUR_OF_DAY);
    int min = calendar.get(Calendar.MINUTE);
    int sec = calendar.get(Calendar.SECOND);
    int milliSec = calendar.get(Calendar.MILLISECOND);
    // int milliSec = calendar.get(Calendar.MILLISECOND);
    // System.out.println("Hours="+hours);
    // long millisecondsAfterHour = ( nanoSec / 1000000) % MILLISEC_PER_HOUR;
    // System.out.println("millisecondsAfterHour="+millisecondsAfterHour);
    // millis -= TimeUnit.HOURS.toMillis(hours);
    // long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
    // millis -= TimeUnit.MINUTES.toMillis(minutes);
    // long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

    // System.out.println("hours="+hours + (hours*10000000L));
    // return Long.toString(hours * 10000000L + min * 100000L +
    // (long)sec*1000L);

    String outStr = String.format("%02d%02d%02d%03d", hours, min, sec, milliSec);
    System.out.println(curDate + ",  Time in HHMMSSmmm: " + outStr);
    return outStr;
  }

  private String generateFileName(LogType type)
  {
    return "Axon_" + type + "_Log_" + user.getName() + '_' + FILE_DATE_FORMAT.format(new Date()) + LOG_EXTENSION;
  }

  public byte updateSocket()
  {
//	  System.out.println("In Logger's updateSocket()");
    if (socket == null)
    {
      socketByteLast = 0;
      return 0;
    }

    if (socketByteLast != SocketToParallelPort.TRIGGER_SIGNAL_GROUND)
    {
      socket.sendByte(SocketToParallelPort.TRIGGER_SIGNAL_GROUND);
      socketByteLast = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    }

    else if (socketByteSend != SocketToParallelPort.TRIGGER_SIGNAL_GROUND)
    {
      socket.sendByte(socketByteSend);
      socketByteLast = socketByteSend;
      socketByteSend = SocketToParallelPort.TRIGGER_SIGNAL_GROUND;
    }
    return socketByteLast;
  }

  public void scheduleSocketOut(byte data)
  {
    // socket.sendByte(data);
    socketByteSend = data;

  }

//  public void closeLog()
//  {
//    if (socket != null)
//    {
//      try
//      {
//        socket.close();
//        writer.close();
//      }
//      catch (IOException e)
//      {}
//    }
//  }
  
  
  public void closeLog(boolean closeSocket)
  {
    if ((closeSocket) && (socket != null))
    {
      try
      {
        socket.close();
        //TODO end socket process
      }
      catch (Exception e)
      {}
    }
    
    if (logFile != null)
    {
      try
      {
        writer.close();
      }
      catch (IOException e)
      {}
    }
  }
}
