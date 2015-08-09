/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_object;

import java.util.Arrays;

/**
 *
 * @author Guillaume
 */
public class ClassObjective {
    
    private boolean areEasyToSatisfyConstraints;
    private final boolean isExistLowPriorityConstraints;
    private String[] objectives;
    private int kindObjective;
    
    public ClassObjective(String[] objectives){
        
        areEasyToSatisfyConstraints = false;
        
        for(String str : objectives){
            if(str.contains("easy_to_satisfy")) areEasyToSatisfyConstraints = true;
        }
        
        System.out.println("objectives length = " + objectives.length);
        for (int i = 0; i < objectives.length; i++) {
            System.out.println(objectives[i]);
        }
        
        if (objectives.length==2){
            isExistLowPriorityConstraints = false;
            if(objectives[0].equalsIgnoreCase("high_priority_level_and_easy_to_satisfy_ratio_constraints")){
                if(objectives[1].equalsIgnoreCase("paint_color_batches")) kindObjective = 3;
                else throw new IllegalStateException("Cas inconnu : " + objectives.toString());
                
            }else if(objectives[0].equalsIgnoreCase("high_priority_level_and_difficult_to_satisfy_ratio_constraints")){
                if(objectives[1].equalsIgnoreCase("paint_color_batches")) kindObjective = 7;
                else throw new IllegalStateException("Cas inconnu : " + objectives.toString());
                
            }else{
                if(!objectives[0].equalsIgnoreCase("paint_color_batches") || !objectives[1].equalsIgnoreCase("high_priority_level_and_easy_to_satisfy_ratio_constraints"))
                    throw new IllegalStateException("Cas inconnu : " + objectives.toString());
                else kindObjective = 5;
            }
        }else{
            isExistLowPriorityConstraints = true;
            
            
            if(objectives[0].equalsIgnoreCase("high_priority_level_and_easy_to_satisfy_ratio_constraints")){
                if(objectives[1].equalsIgnoreCase("paint_color_batches")) kindObjective = 1;
                else if(objectives[1].equalsIgnoreCase("low_priority_level_ratio_constraints")) kindObjective = 2;
                else throw new IllegalStateException("Cas inconnu : " + objectives.toString());
                
            }else if(objectives[0].equalsIgnoreCase("high_priority_level_and_difficult_to_satisfy_ratio_constraints")){
                if(objectives[1].equalsIgnoreCase("paint_color_batches")) kindObjective = 6;
                else if(objectives[1].equalsIgnoreCase("low_priority_level_ratio_constraints")) kindObjective = 8;
                else throw new IllegalStateException("Cas inconnu : " + objectives.toString());
                
            }else{
                if(!objectives[0].equalsIgnoreCase("paint_color_batches") 
                        || !objectives[1].equalsIgnoreCase("high_priority_level_"
                                + "and_easy_to_satisfy_ratio_constraints")
                        || !objectives[2].equalsIgnoreCase("low_priority_level_ratio_constraints"))
                    throw new IllegalStateException("Cas inconnu : " + objectives.toString());
                else kindObjective = 4;
            }
        }
        
        this.objectives = objectives;
        
    }

    public boolean areEasyToSatisfyConstraints() {
        return areEasyToSatisfyConstraints;
    }

    public boolean isExistLowPriorityConstraints() {
        return isExistLowPriorityConstraints;
    }

    public String[] getObjectives() {
        return objectives;
    }

    public int getKindObjective() {
        return kindObjective;
    }
    
    /**
     * Warning : the result is given in order : High prio, low prio, paint batches...
     * <p>
     * Particularly : it's not necessarily the same order than for objectives[]...
     * @return Objective multipliers.
     */
    public int[] getMultForCompute(){
        int[] result = new int[3]; //result[0] --> High prioritylevel, result[1] --> low priority level, result[2] --> paint color batches
        switch(kindObjective){
            case 1:
                result[0] = 1000000; 
                result[1] = 1; 
                result[2] = 1000;
                break;
            case 2:
                result[0] = 1000000; 
                result[1] = 1000; 
                result[2] = 1;
                break;
            case 3:
                result[0] = 1000000; 
                result[1] = 0; 
                result[2] = 1000;
                break;
            case 4:
                result[0] = 1000; 
                result[1] = 1; 
                result[2] = 1000000;
                break;
            case 5:
                result[0] = 1000; 
                result[1] = 0; 
                result[2] = 1000000;
                break;
            case 6:
                result[0] = 1000000; 
                result[1] = 1; 
                result[2] = 1000;
                break;
            case 7:
                result[0] = 1000000; 
                result[1] = 0; 
                result[2] = 1000;
                break;
            case 8:
                result[0] = 1000000; 
                result[1] = 1000; 
                result[2] = 1;
                break;
            default:
                result[0] = Integer.MAX_VALUE; 
                result[1] = Integer.MAX_VALUE; 
                result[2] = Integer.MAX_VALUE;
                throw new IllegalStateException("Unknow case");
        }
        
        return result;
        
    }
    
    @Override
    public String toString(){
        String temp = "Objectives order :\n";
        for(String str : objectives){
            temp += str + "\n";
        }
        return temp;
    }
    
}
