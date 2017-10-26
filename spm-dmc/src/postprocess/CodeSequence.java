package postprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeSequence {
	public List<String> code;
	public Map<String, Boolean> codeMap;
	public int numUniqueCode;
	public int supCount;
	
	public CodeSequence() {
		code = null;
		numUniqueCode = 0;
		supCount = 0;
		codeMap = new HashMap<>();
	}
	
	public CodeSequence(String seq, Map<Integer, String> dict) {
		code = new ArrayList<String>();
		codeMap = new HashMap<>();
		String[] codes = seq.split(" ");
		
		for (int i = 0; i < codes.length; i++) {
			
			if (i == codes.length-1) {
				supCount = Integer.parseInt(codes[i].trim());
			}
			else if (codes[i].trim().equalsIgnoreCase("#SUP:") || 
					codes[i].trim().equalsIgnoreCase("-1")){
				// skip them
			}
			else if (codes[i].trim().length() == 0) {
				
			}
			else {
				code.add(dict.get(Integer.parseInt(codes[i].trim())));
				codeMap.put(dict.get(Integer.parseInt(codes[i].trim())), true);
			}
		}
		
		numUniqueCode = codeMap.size();
	}
	
	public String getUniqueCodeSequence(String delimiter) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < code.size(); i++) {
			if (codeMap.get(code.get(i))) {
				sb.append(code.get(i));
				sb.append(delimiter);
				codeMap.put(code.get(i), false);
			}
		}
		
		sb.append(supCount);
		
		for (int i = 0; i < code.size(); i++) 
			codeMap.put(code.get(i), true);
		
		return sb.toString().trim();
	}
	
	public String getCodeSequence(String delimiter) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < code.size(); i++) {
			sb.append(code.get(i));
			sb.append(delimiter);
		}
		
		sb.append(supCount);
		
		return sb.toString().trim();
	}
}
