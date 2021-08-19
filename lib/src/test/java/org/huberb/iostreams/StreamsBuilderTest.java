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
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.xml.bind.JAXB;
import org.apache.commons.io.IOUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 *
 * @author berni3
 */
public class StreamsBuilderTest {

    private static final Logger LOG = Logger.getLogger(StreamsBuilderTest.class.getName());

    @BeforeAll
    public static void beforeAll() {
        LOG.setLevel(Level.OFF);
    }

    @ParameterizedTest
    @MethodSource(value = "createSampleDataStream")
    public void test_String_B64EncGzip_GunzipB64Dec(String s) throws IOException {
        LOG.log(Level.INFO, "test_String_B64EncGzip_GunzipB64Dec input {0}", s);

        // encode AAA -> b64(gzip(AAA)) 
        final ByteArrayOutputStream baosSinkEncode = new ByteArrayOutputStream();
        {

            // AAA -> gzip -> b64encode -> b64gzipAAA
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

        // decode b64(gzip(AAA)) -> AAA
        final byte[] bytesOfBaosSinkEncoded = baosSinkEncode.toByteArray();
        final ByteArrayOutputStream baosSinkDecode = new ByteArrayOutputStream();
        {
            // b64gzipAAA -> b64decode -> gunzip -> AAA
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

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 100})
    public void test_XmlParent_MimeEncGzip_GunzipB64Dec(int n) throws IOException {
        final SampleXmlData.XmlParent xmlParent = new SampleXmlData.XmlParentFactory().createSample(n, "testXml100_");
        LOG.log(Level.INFO, "test_XmlParent_MimeEncGzip_GunzipB64Dec input {0}", xmlParent);

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

        try (StringWriter swXmlParent = new StringWriter();
                StringWriter swXmlParent2 = new StringWriter()) {
            JAXB.marshal(xmlParent, swXmlParent);
            JAXB.marshal(xmlParent2, swXmlParent2);

            swXmlParent.flush();
            swXmlParent2.flush();

            assertEquals(swXmlParent.toString(), swXmlParent2.toString());
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
                "A",
                "abc",
                sampleData.createSmallSample(),
                sampleData.createSample("1234567890!\"§$%&/()=?", 64),
                sampleData.createSample("Hello", 128),
                sampleData.createSample("Lorem ipsum", 512)
        ).stream();
    }

}
