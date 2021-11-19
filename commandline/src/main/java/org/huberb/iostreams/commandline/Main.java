/*
 * Copyright 2021 pi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.huberb.iostreams.commandline.support.IgnoreCloseInputStream;
import org.huberb.iostreams.commandline.support.IgnoreCloseOutputStream;
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
        version = "iostreams 0.1-SNAPSHOT",
        description = "Run convert input using - %n"
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
                description = "Read from a file")
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

        enum Mode {
            UNKNOWN, COMPRESS, DECOMPRESS;
        }

        enum Modes {
            //---
            compressB64(Mode.COMPRESS, Arrays.asList(Modecompress.B64ENC), null),
            compressMime(Mode.COMPRESS, Arrays.asList(Modecompress.MIMEENC), null),
            compressGzip(Mode.COMPRESS, Arrays.asList(Modecompress.GZIP), null),
            compressDeflate(Mode.COMPRESS, Arrays.asList(Modecompress.DEFLATE), null),
            compressB64Gzip(Mode.COMPRESS, Arrays.asList(Modecompress.B64ENC, Modecompress.GZIP), null),
            compressMimeGzip(Mode.COMPRESS, Arrays.asList(Modecompress.MIMEENC, Modecompress.GZIP), null),
            compressB64Deflate(Mode.COMPRESS, Arrays.asList(Modecompress.B64ENC, Modecompress.DEFLATE), null),
            compressMimeDeflate(Mode.COMPRESS, Arrays.asList(Modecompress.MIMEENC, Modecompress.DEFLATE), null),
            //---
            decompressB64(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.B64DEC)),
            decompressMime(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.MIMEDEC)),
            decompressGunzip(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.GUNZIP)),
            decompressInflate(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.INFLATE)),
            decompressB64Gunzip(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.B64DEC, Modedecompress.GUNZIP)),
            decompressMimeGunzip(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.MIMEDEC, Modedecompress.GUNZIP)),
            decompressB64Inflate(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.B64DEC, Modedecompress.INFLATE)),
            decompressMimeInflate(Mode.DECOMPRESS, null, Arrays.asList(Modedecompress.MIMEDEC, Modedecompress.INFLATE));
            final List<Modecompress> modecompressList;
            final List<Modedecompress> modedecompressList;
            final Mode mode;

            private Modes(Mode mode, List<Modecompress> modecompressList, List<Modedecompress> modedecompressList) {
                this.mode = mode;
                this.modecompressList = modecompressList;
                this.modedecompressList = modedecompressList;
            }
        }

        @CommandLine.Option(names = {"--compress"},
                paramLabel = "COMPRESS", split = ",",
                description = "Valid values: \"${COMPLETION-CANDIDATES}\"")
        List<Modecompress> modecompressListOption;

        @CommandLine.Option(names = {"--decompress"},
                paramLabel = "DECOMPRESS", split = ",",
                description = "Valid values: \"${COMPLETION-CANDIDATES}\"")
        List<Modedecompress> modedecompressListOption;

        @CommandLine.Option(names = {"--modes"},
                paramLabel = "MODES",
                description = "Valid values: \"${COMPLETION-CANDIDATES}\"")
        Modes modes;

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

        ProcessingControl build() {
            final Mode mode;
            List<Modecompress> modeCompressList = null;
            List<Modedecompress> modeDecompressList = null;

            if (this.modecompressListOption != null && this.modedecompressListOption == null && this.modes == null) {
                mode = Mode.COMPRESS;
                modeCompressList = modecompressListOption;
            } else if (this.modecompressListOption == null && this.modedecompressListOption != null && this.modes == null) {
                mode = Mode.DECOMPRESS;
                modeDecompressList = modedecompressListOption;
            } else if (this.modecompressListOption == null && this.modedecompressListOption == null && this.modes != null) {
                mode = this.modes.mode;
                modeCompressList = this.modes.modecompressList;
                modeDecompressList = this.modes.modedecompressList;
            } else {
                mode = Mode.UNKNOWN;
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
        final InputStreamFromFileOrStdinExclusiveFactory inputStreamFromExclusiveFactory = new InputStreamFromFileOrStdinExclusiveFactory(fromFileOrStdinExclusive);
        final Optional<InputStream> optionalInputStream = inputStreamFromExclusiveFactory.create();
        if (optionalInputStream.isPresent()) {
            // don't forget to close is
            try (final InputStream is = optionalInputStream.get()) {
                // Calc Mode, and Modecompress, Modedecompress values
                final ModesExclusive.ProcessingControl processingControl = this.modesExclusive.build();
                final ModesExclusive.Mode mode = processingControl.mode;
                if (mode == ModesExclusive.Mode.COMPRESS) {
                    // run Mode.compress, List<Modecompress>
                    final ProcessingModesCompress processingModesCompress = new ProcessingModesCompress();
                    final List<Modecompress> defaultProcessingSteps = processingControl.modecompressList;
                    final OutputStream os = new IgnoreCloseOutputStream(System.out);
                    processingModesCompress.processModecompress(defaultProcessingSteps, is, os);
                    result = 0;
                } else if (mode == ModesExclusive.Mode.DECOMPRESS) {
                    // run Mode.decompress, List<Modedecompress>
                    final ProcessingModesDecompress processingModesDecompress = new ProcessingModesDecompress();
                    final List<Modedecompress> defaultProcessModes = processingControl.modedecompressList;
                    final OutputStream os = new IgnoreCloseOutputStream(System.out);
                    processingModesDecompress.processModedecompress(defaultProcessModes, is, os);
                    result = 0;
                } else {
                    logErrorMessage("Unknown processing-mode %s%n", this.modesExclusive);
                    result = 1;
                }
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
    static class InputStreamFromFileOrStdinExclusiveFactory {

        private final FromFileOrStdinExclusive fromFileOrStdinExclusive;

        public InputStreamFromFileOrStdinExclusiveFactory(FromFileOrStdinExclusive exclusive) {
            this.fromFileOrStdinExclusive = exclusive;
        }

        Optional<InputStream> create() throws IOException {
            final Optional<InputStream> optInputStream;
            if (fromFileOrStdinExclusive.fromFile != null) {
                final InputStream is = FileUtils.openInputStream(fromFileOrStdinExclusive.fromFile);
                optInputStream = Optional.of(is);
            } else if (fromFileOrStdinExclusive.stdin) {
                final InputStream is = new IgnoreCloseInputStream(System.in);
                optInputStream = Optional.of(is);
            } else {
                optInputStream = Optional.empty();
            }
            return optInputStream;
        }
    }

}
