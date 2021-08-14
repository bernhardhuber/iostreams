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
                description = "Read from file name")
        File fromFile;
        @CommandLine.Option(names = {"--stdin"},
                required = false,
                description = "Read from stdin")
        boolean stdin;
    }
    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    private FromFileOrStdinExclusive fromFileOrStdinExclusive;

    /*
        mode compress:    deflate, gzip, b64enc, mimeenc
        mode decompress:  inflate, gunzip, b64dec, mimedec
     */
    static class ModesExclusive {

        enum Modes {
            //---
            compressB64(Arrays.asList(Modecompress.b64enc), null),
            compressMime(Arrays.asList(Modecompress.mimeFenc), null),
            compressGzip(Arrays.asList(Modecompress.gzip), null),
            compressDeflate(Arrays.asList(Modecompress.deflate), null),
            compressB64Gzip(Arrays.asList(Modecompress.b64enc, Modecompress.gzip), null),
            compressMimeGzip(Arrays.asList(Modecompress.mimeFenc, Modecompress.gzip), null),
            compressB64Deflate(Arrays.asList(Modecompress.b64enc, Modecompress.deflate), null),
            compressMimeDeflate(Arrays.asList(Modecompress.mimeFenc, Modecompress.deflate), null),
            //---
            decompressB64(null, Arrays.asList(Modedecompress.b64dec)),
            decompressMime(null, Arrays.asList(Modedecompress.mimedec)),
            decompressGunzip(null, Arrays.asList(Modedecompress.gunzip)),
            decompressInflate(null, Arrays.asList(Modedecompress.inflate)),
            decompressB64Gunzip(null, Arrays.asList(Modedecompress.b64dec, Modedecompress.gunzip)),
            decompressMimeGunzip(null, Arrays.asList(Modedecompress.mimedec, Modedecompress.gunzip)),
            decompressB64Inflate(null, Arrays.asList(Modedecompress.b64dec, Modedecompress.inflate)),
            decompressMimeInflate(null, Arrays.asList(Modedecompress.mimedec, Modedecompress.inflate));
            final List<Modecompress> modecompressList;
            final List<Modedecompress> modedecompressList;

            private Modes(List<Modecompress> modecompressList, List<Modedecompress> modedecompressList) {
                this.modecompressList = modecompressList;
                this.modedecompressList = modedecompressList;
            }
        }

        @CommandLine.Option(names = {"--compress"},
                paramLabel = "COMPRESS",
                description = "Compress input")
        String compressModes;

        @CommandLine.Option(names = {"--decompress"},
                paramLabel = "DECOMPRESS",
                description = "Decompress input")
        String decompressModes;

        @CommandLine.Option(names = {"--modes"},
                paramLabel = "MODES",
                description = "Valid values: ${COMPLETION-CANDIDATES}\"")
        Modes modes;

        enum Mode {
            unknown, compress, decompress;
        }

        static class ProcessingControl {

            final Mode mode;
            final List<Modecompress> modecompressList;
            final List<Modedecompress> modedecompressList;

            public ProcessingControl(Mode mode, List<Modecompress> modecompressList, List<Modedecompress> modedecompressList) {
                this.mode = mode;
                this.modecompressList = modecompressList;
                this.modedecompressList = modedecompressList;
            }

        }

        ProcessingControl build(ModesExclusive modesExclusive) {
            final Mode mode;
            List<Modecompress> modeCompressList = null;
            List<Modedecompress> modeDecompressList = null;

            if (this.compressModes != null && this.decompressModes == null && this.modes == null) {
                mode = Mode.compress;
                final ProcessingModesCompress processingModesCompress = new ProcessingModesCompress();
                final List<Modecompress> result = processingModesCompress.convertStringToModecompressList(this.compressModes);
                modeCompressList = result;
            } else if (this.compressModes == null && this.decompressModes != null && this.modes == null) {
                mode = Mode.decompress;
                final ProcessingModesDecompress processingModesCompress = new ProcessingModesDecompress();
                final List<Modedecompress> result = processingModesCompress.convertStringToModedecompressList(this.compressModes);
                modeDecompressList = result;
            } else if (this.compressModes == null && this.decompressModes == null && this.modes != null) {
                if (this.modes == Modes.compressB64
                        || this.modes == Modes.compressB64Deflate
                        || this.modes == Modes.compressB64Gzip
                        || this.modes == Modes.compressDeflate
                        || this.modes == Modes.compressGzip
                        || this.modes == Modes.compressMime
                        || this.modes == Modes.compressMimeDeflate
                        || this.modes == Modes.compressMimeGzip) {
                    mode = Mode.compress;
                    modeCompressList = this.modes.modecompressList;
                } else if (this.modes == Modes.decompressB64
                        || this.modes == Modes.decompressB64Gunzip
                        || this.modes == Modes.decompressB64Inflate
                        || this.modes == Modes.decompressGunzip
                        || this.modes == Modes.decompressInflate
                        || this.modes == Modes.decompressMime
                        || this.modes == Modes.decompressMimeGunzip
                        || this.modes == Modes.decompressMimeInflate) {
                    mode = Mode.decompress;
                    modeDecompressList = this.modes.modedecompressList;
                } else {
                    mode = Mode.unknown;
                }
            } else {
                mode = Mode.unknown;
            }
            final ProcessingControl processingControl = new ProcessingControl(mode, modeCompressList, modeDecompressList);
            return processingControl;
        }
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
            final InputStream is = optionalInputStream.get();

            final ModesExclusive.ProcessingControl processingControl = this.modesExclusive.build(this.modesExclusive);
            final ModesExclusive.Mode mode = processingControl.mode;
            if (mode == ModesExclusive.Mode.compress) {
                final ProcessingModesCompress processingModesCompress = new ProcessingModesCompress();
                final List<Modecompress> defaultProcessingSteps = processingControl.modecompressList;
                final OutputStream os = new IgnoreCloseOutputStream(System.out);
                processingModesCompress.xxxcompress(defaultProcessingSteps, is, os);
                result = 0;
            } else if (mode == ModesExclusive.Mode.decompress) {
                final ProcessingModesDecompress processingModesDecompress = new ProcessingModesDecompress();
                final List<Modedecompress> defaultProcessModes = processingControl.modedecompressList;
                final OutputStream os = new IgnoreCloseOutputStream(System.out);
                processingModesDecompress.xxxdecompress(defaultProcessModes, is, os);
                result = 0;
            } else {
                logErrorMessage("Unknown processing-mode %s%n", this.modesExclusive);
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
