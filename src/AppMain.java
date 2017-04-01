public class AppMain {
	public static void main(String[] args) throws Exception {
		//Model model = new Model();
		//model.evaluateModelByKFolds(5, false, 0);
		//model.printTopKSuccessAndFailureSequence(20,false);
		//model.provideFrequencyDistributionOfSequence(false);
		//System.out.println("\nFinished execution.");
		
		// Preprocess dmc files
		WordtoText.wordToText("c:/dmc/rawfiles", "c:/dmc/input");
		UtilityClass.splitSentenceForMSDoc("c:/dmc/input", "c:/dmc/linebreak");
		//UtilityClass.preprocessMITranscripts("c:/dmc/input", "c:/dmc/output/");
		UtilityClass.preprocesseCoachingData("c:/dmc/linebreak", "c:/dmc/output/");
		
		//CRFClassifier crfModel = new CRFClassifier();
		//crfModel.evaluateModel(5);
	}
}
