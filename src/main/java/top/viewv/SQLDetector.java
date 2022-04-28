package top.viewv;

import soot.Unit;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import sql.sand.abstraction.Silica;
import sql.sand.abstraction.Use;
import sql.sand.function.Analyzer;
import sql.sand.function.SilicaFinder;

import java.util.Set;

public class SQLDetector {
    public void detect(Set<String> classFilter){
        System.out.println("SQLDetector");;
        Set<Silica> relevantSilicas = SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter);
        for (Silica silica : relevantSilicas) {
            Set<Use> useSet = Analyzer.getUseSet(silica);
            for (Use use : useSet) {
                if (use.getSelectedColumn() != null) {
                    if (use.getCodepoint().getStatement() instanceof IfStmt) {
                        Stmt stmt = (Stmt) use.getCodepoint().getStatement();
                        Unit unit = use.getCodepoint().getStatement();
                        System.out.println(unit.getUseBoxes());
                    }
                }
            }
        }
    }
}
