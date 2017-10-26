package postprocess;

import java.util.Comparator;

public class SortBySupCountUniqueCode implements Comparator<CodeSequence> {
	public int compare(CodeSequence s1, CodeSequence s2) {
		if (s1.supCount != s2.supCount) {
			return s2.supCount - s1.supCount;
		}
		else if(s1.numUniqueCode != s2.numUniqueCode) {
			return s2.numUniqueCode - s1.numUniqueCode;
		} 
		else {
			return s2.code.size() - s1.code.size();
		}
	}
}
