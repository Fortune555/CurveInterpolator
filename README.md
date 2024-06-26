# CurveInterpolator
The code in this repository perform linear interpolation on a yield curve.

# BondCurve README

## Overview

`BondCurve` is a Java application that reads bond curve data from a CSV file and performs linear interpolation to find the interest rate for a specified date and rate type. The application uses the Tablesaw library for data manipulation and the `BondCurveInterpolation` class to perform the interpolation.

## Prerequisites

- Java Development Kit (JDK) installed
- Maven installed
- A CSV file containing bond curve data with columns "Date", "Num Days", and specific rate types (e.g., "Bid Rate", "Ask Rate", "Mid Rate")

## Setup

1. **Add Tablesaw Dependency**

   Add the following Tablesaw dependency to your `pom.xml`:

   ```xml
   <dependency>
       <groupId>tech.tablesaw</groupId>
       <artifactId>tablesaw-core</artifactId>
       <version>0.38.2</version>
   </dependency>
   ```

2. **CSV File Format**

   Ensure your CSV file has the following columns:
   - `Date`: The date corresponding to the bond rate.
   - `Num Days`: The number of days from the initial date.
   - Rate type columns (e.g., "Bid Rate", "Ask Rate", "Mid Rate"): The bond rates for different types.

## Usage

1. **Run the Application**

   To run the application, execute the `BondCurve` class. You will be prompted to enter the date, rate type, and the full path to the CSV file.

   ```sh
   java BondCurve
   ```

2. **Input Data**

   Enter the following when prompted:
   - **Date**: The date for which you want to find the rate in `YYYY-MM-DD` format.
   - **Rate Type**: The rate type you are interested in (`Bid`, `Ask`, or `Mid`).
   - **CSV File Path**: The full path to the CSV file containing the bond curve data.

3. **Example Usage**

   ```plaintext
   Enter date YYYY-MM-DD: 
   2024-06-07
   Enter rate type(Bid, Ask or Mid): 
   Bid
   Enter full path of the csv file: 
   /home/fortune/eclipse-workspace/tablesaw-core/src/bondcurve.csv
   The Bid rate for 2024-06-07 is: 0.0875
   ```

### Code Description

- **BondCurve Class**
  - `main(String[] args)`: The entry point of the application. Prompts the user for input and prints the interpolated rate.

- **BondCurveInterpolation Class**
  - This class is used to read the CSV file, extract the necessary data, and perform linear interpolation to calculate the interest rate for the specified date and rate type.

### Class: BondCurve

```java
import tech.tablesaw.api.Table;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class BondCurve {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter date YYYY-MM-DD: ");  		
        String date = input.nextLine();

        System.out.println("Enter rate type(Bid, Ask or Mid): ");  		
        String rateType = input.nextLine(); 

        System.out.println("Enter full path of the csv file: ");  		
        String path = "/home/fortune/eclipse-workspace/tablesaw-core/src/bondcurve.csv";

        BondCurveInterpolation interpolation = new BondCurveInterpolation(date, rateType, path);

        System.out.println("The " + rateType + " rate for " + date + " is: " + interpolation.getRate());
    }
}
```

### Class: BondCurveInterpolation

```java
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

    public BondCurveInterpolation(String date, String rateType, String path){    
        this.date = date;
        this.rateType = rateType;
        this.filePath =  path;
    }

    public Table getData(){       
        try {
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
        DoubleColumn yColumn = getData().doubleColumn(rateType + " Rate");      
        
        if(numberOfDays >= 0 && numberOfDays <= 720) {
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

            return y1 + (numberOfDays - x1) * (y2 - y1) / (x2 - x1);
        } else if (numberOfDays > 720) {
            return yColumn.get(getData().rowCount() - 1);
        } else {
            System.out.println("This date is before the initial: " + initialDate);
            return 0;
        }
    }
}
```

### Notes

