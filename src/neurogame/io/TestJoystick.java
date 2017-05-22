package neurogame.io;



import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class TestJoystick
{

  private static Controller joystick = null;
  private static int buttonCount;

  private static void update()
  {
    joystick.poll();
    for (int i = 0; i < buttonCount; i++)
    {
      if (joystick.isButtonPressed(i))
      {
        System.out.println("Pressed Button " + i);
      }
    }
//    double x = joystick.getAxisValue(axisX);
//    double y = joystick.getAxisValue(axisY);

     for (int i = 0; i < joystick.getAxisCount(); i++)
     {
       double x = joystick.getAxisValue(i);
       System.out.format("     %d=%5.2f", i , x);
    
     }
     System.out.println(" ");

//    if (lastX != x || lastY != y)
//    {
//      System.out.println("Joystick: (" + x + ", " + y + ")");
//      lastX = x;
//      lastY = y;
//    }
  }

  public static void main(String[] argv)
  {
    try
    {
      Controllers.create();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    }

    int count = Controllers.getControllerCount();
    System.out.println(count + " Controllers Found");

    for (int i = 0; i < count; i++)
    {
      Controller controller = Controllers.getController(i);
      System.out.println(controller.getName());
      if (controller.getName().contains("Gamepad"))
      //if (controller.getName().contains("Joystick"))
      {
        joystick = controller;
        System.out.println("Gamepad found at index " + i);
        break;
      }
    }

    if (joystick == null)
    {
      System.out.println("Gamepad not found");
      System.exit(0);
    }

    buttonCount = joystick.getButtonCount();

    boolean running = true;
    while (running)
    {

      try
      {
        Thread.sleep(100);
      }
      catch (Exception e)
      {
      }

      update();
    }
  }
}