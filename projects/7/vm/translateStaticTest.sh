#!/bin/bash

# binディレクトリがなければ作成
mkdir -p bin

# VMトランスレータをコンパイル
javac -d bin src/vm/parser/Parser.java src/vm/codewriter/CodeWriter.java src/vm/VMTranslator.java

# SimpleAdd.vmを変換
java -cp bin vm.VMTranslator ../MemoryAccess/StaticTest/StaticTest.vm

# 変換結果を表示
echo "変換完了！"
echo "生成されたファイル: ../MemoryAccess/StaticTest/StaticTest.asm" 