- Ensure the date you enter is within the range of dates in the CSV file.
- Linear interpolation is used to calculate the rate between two known points.
- If the specified date is beyond the range of the CSV file, the last available rate is returned.
- If the date is before the initial date in the CSV file, a message will be printed, and a rate of 0 will be returned.

-------------------------------------------------------------------------------------------------------------------------------------------------


## README for BondCurveInterpolation

### Overview

`BondCurveInterpolation` is a Java class designed to read bond curve data from a CSV file and perform linear interpolation to find the interest rate for a specific date. The class uses the Tablesaw library for data manipulation.

### Prerequisites

- Java Development Kit (JDK) installed
- Maven installed
- A CSV file containing bond curve data with columns "Date", "Num Days", and specific rate types (e.g., "Bid Rate", "Ask Rate", "Mid Rate".)

### Setup

1. **Add Tablesaw Dependency**

   Add the following Tablesaw dependency to your `pom.xml`:

   ```xml
   <dependency>
       <groupId>tech.tablesaw</groupId>
       <artifactId>tablesaw-core</artifactId>
       <version>0.38.2</version>
   </dependency>
   ```

2. **CSV File Format**

   Ensure your CSV file has the following columns:
   - `Date`: The date corresponding to the bond rate.
   - `Num Days`: The number of days from the initial date.
   - Rate type columns (e.g., "Bid Rate", "Ask Rate", "Mid Rate"): The bond rates for different time periods.

### Usage

1. **Instantiate the Class**

   Create an instance of the `BondCurveInterpolation` class by providing the date, rate type, and path to the CSV file:

   ```java
   BondCurveInterpolation interpolation = new BondCurveInterpolation("2024-06-07", "Mid", "/path/to/your/bondcurve.csv");
   ```

2. **Get Interpolated Rate**

   Call the `getRate` method to obtain the interpolated rate for the specified date:

   ```java
   double rate = interpolation.getRate();
   System.out.println("Interpolated rate: " + rate);
   ```

### Class Description

- **Fields**
  - `table`: Stores the data loaded from the CSV file.
  - `date`: The date for which the rate is to be interpolated.
  - `rateType`: The type of rate to be interpolated (e.g., "Bid Rate", "Ask Rate", "Mid Rate").
  - `filePath`: The path to the CSV file.

- **Constructor**
  - Initializes the `date`, `rateType`, and `filePath` fields.

- **Methods**
  - `getData()`: Reads and returns the data from the CSV file as a `Table`.
  - `getRate()`: Performs linear interpolation to find the rate for the specified date.

### Example

```java
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

    public BondCurveInterpolation(String date, String rateType, String path){    
        this.date = date;
        this.rateType = rateType;
        this.filePath =  path;
    }

    public Table getData(){       
        try {
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
        DoubleColumn yColumn = getData().doubleColumn(rateType + " Rate");      
        
        if(numberOfDays >= 0 && numberOfDays <= 720) {
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

            return y1 + (numberOfDays - x1) * (y2 - y1) / (x2 - x1);
        } else if (numberOfDays > 720) {
            return yColumn.get(getData().rowCount() - 1);
        } else {
            System.out.println("This date is before the initial: " + initialDate);
            return 0;
        }
    }
}
```

### Notes

- The `getRate` method assumes the date for which the rate is being calculated is between the first and last date in the CSV file.
- Linear interpolation is used to calculate the rate between two known points.
- If the specified date is beyond the range of the CSV file, the last available rate is returned.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------
# BondCurveInterpolationTest

## Overview

`BondCurveInterpolationTest` is a JUnit test class for testing the `BondCurveInterpolation` class. This class reads bond curve data from a CSV file and provides methods to interpolate and retrieve rates based on a specified date and rate type.

## Prerequisites

- Java Development Kit (JDK) installed
- Maven or Gradle for dependency management (optional but recommended)
- JUnit 4.x for testing
- Tablesaw library for data manipulation

## Setup

