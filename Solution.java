/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Guillaume
 */
public class Solution implements Comparable<Solution>{
    
    private ArrayList<Car> cars; //If too big (because of deep copy, we can use
                                //ArrayList<Long> instead (where every long is an id)
    
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
