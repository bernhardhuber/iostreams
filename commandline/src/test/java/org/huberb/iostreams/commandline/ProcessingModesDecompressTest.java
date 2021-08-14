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
import org.huberb.iostreams.commandline.ProcessingModesDecompress.Modedecompress;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pi
 */
public class ProcessingModesDecompressTest {

    public ProcessingModesDecompressTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of xxxdecompress method, of class ProcessingModesDecompress.
     *
     * @throws java.io.IOException
     */
    @Test
    public void test_xxxdecompress_b64dec() throws IOException {
        String s = "ABC";
        final Charset charset = Charset.forName("UTF-8");
        final String sEncoded = Base64.getEncoder().encodeToString(s.getBytes(charset));
        assertEquals("QUJD", sEncoded);

        final List<Modedecompress> modes = Arrays.asList(Modedecompress.b64dec);
        try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(sEncoded.getBytes(charset));
                UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream()) {

            final ProcessingModesDecompress xxx = new ProcessingModesDecompress();
            xxx.xxxdecompress(modes, bais, baos);

            baos.flush();

            final byte[] baosBytes = baos.toByteArray();
            final String result = new String(baosBytes, charset);
            assertEquals("ABC", result);
        }
    }

}
