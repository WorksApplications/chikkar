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

public class Flags {
    private final boolean hasAmbiguity;
    private final boolean isNoun;
    private final int formType;
    private final int acronymType;
    private final int variantType;

    public Flags(boolean hasAmbiguity, boolean isNoun, int formType, int acronymType, int variantType) {
        this.hasAmbiguity = hasAmbiguity;
        this.isNoun = isNoun;
        this.formType = formType;
        this.acronymType = acronymType;
        this.variantType = variantType;
    }

    Flags(short flags) {
        this.hasAmbiguity = ((flags & 0x0001) == 1);
        this.isNoun = ((flags & 0x0002) == 2);
        this.formType = (flags >> 2) & 0x0007;
        this.acronymType = (flags >> 5) & 0x0003;
        this.variantType = (flags >> 7) & 0x0003;
    }

    public boolean hasAmbiguity() {
        return hasAmbiguity;
    }

    public boolean isNoun() {
        return isNoun;
    }

    public int formType() {
        return formType;
    }

    public int acronymType() {
        return acronymType;
    }

    public int variantType() {
        return variantType;
    }

    short encode() {
        short flags = 0;
        flags |= (hasAmbiguity) ? 1 : 0;
        flags |= ((isNoun) ? 1 : 0) << 1;
        flags |= ((short) formType) << 2;
        flags |= ((short) acronymType) << 5;
        flags |= ((short) variantType) << 7;
        return flags;
    }
}
