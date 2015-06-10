/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Guillaume
 */
public class Car {
    
    private long id;
    private Date dat;
    private long seqDep;
    private int paintColor;
    private ArrayList<RatioConstraint> ratioConstraint; //depend of car
    private ArrayList<RatioConstraint> highRatioConstraint; //Idem
    private ArrayList<RatioConstraint> lowRatioConstraint;  //Idem
    
//    private int[] constrained; 
    
    /**
     * 
     * @param dat
     * @param seqDep
     * @param id
     * @param paintColor 
     */
    public Car(Date dat, long seqDep, long id, int paintColor){
        this.dat =dat;
        this.seqDep = seqDep;
        this.id = id;
        this.paintColor = paintColor;
        this.ratioConstraint = new ArrayList<>();
        this.highRatioConstraint = new ArrayList<>();
        this.lowRatioConstraint=  new ArrayList<>();
    }
    
    /**
     * Create a deep copy from an existant car.
     * <p>
     * @param car the car to be copied into this object.
     */
    public Car(Car car){
        this.dat = new Date(car.getDat().getTime());
        this.seqDep = car.seqDep;
        this.id = car.id;
        this.paintColor = car.paintColor;
        this.ratioConstraint = car.ratioConstraint;
    }
    
    public void addRationConstraint(RatioConstraint rat){
        ratioConstraint.add(rat);
        if (rat.isIsPrioritary()) highRatioConstraint.add(rat);
        else lowRatioConstraint.add(rat);
    }

    public long getId() {
        return id;
    }

    public Date getDat() {
        return dat;
    }

    public long getSeqDep() {
        return seqDep;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public ArrayList<RatioConstraint> getRatioConstraint() {
        return ratioConstraint;
    }
    
    public boolean isContrainedBy(RatioConstraint rat){
        return ratioConstraint.contains(rat);
    }

    public ArrayList<RatioConstraint> getHighRatioConstraint() {
        return highRatioConstraint;
    }

    public ArrayList<RatioConstraint> getLowRatioConstraint() {
        return lowRatioConstraint;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    

    @Override
    public String toString() {
        return "Id = " + id + "\n"; //To change body of generated methods, choose Tools | Templates.
    }
    
}
