
import java.util.*;
import java.io.*;

class Data{
    Data(String op,String f7,String f3){
        opcode = op;
        func7 = f7;
        func3 = f3;
    }
    String opcode;
    String func3;
    String func7;
}

public class lexical{
    private static Map<String,Data> ins = new HashMap<String,Data>();

    private static void initialize(){
        ins.put("add",new Data("0110011","0000000","000"));
        ins.put("and",new Data("0110011","0000000","111"));
        ins.put("or",new Data("0110011","0000000","110"));
        ins.put("sll",new Data("0110011","0000000","001"));
        ins.put("slt",new Data("0110011","0000000","010"));
        ins.put("sltu",new Data("0110011","0000000","011"));
        ins.put("sra",new Data("0110011","0100000","101"));
        ins.put("srl",new Data("0110011","0000000","101"));
        ins.put("sub",new Data("0110011","0100000","000"));
        ins.put("xor",new Data("0110011","0000000","100"));
        ins.put("addi",new Data("0010011","","000"));
        ins.put("andi",new Data("0010011","","111"));
        ins.put("jalr",new Data("1100111","","000"));
        ins.put("lb",new Data("0000011","","000"));
        ins.put("lw",new Data("0000011","","010"));
        ins.put("lh",new Data("0000011","","001"));
        ins.put("lhu",new Data("0000011","","101"));
        ins.put("lbu",new Data("0000011","","100"));
        //  ins.put("lwu",new Data(("0010011","","000"));
        ins.put("ori",new Data("0010011","","110"));
        ins.put("slli",new Data("0010011","","001"));
        ins.put("slti",new Data("0010011","","010"));
        ins.put("sltiu",new Data("0010011","","011"));
        ins.put("srai",new Data("0010011","","101"));
        ins.put("srli",new Data("0010011","","101"));
        ins.put("xori",new Data("0010011","","100"));
//        ins.put("la",new Data(("0010011","","000"));
        ins.put("auipc",new Data("0010111","",""));
        ins.put("lui",new Data("0110111","",""));
        ins.put("sb",new Data("0100011","","000"));
        ins.put("sh",new Data("0100011","","001"));
        ins.put("sw",new Data("0100011","","010"));
        ins.put("beq",new Data("1100011","","000"));
        ins.put("bge",new Data("1100011","","101"));
        ins.put("bgeu",new Data("1100011","","111"));
        ins.put("blt",new Data("1100011","","100"));
        ins.put("bne",new Data("1100011","","001"));
        ins.put("bltu",new Data("1100011","","110"));
        ins.put("jal",new Data("1101111","",""));
        ins.put("mul",new Data("0110011","0000001","000"));
        ins.put("div",new Data("0110011","0000001","100"));
    }

    private static String registerToBits(String reg){
        int val = 0;
        for(int i=1;i<reg.length();i++){
            val = val*10 + Character.getNumericValue(reg.charAt(i));
        }
        if(val<0 || val>31){
            System.out.println("Register value is out of range !");
            return null;
        }
        StringBuffer ans =  new StringBuffer(5);
        for(int i=0;i<5;i++){
            if(val%2==0){
                ans.append('0');
            }
            else{
                ans.append('1');
            }
            val /= 2;
        }
        ans.reverse();
        return ans.toString();
    }

    private static String findTwoscomplement(StringBuffer str)
    {
        int n = str.length();
        int i;
        for (i = n-1 ; i >= 0 ; i--)
            if (str.charAt(i) == '1')
                break;
        if (i == -1)
            return "1" + str;
        for (int k = i-1 ; k >= 0; k--)
        {
            if (str.charAt(k) == '1')
                str.replace(k, k+1, "0");
            else
                str.replace(k, k+1, "1");
        }
        return str.toString();
    }

    private static String immediateToBits(String str,int length){  // accepts a +ve decimal value and returns converted binary string
        StringBuffer ans = new StringBuffer(length);
        int val = 0,start = 0;
        if(str.charAt(0) == '-') start = 1;
        for(int i=start;i<str.length();i++){
            val = val*10 + Character.getNumericValue(str.charAt(i));
        }
        for(int i=0;i<length;i++){
            if(val%2==0){
                ans.append('0');
            }
            else{
                ans.append('1');
            }
            val /= 2;
        }
        ans.reverse();
        if(start == 1) return findTwoscomplement(ans);
        return ans.toString();
    }


