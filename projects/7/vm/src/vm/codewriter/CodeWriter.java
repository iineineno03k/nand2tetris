package vm.codewriter;

import java.io.FileWriter;
import java.io.IOException;

/**
 * VMコマンドをHackアセンブリコードに変換する
 */
public class CodeWriter {
    private FileWriter writer;
    private int labelCounter; // ジャンプラベル用カウンタ
    
    // コマンドタイプ定数
    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    
    /**
     * 出力ファイルを開き、書き込む準備をする
     */
    public CodeWriter(String filename) throws IOException {
        writer = new FileWriter(filename);
        labelCounter = 0;
    }
    
    /**
     * 与えられた算術コマンドをアセンブリコードに変換し、書き込む
     */
    public void writeArithmetic(String command) throws IOException {
        // VMコマンドのコメントを追加
        writer.write("// " + command + "\n");
        
        if (command.equals("add")) {
            // スタックから2つの値をポップして足し、結果をプッシュ
            popToD();
            decrementSP();
            writer.write("M=M+D\n");
            incrementSP();
        } else if (command.equals("sub")) {
            // スタックから2つの値をポップして引き、結果をプッシュ
            popToD();
            decrementSP();
            writer.write("M=M-D\n");
            incrementSP();
        } else if (command.equals("neg")) {
            // スタックトップの値を反転
            decrementSP();
            writer.write("M=-M\n");
            incrementSP();
        } else if (command.equals("eq") || command.equals("gt") || command.equals("lt")) {
            // 比較操作
            String label = "LABEL" + labelCounter++;
            popToD();
            decrementSP();
            writer.write("D=M-D\n");
            writer.write("@" + label + "\n");
            
            if (command.equals("eq")) {
                writer.write("D;JEQ\n"); // 等しい場合
            } else if (command.equals("gt")) {
                writer.write("D;JGT\n"); // より大きい場合
            } else if (command.equals("lt")) {
                writer.write("D;JLT\n"); // より小さい場合
            }
            
            writer.write("@SP\n");
            writer.write("A=M-1\n");
            writer.write("M=0\n");
            writer.write("@CONTINUE" + labelCounter + "\n");
            writer.write("0;JMP\n");
            writer.write("(" + label + ")\n");
            writer.write("@SP\n");
            writer.write("A=M-1\n");
            writer.write("M=-1\n");
            writer.write("(CONTINUE" + labelCounter + ")\n");
            labelCounter++;
        } else if (command.equals("and")) {
            // 論理AND
            popToD();
            decrementSP();
            writer.write("M=M&D\n");
            incrementSP();
        } else if (command.equals("or")) {
            // 論理OR
            popToD();
            decrementSP();
            writer.write("M=M|D\n");
            incrementSP();
        } else if (command.equals("not")) {
            // 論理NOT
            decrementSP();
            writer.write("M=!M\n");
            incrementSP();
        }
    }
    
    /**
     * C_PUSHまたはC_POPコマンドをアセンブリコードに変換し、書き込む
     */
    public void writePushPop(int command, String segment, int index) throws IOException {
        if (command == C_PUSH) {
            // VMコマンドのコメントを追加
            writer.write("// push " + segment + " " + index + "\n");
            
            if (segment.equals("constant")) {
                // 定数をプッシュ
                writer.write("@" + index + "\n");
                writer.write("D=A\n");
                pushD();
            }
            // 他のセグメントは後で実装
        } else if (command == C_POP) {
            // VMコマンドのコメントを追加
            writer.write("// pop " + segment + " " + index + "\n");
            
            // ポップコマンドは後で実装
        }
    }
    
    /**
     * 出力ファイルを閉じる
     */
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
    
    // ヘルパーメソッド
    
    // スタックポインタをインクリメント
    private void incrementSP() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M+1\n");
    }
    
    // スタックポインタをデクリメント
    private void decrementSP() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M-1\n");
        writer.write("A=M\n");
    }
    
    // Dレジスタをスタックにプッシュ
    private void pushD() throws IOException {
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        incrementSP();
    }
    
    // スタックからポップしてDレジスタに格納
    private void popToD() throws IOException {
        decrementSP();
        writer.write("D=M\n");
    }
} 