/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.huberb.iostreams.commandline;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.huberb.iostreams.commandline.ProcessingModesCompress;
import org.huberb.iostreams.commandline.ProcessingModesCompress.Modecompress;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class ProcessingModesCompressTest {

    public ProcessingModesCompressTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of xxxcompress method, of class ProcessingModesCompress.
     *
     * @throws java.io.IOException
     */
    @Test
    public void test_xxxcompress_b64enc() throws IOException {
        final Charset charset = Charset.forName("UTF-8");
        String s = "ABC";

        final List<Modecompress> modes = Arrays.asList(Modecompress.b64enc);
        try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(s.getBytes(charset));
                UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream()) {

            final ProcessingModesCompress xxx = new ProcessingModesCompress();
            xxx.xxxcompress(modes, bais, baos);

            baos.flush();

            final byte[] baosBytes = baos.toByteArray();
            final String result = new String(baosBytes, charset);
            assertEquals("QUJD", result);

            final String resultDecoded = new String(Base64.getDecoder().decode(baosBytes), charset);
            assertEquals(s, resultDecoded);
        }
    }

}
