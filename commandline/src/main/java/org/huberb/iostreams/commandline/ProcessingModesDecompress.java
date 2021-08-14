/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
