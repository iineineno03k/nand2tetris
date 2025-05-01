package asm.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParserTest {
    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        try {
            // テストファイルの作成
            createTestFile("test.asm");

            // テスト実行
            runTests("test.asm");

            // 結果出力
            System.out.println("\n===== テスト結果概要 =====");
            System.out.println("実行: " + totalTests + " テスト");
            System.out.println("成功: " + passedTests + " テスト");
            System.out.println("失敗: " + (totalTests - passedTests) + " テスト");

            if (passedTests == totalTests) {
                System.out.println("全テスト成功！");
            } else {
                System.out.println("テスト失敗あり。上記エラーを確認してください。");
            }

            // テスト後のクリーンアップ
            Files.deleteIfExists(Paths.get("test.asm"));
        } catch (Exception e) {
            System.err.println("テスト実行中にエラー発生: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTestFile(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // テスト用アセンブリコード
            writer.write("// テスト用コード\n");
            writer.write("@100\n"); // A命令テスト用
            writer.write("D=A\n"); // C命令（dest+comp）テスト用
            writer.write("@i\n"); // A命令（シンボル）テスト用
            writer.write("M=D\n"); // C命令テスト用
            writer.write("(LOOP)\n"); // L命令テスト用
            writer.write("@i\n");
            writer.write("D=M\n");
            writer.write("@LOOP\n");
            writer.write("D;JGT\n"); // C命令（jump）テスト用
        }
        System.out.println("テスト用ファイル作成: " + filename);
    }

    private static void runTests(String filename) throws IOException {
        System.out.println("===== Parser テスト開始 =====");
        Parser parser = new Parser(filename);
        
        // テスト1: A命令 (@100)
        System.out.println("\nテスト1のデバッグ情報:");
        System.out.println("コマンド: " + parser.currentCommand);
        System.out.println("hasMoreCommands: " + parser.hasMoreCommands());
        System.out.println("instructionType: " + parser.instructionType());
        System.out.println("symbol: " + parser.symbol());
        
        testCase("A命令の解析", 
            parser.hasMoreCommands() && 
            parser.instructionType().equals("A_COMMAND") && 
            parser.symbol().equals("100"));
        
        // テスト2: C命令 (D=A)
        parser.advance();
        System.out.println("\nテスト2のデバッグ情報:");
        System.out.println("コマンド: " + parser.currentCommand);
        System.out.println("instructionType: " + parser.instructionType());
        System.out.println("dest: [" + parser.dest() + "]");
        System.out.println("comp: [" + parser.comp() + "]");
        System.out.println("jump: [" + parser.jump() + "]");
        
        testCase("C命令(dest+comp)の解析", 
            parser.hasMoreCommands() && 
            parser.instructionType().equals("C_COMMAND") && 
            parser.dest().equals("D") && 
            parser.comp().equals("A") && 
            parser.jump().equals(""));
        
        // @i をスキップ
        parser.advance();
        System.out.println("\nスキップ @i:");
        System.out.println("コマンド: " + parser.currentCommand);
        
        // テスト3: C命令 (M=D)
        parser.advance();
        System.out.println("\nテスト3のデバッグ情報:");
        System.out.println("コマンド: " + parser.currentCommand);
        System.out.println("instructionType: " + parser.instructionType());
        System.out.println("dest: [" + parser.dest() + "]");
        System.out.println("comp: [" + parser.comp() + "]");
        System.out.println("jump: [" + parser.jump() + "]");
        
        testCase("C命令(M=D)の解析", 
            parser.hasMoreCommands() && 
            parser.instructionType().equals("C_COMMAND") && 
            parser.dest().equals("M") && 
            parser.comp().equals("D") && 
            parser.jump().equals(""));
        
        // テスト4: L命令 ((LOOP))
        parser.advance();
        System.out.println("\nテスト4のデバッグ情報:");
        System.out.println("コマンド: " + parser.currentCommand);
        System.out.println("instructionType: " + parser.instructionType());
        System.out.println("symbol: [" + parser.symbol() + "]");
        
        testCase("L命令の解析", 
            parser.hasMoreCommands() && 
            parser.instructionType().equals("L_COMMAND") && 
            parser.symbol().equals("LOOP"));
        
        // @i, D=M, @LOOP をスキップ
        System.out.println("\n残りの命令をスキップ:");
        for (int i = 0; i < 3; i++) {
            parser.advance();
            System.out.println("コマンド " + (i+1) + ": " + parser.currentCommand);
        }
        
        // さらに進めてD;JGTに到達
        parser.advance();
        
        // テスト5: ジャンプ命令 (D;JGT)
        System.out.println("\nテスト5のデバッグ情報:");
        System.out.println("コマンド: " + parser.currentCommand);
        System.out.println("instructionType: " + parser.instructionType());
        System.out.println("dest: [" + parser.dest() + "]");
        System.out.println("comp: [" + parser.comp() + "]");
        System.out.println("jump: [" + parser.jump() + "]");
        
        testCase("ジャンプ命令の解析", 
            parser.hasMoreCommands() && 
            parser.instructionType().equals("C_COMMAND") && 
            parser.dest().equals("") && 
            parser.comp().equals("D") && 
            parser.jump().equals("JGT"));
        
        // テスト6: ファイル終端
        parser.advance();
        System.out.println("\nテスト6のデバッグ情報:");
        System.out.println("hasMoreCommands: " + parser.hasMoreCommands());
        
        testCase("ファイル終端の処理", !parser.hasMoreCommands());
    }

    private static void testCase(String testName, boolean condition) {
        totalTests++;
        System.out.print("テスト " + totalTests + ": " + testName + " - ");

        if (condition) {
            System.out.println("成功 ✓");
            passedTests++;
        } else {
            System.out.println("失敗 ✗ - 条件が満たされていません");
        }
    }
}