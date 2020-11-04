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

/**
 * A container of synonyms
 */
public class Dictionary extends BinaryDictionary {

    private boolean enableTrie;
    private SynonymGroupList groupList;

    /**
     * Reads the synonym dictionary from the specified file.
     * 
     * If {@code enableTrie} is {@code false}, a search by synonym group IDs takes
     * precedence over a search by the headword.
     * 
     * @param fileName
     *            path of synonym dictionary file
     * @param enableTrie
     *            true to enable trie, otherwise false
     * @throws IOException
     *             if reading the file is failed
     */
    public Dictionary(String fileName, boolean enableTrie) throws IOException {
        super(fileName);
        this.enableTrie = enableTrie;
        groupList = new SynonymGroupList(bytes, offset);
    }

    /**
     * Returns a synonym group ID that contains the specified headword or a
     * specified synonym group ID.
     * 
     * If {@code enableTrie} is {@code true} or {@code groupIds} is {@code null},
     * this method searches by the headword. Otherwise, {@code groupIds} is returned
     * as it is.
     * 
     * @param word
     *            a headword to search for
     * @param groupIds
     *            an array of synonym group IDs to search for
     * @return an array of synoym group IDs found, or an empty array if not found
     */
    public int[] lookup(String word, int[] groupIds) {
        if (enableTrie || groupIds == null) {
            return trie.lookup(word.getBytes(StandardCharsets.UTF_8));
        } else {
            return groupIds;
        }
    }

    /**
     * Returns a group of synonyms with the specified ID.
     * 
     * @param groupId
     *            a synonym group ID
     * @return an Optional describing the group of synonyms with the specified ID,
     *         or an empty Optional
     */
    public Optional<SynonymGroup> getSynonymGroup(int groupId) {
        return groupList.getSynonymGroup(groupId);
    }
}
