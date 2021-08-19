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
package org.huberb.iostreams.commandline.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author pi
 */
@ExtendWith(MockitoExtension.class)
public class IgnoreCloseOutputStreamTest {

    public IgnoreCloseOutputStreamTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of read method, of class IgnoreCloseInputStream.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testDelegateCloseNotInvoked() throws IOException {
        final ByteArrayOutputStream spyBaos = Mockito.spy(ByteArrayOutputStream.class);
        final IgnoreCloseOutputStream ignoreCloseOutputStream = new IgnoreCloseOutputStream(spyBaos);

        final String s = "ABC";
        final Charset charset = Charset.forName("UTF-8");

        try (ignoreCloseOutputStream) {
            ignoreCloseOutputStream.write(s.getBytes(charset));
        }
        assertEquals(s, spyBaos.toString(charset));
        Mockito.verify(spyBaos, Mockito.times(1)).flush();
        Mockito.verify(spyBaos, Mockito.times(0)).close();
    }

}
