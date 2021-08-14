/*
 * Copyright 2018 berni3.
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
package org.huberb.iostreams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.function.Function;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;

/**
 * Wrapper of converting, encoding, and compressing functions.
 *
 * @author berni3
 */
public class Functions {

    /**
     * Functions wrapper of base64, and Mime encoder, and decoder.
     */
    public static class FBase64 {

        /**
         * Creates a function to encode a byte array using type base64 encoding
         * scheme.
         *
         * @return
         * @see Base64
         */
        public Function<byte[], byte[]> encode() {
            return (byte[] src) -> Base64.getEncoder().encode(src);
        }

        /**
         * Creates a function to encode a byte array using type base64 encoding
         * scheme.
         *
         * @return
         * @see Base64
         */
        public Function<byte[], byte[]> mimeEncode() {
            return (byte[] src) -> Base64.getMimeEncoder().encode(src);
        }

        /**
         * Creates a function to decode a byte array using type base64 decoding
         * scheme.
         *
         * @return
         * @see Base64
         */
        public Function<byte[], byte[]> decode() {
            return (byte[] src) -> Base64.getDecoder().decode(src);
        }

        /**
         * Creates a function to encode a byte array using type base64 decoding
         * scheme.
         *
         * @return
         * @see Base64
         */
        public Function<byte[], byte[]> mimeDecode() {
            return (byte[] src) -> Base64.getMimeDecoder().decode(src);
        }

    }

    /**
     * Functions wrappers converting a string to byte[] and vice versa.
     */
    public static class FConvertStringBytes {

        /**
         * Creates a function to convert a byte array to a string using UTF-8.
         *
         * @return
         * @see java.lang.String#String(byte[], java.nio.charset.Charset)
         */
        public Function<byte[], String> convertToString() {
            return (byte[] b) -> {
                try {
                    return new String(b, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new StreamsException("convertToString", ex);
                }
            };
        }

        /**
         * Creates a function to convert a string to a byte array using UTF-8.
         *
         * @return
         * @see java.lang.String#getBytes(java.nio.charset.Charset)
         */
        public Function<String, byte[]> convertToBytes() {
            return (String s) -> {
                try {
                    return s.getBytes("UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new StreamsException("convertToBytes", ex);
                }
            };
        }
    }

    /**
     * Functions wrapper to compress, and decompress using gzip.
     */
    public static class FGzip {

        /**
         * Creates a function for g-zipping a byte array.
         *
         * @return
         * @see GZIPOutputStream
         */
        public Function<byte[], byte[]> gzipCompress() {
            return (byte[] source) -> {
                try (UnsynchronizedByteArrayOutputStream sink = new UnsynchronizedByteArrayOutputStream()) {
                    try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(source);
                            GZIPOutputStream gos = new GZIPOutputStream(sink, false)) {
                        IOUtils.copy(bais, gos);
                    }
                    sink.flush();
                    return sink.toByteArray();
                } catch (IOException ioex) {
                    throw new StreamsException("gzipCompress", ioex);
                }
            };
        }

        /**
         * Creates a function for gun-zipping a byte array.
         *
         * @return
         * @see GZIPInputStream
         */
        public Function<byte[], byte[]> gzipDecompress() {
            return (byte[] source) -> {
                try (UnsynchronizedByteArrayOutputStream sink = new UnsynchronizedByteArrayOutputStream()) {
                    try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(source);
                            GZIPInputStream gis = new GZIPInputStream(bais)) {
                        IOUtils.copy(gis, sink);
                    }
                    sink.flush();
                    return sink.toByteArray();
                } catch (IOException ioex) {
                    throw new StreamsException("gzipDecompress", ioex);
                }
            };
        }
    }

    /**
     * Function wrapper to compress, and decompress using inflate, and deflate
     * algorithm.
     */
    public static class FInflateDeflate {

        /**
         * Creates function to deflate a byte array.
         *
         * @return
         * @see DeflaterOutputStream
         */
        public Function<byte[], byte[]> deflateCompress() {
            return (byte[] source) -> {
                try (UnsynchronizedByteArrayOutputStream sink = new UnsynchronizedByteArrayOutputStream()) {
                    try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(source);
                            DeflaterOutputStream gos = new DeflaterOutputStream(sink, false)) {
                        IOUtils.copy(bais, gos);
                    }
                    sink.flush();
                    return sink.toByteArray();
                } catch (IOException ioex) {
                    throw new StreamsException("deflateCompress", ioex);
                }
            };
        }

        /**
         * Creates a function to inflate a byte array.
         *
         * @return
         * @see InflaterInputStream
         */
        public Function<byte[], byte[]> inflateDecompress() {
            return (byte[] source) -> {
                try (UnsynchronizedByteArrayOutputStream sink = new UnsynchronizedByteArrayOutputStream()) {
                    try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(source);
                            InflaterInputStream gis = new InflaterInputStream(bais)) {
                        IOUtils.copy(gis, sink);
                    }
                    sink.flush();
                    return sink.toByteArray();
                } catch (IOException ioex) {
                    throw new StreamsException("inflateDecompress", ioex);
                }
            };
        }
    }
}
