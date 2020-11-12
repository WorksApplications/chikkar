# chikkar

![build](https://github.com/WorksApplications/chikkar/workflows/build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=WorksApplications_chikkar&metric=alert_status)](https://sonarcloud.io/dashboard?id=WorksApplications_chikkar)

chikkar は [Sudachi 同義語辞書](https://github.com/WorksApplications/SudachiDict/)を利用するためのライブラリです。
Chikkar is a library for using the Sudachi synonym dictionary.

## 利用方法 Ussage

### 辞書の作成 Build a dictionary

利用前にバイナリ形式辞書の作成が必要です。
Before using chikkar, you need to create a binary format dictionary.

```
$ java -cp sudachi-0.5.0.jar:jdartsclone-1.2.0.jar:javax.json-1.1.jar:chikkar-0.1.0.jar -o system_syn.dic synonyms.txt
```

入力ファイルのフォーマットはSudachi 同義語辞書の[マニュアル](https://github.com/WorksApplications/SudachiDict/blob/develop/docs/synonyms.md) を参照してください。
Please refer to the manual of Sudachi Synonyms Dictionary for the format of input files.

### ライブラリの利用　How to use chikkar as a library

以下のようにして同義語の文字列を取得することができます。
You can get a string of synonyms as follows.

```
Chikkar chikkar = new Chikkar();
chikkar.addDictionary(new Dictionary("system_syn.dic", true);

List<String> synonyms = chikkar.find("開店");
```

くわしい利用方法は javadoc を参照してください。
See javadoc for more information on how to use.

### Sudachi コマンドラインツールでの利用 Using chikkar with the Sudachi CLI

形態素解析結果に同義語展開結果が追加されます。
Synonym expansion results are added to the morphological analysis results.

```
$ java -java -cp sudachi-0.5.0.jar:jdartsclone-1.2.0.jar:javax.json-1.1.jar:chikkar-0.1.0.jar \
com.worksap.nlp.sudachi.SudachiCommandLine -s '{"formatterPlugin":[{"class":"com.worksap.nlp.chikkar.SynonymFormatter","systemDict":"system_syn.dic"}]}'
開店記念セール
開店    名詞,普通名詞,サ変可能,*,*,*    開店    始業,営業開始,店開き,オープン,open
記念    名詞,普通名詞,サ変可能,*,*,*    記念    メモリアル,memorial
セール  名詞,普通名詞,一般,*,*,*        セール
EOS
```

以下のオプションを JSON で指定することができます。

- systemDict : `string`
    - Sudachi 同義語辞書 Sudachi synonym dictionary
- userDict : `array of string`
    - ユーザ辞書をリストで指定します。あとに指定した方が優先されます。 Specify user dictionaries by array. The later dictionary has higher priority.
- enableVerb : `boolean`
    - 用言の同義語を有効にします。デフォルトは `false` です。 Enable verb and adjective synonyms. The default is `false`.
- synonymDelimiter : `string`
    - 同義語の区切りを指定します。デフォルトは `,` です。 Specify the separator of synonyms. By default, `,`.
