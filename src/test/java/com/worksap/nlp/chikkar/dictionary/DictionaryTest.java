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
import java.nio.file.Paths;
import java.util.Optional;

import com.worksap.nlp.chikkar.SynonymGroup;
import com.worksap.nlp.chikkar.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DictionaryTest {

    Dictionary dict;
    Dictionary dictGid;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        Utils.copyResource(temporaryFolder.getRoot().toPath(), "/system.dic");

        String path = Paths.get(temporaryFolder.getRoot().getPath(), "system.dic").toString();
        dict = new Dictionary(path, true);
        dictGid = new Dictionary(path, false);
    }

    @After
    public void tearDown() throws IOException {
        if (dict != null) {
            dict.close();
        }
        if (dictGid != null) {
            dictGid.close();
        }
    }

    @Test
    public void lookup() {
        assertThat(dict.lookup("open", null), is(new int[] { 6, 100006 }));
        assertThat(dict.lookup("open", new int[] { 4 }), is(new int[] { 6, 100006 }));

        assertThat(dictGid.lookup("open", null), is(new int[] { 6, 100006 }));
        assertThat(dictGid.lookup("open", new int[] { 4 }), is(new int[] { 4 }));
    }

    @Test
    public void getSynonyms() {
        Optional<SynonymGroup> r = dict.getSynonymGroup(6);
        assertThat(r.isPresent(), is(true));
        SynonymGroup group = r.get();
        assertThat(group.getId(), is(6));

        r = dict.getSynonymGroup(200);
        assertThat(r.isPresent(), is(false));
    }

    @Test
    public void getDictionaryHeader() {
        assertThat(dict.getDictionaryHeader().isDictionary(), is(true));
    }
}
