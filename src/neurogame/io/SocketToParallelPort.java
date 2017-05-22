package neurogame.io;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketToParallelPort
{
  private Socket clientSocket;
  private DataOutputStream writer;
  //private long startNanoSec;
  
  public static final byte TRIGGER_SIGNAL_GROUND          = 0;
  
  public static final byte TRIGGER_ODDBALL_START          = 20;
  public static final byte TRIGGER_ODDBALL_STANDARD_EVENT = 22;
  public static final byte TRIGGER_ODDBALL_RARE_EVENT     = 25;
  public static final byte TRIGGER_ODDBALL_DONE           = 29;  

  public static final byte TRIGGER_BANDIT_START           = 50;
  public static final byte TRIGGER_BANDIT_LOSE            = 51;
  public static final byte TRIGGER_BANDIT_WIN             = 52;
  public static final byte TRIGGER_BANDIT_DONE            = 53;

  public static final byte TRIGGER_GAME_SHOOT_BUTTON       = 90;
  public static final byte TRIGGER_GAME_PLAYER_CRASH_WALL  = 91;
  public static final byte TRIGGER_GAME_PLAYER_CRASH_ENEMY = 92;
  public static final byte TRIGGER_GAME_COLLECT_STAR       = 93;
  public static final byte TRIGGER_GAME_COLLECT_AMMO       = 94;
  public static final byte TRIGGER_GAME_MISSILE_HIT_ENEMY  = 97;
  public static final byte TRIGGER_GAME_ENEMY_LOST         = 98; //hit other enemy, wall or scrolled off screen.
  public static final byte TRIGGER_GAME_OVER               = 99;
  public static final byte TRIGGER_GAME_START_BASE         = 100; //Each game in series starts with 100, 101, 102, ...
  
  public SocketToParallelPort(String host, int portNumber)
  {
    //startNanoSec = System.nanoTime();
    System.out.println("SocketToParallelPort: Starting Client: ");


    while (!openConnection(host, portNumber))
    {
      System.out.println("SocketToParallelPort: Trying to open socket to " + host + ":"+portNumber+"   ");
    }
  }


  private boolean openConnection(String host, int portNumber)
  {
    try
    {
      clientSocket = new Socket(host, portNumber);
    }
    catch (UnknownHostException e)
    {
      System.err.println("SocketToParallelPort: ***Error***: Unknown Host " + host);
      return false;
    }
    catch (IOException e)
    {
      System.err.println("SocketToParallelPort: ***Error***: Could not open connection to " + host
          + " on port " + portNumber);
      return false;
    }

    try
    {
      OutputStream outStream = clientSocket.getOutputStream(); 
      writer = new DataOutputStream(outStream);
    }
    catch (IOException e)
    {
      System.err.println("SocketToParallelPort: ***Error***: Could not open output stream");
      e.printStackTrace();
      return false;
    }
    return true;

  }
  
  
  public void sendByte(byte data)
  {
    byte[] outByteArray = {data};
    try
    {
      writer.write(outByteArray);
      System.out.println("Send Trigger on Parallel Port: "+data);
    }
    catch (IOException e) 
    {
      System.err.println("SocketToParallelPort.sendByte("+data+")"+": ***Error***: Could not write to output stream");
    }
  }


  public void close()
  {
    System.out.println("Client.closeAll()");

    if (writer != null) 
    { try
      {
        writer.close();
      }
      catch (IOException e)
      {
        System.err.println("SocketToParallelPort: ***Error***: Could not close output stream");
      }
    }
  }

//  private String timeDiff()
//  {
//    long namoSecDiff = System.nanoTime() - startNanoSec;
//    double secDiff = (double) namoSecDiff / 1000000000.0;
//    return String.format("%.6f", secDiff);
//
//  }

  public static void main(String[] args)
  {
    
    String host = "sycorax.cs.unm.edu";
    int port = 0;
   
    try
    {
      host = args[0];
      port = Integer.parseInt(args[1]);
      if (port < 1) throw new Exception();
    }
    catch (Exception e)
    {
      System.out.println("Usage: Client host portNumber");
      System.exit(0);
    }
    new SocketToParallelPort(host, port);

  }
}
