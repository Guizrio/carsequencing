/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.awt.BorderLayout;
import java.util.ArrayList;

/**
 *
 * @author Guillaume
 */
public class Solution implements Comparable<Solution>{
    
    private ArrayList<Car> cars; //If too big (because of deep copy, we can use
                                //ArrayList<Long> instead (where every long is an id)
    
    private ArrayList<Long> objViolAtPosition;   //save alls objective violation for all positions
    
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
    public Solution(ArrayList<Car> cars, long objSol, ArrayList<Long> objViolAtPosition, long timeToSolve){
        this.cars = new ArrayList<>(cars);
        this.objSol = objSol;
        this.objViolAtPosition = new ArrayList<Long>(objViolAtPosition);
        this.timeToSolve = timeToSolve;
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

    @Override
    public int compareTo(Solution o) {
        
        if(objSol > o.getObjSol()) return 1;
        if(objSol == o.getObjSol()) return 0;
        return -1;
        
    }
    @Override
    public String toString(){
        return "Solution : \n" + "\tCost : " + objSol + "\n\tTime : " 
                + new Time(timeToSolve).toString();// + "\n\tOrder : \n" + cars.toString().replaceAll(",", "\t,").replaceAll("\\[", "\t\\[");
    }
}
