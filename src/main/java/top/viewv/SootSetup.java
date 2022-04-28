package top.viewv;

import soot.Scene;
import soot.options.Options;

import java.util.ArrayList;
import java.util.List;

public class SootSetup {
    public static void setup() {
        // Setup Soot
        List<String> dir = new ArrayList<>();
        dir.add("target/test-classes");
        Options.v().set_process_dir(dir);
        Options.v().set_whole_program(true);
        Options.v().set_verbose(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_keep_offset(true);
        Options.v().set_allow_phantom_refs(true);

        Scene.v().loadNecessaryClasses();
    }
}
