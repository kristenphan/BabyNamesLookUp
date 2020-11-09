import edu.duke.*;
import org.apache.commons.csv.*;
import java.io.File;

/**
 * https://www.coursera.org/learn/java-programming/supplement/VeM7L/miniproject-exercise-guide
 */
public class Part1 {
    public void totalBirths(FileResource fr) {
        /**
         * Calculate and print the number of boy/girl births and boy/girl names in file
         */
        // calculate the number of boy/girl births and boy/girl names in file
        int totalBirths = 0;
        int totalBoys = 0;
        int totalGirls = 0;
        int invalidRec = 0;
        int girlNames = 0;
        int boyNames = 0;
        int totalNames = 0;
        for (CSVRecord rec: fr.getCSVParser(false)) {
            int numBorn = Integer.parseInt(rec.get(2));
            String gender = rec.get(1).trim().toUpperCase();
            if (gender.equals("M")) {
                totalBoys += numBorn;
                boyNames++;
            }
            else if (gender.equals("F")) {
                totalGirls += numBorn;
                girlNames++;
            }
            else {
                invalidRec++;
            }
        }
        
        totalBirths = totalGirls + totalBoys;
        totalNames = girlNames + boyNames;
        
        // return a message if any invalid records found
        if (invalidRec > 0) {
            System.out.println("totalBirths: invalid records with invalid genders found!");
            return;
        }
        
        // print the results
        System.out.println("Total girls = " + totalGirls);
        System.out.println("Total boys = " + totalBoys);
        System.out.println("Total births = " + totalBirths);
        System.out.println("Total girl names = " + girlNames);
        System.out.println("Total boy names = " + boyNames);
        System.out.println("Total names = " + totalNames);
    }
   
    
    public void testTotalBirths() {
        /** 
         * Test totalBirths method
         */
        FileResource fr = new FileResource();
        totalBirths(fr);
    }
    
    
    public int getRank(int year, String name, String gender, FileResource fr) {
        /** 
         * Return the rank of the name in the file (repr"fr") for the given gender ("F" for female and "M" for male), 
         * where rank 1 is the name with the largest number of births in the given year.
         * If the name is not in the file, then return -1.
         */
       
        // iterate through all records and find the rank of the given name
        int rank = 1;
        for (CSVRecord rec: fr.getCSVParser(false)) {
            String currGender = rec.get(1);
            if (currGender.equals(gender)) {
                String currName = rec.get(0);
                if (currName.equals(name)) {
                    return rank;
                }
                else {
                    rank++;
                }
            }
        }
                
        // The name is not in the file, return -1
        return -1;
    }
    
    
    public void testGetRank() {
        /**
         * Tet getRank() method
         */
        int year = 1971;
        String name = "Frank";
        String gender = "M";
        String filePath = "data/us_babynames_by_year/yob" + year + ".csv";
        FileResource fr = new FileResource(filePath);
        int rank = getRank(year, name, gender, fr);
        System.out.println("The rank of the name " + name + " in year " + year + ": " + rank);
    }
    
    
    public String getName(int year, int rank, String gender) {
        /**
         * Return the name of the person in the file at this rank, for the given gender
         * ("F" for female, "M" for male), where rank 1 is the name with the largest number of births
         * for the given gender.
         * If the rank does not exist in the file, return "NO NAME" 
         */
        
        // select a file to look up the rank
        String filePath = "data/us_babynames_by_year/yob" + year + ".csv";
        FileResource fr = new FileResource(filePath);
        // FileResource fr = new FileResource();
        
        // initialize the variable to keep track of the rank of the current CSVRecord
        int currRank = 1;
        for (CSVRecord rec: fr.getCSVParser(false)) {
            String currGender = rec.get(1);
            if (currGender.equals(gender)) { // found records matching with gender
                if (currRank == rank) { // found a record matching with rank
                    return rec.get(0);
                }
                else {
                    currRank++;
                }
            }
        }
        
        // no match found, return "NO NAME"
        return "NO NAME";
    }
    
    
    public void testGetName() {
        /**
         * Test getName() method
         */
        int year = 1982;
        int rank = 450;
        String gender = "M";
        String name = getName(year, rank, gender);
        System.out.println("The name in rank " + rank + " in year " + year + 
                            " and gender " + gender + ": " + name);
    }
    
    
    public String whatIsNameInYear(String name, int year, int newYear, String gender) {
        /**
         * Return what name would have been named if the person was born in a different year, 
         * based on the same popularity
         */
        
        // get the true rank of the name (in year)
        String filePath = "data/us_babynames_by_year/yob" + year + ".csv";
        FileResource fr = new FileResource(filePath);
        int rank = getRank(year, name, gender, fr);
        
        // get the name with the same popularity in newYear
        String newName = getName(newYear, rank, gender);
           
        return newName;
    }
    
    
    public void testWhatIsNameInYear() {
        /** 
         * Test whatIsNameInYear() method
         */
        String name = "Owen";
        int year = 1974;
        int newYear = 2014;
        String gender = "M";
        String newName = whatIsNameInYear(name, year, newYear, gender);
        System.out.println(name + " born in " + year + " would be " + newName + 
                           " if she was born in " + newYear);
    }
    
    
    public int yearOfHighestRank(String name, String gender) {
        /**
         * Select a range of files to process and return an integer, the year with the highest rank
         * for the name and gender.
         * Of the name and gender are not in any of the selected files, return -1
         * If there is more than one year with the highest rank, choose the earliest one.
         */
        
        //int getRank(int year, String name, String gender) : -1 if not found
        
        // select multiple files to look up the ranks
        DirectoryResource dr = new DirectoryResource();
        
        // Initliaze variable yearOfHighestRank
        int yearOfHighestRank = -1;
        
        // Initialize variable highestRank with the initial value of 0, 
        // lower than the lowest possible rank of -1 when the name is not found in file
        double highestRank = Double.POSITIVE_INFINITY; 
        
        // iterate through all files and determine the year with the highest rank for the name and gender
        for (File f: dr.selectedFiles()) {
            String fileName = f.getName(); // file name in format e.g. "yob2014.csv"
            int year = Integer.parseInt(fileName.substring(3, 7));
            FileResource fr = new FileResource(f);
            int currRank = getRank(year, name, gender, fr);
            if (currRank != -1 && currRank < highestRank) {
                highestRank = currRank;
                yearOfHighestRank = year;
            }
        }
        
        return (int)yearOfHighestRank;
    }
    
    
    public void testYearOfHighestRank() {
        /**
         * Test yearOfHighestRank() method
         */
        
        String name = "Mich";
        String gender = "M";
        int yearOfHighestRank = yearOfHighestRank(name, gender);
        System.out.println("yearOfHighestRank = " + yearOfHighestRank);        
    }
    
    
    public double getAverageRank(String name, String gender) {
        /**
         * Select a range of files to process and return a double representing the average rank
         * of the name and gender ("F" for female, "M" for male) over the selected files
         * Return -1 if the name is not ranked in any of the selected file
         */
        
        // initialize variables to calculate the average rank across all files
        double avgRank = -1; // avgRank = -1 if the name is not ranked in any of the files
        double sumRank = 0;
        int rankCount = 0;
        
        // select files 
        DirectoryResource dr = new DirectoryResource();
        
        for (File f: dr.selectedFiles()) {
            String fileName = f.getName(); // file name in format e.g. "yob2014.csv"
            int year = Integer.parseInt(fileName.substring(3, 7));
            FileResource fr = new FileResource(f);
            int currRank = getRank(year, name, gender, fr);
            if (currRank != -1) {
                sumRank += currRank;
                rankCount++;
            }
        }
        
        // calculate average rank
        if (sumRank != 0) {
            avgRank = sumRank / rankCount;
        }
        
        return avgRank;
    }
    
    
    public void testGetAverageRank() {
        /**
         * Test getAverageRank() method
         */
        String name = "Robert";
        String gender = "M";
        double avgRank = getAverageRank(name, gender);
        System.out.println("Avg rank for name " + name + " gender " + gender + ": " + avgRank);
    }
    
    
    public static void main(String[] args) {
    Part1 test = new Part1();
    test.testTotalBirths();
    }
}
