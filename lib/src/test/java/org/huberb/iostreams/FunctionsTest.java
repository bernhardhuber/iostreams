package org.huberb.iostreams;

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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author berni3
 */
public class FunctionsTest {

    private static final Logger LOG = Logger.getLogger(FunctionsTest.class.getName());

    @BeforeAll
    public static void beforeAll() {
        LOG.setLevel(Level.OFF);
    }

    @ParameterizedTest
    @MethodSource(value = "createSampleDataStream")
    public void test_B64_Encode_Decode(String s) {

        final String out1 = new Functions.FConvertStringBytes().convertToBytes()
                .andThen(new Functions.FBase64().encode())
                .andThen(new Functions.FConvertStringBytes().convertToString()).apply(s);
        assertNotNull(out1);
        LOG.log(Level.INFO, "encode: {0} -> {1}", new Object[] { s, out1 });

        final String out2 = new Functions.FConvertStringBytes().convertToBytes()
                .andThen(new Functions.FBase64().decode())
                .andThen(new Functions.FConvertStringBytes().convertToString()).apply(out1);
        assertNotNull(out2);
        LOG.log(Level.INFO, "decode: {0} -> {1}", new Object[] { s, out2 });

        assertEquals(s, out2);
    }

    @ParameterizedTest
    @MethodSource(value = "createSampleDataStream")
    public void test_B64_MimeEncode_MimeDecode(String s) {

        final String out1 = new Functions.FConvertStringBytes().convertToBytes()
                .andThen(new Functions.FBase64().mimeEncode())
                .andThen(new Functions.FConvertStringBytes().convertToString()).apply(s);
        assertNotNull(out1);
        LOG.log(Level.INFO, "mimeEncode: {0} -> {1}", new Object[] { s, out1 });

        final String out2 = new Functions.FConvertStringBytes().convertToBytes()
                .andThen(new Functions.FBase64().mimeDecode())
                .andThen(new Functions.FConvertStringBytes().convertToString()).apply(out1);
        assertNotNull(out2);
        LOG.log(Level.INFO, "mimeDecode: {0} -> {1}", new Object[] { s, out2 });

        assertEquals(s, out2);
    }

    @ParameterizedTest
    @MethodSource(value = "createSampleDataStream")
    public void test_Gzip_B64Enc_B64Dec_Gunzip(String s) {

        final String out1 = new Functions.FConvertStringBytes().convertToBytes()
                .andThen(new Functions.FGzip().gzipCompress()).andThen(new Functions.FBase64().encode())
                .andThen(new Functions.FConvertStringBytes().convertToString()).apply(s);
        assertNotNull(out1);
        LOG.log(Level.INFO, "gzip-encode: {0} -> {1}", new Object[] { s, out1 });

        final String out2 = new Functions.FConvertStringBytes().convertToBytes()
                .andThen(new Functions.FBase64().decode()).andThen(new Functions.FGzip().gzipDecompress())
                .andThen(new Functions.FConvertStringBytes().convertToString()).apply(out1);
        assertNotNull(out2);
        LOG.log(Level.INFO, "decode-gunzip: {0} -> {1}", new Object[] { s, out2 });

        assertEquals(s, out2);
    }

    @ParameterizedTest
    @MethodSource(value = "createSampleDataStream")
    public void test_Compress_Decompress(String s) throws UnsupportedEncodingException {
        final Charset charset = StandardCharsets.UTF_8;
        final byte[] b1 = new Functions.FGzip().gzipCompress().apply(s.getBytes(charset));
        final byte[] b2 = new Functions.FGzip().gzipDecompress().apply(b1);
        final String abc2 = new String(b2, charset);

        assertNotNull(abc2);
        LOG.log(Level.INFO, "gzip-gunzip: {0} -> {1}", new Object[] { s, abc2 });
        assertEquals(s, abc2);
    }

    /**
     * Create sample data stream.
     *
     * @return
     */
    static Stream<String> createSampleDataStream() {
        final SampleData sampleData = new SampleData();
        return Arrays.asList("A", "abc", sampleData.createSmallSample(),
                sampleData.createSample("1234567890!\"§$%&/()=?", 64), sampleData.createSample("Hello", 128),
                sampleData.createSample("Lorem ipsum", 512)).stream();
    }
}
