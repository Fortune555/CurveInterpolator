import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;


public class BondCurveInterpolation {

	private Table table;
	private String date;
	private String rateType;
	private String filePath;
	
	public BondCurveInterpolation(String date, String rateType,	 String path){	
		this.date = date;
		this.rateType = rateType;
		this.filePath =  path;
}
	

	public Table getData(){       
        // Replace with the path to your CSV file
        try {
            // Load the CSV file into a Table
            Table table = Table.read().csv(filePath);
            
            return table;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }        
        
    }	
	
	public double getRate() {
    	 	
    	LocalDate initialDate = getData().dateColumn("Date").get(0);
    	long numberOfDays = ChronoUnit.DAYS.between(initialDate, LocalDate.parse(date));
    	
    	  IntColumn xColumn = getData().intColumn("Num Days");
         
    	  DoubleColumn yColumn = getData().doubleColumn(rateType + " "+ "Rate");   	  
    	  
    	if(numberOfDays>=0 && numberOfDays <=720) {
   	
        // Find the surrounding points for interpolation
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        for (int i = 0; i < getData().rowCount() - 1; i++) {
            if (xColumn.get(i) <= numberOfDays && xColumn.get(i + 1) >= numberOfDays) {
                x1 = xColumn.get(i);                
				y1 = yColumn.get(i);
                x2 = xColumn.get(i + 1);
                y2 = yColumn.get(i + 1);
                break;
            	}
        }

        // Perform linear interpolation
        return y1 + (numberOfDays - x1) * (y2 - y1) / (x2 - x1);
        
    	}else if(numberOfDays > 720) {
    			return yColumn.get(getData().rowCount() - 1);
		    	}  else{
		    		System.out.println("This date is before the initial: "+ initialDate);
		    		
		    		return 0;
		    	}
		}
    	
}
