/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author g.rio
 */
public class Swapper extends Algo{
    
    private ArrayList<Long> objViolAtPosition;   //save alls objective violation for all positions 
                                               //    (in order to don't recalculate violations along entirely vector of cars.
    private long multOfPaintViolations;     //keep the number multiplier of paint violation (objective function)
    
    long iViolationsAfter;     //Used to keep in memory violations costs after swap() call (because objViolAtPosition isn't be refreshed automatically --> more efficient)
    long jViolationsAfter;
    
    ArrayList<RatioConstraint> modifConstraints;   //The list of constraints for which we have count a violation in 
    
    public Swapper(DataProblem dat) {
        super(dat);
        multOfPaintViolations = dat.getClassObjective().getMultForCompute()[2];
        objViolAtPosition = new ArrayList<>();
        for (int i = 0; i < dat.getNbCars(); i++) {
            objViolAtPosition.add(objViolAtPos(i, dat.getCars()));
        }
    }
    
    
    /**
     * Perform a swap between car i and car j and return Solution.
     * <p>
     * @param sol The solution where cars must be swapped
     * @param i car to be exchanged with car j
     * @param j car to be exchanged with car i
     * @return the Solution os swap.
     */
    public Solution swap(Solution sol, int i, int j){
        Time timeStart = new Time();
        ArrayList<Car> cars = new ArrayList<Car>(sol.getCars());
        dat.getClassObjective().getMultForCompute();
        
        ArrayList<RatioConstraint> highPrioConstI = cars.get(i).getHighRatioConstraint();
        ArrayList<RatioConstraint> lowPrioConstI = cars.get(i).getLowRatioConstraint(); 
        ArrayList<RatioConstraint> highPrioConstJ = cars.get(j).getHighRatioConstraint();
        ArrayList<RatioConstraint> lowPrioConstJ = cars.get(j).getLowRatioConstraint(); 
        
        long nbTotalViol[] = new long[2]; //Order : highprio, lowprio, paint batches
        //Count number of HighPriority constraints violations
        nbTotalViol[0] = 0;
        for (RatioConstraint ratConst : highPrioConst) {
            int startPossibleViol = Math.max(i - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around i
            //int endPossibleViol = Math.min(i + ratConst.getWindowSize()-1, dat.getNbCars()-1);    //Last position where we could found an objective violation in window around i

            for (int j = startPossibleViol; j <= i; j++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - j); //Because lasts windows are "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int k = j; k < j+min; k++) {
                    if (shedulCars.get(k).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                    nbTotalViol[0]+=Math.max(0,nbCarShedulInWindow - ratConst.getMaxCarInWindow());
                }
            }
        }
//        long iViolationsBefore = objViolAtPosition.get(i);
//        long jViolationsBefore = objViolAtPosition.get(j);
                
        Car car1 = new Car(cars.get(i));
        
        cars.set(i, cars.get(j));
        cars.set(j, car1);
        
//        iViolationsAfter = objViolAtPos(i, cars);
//        jViolationsAfter = objViolAtPos(j, cars);
        
        //objViolAtPosition.set(i, iViolationsAfter);    //We don't set now because it's not necessary if result of swap does'nt give better solution...
        //objViolAtPosition.set(j, jViolationsAfter);       //NOte : if we plan to call more than one swap at a time without check and modify objViolAtPosition, it's more complicated,
                                                            //      and maybe in this case it's better to automatically set objViolAtPosition here (and eventually to deset it after...
        
        //We have to compute the new paint violation number@TODO ta mère
        nbTotalViol[2]=0;
        int color = cars.get(0).getPaintColor();
        int sameColor=0;
        for (int k = 0; k < dat.getNbCars(); k++) {
            if(color == cars.get(k).getPaintColor()){
                if(sameColor == dat.getMaxSamePainting()+1){
                    sameColor=1;
                    nbTotalViol[2]++;
                }
            }else{
                nbTotalViol[2]++;
                color=cars.get(k).getPaintColor();
                sameColor=1;
            }
        }
        
        long objValue = sol.getObjSol() - iViolationsBefore - jViolationsBefore
                                        + iViolationsAfter + jViolationsAfter
                                        + nbTotalViol[2] * multOfPaintViolations;
        
        Solution solBis = new Solution(cars, objValue, sol.getPartialObjSol()[0],
                sol.getPartialObjSol()[1], sol.getPartialObjSol()[2], 
                new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) 
                        + sol.getTimeToSolve());
        
        
        
