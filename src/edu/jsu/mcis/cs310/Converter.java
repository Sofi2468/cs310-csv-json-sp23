package edu.jsu.mcis.cs310;


import java.io.*;
import java.util.*;
import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result= "{}"; 
        
        try {
        
            
            // Create a CSV reader from the CSV string
            CSVReader reader = new CSVReader(new StringReader(csvString));
            
            // Read the header row of the CSV file
            String[] header = reader.readNext();
            
            // Create a list to hold the data from the CSV file
            List<String[]> rows = new ArrayList<>();
            String[] row;
            
            // Read the data rows from the CSV file
            while((row = reader.readNext()) != null) {
                rows.add(row);
            }
            
            // Create a JsonObject to hold the JSON data
            JsonObject obj = new JsonObject();
            
            // Add the header row to the JsonObject as the "ColHeadings" key
            obj.put("ColHeadings", header);
            
            // Create a list to hold the data from the CSV file
            List<String> prodNums = new ArrayList<>();
            List<List<Object>> data = new ArrayList<>();
            
            // Iterate over the data rows
            for(String[] rowData : rows) {
                // Add the "ProdNum" column to the "ProdNums" list
                prodNums.add(rowData[0]);
                
                // Create a list to hold the data from the current row
                List<Object> dataRow = new ArrayList<>();
                
                // Iterate over the columns in the current row
                for(int i = 1; i < rowData.length; i++) {
                    // Check if the current column is an integer
                    try {
                        dataRow.add(Integer.parseInt(rowData[i]));
                    }
                    catch(NumberFormatException e) {
                        dataRow.add(rowData[i]);
                    }
                }
                
                // Add the data from the current row to the "Data" list
                data.add(dataRow);
            }
            
            // Add the "ProdNums" list to the JsonObject
            obj.put("ProdNums", prodNums);
            
            // Add the "Data" list to the JsonObject
            obj.put("Data", data);
            
            // Convert the JsonObject to a string
            result = obj.toJson();
 
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result= "{}"; 
        
        try {
            
        // Create a JsonObject from the JSON string
        JsonObject json = (JsonObject) Jsoner.deserialize(jsonString);
        
        // Get the header row from the JsonObject, using the "ColHeadings" key
        List<String> header = (List<String>) json.get("ColHeadings");
        
        // Get the "Data" key from the JsonObject
        List<List<Object>> data = (List<List<Object>>) json.get("Data");
        
        // Get the "ProdNums" key from the JsonObject
        List<String> prodNums = (List<String>) json.get("ProdNums");
        
        // Create a CSV writer
        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);
        
        // Write the header row to the CSV writer
        writer.writeNext(header.toArray(new String[0]));
        
        // Write the data rows to the CSV writer
        for (int i = 0; i < data.size(); i++) {
            List<Object> rowData = data.get(i);
            String[] rowWithProdNum = new String[rowData.size() + 1];
            rowWithProdNum[0] = prodNums.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                rowWithProdNum[j + 1] = rowData.get(j).toString();
            }
            writer.writeNext(rowWithProdNum);
        }
        writer.close();
        
        // Get the string representation of the CSV data
        result = stringWriter.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