1. **Clone the repository**:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. **Add dependencies**: Ensure that the following dependencies are added to your `pom.xml` (for Maven) or `build.gradle` (for Gradle) file.

    **Maven**:
    ```xml
    <dependencies>
        <!-- JUnit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
        
        <!-- Tablesaw -->
        <dependency>
            <groupId>tech.tablesaw</groupId>
            <artifactId>tablesaw-core</artifactId>
            <version>0.38.3</version>
        </dependency>
    </dependencies>
    ```

    **Gradle**:
    ```groovy
    dependencies {
        // JUnit
        testImplementation 'junit:junit:4.13.2'
        
        // Tablesaw
        implementation 'tech.tablesaw:tablesaw-core:0.38.3'
    }
    ```

3. **Prepare the test data**: Ensure that the `testfile.csv` is placed in the correct directory. The file path should match the one specified in the `BondCurveInterpolationTest` class:
    ```sh
    <project-root>/testfile.csv
    ```

## Running the Tests

### Using an IDE

1. **Open the project** in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
2. **Navigate to the test class**: `BondCurveInterpolationTest`.
3. **Run the test class** using the IDE's built-in test runner.

### Using Command Line

1. **Navigate to the project directory**:
    ```sh
    cd <project-directory>
    ```

2. **Run the tests**:
    - If using Maven:
      ```sh
      mvn test
      ```

    - If using Gradle:
      ```sh
      gradle test
      ```

## Test Cases

The `BondCurveInterpolationTest` class includes the following test cases:

1. **testGetData**: Verifies that the `getData` method correctly loads the table from the CSV file and that the row count matches the expected value.
    ```java
    @Test
    public void testGetData() {
        BondCurveInterpolation bondCurveInterpolation = new BondCurveInterpolation("2024-06-10", "Bid", path);
        Table table = bondCurveInterpolation.getData();
        assertNotNull(table);
        assertEquals(9, table.rowCount());
    }
    ```

2. **testGetRateWithinRange**: Tests if the `getRate` method correctly interpolates the rate for a date within the range of the CSV data.
    ```java
    @Test
    public void testGetRateWithinRange() {
        bondCurveInterpolation = new BondCurveInterpolation("2024-06-10", "Bid", path);
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0.0463, rate, 0.001);
    }
    ```

3. **testGetRateAboveRange**: Tests if the `getRate` method correctly returns the last rate for a date above the range of the CSV data.
    ```java
    @Test
    public void testGetRateAboveRange() {
        bondCurveInterpolation = new BondCurveInterpolation("2027-06-01", "Bid", path);
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0.113, rate, 0.001);
    }
    ```

4. **testGetRateBelowRange**: Tests if the `getRate` method returns zero for a date below the range of the CSV data.
    ```java
    @Test
    public void testGetRateBelowRange() {
        bondCurveInterpolation = new BondCurveInterpolation("2022-01-01", "Bid", path);
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0, rate, 0.01);
    }
    ```

5. **testGetRateWithDifferentRateType**: Tests if the `getRate` method correctly interpolates the rate for a different rate type within the range of the CSV data.
    ```java
    @Test
    public void testGetRateWithDifferentRateType() {
        bondCurveInterpolation = new BondCurveInterpolation("2025-02-01", "Ask", path);
        double rate = bondCurveInterpolation.getRate();
        assertEquals(0.0712, rate, 0.001);
    }
    ```

## Conclusion

This `BondCurveInterpolationTest` class helps ensure the correctness of the `BondCurveInterpolation` methods by verifying the data loading and rate interpolation functionalities. Make sure the CSV file path is correct and that the necessary dependencies are included in your project to run the tests successfully.


This README provides a basic understanding of how to use the `BondCurveInterpolation` class to perform linear interpolation on bond curve data stored in a CSV file using Tablesaw. Make sure your CSV file is correctly formatted and that you have included the necessary Maven dependencies.
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
