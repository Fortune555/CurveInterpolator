//package com.example;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class BondCurve {
    //private static Table table;

	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter date YYYY-MM-DD: ");  		
		String date = input.nextLine();
		
		System.out.println("Enter rate type(Bid, Ask or Mid): ");  		
		String rateType = input.nextLine(); 
		
		System.out.println("Enter full path of the csv file: ");  		
		//String path = input.nextLine();
		String path = "/home/fortune/eclipse-workspace/tablesaw-core/src/bondcurve.csv";
		
		BondCurveInterpolation interpolation = new BondCurveInterpolation( date,rateType, path);
		
		System.out.println("The "+rateType + " rate for " + date + " is: "+ interpolation.getRate());
		
		
        
        
    }
    
	
    
    }
    



