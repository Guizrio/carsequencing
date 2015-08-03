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
public class BetterSwap extends Swapper{

    public BetterSwap(DataProblem dat) {
        super(dat);
    }
    
    
    @Override
    public Solution solve(){
        
        Time timeStart = new Time();
        
        ArrayList<Car> allCars = new ArrayList<>(dat.getCars());
        Solution localIncumbent = InitialyzeSolutionValue(allCars, timeStart);
        Solution globalIncumbent = new Solution(localIncumbent);
        
        System.out.println("\n\n");
        System.out.println("Total number of cars = " + allCars.size());
        System.out.println("Number of cars which must be sheduled : " + dat.getNbCarsDayJ());
        System.out.println("Initial Solution Value = " + localIncumbent.getObjSol());
        System.out.println("");
        
        long nbIterations = 0; //number of iterations performed by algorithm
        
        //First we takes just car which have to be sorted
        while(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) < CarSequencing.maxTimeToSolve){
            boolean bestfound=true;
            System.out.println(new Time().timeElapsedSince(timeStart.getLastSavedTime()));
            for (int i = dat.getNbCarsDayJMinus1(); i < dat.getNbCars()-1; i++) {
                for (int j = i+1; j < dat.getNbCars(); j++) {
                    nbIterations++;
                    Solution solToTest = swap(localIncumbent, i, j);
            
                    if(localIncumbent.getObjSol() > solToTest.getObjSol()){
                        localIncumbent = solToTest;
                        localIncumbent.setTimeToSolve(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()));
                        objViolAtPosition = solToTest.getObjViolAtPosition();
                        bestfound = false;
                    }
                }
            }
            
            if(globalIncumbent.getObjSol() > localIncumbent.getObjSol()){
                System.out.println("ameliorated at " + new Time().timeElapsedSince(timeStart.getLastSavedTime()));
                globalIncumbent = new Solution(localIncumbent);
            }
            
            if(bestfound){
                System.out.println("Local swap convergence reached, best cost : "
                        + localIncumbent.getObjSol());
                //System.out.println("Local solution validate ? " + SolutionValidator.validate(localIncumbent, dat));
                 System.out.println("Begin initialization of another seed");
                //Portion of car which will could be swapped to break local convergence
               int  nbToChange = (int) (dat.getNbCarsDayJ()/5);
               
               Time tt = new Time();
                for (int i = 0; i < nbToChange; i++) {
                    int aleaPos1 = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
                    int aleaPos2 = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
                    
                    localIncumbent = swap(localIncumbent, aleaPos1, aleaPos2);
                }
                //System.out.println("Time passed to initialyze new seed : " + new Time().timeElapsedSince(tt.getLastSavedTime()));
            }
            
            //Attempt to resolve with multiples swaps before valuate solution :
            
//            if(bestfound && new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) <= timeToSolve){
                
//                int i=dat.getNbCarsDayJMinus1()+1;
//                sortie:
//                while( i<dat.getNbCars()-1 && new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) <= timeToSolve){
//                    for (int j = i+1; j < dat.getNbCars(); j++) {
//                        for (int k = i+1; k < dat.getNbCars(); k++) {
//                            nbIterations++;
//                            Solution solToTest = swap(incumbent, i-1, j);
//                            solToTest = swap(solToTest,i,k);
//
//                            if(incumbent.getObjSol() > solToTest.getObjSol()){
//                                incumbent = solToTest;
//                                incumbent.setTimeToSolve(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()));
//                                objViolAtPosition = solToTest.getObjViolAtPosition();
//                                bestfound = false;
//                                break sortie;
//                            }
//                        }
//                    }
//                    i++;
//                }
//                sortie:
//                while(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) <= timeToSolve){
//                    nbIterations++;
//                    
//                    int i = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
//                    int j = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
//                    int k =(int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
//                    
//                    Solution solToTest = swap(incumbent, i-1, j);
//                    solToTest = swap(solToTest,i,k);
//
//                    if(incumbent.getObjSol() > solToTest.getObjSol()){
//                        incumbent = solToTest;
//                        incumbent.setTimeToSolve(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()));
//                        objViolAtPosition = solToTest.getObjViolAtPosition();
//                        bestfound = false;
//                        break sortie;
//                    }
//                }
//                
//                if(bestfound && new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) <= timeToSolve){
//                    System.out.println("limit of algo reached");
//                    break;
//                }else if(new Time().timeLongElapsedSince(timeStart.getLastSavedTime()) > timeToSolve){
//                    System.out.println("Time limit reached");
//                    break;
//                }
            
            
                
//            }else if(bestfound){
//                localIncumbent.setBestfound(bestfound);
//            }else{
//                localIncumbent.setBestfound(false);
//            }
            
        }
        System.out.println("Nombre d'itérations : " + nbIterations);
        return globalIncumbent;
    }
    
    @Override
    public ArrayList<String> getParams() {
       ArrayList<String> param = new ArrayList<>();
       param.add("No parameters for this algorithm except time for resolution : " + new Time(CarSequencing.maxTimeToSolve));
       return param;
    }

    @Override
    public String getDescription() {        
        String str = "Basic Algorithm Extension : \n"
                + "\t- Same comportment as for Swapper solve method,\n"
                + "\t  except that if remains time and the Swapper part finish,"
                + "\t  then the algorithm take a random deviated solution from "
                + "\t  the incumbent and restart same process."
                + "\t- It stop when time is out";
        return str;
    }
    
}
