package postprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MyScopeCodeSequence {
	List<CodeSequence> sequences;
	
	public MyScopeCodeSequence() {
		sequences = new ArrayList<>();
	}
	
	public MyScopeCodeSequence(String filePath, Map<Integer, String> dict) {
		sequences = new ArrayList<>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));			
			String line = null;
			
			while((line = br.readLine()) != null) {				
				CodeSequence seq = new CodeSequence(line.trim(), dict);
				sequences.add(seq);
			}
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printToFile(String toFilePath) {
		Collections.sort(sequences, new SortBySupCountUniqueCode());
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(toFilePath));
			for (int i = 0; i < sequences.size(); i++) {
				writer.write(sequences.get(i).getCodeSequence(","));
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

