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
import java.io.OutputStream;

/**
 * Output stream ignoring {@link OutputStream#close()}.
 * <p>
 * Usually only used for wrapping {@link System#out}, or {@link System#err}.
 */
class IgnoreCloseOutputStream extends OutputStream {

    private final OutputStream delegate;

    public IgnoreCloseOutputStream(OutputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void close() throws IOException {
        // ignore close, just flush
        delegate.flush();
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
    }
    
}
