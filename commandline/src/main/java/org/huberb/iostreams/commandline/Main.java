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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.huberb.iostreams.commandline.ProcessingModesCompress.Modecompress;
import org.huberb.iostreams.commandline.ProcessingModesDecompress.Modedecompress;
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
        + "mime-encoder, mime-decoder, "
        + "gzip, gunzip, "
        + "deflate, or inflate%n"
        + ""
)
public class Main implements Callable<Integer> {

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
    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    private FromFileOrStdinExclusive fromFileOrStdinExclusive;

    // TODO remove mode, use modesExclusive
    @CommandLine.Option(names = {"--mode"},
            required = true,
            paramLabel = "MODE",
            description = "mode : c(ompress) | d(ecompress)")
    private String mode;

    /*
        mode compress:    deflate, gzip, b64enc, mimeenc
        mode decompress:  inflate, gunzip, b64dec, mimedec
     */
    static class ModesExclusive {

        @CommandLine.Option(names = {"--compress"},
                paramLabel = "COMPRESS",
                description = "compress input")
        String compressModes;

        @CommandLine.Option(names = {"--decompress"},
                paramLabel = "DECOMPRESS",
                description = "decompress input")
        String decompressModes;
    }
    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    private ModesExclusive modesExclusive;

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        final int result;
        // create input stream
        final InputStreamFromExclusiveFactory inputStreamFromExclusiveFactory = new InputStreamFromExclusiveFactory(fromFileOrStdinExclusive);
        final Optional<InputStream> optionalInputStream = inputStreamFromExclusiveFactory.create();
        if (optionalInputStream.isPresent()) {
            InputStream is = optionalInputStream.get();
            if ("c".equals(this.mode)) {
                final ProcessingModesCompress processingModesCompress = new ProcessingModesCompress();
                final List<Modecompress> modes = Arrays.asList(Modecompress.b64enc, Modecompress.gzip);
                final OutputStream os = new IgnoreCloseOutputStream(System.out);
                processingModesCompress.xxxcompress(modes, is, os);
                result = 0;
            } else if ("d".equals(this.mode)) {
                final ProcessingModesDecompress processingModesDecompress = new ProcessingModesDecompress();
                final List<Modedecompress> modes = Arrays.asList(Modedecompress.b64dec, Modedecompress.gunzip);
                final OutputStream os = new IgnoreCloseOutputStream(System.out);
                processingModesDecompress.xxxdecompress(modes, is, os);
                result = 0;
            } else {
                logErrorMessage("Unknown processing-mode %s%n", this.mode);
                result = 1;
            }
        } else {
            logErrorMessage("No input defined%n");
            result = 1;
        }

        return result;
    }

    void logErrorMessage(String fmt, Object... args) {
        System.err.format(fmt, args);
    }

    /**
     * Create an input stream from a file, or stdin.
     */
    static class InputStreamFromExclusiveFactory {

        private final FromFileOrStdinExclusive exclusive;

        public InputStreamFromExclusiveFactory(FromFileOrStdinExclusive exclusive) {
            this.exclusive = exclusive;
        }

        Optional<InputStream> create() throws IOException {
            final Optional<InputStream> optInputStream;
            if (exclusive.fromFile != null) {
                final InputStream is = FileUtils.openInputStream(exclusive.fromFile);
                optInputStream = Optional.of(is);
            } else if (exclusive.stdin) {
                final InputStream is = new IgnoreCloseInputStream(System.in);
                optInputStream = Optional.of(is);
            } else {
                optInputStream = Optional.empty();
            }
            return optInputStream;
        }
    }

}
