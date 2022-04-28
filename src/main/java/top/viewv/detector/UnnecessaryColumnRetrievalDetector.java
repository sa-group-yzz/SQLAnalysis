package top.viewv.detector;

import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.function.Analyzer;
import top.viewv.function.SilicaFinder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnnecessaryColumnRetrievalDetector {
	/**
	 * Check if 
	 * (1) a column is selected
	 * (2) but it is not used
	 */
	
	//29 lines of code
	public static Set<Silica> detect(Set<String> classFilter)
	{		
		Set<Silica> targetSilicas = new HashSet<>();
		for(Silica silica : SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter))
		{
			Set<Use> useSet = Analyzer.getUseSet(silica);
			Set<String> usedColumns = new HashSet<>();
			boolean isFromIndex = false;
			for(Use use : useSet)
			{
				if(use.IsFromIndex())
					isFromIndex = true;
				if(use.getSelectedColumn() != null)
					usedColumns.addAll(use.getSelectedColumn());
			}
			for(List<String> selectedColumns : silica.getQueryToSelectedColumns().values())
			{
				for(String selectedColumn : selectedColumns)
				{
					if(!usedColumns.contains(selectedColumn) && !usedColumns.contains("$$$"))
					{
						silica.setFromIndex(isFromIndex);
						targetSilicas.add(silica);
						break;
					}
				}
			}
		}
		return targetSilicas;
	}
}
