package neurogame.game;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TrialSequenceGenerator
{
  //set the seed in option page?
  public static Random rand = new Random(); 
  
  //*******************************************************
  // Parameters:
  //  tProb = (number of target trials)/(total number
  //                of trials)
  //  nProb = (number of novel trials)/(total number of
  //               trials)
  //  nTrials = total number of trials for the test
  // Description:
  //  0 = standard (sound clip or picture)
  //  1 = target (sound clip or picture)
  //  2 = novel (sound clip or picture)
  //  
  //  Based on the pre-made sequence made by this method,
  //  we generate certain types of sound at every trial.
  //*******************************************************
  public static int[] generateSequence(double tProb, double nProb, 
      int nTrials, boolean inBetweens)
  {
    int seed = (int)(Math.random()*100000);
    rand.setSeed(seed);
    ArrayList<Integer> tempArrayList = new ArrayList<>(nTrials);
    int[] seq = new int[1]; 
    if(inBetweens)
    {
//      int extra = 1; //we want to have inbetweens for when nTrials is 1 as well
//      if (nTrials > 1)
//      {
//        extra = nTrials-1;
//      }
      seq = new int[nTrials*2]; //adding nTrials-1 size to be able to put plus signs
      System.out.println("seq.length="+seq.length);
    }
    else if(!inBetweens)
    {
      seq = new int[nTrials];
    }
    
    int numNovels = 0;
    int numTargets = 0;
    JFrame errFrame = new JFrame();
    
    double minN = (Math.floor((tProb*nTrials))*3) + (Math.floor(nProb*nTrials)*3);
    System.out.println("minN="+minN);
    //sanity check:
    if(minN >= nTrials)
    {
      JOptionPane.showMessageDialog(
          errFrame,
          "N must be larger than "+minN+" to accomodate minimum local probability of standards",
          "Invalid Number of Trials",
          JOptionPane.ERROR_MESSAGE
          );
      errFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      return seq;
    }

    for(int i=0; i < nTrials; i++)
    {
      double currentTProb = ((double)numTargets)/nTrials;
      double currentNProb = ((double)numNovels)/nTrials;
      int trialType = (int)(Math.random()*2)+1;
      if (currentNProb >= nProb &&
          currentTProb >= tProb)
      {
        System.out.println("currentTProb: " +currentTProb +
            " currentNProb: " + currentNProb);
        break;
      }
      else if(currentTProb >= tProb)
      {
        trialType = 2; //novel = 2;
        numNovels++;
      }
      else if(currentNProb >= nProb)
      {
        trialType = 1; //target = 1;
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
      //0 is standard
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

    //use rand to find an index and add the extra in-between zeros
    while(tempArrayList.size() <= nTrials)
    {
      int index = rand.nextInt(tempArrayList.size()); //max is nTrials - 1
      tempArrayList.add(index, 0);
    }
    
    //insert the in-between-trial plus signs! i is the actual ArrayList index
    if (!inBetweens)
    {
      tempArrayList.remove(tempArrayList.size()-1); //remove the last one
      System.out.print("tempArrayList contents before inbetween 3: ");
      for (int i=0; i<tempArrayList.size(); i++)
      {
        System.out.print(tempArrayList.get(i)+" ");
      }
      System.out.println();
     
    }
    if (inBetweens)
    {
      System.out.println("seq.length="+seq.length);
      for(int i=1 ; i < seq.length-1; i++)
      {
        //insert every odd index (in between)
        if(i%2 == 1)
        {
          tempArrayList.add(i, 3);
        }
      }
      tempArrayList.remove(tempArrayList.size()-1);
      tempArrayList.add(3); //add the last 3 to the end
      System.out.print("tempArrayList contents: ");
      for (int i=0; i<tempArrayList.size(); i++)
      {
        System.out.print(tempArrayList.get(i)+" ");
      }
      System.out.println();
    }
    
    //turn the ArrayList to an array
    System.out.println("Temporary ArrayList size: " + tempArrayList.size());
    for(int i=0 ; i < tempArrayList.size() ; i++)
    {
      seq[i] = tempArrayList.get(i);
    }
    System.out.println("Number of Targets: " + numTargets 
                    + " Number of Novels: " +numNovels);
    
    for(Integer i : tempArrayList)
    {
      System.out.print(i + " ");
    }
    System.out.println();
    return seq;
  }
  
  public static void main(String[] args) 
  {
    //max number of standard and novel is nTrial/3
//    generate(.04, .15, 100);
    int[] nums = generateSequence(0.1, 0.1, 10, false);
    //print the output array
//    System.out.println("Printing the array:");
//    for(int i : nums)
//    {
//      System.out.print(i + " ");
//    }
//    System.exit(0);
  }
}
