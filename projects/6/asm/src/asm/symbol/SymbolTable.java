package asm.symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable.java - シンボルテーブル管理クラス
 * Hackアセンブリのシンボルと対応するアドレスを管理します
 */
public class SymbolTable {
    // シンボルとアドレスのマップ
    private Map<String, Integer> symbolTable;
    
    /**
     * コンストラクタ - シンボルテーブルを初期化
     */
    public SymbolTable() {
        symbolTable = new HashMap<>();
        initSymbolTable();
    }
    
    /**
     * 初期シンボルテーブルをセットアップ
     */
    private void initSymbolTable() {
        // 事前定義シンボル
        for (int i = 0; i <= 15; i++) {
            addEntry("R" + i, i);
        }
        addEntry("SCREEN", 16384);
        addEntry("KBD", 24576);
        addEntry("SP", 0);
        addEntry("LCL", 1);
        addEntry("ARG", 2);
        addEntry("THIS", 3);
        addEntry("THAT", 4);
    }
    
    /**
     * シンボルテーブルにエントリを追加
     * @param symbol シンボル名
     * @param address アドレス値
     */
    public void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }
    
    /**
     * シンボルがテーブルに含まれているかを確認
     * @param symbol 確認するシンボル名
     * @return シンボルが含まれていればtrue、そうでなければfalse
     */
    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }
    
    /**
     * シンボルのアドレスを取得
     * @param symbol アドレスを取得するシンボル名
     * @return シンボルに対応するアドレス（存在しない場合はnull）
     */
    public Integer getAddress(String symbol) {
        return symbolTable.get(symbol);
    }
} 