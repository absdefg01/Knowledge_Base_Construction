import java.io.*;
import java.util.*;

/**
 * Skeleton for an evaluator
 */
public class Evaluator {

	/**
	 * Takes as arguments (1) the gold standard and (2) the output of a program.
	 * Prints to the screen one line with the precision
	 * and one line with the recall.
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("usage: Evaluator, error");
			return;
		}
		
		File goldStandard = new File(args[0]);
		File result = new File(args[1]);
		
		BufferedReader in1 = new BufferedReader(new InputStreamReader(new FileInputStream(goldStandard), "UTF-8"));
		BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(result), "UTF-8"));

		String readLine_gold = null;
		String readLine_result = null;
		
		
		//golden Sample.tsv 
		Set<String> list1_GoldSample = new HashSet<>();
		Set<String> label_GoldSample = new HashSet<>();
		while( (readLine_gold = in1.readLine()) != null && readLine_gold != "" ){			
			list1_GoldSample.add(readLine_gold.replaceAll("[\\s]+", ""));
			label_GoldSample.add(readLine_gold.split("[\\s]+")[0]);
		}
		//System.out.println(list1_GoldSample.size());
		
		/**
		 * result.tsv
		 */
		Set<String> list_result = new HashSet<>();
		Set<String> label_result = new HashSet<>();
		while ( (readLine_result = in2.readLine()) != null && readLine_result != "" ){
			list_result.add(readLine_result.replaceAll("[\\s]+", ""));
			label_result.add(readLine_result.split("[\\s]+")[0]);
		}
		
		
		int nbTotalGoldSample = list1_GoldSample.size();
		//correct answer : intersection between result.csv and goldSample
		list1_GoldSample.retainAll(list_result);
		int res_correct = list1_GoldSample.size();
		//retrived label : intersection between labels in the document resuult.tsv
		//and the labels in the goldSample.tsv
		label_result.retainAll(label_GoldSample);
		int label_retrieve = label_result.size();
		
		/**
		 * précision = nb de documents correctement attribués à la classe i / nb de documents attribués à la classe i
		 */
		float precision = (float)res_correct/(float)label_retrieve;
		System.out.println("precision : " + precision);
				
		/**
		 * recall
		 */ 
		float recall = (float)res_correct/(float)nbTotalGoldSample;
		System.out.println("recall : " + recall);
		
		in1.close();
		in2.close();
	}
}
