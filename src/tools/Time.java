/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import static java.lang.Thread.sleep;

/**
 *Class use to save time and return with different format, or to time applications.
 * <p>
 * les temps entrés doivent être en nanosecondes.<p>
 *ATTENTION car les temps sont arrondi à la milliseconde (convention 0,5 arrondi à 1)
 * Cela signifie qu'un temps passé en paramètres en nanosecondes égal à 10,5 millisecondes sera
 * redonné par la méthode getNbMilliSec() comme égal à 11 millisecondes.
 * <p>
 * Note : le nombre d'heures partielles est égal au nombre d'heures totales...
 * <p>
 * Si on souhaite reconstruire un temps customizé, les fonctions getPartials*() sont utiles.
 * <p>
 * Pour avoir des troncatures à une unité choisie, les fonctions getNb*() sont utiles.
 * <p>
 * Les méthodes timeElapsed*() modifient le time de la classe et servent de chronomètre,
 * ou pour calculer un espacement de temps entre deux temps.
 * 
 * @author Guillaume
 */
public class Time {
    
    private long time;
    private long lastSaveTime;
    
    //variables dont la somme constitue le temps total
    private long hours;
    private int minutes;
    private int secondes;
    private int millisecondes;
    
    //Variables de temps total
    private long totalMinutes;
    private long totalSecondes;
    private long totalMilliSec;
    
    
    /**
     * Contruct a new Time from the current JVM time.
     */
    public Time(){
        this.time = System.nanoTime();
        
        this.hours = 0l;
        this.minutes = 0;
        this.secondes = 0;
        this.millisecondes = 0;
        
        this.totalMinutes = 0l;
        this.totalSecondes = 0l;
        this.totalMilliSec = 0l;
        convertTime();
    }
    
    /**
     * Construct the new Time from a time in nanosecond.
     * @param time a nanosecond time to be converted.
     */
    public Time(long time){
        this.time = time;
        
        this.hours = 0l;
        this.minutes = 0;
        this.secondes = 0;
        this.millisecondes = 0;
        
        this.totalMinutes = 0l;
        this.totalSecondes = 0l;
        this.totalMilliSec = 0l;
        convertTime();
    }
    
    /**
     * Function to initialize all variables except time.
     */
    private void convertTime(){
        long reste = time / 100000l;
        
        hours = reste / 36000000l;
        reste -= hours * 36000000l;
        minutes = (int) (reste / 600000l);
        
        totalMinutes = hours*60l + minutes;
        
        reste -= (long)minutes * 600000l;
        secondes = (int) (reste / 10000l);
        
        totalSecondes = totalMinutes * 60l + secondes;
        
        reste -= (long)secondes * 10000l;
        millisecondes = (int)Math.round((double)reste / (double)10);
        
        totalMilliSec = totalSecondes * 1000l + (long)millisecondes;
        lastSaveTime = time;
    }
    
    /**
     * Display the time elapsed since value initialization in constructor
     * or last timeElaps*() call.
     * <p>
     * If no timeElaps*() call was done between initialisation and now, then :
     * <MENU>
     * if this instance was created with empty construtor, it display time
     * elapsed between the creation of this instance and now.
     * <p>
     * if this instance was created with constructor Time(long #time), it display
     * time elapsed between #time and now.
     * </MENU>
     * Otherwise, it display time elapsed since last call of timeElaps*() method.
     * <p>
     * after the call the current time of this instance is set to the time of end of call,
     * but there aren't recalculation of all other variables, so it's possible after
     * a timeElaps*() call (let be T1) to use methods to get hours, minutes,... and they would give
     * the time between "creation of instance or #time or last timeElaps*() call" and this timeElaps*()
     * call (T1), in their respective unit.
     * 
     * @return time between (creation of instance or #time or last timeElaps*() call) and now.
     */
    public String timeElapsed(){
        this.time = System.nanoTime() - this.time;
        convertTime();
        this.time = System.nanoTime();
        return toString();
    }
    
