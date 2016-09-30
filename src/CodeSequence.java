import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;


public class CodeSequence implements Comparator<CodeSequence>{
	private ArrayList<State> _states = new ArrayList<>();
	private double _likelihoodOfSuccess;
	private double _likelihoodOfFailure;
	private double _delta;
	private final double dummyProbability = Math.pow(10, -50);
	
	// Constructor
	public CodeSequence(){
		_likelihoodOfSuccess = -1;
		_likelihoodOfFailure = -1;
		_delta = -1;
	}
	public CodeSequence(String codeSequence){
		String[] sequences = codeSequence.split(",");
		for(int i=0; i < sequences.length; i++){
			State state = new State(sequences[i]);
			add(state);
		}
	}
	
	public void add(State state)
    {
    	_states.add(state);
    }
    public ArrayList<State> getStates()
    {
        return _states;
    }
    public void setStates(ArrayList<State> states) {
		this._states = states;
	}
    
	public double getLikelihoodOfSuccess() {
		return this._likelihoodOfSuccess;
	}
	public void setLikelihoodOfSuccess(HashMap<String, Integer> transitionProbabilityMap
			, HashMap<String, Integer> stateFrequencyMap) {
		double probabilityOfSuccess = 1;
		for(int i=0; i < _states.size()-1; i++){
			if(i == _states.size()-2){
				String key = _states.get(i).getState() + "-" + "500";
				if(transitionProbabilityMap.containsKey(key)){
					probabilityOfSuccess = probabilityOfSuccess*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));
				}
				else
					probabilityOfSuccess = probabilityOfSuccess*dummyProbability;
			}
			else{
				String key = _states.get(i).getState() + "-" + _states.get(i+1).getState();
				if(transitionProbabilityMap.containsKey(key)){
					probabilityOfSuccess = probabilityOfSuccess*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));	
				}
				else
					probabilityOfSuccess = probabilityOfSuccess*dummyProbability;
			}
		}
		this._likelihoodOfSuccess = Math.log(probabilityOfSuccess);
	}
	
	public double predictLikelihoodOfSuccess(HashMap<String, Integer> transitionProbabilityMap
			, HashMap<String, Integer> stateFrequencyMap, CodeSequence codeSequence) {
		double probabilityOfSuccess = 1;
		for(int i=0; i < codeSequence.getStates().size()-1; i++){
			if(i == codeSequence.getStates().size()-2){
				String key = codeSequence.getStates().get(i).getState() + "-" + "500";
				if(transitionProbabilityMap.containsKey(key)){
					probabilityOfSuccess = probabilityOfSuccess*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));
				}
				else
					probabilityOfSuccess = probabilityOfSuccess*dummyProbability;
			}
			else{
				String key = codeSequence.getStates().get(i).getState() + "-" + codeSequence.getStates().get(i+1).getState();
				if(transitionProbabilityMap.containsKey(key)){
					probabilityOfSuccess = probabilityOfSuccess*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));	
				}
				else
					probabilityOfSuccess = probabilityOfSuccess*dummyProbability;
			}
		}
		return Math.log(probabilityOfSuccess);
	}
	
	public double getLikelihoodOfFailure() {
		return _likelihoodOfFailure;
	}
	public void setLikelihoodOfFailure(HashMap<String, Integer> transitionProbabilityMap
			, HashMap<String, Integer> stateFrequencyMap) {
		double probabilityOfFailure = 1;
		for(int i=0; i < _states.size()-1; i++){
			if(i == _states.size()-2){
				String key = _states.get(i).getState() + "-" + "400";
				if(transitionProbabilityMap.containsKey(key))
					probabilityOfFailure = probabilityOfFailure*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));
				else
					probabilityOfFailure = probabilityOfFailure*dummyProbability;
			}
			else{
				String key = _states.get(i).getState() + "-" + _states.get(i+1).getState();
				if(transitionProbabilityMap.containsKey(key))
					probabilityOfFailure = probabilityOfFailure*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));	
				else
					probabilityOfFailure = probabilityOfFailure*dummyProbability;
			}
		}
		this._likelihoodOfFailure = Math.log(probabilityOfFailure);
	}
	
	public double predictLikelihoodOfFailure(HashMap<String, Integer> transitionProbabilityMap
			, HashMap<String, Integer> stateFrequencyMap, CodeSequence codeSequence) {
		double probabilityOfFailure = 1;
		for(int i=0; i < codeSequence.getStates().size()-1; i++){
			if(i == codeSequence.getStates().size()-2){
				String key = codeSequence.getStates().get(i).getState() + "-" + "400";
				if(transitionProbabilityMap.containsKey(key))
					probabilityOfFailure = probabilityOfFailure*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));
				else
					probabilityOfFailure = probabilityOfFailure*dummyProbability;
			}
			else{
				String key = codeSequence.getStates().get(i).getState() + "-" + codeSequence.getStates().get(i+1).getState();
				if(transitionProbabilityMap.containsKey(key))
					probabilityOfFailure = probabilityOfFailure*((double)transitionProbabilityMap.get(key)/stateFrequencyMap.get(_states.get(i).getState()));
				else
					probabilityOfFailure = probabilityOfFailure*dummyProbability;
			}
		}
		return Math.log(probabilityOfFailure);
	}
	
	public double predictDeltaLevel(HashMap<String, Integer> transitionProbabilityMap
			, HashMap<String, Integer> stateFrequencyMap){
		setLikelihoodOfSuccess(transitionProbabilityMap, stateFrequencyMap);
		setLikelihoodOfFailure(transitionProbabilityMap, stateFrequencyMap);
		set_delta();
		return get_delta();
	}
	
	public double get_delta() {
		return _delta;
	}
	public void set_delta() {
		this._delta = _likelihoodOfSuccess-_likelihoodOfFailure;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(State state:_states){
			if(sb.length() != 0)
				sb.append(",");
			sb.append(state.getState());
		}
		return sb.toString();
	}
	
	public int compare(CodeSequence obj1, CodeSequence obj2) {
    	if(obj1._likelihoodOfSuccess-obj2._likelihoodOfSuccess < 0)
    		return -1;
    	else if(obj1._likelihoodOfSuccess-obj2._likelihoodOfSuccess > 0)
    		return 1;
    	else
    		return 0;
    }
	
	public static class Comparators {

	    public static Comparator<CodeSequence> SUCCESS = new Comparator<CodeSequence>() {
	        @Override
	        public int compare(CodeSequence obj1, CodeSequence obj2) {
	        	if(obj1._likelihoodOfSuccess-obj2._likelihoodOfSuccess < 0)
	        		return -1;
	        	else if(obj1._likelihoodOfSuccess-obj2._likelihoodOfSuccess > 0)
	        		return 1;
	        	else
	        		return 0;
	        }
	    };
	    public static Comparator<CodeSequence> FAILURE = new Comparator<CodeSequence>() {
	        @Override
	        public int compare(CodeSequence obj1, CodeSequence obj2) {
	        	if(obj1._likelihoodOfFailure-obj2._likelihoodOfFailure < 0)
	        		return -1;
	        	else if(obj1._likelihoodOfFailure-obj2._likelihoodOfFailure > 0)
	        		return 1;
	        	else
	        		return 0;
	        }
	    };
	    
	    public static Comparator<CodeSequence> DELTALEVEL = new Comparator<CodeSequence>() {
	        @Override
	        public int compare(CodeSequence obj1, CodeSequence obj2) {
	        	if(obj1._delta-obj2._delta < 0)
	        		return -1;
	        	else if(obj1._delta-obj2._delta > 0)
	        		return 1;
	        	else
	        		return 0;
	        }
	    };
	}
}


class State
{
    private String _code;

    // Constructor
    public State(String code)
    {
        this._code = code;
    }

    // Gets code
    public String getState()
    {
        return _code;
    }
}


