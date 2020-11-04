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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.worksap.nlp.chikkar.SynonymGroup;

public class Dictionary extends BinaryDictionary {

    private boolean enableTrie;
    private SynonymGroupList groupList;

    public Dictionary(String fileName, boolean enableTrie) throws IOException {
        super(fileName);
        this.enableTrie = enableTrie;
        groupList = new SynonymGroupList(bytes, offset);
    }

    public int[] lookup(String word, int[] groupIds) {
        if (enableTrie || groupIds == null) {
            return trie.lookup(word.getBytes(StandardCharsets.UTF_8));
        } else {
            return groupIds;
        }
    }

    public Optional<SynonymGroup> getSynonymGroup(int groupId) {
        return groupList.getSynonymGroup(groupId);
    }
}
