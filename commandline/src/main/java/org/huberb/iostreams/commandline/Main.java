/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.iostreams.commandline;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.huberb.iostreams.StreamsBuilder;
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

    @CommandLine.Option(names = {"--from-file"},
            paramLabel = "FROM_FILE",
            description = "read from file name")
    private File fromFile;
    @CommandLine.Option(names = {"--mode"},
            required = true,
            paramLabel = "MODE",
            description = "mode : c(ompress) | d(ecompress)")
    private String mode;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);

    }

    @Override
    public Integer call() throws Exception {
        XFactory xFactory = new XFactory();
        if ("c".equals(this.mode)) {
            xFactory.processGzipB64(this.fromFile);
        } else if ("d".equals(this.mode)) {
            xFactory.processB64Gunzip(this.fromFile);
        }
        return 0;
    }

    static class XFactory {

        void processGzipB64(final File inputFile) throws IOException {
            // encode AAA -> b64(gzip(AAA)) 
            final OutputStream baosSinkEncode = System.out;
            {

                // AAA -> gzip -> b64encode -> b64gzipAAA
                try (final OutputStream os = new StreamsBuilder.OutputStreamBuilder().
                        sink(baosSinkEncode).
                        b64Encode().
                        gzip().
                        build();
                        final InputStream is = FileUtils.openInputStream(inputFile)) {

                    IOUtils.copy(is, os);
                }
                baosSinkEncode.flush();
            }
        }

        void processB64Gunzip(final File inputFile) throws IOException {
            // decode b64(gzip(AAA)) -> AAA

            final OutputStream baosSinkDecode = System.out;
            {
                // b64gzipAAA -> b64decode -> gunzip -> AAA
                try (final InputStream source = FileUtils.openInputStream(inputFile);
                        final InputStream is = new StreamsBuilder.InputStreamBuilder().
                                source(source).
                                b64Decode().
                                gunzip().
                                build()) {
                    IOUtils.copy(is, baosSinkDecode);
                }
                baosSinkDecode.flush();
            }

        }
    }
}
