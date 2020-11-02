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

import java.nio.ByteBuffer;

class IdTable {

    private final ByteBuffer bytes;
    private final int size;
    private final int offset;

    IdTable(ByteBuffer bytes, int offset) {
        this.bytes = bytes;
        size = bytes.getInt(offset);
        this.offset = offset + 4;
    }

    int storageSize() {
        return 4 + size;
    }

    int[] get(int index) {
        int length = Byte.toUnsignedInt(bytes.get(offset + index++));
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = bytes.getInt(offset + index);
            index += 4;
        }
        return result;
    }
}
