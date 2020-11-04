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

package com.worksap.nlp.chikkar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.worksap.nlp.chikkar.dictionary.Flags;

import org.junit.Before;
import org.junit.Test;

public class SynonymGroupTest {

    private SynonymGroup group;

    @Before
    public void setUp() {
        Flags flags = new Flags(false, true, 0, 0, 0);
        Synonym synomymA = new Synonym("aaa", new int[] { 1 }, flags, "");
        Synonym synomymB = new Synonym("bbb", new int[] { 2 }, flags, "");
        group = new SynonymGroup(1, Arrays.asList(synomymA, synomymB));
    }

    @Test
    public void getId() {
        assertThat(group.getId(), is(1));
    }

    @Test
    public void getSynonyms() {
        List<Synonym> synonyms = group.getSynonyms();
        assertThat(synonyms.size(), is(2));
        Synonym s = synonyms.get(0);
        assertThat(s.getHeadword(), is("aaa"));
        assertThat(s.hasAmbiguity(), is(false));
        assertThat(s.isNoun(), is(true));
        assertThat(s.getLexemeIds(), is(new int[] { 1 }));
        assertThat(s.getFormType(), is(0));
        assertThat(s.getAcronymType(), is(0));
        assertThat(s.getVariantType(), is(0));
        s = synonyms.get(1);
        assertThat(s.getHeadword(), is("bbb"));
    }

    @Test
    public void lookup() {
        Optional<Synonym> s = group.lookup("aaa");
        assertThat(s.isPresent(), is(true));
        assertThat(s.get().getHeadword(), is("aaa"));
        s = group.lookup("ccc");
        assertThat(s.isPresent(), is(false));
    }
}
