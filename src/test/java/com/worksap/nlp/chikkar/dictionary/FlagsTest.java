/*
 * Copyright (c) 2020 Works Applications Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worksap.nlp.chikkar.dictionary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class FlagsTest {
    @Test
    public void allZero() {
        Flags flags = new Flags(false, false, 0, 0, 0);
        short code = flags.encode();
        Flags newFlags = new Flags(code);
        assertThat(newFlags.hasAmbiguity(), is(false));
        assertThat(newFlags.isNoun(), is(false));
        assertThat(newFlags.formType(), is(0));
        assertThat(newFlags.acronymType(), is(0));
        assertThat(newFlags.variantType(), is(0));
    }

    @Test
    public void max() {
        Flags flags = new Flags(true, true, 4, 2, 3);
        short code = flags.encode();
        Flags newFlags = new Flags(code);
        assertThat(newFlags.hasAmbiguity(), is(true));
        assertThat(newFlags.isNoun(), is(true));
        assertThat(newFlags.formType(), is(4));
        assertThat(newFlags.acronymType(), is(2));
        assertThat(newFlags.variantType(), is(3));
    }
}
