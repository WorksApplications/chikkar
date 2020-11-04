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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

import com.worksap.nlp.dartsclone.DoubleArray;

public class DoubleArrayTrie {

    private DoubleArray trie;
    private IdTable groupIdTable;
    private int storageSize;

    public DoubleArrayTrie(ByteBuffer bytes, int offset) {
        int position = offset;
        trie = new DoubleArray();
        int size = bytes.getInt(position);
        position += 4;
        ((Buffer) bytes).position(position); // a kludge for Java 9
        IntBuffer array = bytes.asIntBuffer();
        trie.setArray(array, size);
        position += trie.totalSize();

        groupIdTable = new IdTable(bytes, position);
        position += groupIdTable.storageSize();
        storageSize = position - offset;
    }

    /**
     * Returns the group IDs obtained by common prefix search.
     *
     * <p>
     * The search begin with the position at the {@code offset} of the {@code text}.
     *
     * <p>
     * The return value is consist of the group ID and the length of the matched
     * part.
     * 
     * @param text
     *            the key
     * @param offset
     *            the offset of the key
     * @return the iterator of results
     */
    public Iterator<int[]> lookup(byte[] text, int offset) {
        Iterator<int[]> iterator = trie.commonPrefixSearch(text, offset);
        if (!iterator.hasNext()) {
            return iterator;
        }
        return new Itr(iterator);
    }

    private class Itr implements Iterator<int[]> {
        private final Iterator<int[]> iterator;
        private int[] groupIds;
        private int length;
        private int index;

        Itr(Iterator<int[]> iterator) {
            this.iterator = iterator;
            index = -1;
        }

        @Override
        public boolean hasNext() {
            if (index < 0) {
                return iterator.hasNext();
            } else {
                return (index < groupIds.length) || iterator.hasNext();
            }
        }

        @Override
        public int[] next() {
            if (index < 0 || index >= groupIds.length) {
                int[] p = iterator.next();
                groupIds = groupIdTable.get(p[0]);
                length = p[1];
                index = 0;
            }
            return new int[] { groupIds[index++], length };
        }
    }

    /**
     * Returns the group IDs obtained by exact match search.
     *
     * <p>
     * The return value is an array of the group IDs.
     * 
     * @param text
     *            the key
     * @return the array of results
     */
    public int[] lookup(byte[] text) {
        int[] r = trie.exactMatchSearch(text);
        if (r[0] < 0) {
            return new int[0];
        } else {
            return groupIdTable.get(r[0]);
        }
    }

    public int storageSize() {
        return storageSize;
    }
}
