package top.viewv.detector;

import top.viewv.abstraction.Silica;
import top.viewv.function.Analyzer;
import top.viewv.function.SilicaFinder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnparamerizedDetectorAugmented {
	/**
	 * Check if (1) a query contains a dynamic value at its data value position
	 * and (2) if the query is issued in a loop
	 */
	
	//25  lines of code
	public static Set<Silica> detect(Set<String> classFilter)
	{
		Set<Silica> targetSilicas = new HashSet<>();
		Set<Silica> relevantSilicas = SilicaFinder.find("^(select|insert|update|delete).*", classFilter);
		for(Silica silica : relevantSilicas)
		{
			boolean containDynamic = false;
			if(silica.getQueryToDataValues() != null)
			{
				for(List<String> dataValues : silica.getQueryToDataValues().values())
				{
					for(String dataValue : dataValues)
					{
						if(dataValue.equals("$$$"))
						{
							containDynamic = true;
							break;
						}
					}
				}
			}
			if(containDynamic)
			{
				if(!Analyzer.getLoopSet(silica.getCodepoint()).isEmpty())
					silica.setIsPlus(true);
				targetSilicas.add(silica);
			}
		}
		return targetSilicas;
	}
}
