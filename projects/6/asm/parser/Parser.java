import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    BufferedReader reader;
    String currentCommand = null;
    String nextCommand = null;

    public Parser(String filename) throws IOException {
        reader = new BufferedReader(new FileReader(filename));
        // 最初に次のコマンドを読み込む
        readNextCommand();
        // 最初のコマンドを現在のコマンドに設定
        advance();
    }

    public boolean hasMoreCommands() {
        return currentCommand != null;
    }

    // 次のコマンドを読み込むヘルパーメソッド
    private void readNextCommand() throws IOException {
        nextCommand = null;
        String line;
        while ((line = reader.readLine()) != null) {
            // コメント削除 (//)
            int commentStart = line.indexOf("//");
            if (commentStart != -1) {
                line = line.substring(0, commentStart);
            }

            // 空白削除
            line = line.trim();

            // 空行スキップ
            if (!line.isEmpty()) {
                nextCommand = line;
                break;
            }
        }
    }

    public void advance() throws IOException {
        currentCommand = nextCommand;
        readNextCommand();
    }
    
    // デバッグ用メソッド
    public String getCurrentCommand() {
        return currentCommand;
    }

    public String instructionType() {
        if (currentCommand.startsWith("@")) {
            return "A_COMMAND";
        } else if (currentCommand.startsWith("(") && currentCommand.endsWith(")")) {
            return "L_COMMAND";
        } else {
            return "C_COMMAND";
        }
    }

    public String symbol() {
        if (instructionType().equals("A_COMMAND")) {
            return currentCommand.substring(1);
        } else if (instructionType().equals("L_COMMAND")) {
            return currentCommand.substring(1, currentCommand.length() - 1);
        }
        return "";
    }

    public String dest() {
        if (!instructionType().equals("C_COMMAND"))
            return "";

        int equalPos = currentCommand.indexOf('=');
        if (equalPos == -1)
            return "";

        return currentCommand.substring(0, equalPos);
    }

    public String comp() {
        if (!instructionType().equals("C_COMMAND"))
            return "";

        int equalPos = currentCommand.indexOf('=');
        int semicolonPos = currentCommand.indexOf(';');

        if (equalPos == -1) {
            if (semicolonPos == -1) {
                return currentCommand;
            } else {
                return currentCommand.substring(0, semicolonPos);
            }
        } else {
            if (semicolonPos == -1) {
                return currentCommand.substring(equalPos + 1);
            } else {
                return currentCommand.substring(equalPos + 1, semicolonPos);
            }
        }
    }

    public String jump() {
        if (!instructionType().equals("C_COMMAND"))
            return "";

        int semicolonPos = currentCommand.indexOf(';');
        if (semicolonPos == -1)
            return "";

        return currentCommand.substring(semicolonPos + 1);
    }
}