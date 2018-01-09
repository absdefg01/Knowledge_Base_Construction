package lab4;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.*;

/**
 * Takes as argument a Max-Sat-file or a folder of Max-Sat-files, 
 * writes a KB to the corresponding output file(s).
 * Does not take longer than 5 minutes PER PROBLEM.
 */
public class MaxSat {

    /** Start time*/
    public static long startTime;

    /** TRUE if we have to stop*/
    public static boolean haveToStop() {
        return (System.currentTimeMillis() - startTime > 5 * 60 * 1000);
    }

    public static Atom  bestAtoms(List<Clause> our_rules, HashSet<Atom> our_atoms){
    		HashMap<Atom,Integer> atoms_weight = new HashMap<Atom,Integer>();
    		java.util.Iterator<Atom> iterator = our_atoms.iterator();
    		
    	    while (iterator.hasNext()) {
    	    		int weight = 0;	//weight of this atom
    	    		Atom a = iterator.next();
    	    		for(int i = 0; i < our_rules.size(); i++) {
    	    			List<Atom> prov_atoms = our_rules.get(i).atoms;
    	    			for(int j = 0; j < prov_atoms.size(); j++) {
	    				if(prov_atoms.get(j).equals(a)) {
	    					weight += our_rules.get(i).weight;
    	    				}
    	    			}
    	    		}
    	    		atoms_weight.put(a, weight);
    	    }

    	    int maxWeight = Collections.max(atoms_weight.values());	//retourner le poid maximal dans le hashmap
    		
    		List<Atom> list_atoms = new ArrayList<Atom>();
	    	for(Entry<Atom, Integer> entry : atoms_weight.entrySet()) {
			if(entry.getValue() == maxWeight) {
				Atom a = entry.getKey();	//key ayant le valeur maximale	
				list_atoms.add(a);
			}
		}
	    	Collections.shuffle(list_atoms);
	    	
	    	return list_atoms.get(0);
    }
    


    
    public static Set<Atom> maxSAT(List<Clause> rules){
    		//atomes à ajouter dans KB, il contient des + et - atomes
    		Set<Atom> s = new HashSet<Atom>();
    		
    		//nouvelle liste contenant des regles pour faire des manipulations
    		List<Clause> our_rules = new ArrayList<Clause>();
    		for(int i = 0; i < rules.size(); i++) {
    			our_rules.add(rules.get(i));
        	}
    		
    		//our_atoms : liste contenant atoms dans des regles
    		HashSet<Atom> our_atoms = new HashSet<Atom>();
    		for(int i = 0; i < our_rules.size(); i++) {
    			List<Atom> atoms = our_rules.get(i).atoms;
    			for(int j = 0; j < atoms.size(); j++) {
    				our_atoms.add(atoms.get(j));
    			}
    		}
    		
    		/*
    		HashMap<Atom,Integer> atoms_weight = new HashMap<Atom,Integer>();
    		java.util.Iterator<Atom> iterator = our_atoms.iterator();
    	    while (iterator.hasNext()) {
    	    		int weight = 0;	//weight of this atom
    	    		Atom a = iterator.next();
    	    		for(int i = 0; i < our_rules.size(); i++) {
    	    			List<Atom> prov_atoms = our_rules.get(i).atoms;
    	    			for(int j = 0; j < prov_atoms.size(); j++) {
	    				if(prov_atoms.get(j).equals(a)) {
	    					weight += our_rules.get(i).weight;
    	    				}
    	    			}
    	    		}
    	    		atoms_weight.put(a, weight);
    	    }
    	    */
    		while(!our_atoms.isEmpty()) {
        		Atom bestAtom = bestAtoms(our_rules, our_atoms);
        		s.add(bestAtom);
        		for(int i = 0; i < our_rules.size(); i++) {
        			List<Atom> prov_atoms = our_rules.get(i).atoms;
        			for(int j = 0; j < prov_atoms.size(); j++) {
        				if(prov_atoms.get(j).equals(bestAtom)) {
        					our_rules.remove(i);
        					break;
        				}
        			}
        		}
        		our_atoms.remove(bestAtom);
    			our_atoms.remove(bestAtom.negation());
    		}
    		
    		return s;
    }

    public static void main(String[] args) throws IOException {
        File argument = new File(args[0]);
        for (File file : argument.isDirectory() ? argument.listFiles() : new File[] { argument }) {
            startTime = System.currentTimeMillis();
            List<Clause> rules = Clause.readFrom(file);
            
            Set<Atom> bestKB = maxSAT(rules);
            try (Writer out = Files.newBufferedWriter(Paths.get(file.getName().replaceAll("\\.[a-z]+$", ".res")),
                    Charset.forName("UTF-8"))) {
                for (Atom var : bestKB)
                    if (var.isPositive()) out.write(var + "\n");
            }
        }
    }
}
