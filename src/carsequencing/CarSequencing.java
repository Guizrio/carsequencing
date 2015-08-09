/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import tools.Stats;
import tools.Time;
import data_object.DataProblem;
import solvers.SolverBuilder;
import solvers.Solver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guillaume
 */
public class CarSequencing {
    
    public static final long maxTimeToSolve = 20000000000L;    //Time resolution allowed for solvers
    private static PrintWriter fil;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IllegalAccessException, IllegalStateException {
        
        //First we take Solvers we want to use
        String[] solvers = {"Algorithm","Swapper","BetterSwap","GeneticAlgo"};
        
        //Second we initialyze Instances Kind (folders which contains problem instances)
        String[] instancesKind = {"Instances_set_A","Instances_set_X"};
        
        //Set number of launch per solver and problem (to get means for non determinist solvers)
        int repeatSolve = 5;
        
        //Create Solutions folder
        String solutionsPath = new File("").getAbsolutePath() + File.separator + "Solutions";
        new File(solutionsPath).mkdirs();
        
        //List instances
        HashMap<String,String[]> instancesNames = new HashMap<>();
        
        for (int i = 0; i < instancesKind.length; i++) {
            String instancesKind1 = instancesKind[i];
            String folderPath = new File("").getAbsolutePath()+ File.separator 
                                                    + instancesKind1;
            boolean folderPathExists = new File(folderPath).exists();
            if(!folderPathExists){
                throw new IllegalAccessException("Could not find instances of kind " + instancesKind1);
            }else{
                
                //We check for all files of instance kind instanceKind1
                String[] instancesInCurrentFolder = new java.io.File(folderPath).list();
                Arrays.sort(instancesInCurrentFolder);
                
                instancesNames.put(instancesKind1, instancesInCurrentFolder);
                
                //We create folder solution
                new File(solutionsPath + File.separator + instancesKind1).mkdirs();
                
            }
        }
        
        //Now we could solve the problems :
        for (String solverName : solvers) {  //solvers parcour
            
            for(String key : instancesNames.keySet()){ //Kind of instance parcour
                
                for(String instance : instancesNames.get(key)){ //instance name parcour
                    
                    String instancePath = new File("").getAbsolutePath() + File.separator 
                                                + key + File.separator 
                                                + instance;
                    
                    String solutionPath = solutionsPath + File.separator 
                                                      + key + File.separator;
                    
                    String FileResult = solutionPath + solverName + " - " 
                                                        + instance + ".txt";
                    
                    fil = null;  //In case of fail of next block
                    try {
                        fil = new PrintWriter(new FileWriter(FileResult, false));
                    } catch (IOException ex) {
                        Logger.getLogger(CarSequencing.class.getName()).log(Level.SEVERE, null, ex);
                        throw new IllegalStateException("Problem in file solution creation.");
                    }
                    
                    DataProblem dat = new DataProblem(instancePath);
                    Stats stat = new Stats(3);
                    fil.println("==================================================================");
                    fil.println("Solutions given by Algorithm '" + solverName + "' for"
                            + " instance " + instance 
                            + "\n\tNumber of cars : " + dat.getNbCars() + " (of day J-1 : " 
                            + dat.getNbCarsDayJMinus1() + ", of day J : " +dat.getNbCarsDayJ()+")"
                            + "\n\tEasy to satisfy ratio constraint : " + dat.areEasySatisfyRatioConstraints()
                            + "\n\tInitial solution value : " + new Solution(dat.getCars(), dat).getObjSol()
                            );
                    fil.println("");
                    Solver solver = new SolverBuilder(solverName, dat).getSolver();
                    fil.println("Description : " + solver.getDescription());
                    fil.println("");
                    fil.println("Parameters : " + solver.getParams());
                    fil.println("");
                    fil.println("Computer type : ");
                    fil.println("\t- Mother board : Z97");
                    fil.println("\t- Processor    : i7 4790k @ 4.0Ghz");
                    fil.println("\t- RAM          : 16GB");
                    fil.println("==================================================================");
                    fil.println("");
                    fil.println("");
                    fil.println("");
                    fil.flush();
                    
                    Solution initSol = new Solution(dat.getCars(), dat);
                    
                    for (int i = 0; i < repeatSolve; i++) {
                        solver = new SolverBuilder(solverName, dat).getSolver();
                        
                        Solution sol = solver.solve();
                        
                        if(!SolutionValidator.validate(sol, dat)){
                            throw new IllegalStateException("the solution is not valid !");
                        }
                        
                        boolean equalToInitialScheduling = false;
                        if(sol.getObjSol() == initSol.getObjSol()){
                            equalToInitialScheduling = true;
                        }
                        
                        stat.setT(sol.getTimeToSolve());
                        stat.setZ((double)sol.getObjSol());
                        
                        fil.println("Solution n° " + i);
                        fil.println("Objectif value : " + sol.getObjSol());
                        fil.println("Time to solve : " + new Time(sol.getTimeToSolve()));
                        fil.println("Scheduling : " + sol.getCars());
                        fil.println("Does it have same objective value than initial scheduling : " + equalToInitialScheduling);
                        fil.println("");
                        fil.println("");
                        fil.flush();
                    }
                    fil.println("");
                    fil.println("");
                    fil.println(stat.result());
                    fil.flush();
                    fil.close();
                }
            }
        }
    }
    
}
