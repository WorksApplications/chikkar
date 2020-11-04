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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.worksap.nlp.chikkar.Synonym;
import com.worksap.nlp.chikkar.SynonymGroup;

class SynonymGroupList {
    private final ByteBuffer bytes;
    private final int size;
    private final Map<Integer, Integer> groupIdToOffset;

    SynonymGroupList(ByteBuffer bytes, int offset) {
        this.bytes = bytes;

        ByteBuffer buf = bytes.asReadOnlyBuffer();
        buf.order(bytes.order());
        ((Buffer) buf).position(offset);
        this.size = buf.getInt();

        groupIdToOffset = new HashMap<>();
        for (int i = 0; i < this.size; i++) {
            int gid = buf.getInt();
            int off = buf.getInt();
            groupIdToOffset.put(gid, off);
        }
    }

    Optional<SynonymGroup> getSynonymGroup(int gid) {
        Integer offset = groupIdToOffset.get(gid);
        if (offset == null) {
            return Optional.empty();
        }

        ByteBuffer buf = bytes.asReadOnlyBuffer();
        buf.order(bytes.order());
        ((Buffer) buf).position(groupIdToOffset.get(gid));

        List<Synonym> synonyms = new ArrayList<>();
        int n = buf.getShort();
        for (int i = 0; i < n; i++) {
            String headword = bufferToString(buf);
            int[] lexemeIds = bufferToShortArray(buf);
            short flags = buf.getShort();
            String category = bufferToString(buf);
            synonyms.add(new Synonym(headword, lexemeIds, new Flags(flags), category));
        }
        return Optional.of(new SynonymGroup(gid, synonyms));
    }

    private int bufferToStringLength(ByteBuffer buffer) {
        byte length = buffer.get();
        if (length < 0) {
            int high = Byte.toUnsignedInt(length);
            int low = Byte.toUnsignedInt(buffer.get());
            return ((high & 0x7F) << 8) | low;
        }
        return length;
    }

    private String bufferToString(ByteBuffer buffer) {
        int length = bufferToStringLength(buffer);
        char[] str = new char[length];
        for (int i = 0; i < length; i++) {
            str[i] = buffer.getChar();
        }
        return new String(str);
    }

    private int[] bufferToShortArray(ByteBuffer buffer) {
        int length = Byte.toUnsignedInt(buffer.get());
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = buffer.getShort();
        }
        return array;
    }
}
