/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 *
 * @author Guillaume
 */
public class Solution implements Comparable<Solution>{
    
    private ArrayList<Car> cars; //Represents all cars (J and J-1)
    
    private ArrayList<Long> objViolAtPosition;   //save alls objective violation for all positions
    
    private long paintViol;
    private boolean bestfound;
    
    private long objSol;
    private long timeToSolve;   //Time to found this solution (not necessary
                                //total time of algorithm duration 
    
    /**
     * Initialyze a new solution from list of sequenced cars, the objective associated with,
     * and partial objectives associated with, and finally time of resolution.
     * <p>
     * @param cars  The sequenced cars list
     * @param objSol    The objective of this solution
     * @param objViolAtPosition
     * @param timeToSolve The time to get this solution (not necessarily global time)
     */
    public Solution(ArrayList<Car> cars, long objSol, ArrayList<Long> objViolAtPosition, long paintViol, long timeToSolve){
        this.cars = new ArrayList<>(cars);
        this.objSol = objSol;
        this.objViolAtPosition = new ArrayList<Long>(objViolAtPosition);
        this.timeToSolve = timeToSolve;
        this.paintViol = paintViol;
        this.bestfound = false;
    }
    
    public Solution(Solution toCopy){
        this.cars = new ArrayList<>(toCopy.getCars());
        this.objSol = toCopy.getObjSol();
        this.objViolAtPosition = new ArrayList<Long>(toCopy.getObjViolAtPosition());
        this.timeToSolve = toCopy.getTimeToSolve();
        this.paintViol = toCopy.getPaintViol();
        this.bestfound = toCopy.isBestfound();
    }
    
    public Solution(){}
    
    /**
     * Construct a Solution from existing car sequence.
     * @param allCars
     * @param dat 
     */
    public Solution(ArrayList<Car> allCars, DataProblem dat){
        solutionInit(cars, dat);
    }
    
    /**
     * Construct a random solution.
     * @param dat 
     */
    public Solution(DataProblem dat) {
        
        //Fist we take another order for cars
        ArrayList<Car> unmovableCars = new ArrayList<>(dat.getHaveNotToBeSheduledcars());
        ArrayList<Car> carsToBeScheduled = new ArrayList<>(dat.getHaveToBeSheduledcars());
        
        Collections.shuffle(carsToBeScheduled);
        
        ArrayList<Car> cars = new ArrayList<>(unmovableCars);
        cars.addAll(carsToBeScheduled);
        
        //Then we compute solution for this
        solutionInit(cars, dat);
    }
   

    public ArrayList<Car> getCars() {
        return cars;
    }

    public long getObjSol() {
        return objSol;
    }

    public long getTimeToSolve() {
        return timeToSolve;
    }

    public ArrayList<Long> getObjViolAtPosition() {
        return objViolAtPosition;
    }

    public long getPaintViol() {
        return paintViol;
    }

    public boolean isBestfound() {
        return bestfound;
    }

    public void setBestfound(boolean bestfound) {
        this.bestfound = bestfound;
    }

    public void setTimeToSolve(long timeToSolve) {
        this.timeToSolve = timeToSolve;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.cars);
        hash = 59 * hash + Objects.hashCode(this.objViolAtPosition);
        hash = 59 * hash + (int) (this.paintViol ^ (this.paintViol >>> 32));
        hash = 59 * hash + (int) (this.objSol ^ (this.objSol >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Solution other = (Solution) obj;
        if (!Objects.equals(this.cars, other.cars)) {
            return false;
        }
        if (!Objects.equals(this.objViolAtPosition, other.objViolAtPosition)) {
            return false;
        }
        if (this.paintViol != other.paintViol) {
            return false;
        }
        if (this.objSol != other.objSol) {
            return false;
        }
        return true;
    }
    
    /**
     * method called from constructor : finish to construct a solution from existing car Sequence.
     * @param allCars
     * @param dat 
     */
    private void solutionInit(ArrayList<Car> allCars, DataProblem dat){
       Swapper swap = new Swapper(dat);
       this.cars = new ArrayList<>(allCars);
       
        this.objViolAtPosition = new ArrayList<>();
        for (int i = 0; i < dat.getNbCars(); i++) {
            this.objViolAtPosition.add(swap.objViolAtPos(i, dat.getCars()));  //Normally we want just for first starting windows, not first car... so WARNING !
        }
       
        Solution temp = swap.InitialyzeSolutionValue(allCars, new Time());
        this.objSol = temp.getObjSol();
        this.timeToSolve = 0L;
        this.bestfound = false;
        this.paintViol = temp.getPaintViol();
    }
    
    
    @Override
    public int compareTo(Solution o) {
        
        if(objSol > o.getObjSol()) return 1;
        if(objSol == o.getObjSol()) return 0;
        return -1;
        
    }
    @Override
    public String toString(){
        return "Solution : \n" + "\tCost : " + objSol + "\n\tTime : " 
                + new Time(timeToSolve).toString()
                + "\n\tBestFound : " + bestfound;// + "\n\tOrder : \n" + cars.toString().replaceAll(",", "\t,").replaceAll("\\[", "\t\\[");
    }

    
}
