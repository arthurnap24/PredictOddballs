package neurogame.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class User implements Comparable<User> 
{
  public enum SortType {SORT_BY_NAME, SORT_BY_SCORE};
  private SortType sortMode = SortType.SORT_BY_SCORE;

  private String name;
  private int highScore = 0;
  private String highScoreDate = "New Player";
  private boolean logging = true;
  private String controllerName = "Logitech Dual Action";//name of joystick currently in Logan Lab
  private int joystickIdx_x = 3;
  private int joystickIdx_y = 2;

  public static final String filePath = "/resources/users.txt";
  public static final String COLUMN_HEADERS = "UserName, HighScore, Date, Logging, Controller, x-axis, y-axis";
  private static final DateFormat dateFormat = new SimpleDateFormat("MMM-d-yyyy");
  
  private static ArrayList<User> userList = new ArrayList<User>();

  public User(String str)
  {
    String[] token = str.split("[,]");
    
    name = token[0];
    
    if (token.length == 7)
    {
      highScore      = Integer.parseInt(token[1]);
      highScoreDate  = token[2];
      logging        = Boolean.parseBoolean(token[3]);
      controllerName = token[4];
      joystickIdx_x  = Integer.parseInt(token[5]);
      joystickIdx_y  = Integer.parseInt(token[6]);
    }
  }

  public String toString()
  {
    return name + "," + highScore + "," + highScoreDate + "," + logging + "," + controllerName + "," + joystickIdx_x + ","
        + joystickIdx_y;
  }

  public void setHighscore(int score)
  {
    if (score > highScore)
    { Date date = new Date();

      highScore = score;
      highScoreDate = dateFormat.format(date);
      saveUsers();
    }
  }

  public String getName()
  {
    return name;
  }

  public int getHighScore()
  {
    return highScore;
  }
  
  public String getHighScoreDate()
  { return highScoreDate;
  }
  
  public boolean isLogging() { return logging;}
  public void setLogging(boolean state) {logging = state;}
  public String getController() { return controllerName;}
  public void setController(String controller) {controllerName = controller;}
  
  public int getControllerXAxis() { return joystickIdx_x;}
  public int getControllerYAxis() { return joystickIdx_y;}
  public void setControllerXAxis(int idx) { joystickIdx_x = idx;}
  public void setControllerYAxis(int idx) { joystickIdx_y = idx;}
  
  public static ArrayList<User> getUserList() { return userList; }
  public static User getUser(int idx) { return userList.get(idx); }
  public static User getUser(String name)
  {
    for (User user : userList)
    { 
      if (name.equals(user.getName()))  return user;
    } 
    return null;
  }
  
  public static User addUser(String name)
  {
    User user = new User(name);
    userList.add(user);
    return user;
  }

  
  public static void loadUsers()
  {
    userList.clear();
    String path = System.getProperty("user.dir");
    File file = new File(path+filePath);
    System.out.println("User.loadUsers() filepath="+file.getAbsolutePath());
    
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      
      String record = reader.readLine(); //The first record should be a header line
      record = reader.readLine();
      while (record != null)
      {
        System.out.println("   record=" + record);
        userList.add(new User(record));
        
        User user = userList.get(userList.size()-1);
        System.out.println("   ====>user=" + user);
        
        record = reader.readLine();
      }
      reader.close();
    }
    catch (Exception e)
    {
      e.getStackTrace();
      System.out.println("Problem Parsing User: " + file.getName());
    }
  }

  public static void saveUsers()
  {
    String path = System.getProperty("user.dir");
    File file = new File(path+filePath);
    System.out.println("User.saveUsers() filepath="+file.getAbsolutePath());
    try
    {
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      
      writer.write(COLUMN_HEADERS);
      writer.newLine();
      
      for (User user : userList)
      { writer.write(user.toString());
        writer.newLine();
      }

      writer.close();
    }
    catch (Exception e)
    {
      e.getStackTrace();
      System.out.println("User.saveUsers ERROR: " + file.getName());
    }
  }
  
  public static int getUserCount()
  { return userList.size();
  }
  
  
  
  @Override
  public int compareTo(User  otherObj)
  {
    User other = (User)otherObj;
    if (sortMode == SortType.SORT_BY_SCORE)
    {
      return other.highScore - highScore;
    }
    
    return 0;
  }
  
  public static void main(String[] args)
  {
//    System.out.println("User.main() Writing default user list");
//    userList = new ArrayList<User>();
//    userList.add(new User("FEC Lab"));
//    userList.add(new User("Laura Lab"));
//    userList.add(new User("Logan Lab"));
//    saveUsers();
    loadUsers();
  }
}
