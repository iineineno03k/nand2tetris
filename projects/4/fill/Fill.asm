// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

//// 疑似コード
//KEYBOARD
//    i = 0
//    j = 0
//    if(RAM[KBD] == 0) goto RESET
//    if(RAM[KBD] != 0) goto BLACK
//
//RESET:
//    if(i == 8225) goto KEYBOARD
//    RAM[SCREEN + i] = 0
//    i = i + 1
//    goto RESET
//
//BLACK:
//    if(j == 8225) goto KEYBOARD
//    RAM[SCREEN + j] = -1
//    j = j + 1
//    goto BLACK

(KEYBOARD)
    // i = 0
    @i
    M=0
    // j = 0
    @j
    M=0
    // if(RAM[KBD] == 0) goto RESET
    @KBD
    D=M
    @RESET
    D;JEQ
    // if(RAM[KBD] != 0) goto BLACK
    @BLACK
    D;JNE
(RESET)
    // if(i == 8225) goto KEYBOARD
    @i
    D=M
    @8225
    D=D-A
    @KEYBOARD
    D;JEQ
    // RAM[SCREEN + i] = 0
    @SCREEN
    D=A
    @i
    D=D+M
    @address
    M=D
    @address
    A=M
    M=0
    // i = i + 1
    @i
    M=M+1
    // goto RESET
    @RESET
    0;JMP                                            
(BLACK)
    // if(j == 8225) goto KEYBOARD
    @j
    D=M
    @8225
    D=D-A
    @KEYBOARD
    D;JEQ
    // RAM[SCREEN + j] = -1
    @SCREEN
    D=A
    @j
    D=D+M
    @address
    M=D
    @address
    A=M
    M=-1
    // j = j + 1
    @j
    M=M+1
    // goto BLACK
    @BLACK
    0;JMP