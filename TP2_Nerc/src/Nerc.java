import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Skeleton for NERC task.
 * 
 * @author Fabian M. Suchanek
 *
 */
public class Nerc {
	private static ArrayList l_GEO = new ArrayList();
	private static ArrayList l_ORG = new ArrayList();
	private static ArrayList l_NAT = new ArrayList();

	
    /** Labels that we will attach to the words*/
    public enum Class {
        ARTIFACT, EVENT, GEO, NATURAL, ORGANIZATION, PERSON, TIME, OTHER
    }
    
    public static boolean verifTime(Window window) {
    		//dictionary features
		Pattern p = Pattern.compile("((?i)Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|January|February|March|April|May|June|July|August|September|October|November|December"
				+ "|[1][0-9][0-9][0-9]|[2][0-9][0-9][0-9])");
		Matcher m = p.matcher(window.getWordAt(0));
	    	if (m.find()==true){
	    		return true;
	    	}else {
		    	return false;
	    	}  	
    }
    
    //dictionary features
    public static ArrayList getGeo() {
        //args = new String[] { "/Users/fabian/Data/ner-test.tsv", "/Users/fabian/Data/ner-train.tsv" };
    		String[] args2 = new String[] {"/Users/fabian/Data/ner-train.tsv" };
        try (BufferedReader in = Files.newBufferedReader(Paths.get(args2[0]))) {
        		String line;
            while (null != (line = in.readLine())) {
            		if(line.contains("GEO")) {
            			String line2[] = line.split("\\s+");
            			l_GEO.add(line2[1]);
            		}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
        return l_GEO;
    }
    
	public static boolean verifGEO(Window window) {
		// dictionary features
		if(l_GEO.contains(window.getWordAt(0))){
			return true;
		}else {
			return false;
		}
	}
	
	//postag features
	public static boolean verifPerson(Window window) {
		if (((window.getTagAt(0).contains("NNP") && (window.getTagAt(1).contains("NNP")||window.getTagAt(1).contains("VBD")||window.getTagAt(1).contains("VBZ"))))){
			return true;
		}else {
			return false;
		}			
	}
	
	//dictionary features + postag
    public static ArrayList getOrg() {
        String[] args2 = new String[] {"/Users/fabian/Data/ner-train.tsv" };
        try (BufferedReader in = Files.newBufferedReader(Paths.get(args2[0]))) {
        		String line;
            while (null != (line = in.readLine())) {
            		if(line.contains("ORGANIZATION")) {
            			String line2[] = line.split("\\s+");
            			l_ORG.add(line2[1]);
            		}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
        return l_ORG;
    }
    
	public static boolean verifOrg(Window window) {
		if(l_ORG.contains(window.getWordAt(0)) && (window.getTagAt(0).contains("NNP"))){
			return true;
		}else {
			return false;
		}			
	}
	
	//dictionary features
    public static ArrayList getNatual() {
        String[] args2 = new String[] {"/Users/fabian/Data/ner-train.tsv"};
        try (BufferedReader in = Files.newBufferedReader(Paths.get(args2[0]))) {
        		String line;
            while (null != (line = in.readLine())) {
            		if(line.contains("NATURAL")) {
            			String line2[] = line.split("\\s+");
            			l_NAT.add(line2[1]);
            		}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
        return l_NAT;
    }

    public static boolean verifNat(Window window) {
		if(l_NAT.contains(window.getWordAt(0)) && (window.getTagAt(0).contains("NNP")||window.getTagAt(0).contains("NNPS")
				||window.getTagAt(0).contains("NN")||window.getTagAt(0).contains("NNS"))){
			return true;
		}else {
			return false;
		}			
	}
    
    //postag features
  	public static boolean verifEVEN(Window window) {
  		if (window.getTagAt(0).contains("NNP")||window.getTagAt(0).contains("NNPS") 
  				&& (window.getTagAt(1).contains("DT")||window.getTagAt(1).contains("NN")
  				||window.getTagAt(1).contains("RN")||window.getTagAt(1).contains("JJ"))){
  			return true;
  		}else {
  			return false;
  		}			
  	}
  	
  //postag features
  	public static boolean verifART(Window window) {
  		if (window.getTagAt(0).contains("NNP")||window.getTagAt(0).contains("NNPS") 
  				&& (window.getTagAt(1).contains("DT")||window.getTagAt(1).contains("NN")
  				||window.getTagAt(1).contains("RN")||window.getTagAt(1).contains("JJ"))){
  			return true;
  		}else {
  			return false;
  		}			
  	}
  	
    
    /** Determines the class for the word at position 0 in the window*/
    public static Class findClass(Window window) {
    		if (verifTime(window)==true){
	    		return Class.TIME;
	    	}else if (verifGEO(window)==true){
	    		return Class.GEO;
	    	}else if (verifPerson(window)==true){
	    		return Class.PERSON;
	    	}else if (verifOrg(window)==true){
	    		return Class.ORGANIZATION;
	    	}else if (verifOrg(window)==true) {
	    		return Class.NATURAL;
	    	}else if(verifEVEN(window)==true) {
	    		return Class.EVENT;
	    	}if(verifEVEN(window)==true) {
	    		return Class.ARTIFACT;
	    	}
		return (Class.OTHER);
	    	  	
    }

    
    

    /** Takes as arguments:
     * (1) a testing file with sentences
     * (2) optionally: a training file with labeled sentences
     * 
     *  Writes to the file result.tsv lines of the form
     *     X-WORD \t CLASS
     *  where X is a sentence number, WORD is a word, and CLASS is a class.
     */
    public static void main(String[] args) throws IOException {
        args = new String[] { "/Users/fabian/Data/ner-test.tsv", "/Users/fabian/Data/ner-train.tsv" };
        //args = new String[] { "/Users/zhaomengzi/Downloads/M2/Knowledge_Base_Contruction/TP1/TP2_Nerc/corpus/ner-test.tsv", "/Users/zhaomengzi/Downloads/M2/Knowledge_Base_Contruction/TP1/TP2_Nerc/corpus/ner-train.tsv" };
        ArrayList l1 = getGeo();
        ArrayList l2 = getOrg();
        ArrayList l3 = getNatual();
        // EXPERIMENTAL: If you wish, you can train a KNN classifier here
        // on the file args[1].
        // KNN<Nerc.Class> knn = new KNN<>(5);
        // knn.addTrainingExample(Nerc.Class.ARTIFACT, 1, 2, 3);
        
		try (BufferedWriter out = Files.newBufferedWriter(Paths.get("result.tsv"))) {
			try (BufferedReader in = Files.newBufferedReader(Paths.get(args[0]))) {
					String line;
					Window window = new Window(5);
					while (null != (line = in.readLine())) {
						window.add(line);
						// System.out.println(line);
						if (window.getWordAt(-window.width) == null)
							continue;
						Class c = findClass(window);
						if (c != null && c != Class.OTHER)
							out.write(window.getSentenceNumberAt(0) + "-" + window.getWordAt(0) + "\t" + c + "\n");
					}
				
			}
		}
    }
}
