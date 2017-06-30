import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.mallet.fst.*;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SimpleTaggerSentence2TokenSequence;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.InstanceList;



public class CRFClassifier{
	
	double accuracy = 0, precission = 0, recall = 0, fmeasure = 0, kappa_stat = 0;
	PrintWriter pw, pwauc;
	
	public void evaluateModel(int folds) throws IOException{
		
		pw = new PrintWriter(new FileWriter(("report/resultcrf.txt"), true));
		pwauc = new PrintWriter(new FileWriter(("report/resultcrf-auc.txt"), true));
		//prepare instance transformation pipeline
		InstanceList trainingInstanceList = new InstanceList(buildPipe());
		
		// get distinct code
		Map<String, String> distCode = UtilityClass.readMergeCode("code/all_collapsemap.txt");
        
        File rawDataFolder = new File("crfdata");
		File []rawFiles = rawDataFolder.listFiles();
		
		for (int i = 0; i < rawFiles.length; ++i) {
			trainingInstanceList.addThruPipe(new CsvIterator(new FileReader(rawFiles[i]), "(\\d+)\\s+(\\w+)\\s+(.*)", 3, 2, 1));
		}
		
        
		System.out.println(trainingInstanceList.size());
		     
        InstanceList.CrossValidationIterator cvIter = trainingInstanceList.crossValidationIterator(folds); // for 10-folds
	    while (cvIter.hasNext()) {
	    	InstanceList[] trainTestSplits = cvIter.nextSplit();
	        InstanceList trainSplit = trainTestSplits[0];
	        InstanceList testSplit = trainTestSplits[1];
	   
	        /*System.out.println(trainSplit.get(0).getName());
	        System.out.println(trainSplit.get(0).getTarget());
	        System.out.println(trainSplit.get(0).getData());
	        System.out.println();
	        System.out.println(trainSplit.get(1).getName());
	        System.out.println(trainSplit.get(1).getTarget());
	        System.out.println(trainSplit.get(1).getData());
	        System.out.println();
	        System.out.println(trainSplit.get(2).getName());
	        System.out.println(trainSplit.get(2).getTarget());
	        System.out.println(trainSplit.get(2).getData().toString());
	        break;*/
	        
	        run(trainSplit, testSplit, distCode, folds);
	    }
	    
	    precission = precission/folds;
	    recall = recall/folds;
	    fmeasure = (2*precission*recall)/(precission + recall);
	    kappa_stat = kappa_stat/folds;
	    
	    System.out.printf("Accuracy: %.2f%%", (accuracy/folds)*100);
	    System.out.printf("	Precission: %.3f", precission);
	    System.out.printf("	Recall: %.3f", recall);
	    System.out.printf("	F-Measure: %.3f", fmeasure);
	    System.out.printf("	Kappa: %.3f", kappa_stat);

		pw.println("\n\n==== CRF MODEL ====\n\n");
		pw.println("==== Classification Summary ====");
		pw.println("Ten folds cross validation accuracy: " + ((accuracy/folds)*100) + "%");
		pw.println("Precision: " + precission);
		pw.println("Recall: " + recall);
		pw.println("F-Measure: " + fmeasure);
		pw.println("Kappa: " + kappa_stat);
		pw.close();
		pwauc.close();
	}
	
