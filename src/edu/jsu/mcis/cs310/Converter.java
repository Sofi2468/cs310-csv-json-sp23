package edu.jsu.mcis.cs310;


import java.io.*;
import java.util.*;
import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.text.DecimalFormat;

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
        
        String result = ""; // default return value; replace later!
        DecimalFormat df = new DecimalFormat("00");
        
         try {
       
        // Deserialize JSON string to a JsonObject
        JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());
       
        // Get the column headings
        JsonArray header = (JsonArray) (jsonObject.get("ColHeadings"));
       
        // Get the product numbers 
        JsonArray prodNumber = (JsonArray) (jsonObject.get("ProdNums"));
       
        // Get the data for each product 
        JsonArray eachdataall = (JsonArray) (jsonObject.get("Data"));
       
        // Create a StringWriter object to hold the CSV data
        StringWriter stringWriter = new StringWriter();
        
        // Create a CSVWriter object to write the CSV data
        CSVWriter writer = new CSVWriter(stringWriter, ',', '"', '\\', "\n");

        // Write the column headings to the CSV file
        String[] headers = new String[header.size()];
        for (int i = 0; i < header.size(); i++) {
            headers[i] = header.get(i).toString();
        }
        writer.writeNext(headers);
       
        // Create a decimal formatter to format the episode numbers
        DecimalFormat formatter = new DecimalFormat("00");
        
        // Loop through each product
        for(int i=0;i<prodNumber.size();i++){
           
            // Create an array to hold the data for each row 
            String[] row= new  String[header.size()];
           
            // Get the data for the current product
            JsonArray eachdata = new JsonArray();
            eachdata=(JsonArray) eachdataall.get(i);
           
            // Write the product number to the first column of the row
            row[0]=prodNumber.get(i).toString();
           
            // Loop through the data for each column in the row
            for (int j = 0; j < eachdata.size(); j++) {
               
                // If the current column is the "Episode" column, format the episode number
                if(eachdata.get(j)==eachdata.get(header.indexOf("Episode")-1)){
                    int episode = Integer.parseInt(eachdata.get(j).toString());
                    String fnumber = "";
                    fnumber = formatter.format(episode);
                    row[j+1]=fnumber;
                }
                // Otherwise, just add the data to the row
                else{
                    row[j+1] = eachdata.get(j).toString();
                }
               
            }
           
            // Write the row to the CSV file
            writer.writeNext(row);
           
           
        }
       
        result = stringWriter.toString();
    }

        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
