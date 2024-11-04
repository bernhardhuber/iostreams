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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.huberb.iostreams.StreamsBuilder;

/**
 * Process decompressing modes.
 *
 * @author pi
 */
class ProcessingModesDecompress {

    /**
     * Define supported decompress modes.
     */
    enum ModeDecompress {
        INFLATE, GUNZIP, B64DEC, MIMEDEC;

        /**
         * Convert a comma separated string of mode decompress modes to a list
         * of {@link ModeDecompress} values.
         *
         * @param s
         * @return
         */
        static List<ModeDecompress> convertStringToModedecompressList(String s) {
            final List<ModeDecompress> l = new ArrayList<>();
            final List<String> sSplittedList = Arrays.asList(s.split(","));
            for (String t : sSplittedList) {
                if (t == null || t.isEmpty()) {
                    continue;
                }
                final ModeDecompress u = ModeDecompress.valueOf(t);
                l.add(u);
            }
            return l;
        }
    }

    /**
     * Process decompress modes.
     *
     * @param modecompressList
     * @param inputStreamSource
     * @param outputStream
     * @throws IOException
     */
    void processModedecompress(List<ModeDecompress> modecompressList,
            InputStream inputStreamSource,
            OutputStream outputStream) throws IOException {
        //---
        final StreamsBuilder.InputStreamBuilder inputStreamBuilder = new StreamsBuilder.InputStreamBuilder();
        inputStreamBuilder.source(inputStreamSource);
        for (ModeDecompress u : modecompressList) {
            if (u == ModeDecompress.INFLATE) {
                inputStreamBuilder.inflate();
            } else if (u == ModeDecompress.GUNZIP) {
                inputStreamBuilder.gunzip();
            } else if (u == ModeDecompress.B64DEC) {
                inputStreamBuilder.b64Decode();
            } else if (u == ModeDecompress.MIMEDEC) {
                inputStreamBuilder.mimeDecode();
            }
        }
        // b64gzipAAA -> b64decode -> gunzip -> AAA
        try (final OutputStream baosSinkDecode = outputStream; final InputStream is = inputStreamBuilder.build()) {
            IOUtils.copy(is, baosSinkDecode);
        }
    }

}
