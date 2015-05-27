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
    
    private long objSol;
    private long[] partialObjSol;   //objective of highprio, lowprio, paint batches (in this order)
    private long timeToSolve;   //Time to found this solution (not necessary
                                //total time of algorithm duration 
    
    /**
     * Initialyze a new solution from list of sequenced cars, the objective associated with,
     * and partial objectives associated with, and finally time of resolution.
     * <p>
     * @param cars  The sequenced cars list
     * @param objSol    The objective of this solution
     * @param sol1  The objective of high priority constraints violations 
     * @param sol2  The objective of low priority constraints violations
     * @param sol3  The objective of paint batches constraints violations
     * @param timeToSolve The time to get this solution (not necessarily global time)
     */
    public Solution(ArrayList<Car> cars, long objSol, long sol1, long sol2, long sol3, long timeToSolve){
        this.cars = new ArrayList<>(cars);
        this.objSol = objSol;
        partialObjSol = new long[3];
        partialObjSol[0] = sol1;
        partialObjSol[1] = sol2;
        partialObjSol[2] = sol3;
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
    
    public long getPaintviolations(){
        return partialObjSol[2];
    }

    public long[] getPartialObjSol() {
        return partialObjSol;
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
