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

import java.util.List;
import java.util.Optional;

/**
 * A container of synonyms
 */
public class SynonymGroup {
    private final int id;
    private final List<Synonym> synonyms;

    /**
     * Constructs a new group with the specified synonym group ID and the list of
     * synonyms.
     * 
     * @param id
     *            synonym group ID
     * @param synonyms
     *            a list of synonyms
     */
    public SynonymGroup(int id, List<Synonym> synonyms) {
        this.id = id;
        this.synonyms = synonyms;
    }

    /**
     * Returns the ID of this group.
     * 
     * @return the ID of this group
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the list of synonyms in this group.
     * 
     * @return the list of synonyms in this group.
     */
    public List<Synonym> getSynonyms() {
        return synonyms;
    }

    /**
     * Returns a synonym from this group with the specified headword.
     * 
     * @param word
     *            a headword
     * @return an Optional describing the synonym with the specified headword, or an
     *         empty Optional if a synonym is not found
     */
    public Optional<Synonym> lookup(String word) {
        return synonyms.stream().filter(s -> s.getHeadword().equals(word)).findFirst();
    }
}