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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ChunkedOutputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author berni3
 */
public class B64GzipStreamTest {

    Logger LOG = Logger.getLogger(B64GzipStreamTest.class.getName());

    @ParameterizedTest
    @MethodSource(value = "createSampleDataStream")
    public void test_GzipB64encode_B64decodeGunzip(String s) throws IOException {

        LOG.log(Level.INFO, "test_GzipB64encode_B64decodeGunzip input {0}", s);
        final byte[] buffer;
        {
            try (final ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                try (final ByteArrayInputStream source = new ByteArrayInputStream(s.getBytes("UTF-8"));
                        final OutputStream pipe = new ChunkedOutputStream(new GZIPOutputStream(Base64.getEncoder().wrap(sink), false))) {
                    IOUtils.copy(source, pipe);
                }
                sink.flush();
                //LOG.log(Level.INFO, "GzipB46Encode {0}", sink.toString("UTF-8"));
                buffer = sink.toByteArray();
            }
        }

        {
            try (final ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                try (final ByteArrayInputStream source = new ByteArrayInputStream(buffer);
                        final InputStream pipe = new GZIPInputStream(Base64.getDecoder().wrap(source))) {
                    IOUtils.copy(pipe, sink);
                }
                sink.flush();
                LOG.log(Level.INFO, "b46DecodeGunzip {0}", sink.toString("UTF-8"));
                assertEquals(s, sink.toString("UTF-8"));
            }
        }
    }

    @ParameterizedTest
    @MethodSource(value = "createSampleDataStream")
    public void test_Gzip_Gunzip(String s) throws IOException {

        LOG.log(Level.INFO, "test_Gzip_Gunzip input {0}", s);
        final byte[] buffer;
        {
            try (final ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                try (final ByteArrayInputStream source = new ByteArrayInputStream(s.getBytes("UTF-8"));
                        final OutputStream pipe = new ChunkedOutputStream(new GZIPOutputStream(sink))) {
                    IOUtils.copy(source, pipe);
                }
                sink.flush();
                //LOG.log(Level.INFO, "Gzip {0}", sink.toString("UTF-8"));
                buffer = sink.toByteArray();
            }
        }

        {
            try (final ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                try (final ByteArrayInputStream source = new ByteArrayInputStream(buffer);
                        final InputStream pipe = new GZIPInputStream(source)) {
                    IOUtils.copy(pipe, sink);
                }
                sink.flush();
                LOG.log(Level.INFO, "Gunzip {0}", sink.toString("UTF-8"));
                assertEquals(s, sink.toString("UTF-8"));
            }
        }
    }

    /**
     * Create sample data stream.
     *
     * @return
     */
    static Stream<String> createSampleDataStream() {
        final SampleData sampleData = new SampleData();
        return Arrays.asList(
                "A", "abc",
                sampleData.createSmallSample(),
                sampleData.createSample("1234567890!\"§$%&/()=?", 64),
                sampleData.createSample("Hello", 128),
                sampleData.createSample("Lorem ipsum", 512)
        ).stream();
    }
}
