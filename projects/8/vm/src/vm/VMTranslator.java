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
        
        // ファイル名を設定（static変数のため）
        codeWriter.setFileName(vmFilePath);
        
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
        while (parser.hasMoreLines()) {
            parser.advance();
            int commandType = parser.commandType();
            
            switch (commandType) {
                case Parser.C_ARITHMETIC:
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                    
                case Parser.C_PUSH:
                case Parser.C_POP:
                    codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                    break;
                    
                case Parser.C_LABEL:
                    codeWriter.writeLabel(parser.arg1());
                    break;
                    
                case Parser.C_GOTO:
                    codeWriter.writeGoto(parser.arg1());
                    break;
                    
                case Parser.C_IF:
                    codeWriter.writeIf(parser.arg1());
                    break;
                    
                case Parser.C_FUNCTION:
                    codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    break;
                    
                case Parser.C_CALL:
                    codeWriter.writeCall(parser.arg1(), parser.arg2());
                    break;
                    
                case Parser.C_RETURN:
                    codeWriter.writeReturn();
                    break;
            }
        }
    }
    
    /**
     * ディレクトリ内の全VMファイルを処理
     */
    public void translateDirectory(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dirPath + "はディレクトリではありません");
        }
        
        // 出力ファイル名を生成（ディレクトリ名を使用）
        String dirName = dir.getName();
        String asmFilePath = dir.getPath() + File.separator + dirName + ".asm";
        
        // コードライターを初期化
        codeWriter = new CodeWriter(asmFilePath);
        
        // ディレクトリ内の全VMファイルを処理
        File[] files = dir.listFiles((d, name) -> name.endsWith(".vm"));
        if (files != null) {
            for (File file : files) {
                // 各ファイルをパース
                parser = new Parser(file.getPath());
                // ファイル名を設定
                codeWriter.setFileName(file.getPath());
                // コマンドを変換
                translateCommands();
                parser.close();
            }
        }
        
        // コードライターを閉じる
        codeWriter.close();
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("使用法: java vm.VMTranslator <入力.vm または ディレクトリ>");
            System.exit(1);
        }
        
        String inputPath = args[0];
        File input = new File(inputPath);
        VMTranslator translator = new VMTranslator();
        
        try {
            if (input.isDirectory()) {
                translator.translateDirectory(inputPath);
                System.out.println("ディレクトリ変換完了: " + inputPath);
            } else {
                translator.translateFile(inputPath);
                System.out.println("ファイル変換完了: " + inputPath);
            }
        } catch (IOException e) {
            System.err.println("エラー: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 