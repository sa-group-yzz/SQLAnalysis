package top.viewv.test;

import com.alibaba.druid.stat.TableStat;
import org.junit.Test;
import soot.jimple.IfStmt;
import top.viewv.SQLAnalysis;
import top.viewv.abstraction.Def;
import top.viewv.abstraction.Silica;
import top.viewv.abstraction.Use;
import top.viewv.analysis.DefAnalysis;
import top.viewv.function.Analyzer;
import top.viewv.function.SilicaFinder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SQLDetectorTest extends BaseTest {

    @Test
    public void test() {
        Set<String> classFilter = new HashSet<>(Arrays.asList("top.viewv.testcase.Case1"));
        Set<Silica> relevantSilicas = SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter);
        for (Silica silica : relevantSilicas) {
            Set<String> queries = silica.getStringQueries();
            HashMap<String, TableStat.Condition> conditionHashMap = new HashMap<>();
            for (String query : queries) {
                SQLAnalysis analysis = new SQLAnalysis(query);
                for (TableStat.Condition condition : analysis.getConditions()) {
                    conditionHashMap.put(condition.getColumn().getName(), condition);
                }
            }
            System.out.println(silica.getStringQueries());
            Set<Use> useSet = Analyzer.getUseSet(silica);

            DefAnalysis defAnalysis = new DefAnalysis(silica);
            Set<Def> defSet = defAnalysis.getDefSet();

            for (Def def : defSet) {
                System.out.println(def);
            }

            for (Use use : useSet) {
                if (use.getSelectedColumn() != null) {
                    if (use.getCodepoint().getStatement() instanceof IfStmt) {
                        System.out.println(use);
                        for (String useColumn : use.getSelectedColumn()) {
                            if (conditionHashMap.containsKey(useColumn)) {
                                System.out.println(use);
                                System.out.println(conditionHashMap.get(useColumn));

                            }
                        }
                    }

                }
            }
        }
    }
}
