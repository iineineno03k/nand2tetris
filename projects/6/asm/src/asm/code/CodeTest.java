/**
 * CodeTest.java - Codeクラステスト
 */
package asm.code;

import java.io.IOException;

public class CodeTest {
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("===== Code クラステスト開始 =====");
        
        // 各メソッドのテスト
        testDest();
        testComp();
        testJump();
        
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
     * destメソッドのテスト
     */
    private static void testDest() {
        System.out.println("\n----- destメソッドのテスト -----");
        Code code = new Code();
        
        // 宛先なし
        testCase("dest(null)", code.dest(null), "000");
        testCase("dest(\"\")", code.dest(""), "000");
        
        // 各レジスタへの書き込み
        testCase("dest(\"M\")", code.dest("M"), "001");
        testCase("dest(\"D\")", code.dest("D"), "010");
        testCase("dest(\"MD\")", code.dest("MD"), "011");
        testCase("dest(\"A\")", code.dest("A"), "100");
        testCase("dest(\"AM\")", code.dest("AM"), "101");
        testCase("dest(\"AD\")", code.dest("AD"), "110");
        testCase("dest(\"AMD\")", code.dest("AMD"), "111");
    }
    
    /**
     * compメソッドのテスト
     */
    private static void testComp() {
        System.out.println("\n----- compメソッドのテスト -----");
        Code code = new Code();
        
        // a=0 (Aレジスタ使用)
        testCase("comp(\"0\")", code.comp("0"), "0101010");
        testCase("comp(\"1\")", code.comp("1"), "0111111");
        testCase("comp(\"-1\")", code.comp("-1"), "0111010");
        testCase("comp(\"D\")", code.comp("D"), "0001100");
        testCase("comp(\"A\")", code.comp("A"), "0110000");
        testCase("comp(\"!D\")", code.comp("!D"), "0001101");
        testCase("comp(\"!A\")", code.comp("!A"), "0110001");
        testCase("comp(\"-D\")", code.comp("-D"), "0001111");
        testCase("comp(\"-A\")", code.comp("-A"), "0110011");
        testCase("comp(\"D+1\")", code.comp("D+1"), "0011111");
        testCase("comp(\"A+1\")", code.comp("A+1"), "0110111");
        testCase("comp(\"D-1\")", code.comp("D-1"), "0001110");
        testCase("comp(\"A-1\")", code.comp("A-1"), "0110010");
        testCase("comp(\"D+A\")", code.comp("D+A"), "0000010");
        testCase("comp(\"D-A\")", code.comp("D-A"), "0010011");
        testCase("comp(\"A-D\")", code.comp("A-D"), "0000111");
        testCase("comp(\"D&A\")", code.comp("D&A"), "0000000");
        testCase("comp(\"D|A\")", code.comp("D|A"), "0010101");
        
        // a=1 (Mを使用)
        testCase("comp(\"M\")", code.comp("M"), "1110000");
        testCase("comp(\"!M\")", code.comp("!M"), "1110001");
        testCase("comp(\"-M\")", code.comp("-M"), "1110011");
        testCase("comp(\"M+1\")", code.comp("M+1"), "1110111");
        testCase("comp(\"M-1\")", code.comp("M-1"), "1110010");
        testCase("comp(\"D+M\")", code.comp("D+M"), "1000010");
        testCase("comp(\"D-M\")", code.comp("D-M"), "1010011");
        testCase("comp(\"M-D\")", code.comp("M-D"), "1000111");
        testCase("comp(\"D&M\")", code.comp("D&M"), "1000000");
        testCase("comp(\"D|M\")", code.comp("D|M"), "1010101");
        
        // 例外ケース
        try {
            code.comp("");
            testCase("comp(\"\") 例外検出", "例外なし", "例外発生");
        } catch (IllegalArgumentException e) {
            testCase("comp(\"\") 例外検出", "例外発生", "例外発生");
        }
    }
    
    /**
     * jumpメソッドのテスト
     */
    private static void testJump() {
        System.out.println("\n----- jumpメソッドのテスト -----");
        Code code = new Code();
        
        // ジャンプなし
        testCase("jump(null)", code.jump(null), "000");
        testCase("jump(\"\")", code.jump(""), "000");
        
        // 各ジャンプ条件
        testCase("jump(\"JGT\")", code.jump("JGT"), "001");
        testCase("jump(\"JEQ\")", code.jump("JEQ"), "010");
        testCase("jump(\"JGE\")", code.jump("JGE"), "011");
        testCase("jump(\"JLT\")", code.jump("JLT"), "100");
        testCase("jump(\"JNE\")", code.jump("JNE"), "101");
        testCase("jump(\"JLE\")", code.jump("JLE"), "110");
        testCase("jump(\"JMP\")", code.jump("JMP"), "111");
        
        // 未定義の値
        testCase("jump(\"XXX\")", code.jump("XXX"), "000");
    }
    
    /**
     * テストケース実行
     */
    private static void testCase(String testName, String actual, String expected) {
        totalTests++;
        boolean passed = actual.equals(expected);
        
        System.out.print("テスト " + totalTests + ": " + testName + " - ");
        if (passed) {
            System.out.println("成功 ✓");
            passedTests++;
        } else {
            System.out.println("失敗 ✗");
            System.out.println("  期待値: [" + expected + "]");
            System.out.println("  実際値: [" + actual + "]");
        }
    }
} 