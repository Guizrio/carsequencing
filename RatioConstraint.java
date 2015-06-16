/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import com.sun.scenario.effect.impl.prism.PrCropPeer;

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
    public String toString(){
        return "name : " + name + "\tPriority : " + (isPrioritary ? "HighPriority" : "LowPriority") + "\t ratio : " + ratio + "\n";
    }
    
}
