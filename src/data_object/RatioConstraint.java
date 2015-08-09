/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_object;

import com.sun.scenario.effect.impl.prism.PrCropPeer;
import java.util.Objects;

/**
 *
 * @author Guillaume
 */
public class RatioConstraint implements Comparable<RatioConstraint>{
    
    private boolean isPrioritary;
    private String ratio;
    private int windowSize;
    private int maxCarInWindow;
    private String name;
    private final int ObjectiveCoeff;
    
    public RatioConstraint(String ratio, int windowSize, int maxCarInWindow, boolean isPrioritary, String name, int ObjectiveCoeff){
        this.ratio = ratio;
        this.maxCarInWindow = maxCarInWindow;
        this.windowSize = windowSize;
        this.name = name;
        this.isPrioritary = isPrioritary;
        this.ObjectiveCoeff = ObjectiveCoeff;
    }

    public boolean isIsPrioritary() {
        return isPrioritary;
    }

    public String getRatio() {
        return ratio;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getMaxCarInWindow() {
        return maxCarInWindow;
    }

    public String getName() {
        return name;
    }

    public int getObjectiveCoeff() {
        return ObjectiveCoeff;
    }

    @Override
    public int compareTo(RatioConstraint o){
        
        if(isPrioritary && !o.isIsPrioritary()){
            
            return 1;
        
        }else if(isPrioritary==o.isIsPrioritary() 
                && windowSize==o.getWindowSize() 
                && maxCarInWindow==o.maxCarInWindow){
            
            return 0;
            
        }else if (!isPrioritary && !o.isIsPrioritary()){
            
            if(maxCarInWindow <= o.getMaxCarInWindow() 
                    && windowSize >= o.getWindowSize()) 
                return 1;
            
            if(maxCarInWindow >= o.getMaxCarInWindow() 
                    && windowSize <= o.getWindowSize()) 
                return -1;
            
            double thiis = (double)(maxCarInWindow)/(double)(windowSize);
            double other = (double)(o.getMaxCarInWindow())/(double)(o.getWindowSize());
                        
            if(other > thiis) return 1;
            
            if(other == thiis && windowSize <= o.getWindowSize()) return 1;
            
            return -1;
            
        }else if (isPrioritary && o.isIsPrioritary()){
            
            if(maxCarInWindow <= o.getMaxCarInWindow() 
                    && windowSize >= o.getWindowSize()) 
                return 1;
            
            if(maxCarInWindow >= o.getMaxCarInWindow() 
                    && windowSize <= o.getWindowSize()) 
                return -1;
            
            double thiis = (double)(maxCarInWindow)/(double)(windowSize);
            double other = (double)(o.getMaxCarInWindow())/(double)(o.getWindowSize());
                        
            if(other >= thiis) return 1;
            
            if(other == thiis && windowSize <= o.getWindowSize()) return 1;
            
            return -1;
            
        }else{
            
            return -1;
            
        }        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.isPrioritary ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.ratio);
        hash = 29 * hash + this.windowSize;
        hash = 29 * hash + this.maxCarInWindow;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + this.ObjectiveCoeff;
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
        final RatioConstraint other = (RatioConstraint) obj;
        if (this.isPrioritary != other.isPrioritary) {
            return false;
        }
        if (!Objects.equals(this.ratio, other.ratio)) {
            return false;
        }
        if (this.windowSize != other.windowSize) {
            return false;
        }
        if (this.maxCarInWindow != other.maxCarInWindow) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.ObjectiveCoeff != other.ObjectiveCoeff) {
            return false;
        }
        return true;
    }
    
    
    
    @Override
    public String toString(){
        return "name : " + name + "\tPriority : " + (isPrioritary ? "HighPriority" : "LowPriority") + "\t ratio : " + ratio + "\n";
    }
    
}
