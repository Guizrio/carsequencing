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
public class GeneticAlgo implements Solver{ 
   private int nbSolutions = 200;
   private int maxUselessIterations = 70;
   private double propPopMothers = 0.25;
   private double propPopFathers = 0.5;
   private double propPopChildrenCouldMuting = 0.2;
   private double propGenInChromozomCouldMute = 0.5;    //Represent the maximal proportion of mutation in a children' gene which has to mute 
   private int nbGenInChromozomCouldMute;  //bind with propGenInChromozomCouldMute multiply by number of car must be scheduled
   private boolean continuity = false;
   private boolean cataclysmeActivated = true;
   
   private double nbCarsToSchedule;
   private DataProblem dat;
   
   
   public GeneticAlgo(DataProblem dat, boolean continuity){
       this.dat = dat;
       nbGenInChromozomCouldMute = (int)(propGenInChromozomCouldMute * dat.getNbCarsDayJ());
       this.continuity = continuity;
   }
   
   public GeneticAlgo(    int nbSolutions, 
                            int nbMutations,
                            int maxUselessIterations,
                            double propPopMothers,
                            double propPopFathers,
                            double propPopCouldMuting,
                            boolean continuity,
                            double propGenInChromozomCouldMute,
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
       
       if(propPopMothers < propPopFathers){
           throw new IllegalArgumentException("Mohers proportion could not exceed father proportion");
       }
       
       if(propPopFathers >= 0.5){
           throw new IllegalArgumentException("Mother couldn't exceed or be equal to half of population");
       }
           
       this.nbSolutions = nbSolutions;
       this.maxUselessIterations = maxUselessIterations;
       this.propPopMothers = propPopMothers;
       this.propPopFathers = propPopFathers;
       this.propPopChildrenCouldMuting = propPopCouldMuting;
       this.dat = dat;
       this.nbCarsToSchedule = dat.getNbCarsDayJ();
       this.propGenInChromozomCouldMute = propGenInChromozomCouldMute;
       this.nbGenInChromozomCouldMute = (int)(propGenInChromozomCouldMute * dat.getNbCarsDayJ());
       this.continuity = true;    //parameter for cross-over kind (continuity=true => all genes of mother and father are taken in the order they appear)
   }
    
    public Solution solve() {
        
        Time time = new Time();
        
        int compteur = 0;   // nombre total d'itérations
        ArrayList<Solution> solutions = new ArrayList<>();
        
        solutions.add(new Solution(dat.getCars(), dat));
        Swapper swapBibi = new Swapper(dat);
        solutions.add(swapBibi.solve());
        
        System.out.println("Base solution from swapper added");
        
        for (int i = 0 ; i < nbSolutions-2 ; i++) {
            Solution newSol = new Solution(dat);
            solutions.add(newSol);
        }
        
        Solution solBest = new Solution(solutions.get(0));
        Solution solBest2 = solutions.get(0);
        boolean firstPass = true;
        
        while(new Time().timeLongElapsedSince(time.getLastSavedTime()) < CarSequencing.maxTimeToSolve){
            
            int iterations = 0; // itérations sans changement de la solution optimale
            
            if(!firstPass && cataclysmeActivated){ 
                //Big cataclysme, no survivor, only new species.
                //Maybe we could had a feature that allow few survivors with probability in next version
                
                for (int i = 0 ; i < solutions.size() ; i++) {
                    Solution newSol = new Solution(dat);
                    newSol.setTimeToSolve(new Time().timeLongElapsedSince(time.getLastSavedTime()));
                    solutions.set(i,newSol);
                    solBest2 = solutions.get(0);
                }
            }
            
            firstPass = false;
            
            while (iterations < maxUselessIterations && new Time().timeLongElapsedSince(time.getLastSavedTime()) < CarSequencing.maxTimeToSolve) {
                compteur++;
                System.out.println("Generation number " + compteur);
                System.out.println("Degenerancy " + iterations);

                QuickSort.quicksort(solutions, 0, solutions.size() - 1);
                //System.out.println(solBest.getValObj(graph) + "    " + solutions.get(0).getValObj(graph));

//                System.out.println(solutions);

                if(solutions.get(0).getObjSol() < solBest2.getObjSol()){
                    System.out.println("Current Local Best SOlution : " + solBest2);
                    iterations = 0;
                    solBest2 = solutions.get(0);
                    solBest2.setTimeToSolve(new Time().timeLongElapsedSince(time.getLastSavedTime()));
                    
                }else{
                    iterations++;
                }
                
                if(solBest.getObjSol() > solBest2.getObjSol()){
                    solBest = new Solution(solBest2);
                }

                for (int i = 0 ; i < solutions.size() * propPopMothers ; i++) {
                    int rdm = (int)( Math.random()*(solutions.size() * propPopFathers) );
                    ResultatMating result = this.croisement(solutions.get(i), solutions.get(rdm));      

                    //Check if has to mute
                    if(Math.random() <= propPopChildrenCouldMuting){
                        result = mute(result);
                    }

                    int indexModif = solutions.size() - 2*i - 1;

                    solutions.set(indexModif, result.getSolution1());
                    solutions.set(indexModif-1, result.getSolution2());
                }
            }
        }
        
        
        
        return solBest;
    }

