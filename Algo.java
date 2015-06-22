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
    
    public Algo(DataProblem dat){
        this.dat = dat;
    }
    
    public abstract Solution solve();
    
    /**
     * Calcul of Solution' value without any supposition manier of problem resolution.
     * (naive test).
     * @param shedulCars Sheduled cars in day. (J and J-1)
     * @return the solution cost (better is lower)
     */
    public Solution InitialyzeSolutionValue(ArrayList<Car> shedulCars, Time timeStart){
        
        long nbTotalViol[] = new long[3]; //Order : highprio, lowprio, paint batches
        int[] multObjective = dat.getClassObjective().getMultForCompute();        
        
        ArrayList<RatioConstraint> highPrioConst = dat.getHighConst();
        ArrayList<RatioConstraint> lowPrioConst = dat.getLowConst();
        
        //Count number of HighPriority constraints violations
        nbTotalViol[0] = 0;
        for (RatioConstraint ratConst : highPrioConst) {
            for (int i = 0; i < dat.getNbCars(); i++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - i); //Because lasts windows are "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int j = i; j < i+min; j++) {
                    if (shedulCars.get(j).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                    nbTotalViol[0]+=Math.max(0,nbCarShedulInWindow - ratConst.getMaxCarInWindow());
                }
            }
        }
        
        //Count number of LowPriority constraints violations
        nbTotalViol[1]=0;
        for (RatioConstraint ratConst : lowPrioConst) {
            for (int i = 0; i < dat.getNbCars(); i++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - i); //Because lasts windows are "incomplete" (not same length)
                int nbCarShedulInWindow = 0;
                for (int j = i; j < i+min; j++) {
                    if (shedulCars.get(j).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                    nbTotalViol[1] += Math.max(0, nbCarShedulInWindow - ratConst.getMaxCarInWindow());
                }
            }
        }
        
        //Count number of paint color batches violations
        nbTotalViol[2]=0;
        int color = shedulCars.get(0).getPaintColor();
        int sameColor=0;
        for (int i = 0; i < dat.getNbCars(); i++) {
            if(color == shedulCars.get(i).getPaintColor()){
                if(++sameColor == dat.getMaxSamePainting()+1){
                    sameColor=1;
                    nbTotalViol[2]++;
                }
            }else{
                color=shedulCars.get(i).getPaintColor();
                sameColor=1;
            }
        }
        
        long objValue = nbTotalViol[0] * multObjective[0]
                + nbTotalViol[1] * multObjective[1]
                + nbTotalViol[2] * multObjective[2];
        
        Solution sol = new Solution(shedulCars, objValue, new ArrayList<Long>(),
                new Time().timeLongElapsedSince(timeStart.getLastSavedTime()));
        
        return sol;
    }
    
}
