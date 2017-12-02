import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;

/**
 * Skeleton class to perform disambiguation
 * 
 * @author Jonathan Lajus
 *
 */
public class Disambiguation {

    /**
     * This program takes 3 command line arguments, namely the paths to: 
     * - yagoLinks.tsv 
     * - yagoLabels.tsv 
     * - wikipedia-ambiguous.txt 
     * in this order.
     * 
     * The program prints statements of the following form into the file
     * results.tsv: 
     *    <pageTitle> TAB <yagoEntity> NEWLINE 
     * It is OK to skip articles.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("usage: Disambiguation <yagoLinks> <yagoLabels> <wikiText>");
            return;
        }
        File dblinks = new File(args[0]);
        File dblabels = new File(args[1]);
        File wiki = new File(args[2]);

        SimpleDatabase db = new SimpleDatabase(dblinks, dblabels);

        try (Parser parser = new Parser(wiki)) {
            try (Writer out = new OutputStreamWriter(new FileOutputStream("results.tsv"), "UTF-8")) {
                while (parser.hasNext()) {
                    Page nextPage = parser.next();
                    String pageTitle = nextPage.title; // "Clinton_1"
                    String pageContent = nextPage.content; // "Hillary Clinton was..."
                    String pageLabel = nextPage.label(); // "Clinton"
                    /**
                     * TODO CODE HERE to disambiguate the entity.
                     */
                    String correspondingYagoEntity = Disambiguation(db, pageContent, pageLabel);

					if (correspondingYagoEntity != null) {
						// System.out.println(pageTitle + "\t" + correspondingYagoEntity);
					}
                    out.write(pageTitle + "\t" + correspondingYagoEntity + "\n");
                }
            }
        }
    }
    
	public static String Disambiguation(SimpleDatabase db, String pageContent, String pageLabel) {
		Set<String> entitiesfromLabel = new HashSet<>();
		Set<String> entitiesLinked = new HashSet<>();
		Set<String> labelsLinked = new HashSet<>();
        Set<String> words = new HashSet<String>(Arrays.asList(pageContent.replace(",", "").replace(".", "").replace("\"","").replace("\'", "").split(" ")));
        int cpt = 0;
        HashMap<String, Integer> cptEntity = new HashMap<>();
        
        
        
		/**
		 * first step : 
		 * find the entities by pageLabel in the document yagoLabels
		 * yagoLabels <entities> <- label
		 */
		if(db.reverseLabels.containsKey(pageLabel)) {
			entitiesfromLabel = db.reverseLabels.get(pageLabel);
			for (String el : entitiesfromLabel) {
				//System.out.println(el);
				
				
				
				/**
				 * second step : 
				 * for every entity that we found by pageLabel
				 * we find its linked entities in the document YagoLinks
				 * then find its label
				 */
				entitiesLinked = db.links.get(el);
				//System.out.println(entitiesLinked);
				 
				for(String eLinked : entitiesLinked) {
					//System.out.println(eLinked);
					labelsLinked = db.labels.get(eLinked);
					if (labelsLinked != null) {
						
						
						
						/**
						 * third step : 
						 * for labels of those entities
						 * if label appears in the content of the page
						 * cpt++
						 * the entity who has the largest value of cpt for this page should be the result
						 */
						for(String lbLinked : labelsLinked) {
							//System.out.println(lbLinked);
							if (words.contains(lbLinked)) {
								//System.out.println("word : " + word);
								//System.out.println("lbLinked : " + lbLinked);
								cpt = cpt + 1;
							}
							
						}						
					}
				}
				cptEntity.put(el, cpt);
				cpt = 0;
			}
		}
		
		String resultEntity = null;
		List<String> resultEntities = new ArrayList<String>();
		
		int maxValue = Collections.max(cptEntity.values());
		for(Entry<String, Integer> e : cptEntity.entrySet()) {
			//System.out.println(e);
			if(e.getValue() == maxValue) {
				resultEntity = e.getKey();
				resultEntities.add(resultEntity);
			}
		}
	
		Collections.shuffle(resultEntities);
		int longeur = resultEntities.size();
		int randomNum = 0 + (int)(Math.random() * (longeur-1)); 
		return resultEntities.get(randomNum);
	}
}
