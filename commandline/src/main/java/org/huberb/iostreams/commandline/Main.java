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

        enum Mode {
            unknown, compress, decompress;
        }

        enum Modes {
            //---
            compressB64(Mode.compress, Arrays.asList(Modecompress.b64enc), null),
            compressMime(Mode.compress, Arrays.asList(Modecompress.mimeFenc), null),
            compressGzip(Mode.compress, Arrays.asList(Modecompress.gzip), null),
            compressDeflate(Mode.compress, Arrays.asList(Modecompress.deflate), null),
            compressB64Gzip(Mode.compress, Arrays.asList(Modecompress.b64enc, Modecompress.gzip), null),
            compressMimeGzip(Mode.compress, Arrays.asList(Modecompress.mimeFenc, Modecompress.gzip), null),
            compressB64Deflate(Mode.compress, Arrays.asList(Modecompress.b64enc, Modecompress.deflate), null),
            compressMimeDeflate(Mode.compress, Arrays.asList(Modecompress.mimeFenc, Modecompress.deflate), null),
            //---
            decompressB64(Mode.decompress, null, Arrays.asList(Modedecompress.b64dec)),
            decompressMime(Mode.decompress, null, Arrays.asList(Modedecompress.mimedec)),
            decompressGunzip(Mode.decompress, null, Arrays.asList(Modedecompress.gunzip)),
            decompressInflate(Mode.decompress, null, Arrays.asList(Modedecompress.inflate)),
            decompressB64Gunzip(Mode.decompress, null, Arrays.asList(Modedecompress.b64dec, Modedecompress.gunzip)),
            decompressMimeGunzip(Mode.decompress, null, Arrays.asList(Modedecompress.mimedec, Modedecompress.gunzip)),
            decompressB64Inflate(Mode.decompress, null, Arrays.asList(Modedecompress.b64dec, Modedecompress.inflate)),
            decompressMimeInflate(Mode.decompress, null, Arrays.asList(Modedecompress.mimedec, Modedecompress.inflate));
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

            if (this.compressModes != null && this.decompressModes == null && this.modes == null) {
                mode = Mode.compress;
                final ProcessingModesCompress processingModesCompress = new ProcessingModesCompress();
                final List<Modecompress> result = processingModesCompress.convertStringToModecompressList(this.compressModes);
                modeCompressList = result;
            } else if (this.compressModes == null && this.decompressModes != null && this.modes == null) {
                mode = Mode.decompress;
                final ProcessingModesDecompress processingModesCompress = new ProcessingModesDecompress();
                final List<Modedecompress> result = processingModesCompress.convertStringToModedecompressList(this.decompressModes);
                modeDecompressList = result;
            } else if (this.compressModes == null && this.decompressModes == null && this.modes != null) {
                mode = this.modes.mode;
                modeCompressList = this.modes.modecompressList;
                modeDecompressList = this.modes.modedecompressList;
//                if (this.modes == Modes.compressB64
//                        || this.modes == Modes.compressB64Deflate
//                        || this.modes == Modes.compressB64Gzip
//                        || this.modes == Modes.compressDeflate
//                        || this.modes == Modes.compressGzip
//                        || this.modes == Modes.compressMime
//                        || this.modes == Modes.compressMimeDeflate
//                        || this.modes == Modes.compressMimeGzip) {
//                    mode = Mode.compress;
//                    modeCompressList = this.modes.modecompressList;
//                } else if (this.modes == Modes.decompressB64
//                        || this.modes == Modes.decompressB64Gunzip
//                        || this.modes == Modes.decompressB64Inflate
//                        || this.modes == Modes.decompressGunzip
//                        || this.modes == Modes.decompressInflate
//                        || this.modes == Modes.decompressMime
//                        || this.modes == Modes.decompressMimeGunzip
//                        || this.modes == Modes.decompressMimeInflate) {
//                    mode = Mode.decompress;
//                    modeDecompressList = this.modes.modedecompressList;
//                } else {
//                    mode = Mode.unknown;
//                }
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

            // Calc Mode, and Modecompress, Modedecompress values
            final ModesExclusive.ProcessingControl processingControl = this.modesExclusive.build();
            final ModesExclusive.Mode mode = processingControl.mode;
            if (mode == ModesExclusive.Mode.compress) {
                // run Mode.compress, List<Modecompress>
                final ProcessingModesCompress processingModesCompress = new ProcessingModesCompress();
                final List<Modecompress> defaultProcessingSteps = processingControl.modecompressList;
                final OutputStream os = new IgnoreCloseOutputStream(System.out);
                processingModesCompress.xxxcompress(defaultProcessingSteps, is, os);
                result = 0;
            } else if (mode == ModesExclusive.Mode.decompress) {
                // run Mode.decompress, List<Modedecompress>
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