    public ResultatMating croisement(Solution pere, Solution mere) {
        int coupe = dat.getNbCarsDayJMinus1() + (int)(Math.random() * dat.getNbCarsDayJ());

        Solution enfant1 = new Solution();
        Solution enfant2 = new Solution();
        
        ArrayList<Car> carChildren1 = new ArrayList<>(dat.getHaveNotToBeSheduledcars());
        ArrayList<Car> carChildren2 = new ArrayList<>(dat.getHaveNotToBeSheduledcars());
        
        LinkedList<Car> restantCarsInFather = new LinkedList<Car>(pere.getCars().subList(dat.getNbCarsDayJMinus1(), pere.getCars().size()));
        LinkedList<Car> restantCarsInMother = new LinkedList<Car>(mere.getCars().subList(dat.getNbCarsDayJMinus1(), mere.getCars().size()));
        
        if(continuity){ //Case were we just want to take consecutives car from father untill coupe reached, and finish to fill sequence by restant cars in order they appear in mother sequence 
            for (int i = dat.getNbCarsDayJMinus1(); i < coupe ; i++) {
                carChildren1.add(pere.getCars().get(i));
                carChildren2.add(mere.getCars().get(i));
                
                restantCarsInMother.remove(pere.getCars().get(i));
                restantCarsInFather.remove(mere.getCars().get(i));
            }
            
            carChildren1.addAll(restantCarsInMother);
            carChildren2.addAll(restantCarsInFather);
            
        }else{  //Case were we take #coupe cars randomly from mother (for children 1) and father (for children 2), we inject them into respectively children 1 and 2 in respect of positions, and we finish to fill by add restant car in the ordering they appear in their counterpart.  
            
            for (int i = 0; i < dat.getNbCarsDayJ() ; i++) {
                carChildren1.add(new Car());
                carChildren2.add(new Car());
            }
            
            LinkedList<Car> restantCarInFatherForChild1 = new LinkedList<>(restantCarsInFather);
            LinkedList<Car> restantCarInMotherForChild2 = new LinkedList<>(restantCarsInMother);
            
            for (int i = dat.getNbCarsDayJMinus1(); i < coupe ; i++) {
                int j = (int)(Math.random() * restantCarsInMother.size());
                int k = (int)(Math.random() * restantCarsInFather.size());
                
                int index1 = mere.getCars().indexOf(restantCarsInMother.get(j));
                int index2 = pere.getCars().indexOf(restantCarsInFather.get(k));
                
                carChildren1.set(index1,restantCarsInMother.get(j));
                carChildren2.set(index2,restantCarsInFather.get(k));
                
                restantCarInFatherForChild1.remove(restantCarsInMother.get(j));
                restantCarInMotherForChild2.remove(restantCarsInFather.get(k));
                
                restantCarsInMother.remove(j);
                restantCarsInFather.remove(k);
                
            }
            
            int nbNullCarChildren1 = 0;
            int nbNullCarChildren2 = 0;
            
            for (int i = 0; i < dat.getNbCars(); i++) {
                if(carChildren1.get(i).getDat() == null){
                    nbNullCarChildren1++;
                }
                if(carChildren2.get(i).getDat() == null){
                    nbNullCarChildren2++;
                }
                
            }
            
            for (int i = dat.getNbCarsDayJMinus1(); i < dat.getNbCars(); i++) {
                if(carChildren1.get(i).getDat() == null){
                    carChildren1.set(i, restantCarInFatherForChild1.removeFirst());
                }
                if(carChildren2.get(i).getDat() == null){
                    carChildren2.set(i, restantCarInMotherForChild2.removeFirst());
                }
            }
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
        String str = "Genetic Algorithm";
        return str;
    }
    
    
    public ArrayList<String> getParams(){
        ArrayList<String> params = new ArrayList<>();
        
        params.add("\npopulation : " + nbSolutions);
        params.add("\nmothers' proportion in global population : " + propPopMothers);
        params.add("\nfathers' proportion in global population : " + propPopFathers);
        params.add("\nmax proportion of gene could mute : " + propGenInChromozomCouldMute);
        params.add("\nproportion of mutant children in children population : " + propPopChildrenCouldMuting);
        params.add("\nnumber of generation without increasing objective incumbent solution : " + maxUselessIterations);
        params.add("\nCataclysms activated : " + cataclysmeActivated);
        params.add("\nCross-over continue function : " + continuity);
        params.add("\nTime for resolution : " + new Time(CarSequencing.maxTimeToSolve));
        
        return params;
    }
    
    
    public String getStringParams(){
        String stringParams = " - Pop_" + nbSolutions  
                                + "  MUI_" + maxUselessIterations 
                                +  "  Pmoth_" + propPopMothers 
                                + "  Pfat_" + propPopFathers 
                                +  "  Pmut_" + propPopChildrenCouldMuting
                                + "  PGenMut_" + propGenInChromozomCouldMute;
        return stringParams;
    }
}