    /**
     * Display the time elapsed since timeStart.
     * <p>
     * after the call the current time of this instance is set to the time of end of call.
     * but there aren't recalculation of all other variables, so it's possible after
     * a timeElaps*() call (let be T1) to use methods to get hours, minutes,... and they would give
     * the time between "timeStart" and this timeElaps*() call (T1), in their respective unit.
     * 
     * @param timeStart the precedent time in nanosecond.
     * @return time elapsed between timeStart and now.
     */
    public String timeElapsedSince(long timeStart){
        this.time = System.nanoTime() - timeStart;
        convertTime();
        this.time = System.nanoTime();
        return toString();
    }
    
    public long timeLongElapsedSince(long timeStart){
        this.time = System.nanoTime() - timeStart;
        convertTime();
        this.time = System.nanoTime();
        return lastSaveTime;
    }
    
    /**
     * Display the time elapsed between two times.
     * <p>
     * after the call the current time of this instance is set to the time of end of call.
     * but there aren't recalculation of all other variables, so it's possible after
     * a timeElapsedBetween("timeStart", "timeEnd") call (let be T1) to use methods to get hours, minutes,...
     * and they would give the time between "timeStart" and "timeEnd", in their respective unit.
     * 
     * @param timeStart the start time in nanoseconds
     * @param timeEnd the end time in nanoseconds
     * @return time elapsed between timeStart and timeEnd.
     */
    public String timeElapsedBetween(long timeStart, long timeEnd){
        this.time = timeEnd - timeStart;
        convertTime();
        this.time = System.nanoTime();
        return toString();
    }
    
    /**
     * Return last saved time in nanoseconds.
     * <p>
     * It correspond to time given by methods getNb*() and getPartial*()
     * @return last saved time in nanoseconds.
     */
    public long getLastSavedTime(){
        return lastSaveTime;
    }
    
    /**
     * Get the total time in hours.
     * <p>
     * Note : all time under hour is not displayed (1h59m give 1 hour)
     * <p>
     * This method doesn't have a dual PartialNbhours() method because
     * hour is the highest range in time use here. So they are a same method.
     * 
     * @return total time in hours.
     */
    public long getNbHours(){
        return hours;
    }
    
    /**
     * Get the total time in minutes.
     * <p>
     * Note : all time under minute is not displayed (1m59s give 1 minute)
     * <p>
     * * To reconstruct time from this instance, use Partials method instead.
     * 
     * @return the total time in minutes.
     */
    public long getNbMinutes(){
        return totalMinutes;
    }
    
    /**
     * Get the total time in seconds.
     * <p>
     * Note : all time under second is not displayed (1s999ms give 1 second)
     * <p>
     * To reconstruct time from this instance, use Partials method instead.
     * 
     * @return the total time in seconds. 
     */
    public long getNbSecondes(){
        return totalSecondes;
    }
    
    /**
     * Get the total time in milliseconds.
     * @return the total time in milliseconds.
     */
    public long getNbMilliSec(){
        return totalMilliSec;
    }
    
    
    /**
     * Get partial time correspond to m in the time
     * h:m:s:ms.
     * 
     * @return partial time correspond to m in the time h:m:s:ms.
     */
    public int getPartialNbMinutes(){
        return minutes;
    }
    
    
    /**
     * Get partial time correspond to s in the time
     * h:m:s:ms.
     * 
     * @return partial time correspond to s in the time h:m:s:ms.
     */
    public int getPartialNbSeconds(){
        return secondes;
    }
    
    
    /**
     * Get partial time correspond to ms in the time
     * h:m:s:ms.
     * 
     * @return partial time correspond to ms in the time h:m:s:ms.
     */
    public int getPartialMilliSec(){
        return millisecondes;
    }
    
    @Override
    public String toString(){
        
        if(hours != 0l){
            
            return hours + "h " + minutes + "m " + secondes + "s " + millisecondes +"ms";
            
        }else if(minutes != 0l){
            
            return minutes + "m " + secondes + "s " + millisecondes +"ms";
            
        }else if(secondes != 0l){
            
            return secondes + "s " + millisecondes +"ms";
            
        }else{
            
            return millisecondes +"ms";
        }
        //return hours + "h " + minutes + "m " + secondes + "s " + millisecondes +"ms";
    }
    
