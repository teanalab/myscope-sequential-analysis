import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxtoCSV {
	static void xlsx(File inputFile, File outputFile) {
        // For storing data into CSV files
        StringBuffer data = new StringBuffer();
        try {
                
           FileOutputStream fos = new FileOutputStream(outputFile);
           // Get the workbook object for XLSX file
	       XSSFWorkbook wBook = new XSSFWorkbook(new FileInputStream(inputFile));
	
	                // Get first sheet from the workbook
	                XSSFSheet sheet = wBook.getSheetAt(0);
	                Row row;
	                Cell cell;
	
	                // Iterate through each rows from first sheet
	                Iterator<Row> rowIterator = sheet.iterator();
	                while (rowIterator.hasNext()) {
	                        row = rowIterator.next();
	                        StringBuffer line = new StringBuffer();
	
	                        // For each row, iterate through each columns
	                        Iterator<Cell> cellIterator = row.cellIterator();
	                        while (cellIterator.hasNext()) {
	
	                                cell = cellIterator.next();
	
	                                switch (cell.getCellType()) {
	                                case Cell.CELL_TYPE_STRING:
	                                	line.append(cell.getStringCellValue() + ",");
	                                        break;
	                                case Cell.CELL_TYPE_NUMERIC:  
	                                		final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	                                		DateFormat formatter = new SimpleDateFormat(NEW_FORMAT);
	                                		String time = formatter.format(cell.getDateCellValue());
	                                		line.append(time.toString().substring(11, 21) + ",");
	                                		//line.append(cell.getNumericCellValue() + ",");
	                                	 	break;
	                                case Cell.CELL_TYPE_BLANK:
	                                	line.append("" + ",");
	                                        break;
	                                default:
	                                	line.append(cell + ",");
	
	                                }
	                        }
	                        
	                        if(line.length() > 20){
	                        	String arrData[] = line.toString().split(",");
	                        	if(arrData[0].contains(":"))
	                        		data.append(",,"+line + "\n");
	                        	else if(arrData[1].contains(":")){
	                        		data.append(","+line + "\n");
	                        	}
	                        	else
	                        		data.append(line.toString().replace(";", ":") + "\n");
	                        	//System.out.println(line);
	                        }
	                        else
	                        	data.append(line + "\n");
	                }
	
	                fos.write(data.toString().getBytes());
	                fos.close();
	
	        } catch (Exception ioe) {
	                ioe.printStackTrace();
	        }
	}

}
