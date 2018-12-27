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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author berni3
 */
public class StreamsBuilder {

    static class OutputStreamBuilder {

        List<Function<OutputStream, OutputStream>> l = new ArrayList<>();
        private OutputStream sink;

        OutputStreamBuilder sink(OutputStream os) {
            this.sink = os;
            return this;
        }

        OutputStreamBuilder b64Encode() {
            Function<OutputStream, OutputStream> f = (OutputStream os) -> Base64.getEncoder().wrap(os);
            l.add(f);
            return this;
        }

        OutputStreamBuilder gzip() {
            Function<OutputStream, OutputStream> f = (OutputStream os) -> {
                try {
                    return new GZIPOutputStream(os);
                } catch (IOException ex) {
                    throw new StreamsException(ex);
                }
            };
            l.add(f);
            return this;
        }

//        public OutputStreamBuilder m(Object o) throws JAXBException {
//            Class classesToBeBound = o.getClass();
//            JAXBContext jaxbContext = JAXBContext.newInstance(classesToBeBound);
//            Function<OutputStream, OutputStream> f = (OutputStream os) -> {
//                Marshaller m = jaxbContext.createMarshaller();
//                m.marshal(o, os );
//            }
//                    l.add(f);
//            return this;
//        }

        OutputStream build() {
            OutputStream os = this.sink;
            OutputStream osl = os;

            for (int i = 0; i < l.size(); i++) {
                Function<OutputStream, OutputStream> f = l.get(i);
                osl = f.apply(osl);
            }
            return osl;
        }
    }

    static class InputStreamBuilder {

        List<Function<InputStream, InputStream>> l = new ArrayList<>();
        private InputStream source;

        InputStreamBuilder source(InputStream is) {
            this.source = is;
            return this;
        }

        InputStreamBuilder b64Decode() {
            Function<InputStream, InputStream> f = (InputStream is)
                    -> Base64.getDecoder().wrap(is);
            l.add(f);
            return this;
        }

        InputStreamBuilder gunzip() {
            Function<InputStream, InputStream> f = (InputStream is) -> {
                try {
                    return new GZIPInputStream(is);
                } catch (IOException ex) {
                    throw new StreamsException(ex);
                }
            };
            l.add(f);
            return this;
        }

        InputStream build() {
            InputStream is = this.source;
            InputStream isl = is;

            for (int i = 0; i < l.size(); i++) {
                Function<InputStream, InputStream> f = l.get(i);
                isl = f.apply(isl);
            }
            return isl;
        }
    }

}
