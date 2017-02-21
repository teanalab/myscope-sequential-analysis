public class AppMain {
	public static void main(String[] args) throws Exception {
		Model model = new Model();
		model.evaluateModelByKFolds(5, false, 2);
		//model.printTopKSuccessAndFailureSequence(20,false);
		//model.provideFrequencyDistributionOfSequence(false);
		System.out.println("\nFinished execution.");
		
		//preprocess dmc files
		//WordtoText.wordToText("c:/dmc/rawfiles", "c:/dmc/input");
		//UtilityClass.preprocessMITranscripts("c:/dmc/input", "c:/dmc/output/");
		
		//CRFClassifier crfModel = new CRFClassifier();
		//crfModel.evaluateModel(5);
	}
}
