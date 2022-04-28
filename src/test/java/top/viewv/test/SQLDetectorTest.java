package top.viewv.test;

import org.junit.Test;
import sql.sand.abstraction.Silica;
import top.viewv.SQLDetector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SQLDetectorTest extends BaseTest {

    @Test
    public void test() {
        SQLDetector detector = new SQLDetector();
        Set<Silica> silicas = detector.detect(new HashSet<>(Arrays.asList("top.viewv.testcase.Case1")));
        for (Silica silica : silicas){
            System.out.println(silica);
        }
    }
}
