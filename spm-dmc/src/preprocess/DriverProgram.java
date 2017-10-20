package preprocess;

public class DriverProgram {

	public static void main(String[] args) {
		String formFile = "input/code_sequence/successful.txt";
		String toFile = "input/formatted_sequence/successful.txt";
		
		SequenceData seqData = new SequenceData(formFile);
		seqData.transformToExpectedFormat(formFile, toFile);

	}

}
