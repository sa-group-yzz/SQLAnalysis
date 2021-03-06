package top.viewv.detector;

import top.viewv.abstraction.Codepoint;
import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.analysis.helper.Pair;
import top.viewv.function.Analyzer;
import top.viewv.function.SilicaFinder;

import java.util.HashSet;
import java.util.Set;

public class LoopToJoinDetectorAugmented {

	/**
	 * Check if 
	 * (1) the first query dominates/reaches the second query
	 * (2) the result of the first query is used in the second query
	 * (3) the second query is executed in a loop
	 */
	//45 lines of code
	public static Set<Pair<Silica, Silica>> detect(boolean isMust, Set<String> classFilter)
	{
		Set<Pair<Silica, Silica>> targetSilicaPairs = new HashSet<>();
		Set<Silica> relevantSilicas = SilicaFinder.find("^select.*", classFilter);
		for(Silica silica : relevantSilicas)
		{
			Set<Codepoint> reachSet = Analyzer.getReachableSet(silica.getCodepoint(), Analyzer.getCodepointsFromSilicas(relevantSilicas));
			for(Silica reachSilica : relevantSilicas)
			{
				if(reachSet.contains(reachSilica.getCodepoint()))
				{
					Set<Use> useSet = Analyzer.getUseSet(silica);
					Set<Codepoint> secondLoopSet = Analyzer.getLoopSet(reachSilica.getCodepoint());
					boolean useAsLoopHeader = false;
					for(Use use : useSet)
					{
						if(!secondLoopSet.isEmpty())
						{
							for(Codepoint loopHeader : secondLoopSet)
							{
								if(loopHeader.equals(use.getCodepoint()))
								{
									useAsLoopHeader = true;
									break;
								}
							}
						}
						if(useAsLoopHeader)
							break;
					}
					if(useAsLoopHeader)
					{
						for(Use use : useSet)
						{
							if(use.getSelectedColumn() != null && use.getCodepoint().equals(reachSilica.getCodepoint()))
							{
								targetSilicaPairs.add(new Pair<Silica, Silica>(silica, reachSilica));
								break;
							}
						}
					}
				}
			}
		}
		return targetSilicaPairs;
	}
}
