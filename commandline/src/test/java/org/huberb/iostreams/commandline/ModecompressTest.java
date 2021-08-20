package org.huberb.iostreams.commandline;

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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.huberb.iostreams.commandline.ProcessingModesCompress.Modecompress;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author pi
 */
public class ModecompressTest {

    public ModecompressTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test method {@link  Modecompress#convertStringToModecompressList}.
     *
     * @param t
     */
    @ParameterizedTest
    @MethodSource("sampleData")
    public void testConvertStringToModecompress(Tuple<String, List<Modecompress>> t) {
        final String s = t.r;
        final List<Modecompress> l = t.s;

        final String m = String.format("input %s, modecompress %s", s, l);
        assertEquals(l,
                Modecompress.convertStringToModecompressList(s),
                m);
    }

    static Stream<Tuple<String, List<Modecompress>>> sampleData() {

        final List<Tuple<String, List<Modecompress>>> l = Arrays.asList(
                new Tuple<>("b64enc", Arrays.asList(Modecompress.b64enc)),
                new Tuple<>("deflate", Arrays.asList(Modecompress.deflate)),
                new Tuple<>("gzip", Arrays.asList(Modecompress.gzip)),
                new Tuple<>("mimeenc", Arrays.asList(Modecompress.mimeenc)),
                new Tuple<>("b64enc,deflate,gzip,mimeenc", Arrays.asList(Modecompress.b64enc, Modecompress.deflate, Modecompress.gzip, Modecompress.mimeenc))
        );
        final Stream<Tuple<String, List<Modecompress>>> result = l.stream();
        return result;
    }

}
