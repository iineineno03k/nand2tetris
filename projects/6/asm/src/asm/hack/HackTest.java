package asm.hack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * HackTest.java - Hackアセンブラテスト
 * 
 * 簡単なアセンブリプログラムをテストします
 */
public class HackTest {
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("===== Hack アセンブラテスト開始 =====");
        
        testSimpleAssembly();
        testSymbolHandling();
        
        // 結果概要
        System.out.println("\n===== テスト結果概要 =====");
        System.out.println("実行: " + totalTests + " テスト");
        System.out.println("成功: " + passedTests + " テスト");
        System.out.println("失敗: " + (totalTests - passedTests) + " テスト");
        
        if (passedTests == totalTests) {
            System.out.println("全テスト成功！");
        } else {
            System.out.println("テスト失敗あり。上記エラーを確認してください。");
        }
    }
    
    /**
     * シンプルなアセンブリコードのテスト
     */
    private static void testSimpleAssembly() {
        System.out.println("\n----- シンプルなアセンブリコードのテスト -----");
        
        // テスト用の一時ファイル作成
        try {
            // 入力アセンブリファイル作成
            String inputFile = "testSimple.asm";
            String outputFile = "testSimple.hack";
            writeTestAssemblyFile(inputFile);
            
            // アセンブル実行
            Hack assembler = new Hack();
            assembler.assemble(inputFile, outputFile);
            
            // 結果を検証
            String[] expectedLines = {
                "0000000000000010", // @2
                "1110110000010000", // D=A
                "0000000000000011", // @3
                "1110000010010000", // D=D+A
                "0000000000000000", // @0
                "1110001100001000"  // M=D
            };
            
            verifyOutputFile(outputFile, expectedLines);
            
            // 後片付け
            cleanupFiles(inputFile, outputFile);
            
        } catch (IOException e) {
            testFailed("シンプルアセンブリテスト", "例外なし", "例外発生: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * シンボル処理のテスト
     */
    private static void testSymbolHandling() {
        System.out.println("\n----- シンボル処理のテスト -----");
        
        // テスト用の一時ファイル作成
        try {
            // 入力アセンブリファイル作成
            String inputFile = "testSymbol.asm";
            String outputFile = "testSymbol.hack";
            writeTestSymbolFile(inputFile);
            
            // アセンブル実行
            Hack assembler = new Hack();
            assembler.assemble(inputFile, outputFile);
            
            // 結果を検証
            String[] expectedLines = {
                "0000000000000000", // @R0
                "1111110000010000", // D=M
                "0000000000000001", // @R1
                "1111010011010000", // D=D-M
                "0000000000001100", // @EQUAL (アセンブラが割り当てたアドレス)
                "1110001100000010", // D;JEQ
                "0000000000000000", // @R0
                "1111110000010000", // D=M
                "0000000000000010", // @R2
                "1110001100001000", // M=D
                "0000000000010000", // @END (アセンブラが割り当てたアドレス)
                "1110101010000111", // 0;JMP
                "0000000000000001", // @R1 (EQUAL)
                "1111110000010000", // D=M
                "0000000000000010", // @R2
                "1110001100001000"  // M=D (END)
            };
            
            verifyOutputFile(outputFile, expectedLines);
            
            // 後片付け
            cleanupFiles(inputFile, outputFile);
            
        } catch (IOException e) {
            testFailed("シンボル処理テスト", "例外なし", "例外発生: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * テストアセンブリファイル (シンプル) 作成
     */
    private static void writeTestAssemblyFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("// シンプルな計算例\n");
        content.append("@2\n");
        content.append("D=A\n");
        content.append("@3\n");
        content.append("D=D+A\n");
        content.append("@0\n");
        content.append("M=D\n");
        
        java.nio.file.Files.write(java.nio.file.Paths.get(filename), content.toString().getBytes());
    }
    
    /**
     * テストアセンブリファイル (シンボル) 作成
     */
    private static void writeTestSymbolFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("// シンボル使用例\n");
        content.append("@R0\n");
        content.append("D=M\n");
        content.append("@R1\n");
        content.append("D=D-M\n");
        content.append("@EQUAL\n");
        content.append("D;JEQ\n");
        content.append("@R0\n");
        content.append("D=M\n");
        content.append("@R2\n");
        content.append("M=D\n");
        content.append("@END\n");
        content.append("0;JMP\n");
        content.append("(EQUAL)\n");
        content.append("@R1\n");
        content.append("D=M\n");
        content.append("@R2\n");
        content.append("M=D\n");
        content.append("(END)\n");
        
        java.nio.file.Files.write(java.nio.file.Paths.get(filename), content.toString().getBytes());
    }
    
    /**
     * 出力バイナリファイルの検証
     */
    private static void verifyOutputFile(String filename, String[] expectedLines) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int lineNumber = 0;
        String line;
        
        while ((line = reader.readLine()) != null && lineNumber < expectedLines.length) {
            String expected = expectedLines[lineNumber];
            testCase("バイナリ出力 行 " + (lineNumber + 1), line, expected);
            lineNumber++;
        }
        
        reader.close();
        
        testCase("出力行数", lineNumber, expectedLines.length);
    }
    
    /**
     * テスト用ファイルの削除
     */
    private static void cleanupFiles(String... files) {
        for (String file : files) {
            new File(file).delete();
        }
    }
    
    /**
     * テストケース実行
     */
    private static void testCase(String testName, Object actual, Object expected) {
        totalTests++;
        boolean passed = actual.equals(expected);
        
        System.out.print("テスト " + totalTests + ": " + testName + " - ");
        if (passed) {
            System.out.println("成功 ✓");
            passedTests++;
        } else {
            testFailed(testName, expected.toString(), actual.toString());
        }
    }
    
    /**
     * テスト失敗表示
     */
    private static void testFailed(String testName, String expected, String actual) {
        totalTests++;
        System.out.println("失敗 ✗");
        System.out.println("  期待値: [" + expected + "]");
        System.out.println("  実際値: [" + actual + "]");
    }
} 