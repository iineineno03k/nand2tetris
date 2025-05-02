package vm.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * VMコマンドをパースする
 */
public class Parser {
    private BufferedReader reader;
    private String currentCommand;
    private String currentLine;
    private boolean hasMoreLines;
    
    // コマンドタイプ
    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    
    /**
     * 入力ファイルを開き、パースする準備をする
     */
    public Parser(String filename) throws IOException {
        reader = new BufferedReader(new FileReader(filename));
        currentCommand = null;
        hasMoreLines = true;
        readNextLine(); // 最初の行を読み込む
    }
    
    /**
     * 入力に次のコマンドが存在するかを返す
     */
    public boolean hasMoreLines() {
        return hasMoreLines;
    }
    
    /**
     * 入力から次のコマンドを読み、それを現在のコマンドとする
     */
    public void advance() throws IOException {
        if (currentLine != null && !currentLine.isEmpty()) {
            currentCommand = currentLine;
        }
        readNextLine();
    }
    
    /**
     * 次の有効な行を読み込む
     */
    private void readNextLine() throws IOException {
        currentLine = null;
        
        while (true) {
            String line = reader.readLine();
            
            if (line == null) {
                hasMoreLines = false;
                return;
            }
            
            // コメントと空白行を無視
            line = line.trim();
            int commentStart = line.indexOf("//");
            if (commentStart != -1) {
                line = line.substring(0, commentStart).trim();
            }
            
            if (!line.isEmpty()) {
                currentLine = line;
                return;
            }
        }
    }
    
    /**
     * 現在のコマンドの種類を返す
     */
    public int commandType() {
        if (currentCommand == null) {
            return -1;
        }
        
        String[] parts = currentCommand.split("\\s+");
        String command = parts[0];
        
        if (command.equals("push")) {
            return C_PUSH;
        } else if (command.equals("pop")) {
            return C_POP;
        } else if (isArithmeticCommand(command)) {
            return C_ARITHMETIC;
        }
        
        return -1;
    }
    
    private boolean isArithmeticCommand(String command) {
        return command.equals("add") || command.equals("sub") || 
               command.equals("neg") || command.equals("eq") || 
               command.equals("gt") || command.equals("lt") || 
               command.equals("and") || command.equals("or") || 
               command.equals("not");
    }
    
    /**
     * 現在のコマンドの最初の引数を返す
     * C_ARITHMETICの場合、コマンド自体（add, subなど）が返される
     */
    public String arg1() {
        if (commandType() == C_ARITHMETIC) {
            return currentCommand.split("\\s+")[0];
        }
        
        String[] parts = currentCommand.split("\\s+");
        if (parts.length >= 2) {
            return parts[1];
        }
        
        return null;
    }
    
    /**
     * 現在のコマンドの2番目の引数を返す
     * C_PUSH、C_POPコマンドで使用される
     */
    public int arg2() {
        String[] parts = currentCommand.split("\\s+");
        if (parts.length >= 3) {
            return Integer.parseInt(parts[2]);
        }
        
        return -1;
    }
    
    /**
     * リソースを閉じる
     */
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
} 