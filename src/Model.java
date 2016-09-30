import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class Model {

	public static final int folds = 5;
	public static double results = 0;
	public static final String successCode = "500";
	public static final String failureCode = "400";
	public static ArrayList<CodeSequence> trainData = new ArrayList<>();
	public static ArrayList<CodeSequence> testData = new ArrayList<>();
	public static ArrayList<CodeSequence> data = new ArrayList<>();
	public static HashMap<String,String> codemap = new HashMap<>();
	public static double delta = -0.9, sumRatio400 = 0, sumRatio500=0;
	public static double fpRate = 0;
	public static int tp=0, fp=0, tn=0, fn=0;
	
	public void evaluateModelBy3Folds() throws Exception{
		// Build myscope code mapping
		UtilityClass.buildMap(codemap);
		
		UtilityClass.printCodeMap(codemap);
				
		// Create sequence dataset from raw data
		//UtilityClass.createCodeSequenceFromRawData(true, codemap);
		UtilityClass.createCodeSequenceFromRawData(false, codemap, true);
		
		// Read sequence data from file
		data = UtilityClass.readAllSequence();
		Collections.shuffle(data);
		
		System.out.println("Total sequences: " + data.size());
		
		String finalPrint = "";
		
		for(int j=0; j < 20; j++){
			delta = delta + 0.1;
		//delta = 0.2;
			results = 0;
			sumRatio500 = 0;
			sumRatio400 = 0;
			fp=0;tp=0;fn=0;tn=0;
			// Iterate for each fold
			for(int i=1; i <= folds; i++){
				// Initialize data
				int foldSize = data.size()/folds;
				int startIndex = (i-1)*foldSize;
				int endIndex = data.size()-1;
				if(i != folds) 
					endIndex = startIndex + foldSize;
				
				initialize(startIndex, endIndex);			
				evaluateModel();
				trainData.clear();
				testData.clear();
			}
			
			if(delta > .19 && delta < .201){
				finalPrint = finalPrint + "\nAverage accuracy: " + (results/folds);
				finalPrint = finalPrint +  "\nAverage fp: " + (sumRatio500/(folds));
				finalPrint = finalPrint + "\nConfushion Matrix: " + " tp=" + tp + " fp=" + fp + " tn=" + tn + " fn=" + fn +  " for delta: " + delta;
			}
			
			System.out.println("\nAverage accuracy: " + (results/folds) + " for delta: " + delta);
			System.out.println("Average fp: " + (sumRatio500/(folds)) + " for delta: " + delta);
			System.out.println("Confushion Matrix: " + " tp=" + tp + " fp=" + fp + " tn=" + tn + " fn=" + fn +  " for delta: " + delta);
			sumRatio500 = 0;
			
		}
			
		// Print top k sequences
		printTopKSuccessAndFailureSequence(data,20,false);
		System.out.println("\n\nFinal: " + finalPrint);
	}
	
	public void provideFrequencyDistributionOfSequence() throws Exception{		
		// Create alternate sequence and then calculate their frequencies
		UtilityClass.createCombinationOfCodeSequence(false);
		UtilityClass.calculateFreqOfSequences("SequentialData/allsequence.txt", "SequentialData/alternate-seq-frequency.csv");
		
		// Read sequence data from file
		data = UtilityClass.readAllSequence();
		Collections.shuffle(data);
		System.out.println("Total sequences: " + data.size() + "\n");
		
		// Create normal sequence and then calculate their frequencies
		UtilityClass.createCombinationOfCodeSequence(true);
		UtilityClass.calculateFreqOfSequences("SequentialData/allsequence.txt", "SequentialData/normal-seq-frequency.csv");
	
		// Read sequence data from file
		data = UtilityClass.readAllSequence();
		Collections.shuffle(data);		
		System.out.println("Total sequences: " + data.size());
				
		// Create pair sequence and then calculate their frequencies
		UtilityClass.calculateDistributionOfPairSequence("SequentialData/pair-frequency.csv");
	}
	
	public void evaluateModel(){
		
		// Create successful markov model
		MarkovModel successfulMarkovModel = new SuccessfulMarkovModel();
		successfulMarkovModel.createSequence();
		successfulMarkovModel.buildModel();
		
		// Create unsuccessful markov model
		MarkovModel unsuccessfulMarkovModel = new UnsuccessfulMarkovModel();
		unsuccessfulMarkovModel.createSequence();
		unsuccessfulMarkovModel.buildModel();
		
		// Set threshhold "delta" for decision making
		setDelta(successfulMarkovModel, unsuccessfulMarkovModel);
				
		// Evaluate markov model
		validateModel(successfulMarkovModel, unsuccessfulMarkovModel);
				
	}
	
	public void printTopKSuccessAndFailureSequence(ArrayList<CodeSequence> allsequence, int k, boolean isCodeOnly) {
		HashMap<String, Integer> localTransitionProbabilityMap = new HashMap<>();
		HashMap<String, Integer> localStateFrequencyMap = new HashMap<>();
		// Initialize transition probabilities and state frequency
		for(int i=0; i < allsequence.size(); i++){
			CodeSequence codeSequence = allsequence.get(i);
			Object[] states = codeSequence.getStates().toArray();
			for(int j=0; j < states.length-1; j++){
				State state = (State) states[j];
				State stateNext = (State) states[j+1];
				String key = (state.getState() + "-" + stateNext.getState()).trim();
				// Add transition probabilities
				if(localTransitionProbabilityMap.containsKey(key)){
					localTransitionProbabilityMap.put(key, localTransitionProbabilityMap.get(key)+1);
				}
				else{
					localTransitionProbabilityMap.put(key, 1);
				}
				
				if(localStateFrequencyMap.containsKey(state.getState().trim())){
					localStateFrequencyMap.put(state.getState().trim(), localStateFrequencyMap.get(state.getState().trim())+1);
				}
				else{
					localStateFrequencyMap.put(state.getState().trim(), 1);
				}
			}
		}
		
		// Set probabilities
		for(int i=0; i < allsequence.size(); i++){
			CodeSequence codeSequence = allsequence.get(i);
			codeSequence.setLikelihoodOfSuccess(localTransitionProbabilityMap, localStateFrequencyMap);
			codeSequence.setLikelihoodOfFailure(localTransitionProbabilityMap, localStateFrequencyMap);
			codeSequence.set_delta();
		}
		
		Collections.sort(allsequence, CodeSequence.Comparators.DELTALEVEL);
		//Collections.sort(allsequence, CodeSequence.Comparators.SUCCESS);
		HashMap<String, String> successSequenceMap = new HashMap<>();

		for(int i=allsequence.size()-1; i >= 0; i--){
			if(allsequence.get(i).toString().contains(",500") && allsequence.get(i).getLikelihoodOfSuccess() > -8){
				successSequenceMap.put(allsequence.get(i).toString(), allsequence.get(i).toString());
				//System.out.println(allsequence.get(i).get_delta());
			}
			
			if(successSequenceMap.size() >= k)
				break;
		}
		
		
		//Collections.sort(allsequence, CodeSequence.Comparators.DELTALEVEL);
		//Collections.sort(allsequence, CodeSequence.Comparators.FAILURE);
		HashMap<String, String> failureSequenceMap = new HashMap<>();
		
		for(int i=0; i < allsequence.size()-1; i++){
			if(allsequence.get(i).toString().contains(",400") && allsequence.get(i).getLikelihoodOfFailure() > -8){
				if(!successSequenceMap.containsKey(allsequence.get(i).toString())){
					failureSequenceMap.put(allsequence.get(i).toString(), allsequence.get(i).toString());
					//System.out.println(allsequence.get(i).get_delta());
				}
			}
			
			if(failureSequenceMap.size() >= k)
				break;
		}
		
		if(isCodeOnly){
			System.out.println("\nTop "+k+" successful code sequences: ");
			for(String key:successSequenceMap.keySet())
				System.out.println(successSequenceMap.get(key));
	
			System.out.println("\nTop "+k+" unsuccessful code sequences: ");
			for(String key:failureSequenceMap.keySet())
				System.out.println(failureSequenceMap.get(key));
		}
		else{
			System.out.println("\nTop "+k+" successful code sequences: ");
			for(String key:successSequenceMap.keySet()){
				UtilityClass.printSequenceByMapping(successSequenceMap.get(key), codemap);
			}
	
			System.out.println("\nTop "+k+" unsuccessful code sequences: ");
			for(String key:failureSequenceMap.keySet()){
				UtilityClass.printSequenceByMapping(failureSequenceMap.get(key), codemap);
			}
		}
	}
	
	private void validateModel(MarkovModel successfulMarkovModel, MarkovModel unsuccessfulMarkovModel){
		int accurate = 0;
		double accurate400 = 0, inaccurate400 = 0, accurate500 = 0, inaccurate500 = 0, original500 = 0, original400 = 0;
		for(int i=0; i < successfulMarkovModel.get_testCodeSequenceList().size(); i++){
			String predictedCode = "";
			CodeSequence codeSequence = successfulMarkovModel.get_testCodeSequenceList().get(i);
			String[] sequences = codeSequence.toString().split(",");
			String originalCode = sequences[sequences.length-1];
			
			double estLikelihoodOfSuccess = codeSequence.predictLikelihoodOfSuccess(
					successfulMarkovModel.localTransitionProbabilityMap, successfulMarkovModel.localStateFrequencyMap,
					codeSequence);
			
			double estLikelihoodOfFailure = codeSequence.predictLikelihoodOfFailure(unsuccessfulMarkovModel.localTransitionProbabilityMap,
					unsuccessfulMarkovModel.localStateFrequencyMap,codeSequence);
			
			double deltaLevel = estLikelihoodOfSuccess - estLikelihoodOfFailure;
			
			//if(deltaLevel <= 5 && deltaLevel > -20){
			if(estLikelihoodOfFailure < 0 && deltaLevel < delta){
				predictedCode = "400";
			}
			else{
				predictedCode = "500";
			}
			
			if(originalCode.equalsIgnoreCase(predictedCode)){
				accurate++;
				if(originalCode.equalsIgnoreCase("400")){
					/*System.out.println(deltaLevel + " :" + originalCode + " : " + predictedCode + codeSequence.predictLikelihoodOfSuccess(
							successfulMarkovModel.localTransitionProbabilityMap, successfulMarkovModel.localStateFrequencyMap,
							codeSequence) + " : " + codeSequence.predictLikelihoodOfFailure(unsuccessfulMarkovModel.localTransitionProbabilityMap,
									unsuccessfulMarkovModel.localStateFrequencyMap,codeSequence));
					*/
					accurate400++;
					tn++;
				}
				
				if(originalCode.equalsIgnoreCase("500")){
					accurate500++;
					tp++;
				}
			}
			else{
				
				if(originalCode.equalsIgnoreCase("400")){
					/*System.out.println(deltaLevel + " :" + originalCode + " : " + predictedCode + codeSequence.predictLikelihoodOfSuccess(
							successfulMarkovModel.localTransitionProbabilityMap, successfulMarkovModel.localStateFrequencyMap,
							codeSequence) + " : " + codeSequence.predictLikelihoodOfFailure(unsuccessfulMarkovModel.localTransitionProbabilityMap,
									unsuccessfulMarkovModel.localStateFrequencyMap,codeSequence));
					*/
					inaccurate400++;
					fp++;
				}
				else{
					inaccurate500++;
					fn++;
				}
			}
			
			if(originalCode.equalsIgnoreCase("500")){
				original500++;
			}
			
			if(originalCode.equalsIgnoreCase("400")){
				original400++;
			}
			
		}
		
		double accuracy = (double)accurate/successfulMarkovModel.get_testCodeSequenceList().size();
		results = accuracy + results;
		
		System.out.printf("\nAccuracy: %.2f", accuracy);
		sumRatio500 = sumRatio500 + (inaccurate400/(inaccurate400+accurate400));
		//sumRatio400 = sumRatio400 + (inaccurate500/(inaccurate500+original400));
		//System.out.println("\nRatio: " + (accurate400/(accurate400+inaccurate400)) + ", Data-400: " + (accurate400+inaccurate400) + ", SumRatio: " + sumRatio);
	}
	
	private void setDelta(MarkovModel successfulMarkovModel, MarkovModel unsuccessfulMarkovModel){
		// Set threshhold value for new data
		double minDelta = 0, maxDelta = 0, deltaSum = 0;
		for(int i=0; i < successfulMarkovModel.get_trainCodeSequenceList().size(); i++){
			double currentDelta = successfulMarkovModel.get_trainCodeSequenceList().get(i).getLikelihoodOfSuccess()-
					successfulMarkovModel.get_trainCodeSequenceList().get(i).predictLikelihoodOfFailure(
					successfulMarkovModel.localTransitionProbabilityMap, successfulMarkovModel.localStateFrequencyMap,
					successfulMarkovModel.get_trainCodeSequenceList().get(i));
			if(currentDelta < minDelta)
				minDelta = currentDelta;
			if(currentDelta > maxDelta)
				maxDelta = currentDelta;
	
			deltaSum += currentDelta;
			
			//System.out.println(currentDelta);
		}
		
		// Print threshhold value
		//System.out.println("\nSuccessful Min: " + minDelta + ", Max: " + maxDelta);
		//delta = deltaSum/successfulMarkovModel.get_trainCodeSequenceList().size();
		//delta = 1.0;
		
		minDelta = 0; maxDelta = 0; deltaSum = 0;
		for(int i=0; i < unsuccessfulMarkovModel.get_trainCodeSequenceList().size(); i++){
			double currentDelta = unsuccessfulMarkovModel.get_trainCodeSequenceList().get(i).getLikelihoodOfSuccess()-
					unsuccessfulMarkovModel.get_trainCodeSequenceList().get(i).predictLikelihoodOfFailure(
					unsuccessfulMarkovModel.localTransitionProbabilityMap, unsuccessfulMarkovModel.localStateFrequencyMap,
					unsuccessfulMarkovModel.get_trainCodeSequenceList().get(i));
			if(currentDelta < minDelta)
				minDelta = currentDelta;
			if(currentDelta > maxDelta)
				maxDelta = currentDelta;
	
			deltaSum += currentDelta;
			//System.out.println(currentDelta);
		}
		
		// Print threshhold value
		//System.out.println("\nUnsuccessful Min: " + minDelta + ", Max: " + maxDelta);
	}
	
	private void initialize(int startIndex, int endIndex) throws Exception{
		// Initialize training and test set
		for(int i=0; i < data.size(); i++){
			if(i >= startIndex && i <= endIndex)
				testData.add(data.get(i));
			else
				trainData.add(data.get(i));
		}
		
		// Write the sequence to file by their label
		PrintWriter successTrainWriter = new PrintWriter("SequentialData/train/successful.txt");
		PrintWriter failureTrainWriter = new PrintWriter("SequentialData/train/unsuccessful.txt");
		for(int i=0; i < trainData.size(); i++){
			String[] sequence = trainData.get(i).toString().split(",");
			if(successCode.equalsIgnoreCase(sequence[sequence.length-1]))
				successTrainWriter.println(trainData.get(i).toString());
			else
				failureTrainWriter.println(trainData.get(i).toString());						
		}
		
		successTrainWriter.flush();
		failureTrainWriter.flush();
		
		PrintWriter testWriter = new PrintWriter("SequentialData/test/testdata.txt");
		for(int i=0; i < testData.size(); i++){
			testWriter.println(testData.get(i).toString());
		}
		
		successTrainWriter.close();
		testWriter.close();
		failureTrainWriter.close();
	}
}