    public static void main(String[] args) throws InterruptedException {
        
        long time = 120*1000000000l*3600l + 59*1000000000l*60l + 4*1000000000l + 3*100000000l + 8*10000000l + 9*1000000l + 1*499999l; //le dernier est en dessous de la millisecondes
        Time timeconvert = new Time(time);
        
        System.out.println(timeconvert);
        System.out.println(new Time(timeconvert.getLastSavedTime()));
        System.out.println("Total time (tronqued): ");
        System.out.println("\thours : " + timeconvert.getNbHours());
        System.out.println("\tminutes : " + timeconvert.getNbMinutes());
        System.out.println("\tseconds : " + timeconvert.getNbSecondes());
        System.out.println("\tmilliseconds " + timeconvert.getNbMilliSec());
        System.out.println("Partials times : ");
        System.out.println("\thours : " + timeconvert.getNbHours());
        System.out.println("\tminutes : " + timeconvert.getPartialNbMinutes());
        System.out.println("\tseconds : " + timeconvert.getPartialNbSeconds());
        System.out.println("\tmilliseconds " + timeconvert.getPartialMilliSec());
        System.out.println("===============================================");
        System.out.println("Chrono :");
        timeconvert.timeElapsed(); // ou timeconvert = new Time();
        
        System.out.println(timeconvert.timeElapsed());
        System.out.println(new Time(timeconvert.getLastSavedTime()));
        System.out.println("Total time (tronqued): ");
        System.out.println("\thours : " + timeconvert.getNbHours());
        System.out.println("\tminutes : " + timeconvert.getNbMinutes());
        System.out.println("\tseconds : " + timeconvert.getNbSecondes());
        System.out.println("\tmilliseconds " + timeconvert.getNbMilliSec());
        System.out.println("Partials times : ");
        System.out.println("\thours : " + timeconvert.getNbHours());
        System.out.println("\tminutes : " + timeconvert.getPartialNbMinutes());
        System.out.println("\tseconds : " + timeconvert.getPartialNbSeconds());
        System.out.println("\tmilliseconds " + timeconvert.getPartialMilliSec());
        System.out.println("===============================================");
        
        sleep(1051); //methode sleep peu précise mais c'est pour l'idée ! =)
        
        System.out.println("");
        System.out.println(timeconvert.timeElapsed());
        System.out.println(new Time(timeconvert.getLastSavedTime()));
        System.out.println("Total time (tronqued): ");
        System.out.println("\thours : " + timeconvert.getNbHours());
        System.out.println("\tminutes : " + timeconvert.getNbMinutes());
        System.out.println("\tseconds : " + timeconvert.getNbSecondes());
        System.out.println("\tmilliseconds " + timeconvert.getNbMilliSec());
        System.out.println("Partials times : ");
        System.out.println("\thours : " + timeconvert.getNbHours());
        System.out.println("\tminutes : " + timeconvert.getPartialNbMinutes());
        System.out.println("\tseconds : " + timeconvert.getPartialNbSeconds());
        System.out.println("\tmilliseconds " + timeconvert.getPartialMilliSec());
        System.out.println("===============================================");
        
        timeconvert = new Time();
        
        sleep(10);    //methode sleep peu précise mais c'est pour l'idée ! =)
        System.out.println("Time elapsed : ");
        System.out.println(timeconvert.timeElapsed());
        System.out.println(new Time(timeconvert.getLastSavedTime()));
        
        sleep(10);  //methode sleep peu précise mais c'est pour l'idée ! =)
        System.out.println("Time elapsed : ");
        System.out.println(timeconvert.timeElapsed());
        System.out.println(new Time(timeconvert.getLastSavedTime()));
        
        sleep(10);  //methode sleep peu précise mais c'est pour l'idée ! =)
        System.out.println("Time elapsed : ");
        System.out.println(timeconvert.timeElapsed());
        System.out.println(new Time(timeconvert.getLastSavedTime()));
        System.out.println("");
        
        long time1 = 3*1000000000l*3600l + 25*1000000000l*60l;
        time = 3*1000000000l*3600l + 26*1000000000l*60l + 15*1000000000l;
        System.out.println("time1 = " + new Time(time1));
        System.out.println("time = " + new Time(time));
        System.out.println("Time elapsed between time1 and time: ");
        System.out.println(timeconvert.timeElapsedBetween(time1, time));
        System.out.println(new Time(timeconvert.getLastSavedTime()));
        
        
    }
    
}
