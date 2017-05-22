package neurogame.game;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import neurogame.game.Game.InputType;

//need to have a checkbox for visual or auditory
public class OddballsOptionsPage extends JDialog
{
  private static final long serialVersionUID = 1L;

  //generalize into Game for both TVis and TAud
  private TVisGame visGame;
  private TAudGame audGame;
  private Game game;
  private OddballsFrame oddballsFrame;
  //Labels:
  private JLabel L1 = new JLabel("Stimulus Duration (ms):");
  private JLabel L2 = new JLabel("Response Timeout (ms):");
  private JLabel L4 = new JLabel("ITI min (ms):");
  private JLabel L5 = new JLabel("ITI max (ms):");
  private JLabel L6 = new JLabel("Stimulli Input Types");

  private JLabel L10 = new JLabel("Number of Trials:");
  private JLabel L11 = new JLabel("% Target:");
  private JLabel L12 = new JLabel("% Novel: ") ;
  private JLabel L13 = new JLabel("Error feedback");
  private JLabel L14 = new JLabel("Feedback Delay:");
  private JLabel L15 = new JLabel("Delay Duration (ms):");
  private JLabel L16 = new JLabel("# Practice Trials:");
  private JLabel L17 = new JLabel("Feedback during practice:");
  //TextFields:
  private JTextField T1, T2, T4, T5, T10, T11, T12, T13,
  T15, T16, T18;
  Dimension textFieldSize = new Dimension(50, 20);
  {
    T1 = new JTextField();
    T1.setPreferredSize(textFieldSize);
    T2 = new JTextField();
    T2.setPreferredSize(textFieldSize);
    T4 = new JTextField();
    T4.setPreferredSize(textFieldSize);
    T5 = new JTextField();
    T5.setPreferredSize(textFieldSize);
    T10 = new JTextField();
    T10.setPreferredSize(textFieldSize);
    T11 = new JTextField();
    T11.setPreferredSize(textFieldSize);
    T12 = new JTextField();
    T12.setPreferredSize(textFieldSize);
    T13 = new JTextField();
    T13.setPreferredSize(textFieldSize);
    T15 = new JTextField();
    T15.setPreferredSize(textFieldSize);
    T16 = new JTextField();
    T16.setPreferredSize(textFieldSize);
    T18 = new JTextField();
    T18.setPreferredSize(new Dimension(400, 20));
  }

  JComboBox<String> DD1; //target input type
  JComboBox<String> DD2; //standard input type
  JComboBox<String> DD3; //novel input type
  JComboBox<String> DD4; //button to press for targets
  JComboBox<String> DD5; //button to press for standards
  JComboBox<String> DD6; //button to press for novels

  //CheckBox for gridBag4():
  JCheckBox CB13 = new JCheckBox();
  JCheckBox CB14 = new JCheckBox();
  JCheckBox CB17 = new JCheckBox();
  //Accept the changes:
  JButton acceptButton = new JButton("Accept Changes");

  //Constraints for all the gridBag() calls
  GridBagConstraints c = new GridBagConstraints();

  //Constructor
  public OddballsOptionsPage(OddballsFrame owner, String title,
      Dialog.ModalityType modalityType,
      TVisGame visGame, TAudGame audGame)
  {
    super(owner, title, modalityType);
    oddballsFrame = owner;
    this.visGame = visGame;
    this.audGame = audGame;
    game = visGame; //by default
    this.setLayout(new GridLayout(8,1)); 
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(2,5,2,5);

    this.add(gridBag1());
    this.add(gridBag2());
    this.add(gridBag3());
    this.add(gridBag4());
    this.add(gridBag5());
    this.add(gridBag6());
    this.add(gridBag7());
    this.add(gridBag8());
    setUpAcceptButton();

    initializeGameFieldValues();
    //this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.requestFocus();
    this.setSize(600, 750);
    this.setResizable(false);
  }