abstract class MarkovModel{
	protected ArrayList<CodeSequence> _trainCodeSequenceList = new ArrayList<>();
	protected ArrayList<CodeSequence> _testCodeSequenceList = new ArrayList<>();
	protected HashMap<String, Integer> localTransitionProbabilityMap = new HashMap<>();
	protected HashMap<String, Integer> localStateFrequencyMap = new HashMap<>();
	
	public abstract void createSequence();
	public abstract void buildModel();
	
	public ArrayList<CodeSequence> get_trainCodeSequenceList() {
		return _trainCodeSequenceList;
	}
	
	public void set_trainCodeSequenceList(ArrayList<CodeSequence> trainCodeSequenceList) {
		this._trainCodeSequenceList = trainCodeSequenceList;
	}
	
	public ArrayList<CodeSequence> get_testCodeSequenceList() {
		return _testCodeSequenceList;
	}
	public void set_testCodeSequenceList(
			ArrayList<CodeSequence> testCodeSequenceList) {
		this._testCodeSequenceList = testCodeSequenceList;
	}
	public HashMap<String, Integer> getLocalTransitionProbabilityMap() {
		return localTransitionProbabilityMap;
	}
	public void setLocalTransitionProbabilityMap() {
		HashMap<String, Integer> localTransitionProbabilityMap = new HashMap<>();
		// Initialize transition probabilities and state frequency
		for(int i=0; i < _trainCodeSequenceList.size(); i++){
			CodeSequence codeSequence = _trainCodeSequenceList.get(i);
			Object[] states = codeSequence.getStates().toArray();
			for(int j=0; j < states.length-1; j++){
				State state = (State) states[j];
				State stateNext = (State) states[j+1];
				String key = (state.getState() + "-" + stateNext.getState()).trim();
				// Add transition probabilities
				if(localTransitionProbabilityMap.containsKey(key)){
					localTransitionProbabilityMap.put(key, localTransitionProbabilityMap.get(key)+1);
				}
				else{
					localTransitionProbabilityMap.put(key, 1);
				}
			}
		}
		
		this.localTransitionProbabilityMap = localTransitionProbabilityMap;
	}
	public HashMap<String, Integer> getLocalStateFrequencyMap() {
		return localStateFrequencyMap;
	}
	public void setLocalStateFrequencyMap() {
		HashMap<String, Integer> localStateFrequencyMap = new HashMap<>();
		// Initialize transition probabilities and state frequency
		for(int i=0; i < _trainCodeSequenceList.size(); i++){
			CodeSequence codeSequence = _trainCodeSequenceList.get(i);
			Object[] states = codeSequence.getStates().toArray();
			for(int j=0; j < states.length-1; j++){
				State state = (State) states[j];
				// Add state frequency
				if(localStateFrequencyMap.containsKey(state.getState().trim())){
					localStateFrequencyMap.put(state.getState().trim(), localStateFrequencyMap.get(state.getState().trim())+1);
				}
				else{
					localStateFrequencyMap.put(state.getState().trim(), 1);
				}
			}
		}
		this.localStateFrequencyMap = localStateFrequencyMap;
	}
}

