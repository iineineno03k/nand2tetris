package asm;

import asm.code.CodeTest;
import asm.parser.ParserTest;
import asm.hack.HackTest;

/**
 * Test.java - 統合テストコントローラ
 * 全てのテストを順に実行します
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("===== Hack アセンブラプロジェクト 統合テスト =====\n");
        
        try {
            // Parser テスト
            System.out.println("\n\n===== Parser テスト実行 =====");
            ParserTest.main(args);
            
            // Code テスト
            System.out.println("\n\n===== Code テスト実行 =====");
            CodeTest.main(args);
            
            // Hack テスト
            System.out.println("\n\n===== Hack テスト実行 =====");
            HackTest.main(args);
            
            System.out.println("\n\n===== 統合テスト完了 =====");
            System.out.println("全てのテストが完了しました");
            
        } catch (Exception e) {
            System.err.println("テスト実行中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 