  public void showOptionsPage()
  {
    this.setVisible(true);
  }

  public ButtonGroup visualOrAuditory = new ButtonGroup();
  public JLabel visualOddball = new JLabel("Visual Oddball");
  public JLabel auditoryOddball = new JLabel("Auditory Oddball");
  public JRadioButton isVisualOddball = new JRadioButton();
  public JRadioButton isAuditoryOddball = new JRadioButton();

  private JPanel gridBag1()
  {
    JPanel wholePanel = new JPanel();
    wholePanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    //y=0
    c.gridx = 0;
    c.gridy = 0;
    panel.add(L1, c);
    c.gridx = 1;
    panel.add(T1, c);
    c.gridx = 2;
    panel.add(L2, c);
    c.gridx = 3;
    panel.add(T2, c);
    wholePanel.add(panel, gbc);

    visualOrAuditory.add(isVisualOddball);
    visualOrAuditory.add(isAuditoryOddball);
    gbc.gridx = 0;
    gbc.gridy = 0;
    JPanel panel2 = new JPanel();
    panel2.setLayout(new GridBagLayout());
    //y=1
    c.gridx = 0;
    c.gridy = 1;
    panel2.add(isVisualOddball, c);
    c.gridx = 1;
    panel2.add(visualOddball, c);
    c.gridx = 2;
    panel2.add(isAuditoryOddball, c);
    c.gridx = 3;
    panel2.add(auditoryOddball, c);

    c.gridy = 1;
    c.gridx = 0;
    panel.add(L4, c);
    c.gridx = 1;
    panel.add(T4, c);
    c.gridx = 2;
    panel.add(L5, c);
    c.gridx = 3;
    panel.add(T5, c);

    wholePanel.add(panel2, gbc);

    //    panel.setBorder(BorderFactory.createLineBorder(Color.black));
    wholePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    return wholePanel;
  }

  private JPanel gridBag2()
  {
    String[] inputTypes = {"Required", "Ignore", "NULL"};
    String[] buttonChoices = {"D", "K", "SPACEBAR", "(Num)1", "(Num)2", "(Num)3"};

    JPanel wholePanel = new JPanel();
    wholePanel.setLayout(new GridBagLayout());
    JPanel inputTypePanel = new JPanel();
    JPanel buttonChoicePanel = new JPanel();

    DD1 = new JComboBox<String>(inputTypes);
    DD2 = new JComboBox<String>(inputTypes);
    DD3 = new JComboBox<String>(inputTypes);
    DD4 = new JComboBox<String>(buttonChoices);
    DD5 = new JComboBox<String>(buttonChoices);
    DD6 = new JComboBox<String>(buttonChoices);

    inputTypePanel.setLayout(new GridBagLayout());

    c.anchor = GridBagConstraints.CENTER;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 6;
    inputTypePanel.add(L6,c);
    c.gridwidth = 1;

    c.gridx = 0;
    c.gridy = 1;
    inputTypePanel.add(new JLabel("Target Type"),c);
    buttonChoicePanel.add(new JLabel("Target Key"),c);
    c.gridx = 1;
    c.gridy = 1;
    inputTypePanel.add(DD1,c);
    buttonChoicePanel.add(DD4,c);
    c.gridx = 2;
    c.gridy = 1;
    inputTypePanel.add(new JLabel("Standard Type"), c);
    buttonChoicePanel.add(new JLabel("Standard Key"), c);
    c.gridx = 3;
    c.gridy = 1;
    inputTypePanel.add(DD2, c);
    buttonChoicePanel.add(DD5,c);
    c.gridx = 4;
    c.gridy = 1;
    inputTypePanel.add(new JLabel("Novel Type"), c);
    buttonChoicePanel.add(new JLabel("Novel Key"), c);
    c.gridx = 5;
    c.gridy = 1;
    inputTypePanel.add(DD3, c);
    buttonChoicePanel.add(DD6,c);
    c.gridx = 2;
    c.gridy = 2;
    //    panel.add(passive, c);
    c.gridx = 0;
    c.gridy = 0;
    wholePanel.add(inputTypePanel, c);
    c.gridy = 1;
    wholePanel.add(buttonChoicePanel, c);
    wholePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    return wholePanel;
  }

