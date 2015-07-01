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
     * first it separates constraints in 3 groups :
     * The first group contains exclusively constraints associated to only car number i.
     * The second contains constraints which are associated to both car.
     * The last containt only car from exclusively car j.
     * <p>
     * Then it only calculate the potential unit added violation in the neighborhood of
     * the position i for each windows in this neighborhood (which is of size the windows size).
     * This allows to not recalculate all the solution cost, but only where it is necessary.
     * (but improvements in implementation are still possible).
     * <p>
     * For the first group, we know that if there is a violation on a window, we could
     * subtract '1 multiply by objectiveCoeffConstaint' to the objviolAtPos[i] because
     * Car j couldn't add any violation to this constraint.
     * <p>
     * For the second group, it is necesssary to save windows on which they had violations
     * (other solution : 
     * 
     * @param sol The solution where cars must be swapped
     * @param i car to be exchanged with car j
     * @param j car to be exchanged with car i
     * @return the Solution os swap.
     */
    public Solution swap(Solution sol, int i, int j){
        Time timeStart = new Time();
        ArrayList<Car> cars = new ArrayList<Car>(sol.getCars());
        dat.getClassObjective().getMultForCompute();        
        
        ArrayList<RatioConstraint> constBaseI = cars.get(i).getRatioConstraint();
        ArrayList<RatioConstraint> constBaseJ = cars.get(j).getRatioConstraint();
        
        //Sort in 3 groups :
        ArrayList<RatioConstraint> constI =  new ArrayList<>();
        ArrayList<RatioConstraint> constJ =  new ArrayList<>();
        
        for (RatioConstraint constraint : dat.getRatConst()) {
            if(constBaseJ.contains(constraint)){
                constJ.add(constraint);
            }else if(constBaseI.contains(constraint) && !constBaseJ.contains(constraint)){
                constI.add(constraint);
            }
        }
        
        //We create temp variable to store potential objective diffence at each pos
        long potentialObjDiff[] = new long[objViolAtPosition.size()];               //Not a number of violations but a cost/gain per position...
        
        long objChangeI=0L;
        long objChangeJ=0L;
        
        //Now we just recalculate for only I violations:
        for (RatioConstraint ratConst : constI) {
            int startPossibleViol = Math.max(i - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around i

            for (int k = startPossibleViol; k <= i; k++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - k); //Because lasts windows could be "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int l = k; l < k+min; l++) {
                    if (cars.get(l).getRatioConstraint().contains(ratConst)){
                        if(nbCarShedulInWindow++ > ratConst.getMaxCarInWindow()){
                            objChangeI -= (long)ratConst.getObjectiveCoeff();
                            break; //Like the car I could just add only one violation per window...
                        }
                        System.out.println("");
                    }
                    System.out.println("");
                }
                System.out.println("");
            }
            System.out.println("");
        }
        
         //We do exactly the same for car J :
        for (RatioConstraint ratConst : constJ) {
            int startPossibleViol = Math.max(j - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around j

            for (int k = startPossibleViol; k <= j; k++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - k); //Because lasts windows could be "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int l = k; l < k+min; l++) {
                    if (cars.get(l).getRatioConstraint().contains(ratConst)){
                        if(nbCarShedulInWindow++ > ratConst.getMaxCarInWindow()){
                            objChangeJ -= (long)ratConst.getObjectiveCoeff();
                            break; //Like the car I could just add only one violation per window...
                        }
                    }
                }
            }
        }
        
        //Now it remains to calculate for constraints which are common to both car (more difficult)
        
        //Pour chaque contrainte, On passe juste la première voiture (et partout où c'est
        /*      -Strictement inférieur à nbMaxViol, alors mettre J à la place ne rajoutera pas de violation.
                -Strictement supérieur à nbMaxViol, alors mettre J à la place augmentera nécessairement 
        
        EDIT !!!! ON EST UN PEU CON (normal on est breton) : si les voitures ont les mêmes contraintes
        ben alors forcément les échanger ne change rien.... Pouin Pouin Pouin Pouinouinuoin....
        */
        
        
        //So we have just to calculate with two same block as above the violation added by the swap... (easy)
        
        
        //First we do the swap
        Collections.swap(cars, i, j);
        
        
        //Now we just recalculate for new J violations on windows new J:
        for (RatioConstraint ratConst : constJ) {
            int startPossibleViol = Math.max(i - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around i
            //int endPossibleViol = Math.min(i + ratConst.getWindowSize()-1, dat.getNbCars()-1);    //Last position where we could found an objective violation in window around i

            for (int k = startPossibleViol; k <= i; k++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - k); //Because lasts windows could be "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int l = k; l < k+min; l++) {
                    if (cars.get(l).getRatioConstraint().contains(ratConst)){
                        if(nbCarShedulInWindow++ > ratConst.getMaxCarInWindow()){
                            objChangeI += (long)ratConst.getObjectiveCoeff();
                            break; //Like the car I could just add only one violation per window...
                        }
                    }
                }
            }
        }
        
         //We do exactly the same for car J :
        for (RatioConstraint ratConst : constI) {
            int startPossibleViol = Math.max(j - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around j
            //int endPossibleViol = Math.min(j + ratConst.getWindowSize()-1, dat.getNbCars()-1);    //Last position where we could found an objective violation in window around j

            for (int k = startPossibleViol; k <= j; k++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - k); //Because lasts windows could be "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int l = k; l < k+min; l++) {
                    if (cars.get(l).getRatioConstraint().contains(ratConst)){
                        if(nbCarShedulInWindow++ > ratConst.getMaxCarInWindow()){
                            objChangeJ += (long)ratConst.getObjectiveCoeff();
                            break; //Like the car I could just add only one violation per window...
                        }
                    }
                }
            }
        }
        
        //And Calculate the paint violations :
        long nbPaintViol=0;
        int color = cars.get(0).getPaintColor();
        int sameColor=0;
        for (int k = 0; k < dat.getNbCars(); k++) {
            if(color == cars.get(k).getPaintColor()){
                if(++sameColor == dat.getMaxSamePainting()+1){
                    sameColor=1;
                    nbPaintViol++;
                }
            }else{
                color=cars.get(k).getPaintColor();
                sameColor=1;
            }
        }
        
        long objValue = sol.getObjSol() +objChangeI + objChangeJ
                                        + nbPaintViol * multOfPaintViolations;
        
        //Now we could store new objViolAtPosition for i and j positions
        ArrayList<Long> newObjViolAtPos = new ArrayList<Long>(objViolAtPosition);
        newObjViolAtPos.set(i, newObjViolAtPos.get(i) + objChangeI);
        newObjViolAtPos.set(j, newObjViolAtPos.get(j) + objChangeJ);
        
        
        Solution solBis = new Solution(cars, objValue, newObjViolAtPos, 
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
            
            for (int j = startPossibleViol; j <= i; j++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - j); //Because lasts windows are "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int k = j; k < j+min; k++) {
                    if (shedulCars.get(k).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                }
                nbTotalViol[0]+= Math.max(0,nbCarShedulInWindow - ratConst.getMaxCarInWindow());
            }
        }
        
        //Count number of LowPriority constraints violations
        nbTotalViol[1] = 0;
        for (RatioConstraint ratConst : lowPrioConst) {
            
            int startPossibleViol = Math.max(i - ratConst.getWindowSize() + 1, 0);    //First position where we could found an objective violation in window around i
            
            for (int j = startPossibleViol; j <= i; j++) { //Position of start point window
                int min = Math.min(ratConst.getWindowSize(), dat.getNbCars() - j); //Because lasts windows are "incompletes" (not same length)
                int nbCarShedulInWindow = 0;
                for (int k = j; k < j+min; k++) {
                    if (shedulCars.get(k).getRatioConstraint().contains(ratConst)){
                        nbCarShedulInWindow++;
                    }
                }
                nbTotalViol[1]+= Math.max(0,nbCarShedulInWindow - ratConst.getMaxCarInWindow());
                
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
        while(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) <= 30000000000000l){
            
            for (int i = dat.getNbCarsDayJMinus1(); i < dat.getNbCars()-1; i++) {
                System.out.println("");
                for (int j = i+1; j < dat.getNbCars(); j++) {
                    nbIterations++;
                    Solution solToTest = swap(incumbent, i, j);
            
                    if(incumbent.getObjSol() > solToTest.getObjSol()){
                        incumbent = solToTest;
                        objViolAtPosition = solToTest.getObjViolAtPosition();
                    }
                }
            }
            
//            int randi = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
//            int randj = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
            
            //To be erased
//            if(randi>499) System.out.println("randi = " + randi);
//            if(randi<0) System.out.println("randi = " + randi);
//            if(randj>499) System.out.println("randj = " + randj);
//            if(randj<0) System.out.println("randj = " + randj);

            
        }
        System.out.println("Nombre d'itérations : " + nbIterations);
        return incumbent;
    }
}
