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

public class SynonymGroup {
    private final int id;
    private final List<Synonym> synonyms;

    public SynonymGroup(int id, List<Synonym> synonyms) {
        this.id = id;
        this.synonyms = synonyms;
    }

    public int getId() {
        return id;
    }

    public List<Synonym> getSynonyms() {
        return synonyms;
    }

    public Optional<Synonym> lookup(String word) {
        return synonyms.stream().filter(s -> s.getHeadword().equals(word)).findFirst();
    }
}