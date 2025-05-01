# Hack アセンブラプロジェクト

## 概要
このプロジェクトは、HackアセンブリプログラムをHackマシン語に変換するアセンブラの実装です。
nand2tetris（コンピュータシステムの理論と実装）の第6章の課題として作成されています。

## ファイル構成
- `Parser.java` - アセンブリコードの構文解析
- `Code.java` - 命令コードの生成
- `Hack.java` - 共通定数・ユーティリティ
- `ParserTest.java` - Parserクラスのテスト

## テスト実行方法
以下のコマンドでParserTestを実行できます：
```bash
javac Parser.java ParserTest.java
java ParserTest
```

## 注意点
- IDEでは「Parser cannot be resolved to a type」という警告が表示されることがありますが、
  コマンドラインでのコンパイル・実行は正常に動作します。
- これはIDEのプロジェクト構成の問題であり、コードの機能には影響しません。