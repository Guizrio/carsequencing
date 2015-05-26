/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guillaume
 */
public class Algorithm {
    
    private final DataProblem dat;
    
    public Algorithm(DataProblem dat){
        this.dat = dat;
    }
    
    /**
     * Just a naive resolution by simply shuffle positions...
     * @return 
     */
    public Solution solve(){
        Time tim = new Time();
        
        ArrayList<Car> shedulCars = new ArrayList<>(dat.getHaveToBeSheduledcars());
        ArrayList<Car> nonShedulCars = new ArrayList<>(dat.getHaveNotToBeSheduledcars());
        ArrayList<Car> allCars = new ArrayList<>(dat.getCars());
        
        //Collections.shuffle(shedulCars);
       
        allCars = new ArrayList<>();
        allCars.addAll(nonShedulCars);
        allCars.addAll(shedulCars);
        
//        long objectiveValue = calcSolutionValue(shedulCars);
        Solution sol = calcSolutionValue(allCars, tim);
        
        long nbIterations = 0; //number of iterations performed by algorithm
        
        //First we takes just car which have to be sorted
        while(new Time().timeLongElapsedSince(tim.getLastSavedTime()) <= 10000000l){
            nbIterations++;
            long Incumbent = sol.getObjSol();
            Collections.shuffle(shedulCars);
            
            allCars = new ArrayList();
            allCars.addAll(nonShedulCars);
            allCars.addAll(shedulCars);
            
            Solution solToTest = calcSolutionValue(allCars, tim);
            
            if(Incumbent > solToTest.getObjSol()) sol = solToTest;
        }
        System.out.println("Nombre d'itérations : " + nbIterations);
        return sol;
    }
    
    /**
     * Calcul of Solution' value without any supposition manier of problem resolution.
     * (naive test).
     * @param shedulCars Sheduled cars in day. (J and J-1)
     * @return the solution cost (better is lower)
     */
    public Solution calcSolutionValue(ArrayList<Car> shedulCars, Time timeStart){
        
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
                if(sameColor == dat.getMaxSamePainting()+1){
                    sameColor=1;
                    nbTotalViol[2]++;
                }
            }else{
                nbTotalViol[2]++;
                color=shedulCars.get(i).getPaintColor();
                sameColor=1;
            }
        }
        
        long objValue = nbTotalViol[0] * multObjective[0]
                + nbTotalViol[1] * multObjective[1]
                + nbTotalViol[2] * multObjective[2];
        
        Solution sol = new Solution(shedulCars, objValue,
                nbTotalViol[0] * multObjective[0],
                nbTotalViol[1] * multObjective[1],
                nbTotalViol[2] * multObjective[2],
                new Time().timeLongElapsedSince(timeStart.getLastSavedTime()));
        
        return sol;
    }
}
