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
import org.huberb.iostreams.commandline.ProcessingModesDecompress.Modedecompress;
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

    @ParameterizedTest
    @MethodSource("sampleData")
    public void testConvertStringToModedecompress(Tuple<String, List<Modedecompress>> t) {
        final String s = t.r;
        final List<Modedecompress> l = t.s;

        final String m = String.format("input %s, modecompress %s", s, l);
        assertEquals(l,
                Modedecompress.convertStringToModedecompressList(s),
                m);
    }

    static Stream<Tuple<String, List<Modedecompress>>> sampleData() {

        final List<Tuple<String, List<Modedecompress>>> l = Arrays.asList(
                new Tuple<>("b64dec", Arrays.asList(Modedecompress.b64dec)),
                new Tuple<>("gunzip", Arrays.asList(Modedecompress.gunzip)),
                new Tuple<>("inflate", Arrays.asList(Modedecompress.inflate)),
                new Tuple<>("mimedec", Arrays.asList(Modedecompress.mimedec)),
                new Tuple<>("b64dec,gunzip,inflate,mimedec", Arrays.asList(Modedecompress.b64dec, Modedecompress.gunzip, Modedecompress.inflate, Modedecompress.mimedec))
        );
        final Stream<Tuple<String, List<Modedecompress>>> result = l.stream();
        return result;
    }

    static class Tuple<R, S> {

        final R r;
        final S s;

        public Tuple(R r, S s) {
            this.r = r;
            this.s = s;
        }

    }
}
