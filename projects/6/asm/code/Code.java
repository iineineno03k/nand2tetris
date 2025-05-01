/**
 * Code.java - バイナリコード生成クラス
 * Hackのアセンブリコード要素をバイナリコードに変換します
 */

public class Code {
    /**
     * destニーモニックをバイナリコードに変換
     * @param mnemonic destニーモニック (例: "M", "D", "MD", "A", "AM", "AD", "AMD")
     * @return 3ビットのバイナリコード (d1d2d3)
     */
    public String dest(String mnemonic) {
        if (mnemonic == null || mnemonic.isEmpty()) {
            return "000"; // 宛先なし
        }
        
        // d1 (Aレジスタに書き込み)
        boolean writeA = mnemonic.contains("A");
        // d2 (Dレジスタに書き込み)
        boolean writeD = mnemonic.contains("D");
        // d3 (メモリに書き込み)
        boolean writeM = mnemonic.contains("M");
        
        return (writeA ? "1" : "0") + 
               (writeD ? "1" : "0") + 
               (writeM ? "1" : "0");
    }
    
    /**
     * compニーモニックをバイナリコードに変換
     * @param mnemonic compニーモニック (例: "0", "1", "-1", "D", "A", "M", "!D", "!A", ...)
     * @return 7ビットのバイナリコード (a c1c2c3c4c5c6)
     */
    public String comp(String mnemonic) {
        if (mnemonic == null || mnemonic.isEmpty()) {
            throw new IllegalArgumentException("comp部分は必須です");
        }
        
        // a=0のケース (Aレジスタ使用)
        switch (mnemonic) {
            case "0":   return "0101010";
            case "1":   return "0111111";
            case "-1":  return "0111010";
            case "D":   return "0001100";
            case "A":   return "0110000";
            case "!D":  return "0001101";
            case "!A":  return "0110001";
            case "-D":  return "0001111";
            case "-A":  return "0110011";
            case "D+1": return "0011111";
            case "A+1": return "0110111";
            case "D-1": return "0001110";
            case "A-1": return "0110010";
            case "D+A": return "0000010";
            case "D-A": return "0010011";
            case "A-D": return "0000111";
            case "D&A": return "0000000";
            case "D|A": return "0010101";
        }
        
        // a=1のケース (Mを使用)
        switch (mnemonic) {
            case "M":   return "1110000";
            case "!M":  return "1110001";
            case "-M":  return "1110011";
            case "M+1": return "1110111";
            case "M-1": return "1110010";
            case "D+M": return "1000010";
            case "D-M": return "1010011";
            case "M-D": return "1000111";
            case "D&M": return "1000000";
            case "D|M": return "1010101";
        }
        
        throw new IllegalArgumentException("不正なcomp命令: " + mnemonic);
    }
    
    /**
     * jumpニーモニックをバイナリコードに変換
     * @param mnemonic jumpニーモニック (例: "JGT", "JEQ", "JGE", "JLT", ...)
     * @return 3ビットのバイナリコード (j1j2j3)
     */
    public String jump(String mnemonic) {
        if (mnemonic == null || mnemonic.isEmpty()) {
            return "000"; // ジャンプなし
        }
        
        switch (mnemonic) {
            case "JGT": return "001"; // 正の場合ジャンプ
            case "JEQ": return "010"; // ゼロの場合ジャンプ
            case "JGE": return "011"; // 正またはゼロの場合ジャンプ
            case "JLT": return "100"; // 負の場合ジャンプ
            case "JNE": return "101"; // 非ゼロの場合ジャンプ
            case "JLE": return "110"; // 負またはゼロの場合ジャンプ
            case "JMP": return "111"; // 無条件ジャンプ
            default:    return "000"; // ジャンプなし
        }
    }
}
