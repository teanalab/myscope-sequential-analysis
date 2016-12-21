
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class WordtoText {
	static void wordToText(String inputFolder, String outputFolder) throws Exception{		
		// get all files 
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
					}
					
					for (int k = 0; k < eachSession.length; ++k) {	
						if(eachSession[k].getName().contains(".docx")){			
							FileInputStream fs = new FileInputStream(eachSession[k]);
							XWPFDocument docx = new XWPFDocument(fs); 
							//create text extractor object to extract text from the document
							@SuppressWarnings("resource")
							XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
							FileWriter fw = new FileWriter(outputFolder+"/"+textFileName);
							//write text to the output file  
							fw.write(extractor.getText());					
							//clear data from memory
							fw.flush();
							//close inputstream and file writer
							fs.close();
							fw.close();
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
					}
					
					for (int k = 0; k < sessionPart.length; ++k) {	
						if(sessionPart[k].getName().contains(".docx")){			
							FileInputStream fs = new FileInputStream(sessionPart[k]);
							XWPFDocument docx = new XWPFDocument(fs); 
							//create text extractor object to extract text from the document
							@SuppressWarnings("resource")
							XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
							FileWriter fw = new FileWriter(outputFolder+"/"+textFileName);
							//write text to the output file  
							fw.write(extractor.getText());					
							//clear data from memory
							fw.flush();
							//close inputstream and file writer
							fs.close();
							fw.close();
						}
					}
				}				
			}
		}  
	}
}
