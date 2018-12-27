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
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ChunkedOutputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author berni3
 */
public class B64GzipStreamTest {

    Logger LOG = Logger.getLogger(B64GzipStreamTest.class.getName());

    @Test
    public void test_GzipB64encode_B64decodeGunzip() throws IOException {
        //String s = new SampleData().createSmallSample();
        final int dataSize = 1*1024;
        String s = new SampleData().createSample("Hello", dataSize);
        assertEquals( dataSize,s.length());
        
        LOG.log(Level.INFO, "input {0}", s);
        byte[] buffer;
        {
            try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                try (ByteArrayInputStream source = new ByteArrayInputStream(s.getBytes("UTF-8"));
                        OutputStream pipe = new ChunkedOutputStream(new GZIPOutputStream(Base64.getEncoder().wrap(sink), false))) {
                    IOUtils.copy(source, pipe);
                }
                sink.flush();
                LOG.log(Level.INFO, "GzipB46Encode {0}", sink.toString("UTF-8"));
                buffer = sink.toByteArray();
            }
        }

        {
            try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
                try (ByteArrayInputStream source = new ByteArrayInputStream(buffer);
                        InputStream pipe = new GZIPInputStream(Base64.getDecoder().wrap(source))) {
                    IOUtils.copy(pipe, sink);
                }
                sink.flush();
                LOG.log(Level.INFO, "b46DecodeGunzip {0}", sink.toString("UTF-8"));
                assertEquals(s, sink.toString("UTF-8"));
            }
        }
    }
}
