package asm.hack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Parser と Code クラスのインポート
import asm.parser.Parser;
import asm.code.Code;

/**
 * Hack.java - HackアセンブラのメインクラスNAND2TETRISプロジェクト6
 * ParserとCodeを使用して、Hackアセンブリコードをマシン語に変換します
 */
public class Hack {
    // シンボルテーブル
    private Map<String, Integer> symbolTable;
    // 次に割り当てられる変数アドレス (R15の後から開始)
    private int nextVariableAddress = 16;

    /**
     * コンストラクタ - 初期シンボルテーブルを設定
     */
    public Hack() {
        symbolTable = new HashMap<>();
        initSymbolTable();
    }

    /**
     * 初期シンボルテーブルをセットアップ
     */
    private void initSymbolTable() {
        // 事前定義シンボル
        for (int i = 0; i <= 15; i++) {
            symbolTable.put("R" + i, i);
        }
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
    }

    /**
     * アセンブルプロセスを実行
     * @param inputFile 入力Hackアセンブリファイル
     * @param outputFile 出力バイナリファイル
     */
    public void assemble(String inputFile, String outputFile) throws IOException {
        // 最初のパス - シンボルテーブルを構築 (ラベルのみ)
        buildSymbolTable(inputFile);
        
        // 二回目のパス - 実際のアセンブル処理
        assembleCode(inputFile, outputFile);
    }

    /**
     * 最初のパス - シンボルテーブルを構築（ラベルのみ）
     */
    private void buildSymbolTable(String inputFile) throws IOException {
        Parser parser = new Parser(inputFile);
        int romAddress = 0;

        while (parser.hasMoreCommands()) {
            if (parser.instructionType().equals("L_COMMAND")) {
                // ラベルをシンボルテーブルに追加
                symbolTable.put(parser.symbol(), romAddress);
            } else {
                // A命令またはC命令の場合はROMアドレスを進める
                romAddress++;
            }
            parser.advance();
        }
    }

    /**
     * 二回目のパス - 実際のアセンブル処理
     */
    private void assembleCode(String inputFile, String outputFile) throws IOException {
        Parser parser = new Parser(inputFile);
        Code code = new Code();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        while (parser.hasMoreCommands()) {
            String instruction = "";
            
            if (parser.instructionType().equals("A_COMMAND")) {
                // A命令処理
                String symbol = parser.symbol();
                int address;
                
                try {
                    // 数値の場合
                    address = Integer.parseInt(symbol);
                } catch (NumberFormatException e) {
                    // シンボルの場合
                    if (!symbolTable.containsKey(symbol)) {
                        // 新しい変数としてシンボルテーブルに追加
                        symbolTable.put(symbol, nextVariableAddress);
                        nextVariableAddress++;
                    }
                    address = symbolTable.get(symbol);
                }
                
                // 16ビットバイナリに変換
                instruction = String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
            } else if (parser.instructionType().equals("C_COMMAND")) {
                // C命令処理
                // 1 + 1 1 1 + a c1c2c3c4c5c6 + d1d2d3 + j1j2j3
                String dest = code.dest(parser.dest());
                String comp = code.comp(parser.comp());
                String jump = code.jump(parser.jump());
                
                instruction = "111" + comp + dest + jump;
            }
            // L_COMMANDの場合は出力しない
            
            if (!instruction.isEmpty()) {
                writer.write(instruction);
                writer.newLine();
            }
            
            parser.advance();
        }
        
        writer.close();
    }

    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("使用法: java Hack <入力アセンブリファイル> <出力バイナリファイル>");
            System.exit(1);
        }
        
        try {
            Hack assembler = new Hack();
            assembler.assemble(args[0], args[1]);
            System.out.println("アセンブル完了: " + args[1]);
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
