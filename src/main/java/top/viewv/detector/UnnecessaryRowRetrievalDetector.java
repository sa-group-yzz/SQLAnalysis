package top.viewv.detector;

import soot.jimple.IfStmt;
import top.viewv.abstraction.Codepoint;
import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.analysis.helper.Pair;
import top.viewv.function.Analyzer;
import top.viewv.function.SilicaFinder;

import java.util.HashSet;
import java.util.Set;

public class UnnecessaryRowRetrievalDetector {
	/**
	 * Check if 
	 * (1) a select query retrieves some rows but some of them are filtered out after retrieving
	 */
	
	//52 lines of code
	public static Set<Silica> detect(Set<String> classFilter)
	{ 
		Set<Silica> targetSilicas = new HashSet<>();
		
		Set<Silica> relevantSilicas = SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter);
		for(Silica silica : relevantSilicas)
		{
			Set<Use> useSet = Analyzer.getUseSet(silica);
			for(Use use : useSet)
			{
				if(use.getSelectedColumn() != null)
				{
					if(use.getCodepoint().getStatement() instanceof IfStmt)
					{
						//the use of other data is control dependent on the if statement 
						boolean isAllControlDependent = true;
						int trueOfFalseBranch = -1;
						for(Use otherUse : useSet)
						{
							//exclude the uses used in the if statement
							if(otherUse.getSelectedColumn()==null || use.equals(otherUse))
								continue;
							boolean isControlDependentOn = false;
							for(Pair<Codepoint, Integer> cp : Analyzer.getControlDependentSet(otherUse.getCodepoint()))
							{
								if(cp.getFirst().equals(use.getCodepoint()))
								{
									if(trueOfFalseBranch == -1)
									{
										trueOfFalseBranch = cp.getSecond();
										isControlDependentOn = true;
									}
									else
									{
										if(cp.getSecond() == trueOfFalseBranch)
											isControlDependentOn = true;
										else
											isControlDependentOn = false;
									}
								}
							}
							if(!isControlDependentOn)
								isAllControlDependent = false;
						}
						if(isAllControlDependent)
						{
							targetSilicas.add(silica); 
							break;
						}
					}
				}
			}
		}
		return targetSilicas;
	}
}
