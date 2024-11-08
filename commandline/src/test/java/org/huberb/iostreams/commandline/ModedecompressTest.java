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
import org.huberb.iostreams.commandline.ProcessingModesDecompress.ModeDecompress;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author pi
 */
public class ModedecompressTest {

    public ModedecompressTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test method {@link ModeDecompress#convertStringToModedecompressList}.
     *
     * @param t
     */
    @ParameterizedTest
    @MethodSource("sampleData")
    public void testConvertStringToModedecompress(Tuple<String, List<ModeDecompress>> t) {
        final String s = t.r();
        final List<ModeDecompress> l = t.s();

        final String m = String.format("input %s, modecompress %s", s, l);
        assertEquals(l,
                ModeDecompress.convertStringToModedecompressList(s),
                m);
    }

    /**
     * Generate sample  data.
     * 
     * @return tuple of string representation, and parsed enum representation.
     */
    static Stream<Tuple<String, List<ModeDecompress>>> sampleData() {

        final List<Tuple<String, List<ModeDecompress>>> l = Arrays.asList(new Tuple<>("B64DEC", Arrays.asList(ModeDecompress.B64DEC)),
                new Tuple<>("GUNZIP", Arrays.asList(ModeDecompress.GUNZIP)),
                new Tuple<>("INFLATE", Arrays.asList(ModeDecompress.INFLATE)),
                new Tuple<>("MIMEDEC", Arrays.asList(ModeDecompress.MIMEDEC)),
                new Tuple<>("B64DEC,GUNZIP", Arrays.asList(ModeDecompress.B64DEC, ModeDecompress.GUNZIP)),
                new Tuple<>("MIMEDEC,INFLATE", Arrays.asList(ModeDecompress.MIMEDEC, ModeDecompress.INFLATE)),
                new Tuple<>("B64DEC,GUNZIP,INFLATE,MIMEDEC", Arrays.asList(ModeDecompress.B64DEC, ModeDecompress.GUNZIP, ModeDecompress.INFLATE, ModeDecompress.MIMEDEC))
        );
        final Stream<Tuple<String, List<ModeDecompress>>> result = l.stream();
        return result;
    }

}