class SuccessfulMarkovModel extends MarkovModel{
	public void createSequence(){
		try {
			this.set_trainCodeSequenceList(UtilityClass.readSuccessfulSequence("train"));
			this.set_testCodeSequenceList(UtilityClass.readTestSequence());
			this.setLocalTransitionProbabilityMap();
			this.setLocalStateFrequencyMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildModel(){
		ArrayList<CodeSequence> train = this.get_trainCodeSequenceList();
        for (Iterator<CodeSequence> it = train.iterator(); it.hasNext();){
        	CodeSequence codeSequence = it.next();
        	codeSequence.setLikelihoodOfSuccess(localTransitionProbabilityMap, localStateFrequencyMap);
        }
        
        ArrayList<CodeSequence> test = this.get_testCodeSequenceList();
        for (Iterator<CodeSequence> it = test.iterator(); it.hasNext();){
        	CodeSequence codeSequence = it.next();
        	codeSequence.setLikelihoodOfSuccess(localTransitionProbabilityMap, localStateFrequencyMap);
        }
	}
}

class UnsuccessfulMarkovModel extends MarkovModel{
	public void createSequence(){
		try {
			this.set_trainCodeSequenceList(UtilityClass.readUnsuccessfulSequence("train"));
			this.set_testCodeSequenceList(UtilityClass.readTestSequence());
			this.setLocalTransitionProbabilityMap();
			this.setLocalStateFrequencyMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildModel(){
		ArrayList<CodeSequence> train = this.get_trainCodeSequenceList();
        for (Iterator<CodeSequence> it = train.iterator(); it.hasNext();){
        	CodeSequence codeSequence = it.next();
        	codeSequence.setLikelihoodOfFailure(localTransitionProbabilityMap, localStateFrequencyMap);
        }
        
        ArrayList<CodeSequence> test = this.get_testCodeSequenceList();
        for (Iterator<CodeSequence> it = test.iterator(); it.hasNext();){
        	CodeSequence codeSequence = it.next();
        	codeSequence.setLikelihoodOfFailure(localTransitionProbabilityMap, localStateFrequencyMap);
        }
	}
}


