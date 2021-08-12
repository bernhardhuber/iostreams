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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.function.Function;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author berni3
 */
public class Functions {

    static class FBase64 {

        Function<byte[], byte[]> encode() {
            return (byte[] src) -> Base64.getEncoder().encode(src);
        }

        Function<byte[], byte[]> mimeEncode() {
            return (byte[] src) -> Base64.getMimeEncoder().encode(src);
        }

        Function<byte[], byte[]> decode() {
            return (byte[] src) -> Base64.getDecoder().decode(src);
        }

        Function<byte[], byte[]> mimeDecode() {
            return (byte[] src) -> Base64.getMimeDecoder().decode(src);
        }

    }

    static class FConvertStringBytes {

        Function<byte[], String> convertToString() {
            return (byte[] b) -> {
                try {
                    return new String(b, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new StreamsException(ex);
                }
            };
        }

        Function<String, byte[]> convertToBytes() {
            return (String s) -> {
                try {
                    return s.getBytes("UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new StreamsException(ex);
                }
            };
        }
    }

    static class FGzip {

        Function<byte[], byte[]> gzipCompress() {
            return (byte[] source) -> {
                try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(source);
                            GZIPOutputStream gos = new GZIPOutputStream(sink, false)) {
                        IOUtils.copy(bais, gos);
                    }
                    sink.flush();
                    final byte[] bytes = sink.toByteArray();
                    return bytes;
                } catch (IOException ioex) {
                    throw new StreamsException(ioex);
                }
            };
        }

        Function<byte[], byte[]> gzipDecompress() {
            return (byte[] source) -> {
                try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(source);
                            GZIPInputStream gis = new GZIPInputStream(bais)) {
                        IOUtils.copy(gis, sink);

                    }
                    sink.flush();
                    return sink.toByteArray();
                } catch (IOException ioex) {
                    throw new StreamsException(ioex);
                }
            };
        }
    }
    static class FInflateDeflate {

        Function<byte[], byte[]> deflateCompress() {
            return (byte[] source) -> {
                try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(source);
                             DeflaterOutputStream gos = new  DeflaterOutputStream(sink, false)) {
                        IOUtils.copy(bais, gos);
                    }
                    sink.flush();
                    return sink.toByteArray();
                } catch (IOException ioex) {
                    throw new StreamsException(ioex);
                }
            };
        }

        Function<byte[], byte[]> inflateDecompress() {
            return (byte[] source) -> {
                try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(source);
                             InflaterInputStream gis = new  InflaterInputStream(bais)) {
                        IOUtils.copy(gis, sink);
                    }
                    sink.flush();
                    return sink.toByteArray();
                } catch (IOException ioex) {
                    throw new StreamsException(ioex);
                }
            };
        }
    }
}
