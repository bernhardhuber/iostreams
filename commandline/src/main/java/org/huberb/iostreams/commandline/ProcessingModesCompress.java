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
class ProcessingModesCompress {
    
    enum Modecompress {
        deflate, gzip, b64enc, mimeFenc
    }

    List<Modecompress> convertStringToModecompressList(String s) {
        final List<Modecompress> l = new ArrayList<>();
        final List<String> sSplittedList = Arrays.asList(s.split(","));
        for (String t : sSplittedList) {
            Modecompress u = Modecompress.valueOf(t);
            l.add(u);
        }
        return l;
    }

    void xxxcompress(List<Modecompress> l, InputStream xis, OutputStream xos) throws IOException {
        //---
        final StreamsBuilder.OutputStreamBuilder outputStreamBuilder = new StreamsBuilder.OutputStreamBuilder();
        final OutputStream baosSinkEncode = xos;
        outputStreamBuilder.sink(baosSinkEncode);
        for (Modecompress u : l) {
            if (u == Modecompress.deflate) {
                outputStreamBuilder.deflate();
            } else if (u == Modecompress.gzip) {
                outputStreamBuilder.gzip();
            } else if (u == Modecompress.b64enc) {
                outputStreamBuilder.b64Encode();
            } else if (u == Modecompress.mimeFenc) {
                outputStreamBuilder.mimeEncode();
            }
        }
        try (final OutputStream os = outputStreamBuilder.build();final InputStream is = xis) {
            IOUtils.copy(is, os);
        }
    }
    
}
