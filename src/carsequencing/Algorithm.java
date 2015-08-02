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
public class Algorithm implements Solver{
    
    private final DataProblem dat;
    
    public Algorithm(DataProblem dat){
        this.dat = dat;
    }
    
    /**
     * Resolution by simply swapping random positions (without optimization of objective calculation)...
     * @return 
     */
    @Override
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
        
        System.out.println("\n\n");
        System.out.println("Total number of cars = " + allCars.size());
        System.out.println("Number of cars which must be sheduled : " + dat.getNbCarsDayJ());
        System.out.println("Initial Solution Value = " + sol.getObjSol());
        System.out.println("");        
        
        long nbIterations = 0; //number of iterations performed by algorithm
        
        //First we takes just car which have to be sorted
        while(new Time().timeLongElapsedSince(tim.getLastSavedTime()) <= CarSequencing.maxTimeToSolve){
            nbIterations++;
            long Incumbent = sol.getObjSol();
            
            int pos1 = (int)(Math.random()*shedulCars.size());
            int pos2 = (int)(Math.random()*shedulCars.size());
            
            Collections.swap(shedulCars, pos1, pos2);
            
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
     * @param allCars Sheduled cars in day. (J and J-1)
     * @return the solution cost (better is lower)
     */
    public Solution calcSolutionValue(ArrayList<Car> allCars, Time timeStart){
        
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
        
        Solution sol = new Solution(allCars, objValue, new ArrayList<Long>(),
                nbTotalViol[2] * multObjective[2],
                new Time().timeLongElapsedSince(timeStart.getLastSavedTime()));
        
        return sol;
    }

    @Override
    public ArrayList<String> getParams() {
       ArrayList<String> param = new ArrayList<>();
       param.add("No parameters for this algorithm except time for resolution : " + new Time(CarSequencing.maxTimeToSolve));
       return param;
    }

    @Override
    public String getDescription() {
        String str = "Naïve Algorithm : \n"
                + "\t- Resolution by simply swapping random cars positions.\n"
                + "\t- A swap result is keeped only if it's better than incumbent,\n"
                + "\t  particularly, there is only one swap (between 2 car) performed before valuate solution...\n"
                + "\t- This algorithm is without optimization of objective calculation\n"
                + "\t  (all violations are calculated again after every swap).\n"
                + "\t- It stop when time out";
        return str;
    }
}
