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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.worksap.nlp.chikkar.dictionary.Dictionary;

public class Chikkar {
    private List<Dictionary> dictionaries = new ArrayList<>();
    private boolean enableVerb = false;

    public void enableVerb() {
        enableVerb = true;
    }

    public void addDictionary(Dictionary dictionary) {
        dictionaries.add(0, dictionary);
    }

    public List<String> find(String word) {
        return find(word, null);
    }

    public List<String> find(String word, int[] groupIds) {
        for (Dictionary dictionary : dictionaries) {
            int[] gids = dictionary.lookup(word, groupIds);
            if (gids.length == 0) {
                continue;
            }
            List<String> ret = new ArrayList<>();
            for (int gid : gids) {
                gatherHeadword(word, gid, dictionary, ret);
            }
            return ret;
        }
        return Collections.emptyList();
    }

    private void gatherHeadword(String word, int gid, Dictionary dictionary, List<String> headwords) {
        Optional<SynonymGroup> synonyms = dictionary.getSynonymGroup(gid);
        if (synonyms.isEmpty()) {
            return;
        }
        if (synonyms.get().lookup(word).orElseThrow(IllegalStateException::new).hasAmbiguity()) {
            return;
        }
        for (Synonym s : synonyms.get().getSynonyms()) {
            if (s.getHeadword().equals(word)) {
                continue;
            }
            if (!enableVerb && !s.isNoun()) {
                continue;
            }
            headwords.add(s.getHeadword());
        }
    }
}
