public class AppMain {

	public static void main(String[] args) throws Exception {
		Model model = new Model();
		model.evaluateModelBy3Folds();
		System.out.println("Finished execution.");
	}
}
