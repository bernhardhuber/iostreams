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
package org.huberb.iostreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

/**
 *
 * @author berni3
 */
public class StreamsBuilder {

    /**
     * Builds a chain of connected output streams.
     *
     */
    public static class OutputStreamBuilder {

        private final List<Function<OutputStream, OutputStream>> functionList = new ArrayList<>();
        private OutputStream sink;

        /**
         * Define the final output stream (sink output stream).
         *
         * @param os
         * @return
         */
        public OutputStreamBuilder sink(OutputStream os) {
            this.sink = os;
            return this;
        }

        /**
         * Add a base64 encoder to the chain of output streams.
         *
         * @return
         */
        public OutputStreamBuilder b64Encode() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> Base64.getEncoder().wrap(os);
            functionList.add(f);
            return this;
        }

        /**
         * Add a mime encoder to the chain of output streams.
         *
         * @return
         */
        public OutputStreamBuilder mimeEncode() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> Base64.getMimeEncoder().wrap(os);
            functionList.add(f);
            return this;
        }

        /**
         * Add a gzipper to the chain of output streams.
         *
         * @return
         * @see GZIPOutputStream#GZIPOutputStream(java.io.OutputStream) 
         */
        public OutputStreamBuilder gzip() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> {
                try {
                    return new GZIPOutputStream(os);
                } catch (IOException ex) {
                    throw new IOStreamsException("gzip", ex);
                }
            };
            functionList.add(f);
            return this;
        }

        /**
         * Add a deflater to the chain of output streams.
         *
         * @return
         */
        public OutputStreamBuilder deflate() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> {
                return new DeflaterOutputStream(os);
            };
            functionList.add(f);
            return this;
        }

        /**
         * Build the chain of output streams.
         *
         * @return
         */
        public OutputStream build() {
            final OutputStream outputStream = this.sink;
            OutputStream outputStreamApplying = outputStream;

            for (int i = 0; i < functionList.size(); i++) {
                final Function<OutputStream, OutputStream> f = functionList.get(i);
                outputStreamApplying = f.apply(outputStreamApplying);
            }
            return outputStreamApplying;
        }
    }

    /**
     * Build a chain of connected input streams.
     *
     */
    public static class InputStreamBuilder {

        private final List<Function<InputStream, InputStream>> functionList = new ArrayList<>();
        private InputStream source;

        /**
         * Define the initial input stream (source input stream).
         *
         * @param is
         * @return
         */
        public InputStreamBuilder source(InputStream is) {
            this.source = is;
            return this;
        }

        /**
         * Add a base64 decoder to chain of input streams.
         *
         * @return
         * @see Base64#getDecoder()
         */
        public InputStreamBuilder b64Decode() {
            final Function<InputStream, InputStream> f = (InputStream is)
                    -> Base64.getDecoder().wrap(is);
            functionList.add(f);
            return this;
        }

        /**
         * Add a mime decoder to the chain of input streams.
         *
         * @return
         * @see Base64#getMimeDecoder()
         */
        public InputStreamBuilder mimeDecode() {
            final Function<InputStream, InputStream> f = (InputStream is)
                    -> Base64.getMimeDecoder().wrap(is);
            functionList.add(f);
            return this;
        }

        /**
         * Add a gzipper to the chain of input streams.
         *
         * @return
         * @see GZIPInputStream#GZIPInputStream(java.io.InputStream) 
         */
        public InputStreamBuilder gunzip() {
            final Function<InputStream, InputStream> f = (InputStream is) -> {
                try {
                    return new GZIPInputStream(is);
                } catch (IOException ex) {
                    throw new IOStreamsException("gunzip", ex);
                }
            };
            functionList.add(f);
            return this;
        }

        /**
         * Add an inflater to the chain of input streams.
         *
         * @return
         * @see InflaterInputStream#InflaterInputStream(java.io.InputStream) 
         */
        public InputStreamBuilder inflate() {
            final Function<InputStream, InputStream> f = (InputStream is) -> {
                return new InflaterInputStream(is);
            };
            functionList.add(f);
            return this;
        }

        public InputStream build() {
            final InputStream inputStreamSource = this.source;
            InputStream inputStreamApplying = inputStreamSource;

            for (int i = 0; i < functionList.size(); i++) {
                final Function<InputStream, InputStream> f = functionList.get(i);
                inputStreamApplying = f.apply(inputStreamApplying);
            }
            return inputStreamApplying;
        }
    }

}
