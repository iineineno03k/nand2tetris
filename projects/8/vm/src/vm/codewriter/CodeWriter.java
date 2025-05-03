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
            String trueLabel = "TRUE" + labelCounter;
            String endLabel = "END" + labelCounter;
            labelCounter++;

            // 右オペランドをDレジスタにポップ
            popToD();

            // 左オペランドのアドレスを取得（SPを減らす）
            decrementSP();

            // 左オペランド - 右オペランドを計算
            writer.write("D=M-D\n");

            // 条件に応じてジャンプ
            writer.write("@" + trueLabel + "\n");
            if (command.equals("eq")) {
                writer.write("D;JEQ\n"); // 等しい場合
            } else if (command.equals("gt")) {
                writer.write("D;JGT\n"); // より大きい場合
            } else if (command.equals("lt")) {
                writer.write("D;JLT\n"); // より小さい場合
            }

            // 条件が偽の場合
            writer.write("@SP\n");
            writer.write("A=M\n");
            writer.write("M=0\n"); // 偽（0）を格納
            writer.write("@" + endLabel + "\n");
            writer.write("0;JMP\n");

            // 条件が真の場合
            writer.write("(" + trueLabel + ")\n");
            writer.write("@SP\n");
            writer.write("A=M\n");
            writer.write("M=-1\n"); // 真（-1）を格納

            // 終了
            writer.write("(" + endLabel + ")\n");
            incrementSP();
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
                return;
            }

            if (segment.equals("pointer")) {
                if (index == 0) {
                    // thisの挙動
                    writer.write("@THIS\n");
                    writer.write("D=M\n");
                    pushD();
                } else if (index == 1) {
                    // thatの挙動
                    writer.write("@THAT\n");
                    writer.write("D=M\n");
                    pushD();
                }

                return;
            }

            // constant以外の実装
            if (segment.equals("local")) {
                // ローカル変数の値をプッシュ
                writer.write("@LCL\n");
                writer.write("D=M\n");

            } else if (segment.equals("argument")) {
                // 引数の値をプッシュ
                writer.write("@ARG\n");
                writer.write("D=M\n");

            } else if (segment.equals("this")) {
                // thisセグメントの値をプッシュ
                writer.write("@THIS\n");
                writer.write("D=M\n");

            } else if (segment.equals("that")) {
                // thatセグメントの値をプッシュ
                writer.write("@THAT\n");
                writer.write("D=M\n");

            } else if (segment.equals("temp")) {
                // tempセグメントの値をプッシュ
                writer.write("@5\n");
                writer.write("D=A\n");

            } else if (segment.equals("static")) {
                writer.write("@16\n");
                writer.write("D=A\n");

            }
            writer.write("@" + index + "\n");
            writer.write("A=D+A\n");
            writer.write("D=M\n");
            pushD();

        } else if (command == C_POP) {
            // VMコマンドのコメントを追加
            writer.write("// pop " + segment + " " + index + "\n");

            if (segment.equals("pointer")) {
                if (index == 0) {
                    // thisの挙動
                    popToD();
                    writer.write("@THIS\n");
                    writer.write("M=D\n");
                } else if (index == 1) {
                    // thatの挙動
                    popToD();
                    writer.write("@THAT\n");
                    writer.write("M=D\n");
                }
                return;
            }

            // 1. ポップ先のアドレスを計算
            if (segment.equals("local")) {
                // ローカル変数のベースアドレスを取得
                writer.write("@LCL\n");
                writer.write("D=M\n");
            } else if (segment.equals("argument")) {
                // 引数のベースアドレスを取得
                writer.write("@ARG\n");
                writer.write("D=M\n");
            } else if (segment.equals("this")) {
                // thisセグメントのベースアドレスを取得
                writer.write("@THIS\n");
                writer.write("D=M\n");
            } else if (segment.equals("that")) {
                // thatセグメントのベースアドレスを取得
                writer.write("@THAT\n");
                writer.write("D=M\n");
            } else if (segment.equals("temp")) {
                // tempセグメントのベースアドレスは固定値5
                writer.write("@5\n");
                writer.write("D=A\n");
            } else if (segment.equals("static")) {
                writer.write("@16\n");
                writer.write("D=A\n");
            }

            // インデックスを加算してアドレスを計算
            writer.write("@" + index + "\n");
            writer.write("D=D+A\n");
            // アドレス情報を一時的にR13に保存
            writer.write("@R13\n");
            writer.write("M=D\n");

            // 2. スタックからポップした値をDレジスタに取得
            popToD();

            // 3. 計算したアドレスに値を格納
            writer.write("@R13\n");
            writer.write("A=M\n");
            writer.write("M=D\n");
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