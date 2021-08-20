/*
 * Copyright 2021 pi.
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
package org.huberb.iostreams.commandline;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.huberb.iostreams.commandline.ProcessingModesDecompress.Modedecompress;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    @ParameterizedTest
    @CsvSource(value = {
        "ABC, QUJD",
        "0123456789, MDEyMzQ1Njc4OQ==",
        "abcdefghijklmnopqrstuvwxyz, YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo=",
        "ÄÖÜß, w4TDlsOcw58="
    })
    public void test_xxxdecompress_b64dec(String sOriginal, String sOriginalB64Encoded) throws IOException {
        final Charset charset = StandardCharsets.UTF_8;
        final String sEncoded = Base64.getEncoder().encodeToString(sOriginal.getBytes(charset));
        assertEquals(sOriginalB64Encoded, sEncoded);

        final List<Modedecompress> modes = Arrays.asList(Modedecompress.B64DEC);
        try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(sEncoded.getBytes(charset));
                UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream()) {

            final ProcessingModesDecompress processingModesDecompress = new ProcessingModesDecompress();
            processingModesDecompress.xxxdecompress(modes, bais, baos);

            baos.flush();

            final byte[] baosBytes = baos.toByteArray();
            final String result = new String(baosBytes, charset);
            assertEquals(sOriginal, result);
        }
    }

}
