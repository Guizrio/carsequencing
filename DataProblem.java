/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsequencing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to initialize data of a car sequencing problem.
 * <p>
 * 
 * The problem is to shedule cars production in order to minimize differents
 * ratios violations, and cleaning of painting machines.<p>
 *
 * Each car have a day of production, which could be number J or J-1. Each car
 * of day J-1 are already produced, and each car of day J aren't produced yet
 * (we are at start of day J). So only cars of day J could be sheduled, but cars
 * produced in day J-1 could make violations with firts cars of day J
 * sheduled.<p>
 *
 * A painting machine have to be cleaned up if the next car should have a
 * different color than the precedent, but this machine have to be cleaned up
 * also if there is more than X cars with same color. Cars with sames
 * characteristics should be away from each other. But cars with same color must
 * be sheduled consecutively to minimize number of cleaning operations.<p>
 *
 * The data must be extracted from 4 differents files :
 *
 * <MENU>
 * 1. "optimization_objectives.txt" <BR>
 * 2. "paint_batch_limit.txt" <BR>
 * 3. "ratios.txt" <BR>
 * 4. "vehicles.txt" <BR>
 * </MENU>
 *
 * The first contains the priority order of objectives (ratios violation and
 * max painting number).<p>
 *
 * The second contains the maximum car number that a machine could paints with
 * the same color before being cleaned up.<p>
 *
 * The third contains the maximum ratios of cars which could be sheduled in a
 * free window without cause extra delay in production. A ratio determine the
 * window size.<p>
 *
 * The fourth contains cars' description. Each car must be produced at the
 * repective day given in this file.<p>
 * 
 * Format of files must be like those described at http://challenge.roadef.org/2005/fr/sujet.php
 * and more precisely in the description of file http://challenge.roadef.org/2005/files/description_pb_car_sequencing_FR.doc
 * 
 *
 * @author Guillaume
 */
public class DataProblem {
    
    private String folderPath;
    private final String[] nameFiles = {"optimization_objectives.txt", 
            "paint_batch_limit.txt", "ratios.txt", "vehicles.txt"};
    private ArrayList<RatioConstraint> ratConst;
    private ArrayList<RatioConstraint> highConst;
    private ArrayList<RatioConstraint> lowConst;
    private int nbConst;
    private int maxSamePainting;
    private ArrayList<Car> cars;            //All cars (contains those who need to be sequenced (day J) and those who doesn't (day J-1))
    private ArrayList<Car> haveToBeSheduledcars;    //All cars of day J
    private ArrayList<Car> haveNotToBeSheduledcars; //All cars of day J-1
    private int nbHighPrioConstraints;
    private int nbLowPrioConstraints;
    private boolean areEasySatisfyRatioConstraints;
    private ClassObjective classObjective;
    private Date DateJ;
    private int nbCarsDayJ;
    private int nbCarsDayJMinus1;
    private int nbCars;             //total number of cars (day J and J-1)
//    private int[] windowSizes;
//    private int[] maxCarInWindows;
    
    
    /**
     * Class for filtering files name extensions.
    */
    private class Filter implements FilenameFilter
    {
        @Override
        public boolean accept(File rep, String fichier) {

            if (fichier.endsWith(".txt")){
                return true;
            }
            return false;
        }
    }
    
    /**
     * Construct a new instance problem from data where come from folderPath path.
     * <p>
     * The folder must contain 4 files like those described at the top of this class.
     * <p>
     * @param folderPath the path where files are.
     */
    public DataProblem(String folderPath){
        
        File file = new File(folderPath);
        
        //Ensure folder path
        if (!file.exists()) throw new IllegalArgumentException("folder : " 
                + folderPath + " does not exist");
        if(!file.isDirectory()) throw new IllegalArgumentException("path : " 
                + folderPath + " must be a folder path and not a file path");
        
        //Ensure goods files are present
        String[] files = file.list(new Filter());
        
        if(files.length < 4) throw new IllegalArgumentException("folder : " 
                + folderPath + " does not contain minimum number necessary file.");
        
        
        boolean isPresentFile[] = {false, false, false, false};
        
        
        for(String nameFile : files){
            for (int i = 0; i < isPresentFile.length; i++) {
                if (nameFile.contains(nameFiles[i])) isPresentFile[i]=true;
            }            
        }
        
        for (int i = 0; i < isPresentFile.length; i++) {
            if (!isPresentFile[i]) throw new IllegalArgumentException("folder : " 
                + folderPath + " Does not contain file : " 
                + nameFiles[i] );
        }        
        
        this.folderPath = folderPath;
        
        //Now load datas from files
        loadObjectives();
        loadRatios();
        loadCars();
        loadMaxConsecutivePaint();
        
        int nbTempDate=0;
        
        DateJ = cars.get(0).getDat();        
        
        for (Car car : cars) {
            nbTempDate += cars.get(0).getDat().compareTo(car.getDat());
            if(DateJ.before(car.getDat())) DateJ = car.getDat();
        }
        
        haveNotToBeSheduledcars = new ArrayList<>();
        haveToBeSheduledcars = new ArrayList<>();
        
        for (Car car : cars) {
            if(car.getDat().compareTo(DateJ)==0) haveToBeSheduledcars.add(car);
            else haveNotToBeSheduledcars.add(car);
        }
        
        if(nbTempDate >= 0){
            nbCarsDayJ = cars.size() - nbTempDate;
            nbCarsDayJMinus1 = cars.size() - nbCarsDayJ;
        }else{
            nbCarsDayJ = Math.abs(nbTempDate);
            nbCarsDayJMinus1 = cars.size() - nbCarsDayJ;
        }
        
        highConst = new ArrayList<>();
        lowConst = new ArrayList<>();
        for (RatioConstraint ratConst : ratConst) {
            if(ratConst.isIsPrioritary()) highConst.add(ratConst);
            else lowConst.add(ratConst);
        }
        
        nbCars = cars.size();
        
    }
    
