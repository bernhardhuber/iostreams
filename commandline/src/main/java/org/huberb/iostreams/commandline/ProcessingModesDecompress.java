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
 *
 * @author pi
 */
class ProcessingModesDecompress {
    
    enum Modedecompress {
        inflate, gunzip, b64dec, mimedec
    }

    List<Modedecompress> convertStringToModedecompressList(String s) {
        final List<Modedecompress> l = new ArrayList<>();
        final List<String> sSplittedList = Arrays.asList(s.split(","));
        for (String t : sSplittedList) {
            Modedecompress u = Modedecompress.valueOf(t);
            l.add(u);
        }
        return l;
    }

    void xxxdecompress(List<Modedecompress> l, InputStream xis, OutputStream xos) throws IOException {
        //---
        final StreamsBuilder.InputStreamBuilder inputStreamBuilder = new StreamsBuilder.InputStreamBuilder();
        inputStreamBuilder.source(xis);
        for (Modedecompress u : l) {
            if (u == Modedecompress.inflate) {
                inputStreamBuilder.inflate();
            } else if (u == Modedecompress.gunzip) {
                inputStreamBuilder.gunzip();
            } else if (u == Modedecompress.b64dec) {
                inputStreamBuilder.b64Decode();
            } else if (u == Modedecompress.mimedec) {
                inputStreamBuilder.mimeDecode();
            }
        }
        //final OutputStream baosSinkDecode = new IgnoreCloseOutputStream(System.out);
        // b64gzipAAA -> b64decode -> gunzip -> AAA
        try (final OutputStream baosSinkDecode = xos;final InputStream is = inputStreamBuilder.build()) {
            IOUtils.copy(is, baosSinkDecode);
        }
    }
    
}
