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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author berni3
 */
public class FunctionsTest {

    Logger LOG = Logger.getLogger(FunctionsTest.class.getName());

    @Test
    public void test_B64_Encode_Decode() {
        String abc = new SampleData().createSmallSample();

        String out1 = new Functions.FConvertStringBytes().convertToBytes().
                andThen(new Functions.FBase64().encode()).
                andThen(new Functions.FConvertStringBytes().convertToString()).
                apply(abc);
        assertNotNull(out1);
        LOG.log(Level.INFO, "encode: {0} -> {1}", new Object[]{abc, out1});

        String out2 = new Functions.FConvertStringBytes().convertToBytes().
                andThen(new Functions.FBase64().decode()).
                andThen(new Functions.FConvertStringBytes().convertToString()).
                apply(out1);
        assertNotNull(out2);
        LOG.log(Level.INFO, "decode: {0} -> {1}", new Object[]{abc, out2});

        assertEquals(abc, out2);
    }

    @Test
    public void test_B64_MimeEncode_MimeDecode() {
        String abc = new SampleData().createSmallSample();

        String out1 = new Functions.FConvertStringBytes().convertToBytes().
                andThen(new Functions.FBase64().mimeEncode()).
                andThen(new Functions.FConvertStringBytes().convertToString()).
                apply(abc);
        assertNotNull(out1);
        LOG.log(Level.INFO, "mimeEncode: {0} -> {1}", new Object[]{abc, out1});

        String out2 = new Functions.FConvertStringBytes().convertToBytes().
                andThen(new Functions.FBase64().mimeDecode()).
                andThen(new Functions.FConvertStringBytes().convertToString()).
                apply(out1);
        assertNotNull(out2);
        LOG.log(Level.INFO, "mimeDecode: {0} -> {1}", new Object[]{abc, out2});

        assertEquals(abc, out2);
    }

    @Test
    public void test_Gzip_B64Enc_B64Dec_Gunzip() {
        String abc = new SampleData().createSmallSample();

        String out1 = new Functions.FConvertStringBytes().convertToBytes().
                andThen(new Functions.FGzip().gzipCompress()).
                andThen(new Functions.FBase64().encode()).
                andThen(new Functions.FConvertStringBytes().convertToString()).
                apply(abc);
        assertNotNull(out1);
        LOG.log(Level.INFO, "gzip-encode: {0} -> {1}", new Object[]{abc, out1});

        String out2 = new Functions.FConvertStringBytes().convertToBytes().
                andThen(new Functions.FBase64().decode()).
                andThen(new Functions.FGzip().gzipDecompress()).
                andThen(new Functions.FConvertStringBytes().convertToString()).
                apply(out1);
        assertNotNull(out2);
        LOG.log(Level.INFO, "decode-gunzip: {0} -> {1}", new Object[]{abc, out2});

        assertEquals(abc, out2);
    }

    @Test
    public void test_Compress_Decompress() throws UnsupportedEncodingException {
        String abc = new SampleData().createSmallSample();

        byte[] b1 = new Functions.FGzip().gzipCompress().apply(abc.getBytes("UTF-8"));
        byte[] b2 = new Functions.FGzip().gzipDecompress().apply(b1);
        String abc2 = new String(b2, "UTF-8");

        assertNotNull(abc2);
        LOG.log(Level.INFO, "gzip-gunzip: {0} -> {1}", new Object[]{abc, abc2});
        assertEquals(abc, abc2);
    }
}
