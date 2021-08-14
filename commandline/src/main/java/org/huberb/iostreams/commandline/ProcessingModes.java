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
class ProcessingModes {

    static class ProcessingModesCompress {

        enum modecompress {
            deflate, gzip, b64enc, mimeFenc
        }

        List<modecompress> convertStringToModecompressList(String s) {
            final List<modecompress> l = new ArrayList<>();
            final List<String> sSplittedList = Arrays.asList(s.split(","));
            for (String t : sSplittedList) {
                modecompress u = modecompress.valueOf(t);
                l.add(u);
            }
            return l;
        }

        void xxxcompress(List<modecompress> l, InputStream xis, OutputStream xos) throws IOException {
            //---
            final StreamsBuilder.OutputStreamBuilder outputStreamBuilder = new StreamsBuilder.OutputStreamBuilder();
            //final OutputStream baosSinkEncode = new IgnoreCloseOutputStream(System.out);
            final OutputStream baosSinkEncode = xos;
            outputStreamBuilder.sink(baosSinkEncode);
            for (modecompress u : l) {
                if (u == modecompress.deflate) {
                    outputStreamBuilder.deflate();
                } else if (u == modecompress.gzip) {
                    outputStreamBuilder.gzip();
                } else if (u == modecompress.b64enc) {
                    outputStreamBuilder.b64Encode();
                } else if (u == modecompress.mimeFenc) {
                    outputStreamBuilder.mimeEncode();
                }
            }
            try (final OutputStream os = outputStreamBuilder.build();
                    final InputStream is = xis) {
                IOUtils.copy(is, os);
            }
        }
    }

    static class ProcessingModesDecompress {

        enum modedecompress {
            inflate, gunzip, b64dec, mimedec
        }

        List<modedecompress> convertStringToModedecompressList(String s) {
            final List<modedecompress> l = new ArrayList<>();
            final List<String> sSplittedList = Arrays.asList(s.split(","));
            for (String t : sSplittedList) {
                modedecompress u = modedecompress.valueOf(t);
                l.add(u);
            }
            return l;
        }

        void xxxdecompress(List<modedecompress> l, InputStream xis, OutputStream xos) throws IOException {
            //---
            final StreamsBuilder.InputStreamBuilder inputStreamBuilder = new StreamsBuilder.InputStreamBuilder();
            inputStreamBuilder.source(xis);
            for (modedecompress u : l) {
                if (u == modedecompress.inflate) {
                    inputStreamBuilder.inflate();
                } else if (u == modedecompress.gunzip) {
                    inputStreamBuilder.gunzip();
                } else if (u == modedecompress.b64dec) {
                    inputStreamBuilder.b64Decode();
                } else if (u == modedecompress.mimedec) {
                    inputStreamBuilder.mimeDecode();
                }
            }
            //final OutputStream baosSinkDecode = new IgnoreCloseOutputStream(System.out);
            // b64gzipAAA -> b64decode -> gunzip -> AAA
            try (final OutputStream baosSinkDecode = xos;
                    final InputStream is = inputStreamBuilder.build()) {
                IOUtils.copy(is, baosSinkDecode);
            }
        }
    }
}
