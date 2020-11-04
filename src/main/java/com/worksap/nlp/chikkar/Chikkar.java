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

/**
 * A container of synonym dictionaries.
 */
public class Chikkar {
    private List<Dictionary> dictionaries = new ArrayList<>();
    private boolean enableVerb = false;

    /**
     * Enable verb and adjective synonyms.
     * 
     * After this method is called, {@link find} searches for synonyms for verbs and
     * adjectives.
     */
    public void enableVerb() {
        enableVerb = true;
    }

    /**
     * Add a synonym dictionary.
     * 
     * Adds a dictionary to be used for search. When searching, the dictionary added
     * later takes precedence.
     * 
     * @param dictionary
     *            a synonym dictionary
     */
    public void addDictionary(Dictionary dictionary) {
        dictionaries.add(0, dictionary);
    }

    /**
     * Returns synonyms for the specified word.
     * 
     * If {@link enableVerb} is not called, only noun synonyms are returned.
     * 
     * @param word
     *            keyword
     * @return a list of synonyms
     */
    public List<String> find(String word) {
        return find(word, null);
    }

    /**
     * Returns synonyms for the specified word.
     * 
     * If the tries in the dictionaries are enabled and {@code groupIds} is not
     * {@code null}, use the synonym group IDs as keys. Otherwise, use {@code word}
     * as a key.
     *
     * If {@link enableVerb} is not called, only noun synonyms are returned.
     * 
     * @param word
     *            keyword
     * @param groupIds
     *            synonym group IDs
     * @return a list of synonyms
     */
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
        if (!synonyms.isPresent()) {
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
