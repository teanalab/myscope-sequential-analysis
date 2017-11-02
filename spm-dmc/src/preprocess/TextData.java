package preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class TextData {
	
	public static void createSequences(String inputFolder, String successFile, 
			String unsuccessFile, boolean withSpeaker) throws Exception {
		
		PrintWriter successWriter = new PrintWriter(successFile);
		PrintWriter unsuccessWriter = new PrintWriter(unsuccessFile);
		File rawDataFolder = new File(inputFolder);
		File []rawFiles = rawDataFolder.listFiles();
		 
		for (int i = 0; i < rawFiles.length; ++i) {
			BufferedReader br = new BufferedReader(new FileReader(rawFiles[i]));			
			String line = "";
			int count = 0;
			
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(",");
				String code = arr[0].trim();
				
				//System.out.println(rawFiles[i].getName() + " : " + arr[1]);
				
				if (withSpeaker) {
					String pr = arr[1].trim().split(":")[0].trim();
					if(pr.equalsIgnoreCase("O") || pr.equalsIgnoreCase("O2")
							|| pr.equalsIgnoreCase("S2") || pr.equalsIgnoreCase("S3")) {
						code = "O:" + code;
					}
					else if(pr.equalsIgnoreCase("Y") || pr.equalsIgnoreCase("PY")
							 || pr.equalsIgnoreCase("PT")) {
						code = "PT:" + code;
					}
					else if(pr.equalsIgnoreCase("CG") || pr.equalsIgnoreCase("CG2")) {
						code = "CG:" + code;
					}
					else {
						code = "CHW:" + code;
					}
				}
				
				if (arr[0].trim().equalsIgnoreCase("CHT+") || arr[0].trim().equalsIgnoreCase("CML+") 
						|| arr[0].trim().equalsIgnoreCase("AMB+")) {
					sb.append("500");
					if (count > 1)
						successWriter.println(sb.toString());
					sb.setLength(0);
					count = 0;
				}
				else if (arr[0].trim().equalsIgnoreCase("CHT-") || arr[0].trim().equalsIgnoreCase("CML-") 
						|| arr[0].trim().equalsIgnoreCase("AMB-") || arr[0].trim().equalsIgnoreCase("AMB0")) {
					sb.append("400");
					if (count > 1)
						unsuccessWriter.println(sb.toString());
					sb.setLength(0);
					count = 0;
				}
				else {
					count++;
					sb.append(code);
					sb.append(",");
				}
			}
			
			br.close();	
		}
		successWriter.close();
		unsuccessWriter.close();
	}
}
