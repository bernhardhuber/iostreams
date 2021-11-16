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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import org.huberb.iostreams.commandline.Main.FromFileOrStdinExclusive;
import org.huberb.iostreams.commandline.Main.InputStreamFromFileOrStdinExclusiveFactory;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author pi
 */
public class InputStreamFromFileOrStdinExclusiveFactoryTest {

    public InputStreamFromFileOrStdinExclusiveFactoryTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testInputStreamFromExclusiveFactoryCreate_stdin() throws IOException {
        final FromFileOrStdinExclusive inputStreamFromExclusive = new FromFileOrStdinExclusive();
        inputStreamFromExclusive.fromFile = null;
        inputStreamFromExclusive.stdin = true;

        final InputStreamFromFileOrStdinExclusiveFactory inputStreamFromExclusiveFactory = new InputStreamFromFileOrStdinExclusiveFactory(inputStreamFromExclusive);
        final Optional<InputStream> result = inputStreamFromExclusiveFactory.create();
        assertFalse(result.isEmpty());
        assertTrue(result.isPresent());
        assertEquals("org.huberb.iostreams.commandline.support.IgnoreCloseInputStream", result.get().getClass().getName());
        result.get().close();
    }

    @Test
    public void testInputStreamFromExclusiveFactoryCreate_file(@TempDir Path tempDir) throws IOException {
        final Path aFilePath = tempDir.resolve("testInputStreamFromExclusiveFactoryCreate_file");
        final File aFile = aFilePath.toFile();
        if (!aFile.exists()) {
            aFile.createNewFile();
        }
        assertTrue(aFile.exists());

        final FromFileOrStdinExclusive inputStreamFromExclusive = new FromFileOrStdinExclusive();
        inputStreamFromExclusive.fromFile = aFile;
        inputStreamFromExclusive.stdin = false;

        final InputStreamFromFileOrStdinExclusiveFactory inputStreamFromExclusiveFactory = new InputStreamFromFileOrStdinExclusiveFactory(inputStreamFromExclusive);
        final Optional<InputStream> result = inputStreamFromExclusiveFactory.create();
        assertFalse(result.isEmpty());
        assertTrue(result.isPresent());
        assertEquals("java.io.FileInputStream", result.get().getClass().getName());
        result.get().close();
    }

    @Test
    public void testInputStreamFromExclusiveFactoryCreate_none() throws IOException {
        final FromFileOrStdinExclusive inputStreamFromExclusive = new FromFileOrStdinExclusive();
        inputStreamFromExclusive.fromFile = null;
        inputStreamFromExclusive.stdin = false;

        final InputStreamFromFileOrStdinExclusiveFactory inputStreamFromExclusiveFactory = new InputStreamFromFileOrStdinExclusiveFactory(inputStreamFromExclusive);
        final Optional<InputStream> result = inputStreamFromExclusiveFactory.create();
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
    }

}
