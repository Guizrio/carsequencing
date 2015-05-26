/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

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
                        || !objectives[1].equalsIgnoreCase("low_priority_level_ratio_constraints"))
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
    
    public int[] getMultForCompute(){
        int[] result = new int[3]; //result[0] --> High prioritylevel, result[1] --> low priority level, result[2] --> paint color batches
        switch(kindObjective){
            case 1:
                result[0] = 10000; 
                result[1] = 1; 
                result[2] = 100; 
            case 2:
                result[0] = 10000; 
                result[1] = 100; 
                result[2] = 1;
            case 3:
                result[0] = 10000; 
                result[1] = 0; 
                result[2] = 100;
            case 4:
                result[0] = 100; 
                result[1] = 1; 
                result[2] = 10000;
            case 5:
                result[0] = 100; 
                result[1] = 0; 
                result[2] = 10000;
            case 6:
                result[0] = 10000; 
                result[1] = 1; 
                result[2] = 100;
            case 7:
                result[0] = 10000; 
                result[1] = 0; 
                result[2] = 100;
            case 8:
                result[0] = 10000; 
                result[1] = 100; 
                result[2] = 1;
                
//            case else: //Partie à rajouter quand on aura internet... (doit renvoyer une erreur)
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
