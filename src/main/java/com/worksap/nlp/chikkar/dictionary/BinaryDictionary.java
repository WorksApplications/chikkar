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

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BinaryDictionary implements Closeable {

    ByteBuffer bytes;
    DictionaryHeader header;
    DoubleArrayTrie trie;
    int offset;

    BinaryDictionary(String fileName) throws IOException {
        bytes = MMap.map(fileName);
        offset = 0;

        header = new DictionaryHeader(bytes, offset);
        offset += header.storageSize();

        if (!header.isDictionary()) {
            MMap.unmap(bytes);
            throw new IOException("invalid dictionary");
        }

        trie = new DoubleArrayTrie(bytes, offset);
        offset += trie.storageSize();
    }

    public static BinaryDictionary readSystemDictionary(String fileName) throws IOException {
        BinaryDictionary dict = new BinaryDictionary(fileName);
        if (!dict.getDictionaryHeader().isDictionary()) {
            dict.close();
            throw new IOException("invalid system dictionary");
        }
        return dict;
    }

    @Override
    public void close() throws IOException {
        MMap.unmap(bytes);
    }

    public DictionaryHeader getDictionaryHeader() {
        return header;
    }
}