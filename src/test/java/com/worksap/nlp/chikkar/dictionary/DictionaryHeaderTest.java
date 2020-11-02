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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

public class DictionaryHeaderTest {

    DictionaryHeader header;

    @Before
    public void setUp() throws IOException {
        ByteBuffer bytes = DictionaryReader.read("/system.dic");
        header = new DictionaryHeader(bytes, 0);
    }

    @Test
    public void getVersion() {
        assertThat(header.getVersion(), is(DictionaryVersion.SYSTEM_DICT_VERSION_1));
    }

    @Test
    public void getCreateTime() {
        assertThat(header.getCreateTime(), greaterThan(0L));
    }

    @Test
    public void getDescription() {
        assertThat(header.getDescription(), is("the system dictionary for the unit tests"));
    }

    @Test
    public void isDictionary() {
        assertThat(header.isDictionary(), is(true));
    }
}
