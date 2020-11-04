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

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.worksap.nlp.dartsclone.DoubleArray;

public class DictionaryBuilder {

    static final int BUFFER_SIZE = 1024 * 1024;

    static class SynonymEntry {
        String headword;
        int groupId;
        int[] lexemeIds;
        Flags flags;
        String category;
    }

    SortedMap<byte[], List<Integer>> trieKeys = new TreeMap<>((byte[] l, byte[] r) -> {
        int llen = l.length;
        int rlen = r.length;
        for (int i = 0; i < Math.min(llen, rlen); i++) {
            if (l[i] != r[i]) {
                return (l[i] & 0xff) - (r[i] & 0xff);
            }
        }
        return l.length - r.length;
    });
    List<List<SynonymEntry>> synonymGroups = new ArrayList<>();

    protected Logger logger;

    ByteBuffer byteBuffer;
    Buffer buffer;

    DictionaryBuilder() {
        logger = Logger.getLogger(this.getClass().getName());
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer = byteBuffer; // a kludge for Java 9
    }

    void build(String inputPath, FileOutputStream output) throws IOException {
        int lineno = -1;
        try (FileInputStream input = new FileInputStream(inputPath);
                InputStreamReader isr = new InputStreamReader(input);
                LineNumberReader reader = new LineNumberReader(isr)) {
            List<SynonymEntry> block = new ArrayList<>();
            String line;
            int groupId = -1;
            while ((line = reader.readLine()) != null) {
                lineno = reader.getLineNumber();
                if (line.codePoints().allMatch(Character::isWhitespace)) {
                    if (block.isEmpty()) {
                        continue;
                    } else {
                        synonymGroups.add(block);
                        block = new ArrayList<>();
                        groupId = -1;
                    }
                } else {
                    SynonymEntry entry = parseLine(line);
                    if (entry == null) {
                        continue;
                    }
                    if (groupId < 0) {
                        groupId = entry.groupId;
                    } else if (groupId != entry.groupId) {
                        throw new IllegalArgumentException("group ID is changed in block");
                    }
                    trieKeys.computeIfAbsent(entry.headword.getBytes(StandardCharsets.UTF_8), k -> new ArrayList<>())
                            .add(entry.groupId);
                    block.add(entry);
                }
            }
            if (!block.isEmpty()) {
                synonymGroups.add(block);
            }
        } catch (Exception e) {
            if (lineno > 0) {
                logger.severe("Error: " + e.getMessage() + " at line " + lineno + " in " + inputPath + "\n");
            }
            throw e;
        }

        FileChannel outputChannel = output.getChannel();
        writeTrie(outputChannel);
        writeSynonymGroups(outputChannel);
        outputChannel.close();
    }

    SynonymEntry parseLine(String line) {
        String[] cols = line.split(",");
        if (cols.length < 9) {
            throw new IllegalArgumentException("invalid format");
        }
        if (cols[2].equals("2")) {
            return null;
        }

        SynonymEntry entry = new SynonymEntry();
        entry.groupId = Integer.parseInt(cols[0]);
        entry.lexemeIds = (cols[3].equals("")) ? new int[0]
                : Stream.of(cols[3].split("/")).mapToInt(Integer::parseInt).toArray();
        entry.headword = cols[8];
        boolean hasAmbiguity = parseBoolean(cols[2], "0", "1");
        boolean isNoun = parseBoolean(cols[1], "2", "1");
        int formType = parseInt(cols[4], 4);
        int acronymType = parseInt(cols[5], 2);
        int variantType = parseInt(cols[6], 3);
        entry.flags = new Flags(hasAmbiguity, isNoun, formType, acronymType, variantType);
        entry.category = cols[7];

        return entry;
    }

    void writeTrie(FileChannel output) throws IOException {
        DoubleArray trie = new DoubleArray();

        int size = trieKeys.size();

        byte[][] keys = new byte[size][];
        int[] values = new int[size];
        ByteBuffer idTable = ByteBuffer.allocate(trieKeys.size() * (1 + 4 * 256));
        idTable.order(ByteOrder.LITTLE_ENDIAN);

        int i = 0;
        for (Entry<byte[], List<Integer>> entry : trieKeys.entrySet()) {
            keys[i] = entry.getKey();
            values[i] = idTable.position();
            i++;
            List<Integer> ids = entry.getValue();
            idTable.put((byte) ids.size());
            for (int id : ids) {
                idTable.putInt(id);
            }
        }

        logger.info("building the trie");
        trie.build(keys, values, (n, s) -> {
            if (n % ((s / 10) + 1) == 0) {
                logger.info(".");
            }
        });
        logger.info("done\n");

        logger.info("writing the trie...");
        buffer.clear();
        byteBuffer.putInt(trie.size());
        buffer.flip();
        output.write(byteBuffer);
        buffer.clear();

        output.write(trie.byteArray());
        printSize(trie.size() * 4 + 4L);
        trie = null;

        logger.info("writing the word-ID table...");
        byteBuffer.putInt(idTable.position());
        buffer.flip();

        output.write(byteBuffer);
        buffer.clear();

        ((Buffer) idTable).flip(); // a kludge for Java 9
        output.write(idTable);
        printSize(idTable.position() + 4L);
        idTable = null;
    }