    private static String decode_R(Yylex lexer,String instruction){
        StringBuffer output = new StringBuffer(32);
        Data d = ins.get(instruction);
        try {
            lexer.yylex();
            String rd = registerToBits(lexer.yytext());
            lexer.yylex();
            String rs1 = registerToBits(lexer.yytext());
            lexer.yylex();
            String rs2 = registerToBits(lexer.yytext());
            output.append(d.func7);
            output.append(rs2);
            output.append(rs1);
            output.append(d.func3);
            output.append(rd);
            output.append(d.opcode);
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
        return output.toString();
    }

    private static String decode_I(Yylex lexer,String instruction){
        StringBuffer output = new StringBuffer(32);
        Data d = ins.get(instruction);
        try {
            lexer.yylex();
            String rd = registerToBits(lexer.yytext());
            lexer.yylex();
            String rs1 = registerToBits(lexer.yytext());
            if(rs1==null){
                System.out.println(instruction);
            }
            lexer.yylex();
            String imm = immediateToBits(lexer.yytext(),12);
            output.append(imm);
            output.append(rs1);
            output.append(d.func3);
            output.append(rd);
            output.append(d.opcode);
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
        return output.toString();
    }

    private static String decode_U(Yylex lexer,String instruction){
        StringBuffer output = new StringBuffer(32);
        Data d = ins.get(instruction);
        try {
            lexer.yylex();
            String rd = registerToBits(lexer.yytext());
            lexer.yylex();
            String imm = immediateToBits(lexer.yytext(),20);
            output.append(imm);
            output.append(rd);
            output.append(d.opcode);
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
        return output.toString();
    }

    private static String decode_S(Yylex lexer,String instruction){
        System.out.println(lexer.yytext());
        System.out.println(lexer.yylineno());
        StringBuffer output = new StringBuffer(32);
        Data d = ins.get(instruction);
        try {
            lexer.yylex();
            System.out.println(lexer.yytext());
            String rs2 = registerToBits(lexer.yytext());
            lexer.yylex();
            String rs1 = registerToBits(lexer.yytext());
            lexer.yylex();
            String imm = immediateToBits(lexer.yytext(),12);
            System.out.println(rs2+" "+rs1);
            output.append(imm.substring(0,7));
            output.append(rs2);
            output.append(rs1);
            output.append(d.func3);
            output.append(imm.substring(7));
            output.append(d.opcode);
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
        return output.toString();
    }

    private static String decode_UJ(Yylex lexer,String instruction){
        StringBuffer output = new StringBuffer(32);
        Data d = ins.get(instruction);
        try {
            lexer.yylex();
            String rd = registerToBits(lexer.yytext());
            lexer.yylex();
            String imm = immediateToBits(lexer.yytext(),21);
            output.append(imm.charAt(0));
            output.append(imm.substring(10,20));
            output.append(imm.charAt(9));
            output.append(imm.substring(1,9));
            output.append(rd);
            output.append(d.opcode);
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
        return output.toString();
    }

    private static String decode_SB(Yylex lexer,String instruction){
        StringBuffer output = new StringBuffer(32);
        Data d = ins.get(instruction);
        try {
            lexer.yylex();
            String rs1 = registerToBits(lexer.yytext());
            lexer.yylex();
            String rs2 = registerToBits(lexer.yytext());
            lexer.yylex();
            String imm = immediateToBits(lexer.yytext(),13);
//            System.out.println(imm);
            output.append(imm.charAt(0));
            output.append(imm.substring(2,8));
            output.append(rs2);
            output.append(rs1);
            output.append(d.func3);
            output.append(imm.substring(8,12));
            output.append(imm.charAt(1));
            output.append(d.opcode);
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
        return output.toString();
    }

    public void run(memory mem_obj) {
        int line = 0;
        initialize();
        preLexical.prerun(mem_obj);
        try {
            File file = new File("./modified.asm");
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter("./converted.mc"));
            Yylex lexer = new Yylex(br);
            int n = 0;
            while (n >= 0) {
                if (n == 3) {
                    String decoded = decode_R(lexer, lexer.yytext());
                    writer.write(Integer.toString(line));
                    writer.write(" ");
                    writer.write(decoded);
                    writer.write("\n");
                    int k = lexer.yylex();
                    if(k==18 || k==0)
                        line ++;
                }
                if (n == 4) {
                    String decoded = decode_I(lexer, lexer.yytext());
                    writer.write(Integer.toString(line));
                    writer.write(" ");
                    writer.write(decoded);
                    writer.write("\n");
                    int k = lexer.yylex();
                    if(k==18 || k==0)
                        line ++;
                }
                if (n == 5) {
                    String decoded = decode_U(lexer, lexer.yytext());
                    writer.write(Integer.toString(line));
                    writer.write(" ");
                    writer.write(decoded);
                    writer.write("\n");
                    int k = lexer.yylex();
                    if(k==18 || k==0)
                        line ++;
                }
                if (n == 6) {
                    String decoded = decode_S(lexer, lexer.yytext());
                    writer.write(Integer.toString(line));
                    writer.write(" ");
                    writer.write(decoded);
                    writer.write("\n");
                    int k = lexer.yylex();
                    if(k==18 || k==0)
                        line ++;
                }
                if (n == 7) {
                    String decoded = decode_SB(lexer, lexer.yytext());
                    writer.write(Integer.toString(line));
                    writer.write(" ");
                    writer.write(decoded);
                    writer.write("\n");
                    int k = lexer.yylex();
                    if(k==18 || k==0)
                        line ++;
                }
                if (n == 8) {
                    String decoded = decode_UJ(lexer, lexer.yytext());
                    writer.write(Integer.toString(line));
                    writer.write(" ");
                    writer.write(decoded);
                    writer.write("\n");
                    int k = lexer.yylex();
                    if(k==18 || k==0)
                        line ++;
                }
                n = lexer.yylex();
            }
            writer.close();
        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
    }
}