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
import org.huberb.iostreams.commandline.ProcessingModesCompress.ModeCompress;
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
     * Test method {@link  ModeCompress#convertStringToModecompressList}.
     *
     * @param t
     */
    @ParameterizedTest
    @MethodSource("sampleData")
    public void testConvertStringToModecompress(Tuple<String, List<ModeCompress>> t) {
        final String s = t.r();
        final List<ModeCompress> l = t.s();

        final String m = String.format("input %s, modecompress %s", s, l);
        assertEquals(l,
                ModeCompress.convertStringToModecompressList(s),
                m);
    }

    /**
     * Generate sample  data.
     * 
     * @return tuple of string representation, and parsed enum representation.
     */
    static Stream<Tuple<String, List<ModeCompress>>> sampleData() {

        final List<Tuple<String, List<ModeCompress>>> l = Arrays.asList(new Tuple<>("B64ENC", Arrays.asList(ModeCompress.B64ENC)),
                new Tuple<>("DEFLATE", Arrays.asList(ModeCompress.DEFLATE)),
                new Tuple<>("GZIP", Arrays.asList(ModeCompress.GZIP)),
                new Tuple<>("MIMEENC", Arrays.asList(ModeCompress.MIMEENC)),
                new Tuple<>("B64ENC,GZIP", Arrays.asList(ModeCompress.B64ENC, ModeCompress.GZIP)),
                new Tuple<>("MIMEENC,DEFLATE", Arrays.asList(ModeCompress.MIMEENC, ModeCompress.DEFLATE)),
                new Tuple<>("B64ENC,DEFLATE,GZIP,MIMEENC", Arrays.asList(ModeCompress.B64ENC, ModeCompress.DEFLATE, ModeCompress.GZIP, ModeCompress.MIMEENC))
        );
        final Stream<Tuple<String, List<ModeCompress>>> result = l.stream();
        return result;
    }

}
