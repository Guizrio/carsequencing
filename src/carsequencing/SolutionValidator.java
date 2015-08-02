/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.util.ArrayList;

/**
 *Class used to verify the validity of a solution.
 * <p>
 * More exactly, it verify that the given sequenced cars of the solution correspond
 * to the objective of this solution.
 * <p>
 * @author Guillaume
 */
public class SolutionValidator {
    
    /*Note : The Next two variables are not thread-safe. In mono-thread context, they represent
               if a part of the last solution given to validate() method bellow was correct or not.
             If no validate() call was done, they are enabled to false.*/
    private static boolean goodObjValue = false;
    private static boolean goodNbPaintViol = false;
    
    //Serves only for testing
    private static long highPrioViol = Long.MIN_VALUE;
    private static long lowPrioViol = Long.MIN_VALUE;
    private static long paintViol = Long.MIN_VALUE;
    
    public static boolean validate(Solution solToTest, DataProblem dat){
        
        long nbTotalViol[] = new long[3]; //Order : highprio, lowprio, paint batches
        int[] multObjective = dat.getClassObjective().getMultForCompute();
        
        ArrayList<Car> allCars = solToTest.getCars();
        
        ArrayList<RatioConstraint> highPrioConst = dat.getHighConst();
        ArrayList<RatioConstraint> lowPrioConst = dat.getLowConst();
        
        //Count number of HighPriority constraints violations
        nbTotalViol[0] = 0;
        for (RatioConstraint ratConst : highPrioConst) {
            int start = Math.max(0, dat.getNbCarsDayJMinus1() - ratConst.getWindowSize() + 1);
            for (int i = start; i < dat.getNbCars(); i++) { //Position of start point window
                int min = Math.min(i + ratConst.getWindowSize(), dat.getNbCars()); //Because lasts windows are "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int j = i; j < min; j++) {
                    if (allCars.get(j).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                }
                nbTotalViol[0]+=Math.max(0,nbCarShedulInWindow - ratConst.getMaxCarInWindow());
            }
        }
        
        //Count number of LowPriority constraints violations
        nbTotalViol[1]=0;
        for (RatioConstraint ratConst : lowPrioConst) {
            int start = Math.max(0, dat.getNbCarsDayJMinus1() - ratConst.getWindowSize() + 1);
            for (int i = start; i < dat.getNbCars(); i++) { //Position of start point window
                int min = Math.min(i + ratConst.getWindowSize(), dat.getNbCars()); //Because lasts windows are "incomplete" (not same length)
                int nbCarShedulInWindow = 0;
                for (int j = i; j < min; j++) {
                    if (allCars.get(j).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                }
                nbTotalViol[1] += Math.max(0, nbCarShedulInWindow - ratConst.getMaxCarInWindow());
            }
        }
        
        //Count number of paint color batches violations
        nbTotalViol[2]=1;   //we purge every beginning of day... =)
        int color = allCars.get(dat.getNbCarsDayJMinus1()).getPaintColor(); // color of first car of day J
        int sameColor=0;
        for (int i = dat.getNbCarsDayJMinus1(); i < dat.getNbCars(); i++) {
            if(color == allCars.get(i).getPaintColor()){
                if(++sameColor == dat.getMaxSamePainting()+1){
                    sameColor=1;
                    nbTotalViol[2]++;
                }
            }else{
                color=allCars.get(i).getPaintColor();
                nbTotalViol[2]++;
                sameColor=1;
            }
        }
        
        highPrioViol = nbTotalViol[0];
        lowPrioViol = nbTotalViol[1];
        paintViol = nbTotalViol[2];
        
        long objValue = nbTotalViol[0] * multObjective[0]
                + nbTotalViol[1] * multObjective[1]
                + nbTotalViol[2] * multObjective[2];
        
        
        goodObjValue = objValue == solToTest.getObjSol();
        goodNbPaintViol = nbTotalViol[2] * multObjective[2] == solToTest.getPaintViol();    
        
        return objValue == solToTest.getObjSol()
                && nbTotalViol[2] * multObjective[2] == solToTest.getPaintViol();
    }

    public static boolean isGoodObjValue() {
        return goodObjValue;
    }

    public static boolean isGoodNbPaintViol() {
        return goodNbPaintViol;
    }

    public static long getHighPrioViol() {
        return highPrioViol;
    }

    public static long getLowPrioViol() {
        return lowPrioViol;
    }

    public static long getPaintViol() {
        return paintViol;
    }
}
