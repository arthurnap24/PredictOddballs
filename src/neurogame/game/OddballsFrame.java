package neurogame.game;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OddballsFrame extends JFrame
{

  private static final long serialVersionUID = 1L;

  public CardLayout cl;
  public JPanel panelCont;
  private JPanel buttonPanel;
  private JPanel mainMenuPanel;

  public int gameType = 0; //defaults to visual oddball
  private TVisGame visGame; //visual game
  private TAudGame audGame; //auditory game

  public JButton visGameButton;
  public JButton audGameButton;
  public JButton gameButton = new JButton("Start Game");
  private JButton options;

  private ImageIcon PREDICT_LOGO;
  private JLabel testLabel;

  public OddballsFrame(int width, int height) 
  {
    PREDICT_LOGO = new ImageIcon(this.getClass().getResource("/images/PredictLogo.png"));
    this.setLayout(new BorderLayout());
    testLabel = new JLabel("TM", PREDICT_LOGO, JLabel.CENTER);

    //Create the CardLayout
    cl = new CardLayout(); 
    //Create the Oddball tasks
    visGame = new TVisGame(); 
    audGame = new TAudGame(); 
    //Create the JPanel that will have the CardLayout
    panelCont = new JPanel();
    panelCont.setLayout(cl);
    //Create the JPanel that will contain the button panel
    mainMenuPanel = new JPanel();
    mainMenuPanel.setLayout(new BorderLayout());
    mainMenuPanel.setBackground(Color.BLACK);
    mainMenuPanel.add(testLabel, BorderLayout.NORTH);
    //Create the JPanel that will contain the buttons
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
    buttonPanel.setOpaque(false);

    //The first page is the main menu:
    panelCont.add(mainMenuPanel, "mainMenu");
    cl.show(panelCont, "mainMenu");
    
    panelCont.add(audGame, "AuditoryGame");
    panelCont.add(visGame, "VisualGame");
     
    setUpButtons();
    //Add the button panel to the main menu
    mainMenuPanel.add(buttonPanel, BorderLayout.CENTER);

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //TODO: to make it full screen
//    this.setPreferredSize(new Dimension(width, height));
    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    this.setUndecorated(true);
    this.add(panelCont, BorderLayout.CENTER);
    this.pack();
  }

  public void start() 
  {
    this.setVisible(true);
  }

  //Add the buttons to the button panel and centers them as well.
  //It also sets up the ActionListeners for each button.
  private void setUpButtons()
  {
    visGameButton = new JButton("Visual Oddball");
    visGameButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) 
      {
        cl.show(panelCont, "VisualGame");
        visGame.requestFocus();
        visGame.showInstructionFile();
      }
    });
    
    audGameButton = new JButton();
    audGameButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0)
      {
        System.out.println("show the game now");
        cl.show(panelCont, "AuditoryGame");
        audGame.requestFocus();
//        audGame.start();
        audGame.showInstructionFile();
      }
    });
    
    options = new JButton("Options");
    OddballsOptionsPage popUpDialog = new OddballsOptionsPage(this, "Options", 
                                              Dialog.ModalityType.APPLICATION_MODAL, 
                                              visGame, audGame);
    options.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {        
        popUpDialog.showOptionsPage();
        popUpDialog.initializeGameFieldValues();
      }
    });
    //does the click on anyone of the invisible game buttons
    gameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    gameButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        //defaults to clicking the visGameButton
        if (gameType == 0)
        {
          visGameButton.doClick();
        }
        else if (gameType == 1)
        {
          audGameButton.doClick();
        }
      }
    });

    buttonPanel.add(gameButton);
    buttonPanel.add(Box.createRigidArea(new Dimension(10,10)));
    
    buttonPanel.add(Box.createRigidArea(new Dimension(10,10)));

    options.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttonPanel.add(options);
  }

}
