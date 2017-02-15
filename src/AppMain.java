public class AppMain {
	public static void main(String[] args) throws Exception {
		Model model = new Model();
		model.evaluateModelByKFolds(10, false, 0);
		//model.printTopKSuccessAndFailureSequence(20,false);
		//model.provideFrequencyDistributionOfSequence(false);
		System.out.println("\nFinished execution.");
		
		//preprocess dmc files
		//WordtoText.wordToText("c:/dmc/rawfiles", "c:/dmc/input");
		//UtilityClass.preprocessMITranscripts("c:/dmc/input", "c:/dmc/output/");
	}
}
