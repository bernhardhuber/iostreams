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

/**
 * Simple tuple implementation.
 * 
 * @author pi
 */
class Tuple<R, S> {

    private final R r;
    private final S s;

    public Tuple(R r, S s) {
        this.r = r;
        this.s = s;
    }

    R r() {
        return this.r;
    }

    S s() {
        return this.s;
    }

    @Override
    public String toString() {
        return String.format("R %s, S %s", this.r, this.s);
    }

}
