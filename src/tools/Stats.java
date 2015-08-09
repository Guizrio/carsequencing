package tools;

import java.text.DecimalFormat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *Classe pour faire des statistiques avec des temps et des valeurs
 * <p>
 * permet d'établir des stats au fur et à mesure sur le temps de résolution
 * d'une instance, la valeur de la fonction objectif et la capacité qu'il reste 
 * dans le sac après remplissage.
 * <p>
 * Les calculs de moyennes et de variances ne sont effectués qu'à l'appel des méthodes
 * les retournant.
 * 
 * @author Guillaume
 */
public class Stats {
    private long[] tMoy = new long[2];
    private long[] tVar = new long[2];
    private long tMin;
    private long tMax;
    
    private double[] zMoy = new double[2];
    private double[] zVar = new double[2];
    private double zMin;
    private double zMax;
    
    
    private String[] paramsUtilises;
    private DecimalFormat ft = new DecimalFormat(); //To truncate numbers...
    
    public Stats(int nbDigitsToDisplay) {
        double[] initTab = {0.0, 0.0};
        long[] initTabT = {0, 0};
        
        System.arraycopy(initTabT, 0, tMoy, 0, initTabT.length);
        System.arraycopy(initTabT, 0, tVar, 0, initTabT.length);
        tMin=Long.MAX_VALUE;
        tMax=Long.MIN_VALUE;
        
        System.arraycopy(initTab, 0, zMoy, 0, initTab.length);
        System.arraycopy(initTab, 0, zVar, 0, initTab.length);
        zMin=Double.POSITIVE_INFINITY;
        zMax=Double.NEGATIVE_INFINITY;
        
        paramsUtilises=null;
        
        ft.setMaximumFractionDigits(nbDigitsToDisplay);
        
    }
    
    //=========================================================================
    //Les setters
    public void setT(long temps){
        tMoy[0] += temps;
        tMoy[1]++;
        
        tVar[0] += temps*temps/1000000000000L;
        tVar[1]++;
        
        tMin = (tMin > temps ? temps : tMin);
        tMax = (tMax < temps ? temps : tMax);
    }
    
    public void setZ(double z){
        zMoy[0] += z;
        zMoy[1]++;
        
        zVar[0] += z*z;
        zVar[1]++;
        
        zMin = (zMin > z ? z : zMin);
        zMax = (zMax < z ? z : zMax);
    }
    
    
    
    public void setParams(String[] params){
        paramsUtilises = new String[params.length];
        System.arraycopy(params, 0, paramsUtilises, 0, params.length);
    }
    //=========================================================================
    
    
    //Les Getters (ordre T, Z, C)
    public long getTmin(){
        return tMin;
    }
    
    public double getZmin(){
        return zMin;
    }
    
    public long getTmax(){
        return tMax;
    }
    
    public double getZmax(){
        return zMax;
    }
    
    
    public long gettMoy() {
        if(tMoy[1] == 0){
            throw new ArithmeticException(  "Il n'y a pas d'éléments pour"
                                            + " effectuer une moyenne");
        }else{
            return (long)( (double)tMoy[0]/(double)tMoy[1] );
        }
    }
    
    public double getzMoy() {
        if(zMoy[1] == 0.0){
            throw new ArithmeticException(  "Il n'y a pas d'éléments pour"
                                            + " effectuer une moyenne");
        }else{
            return (double)zMoy[0]/(double)zMoy[1];
        }
    }
    
    
    public double gettVar() {
        if(tVar[1] == 0.0){
            throw new ArithmeticException(  "Il n'y a pas d'éléments pour"
                                            + " effectuer une moyenne");
        }else{
            double m = this.gettMoy();
            return ( (double)tVar[0] / (double)tVar[1] ) - m*m/(double)1000000000000L;
        }
    }
    
    public double getzVar() {
        if(zVar[1] == 0.0){
            throw new ArithmeticException(  "Il n'y a pas d'éléments pour"
                                            + " effectuer une moyenne");
        }else{
            double m = this.getzMoy();
            return ( zVar[0] / zVar[1] ) - m*m;
        }
    }
    
    public String[] getParams(){
        return paramsUtilises;
    }
    
    
    public String result(){
        String result = "Temps moyen : " + new Time(gettMoy()) + "\t (min = "+ new Time(tMin) + ", max = " + new Time(tMax)+", écart_type = "+ ft.format(Math.sqrt(gettVar())).replace(',', '.')+")\n"
                        +"Valeur Objectif moyenne : " + ft.format(getzMoy()).replace(',', '.') + "\t (min = "+ ft.format(zMin).replace(',', '.') + ", max = " + ft.format(zMax).replace(',', '.')+", écart_type = "+ ft.format(Math.sqrt(getzVar())).replace(',', '.')+")";
        
        return result;
    }
    
    
    public static void main(String[] args) {
        Stats stat = new Stats(2);
        stat.setT(0);
        stat.setT(1);
        stat.setT(2);
        
        System.out.println(stat.gettMoy());
        System.out.println(stat.gettVar());
        
        System.out.println(stat.getTmax());
        System.out.println(stat.getTmin());
    }
    
}
