/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
    
    private boolean printInColumn = false;
    
//    private int[] constrained;
    
    public Car(){}
    
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
        this.seqDep = car.getSeqDep();
        this.id = car.getId();
        this.paintColor = car.getPaintColor();
        this.ratioConstraint = new ArrayList<RatioConstraint>(car.getRatioConstraint());
        this.highRatioConstraint = new ArrayList<RatioConstraint>(car.getHighRatioConstraint());
        this.lowRatioConstraint = new ArrayList<RatioConstraint>(car.getLowRatioConstraint());
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
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 23 * hash + Objects.hashCode(this.dat);
        hash = 23 * hash + (int) (this.seqDep ^ (this.seqDep >>> 32));
        hash = 23 * hash + this.paintColor;
        hash = 23 * hash + Objects.hashCode(this.ratioConstraint);
        hash = 23 * hash + Objects.hashCode(this.highRatioConstraint);
        hash = 23 * hash + Objects.hashCode(this.lowRatioConstraint);
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
        final Car other = (Car) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.dat, other.dat)) {
            return false;
        }
        if (this.seqDep != other.seqDep) {
            return false;
        }
        if (this.paintColor != other.paintColor) {
            return false;
        }
        if (!Objects.equals(this.ratioConstraint, other.ratioConstraint)) {
            return false;
        }
        if (!Objects.equals(this.highRatioConstraint, other.highRatioConstraint)) {
            return false;
        }
        if (!Objects.equals(this.lowRatioConstraint, other.lowRatioConstraint)) {
            return false;
        }
        return true;
    }
    
    
    

    @Override
    public String toString() {
        if(printInColumn){
            return "Id = " + id + "\n";
        }else{
            return "Id = " + id;
        }
        
    }
    
}
