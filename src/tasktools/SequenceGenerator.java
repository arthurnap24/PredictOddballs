package tasktools;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * This class's generateSequence() method will be caleld
 * by the OddballsFrame class, and the output of that function
 * will be stored in an int[] and passed into TAudGame TVisGame
 * classes. They will then be the whole set of trials for the game.
 */
public class SequenceGenerator
{

  //Works properly
  public static int[] generateSequence(double tProb, double nProb, int nTrials)
  {
    ArrayList<Integer> tempArrayList = new ArrayList<>();
    int[] seq = new int[nTrials];

    int numNovels = 0;
    int numTargets = 0;
    JFrame errFrame = new JFrame();
    errFrame.setPreferredSize(new Dimension(300,300));
    double minN = (Math.floor((tProb*nTrials))*3) + (Math.floor(nProb*nTrials)*3);
    //sanity check:
    if(minN >= nTrials)
    {
      JOptionPane.showMessageDialog(
          errFrame,
          "N must be larger than "+minN+" to accomodate minimum local probability of standards",
          "Invalid Number of Trials",
          JOptionPane.ERROR_MESSAGE
          );
      System.exit(1);
      return seq;
    }

    for(int i=0; i < nTrials; i++)
    {
      double currentTProb = ((double)numTargets)/nTrials;
      double currentNProb = ((double)numNovels)/nTrials;
      int trialType = (int)(Math.random()*2)+1;
      if(currentNProb >= nProb &&
          currentTProb >= tProb)
      {
        break;
      }
      else if(currentTProb >= tProb)
      {
        trialType = 2;
        numNovels++;
      }
      else if(currentNProb >= nProb)
      {
        trialType = 1;
        numTargets++;
      }
      else if(trialType == 1 && currentTProb < tProb)
      {
        numTargets++;
      }
      else if(trialType ==  2 && currentNProb < nProb)
      {
        numNovels++;
      }
      tempArrayList.add(trialType);

      if(tempArrayList.size() <= nTrials-2)
      {
        tempArrayList.add(0);
        tempArrayList.add(0);
        i=i+2;
      }
      else if(tempArrayList.size() <= nTrials-1)
      {
        tempArrayList.add(0);
        i++;
      }
    }

    //add the extra in between zeros, insert at i=3 right away
    for(int i=3; i < nTrials; i+=3)
    {
      if(tempArrayList.size() >= nTrials)
      {
        break;
      }
      //maximum of five zeros per space, two already there
      int numZerosPadding = (int)(Math.random()*3)+1;
      int j = 0;
      while(tempArrayList.size() < nTrials && 
          j < numZerosPadding)
      {
        tempArrayList.add(i, 0);
        i++;
        j++;
      }
    }
    //turn the ArrayList to an array
    for(int i=0 ; i < tempArrayList.size() ; i++)
    {
      seq[i] = tempArrayList.get(i);
    }
    return seq;
  }

  
  public static void main(String[] args)
  {
    //invalid n in this case:
    //generateSequence(.20, .20, 30);
    
    //working n in this case:
    generateSequence(.15, .15, 40);
  }
}
