import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Skeleton code for a type extractor.
 */
public class TypeExtractor {

    /**
    Given as argument a Wikipedia file, the task is to run through all Wikipedia articles,
    and to extract for each article the type (=class) of which the article
    entity is an instance. For example, from a page starting with "Leicester is a city",
    you should extract "city". 
    
    - extract just the head noun ("American rock star" -> "star")
    - if the type cannot reasonably be extracted ("Mathematics was invented in the 19th century"),
      skip the article (do not output anything)
    - take only the first item of a conjunction ("and")
    - do not extract too general words ("type of", "way", "form of")
    - keep the plural
    
    The output shall be printed to file "result.tsv" in the form
       entity TAB type NEWLINE
    with one or zero lines per entity.    
     */
	
	public static String findType(String title, String content) {
		String traitTitle[] = title.split("\\s+");
		String key = traitTitle[traitTitle.length-1];
		String type = null;

		//if(content.contains(key)) {
			String traitContent[] = content.split("\\s+");
			ArrayList<ArrayList> words_list = new ArrayList<ArrayList>();
			for(String c : traitContent) {
				c = c.trim();
				String words[] = c.split("/");
				ArrayList<String> words_prov = new ArrayList<String>();
				words_prov.add(words[0]);
				words_prov.add(words[1]);
				words_list.add(words_prov);
			}
			
			for(int i = 0; i< words_list.size(); i++) {
				for(int j = 0; j < words_list.get(i).size(); j++) {
					/**
					 * we check the position of the word whose type is "VBZ"
					 * words like : is, are ...
					 */
					if(words_list.get(i).get(j).toString().contains("VBZ") || words_list.get(i).get(j).toString() == ("VB")
							||words_list.get(i).get(j).toString().contains("VBD")||words_list.get(i).get(j).toString().contains("VBP")) {
						int indice = i + 1;
						
						for(int x = indice; x < words_list.size(); x++) {
							/**
							 * if we have one NN, we take the word
							 * if we have several NN, we take the last word who has the type NN
							 * 
							 */
							
							if(words_list.get(x).get(1).toString().contains("NN") && words_list.get(x+1).get(1).toString().contains("NN")) {
								continue;
							}else if(words_list.get(x).get(1).toString().contains("NN") && !words_list.get(x+1).get(1).toString().contains("NN") 
									&& !words_list.get(x+1).get(1).toString().contains("POS") && (!words_list.get(x-1).get(1).toString().contains("IN") 
									|| words_list.get(x-1).get(0).toString().contains("as"))) {
								
								/*if(words_list.get(x+1).get(1).toString().contains("VBN")) {
									for(int n = x + 2; n < words_list.size(); n++) {
										if(words_list.get(n).get(1).toString().contains("NN") && words_list.get(n+1).get(1).toString().contains("NN")) {
											continue;
										}else {
											if(words_list.get(n).get(1).toString().contains("NN") && !words_list.get(n+1).get(1).toString().contains("NN")) {
												if(title.contains("Tropical cyclone")) {
													System.out.println("aa");
													System.out.println(n);
													
												}
												
												type = words_list.get(x).get(0).toString();
												if (type.hashCode() == ("way".hashCode()) || type.hashCode() == ("form".hashCode()) || type.hashCode() == ("type").hashCode() 
														|| type.hashCode() == ("ways".hashCode()) || type.hashCode() == ("forms".hashCode()) || type.hashCode() == ("types").hashCode() ){
													return null;
												}
												return type;
											}
										}
									}
								}else {  */
									type = words_list.get(x).get(0).toString();
									if (type.hashCode() == ("way".hashCode()) || type.hashCode() == ("form".hashCode()) || type.hashCode() == ("type").hashCode() 
											|| type.hashCode() == ("ways".hashCode()) || type.hashCode() == ("forms".hashCode()) || type.hashCode() == ("types").hashCode() ){
										return null;
									}
									return type;
								//}
								
							}else if(words_list.get(x).get(1).toString().contains("NN") && words_list.get(x+1).get(1).toString().contains("POS")
									&& !words_list.get(x-1).get(1).toString().contains("IN")) {
								for(int m = x + 2 ; m < words_list.size(); m++) {
									if(words_list.get(x+2).get(1).toString().contains("NN") && !words_list.get(x+2).get(1).toString().contains("NN")) {
										type = words_list.get(x+2).get(0).toString();
										if (type.hashCode() == ("way".hashCode()) || type.hashCode() == ("form".hashCode()) || type.hashCode() == ("type").hashCode() 
												|| type.hashCode() == ("ways".hashCode()) || type.hashCode() == ("forms".hashCode()) || type.hashCode() == ("types").hashCode() ){
											return null;
										}
										return type;
									}else if(words_list.get(x+2).get(1).toString().contains("NN") && words_list.get(x+2).get(1).toString().contains("NN")) {
										continue;
									}
								}
								
								
							}
							
						}
					}
				}
			}
		/*
		}else {
			return null;
		}
		*/
		return null;
    }
	// do not extract too general words ("type of", "way", "form of")

    public static void main(String args[]) throws IOException {
    		try (Writer out = new OutputStreamWriter(new FileOutputStream("results.tsv"), "UTF-8")) {
            try (Parser parser = new Parser(new File(args[0]))) {
                while (parser.hasNext()) {
                    Page nextPage = parser.next();
                    // Magic happens here
                    String title = nextPage.title;
                    String content = nextPage.content;
                    String type = findType(title,content);
                    if (type != null) out.write(nextPage.title + "\t" + type + "\n");
                }

            }
        }
    }

}