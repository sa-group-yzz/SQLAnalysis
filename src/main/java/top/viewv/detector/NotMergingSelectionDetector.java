package top.viewv.detector;

import top.viewv.abstraction.Codepoint;
import top.viewv.abstraction.Def;
import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.analysis.helper.Pair;
import top.viewv.function.Analyzer;
import top.viewv.function.SilicaFinder;

import java.util.HashSet;
import java.util.Set;

public class NotMergingSelectionDetector {
	/**
	 * Check if 
	 * (1) the second query is executed after the first query is executed
	 * (2) the queries are identical except the selection predicates
	 */
	
	//25  lines of code
	public static Set<Pair<Silica, Silica>> detect(boolean isMust, Set<String> classFilter)
	{
		Set<Pair<Silica, Silica>> targetSilicaPairs = new HashSet<>();
		Set<Silica> relevantSilicas = SilicaFinder.find("^(select)(?!.*(\\?|unknown@)).*", classFilter);
		for(Silica silica : relevantSilicas)
		{
			Set<Codepoint> reachSet = Analyzer.getReachableSet(silica.getCodepoint(), Analyzer.getCodepointsFromSilicas(relevantSilicas));
			for(Silica reachSilica : relevantSilicas)
			{
				if(reachSet.contains(reachSilica.getCodepoint()))
				{	
					Set<String> firstQueries =  Analyzer.getQuerySet(silica);
					Set<String> secondQueries = Analyzer.getQuerySet(reachSilica);
					if(firstQueries.equals(secondQueries))
						continue;
					for(String firstQuery : firstQueries)
						for(String secondQuery: secondQueries)
							if(firstQuery.contains("where") && secondQuery.contains("where"))
								if(firstQuery.substring(0, firstQuery.indexOf("where")).equals(secondQuery.substring(0, secondQuery.indexOf("where"))))
									if(!firstQuery.substring(firstQuery.indexOf("where")).equals(secondQuery.substring(secondQuery.indexOf("where"))))
										targetSilicaPairs.add(new Pair<Silica, Silica>(silica, reachSilica));	
				}
			}
		}
		return targetSilicaPairs;
	}
}
