package lab5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Skeleton class for a program that maps the entities from one KB to the
 * entities of another KB.
 * 
 * @author Fabian
 *
 */

public class EntityMapper {
	
	
	/*
	public static HashMap<String, Float> localFunctionality(KnowledgeBase kb2, String entity1) {
		HashMap<String, Float> localFunc = new HashMap<String, Float>();
		ArrayList<String> relations = new ArrayList<String>();
		
		for(String relation2 : kb2.facts.get(entity1).keySet()) {
			if(!localFunc.containsKey(relation2)) {
				localFunc.put(relation2, (float) 1.0);
			}else {
				float val = localFunc.get(relation2);
				val = val + 1;
				localFunc.put(relation2, val);
				
			}
		}		
		for(String relation : localFunc.keySet()) {
			float val = localFunc.get(relation);
			val = 1/val;
			localFunc.put(relation, val);
		}
		return localFunc;
	}
	*/
	/*
	public static HashMap<String,Integer> calculateNote(HashMap<String, Float> localFunc){
		HashMap<String,Integer> notes = new HashMap<String,Integer>();
		double avg = 0.0;
		for(String key : localFunc.keySet()) {
			avg += localFunc.get(key);
		}
		avg = avg/localFunc.size();
		
		for(String key : localFunc.keySet()) {
			if(localFunc.get(key) < avg/2) {
				notes.put(key, 1);
			}else if(localFunc.get(key) < avg) {
					notes.put(key, 2);
			}else if(localFunc.get(key) > 2 * avg) {
				notes.put(key, 4);
			}else if(localFunc.get(key) > avg) {
				notes.put(key, 3);
			}
		}
		return notes;
	}
	*/
	
	//HashMap<String, HashMap<String, Integer>>
	//String : entity
	//String : relation
	//Integer : nb of relations existing for this entity
	/*
	public static HashMap<String, HashMap<String, Float>> localFunctionality(KnowledgeBase kb2) {
		HashMap<String, HashMap<String, Float>> localFunc = new HashMap<String, HashMap<String, Float>>();
		for(String entity2 : kb2.facts.keySet()) {
			for(String relation2 : kb2.facts.get(entity2).keySet()) {
				if(localFunc.containsKey(entity2)) {
					if(localFunc.containsKey(relation2)) {
						HashMap<String,Float> sub = localFunc.get(entity2);
						float val = sub.get(relation2);
						val = val + 1;
						sub.put(relation2, val);
						localFunc.put(entity2, sub);
					}else {
						HashMap<String,Float> sub = new HashMap<String, Float>();
						sub.put(relation2, (float) 1);
						localFunc.put(entity2, sub);
					}
				}else {
					HashMap<String,Float> sub = new HashMap<String, Float>();
					sub.put(relation2, (float) 1);
					localFunc.put(entity2, sub);
				}
			}
		}
		
		for(String entity : localFunc.keySet()) {
			for(String relation : localFunc.get(entity).keySet()) {
				float val = localFunc.get(entity).get(relation);
				val = val/1;
				HashMap<String,Float> sub = new HashMap<String, Float>();
				sub.put(relation, val);
				localFunc.put(relation,sub);
			}
		}
		return localFunc;
	}
	*/
	
	
	public static HashMap<String, HashMap<String, Float>> localFunctionality(KnowledgeBase kb2) {
		HashMap<String, HashMap<String, Float>> localFunc = new HashMap<String, HashMap<String, Float>>();
		for(String entity2 : kb2.facts.keySet()) {
			for(String relation2 : kb2.facts.get(entity2).keySet()) {
				if(localFunc.containsKey(entity2)) {
					if(localFunc.containsKey(relation2)) {
						HashMap<String,Float> sub = localFunc.get(entity2);
						float val = sub.get(relation2);
						val = val + 1;
						sub.put(relation2, val);
						localFunc.put(entity2, sub);
					}else {
						System.out.println("aaa");
						HashMap<String,Float> sub = new HashMap<String, Float>();
						sub.put(relation2, (float) 1);
						localFunc.put(entity2, sub);
						
					}
				}else {
					HashMap<String,Float> sub = new HashMap<String, Float>();
					sub.put(relation2, (float) 1);
					localFunc.put(entity2, sub);
				}
			}
		}
		
		for(String entity : localFunc.keySet()) {
			for(String relation : localFunc.get(entity).keySet()) {
				float val = localFunc.get(entity).get(relation);
				val = val/1;
				HashMap<String,Float> sub = new HashMap<String, Float>();
				sub.put(relation, val);
				localFunc.put(relation,sub);
			}
		}
		return localFunc;
	}
	
	private static int entityEquality(String entity1, String entity2, Map<String, Map<String, Set<String>>> facts1, Map<String, Map<String, Set<String>>> facts2 )
	{
		Map<String, Set<String>> entities1 = facts1.get(entity1);
		Map<String, Set<String>> entities2 = facts2.get(entity2);
		int cpt = 0;
		for(String relation1 : entities1.keySet())
		{
			for(String relation2 : entities2.keySet())
			{
				for(String str1 : entities1.get(relation1)){
					for(String str2 : entities2.get(relation2)){
						if(str1.equals(str2)){
							cpt++;
						}
					}
				}
			}
		}

		return cpt;
	}
	
    /**
     * Takes as input (1) one knowledge base (2) another knowledge base.
     * 
     * Prints "entity1 TAB entity2 NEWLINE" to the file results.tsv, if the first
     * entity from the first knowledge base is the same as the second entity
     * from the second knowledge base. Output 0 or 1 line per entity1.
     */
    public static void main(String[] args) throws IOException {
        KnowledgeBase kb1 = new KnowledgeBase(new File(args[0]));
        KnowledgeBase kb2 = new KnowledgeBase(new File(args[1]));
        try (Writer out = new OutputStreamWriter(new FileOutputStream("results.tsv"), "UTF-8")) {
        		for (String entity1 : kb1.facts.keySet()) {     
            		String mostLikelyCandidate = null;
            		int cpt = 0;
            		int max = 0;
            		// Something smart here
            		for(String entity2 : kb2.facts.keySet()) {
            			cpt = entityEquality(entity1,entity2,kb1.facts,kb2.facts);
            			
            			if (cpt > max) {
            				max = cpt;
                			mostLikelyCandidate = entity2;
                		}
            		}
            		
                if (mostLikelyCandidate != null) {
                    out.write(entity1 + "\t" + mostLikelyCandidate + "\n");
                }
                
            }      
        }
    }
}
