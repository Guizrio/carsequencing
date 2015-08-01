/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Guillaume
 */
public class GeneticAlgo {
   private String description = "Algorithme génétique"; 
   private int nbSolutions = 200;
   private int nbMutations = 200;
   private int maxUselessIterations = 60;
   private double propPopMothers = 0.25;
   private double propPopFathers = 0.5;
   private double propPopCouldMuting = 0.75;
   private double nbGenInChromozomCouldMute = 3; 
   
   private double nbCarsToSchedule;
   private DataProblem dat;
   
   
   public GeneticAlgo(){}
   
   public GeneticAlgo(    int nbSolutions, 
                            int nbMutations,
                            int maxUselessIterations,
                            double propPopMothers,
                            double propPopFathers,
                            double propPopCouldMuting,
                            DataProblem dat){
       
       if(  nbSolutions < 0 ||
            nbMutations < 0 ||
            nbSolutions < nbMutations ||
            maxUselessIterations < 0 ||
            propPopMothers < 0 || propPopMothers > 1 ||
            propPopFathers < 0 || propPopFathers > 1 ||
            propPopCouldMuting < 0 || propPopCouldMuting > 1
            ) {
           
           throw new IllegalArgumentException("un argument n'est pas correct");
       }
           
       this.nbSolutions = nbSolutions;
       this.nbMutations = nbMutations;
       this.maxUselessIterations = maxUselessIterations;
       this.propPopMothers = propPopMothers;
       this.propPopFathers = propPopFathers;
       this.propPopCouldMuting = propPopCouldMuting;
       this.dat = dat;
       this.nbCarsToSchedule = dat.getNbCarsDayJ();
       
   }
    
    public Solution solve(DataProblem dat) {
        
        Time time = new Time();
        
        int iterations = 0; // itérations sans changement de la solution optimale
        ArrayList<Solution> solutions = new ArrayList<>();
        
        solutions.add(new Solution(dat.getCars(), dat));
        Swapper swapBibi = new Swapper(dat);
        solutions.add(swapBibi.solve(CarSequencing.maxTimeToSolve));
        
        for (int i = 0 ; i < nbSolutions-2 ; i++) {
            Solution newSol = new Solution(dat);
            solutions.add(newSol);
        }
        
        Solution solBest = solutions.get(0);

        while (iterations < maxUselessIterations && new Time().timeLongElapsedSince(time.getLastSavedTime()) < CarSequencing.maxTimeToSolve) {
            QuickSort.quicksort(solutions, 0, solutions.size() - 1);
            //System.out.println(solBest.getValObj(graph) + "    " + solutions.get(0).getValObj(graph));
            
            if (solutions.get(0).getObjSol() < solBest.getObjSol()) {
                iterations = 0;
            }
            else {
                iterations++;
            }
            
            solBest = solutions.get(0); 
            
            for (int i = 0 ; i < solutions.size() * propPopMothers ; i++) {
                int rdm = (int)( Math.random()*(solutions.size() * propPopFathers) );
                ResultatMating result = this.croisement(solutions.get(i), solutions.get(rdm), true);      
                
                //Check if has to mute
                if(Math.random() <= propPopCouldMuting){
                    result = mute(result);
                }
                
                solutions.set(2 * i + solutions.size() / 2, result.getSolution1());
                solutions.set(2 * i + solutions.size() / 2 + 1, result.getSolution2());
            }
        }
        
        return solBest;
    }

    public ResultatMating croisement(Solution pere, Solution mere, boolean continuity) {
        int coupe = dat.getNbCarsDayJMinus1() + (int)(Math.random() * dat.getNbCarsDayJ());

        Solution enfant1 = new Solution();
        Solution enfant2 = new Solution();
        
        ArrayList<Car> carChildren1 = new ArrayList<>(dat.getHaveNotToBeSheduledcars());
        ArrayList<Car> carChildren2 = new ArrayList<>(dat.getHaveNotToBeSheduledcars());
        
        LinkedList<Car> restantCarsInFather = new LinkedList<Car>(pere.getCars().subList(dat.getNbCarsDayJMinus1(), pere.getCars().size()-1));
        LinkedList<Car> restantCarsInMother = new LinkedList<Car>(mere.getCars().subList(dat.getNbCarsDayJMinus1(), mere.getCars().size()-1));
        
        if(continuity){ //Case were we just want to take consecutives car from father untill coupe reached, and finish to fill sequence by restant cars in order they appear in mother sequence 
            for (int i = dat.getNbCarsDayJMinus1(); i < coupe ; i++) {
                carChildren1.add(pere.getCars().get(i));
                carChildren2.add(mere.getCars().get(i));
                
                restantCarsInMother.remove(pere.getCars().get(i));
                restantCarsInFather.remove(mere.getCars().get(i));
            }
            
            carChildren1.addAll(restantCarsInMother);
            carChildren2.addAll(restantCarsInFather);
        }      
        
        enfant1 = new Solution(carChildren1, dat);
        enfant2 = new Solution(carChildren2, dat);
        
        return (new ResultatMating(enfant1, enfant2));  
    }
    
    public class ResultatMating {
        // sert à contenir seulement le résultat du croisement
        private Solution solution1;
        private Solution solution2;

        public ResultatMating(Solution solution1, Solution solution2) {
          this.solution1 = solution1;    
          this.solution2 = solution2;    
        }

        public Solution getSolution1() {
          return this.solution1;
        }

        public Solution getSolution2() {
          return this.solution2;
        }

      }
    
    public ResultatMating mute(ResultatMating rm){
        Swapper swapChildren1 = new Swapper(rm.getSolution1(),dat);
        Swapper swapChildren2 = new Swapper(rm.getSolution2(),dat);
        
        Solution childrenMuted1 = new Solution(rm.getSolution1());
        Solution childrenMuted2 = new Solution(rm.getSolution2());
        
        int nbGenToMute = (int)(Math.random()* nbGenInChromozomCouldMute) + 1;
        
        for (int i = 0; i < nbGenToMute; i++) {
            
            int j = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
            int k = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
            childrenMuted1 = swapChildren1.swap(childrenMuted1, j, k);
            
            j = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
            k = (int)(Math.random()*dat.getNbCarsDayJ())+dat.getNbCarsDayJMinus1();
            childrenMuted2 = swapChildren2.swap(childrenMuted2, j, k);
            
        }
        
        return new ResultatMating(childrenMuted1, childrenMuted2);
    }
    
    public String getDescription(){
        return description;
    }
    
    
    public ArrayList<String> getParams(){
        ArrayList<String> params = new ArrayList<>();
        
        params.add("population : " + nbSolutions);
        params.add("proportion de mères dans la population : " + propPopMothers);
        params.add("proportion de pères dans la population : " + propPopFathers);
        params.add("nombre de mutations : " + nbMutations);
        params.add("proportion/catégorie d'individus pouvant muter : " + propPopCouldMuting);
        params.add("nombre maximum d'itérations inchangeantes avant d'arêter l'algo : " + maxUselessIterations);
        
        return params;
    }
    
    
    public String getStringParams(){
        String stringParams = " - Pop_" + nbSolutions + "  Nmut_" + nbMutations 
                                + "  MUI_" + maxUselessIterations 
                                +  "  Pmoth_" + propPopMothers 
                                + "  Pfat_" + propPopFathers 
                                +  "  Pmut_" + propPopCouldMuting;
        return stringParams;
    }
}
