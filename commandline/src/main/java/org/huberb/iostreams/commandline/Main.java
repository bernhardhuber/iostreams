/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.iostreams.commandline;

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
 * Command line application running base64-encoder, base64-decoder,
 * mime-encoder, mime-decoder, gzip, gunzip, deflate, or inflate.
 *
 * @author pi
 */
@CommandLine.Command(name = "Main",
        mixinStandardHelpOptions = true,
        showAtFileInUsageHelp = true,
        showDefaultValues = true,
        version = "Main 0.1-SNAPSHOT",
        description = "Run iostream tool - %n"
        + "base64-encoder, base64-decoder, "
        + " mime-encoder, mime-decoder, "
        + "gzip, gunzip, "
        + "deflate, or inflate%n"
        + ""
)
public class Main implements Callable<Integer> {

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    private FromFileOrStdinExclusive exclusive;

    // make --form-file, and --stdin mutual exclusive
    static class FromFileOrStdinExclusive {

        @CommandLine.Option(names = {"--from-file"},
                paramLabel = "FROM_FILE",
                description = "read from file name")
        File fromFile;
        @CommandLine.Option(names = {"--stdin"},
                required = false,
                description = "read from stdin")
        boolean stdin;
    }

    @CommandLine.Option(names = {"--mode"},
            required = true,
            paramLabel = "MODE",
            description = "mode : c(ompress) | d(ecompress)")
    private String mode;

    /*
        mode compress:    deflate, gzip, b64enc, mimeenc
        mode decompress:  inflate, gunzip, b64dec, mimedec
     */
    public static void main(String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // create output stream
        final InputStreamFromExclusiveFactory inputStreamFromExclusiveFactory = new InputStreamFromExclusiveFactory(exclusive);
        final InputStream is = inputStreamFromExclusiveFactory.create();

        // select processing mode, and process
        final Processor xFactory = new Processor();
        if ("c".equals(this.mode)) {
            xFactory.processGzipB64(is);
        } else if ("d".equals(this.mode)) {
            xFactory.processB64Gunzip(is);
        }
        return 0;
    }

    static class InputStreamFromExclusiveFactory {

        private final FromFileOrStdinExclusive exclusive;

        public InputStreamFromExclusiveFactory(FromFileOrStdinExclusive exclusive) {
            this.exclusive = exclusive;
        }

        InputStream create() throws IOException {
            final InputStream result;
            if (exclusive.fromFile != null) {
                result = FileUtils.openInputStream(exclusive.fromFile);
            } else if (exclusive.stdin) {
                result = new IgnoreCloseInputStream(System.in);
            } else {
                // FIXME throw exception, or use Optional
                return null;
            }
            return result;
        }
    }

    static class Processor {

        void processGzipB64(InputStream xis) throws IOException {
            // encode AAA -> b64(gzip(AAA)) 

            final OutputStream baosSinkEncode = new IgnoreCloseOutputStream(System.out);
            {

                // AAA -> gzip -> b64encode -> b64gzipAAA
                try (final OutputStream os = new StreamsBuilder.OutputStreamBuilder().
                        sink(baosSinkEncode).
                        b64Encode().
                        gzip().
                        build();
                        final InputStream is = xis) {

                    IOUtils.copy(is, os);
                }
                baosSinkEncode.flush();
            }
        }

        void processB64Gunzip(final InputStream xis) throws IOException {
            // decode b64(gzip(AAA)) -> AAA

            final OutputStream baosSinkDecode = new IgnoreCloseOutputStream(System.out);
            {
                // b64gzipAAA -> b64decode -> gunzip -> AAA
                try (final InputStream source = xis;
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
