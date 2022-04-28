package top.viewv;

import sql.sand.abstraction.Silica;
import sql.sand.function.SilicaFinder;

import java.util.Set;

public class SQLDetector {
    public Set<Silica> detect(Set<String> classFilter){
        Set<Silica> relevantSilicas = SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter);
        return relevantSilicas;
    }
}
