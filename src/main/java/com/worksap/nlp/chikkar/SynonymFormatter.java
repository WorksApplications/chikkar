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

import java.io.IOException;

import com.worksap.nlp.chikkar.dictionary.Dictionary;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.SimpleMorphemeFormatter;

public class SynonymFormatter extends SimpleMorphemeFormatter {
    Chikkar chikkar;
    String synonymDelimiter;

    @Override
    public void setUp() throws IOException {
        super.setUp();

        chikkar = new Chikkar();

        String systemDictPath = settings.getString("systemDict");
        if (systemDictPath == null) {
            throw new IllegalArgumentException("no systemDict");
        }
        chikkar.addDictionary(new Dictionary(systemDictPath, false));

        for (String path : settings.getStringList("userDict")) {
            chikkar.addDictionary(new Dictionary(path, false));
        }

        if (settings.getBoolean("enableVerb", false)) {
            chikkar.enableVerb();
        }

        synonymDelimiter = settings.getString("synonymDelimiter", ",");
    }

    @Override
    public String formatMorpheme(Morpheme morpheme) {
        return super.formatMorpheme(morpheme) + columnDelimiter
                + String.join(synonymDelimiter, chikkar.find(morpheme.normalizedForm(), morpheme.getSynonymGroupIds()));
    }
}
