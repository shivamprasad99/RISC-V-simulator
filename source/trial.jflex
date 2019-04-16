
import java.io.*;
import java.lang.*;
%%

%{
void printf(String s){
    System.out.print(s);
}

public int yylineno(){
    return yyline+1;
}

public void adjustlineno(){
    System.out.println("Entered" + yyline);
    yyline--;
    System.out.println(yyline);
}

%}

rType =                                          add|and|or|sll|slt|sltu|sra|srl|sub|xor|mul|div
iType =                                          addi|andi|jalr|lb|lw|lh|lwu|ori|slli|srli|xori|lbu|lhu|slti|sltiu|srai
uType =                                          auipc|lui
sType =                                          sb|sh|sw
sbType =                                         beq|bge|bgeu|blt|bne|bltu
ujType  =                                        jal
pseudo =                                         la
comment =                                        "#".*[^\n]
%standalone
%public

%%

[\t\, ]+                                        {/* ignore whitespace */;}
{comment}                                       {/*printf("Comment ");*/yylex();yyline--;return 0;}
:                                               {/*printf("Colon ");*/return 1;}
[\n]+                                           {yyline++;/*printf("\n");*/return 18;}  /*new change*/
".data"|".text"                                 {/*printf("Directive ");*/return 2;}
{rType}                                         {/*printf("Instruction ");*/return 3;}
{iType}                                         {/*printf("Instruction ");*/return 4;}
{uType}                                         {/*printf("Instruction ");*/return 5;}
{sType}                                         {/*printf("Instruction ");*/return 6;}
{sbType}                                        {/*printf("Instruction ");*/return 7;}
{ujType}                                        {/*printf("Instruction ");*/return 8;}
{pseudo}                                        {/*printf("Pseudo ");*/return 9;}
".word"                                           {/*printf("Datatype ");*/return 10;}
"0x"[0-9a-f]+                                   {/*printf("Hex ");*/return 11;}
"x"[0-9]+                                       {/*printf("Register ");*/return 12;}
[-]?[0-9]+                                      {/*printf("Number ");*/return 13;}
[a-zA-Z0-9_]+(:)                                    {/*printf("LabelSource");*/return 16;}        /* new_change */
[a-zA-Z0-9_]+                                       {/*printf("Label ");*/return 14;}
[0-9]+\([x][0-9]*\)                                {/*printf("Memory ");*/return 15;}
.                                               {/*printf("Unexpected ");*/return 17;}  /* new_change */