    void writeSynonymGroups(FileChannel output) throws IOException {
        long mark = output.position();
        output.position(mark + 4 * synonymGroups.size() * 2 + 4);

        ByteBuffer offsets = ByteBuffer.allocate(4 * synonymGroups.size() * 2 + 4);
        offsets.order(ByteOrder.LITTLE_ENDIAN);
        offsets.putInt(synonymGroups.size());

        logger.info("writing the synonym groups...");
        buffer.clear();
        long base = output.position();
        for (List<SynonymEntry> entries : synonymGroups) {
            if (entries.isEmpty()) {
                continue;
            }
            offsets.putInt(entries.get(0).groupId);
            offsets.putInt((int) output.position());

            byteBuffer.putShort((short) entries.size());
            for (SynonymEntry entry : entries) {
                writeString(entry.headword);
                writeShortArray(entry.lexemeIds);
                byteBuffer.putShort(entry.flags.encode());
                writeString(entry.category);

            }
            buffer.flip();
            output.write(byteBuffer);
            buffer.clear();
        }
        printSize(output.position() - base);

        logger.info("writing synonym groups offsets...");
        output.position(mark);
        ((Buffer) offsets).flip(); // a kludge for Java 9
        output.write(offsets);
        printSize(offsets.position());
    }

    boolean parseBoolean(String s, String falseString, String trueString) {
        if (s.equals(falseString)) {
            return false;
        } else if (s.equals(trueString)) {
            return true;
        } else {
            throw new IllegalArgumentException("invalid value: " + s);
        }
    }

    int parseInt(String s, int limit) {
        int v = Integer.parseInt(s);
        if (v < 0 || v > limit) {
            throw new IllegalArgumentException("invalid value: " + s);
        }
        return v;
    }

    void writeString(String text) {
        writeStringLength((short) text.length());
        for (int i = 0; i < text.length(); i++) {
            byteBuffer.putChar(text.charAt(i));
        }
    }

    void writeShortArray(int[] array) {
        byteBuffer.put((byte) array.length);
        for (int i : array) {
            byteBuffer.putShort((short) i);
        }
    }

    void writeStringLength(short length) {
        if (length <= Byte.MAX_VALUE) {
            byteBuffer.put((byte) length);
        } else {
            byteBuffer.put((byte) ((length >> 8) | 0x80));
            byteBuffer.put((byte) (length & 0xFF));
        }
    }

    void printSize(long size) {
        logger.info(() -> String.format(" %,d bytes%n", size));
    }

    static void printUsage() {
        Console console = System.console();
        console.printf("usage: DictionaryBuilder -o file [-d description] input\n");
        console.printf("\t-d description\tcomment\n");
    }

    static void readLoggerConfig() throws IOException {
        InputStream is = DictionaryBuilder.class.getResourceAsStream("/logger.properties");
        if (is != null) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }

    public static void main(String[] args) throws IOException {
        readLoggerConfig();

        String description = "";
        String outputPath = null;
        int i = 0;
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("-o") && i + 1 < args.length) {
                outputPath = args[++i];
            } else if (args[i].equals("-d") && i + 1 < args.length) {
                description = args[++i];
            } else if (args[i].equals("-h")) {
                printUsage();
                return;
            } else {
                break;
            }
        }
        if (args.length <= i || outputPath == null) {
            printUsage();
            return;
        }
        String inputPath = args[i];

        DictionaryHeader header = new DictionaryHeader(DictionaryVersion.SYSTEM_DICT_VERSION_1,
                Instant.now().getEpochSecond(), description);
        try (FileOutputStream output = new FileOutputStream(outputPath)) {
            output.write(header.toByte());

            DictionaryBuilder builder = new DictionaryBuilder();
            builder.build(inputPath, output);
        }
    }
}
