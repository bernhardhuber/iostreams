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

    public static class OutputStreamBuilder {

        private final List<Function<OutputStream, OutputStream>> functionList = new ArrayList<>();
        private OutputStream sink;

        public OutputStreamBuilder sink(OutputStream os) {
            this.sink = os;
            return this;
        }

        public OutputStreamBuilder b64Encode() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> Base64.getEncoder().wrap(os);
            functionList.add(f);
            return this;
        }

        public OutputStreamBuilder mimeEncode() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> Base64.getMimeEncoder().wrap(os);
            functionList.add(f);
            return this;
        }

        public OutputStreamBuilder gzip() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> {
                try {
                    return new GZIPOutputStream(os);
                } catch (IOException ex) {
                    throw new StreamsException("gzip", ex);
                }
            };
            functionList.add(f);
            return this;
        }

        public OutputStreamBuilder deflate() {
            final Function<OutputStream, OutputStream> f = (OutputStream os) -> {
                return new DeflaterOutputStream(os);
            };
            functionList.add(f);
            return this;
        }

        public OutputStream build() {
            final OutputStream outputStream = this.sink;
            OutputStream outputStreamApplying = outputStream;

            for (int i = 0; i < functionList.size(); i++) {
                Function<OutputStream, OutputStream> f = functionList.get(i);
                outputStreamApplying = f.apply(outputStreamApplying);
            }
            return outputStreamApplying;
        }
    }

    public static class InputStreamBuilder {

        private final List<Function<InputStream, InputStream>> functionList = new ArrayList<>();
        private InputStream source;

        public InputStreamBuilder source(InputStream is) {
            this.source = is;
            return this;
        }

        public InputStreamBuilder b64Decode() {
            final Function<InputStream, InputStream> f = (InputStream is)
                    -> Base64.getDecoder().wrap(is);
            functionList.add(f);
            return this;
        }

        public InputStreamBuilder mimeDecode() {
            final Function<InputStream, InputStream> f = (InputStream is)
                    -> Base64.getMimeDecoder().wrap(is);
            functionList.add(f);
            return this;
        }

        public InputStreamBuilder gunzip() {
            final Function<InputStream, InputStream> f = (InputStream is) -> {
                try {
                    return new GZIPInputStream(is);
                } catch (IOException ex) {
                    throw new StreamsException("gunzip", ex);
                }
            };
            functionList.add(f);
            return this;
        }

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
