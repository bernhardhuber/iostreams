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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author berni3
 */
public class StreamsBuilderTest {

    private static final Logger LOG = Logger.getLogger(StreamsBuilderTest.class.getName());

    @Test
    public void test_String_B64EncGzip_GunzipB64Dec() throws IOException {
        String s = new SampleData().createSmallSample();
        LOG.log(Level.INFO, "input {0}", s);

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        {

            try (OutputStream os = new StreamsBuilder.OutputStreamBuilder().
                    sink(sink).
                    b64Encode().
                    gzip().
                    build();
                    InputStream is = IOUtils.toInputStream(s, "UTF-8")) {
                IOUtils.copy(is, os);
            }
            sink.flush();
        }
        LOG.log(Level.INFO, "gzipB64Encode {0}", sink.toString("UTF-8"));
        byte[] bytes = sink.toByteArray();
        ByteArrayOutputStream sink2 = new ByteArrayOutputStream();
        {
            try (ByteArrayInputStream source = new ByteArrayInputStream(bytes);
                    InputStream is = new StreamsBuilder.InputStreamBuilder().
                            source(source).
                            b64Decode().
                            gunzip().
                            build()) {
                IOUtils.copy(is, sink2);
            }
            sink2.flush();
        }
        LOG.log(Level.INFO, "b64DecodeGunzip {0}", sink2.toString("UTF-8"));

        assertEquals(s, sink2.toString("UTF-8"));
    }

    @Test
    public void test_XmlParent_MimeEncGzip_GunzipB64Dec() throws IOException {
        SampleXmlData.XmlParent xmlParent = new SampleXmlData.XmlParentFactory().createSample(10, "testXml100_");
        LOG.log(Level.INFO, "input {0}", xmlParent);

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        {
            try (OutputStream os = new StreamsBuilder.OutputStreamBuilder().
                    sink(sink).
                    mimeEncode().
                    gzip().
                    build()) {
                JAXB.marshal(xmlParent, os);
            }
            sink.flush();
        }
        LOG.log(Level.INFO, "gzipMimeEncode {0}", sink.toString("UTF-8"));
        byte[] bytes = sink.toByteArray();
        SampleXmlData.XmlParent xmlParent2;
        {
            ByteArrayInputStream source = new ByteArrayInputStream(bytes);
            try (InputStream is = new StreamsBuilder.InputStreamBuilder().
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
