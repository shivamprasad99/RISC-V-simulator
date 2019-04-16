import java.io.*;
import java.util.*;


public class preLexical {

    private static Map<String,Integer> labelMap =new HashMap<String, Integer>();
    private static HashMap<String,Integer> variables = new HashMap<String,Integer>();
    private static int indexer = 0;

    public static void preprocessor(String filename,memory mem_obj){   // will need to pass a memory object to this function to store the data values in memory
        // also need to maintain a pc counter while preprocessing to enable labeled lw command
        try {
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            Yylex lexer = new Yylex(br);
            BufferedWriter wr = new BufferedWriter((new FileWriter(new File("./preprocessed.asm"))));
            int nToken = lexer.yylex();
            while(nToken>=0){
                if(nToken == 18 || nToken == 0) {
                    wr.write("\n");
                    while (nToken == 18 || nToken == 0) {
                        nToken = lexer.yylex();
                    }
                }
                if(nToken == 16){
                    wr.write(lexer.yytext());
                    wr.write(" ");
                    nToken = lexer.yylex();
                    while(nToken == 18 || nToken == 0){
                        nToken = lexer.yylex();
                    }
                }
                if(nToken == 2 && lexer.yytext().equals(".data")){
                    System.out.println("Entered .data");
                    nToken = lexer.yylex();
                    while(nToken == 18)
                        nToken = lexer.yylex();
                    while(nToken==16){
                        variables.put(lexer.yytext().substring(0,lexer.yytext().length()-1),indexer);
                        nToken = lexer.yylex();
                        if(nToken == 10){
                            nToken = lexer.yylex();
                            while(nToken==13 || nToken==11){
                                // store in memory here
                                if(nToken==11)
                                    mem_obj.storeInitialWord(Integer.parseInt(lexer.yytext().substring(2),16));
                                if(nToken==13){
                                    System.out.println(" to store " + Integer.parseInt(lexer.yytext())+"...");
                                    mem_obj.storeInitialWord(Integer.parseInt(lexer.yytext()));
                                }
                                nToken = lexer.yylex();
                                indexer+=4;
                            }
                        }
                        while(nToken == 18)
                            nToken = lexer.yylex();
                    }
                    while(nToken != 2 && nToken != -1){
                        nToken = lexer.yylex();
                    }
                }
                if(nToken != 18)
                    wr.write(lexer.yytext());
                wr.write(" ");
                nToken = lexer.yylex();
            }
            wr.close();

            wr = new BufferedWriter((new FileWriter(new File("./preprocessed2.asm"))));
            br = new BufferedReader(new FileReader("./preprocessed.asm"));
            lexer = new Yylex(br);
            nToken = lexer.yylex();
            while(nToken>=0){
                if(nToken == 9 && lexer.yytext().equals("la")){
                    String repl = "addi";
                    String reg;
                    nToken = lexer.yylex();
                    repl += " ";
                    repl += lexer.yytext();
                    reg = lexer.yytext();
                    nToken = lexer.yylex();
                    if(nToken == 15){
                        wr.write(repl+" ");
                        String x=lexer.yytext();

                        StringBuffer y=new StringBuffer();
                        int i;
    //                     System.out.println(x);

                        for(i=0;i<x.length();i++){
                            if(x.charAt(i)=='(')
                                break;
                            y.append(x.charAt(i));
                        }
                        i++;
    //                    System.out.println(y);
                        StringBuffer z= new StringBuffer();
                        for(;i<x.length();i++){
                            if(x.charAt(i)==')') break;
                            else z.append(x.charAt(i));
                        }
    //                    System.out.println(z);

                        wr.write(z.append(" ").append(y).toString());
                        wr.write(" ");
                    }
                    else{
                        wr.write("auipc "+reg+" 65536");
                        wr.write("\n");
                        wr.write(repl+" "+lexer.yytext());
                        
                    }
                    nToken = lexer.yylex();
                }
                if(nToken == 4 && lexer.yytext().equals("lw")){
                    String repl = lexer.yytext();
                    String reg;
                    nToken = lexer.yylex();
                    repl += " ";
                    repl += lexer.yytext();
                    reg = lexer.yytext();
                    nToken = lexer.yylex();
                    if(nToken == 15){
                        wr.write(repl+" ");
                        String x=lexer.yytext();

                        StringBuffer y=new StringBuffer();
                        int i;
    //                     System.out.println(x);

                        for(i=0;i<x.length();i++){
                            if(x.charAt(i)=='(')
                                break;
                            y.append(x.charAt(i));
                        }
                        i++;
    //                    System.out.println(y);
                        StringBuffer z= new StringBuffer();
                        for(;i<x.length();i++){
                            if(x.charAt(i)==')') break;
                            else z.append(x.charAt(i));
                        }
    //                    System.out.println(z);

                        wr.write(z.append(" ").append(y).toString());
                        wr.write(" ");
                    }
                    else{
                        wr.write("auipc "+reg+" 65536");
                        wr.write("\n");
                        wr.write(repl+" "+lexer.yytext());
                        
                    }
                    nToken = lexer.yylex();
                }
                wr.write(lexer.yytext());
                wr.write(" ");
                nToken = lexer.yylex();
            }
            wr.close();

            
        }catch(Exception exception){
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
    }

    public static void prerun(memory mem_obj){
        try {
            preprocessor("./test.asm",mem_obj);
            File file = new File("./preprocessed2.asm");
            BufferedReader br = new BufferedReader(new FileReader(file));
            Yylex lexer = new Yylex(br);
            int nToken=lexer.yylex();
            int flag = 0;
            while(nToken>=0) {
                if (nToken == 16) {
                    String removeColon = lexer.yytext();
                    removeColon=removeColon.substring(0,removeColon.length()-1);
                    labelMap.put(removeColon, lexer.yylineno());
                    flag = 1;
                }
                nToken = lexer.yylex();
                if(flag==1 && nToken == 18){
//                    lexer.adjustlineno();
                }
                flag = 0;
            }
            br= new BufferedReader( new FileReader(file));
            lexer=new Yylex(br);
            nToken=lexer.yylex();
            BufferedWriter wr = new BufferedWriter((new FileWriter(new File("./modified.asm"))));
            int programcounter = 0;
            while (nToken>=0){           /* System.out.println(lexer.yytext())*/;
                if(nToken >=3 && nToken <= 8){
                    programcounter += 4;
                }
                if(nToken == 4 && (lexer.yytext().equals("lw")||lexer.yytext().equals("addi"))){
                    String repl = lexer.yytext();
                    String reg,reg1;
                    nToken = lexer.yylex();
                    reg1 = lexer.yytext();
                    repl += " ";
                    repl += lexer.yytext();
                    nToken = lexer.yylex();
                    reg = lexer.yytext();
                    if(nToken == 12){
                        wr.write(repl+" "+reg+" ");
                        nToken = lexer.yylex();
                        wr.write(lexer.yytext());
                    }
                    else{
                        wr.write(repl+" "+reg1+" "+(variables.get(lexer.yytext())-programcounter+8));
                    }
                    nToken = lexer.yylex();
                }
                if(nToken==14){
                    int temp = 0;
                    try {
                        temp = labelMap.get(lexer.yytext()) - lexer.yylineno();
                    }
                    catch ( Exception exception ) {
                        System.out.println( "Exception in Main "+ exception.toString() );
                        exception.printStackTrace();
                    }
//                    System.out.println(temp);
                    wr.write(String.valueOf(temp*4));
                    wr.write(" ");
                }
                else if(nToken==15){
                     String x=lexer.yytext();

                     StringBuffer y=new StringBuffer();
                     int i;
//                     System.out.println(x);

                     for(i=0;i<x.length();i++){
                         if(x.charAt(i)=='(')
                             break;
                         y.append(x.charAt(i));
                     }
                     i++;
//                    System.out.println(y);
                    StringBuffer z= new StringBuffer();
                    for(;i<x.length();i++){
                        if(x.charAt(i)==')') break;
                        else z.append(x.charAt(i));
                    }
//                    System.out.println(z);

                    wr.write(z.append(" ").append(y).toString());
                    wr.write(" ");
                }
                else if(nToken==0){
                    ;
                }
                else if(nToken==18){
                    wr.newLine();
                }
                else if(nToken==19){
                    wr.write(" ");
                }
                else if(nToken==16){
                    //ignore
                }

                else if(nToken==11){
                    String temp=lexer.yytext();
                    temp=temp.substring(2);
                    Integer z= Integer.parseInt(temp,16);
                    wr.write(String.valueOf(z));
                    wr.write(" ");
                }
                else {
                    wr.write(lexer.yytext());
                    wr.write(" ");
                }
                nToken=lexer.yylex();
            }                wr.close();


        }
        catch ( Exception exception ) {
            System.out.println( "Exception in Main "+ exception.toString() );
            exception.printStackTrace();
        }
    }

}
