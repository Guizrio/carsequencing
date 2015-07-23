/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.util.ArrayList;

/**
 *
 * @author g.rio
 */
public abstract class Algo {
    
    protected final DataProblem dat;
    protected  ArrayList<Long> objViolAtPosition;   //save alls objective violation for all positions 
                                               //    (in order to don't recalculate violations along entirely vector of cars.
    
    public Algo(DataProblem dat){
        this.dat = dat;
        this.objViolAtPosition = new ArrayList<>();
    }
    
    public abstract Solution solve(long time);
    
    /**
     * Calcul of Solution' value without any supposition manier of problem resolution.
     * (naive test).
     * @param allCars Sheduled cars in day. (J and J-1)
     * @return the solution cost (better is lower)
     */
    public Solution InitialyzeSolutionValue(ArrayList<Car> allCars, Time timeStart){
        
        long nbTotalViol[] = new long[3]; //Order : highprio, lowprio, paint batches
        int[] multObjective = dat.getClassObjective().getMultForCompute();
        
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
        
        long objValue = nbTotalViol[0] * multObjective[0]
                + nbTotalViol[1] * multObjective[1]
                + nbTotalViol[2] * multObjective[2];
        
        Solution sol = new Solution(allCars, objValue, objViolAtPosition,
                nbTotalViol[2] * multObjective[2],
                new Time().timeLongElapsedSince(timeStart.getLastSavedTime()));
        
        return sol;
    }
    
}
