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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DoubleArrayTrieTest {

    static final byte[] OPEN = "open".getBytes(StandardCharsets.UTF_8);
    DoubleArrayTrie trie;

    @Before
    public void setUp() throws IOException {
        ByteBuffer bytes = DictionaryReader.read("/system.dic");
        DictionaryHeader header = new DictionaryHeader(bytes, 0);
        trie = new DoubleArrayTrie(bytes, header.storageSize());
    }

    @Test
    public void commonPrefixSearch() {
        List<int[]> r = iteratorToList(trie.lookup(OPEN, 0));
        assertThat(r.size(), is(2));
        assertThat(r.get(0)[0], is(6));
        assertThat(r.get(0)[1], is(4));
        assertThat(r.get(1)[0], is(100006));
        assertThat(r.get(1)[1], is(4));
    }

    @Test
    public void exactMatch() {
        assertThat(trie.lookup(OPEN), is(new int[] { 6, 100006 }));
        assertThat(trie.lookup("nothing".getBytes(StandardCharsets.UTF_8)).length, is(0));
    }

    @Test
    public void storageSize() {
        assertThat(trie.storageSize(), is(1095));
    }

    static <E> List<E> iteratorToList(Iterator<E> iterator) {
        List<E> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
}
