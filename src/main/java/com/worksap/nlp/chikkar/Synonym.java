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

/**
 * A synonym
 */
public class Synonym {
    /** Typical form */
    public static final int NONE = 0;

    /** Translated from another language */
    public static final int FORM_TRANSLATION = 1;
    /** Alias or common name */
    public static final int FORM_ALIAS = 2;
    /** Old name */
    public static final int FORM_OLD_NAME = 3;
    /** Misused words */
    public static final int FORM_MISNOMER = 4;

    /** Abbreviations written in Latin letters */
    public static final int ACRONYM_ALPHABET = 1;
    /** Abbreviations written outside the Latin alphabet */
    public static final int ACRONYM_OTHERS = 2;

    /** Original spelling of foreign words or romanization of Japanese words */
    public static final int VARIANT_ALPHABET = 1;
    /** Variant notation */
    public static final int VARIANT_GENERAL = 2;
    /** Misspelled words */
    public static final int VARIANT_MISSPELLED = 3;

    private final String headword;
    private final int[] lexemeIds;
    private final Flags flags;
    private final String category;

    /**
     * Construct a new synonym with the specified parameter.
     *
     * @param headword
     *            a notation string
     * @param lexemeIds
     *            a ID of lexeme in the synonym group
     * @param flags
     *            encoded flags
     * @param category
     *            category Information of the synonym
     */
    public Synonym(String headword, int[] lexemeIds, Flags flags, String category) {
        this.headword = headword;
        this.lexemeIds = lexemeIds;
        this.flags = flags;
        this.category = category;
    }

    /**
     * Returns the notaion of this synonym.
     * 
     * @return the notation of this synonym
     */
    public String getHeadword() {
        return headword;
    }

    /**
     * Returns {@code true} if and only if this synonym has ambiguity.
     * 
     * @return {@code true} if this synonym is ambiguous, {@code false} otherwise
     */
    public boolean hasAmbiguity() {
        return flags.hasAmbiguity();
    }

    /**
     * Returns the IDs of the lexemes that corresponds to this synonym.
     * 
     * @return an array of the IDs of the lexemes of this synonym
     */
    public int[] getLexemeIds() {
        return lexemeIds;
    }

    /**
     * Returns {@code true} if and only if this synonym is a noun;
     * 
     * @return {@code true} if this synonym is a noun, {@code false} otherwise
     */
    public boolean isNoun() {
        return flags.isNoun();
    }

    /**
     * Returns the word form type of this synonym.
     * 
     * @return the word form type of this synonym
     */
    public int getFormType() {
        return flags.formType();
    }

    /**
     * Returns the acronym type of this synonym.
     * 
     * @return the acronym type of this synonym
     */
    public int getAcronymType() {
        return flags.acronymType();
    }

    /**
     * Returns the variant type of this synonym.
     * 
     * @return the variant type of this synonym
     */
    public int getVariantType() {
        return flags.variantType();
    }

    /**
     * Returns the category information of this synonym.
     * 
     * @return the category information of this synonym
     */
    public String getCategory() {
        return category;
    }
}
