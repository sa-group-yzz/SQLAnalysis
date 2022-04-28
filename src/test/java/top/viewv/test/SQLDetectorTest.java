package top.viewv.test;

import org.junit.Test;
import soot.jimple.IfStmt;
import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.function.Analyzer;
import top.viewv.function.SilicaFinder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SQLDetectorTest extends BaseTest {

    @Test
    public void test() {
        Set<String> classFilter = new HashSet<>(Arrays.asList("top.viewv.testcase.Case1"));
        Set<Silica> relevantSilicas = SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter);
        for (Silica silica : relevantSilicas) {
            System.out.println(silica);
            System.out.println(silica.getStringQueries());
            Set<Use> useSet = Analyzer.getUseSet(silica);
            for (Use use : useSet) {
                if (use.getSelectedColumn() != null) {
                    if (use.getCodepoint().getStatement() instanceof IfStmt) {
                        System.out.println(use);
                    }
                }
            }
        }
    }
}
