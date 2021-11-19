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
 * Process compressing modes.
 *
 * @author pi
 */
class ProcessingModesCompress {

    /**
     * Define supported compress modes.
     */
    enum Modecompress {
        DEFLATE, GZIP, B64ENC, MIMEENC;

        /**
         * Convert a comma separated string of mode compress modes to a list of
         * {@link Modecompress} values.
         *
         * @param s
         * @return
         */
        static List<Modecompress> convertStringToModecompressList(String s) {
            final List<Modecompress> l = new ArrayList<>();
            final List<String> sSplittedList = Arrays.asList(s.split(","));
            for (String t : sSplittedList) {
                if (t == null || t.isEmpty()) {
                    continue;
                }
                final Modecompress u = Modecompress.valueOf(t);
                l.add(u);
            }
            return l;
        }
    }

    /**
     * Process compress modes.
     *
     * @param modecompressList
     * @param inputStream
     * @param outputStreamSink
     * @throws IOException
     */
    void processModecompress(List<Modecompress> modecompressList,
            InputStream inputStream,
            OutputStream outputStreamSink) throws IOException {
        //---
        final StreamsBuilder.OutputStreamBuilder outputStreamBuilder = new StreamsBuilder.OutputStreamBuilder();
        outputStreamBuilder.sink(outputStreamSink);
        for (Modecompress u : modecompressList) {
            if (u == Modecompress.DEFLATE) {
                outputStreamBuilder.deflate();
            } else if (u == Modecompress.GZIP) {
                outputStreamBuilder.gzip();
            } else if (u == Modecompress.B64ENC) {
                outputStreamBuilder.b64Encode();
            } else if (u == Modecompress.MIMEENC) {
                outputStreamBuilder.mimeEncode();
            }
        }
        try (final OutputStream os = outputStreamBuilder.build(); final InputStream is = inputStream) {
            IOUtils.copy(is, os);
        }
    }

}
