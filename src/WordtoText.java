
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.poi.POIDocument;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;


public class WordtoText {
	static void wordToText(String inputFolder, String outputFolder) throws Exception{		
		// get all files 
		int xlsxCounter = 0;
		File rawDataFolder = new File(inputFolder);
		File []sessions = rawDataFolder.listFiles();
		for (int i = 0; i < sessions.length; ++i) {		
			File []eachSession = sessions[i].listFiles();			
			
			for (int j = 0; j < eachSession.length; ++j) {
				File []sessionPart = eachSession[j].listFiles();								
				String textFileName = "";	
				
				if(eachSession[0].isFile()){
					for (int k = 0; k < eachSession.length; ++k) {	
						if(eachSession[k].getName().contains(".xlsx")){
							textFileName = eachSession[k].getName().replace(".xlsx", ".txt");
							String csvFileName = outputFolder+"/"+eachSession[k].getName().replace(".xlsx", ".csv");
							File csvFile = new File(csvFileName);
							XlsxtoCSV.xlsx(eachSession[k].getAbsoluteFile(), csvFile);
							System.out.println(csvFileName);
						}
						else if(eachSession[k].getName().contains(".xls")){
							xlsxCounter++;
						}
					}
					
					for (int k = 0; k < eachSession.length; ++k) {	
						if(eachSession[k].getName().contains(".doc")){							
							writeDocument(eachSession[k], textFileName, outputFolder);
						}
					}
					
					break;
				}
				else{
					
					for (int k = 0; k < sessionPart.length; ++k) {	
						if(sessionPart[k].getName().contains(".xlsx")){
							textFileName = sessionPart[k].getName().replace(".xlsx", ".txt");
							String csvFileName = outputFolder+"/"+sessionPart[k].getName().replace(".xlsx", ".csv");
							File csvFile = new File(csvFileName);
							XlsxtoCSV.xlsx(sessionPart[k].getAbsoluteFile(), csvFile);
							System.out.println(csvFileName);
						}
						else if(sessionPart[k].getName().contains(".xls")){
							xlsxCounter++;
						}
					}
					
					for (int k = 0; k < sessionPart.length; ++k) {	
						if(sessionPart[k].getName().contains(".doc")){			
							writeDocument(sessionPart[k], textFileName, outputFolder);
						}
					}
				}				
			}
		}
		
		System.out.println("Number of old excel file: " + xlsxCounter);
	}
	
	public static void writeDocument(File input, String outTextFileName, String outputFolder){
		if(input.getName().contains("docx")){ //is a docx
		    try{
		    	FileInputStream fs = new FileInputStream(input);
				XWPFDocument docx = new XWPFDocument(fs); 
				//create text extractor object to extract text from the document
				XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
				FileWriter fw = new FileWriter(outputFolder+"/"+outTextFileName);
				//write text to the output file  
				fw.write(extractor.getText());					
				//clear data from memory
				fw.flush();
				//close inputstream and file writer
				fs.close();
				fw.close();
		    }catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		else { //is not a docx
	        try{
	        	FileInputStream fs = new FileInputStream(input);
	            HWPFDocument doc = new HWPFDocument(fs);
	            WordExtractor extractor = new WordExtractor(doc);
	            FileWriter fw = new FileWriter(outputFolder+"/"+outTextFileName);
				//write text to the output file  
				fw.write(extractor.getText());					
				//clear data from memory
				fw.flush();
				//close inputstream and file writer
				fs.close();
				fw.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
}
