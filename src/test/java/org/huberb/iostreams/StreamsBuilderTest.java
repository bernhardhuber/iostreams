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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXB;
import org.apache.commons.io.IOUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author berni3
 */
public class StreamsBuilderTest {

    private static final Logger LOG = Logger.getLogger(StreamsBuilderTest.class.getName());

    @Test
    public void test_String_B64EncGzip_GunzipB64Dec() throws IOException {
        final String s = new SampleData().createSmallSample();
        LOG.log(Level.INFO, "input {0}", s);

        final ByteArrayOutputStream baosSinkEncode = new ByteArrayOutputStream();
        {

            try (final OutputStream os = new StreamsBuilder.OutputStreamBuilder().
                    sink(baosSinkEncode).
                    b64Encode().
                    gzip().
                    build();
                    final InputStream is = IOUtils.toInputStream(s, "UTF-8")) {
                IOUtils.copy(is, os);
            }
            baosSinkEncode.flush();
        }
        LOG.log(Level.INFO, "gzipB64Encode {0}", baosSinkEncode.toString("UTF-8"));
        final byte[] bytesOfBaosSinkEncoded = baosSinkEncode.toByteArray();
        final ByteArrayOutputStream baosSinkDecode = new ByteArrayOutputStream();
        {
            try (final ByteArrayInputStream source = new ByteArrayInputStream(bytesOfBaosSinkEncoded);
                    final InputStream is = new StreamsBuilder.InputStreamBuilder().
                            source(source).
                            b64Decode().
                            gunzip().
                            build()) {
                IOUtils.copy(is, baosSinkDecode);
            }
            baosSinkDecode.flush();
        }
        LOG.log(Level.INFO, "b64DecodeGunzip {0}", baosSinkDecode.toString("UTF-8"));

        assertEquals(s, baosSinkDecode.toString("UTF-8"));
    }

    @Test
    public void test_XmlParent_MimeEncGzip_GunzipB64Dec() throws IOException {
        final SampleXmlData.XmlParent xmlParent = new SampleXmlData.XmlParentFactory().createSample(10, "testXml100_");
        LOG.log(Level.INFO, "input {0}", xmlParent);

        final ByteArrayOutputStream baosSinkEncode = new ByteArrayOutputStream();
        {
            try (final OutputStream os = new StreamsBuilder.OutputStreamBuilder().
                    sink(baosSinkEncode).
                    mimeEncode().
                    gzip().
                    build()) {
                JAXB.marshal(xmlParent, os);
            }
            baosSinkEncode.flush();
        }
        LOG.log(Level.INFO, "gzipMimeEncode {0}", baosSinkEncode.toString("UTF-8"));
        byte[] bytes = baosSinkEncode.toByteArray();
        final SampleXmlData.XmlParent xmlParent2;
        {
            final ByteArrayInputStream source = new ByteArrayInputStream(bytes);
            try (final InputStream is = new StreamsBuilder.InputStreamBuilder().
                    source(source).
                    mimeDecode().
                    gunzip().
                    build()) {
                xmlParent2 = JAXB.unmarshal(is, SampleXmlData.XmlParent.class);
            }
        }
        LOG.log(Level.INFO, "decodeMimeGunzip {0}", xmlParent2);

        assertEquals(xmlParent.getParentName(), xmlParent2.getParentName());
    }

}
