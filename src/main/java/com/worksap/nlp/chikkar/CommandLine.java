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

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.worksap.nlp.chikkar.dictionary.Dictionary;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;

/**
 * A command-line morphological analysis tool.
 */
public class CommandLine {

    static Logger logger;

    static class FileOrStdoutPrintStream extends PrintStream {

        private boolean isFile;

        FileOrStdoutPrintStream(String fileName) throws FileNotFoundException {
            super(fileName == null ? System.out : new FileOutputStream(fileName), fileName == null);
            isFile = fileName != null;
        }

        @Override
        public void close() {
            if (isFile) {
                super.close();
            } else {
                flush();
            }
        }
    }

    static void run(Tokenizer tokenizer, Tokenizer.SplitMode mode, Chikkar chikkar, InputStream input,
            PrintStream output, boolean ignoreError) throws IOException {

        try (InputStreamReader inputReader = new InputStreamReader(input);
                BufferedReader reader = new BufferedReader(inputReader)) {

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                try {
                    for (List<Morpheme> sentence : tokenizer.tokenizeSentences(mode, line)) {
                        for (Morpheme m : sentence) {
                            output.print(m.surface());
                            output.print("\t");
                            output.print(m.normalizedForm());
                            output.print("\t");
                            output.print(String.join("\t", chikkar.find(m.normalizedForm(), m.getSynonymGroupIds())));
                            output.println();
                        }
                        output.println("EOS");
                    }
                } catch (RuntimeException e) {
                    if (ignoreError) {
                        logger.warning(e.getMessage() + "\n");
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    static String readAll(InputStream input) throws IOException {
        try (InputStreamReader isReader = new InputStreamReader(input, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isReader)) {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
            return sb.toString();
        }
    }

    /**
     * Analyzes the input texts.
     *
     * <p>
     * Usage:
     * {@code SudachiCommandLine [-r file] [-m A|B|C] [-o file] [-d] [file ...]}
     * <p>
     * The following are the options.
     * <dl>
     * <dt>{@code -r file}</dt>
     * <dd>the settings file in JSON format (overrides -s)</dd>
     * <dt>{@code -s string}</dt>
     * <dd>an additional settings string in JSON format (overrides -r)</dd>
     * <dt>{@code -m {A|B|C}}</dt>
     * <dd>the mode of splitting</dd>
     * <dt>{@code -o file}</dt>
     * <dd>the output file</dd>
     * <dt>{@code -d file}</dt>
     * <dd>the system synonym file</dd>
     * <dt>{@code -u file}</dt>
     * <dd>the user synonym file</dd>
     * <dt>{@code -h}</dt>
     * <dd>show the usage</dd>
     * </dl>
     * <p>
     * If the output file is not specified, this tool writes the output to the
     * standard output.
     * <p>
     * The {@code file} operands are processed in command-line order. If
     * {@code file} is absent, this tool reads from the starndard input.
     *
     * <p>
     * This tool processes a line as a sentence.
     *
     * @param args
     *            the options and the input filenames
     * @throws IOException
     *             if IO is failed
     */
    public static void main(String[] args) throws IOException {
        InputStream is = CommandLine.class.getResourceAsStream("/logger.properties");
        if (is != null) {
            LogManager.getLogManager().readConfiguration(is);
        }
        logger = Logger.getLogger(CommandLine.class.getName());

        Tokenizer.SplitMode mode = Tokenizer.SplitMode.C;
        String settings = null;
        boolean mergeSettings = false;
        String resourcesDirectory = null;
        String outputFileName = null;
        boolean ignoreError = false;

        Chikkar chikkar = new Chikkar();

        int i = 0;
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("-r") && i + 1 < args.length) {
                try (FileInputStream input = new FileInputStream(args[++i])) {
                    settings = readAll(input);
                    mergeSettings = false;
                }
            } else if (args[i].equals("-p") && i + 1 < args.length) {
                resourcesDirectory = args[++i];
            } else if (args[i].equals("-s") && i + 1 < args.length) {
                settings = args[++i];
                mergeSettings = true;
            } else if (args[i].equals("-m") && i + 1 < args.length) {
                switch (args[++i]) {
                case "A":
                    mode = Tokenizer.SplitMode.A;
                    break;
                case "B":
                    mode = Tokenizer.SplitMode.B;
                    break;
                default:
                    mode = Tokenizer.SplitMode.C;
                    break;
                }
            } else if (args[i].equals("-o") && i + 1 < args.length) {
                outputFileName = args[++i];
            } else if (args[i].equals("-d") && i + 1 < args.length) {
                chikkar.addDictionary(new Dictionary(args[++i], false));
            } else if (args[i].equals("-u") && i + 1 < args.length) {
                chikkar.addDictionary(new Dictionary(args[++i], true));
            } else if (args[i].equals("-v")) {
                chikkar.enableVerb();
            } else if (args[i].equals("-f")) {
                ignoreError = true;
            } else if (args[i].equals("-h")) {
                Console console = System.console();
                console.printf("usage: CommandLine [-r file] [-m A|B|C] [-o file] [file ...]\n");
                console.printf("\t-r file\tread settings from file (overrides -s)\n");
                console.printf("\t-s string\tadditional settings (overrides -r)\n");
                console.printf("\t-p directory\troot directory of resources\n");
                console.printf("\t-m mode\tmode of splitting\n");
                console.printf("\t-o file\toutput to file\n");
                console.printf("\t-d file\tsystem synonym dictionary\n");
                console.printf("\t-u file\tuser synonym dictionary\n");
                console.printf("\t-f\tignore error\n");
                return;
            } else {
                break;
            }
        }

        try (PrintStream output = new FileOrStdoutPrintStream(outputFileName);
                com.worksap.nlp.sudachi.Dictionary dict = new com.worksap.nlp.sudachi.DictionaryFactory()
                        .create(resourcesDirectory, settings, mergeSettings)) {
            Tokenizer tokenizer = dict.create();
            if (i < args.length) {
                for (; i < args.length; i++) {
                    try (FileInputStream input = new FileInputStream(args[i])) {
                        run(tokenizer, mode, chikkar, input, output, ignoreError);
                    }
                }
            } else {
                run(tokenizer, mode, chikkar, System.in, output, ignoreError);
            }
        }
    }
}
