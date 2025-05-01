package asm.symbol;

/**
 * SymbolTableTest.java - SymbolTableクラスのテスト
 */
public class SymbolTableTest {
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("===== SymbolTable クラステスト開始 =====");
        
        testInitialization();
        testAddEntry();
        testContains();
        testGetAddress();
        
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
     * 初期化テスト - 事前定義シンボルが正しく初期化されているか
     */
    private static void testInitialization() {
        System.out.println("\n----- 初期化テスト -----");
        
        SymbolTable table = new SymbolTable();
        
        // 事前定義シンボルの一部をテスト
        testCase("R0の初期値", table.getAddress("R0"), 0);
        testCase("R15の初期値", table.getAddress("R15"), 15);
        testCase("SCREENの初期値", table.getAddress("SCREEN"), 16384);
        testCase("KBDの初期値", table.getAddress("KBD"), 24576);
        testCase("SPの初期値", table.getAddress("SP"), 0);
        testCase("LCLの初期値", table.getAddress("LCL"), 1);
        testCase("ARGの初期値", table.getAddress("ARG"), 2);
        testCase("THISの初期値", table.getAddress("THIS"), 3);
        testCase("THATの初期値", table.getAddress("THAT"), 4);
    }
    
    /**
     * addEntryメソッドのテスト
     */
    private static void testAddEntry() {
        System.out.println("\n----- addEntryメソッドのテスト -----");
        
        SymbolTable table = new SymbolTable();
        
        // 新しいシンボルを追加
        table.addEntry("LOOP", 10);
        table.addEntry("END", 20);
        table.addEntry("i", 16);
        
        // 追加したシンボルの値を確認
        testCase("LOOPのアドレス", table.getAddress("LOOP"), 10);
        testCase("ENDのアドレス", table.getAddress("END"), 20);
        testCase("iのアドレス", table.getAddress("i"), 16);
        
        // 既存のシンボルを上書き
        table.addEntry("LOOP", 30);
        testCase("LOOP上書き後のアドレス", table.getAddress("LOOP"), 30);
    }
    
    /**
     * containsメソッドのテスト
     */
    private static void testContains() {
        System.out.println("\n----- containsメソッドのテスト -----");
        
        SymbolTable table = new SymbolTable();
        
        // 初期シンボルの存在確認
        testCase("R0の存在", table.contains("R0"), true);
        testCase("SCREENの存在", table.contains("SCREEN"), true);
        
        // 存在しないシンボルの確認
        testCase("LOOPの存在", table.contains("LOOP"), false);
        
        // シンボル追加後の確認
        table.addEntry("LOOP", 10);
        testCase("LOOP追加後の存在", table.contains("LOOP"), true);
    }
    
    /**
     * getAddressメソッドのテスト
     */
    private static void testGetAddress() {
        System.out.println("\n----- getAddressメソッドのテスト -----");
        
        SymbolTable table = new SymbolTable();
        
        // 既存のシンボルのアドレス取得
        testCase("R5のアドレス", table.getAddress("R5"), 5);
        
        // 存在しないシンボルの場合
        testCase("存在しないシンボル", table.getAddress("NOTEXIST"), null);
    }
    
    /**
     * テストケース実行
     */
    private static void testCase(String testName, Object actual, Object expected) {
        totalTests++;
        boolean passed = (actual == null && expected == null) || 
                         (actual != null && actual.equals(expected));
        
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