	public Pipe buildPipe() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add(new SimpleTaggerSentence2TokenSequence());
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequenceRemoveStopwords(false, true));
        pipeList.add(new TokenSequence2FeatureVectorSequence());
        return new SerialPipes(pipeList);
    }
	
	public void run (InstanceList trainingData, InstanceList testingData, Map<String, String> distCode, int folds) throws IOException {
	      
		double localprecission = 0, localrecall = 0, localKappa = 0;
		
		// model
	      CRF crf = new CRF(trainingData.getDataAlphabet(), trainingData.getTargetAlphabet());
	      
	      // set states and weight
		  crf.addFullyConnectedStatesForLabels();
	      crf.addStartState();
	     
	      // create trainer
	      CRFTrainerByThreadedLabelLikelihood crfTrainer = new CRFTrainerByThreadedLabelLikelihood(crf, 50);
	      //CRFTrainerByLabelLikelihood crfTrainer = new CRFTrainerByLabelLikelihood(crf);
	      
	      // create evaluator
		  InstanceAccuracyEvaluator iacu = new InstanceAccuracyEvaluator();
		  crfTrainer.addEvaluator(iacu); 
		  
		  // evaluate
	      crfTrainer.train(trainingData, Integer.MAX_VALUE);
	       
	      // evaluate accuracy
	      iacu.evaluateInstanceList(crfTrainer, testingData, "testing instances: ");
	      accuracy = accuracy + iacu.getAccuracy("testing instances: ");
	      
	      Map<String, Integer> codeFreq = new HashMap<String, Integer>(trainingData.getTargetAlphabet().size());
		  for (String key:distCode.keySet()) {
	    	  codeFreq.put(key.trim(), 0);
	      }
	      
		  Map<String, Integer> actualCodeFreq = new HashMap<String, Integer>(codeFreq);
		  Map<String, Integer> tpMap = new HashMap<String, Integer>(codeFreq);
		  
		  //System.out.println("output: " + crf.predict(testingData));
		  
	      for(int i = 0; i < testingData.size(); i++){
	    	  
	    	  testingData.get(i).unLock();
		      
		      //System.out.print("Target: "+ testingData.get(i).getTarget() + ":");
		      
		      FeatureVectorSequence input =  (FeatureVectorSequence) testingData.get(i).getData();
		      //for (int index=0; index < output.size(); index++) {
		      //System.out.print(output.get(index) + " ");
		      //}
		     
		      //System.out.println("output: " + crf.transduce(output));
//		      double logScore = new SumLatticeDefault(crf,input,crf.transduce(input)).getTotalWeight();
//		      double logZ = new SumLatticeDefault(crf,input).getTotalWeight();
//		      double prob = Math.exp(logScore - logZ);
//		
//		      PrintWriter pwriter = new PrintWriter(new FileWriter("crf-result/"+rocFile, true), true);
//		      if(crf.transduce(input).toString().trim().contains(testingData.get(i).getTarget().toString().substring(3, 6).trim()))
//		    	  pwriter.println(testingData.get(i).getTarget().toString().substring(3, 6).trim() + "," + prob + "," + 1);
//		      else
//		    	  pwriter.println(testingData.get(i).getTarget().toString().substring(3, 6).trim() + "," + prob + "," + 0);
//	
//		      pwriter.flush();
//		      
		      pwauc.println(testingData.get(i).getTarget().toString().substring(3, 6).trim() + "," + crf.transduce(input).toString().trim());	
		      pwauc.flush();
		      
		      actualCodeFreq.put(testingData.get(i).getTarget().toString().substring(3, 6).trim(), actualCodeFreq.get(testingData.get(i).getTarget().toString().substring(3, 6).trim())+1);
		      codeFreq.put(crf.transduce(input).toString().trim(), codeFreq.get(crf.transduce(input).toString().trim())+1);
		  	  if(crf.transduce(input).toString().trim().contains(testingData.get(i).getTarget().toString().substring(3, 6).trim()))
		  		tpMap.put(crf.transduce(input).toString().trim(), tpMap.get(crf.transduce(input).toString().trim())+1);
		  	  
		  	  testingData.get(i).lock();
		      
	      }
	      
	      for (String key:distCode.keySet()) {
	    	  if(codeFreq.get(key) > 0)
	    		  localprecission = localprecission + ((double)tpMap.get(key)*actualCodeFreq.get(key)/codeFreq.get(key));
	    	  
	    	  localrecall = localrecall + tpMap.get(key);
	    	  System.out.println("localprecission: " + localprecission);
	    	  
	    	  int kdata_point = testingData.size();
	    	  int ktp_fn = actualCodeFreq.get(key);
	    	  int ktn_fp = testingData.size() - ktp_fn;
	    	  int ktp_fp = codeFreq.get(key);
	    	  int ktn_fn = testingData.size() - ktp_fp;
	    	  int ktp = tpMap.get(key);
	    	  int kfp = ktp_fp - ktp;
	    	  int kfn = ktp_fn - ktp;
	    	  int ktn = ktn_fp - kfp;
	    	  int ktp_tn = ktp + ktn;
	    	  
	    	  /*System.out.println("kdata_point: " + kdata_point);
              System.out.println("ktp_fn: " + ktp_fn);
              System.out.println("ktn_fp: " + ktn_fp);
              System.out.println("ktp_fp: " + ktp_fp);
              System.out.println("ktn_fn: " + ktn_fn);
              System.out.println("ktp: " + ktp);
              System.out.println("ktp_tn: " + ktp_tn);
              System.out.println("ktn: " + ktn);*/
	    	  
              double ktotal_accuracy = (double)ktp_tn/kdata_point;
              double krand_accuracy = (double)((ktn_fp*ktn_fn)+(ktp_fn*ktp_fp))/(kdata_point*kdata_point);
              
              double kkappa = (double)(ktotal_accuracy-krand_accuracy)/(1-krand_accuracy);
              /*pw.println("ktotal_accuracy: " + ktotal_accuracy);
              pw.println("krand_accuracy: " + krand_accuracy);
              pw.println("kkappa: " + kkappa);
              pw.println();*/
              
              if(Double.isNaN(kkappa))
            	  kkappa = 1;
              localKappa = localKappa + ((double)(kkappa*ktp_fn)/kdata_point);
	      }
	      
	      localprecission = localprecission/(testingData.size());
	      localrecall = localrecall/(testingData.size());
	      
	      precission = precission + localprecission;
	      recall = recall + localrecall;
	      kappa_stat = kappa_stat + localKappa;
	      //pw.println("localKappa: " + localKappa);
	      
	      System.out.println("localprecission: " + localprecission);
	      System.out.println("localrecall: " + localrecall);
	      System.out.println("localKappa: " + localKappa);
	      
	      System.out.println("Actual: " + actualCodeFreq);
	      System.out.println("Predicted: " + codeFreq);
	      System.out.println("True Positive: " + tpMap);
	      crfTrainer.shutdown();
	}
}
