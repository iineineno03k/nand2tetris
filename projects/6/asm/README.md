# Hack アセンブラプロジェクト

## 概要
このプロジェクトは、HackアセンブリプログラムをHackマシン語に変換するアセンブラの実装です。
nand2tetris（コンピュータシステムの理論と実装）の第6章の課題として作成されています。

## ファイル構成
- `src/asm/parser/Parser.java` - アセンブリコードの構文解析
- `src/asm/code/Code.java` - 命令コードの生成
- `src/asm/hack/Hack.java` - アセンブラのメインクラス
- `src/asm/hack/HackTest.java` - アセンブラテスト
- `src/asm/code/CodeTest.java` - Codeクラステスト
- `src/asm/parser/ParserTest.java` - Parserクラステスト
- `src/asm/Test.java` - 統合テスト

## コンポーネント説明
- **Parser**: アセンブリ言語コマンドを解析する
- **Code**: Hackアセンブリニーモニックをバイナリコードに変換する
- **Hack**: メインクラス。シンボルテーブルを管理し、アセンブル処理を実行する

## プロジェクトのビルドと実行

### コンパイル
```bash
# プロジェクトルートからコンパイル
javac -d bin src/asm/code/Code.java src/asm/parser/Parser.java src/asm/hack/Hack.java
```

### アセンブラの実行
```bash
# Hack アセンブラの実行
java -cp bin asm.hack.Hack [入力アセンブリファイル] [出力バイナリファイル]

# 例:
java -cp bin asm.hack.Hack Add.asm Add.hack
```

### テストの実行
```bash
# 個別テストのコンパイル
javac -d bin src/asm/code/Code.java src/asm/parser/Parser.java src/asm/hack/Hack.java src/asm/code/CodeTest.java src/asm/parser/ParserTest.java src/asm/hack/HackTest.java

# 統合テストのコンパイル (全てのクラスを含む)
javac -d bin src/asm/code/Code.java src/asm/parser/Parser.java src/asm/hack/Hack.java src/asm/code/CodeTest.java src/asm/parser/ParserTest.java src/asm/hack/HackTest.java src/asm/Test.java

# 個別テスト実行
java -cp bin asm.code.CodeTest
java -cp bin asm.parser.ParserTest
java -cp bin asm.hack.HackTest

# 統合テスト実行 (全てのテストを順に実行)
java -cp bin asm.Test
```

## 注意点
- このプロジェクトは`asm`をルートパッケージとし、その下に`code`、`parser`、`hack`のサブパッケージを持つ構造になっています。
- `bin`ディレクトリにコンパイル済みのクラスファイルが生成されます。
- パッケージ構造のため、プロジェクトのビルドと実行には`-cp`（クラスパス）オプションが必要です。