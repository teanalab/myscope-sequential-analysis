package postprocess;

import java.util.Comparator;

public class SortBySequenceLength implements Comparator<CodeSequence> {
	public int compare(CodeSequence s1, CodeSequence s2) {
		return s2.code.size() - s1.code.size();
	}
}
