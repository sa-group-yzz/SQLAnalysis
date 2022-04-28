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

public class ReadablePasswordDetector {

	// 30 lines of code
	private static Set<String> cryptographicLibClass = crypSource();
	private static Set<String> crypSource()
	{		
		Set<String> source = new HashSet<>();
		source.add("javax.crypto.Cipher");
		return source;
	}
	public static Set<Silica> detect(Set<String> classFilter)
	{	 
		Set<Silica> targetSilicas = new HashSet<>();
		Set<Silica> relevantSilicas = SilicaFinder.find("(.*(password =|password=|pwd =|pwd=).*)|(insert into.*(password|pwd).*)", classFilter);
		for(Silica silica : relevantSilicas)
		{
			boolean isEncrypted = false;
			for(Def def : Analyzer.getDefSet(silica))
			{
				if(def.getType().equals("METHOD"))
				{
					for(String lib : cryptographicLibClass)
					{
						if(def.getName().contains(lib))
							isEncrypted = true;
					}
				}
			}
			if(!isEncrypted)
				targetSilicas.add(silica);
		}
		return targetSilicas;
	}
}