        return solBis;
    }
    
    /**
     * Give the objective violation for high and low priority at position i
     * of an sequenced car list.
     * <p>
     * Note that like paint batches does'nt have a "window" in which count the number
     * of violations (except window which represent all cars), the result of this
     * function don't contain any paint batches violations.
     * <p>
     * @param i the index of the sequenced car in list where we want the objective violation.
     * @return Objective violation at position i
     */
    private long objViolAtPos(int i, ArrayList<Car> shedulCars){
        long nbTotalViol[] = new long[2]; //Order : highprio, lowprio, paint batches
        int[] multObjective = dat.getClassObjective().getMultForCompute();        
        
        ArrayList<RatioConstraint> highPrioConst = dat.getHighConst();      // Better : use directly shedulCars.get(i).getHighRatioConstraint();
        ArrayList<RatioConstraint> lowPrioConst = dat.getLowConst();        // Better : use directly shedulCars.get(i).getLowRatioConstraint();
        
        //Count number of HighPriority constraints violations
        nbTotalViol[0] = 0;
        for (RatioConstraint ratConst : highPrioConst) {
            int startPossibleViol = Math.max(i - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around i
            //int endPossibleViol = Math.min(i + ratConst.getWindowSize()-1, dat.getNbCars()-1);    //Last position where we could found an objective violation in window around i

            for (int j = startPossibleViol; j <= i; j++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - j); //Because lasts windows are "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int k = j; k < j+min; k++) {
                    if (shedulCars.get(k).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                }
                nbTotalViol[0]+= nbCarShedulInWindow - ratConst.getMaxCarInWindow()>0 ? 1: 0;
            }
        }
        
        //Count number of LowPriority constraints violations
        nbTotalViol[1] = 0;
        for (RatioConstraint ratConst : lowPrioConst) {
            int startPossibleViol = Math.max(i - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around i
            //int endPossibleViol = Math.min(i + ratConst.getWindowSize() - 1, dat.getNbCars());    //Last position where we could found an objective violation in window around i
            
            for (int j = startPossibleViol; j <= i; j++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - j); //Because lasts windows are "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int k = j; k < j+min; k++) {
                    if (shedulCars.get(k).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                }
                nbTotalViol[1]+= nbCarShedulInWindow - ratConst.getMaxCarInWindow()>0 ? 1: 0;
                
            }
        }
        
        return nbTotalViol[0] * multObjective[0]
                + nbTotalViol[1] * multObjective[1];
    }

    @Override
    public Solution solve() {
        Time timeStart = new Time();
        
        ArrayList<Car> shedulCars = new ArrayList<>(dat.getCars());
        Solution incumbent = InitialyzeSolutionValue(shedulCars, timeStart);
        
        System.out.println("\n\n");
        System.out.println("Total number of cars = " + shedulCars.size());
        System.out.println("Number of cars which must be sheduled : " + dat.getNbCarsDayJ());
        System.out.println("Initial Solution Value = " + incumbent.getObjSol());
        System.out.println("");
        
        long nbIterations = 0; //number of iterations performed by algorithm
        
        //First we takes just car which have to be sorted
        while(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) <= 1000000000l){
            nbIterations++;
            
            int randi = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
            int randj = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
            
            //To be erased
            if(randi>499) System.out.println("randi = " + randi);
            if(randi<0) System.out.println("randi = " + randi);
            if(randj>499) System.out.println("randj = " + randj);
            if(randj<0) System.out.println("randj = " + randj);
            
            
            Solution solToTest = swap(incumbent, randi, randj);
            
            if(incumbent.getObjSol() > solToTest.getObjSol()){
                incumbent = solToTest;
                objViolAtPosition.set(randi, iViolationsAfter);    
                objViolAtPosition.set(randj, jViolationsAfter);
            }
            
        }
        System.out.println("Nombre d'itérations : " + nbIterations);
        return incumbent;
    }
}
