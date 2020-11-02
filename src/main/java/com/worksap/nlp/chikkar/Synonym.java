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

import com.worksap.nlp.chikkar.dictionary.Flags;

public class Synonym {
    public static final int NONE = 0;

    public static final int FORM_TRANSLATION = 1;
    public static final int FORM_ALIAS = 2;
    public static final int FORM_OLD_NAME = 3;
    public static final int FORM_MISNOMER = 4;

    public static final int ACRONYM_ALPHABET = 1;
    public static final int ACRONYM_OTHERS = 2;

    public static final int VARIANT_ALPHABET = 1;
    public static final int VARIANT_GENERAL = 2;
    public static final int VARIANT_MISSPELLED = 3;

    private final String headword;
    private final int[] lexemeIds;
    private final Flags flags;
    private final String category;

    public Synonym(String headword, int[] lexemeIds, Flags flags, String category) {
        this.headword = headword;
        this.lexemeIds = lexemeIds;
        this.flags = flags;
        this.category = category;
    }

    public String getHeadword() {
        return headword;
    }

    public boolean hasAmbiguity() {
        return flags.hasAmbiguity();
    }

    public int[] getLexemeIds() {
        return lexemeIds;
    }

    public boolean isNoun() {
        return flags.isNoun();
    }

    public int getFormType() {
        return flags.formType();
    }

    public int getAcronymType() {
        return flags.acronymType();
    }

    public int getVariantType() {
        return flags.variantType();
    }

    public String getCategory() {
        return category;
    }
}
