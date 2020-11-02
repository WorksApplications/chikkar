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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import java.io.IOException;
import java.nio.file.Paths;

import com.worksap.nlp.chikkar.dictionary.Dictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ChikkarTest {

    Chikkar chikkar;
    Dictionary systemDict;
    Dictionary userDict;
    Dictionary user2Dict;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        Utils.copyResource(temporaryFolder.getRoot().toPath(), "/system.dic", "/user.dic", "/user2.dic");

        systemDict = new Dictionary(Paths.get(temporaryFolder.getRoot().getPath(), "system.dic").toString(), false);
        userDict = new Dictionary(Paths.get(temporaryFolder.getRoot().getPath(), "user.dic").toString(), true);
        user2Dict = new Dictionary(Paths.get(temporaryFolder.getRoot().getPath(), "user2.dic").toString(), true);

        chikkar = new Chikkar();
        chikkar.addDictionary(systemDict);
    }

    @After
    public void tearDown() throws IOException {
        if (systemDict != null) {
            systemDict.close();
        }
        if (userDict != null) {
            userDict.close();
        }
        if (user2Dict != null) {
            user2Dict.close();
        }
    }

    @Test
    public void find() {
        assertThat(chikkar.find("開店"), containsInAnyOrder("始業", "営業開始", "店開き", "オープン", "open"));
        assertThat(chikkar.find("オープン"), empty());
        assertThat(chikkar.find("nothing"), empty());
    }

    @Test
    public void findWithGid() {
        int[] gid = new int[] { 6 };
        assertThat(chikkar.find("開店", gid), containsInAnyOrder("始業", "営業開始", "店開き", "オープン", "open"));
        assertThat(chikkar.find("オープン", gid), empty());
        assertThat(chikkar.find("nothing", new int[0]), empty());
    }

    @Test(expected = IllegalStateException.class)
    public void findOovWithGid() {
        assertThat(chikkar.find("nothing", new int[] { 6 }), empty());
    }

    @Test
    public void findWithUserDict() {
        chikkar.addDictionary(userDict);
        assertThat(chikkar.find("open"), containsInAnyOrder("開放", "オープン"));
        chikkar.addDictionary(user2Dict);
        assertThat(chikkar.find("open"), empty());
    }

    @Test
    public void enableVerb() {
        chikkar.addDictionary(userDict);
        chikkar.enableVerb();
        assertThat(chikkar.find("open"), containsInAnyOrder("開放", "開け放す", "開く", "オープン"));
    }
}