    private boolean loadObjectives(){
        
        File file = new File(folderPath + File.separator  + nameFiles[0]);
        
        try {
            Scanner scan = new Scanner(file);
            scan.useDelimiter(";");
            
            if(scan.hasNext()) scan.next();
            if(scan.hasNext()) scan.next();
            
            ArrayList<String> objectives = new ArrayList<>();
            
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] seqLineline = line.split(";");
                
                if(seqLineline.length ==2) objectives.add(seqLineline[1]);
                
            }
            
            classObjective = new ClassObjective( objectives.toArray(new String[objectives.size()]));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataProblem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    private boolean loadRatios(){
        
        ratConst = new ArrayList<>();
        
        File file = new File(folderPath + File.separator  + nameFiles[2]);
        
        try {
            Scanner scan = new Scanner(file);
            scan.useDelimiter(";");
            
            if(scan.hasNext()) scan.next();
            if(scan.hasNext()) scan.next();
            if(scan.hasNext()) scan.next();
            
            RatioConstraint rat;
            String ratio;
            int windowSize;
            int maxCarInWindow;
            boolean isPrioritary;
            String name;
            
            nbHighPrioConstraints =0;
            nbLowPrioConstraints=0;
            
            int nbCurrLigne = 1;
            
            while(scan.hasNextLine()){
                nbCurrLigne++;
                String line = scan.nextLine();
                String[] seqLineline = line.split(";");
                
                if(seqLineline.length ==3){
                    
                    ratio = seqLineline[0];
                    
                    String[] temp = ratio.split("/");
                    if(temp.length != 2) throw new IllegalStateException("could not understand ratio : " + ratio);
                    maxCarInWindow = Integer.parseInt(temp[0]);
                    windowSize = Integer.parseInt(temp[1]);
                    
                    isPrioritary = Integer.parseInt(seqLineline[1])==1;
                    
                    if(isPrioritary) nbHighPrioConstraints++;
                    else nbLowPrioConstraints++;
                    
                    name = seqLineline[2];
                    
                    ratConst.add(new RatioConstraint(ratio, windowSize, maxCarInWindow, isPrioritary, name));
                    
                }else if(seqLineline.length > 1) throw new IllegalStateException("The file : " 
                        + file.getAbsolutePath() 
                        + "is not in a good format because it has " 
                        + seqLineline.length 
                        + " values at line number " + nbCurrLigne);
                else{
                    System.out.println("load ratios : ligne non valide...");
                }
                
                nbConst = ratConst.size();
                        
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataProblem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    private boolean loadCars(){
        
        cars = new ArrayList<>();
        
        File file = new File(folderPath + File.separator  + nameFiles[3]);
        
        try {
            Scanner scan = new Scanner(file);
            scan.useDelimiter(";");
            
           
            //Verify if constraints are in good order
            if(scan.hasNextLine()){
                String[] firstLigne = scan.nextLine().split(";");
                
                int nbNoConstField = 4; //number of fields which aren't constraint field
                
                if(firstLigne.length-nbNoConstField != nbConst) 
                    throw new IllegalStateException("Number of constraints does not match");
                        
                for (int i = nbNoConstField; i < nbConst + nbNoConstField; i++) {
                    
                    if(!firstLigne[i].equalsIgnoreCase(ratConst.get(i-nbNoConstField).getName()))
                        throw new IllegalStateException("Case Not Supported yet "
                                + "(constraints have to be in the same order"
                                + " in the different files)");
                }
                
            }else throw new IllegalStateException("No first ligne found");            
    
            
            int nbCurrLigne = 1;
            
            Date dat;
            long seqDep;
            long id;
            int paintColor;
            
            while(scan.hasNextLine()){
                nbCurrLigne++;
                String line = scan.nextLine();
                String[] seqLineline = line.split(";");
                
                if(seqLineline.length == (4+nbConst)){
                    
                    dat= null;
                    
                    try {
                        dat = new SimpleDateFormat("yyyy ww u").parse(seqLineline[0]);
                    } catch (ParseException ex) {
                        System.err.println("Date : " + seqLineline[0] + " format not supported");
                        Logger.getLogger(DataProblem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    seqDep = Long.parseLong(seqLineline[1]);
                    id = Long.parseLong(seqLineline[2]);
                    paintColor = Integer.parseInt(seqLineline[3]);
                    
                    Car car = new Car(dat, seqDep, id, paintColor);
                    
                    for (int i = 0; i < nbConst; i++) {
                        if(Integer.parseInt(seqLineline[4+i])==1){
                            car.addRationConstraint(ratConst.get(i));
                        }
                    }
                    
                    cars.add(car);
                    
                }else if(seqLineline.length > 1) throw new IllegalStateException("The file : " 
                        + file.getAbsolutePath() 
                        + "is not in a good format because it has " 
                        + seqLineline.length 
                        + " values at line number " + nbCurrLigne);
                else{
                    System.out.println("load Cars : ligne non valide...");
                }
                        
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataProblem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    private boolean loadMaxConsecutivePaint(){
        
        File file = new File(folderPath + File.separator  + nameFiles[1]);
        
        try {
            Scanner scan = new Scanner(file);
            scan.useDelimiter(";");
            
            if(scan.hasNext()) scan.next();
            
            int nbCurrLigne = 1;
            
            while(scan.hasNextLine()){
                nbCurrLigne++;
                String line = scan.nextLine();
                String[] seqLineline = line.split(";");
                
                if(seqLineline.length ==1) maxSamePainting = Integer.parseInt(seqLineline[0]);
                else System.out.println("load Max consecutive paintings : ligne non valide...");
            }
            
            if (nbCurrLigne > 3) throw new IllegalStateException("File " 
                    + file.getAbsolutePath() +" have more than 2 lignes...");
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataProblem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String[] getNameFiles() {
        return nameFiles;
    }

    public ArrayList<RatioConstraint> getRatConst() {
        return ratConst;
    }

    public int getNbConst() {
        return nbConst;
    }

    public int getMaxSamePainting() {
        return maxSamePainting;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public int getNbHighPrioConstraints() {
        return nbHighPrioConstraints;
    }

    public int getNbLowPrioConstraints() {
        return nbLowPrioConstraints;
    }

    public boolean areEasySatisfyRatioConstraints() {
        return areEasySatisfyRatioConstraints;
    }

    public ClassObjective getClassObjective() {
        return classObjective;
    }

    public boolean isAreEasySatisfyRatioConstraints() {
        return areEasySatisfyRatioConstraints;
    }

    public int getNbCarsDayJ() {
        return nbCarsDayJ;
    }

    public int getNbCarsDayJMinus1() {
        return nbCarsDayJMinus1;
    }

    public int getNbCars() {
        return nbCars;
    }

    public ArrayList<RatioConstraint> getHighConst() {
        return highConst;
    }

    public ArrayList<RatioConstraint> getLowConst() {
        return lowConst;
    }

    public ArrayList<Car> getHaveToBeSheduledcars() {
        return haveToBeSheduledcars;
    }

    public ArrayList<Car> getHaveNotToBeSheduledcars() {
        return haveNotToBeSheduledcars;
    }

    public Date getDateJ() {
        return DateJ;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void main(String[] args) {
        String folderPath = new File("").getAbsolutePath()  + File.separator 
                                                    + "Instances_set_A"
                                                    + File.separator   
                                                    +"Instances"
                                                    + File.separator
                                                    + "022_3_4_EP_RAF_ENP";
        
        
        DataProblem dat = new DataProblem(folderPath);
        
        ArrayList<RatioConstraint> rat = dat.getRatConst();
        System.out.println(rat);
        
        Collections.sort(rat, Collections.reverseOrder());
        
        System.out.println(rat);
        
        System.out.println(rat.get(2).compareTo(rat.get(3)));
        System.out.println(rat.get(3).compareTo(rat.get(2)));
        
        System.out.println("testDataEnd");
        
    }
    
}
