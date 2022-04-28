package top.viewv.detector;

import top.viewv.abstraction.Silica;
import top.viewv.function.SilicaFinder;

import java.util.Set;

public class UseRandomDetector {
	public static Set<Silica> detect(Set<String> classFilter)
	{	 
		//checking if query matches pattern select * | insert (something other than a left parenthesis) values 
		Set<Silica> targetSilicas = SilicaFinder.find(".*random().*", classFilter);
		return targetSilicas;
	}
}
