package vm;

import java.io.File;
import java.io.IOException;
import vm.parser.Parser;
import vm.codewriter.CodeWriter;

/**
 * VMトランスレータのメインクラス
 */
public class VMTranslator {
    // パーサーとコードライター
    private Parser parser;
    private CodeWriter codeWriter;
    
    /**
     * 単一のVMファイルをアセンブリコードに変換
     */
    public void translateFile(String vmFilePath) throws IOException {
        // 入力ファイル名からベース名を取得
        File vmFile = new File(vmFilePath);
        String baseName = vmFile.getName();
        if (baseName.endsWith(".vm")) {
            baseName = baseName.substring(0, baseName.length() - 3);
        }
        
        // 出力ファイル名を生成
        String asmFilePath = vmFile.getParent() + File.separator + baseName + ".asm";
        
        // パーサーとコードライターを初期化
        parser = new Parser(vmFilePath);
        codeWriter = new CodeWriter(asmFilePath);
        
        // ファイルを変換
        try {
            translateCommands();
        } finally {
            // リソースを閉じる
            if (parser != null) {
                parser.close();
            }
            if (codeWriter != null) {
                codeWriter.close();
            }
        }
    }
    
    /**
     * 現在のパーサーが指しているファイルのすべてのコマンドを変換
     */
    private void translateCommands() throws IOException {
        // ファイル内のすべてのコマンドをパースして変換
        while (true) {
            int commandType = parser.commandType();
            
            if (commandType == CodeWriter.C_ARITHMETIC) {
                codeWriter.writeArithmetic(parser.arg1());
            } else if (commandType == CodeWriter.C_PUSH || commandType == CodeWriter.C_POP) {
                codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
            }
            
            if (!parser.advance()) {
                break;
            }
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("使用法: java vm.VMTranslator <入力.vm>");
            System.exit(1);
        }
        
        String inputPath = args[0];
        VMTranslator translator = new VMTranslator();
        
        try {
            translator.translateFile(inputPath);
            System.out.println("変換完了: " + inputPath);
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 