  private JPanel gridBag3()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());

    c.gridx = 0;
    c.gridy = 0;
    panel.add(L10, c);
    c.gridx = 1;
    panel.add(T10, c);
    c.gridx = 2;
    panel.add(L16, c);

    c.gridx = 3;
    panel.add(T16, c);

    c.gridx = 0;
    c.gridy = 1 ;
    panel.add(L11, c);
    c.gridx = 1;
    panel.add(T11, c);

    c.gridx = 2;
    panel.add(L12, c);
    c.gridx = 3;
    panel.add(T12, c);

    numBlockField.setPreferredSize(textFieldSize);
    numBlockField.setText(game.numBlocks+"");
    c.gridx = 0;
    c.gridy = 2;
    panel.add(numBlock, c);
    c.gridx = 1;
    panel.add(numBlockField, c);

    panel.setBorder(BorderFactory.createLineBorder(Color.black));
    return panel;
  }

  private JPanel gridBag4()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());

    c.gridx = 0;
    c.gridy = 0;
    panel.add(L13, c);
    c.gridx = 1;
    panel.add(CB13, c);
    c.gridx = 2;
    panel.add(L17, c);
    c.gridx = 3;
    panel.add(CB17, c);
    c.gridx = 0;
    c.gridy = 1;

    panel.add(L14, c);
    c.gridx = 1;
    panel.add(CB14, c);
    c.gridx = 2;
    panel.add(L15, c);
    c.gridx = 3;
    panel.add(T15, c);

    panel.setBorder(BorderFactory.createLineBorder(Color.black));
    return panel;
  }

  //TODO: make JLabels local variable
  JLabel numBlock = new JLabel("Number of Blocks:");
  JTextField numBlockField = new JTextField();

  JLabel instructionFile = new JLabel("Instruction File:");
  JButton openInstructionButton = new JButton("Find");
  JTextField instructionFileName = new JTextField();
  JFileChooser instructionFileChooser = new JFileChooser();

  JLabel saveProtocol = new JLabel("Save Protocol in:");
  JButton findLocation = new JButton("Find Where");
  JTextField protocolSaveLoc = new JTextField();
  JLabel protocolFile = new JLabel("Protocol Name:");
  JTextField protocolFileName = new JTextField();
  JFileChooser directoryFileChooser = new JFileChooser();
  JCheckBox useExistingProtocol = new JCheckBox();

  private JPanel gridBag5()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    JPanel panel1 = new JPanel(new GridBagLayout());

    instructionFileName.setPreferredSize(new Dimension(300, 20));
    openInstructionButton.setPreferredSize(new Dimension(60, 20));
    instructionFileChooser.setCurrentDirectory(new File("C:/"));
    instructionFileChooser.setDialogTitle("Find Instruction File");

    openInstructionButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (instructionFileChooser.showOpenDialog(openInstructionButton) == 
            JFileChooser.APPROVE_OPTION )
        {
          instructionFileName.setText(instructionFileChooser.getSelectedFile().getAbsolutePath());
        }
      }
    });

    protocolSaveLoc.setPreferredSize(new Dimension(300, 20));
    findLocation.setPreferredSize(new Dimension(100, 20));
    directoryFileChooser.setCurrentDirectory(new File("C:/"));
    directoryFileChooser.setDialogTitle("Save Protocol in");
    directoryFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    findLocation.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (directoryFileChooser.showOpenDialog(findLocation) ==
            JFileChooser.APPROVE_OPTION)
        {
          protocolSaveLoc.setText(directoryFileChooser.getSelectedFile().toString());
        }
      }
    });

    protocolFileName.setPreferredSize(new Dimension(300, 20));

    c.gridx = 0;
    c.gridy = 0;
    panel1.add(instructionFile, c);
    c.gridx = 1;
    panel1.add(instructionFileName, c);
    c.gridx = 2;
    panel1.add(openInstructionButton, c);

    c.gridx = 0;
    c.gridy = 1;
    panel1.add(saveProtocol, c);
    c.gridx = 1;
    panel1.add(protocolSaveLoc, c);
    c.gridx = 2;
    panel1.add(findLocation, c);

    c.gridy = 2;
    c.gridx = 0;
    panel1.add(protocolFile, c);
    c.gridx = 1;
    panel1.add(protocolFileName, c);

    c.gridx = 0;
    c.gridy = 0;
    panel.add(panel1, c);
    //    c.gridy = 1;
    //    panel.add(panel2, c);
    panel.setBorder(BorderFactory.createLineBorder(Color.black));
    return panel;
  }

  JTextField existingProtocolName = new JTextField();
  private JPanel gridBag6()
  {
    JButton findExistingProt = new JButton("Find");

    JPanel panel = new JPanel(new GridBagLayout());
    JPanel panel1 = new JPanel(new GridBagLayout());
    JPanel panel2 = new JPanel(new GridBagLayout());

    JLabel l1 = new JLabel("OR Use Exisiting Protocol");
    JLabel l2 = new JLabel("Existing Protocol:");
    c.gridx = 0;
    c.gridy = 0;
    panel1.add(l1, c);
    c.gridx = 1;
    panel1.add(useExistingProtocol, c);

    useExistingProtocol.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (useExistingProtocol.isSelected())
        {
          existingProtocolName.setEnabled(true);
          findExistingProt.setEnabled(true);
        }
        else if (!useExistingProtocol.isSelected())
        {
          existingProtocolName.setEnabled(false);
          findExistingProt.setEnabled(false);
        }
      }
    });

    if (!useExistingProtocol.isSelected())
    {
      existingProtocolName.setEnabled(false);
      findExistingProt.setEnabled(false);
    }

    JFileChooser exProtChooser = new JFileChooser();
    exProtChooser.setCurrentDirectory(new File("C:/"));

    findExistingProt.setPreferredSize(new Dimension(60, 20));

    existingProtocolName.setPreferredSize(new Dimension(300, 20));

    findExistingProt.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (exProtChooser.showOpenDialog(findExistingProt) == 
            JFileChooser.APPROVE_OPTION)
        {
          existingProtocolName.setText(exProtChooser.getSelectedFile().getAbsolutePath());
        }
      }
    });

    c.gridx = 0;
    c.gridy = 1;
    panel2.add(l2,c);
    c.gridx = 1;
    panel2.add(existingProtocolName, c); 
    c.gridx = 2;
    panel2.add(findExistingProt, c);

    c.gridx = 0;
    c.gridy = 0;
    panel.add(panel1, c);
    c.gridy = 1;
    panel.add(panel2, c);
    panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    return panel;
  }

  JTextField logFileDir = new JTextField();
  JTextField logFileName = new JTextField();
  private JPanel gridBag7()
  {
    JLabel l = new JLabel("Save Log File in: ");
    JLabel l2 = new JLabel("Log File Name:");

    JFileChooser jfc = new JFileChooser();
    jfc.setCurrentDirectory(new File("C:/"));
    jfc.setDialogTitle("Save Log File in");
    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //TODO: create a field for Dimension(300, 20), creating too many Dimension "objects"
    logFileDir.setPreferredSize(new Dimension(300, 20));
    logFileName.setPreferredSize(new Dimension(300, 20));

    JButton find = new JButton("Find Where");
    find.setPreferredSize(new Dimension(100, 20));
    find.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (jfc.showOpenDialog(find) == JFileChooser.APPROVE_OPTION)
        {
          logFileDir.setText(jfc.getSelectedFile().getAbsolutePath());
        }
      }
    });

    JPanel panel = new JPanel(new GridBagLayout());
    c.gridx = 0;
    c.gridy = 0;
    panel.add(l,c);
    c.gridx = 1;
    panel.add(logFileDir, c);
    c.gridx = 2;
    panel.add(find, c);

    c.gridy = 1;
    c.gridx = 0;
    panel.add(l2, c);
    c.gridx = 1;
    panel.add(logFileName, c);

    return panel;
  }

  private JPanel gridBag8()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());    
    c.gridx = 0;
    c.gridy = 0;
    panel.add(acceptButton);

    return panel;
  }



  private void setUpAcceptButton()
  {
    acceptButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        accept();
      }
    });
  }

  private void accept()
  {
    //no case for when isVisualOddball is selected since that is the default
    if (useExistingProtocol.isSelected())
    {
      System.out.println("Using an existing protocol");
      try
      {
        BufferedReader bR = new BufferedReader(new FileReader(existingProtocolName.getText()));
        //there are 21 fields to fill in
        String entry = bR.readLine();
        if (entry.equals("v"))
        {
          System.out.println("Visual Type");
          isVisualOddball.setSelected(true);
          isAuditoryOddball.setSelected(false);
          oddballsFrame.gameType = 0;
        }
        else if (entry.equals("a"))
        {
          System.out.println("Auditory Type");
          isAuditoryOddball.setSelected(true);
          isVisualOddball.setSelected(false);
          oddballsFrame.gameType = 1;
        }
        entry = bR.readLine();
        T1.setText(entry);
        entry = bR.readLine();
        T2.setText(entry);
        entry = bR.readLine();
        T4.setText(entry);
        entry = bR.readLine();
        T5.getText();
        entry = bR.readLine();
        DD1.setSelectedItem(entry);
        entry = bR.readLine();
        DD2.setSelectedItem(entry);
        entry = bR.readLine();
        DD3.setSelectedItem(entry);
        entry = bR.readLine();
        DD4.setSelectedItem(entry);
        entry = bR.readLine();
        DD5.setSelectedItem(entry);
        entry = bR.readLine();
        DD6.setSelectedItem(entry);
        entry = bR.readLine();
        T10.setText(entry);
        entry = bR.readLine();
        T16.setText(entry);
        entry = bR.readLine();
        T11.setText(entry);
        entry = bR.readLine();
        T12.setText(entry);
        entry = bR.readLine();
        numBlockField.setText(entry);

        entry = bR.readLine();
        if (entry.equals("WEF")) CB13.setSelected(true);
        else if (entry.equals("NEF")) CB13.setSelected(false);

        entry = bR.readLine();
        if (entry.equals("WPEF")) CB17.setSelected(true);
        else if (entry.equals("NPEF")) CB17.setSelected(false);

        entry = bR.readLine();
        if (entry.equals("HFD")) CB14.setSelected(true);
        else if (entry.equals("NFD")) CB14.setSelected(false);

        entry = bR.readLine();
        T15.setText(entry);
        bR.close();
      }
      catch (Exception e) 
      {
        System.out.println("protocol file not found");
      }
    }
    errorCheck();
    //fill up the game fields
    if (!errorOnFields)
    {
      if (isAuditoryOddball.isSelected())
      {
        oddballsFrame.gameType = 1;
        game = audGame;
      }
      else if (isVisualOddball.isSelected())
      {
        oddballsFrame.gameType = 0;
        game = visGame;
      }
      game.stimulusDuration = Integer.parseInt(T1.getText());
      game.responseTimeOut = Integer.parseInt(T2.getText());
      game.intervalMin = Integer.parseInt(T4.getText());
      game.intervalMax = Integer.parseInt(T5.getText());

      game.gameTrials = Integer.parseInt(T10.getText());
      game.percentTarget = Double.parseDouble(T11.getText());
      game.percentNovel = Double.parseDouble(T12.getText());
      game.pracTrials = Integer.parseInt(T16.getText());
      game.delayDuration = Integer.parseInt(T15.getText());

      game.hasErrorFeedback = CB13.isSelected();
      if (!CB14.isSelected())
      {
        game.hasFeedbackDelay = false;
      }
      game.hasPracticeErrorFeedback = CB17.isSelected();
      game.numBlocks = Integer.parseInt(numBlockField.getText());
      //DD1, DD2, DD3 are the input type drop down menus (JComboBoxes), target, standard, novel respectively  
      setGameInputTypes();
      setStimulusKeys();
      try
      {
        //no getClass since it must be in the user's machine
        game.instructionImage = ImageIO.read(new File(instructionFileName.getText()));
      }
      catch(Exception e) {}
      writeToProtocolFile();
      game.logFile = logFileDir.getText() +"/"+ logFileName.getText()+".txt"; 
      game.initialize();
      closeOptionsPage();
    }
    errorOnFields = false;
  }

  private boolean errorOnFields = false;
  private void errorCheck()
  {
    //for the trials
    int nTrials = Integer.parseInt(T10.getText());
    double tProb = Double.parseDouble(T11.getText()) / 100;
    double nProb = Double.parseDouble(T12.getText()) / 100;
    int pracTrials = Integer.parseInt(T16.getText());
    
    
    //not a field
    double minN = (Math.floor((tProb*nTrials))*3) + (Math.floor(nProb*nTrials)*3);
    double minNPrac = (Math.floor((tProb*pracTrials))*3) + (Math.floor(nProb*pracTrials)*3);
    
    //for the stim duration and ITI
    int stimulusDuration = Integer.parseInt(T1.getText());
    int responseTimeOut = Integer.parseInt(T2.getText());
    int intervalMin = Integer.parseInt(T4.getText());
    int intervalMax = Integer.parseInt(T5.getText());
    int delayDur = Integer.parseInt(T15.getText());
    int numBlocks = Integer.parseInt(numBlockField.getText());
    
    if (nTrials < 0) 
    {
      showError("Number of Trials must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (tProb*100 < 0) 
    {
      showError("% Target must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (nProb*100 < 0) 
    {
      showError("% Novel must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (stimulusDuration < 0)
    {
      showError("Stimulus Duration(ms) must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (responseTimeOut < 0)
    {
      showError("Response Timeout(ms) must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (intervalMin < 0)
    {
      showError("ITI min(ms) must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (intervalMax < 0)
    {
      showError("ITI max(ms) must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (delayDur < 0)
    {
      showError("Delay Duration(ms) must be larger than 0");
      errorOnFields = true;
      return;
    }
    if (numBlocks < 1)
    {
      showError("Number of Blocks must be larger than 1");
      errorOnFields = true;
      return;
    }
    
    if (minN >= nTrials)
    {
      showError("Number of Trials must be larger than "+minN+" to accomodate minimum local probability of standards");
      errorOnFields = true;
      return;
    }
    if (minNPrac >= pracTrials && pracTrials > 0)
    {
      showError("Practice Trials must be larger than "+minN+" to accomodate minimum local probability of standards");
      errorOnFields = true;
      return;
    }
 
    if (stimulusDuration > responseTimeOut)
    {
      showError("Stimulus Duration(ms) must be less than or equal to Response Timeout(ms)");
      errorOnFields = true;
      return;
    }
    if (intervalMin > intervalMax)
    {
      showError("ITI min(ms) must be less than or equal to ITI max(ms)");
      errorOnFields = true;
      return;
    }   
  }
  
  private void showError(String errMessage)
  {
    JOptionPane.showMessageDialog(this, 
        errMessage, 
        "Error", JOptionPane.ERROR_MESSAGE);
  }

  //write all the textfield values to a filewriter
  private void writeToProtocolFile()
  {
    String filePath = protocolSaveLoc.getText() + "/" + protocolFileName.getText();     
    try
    {
      PrintWriter file = new PrintWriter(filePath);
      if (isVisualOddball.isSelected() && 
          !isAuditoryOddball.isSelected())
      {
        file.println("v"); //visual = v
      }
      else if (isAuditoryOddball.isSelected() && !isVisualOddball.isSelected())
      {
        file.println("a"); //auditory = a
      }
      file.println(T1.getText()); //Stimulus Duration (ms)
      file.println(T2.getText()); //Response Timeout (ms)
      file.println(T4.getText()); //Intertrial Interval from (ms) 
      file.println(T5.getText()); //                    to (ms)

      //Stimulus Input Types:
      file.println((String)DD1.getSelectedItem()); //Target Type
      file.println((String)DD2.getSelectedItem()); //Standard Type
      file.println((String)DD3.getSelectedItem()); //Novel Type

      //Keys to Press
      file.println((String)DD4.getSelectedItem()); //Target Key
      file.println((String)DD5.getSelectedItem()); //Standard Key
      file.println((String)DD6.getSelectedItem()); //Novel Key

      file.println(T10.getText()); //Number of Trials
      file.println(T16.getText()); //# Practice Trials
      file.println(T11.getText()); //% Target
      file.println(T12.getText()); //% Novel
      file.println(numBlockField.getText()); //Number of Blocks

      if (CB13.isSelected())
      {
        //with error feedback
        file.println("WEF"); //Error feedback checkbox
      }
      else if (!CB13.isSelected())
      {
        //no error feedback
        file.println("NEF");
      }

      //Feedback during practice
      if (CB17.isSelected())
      {
        //with practice error feedback
        file.println("WPEF"); //Feedback during practice
      }
      else if (!CB17.isSelected())
      {
        //no practice error feedback
        file.println("NPEF");
      }

      //Feedback Delay
      if (CB14.isSelected())
      {
        //has feedback delay
        file.println("HFD"); //Feedback Delay
      }
      else if (!CB14.isSelected())
      {
        //no feedback delay
        file.println("NFD"); 
      }

      file.println(T15.getText()); //delay duration
      file.println(instructionFileName.getText()); //instruction file
      //close the file
      file.close();
    }
    catch (FileNotFoundException e)
    {
      System.out.println("No such file");
    }
  }

  //sets the input types of each stimuli types depending on the values of the combo boxes
  private void setGameInputTypes()
  {
    String targetInputType = (String) DD1.getSelectedItem();
    String standardInputType = (String) DD2.getSelectedItem();
    String novelInputType = (String) DD3.getSelectedItem();

    //for targets
    if (targetInputType == "NULL")
    {
      game.targetInputType = InputType.NULL;
    }
    else if (targetInputType == "Required")
    {
      game.targetInputType = InputType.REQUIRED;
    }
    else if (targetInputType == "Ignore")
    {
      game.targetInputType = InputType.IGNORE;
    }
    //for standards
    if (standardInputType == "NULL")
    {
      game.standardInputType = InputType.NULL;
    }
    else if (standardInputType == "Required")
    {
      game.standardInputType = InputType.REQUIRED;
    }
    else if (standardInputType == "Ignore")
    {
      game.standardInputType = InputType.IGNORE;
    }
    //for novels
    if (novelInputType == "NULL")
    {
      game.novelInputType = InputType.NULL;
    }
    else if (novelInputType == "Required")
    {
      game.novelInputType = InputType.REQUIRED;
    }
    else if (novelInputType == "Ignore")
    {
      game.novelInputType = InputType.IGNORE;
    }
  }

  //Set the keyboard keys to press when a certain kind of stimuli shows up
  private void setStimulusKeys()
  {
    //use DD4=Target, DD5=Standard, DD6=Novel
    String targetKey = (String) DD4.getSelectedItem();
    String standardKey = (String) DD5.getSelectedItem();
    String novelKey = (String) DD6.getSelectedItem();

    //for targets
    if (targetKey == "D")
    {
      game.targetKey = KeyEvent.VK_D;
    }
    else if (targetKey == "K")
    {
      game.targetKey = KeyEvent.VK_K;
    }
    else if (targetKey == "SPACEBAR")
    {
      game.targetKey = KeyEvent.VK_SPACE;
    }
    else if (targetKey == "(Num)1")
    {
      game.targetKey = KeyEvent.VK_NUMPAD1;
    }
    else if (targetKey == "(Num)2")
    {
      game.targetKey = KeyEvent.VK_NUMPAD2;
    }
    else if (targetKey == "(Num)3")
    {
      game.targetKey = KeyEvent.VK_NUMPAD3;
    }
    //for standards
    if (standardKey == "D")
    {
      game.standardKey = KeyEvent.VK_D;
    }
    else if (standardKey == "K")
    {
      game.standardKey = KeyEvent.VK_K;
    }
    else if (standardKey == "SPACEBAR")
    {
      game.standardKey = KeyEvent.VK_SPACE;
    }
    else if (standardKey == "(Num)1")
    {
      game.standardKey = KeyEvent.VK_NUMPAD1;
    }
    else if (standardKey == "(Num)2")
    {
      game.standardKey = KeyEvent.VK_NUMPAD2;
    }
    else if (standardKey == "(Num)3")
    {
      game.standardKey = KeyEvent.VK_NUMPAD3;
    }
    //for novels
    if (novelKey == "D")
    {
      game.novelKey = KeyEvent.VK_D;
    }
    else if (novelKey == "K")
    {
      game.novelKey = KeyEvent.VK_K;
    }
    else if (novelKey == "SPACEBAR")
    {
      game.novelKey = KeyEvent.VK_SPACE;
    }
    else if (novelKey == "(Num)1")
    {
      game.novelKey = KeyEvent.VK_NUMPAD1;
    }
    else if (novelKey == "(Num)2")
    {
      game.novelKey = KeyEvent.VK_NUMPAD2;
    }
    else if (novelKey == "(Num)3")
    {
      game.novelKey = KeyEvent.VK_NUMPAD3;
    }
  }

  //Where we initialize the values on the text fields and radio buttons of our options page
  public void initializeGameFieldValues()
  {
    if (oddballsFrame.gameType == 0)
    {
      System.out.println("Radio button should be in VisualOddball");
      isVisualOddball.setSelected(true);
      isAuditoryOddball.setSelected(false);
    }
    else if (oddballsFrame.gameType == 1)
    {
      System.out.println("Radio button should be in AuditoryOddball");
      isAuditoryOddball.setSelected(true);
      isVisualOddball.setSelected(false);
    }
    //concatenate "" to make the numerical values as strings
    T1.setText(game.stimulusDuration + "");
    T2.setText(game.responseTimeOut + "");
    T4.setText(game.intervalMin + "");
    T5.setText(game.intervalMax + "");

    T10.setText(game.gameTrials + "");
    T11.setText(game.percentTarget+"");
    T12.setText(game.percentNovel+"");
    //so that the text field for feed back delay turns grey when the checkbox is not checked
    CB13.setSelected(game.hasErrorFeedback);
    CB13.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        CB14.setEnabled(CB13.isSelected());
        CB14.setSelected(CB13.isSelected());
        T15.setEnabled(CB13.isSelected() && CB14.isSelected());
      }
    });
    CB14.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        T15.setEnabled(CB14.isSelected());
      }
    }
        );
    CB14.setEnabled(CB13.isSelected());
    T15.setText(game.delayDuration+"");
    T15.setEnabled(CB14.isSelected() && CB13.isSelected());
    T16.setText(game.pracTrials +"");
    CB17.setSelected(game.hasPracticeErrorFeedback);  
  }

  private void closeOptionsPage()
  {
    this.dispose();
    System.out.println("should be closed now");
  }
}
