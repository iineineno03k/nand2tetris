#!/bin/bash

# binディレクトリがなければ作成
mkdir -p bin

# VMトランスレータをコンパイル
javac -d bin src/vm/parser/Parser.java src/vm/codewriter/CodeWriter.java src/vm/VMTranslator.java

# ディレクトリ全体を変換（Class1.vmとClass2.vmとSys.vmを含む）
java -cp bin vm.VMTranslator ../FunctionCalls/StaticsTest

# 変換結果を表示
echo "変換完了！"
echo "生成されたファイル: ../FunctionCalls/StaticsTest/StaticsTest.asm" 