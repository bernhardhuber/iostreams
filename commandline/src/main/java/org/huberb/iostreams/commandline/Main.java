/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.iostreams.commandline;

import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 *
 * @author pi
 */
@CommandLine.Command(name = "MainTools",
        mixinStandardHelpOptions = true,
        showAtFileInUsageHelp = true,
        showDefaultValues = true,
        version = "MainTools 0.1-SNAPSHOT",
        description = "Run iostream tool%n"
        + ""
)

public class Main implements Callable<Integer> {

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);

    }

    @Override
    public Integer call() throws Exception {
        
        
        return 0;
    }

}
