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

/**
 * Versions of dictionaries.
 */
public class DictionaryVersion {

    private DictionaryVersion() {
    }

    /** the first version of system dictionries */
    public static final long SYSTEM_DICT_VERSION_1 = 0xeb5b87cc8b3f406cL;

    public static boolean isDictionary(long version) {
        return version == SYSTEM_DICT_VERSION_1;
    }
}
