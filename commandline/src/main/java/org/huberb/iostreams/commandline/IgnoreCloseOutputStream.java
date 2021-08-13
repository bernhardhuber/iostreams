/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
