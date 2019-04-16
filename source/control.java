// pipelining without forwarding
import java.io.*;
import java.util.*;
// import java.nio.*;
import java.lang.Math;

public class control{ 
    // static String IR = "";
    // static int ra, rb, rd, rm, pc_value = 0, immediate, pc_temp;
    static int PC = 0;
    static boolean condition_signal_beq = false; // control unit must set these accordingly
    static boolean condition_signal_bge = false; // control unit must set these accordingly
    static boolean condition_signal_bgeu = false; // control unit must set these accordingly
    static boolean condition_signal_blt = false; // control unit must set these accordingly
    static boolean condition_signal_bne = false; // control unit must set these accordingly
    static boolean condition_signal_bltu = false; // control unit must set these accordingly
    // static int ry, rz;
    static int muxA,muxB, muxY, muxPc, muxMa, muxInc;
    // static int memoryData;
    static register_file register_file_object = new register_file();
    static instructions instruction_object;
    static memory memory_object = new memory();
    static number_to_instrucions_function control_and_name_of_instruction = new number_to_instrucions_function();;
    static PC pc_object = new PC();
    static int line_no;
    static stageTwoDecode decoder_object = new stageTwoDecode(); 
    // static int which_instruction;
    static controlUnit controlUnitObject = new controlUnit();
    static Buffer IF = new Buffer();
    static Buffer ID = new Buffer();
    static Buffer EX = new Buffer();
    static int stall_counter = 0;
    static Buffer MEM = new Buffer();

    // control(){
    //     pc_value = 0;
    //     IR = "";
    //     register_file register_file_object = new register_file();
    //     memory memory_object = new memory();
    //     PC pc_object = new PC();
    //     instructions instruction_object = new instructions(pc_object);  
    //     number_to_instrucions_function control_and_name_of_instruction = new number_to_instrucions_function();
    //     control_and_name_of_instruction.set_number_to_instrucions_function();
    //     stageTwoDecode decoder_object = new stageTwoDecode();
    // }



    // seperate line_no and machine code.
    
    static String seperate_line_and_machineCode(String line){
        char[] char_line = line.toCharArray();  // convert String to char_array
        int i = 0;
        String s = "";
        while(char_line[i] != ' '){
            s += char_line[i];
            i++;    
        }
        line_no = Integer.parseInt(s);
        while(char_line[i] == ' '){             // ignoring the spaces
            i++;
        } 
        int j = 0;
        char[] instruction_for_decoder = new char[32];
        int length_line = line.length();
        length_line -= i;                       // length of line to be read(if correct then 32 bit)
        while(j < length_line){
            instruction_for_decoder[j] = char_line[i];
            i++;j++;
        }
        line = new String(instruction_for_decoder);     // convert charArray to String
        return line;
    }

    // static String convert_machine_code_line_to_instruction(String line){
    //     String[] s = line.split(" ");
    //     line_no = Integer.valueOf(s[0]);
    //     return s[1];
    // }

    static void setMuxValues(Buffer a){
        change_conditional_signal(a.which_instruction);
        if(controlUnitObject.pcSelect == 1)
            pc_object.muxPc = PC;
        if(controlUnitObject.pcSelect == 0)
            pc_object.muxPc = a.ra;
        if(controlUnitObject.maSelect == 0)
            muxMa = a.rz;
        if(controlUnitObject.maSelect == 1)
            muxMa = PC;
        if(controlUnitObject.incSelect == 0)
            pc_object.muxInc = 4;
        if(controlUnitObject.incSelect == 1)
            pc_object.muxInc = a.immediate;
        if(controlUnitObject.bSelect == 0)
            muxB = a.rb;
        if(controlUnitObject.bSelect == 1)
            muxB = a.immediate;
        if(controlUnitObject.ySelect == 0)
            a.ry = a.rz;
        if(controlUnitObject.ySelect == 1)
            a.ry = a.memoryData;
        if(controlUnitObject.ySelect == 2)
            a.ry = pc_object.pc_temp - 4;
        if(controlUnitObject.aSelect==0)
            muxA=a.ra;
        if(controlUnitObject.aSelect==1)
            muxA=PC-4;              //auipc
        if(controlUnitObject.aSelect==2)
            muxA=0; //lui
    }


    static Buffer fetch(){
        controlUnitObject.stage1();
        Buffer a = new Buffer();
        setMuxValues(a);
        a.immediate = 0;
        a.IR = "";
        for(int i = 0; i < 4; i++){
            int val = memory_object.loadByte(PC);
            String currentBinary = Integer.toBinaryString(256 + val);
            String s = currentBinary.substring(currentBinary.length() - 8);
            a.IR = a.IR + s;
            PC++; 
        }
        return a;
    }

    static void change_conditional_signal(int which_instruction){
        switch(which_instruction){
            case 30: condition_signal_beq = true;
                break;
            case 31: condition_signal_bge = true;
                break;
            case 32: condition_signal_bgeu = true;
                break;
            case 33: condition_signal_blt = true;
                break;
            case 34: condition_signal_bne = true;
                break;
            case 35: condition_signal_bltu = true;
        }
    }



    static Buffer decoder(Buffer a){
        controlUnitObject.stage2();
        setMuxValues(a);
            
        ArrayList<Integer> rs1_rs2_rd_immediate_n =  decoder_object.decode(a.IR);   
        System.out.println(rs1_rs2_rd_immediate_n);
        // System.out.println(rs1_rs2_rd_immediate_n.get(1));
        
        try{
            a.which_instruction = rs1_rs2_rd_immediate_n.get(4);
            //a.which_instruction = a.which_instruction;
        }catch(Exception e){}

        try{
            a.ra = register_file_object.load_from_register(rs1_rs2_rd_immediate_n.get(0));
            a.rs1 = rs1_rs2_rd_immediate_n.get(0);
        }catch(Exception e){}

        try{
            a.rb = register_file_object.load_from_register(rs1_rs2_rd_immediate_n.get(1));
            a.rs2 = rs1_rs2_rd_immediate_n.get(1);
            
        }catch(Exception e){}
    
        try{
            a.rd = rs1_rs2_rd_immediate_n.get(2);
        }catch(Exception e){}
        
        try{
            a.immediate = rs1_rs2_rd_immediate_n.get(3);
        }catch(Exception e){}
        
        // System.out.println(ra + " "+rb+ " "+rd+ " "+immediate);
        return a;   
    }


    /*
        kept ry and rz as string and if function return intger convert it to String.
    */
    static Buffer ALU(Buffer a){
        a.rm = a.rb;
        controlUnitObject.setInstruction(a.which_instruction);
        controlUnitObject.stage3();
        setMuxValues(a);
            
        if(a.which_instruction == 1 || a.which_instruction == 10 || (a.which_instruction >= 13 && a.which_instruction <= 17) || (a.which_instruction >= 27 && a.which_instruction <= 29)){
            a.rz = instruction_object.add(muxA, muxB);
        }    
        else if(a.which_instruction == 37){
            a.rz = instruction_object.mul(muxA, muxB);
        }  
        else if(a.which_instruction == 38){
            a.rz = instruction_object.div(muxA, muxB);
        }   
        else if(a.which_instruction == 2 || a.which_instruction == 11){
            a.rz = instruction_object.and(muxA, muxB);
            
        }
        else if(a.which_instruction == 3 || a.which_instruction == 18){
            a.rz = instruction_object.or_(muxA, muxB);
            
        }
        else if(a.which_instruction == 4 || a.which_instruction == 19){
            a.rz = instruction_object.sll(muxA, muxB);
            
        }
        else if(a.which_instruction == 5 || a.which_instruction == 20){
            a.rz = instruction_object.slt(muxA, muxB);
            
        }
        else if(a.which_instruction == 6 || a.which_instruction == 21){
            a.rz = instruction_object.sltu(muxA, muxB);
            
        }
        else if(a.which_instruction == 7 || a.which_instruction == 22){
            a.rz = instruction_object.sra(muxA, muxB);   
        }
        else if(a.which_instruction == 8){
            a.rz = instruction_object.sub(muxA, muxB);
        }
        else if(a.which_instruction == 9 || a.which_instruction == 24){
            a.rz = instruction_object.xor(muxA, muxB);
        }
        else if(a.which_instruction == 12){   // jalr
            // register_file_object.store_in_register(a.rd, PC);
            PC = pc_object.adder(PC);
            // PC -= 4;
            // PC -= stall_counter*4;
        }
        if(a.which_instruction == 36){   // jal
            // register_file_object.store_in_register(a.rd, PC);
            PC = pc_object.adder(PC);
            if(MEM.which_instruction >= 30 && MEM.which_instruction <= 35){
                PC-=4;
            }
            else
            PC -= 8;
            //  PC -= stall_counter*4;
        }
        // where is srl in a.which_instruction
        else if(a.which_instruction == 23){
            a.rz = instruction_object.srl(muxA, muxB);
        }
        else if(a.which_instruction == 25 || a.which_instruction == 26){
            // give pc to ra and immediate value to muxB
            // ALU will do the 12 bit shifting for you
            a.rz = instruction_object.wide_immediate_addition(muxA, muxB) -4;;
            
        }
        else if(a.which_instruction == 30){
            if(condition_signal_beq==true){
                if(a.ra == a.rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues(a);
                PC = pc_object.adder(PC);
                PC -= 8;
            }
        }
        else if(a.which_instruction == 31){
            if(condition_signal_bge==true){
                if(a.ra >= a.rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues(a);
                PC = pc_object.adder(PC);
                PC -= 8;
            }
        }
        else if(a.which_instruction == 32){
            if(condition_signal_bgeu==true){
                if(a.ra >= a.rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues(a);
                PC = pc_object.adder(PC);
                PC -= 8;
            }
        }
        else if(a.which_instruction == 33){
            if(condition_signal_blt==true){
                if(a.ra < a.rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues(a);
                PC = pc_object.adder(PC);
                PC -= 8;
            }
        }
        else if(a.which_instruction == 34){
            if(condition_signal_bne==true){
                if(a.ra != a.rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues(a);
                PC = pc_object.adder(PC);
                PC -= 8;
            }
        }
        else if(a.which_instruction == 35){
            if(condition_signal_bltu==true){
                if(a.ra < a.rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues(a);
                PC = pc_object.adder(PC);
                PC -= 8;
            }
        }
        return a;
    }

    static int calculateTarget(Buffer a){
        if(a.which_instruction == 30){
            if(a.ra == a.rb)
                return a.immediate+PC;
            else
                return PC;
        }
        else if(a.which_instruction == 31){
            if(a.ra >= a.rb)
                return a.immediate+PC;
            else
                return PC;
        }
        else if(a.which_instruction == 32){
            if(a.ra >= a.rb)
                return a.immediate+PC;
            else
                return PC;
        }
        else if(a.which_instruction == 33){
            if(a.ra < a.rb)
                return a.immediate+PC;
            else
                return PC;
        }
        else if(a.which_instruction == 34){
            if(a.ra != a.rb)
                return a.immediate+PC;
            else
                return PC;
        }
        else if(a.which_instruction == 35){
            if(a.ra < a.rb)
                return a.immediate+PC;
            else
                return PC;
        }
        // }
        // if
        return 0;
    }

    static Buffer memory_read_write(Buffer a){
        setMuxValues(a);         // for muxMa
        // System.out.println("----------------"+a.rm+"-----------------");
        controlUnitObject.stage4();
        // System.out.println("----------------"+a.rm+"-----------------");
        setMuxValues(a);
        // System.out.println("----------------"+a.rm+"-----------------");

        // using muxMa
        if(a.which_instruction == 13){
            a.memoryData = memory_object.loadByte(muxMa);
        }
        if(a.which_instruction == 14){
            a.memoryData = memory_object.loadWord(muxMa);
        }
        if(a.which_instruction == 27){
            System.out.println("---------Storing ------------- "+a.rm);
            memory_object.storeDataByte(a.rm, muxMa);
        }
        if(a.which_instruction == 29){
            System.out.println("---------Storing ------------- "+a.rm);
            memory_object.storeDataWord(a.rm, muxMa);
        }   
        return a;
    }


    static void writeBack(Buffer a){
        controlUnitObject.stage5();
        setMuxValues(a);
        // System.out.println(" a.which_instruction "+a.which_instruction + " a.rd " + a.rd);
            
        if(controlUnitObject.rfWrite == 1)
            register_file_object.store_in_register(a.rd, a.ry);
    }


    static void storing_in_memory(String line_input){
        char[] line = line_input.toCharArray();
        for(int i = 0; i < 4; i++){
            int out = 0;
            for(int j = 8*i + 7; j >= 8*i; j--){
                int current_no = 0;
                if(line[j] == '0')
                    current_no = 0;
                else
                    current_no = 1;
                out += current_no * Math.pow(2, (8*i+7) - j);
            }
            memory_object.storeByte(out);
        }
    }

    public static void print(int flag, Scanner read){
        if(flag == 2){
            System.out.println("Print instructions in pipeline -> 1");
            int option = read.nextInt();
            if(option == 1){
                System.out.println(" --- IF");
                System.out.println(" ---- ID " + control_and_name_of_instruction.get_control_unit_values(ID.which_instruction)  + " " + ID.rs1 + " "+ ID.rs2 + " " + ID.rd + " " + ID.immediate);
                System.out.println(" ---- EX " + control_and_name_of_instruction.get_control_unit_values(EX.which_instruction) + " " + EX.rs1 + " "+ EX.rs2 + " " + EX.rd + " " + EX.immediate);
                System.out.println(" ----- MEM " + control_and_name_of_instruction.get_control_unit_values(MEM.which_instruction) + " " + MEM.rs1 + " "+ MEM.rs2 + " " + MEM.rd + " " + MEM.immediate);

            }
        }
    }

    public static void main(String args[]){
        lexical ltemp = new lexical();
        ltemp.run(memory_object);
        System.out.println("-------Data Memory--------\n");
            memory_object.printDataMemory();
        instruction_object = new instructions(pc_object);
        // int address_c, address_b, address_a, rz, rm;
        control_and_name_of_instruction.set_number_to_instrucions_function();
        BufferedReader file_reader;
        register_file_object.store_in_register(2,memory_object.stack_start);
        Scanner read = new Scanner(System.in);
        try{
            file_reader = new BufferedReader(new FileReader("./converted.mc"));    
            String line = "";
            
            while((line = file_reader.readLine()) !=null){
                if(line.length() == 0)
                    continue;
                line = seperate_line_and_machineCode(line);
                System.out.println(line+" ");
                storing_in_memory(line);
            }
        }
        catch(IOException e){
            System.out.println("You either deleted or shifted the file containing machine code");
        }
        
         System.out.println("Added to memory");
        // Buffer IF = new Buffer();
        // Buffer ID = new Buffer();
        // Buffer EX = new Buffer();
        // Buffer MEM = new Buffer();
        
        // Scanner read = new Scanner(System.in);
       
        int flag = 1;
        System.out.println("Run(1) or Step(2)");
        int a = read.nextInt();
        if(a == 1)
            flag = 1;
        else
            flag = 2;
        

        
        int cycle_counter=0;
        IF = fetch();

        int ex_stall=0,mem_stall=0;
        // System.out.println("IF");
        // System.out.println("ID " + ID.which_instruction);
        // System.out.println("EX " + EX.which_instruction);
        // System.out.println("MEM " + MEM.which_instruction);
        while(PC < memory_object.code_start + 4*20){
            writeBack(MEM);
          
            MEM = memory_read_write(EX);    // for ry
          
            EX = ALU(ID);

            ID = decoder(IF);
            
            if((ID.rs1 == EX.rd&&ID.rs1!=0)||(ID.rs2 == EX.rd&&ID.rs2!=0)){
                // IF = fetch();
                stall_counter+=2;
                System.out.println("entered sec");
                
                print(flag, read);
                cycle_counter+=2;
                writeBack(MEM);
                // System.out.println("WHAT THE FUCKWHAT THE FUCKWHAT THE FUCKWHAT THE FUCKWHAT THE FUCKWHAT THE FUCKWHAT THE FUCKWHAT THE FUCKWHAT THE FUCKWHAT THE FUCK");
                MEM = memory_read_write(EX);    // for ry
                
                print(flag, read);

                writeBack(MEM); 

                ID = decoder(IF);
            }
            else if((ID.rs1 == MEM.rd&&ID.rs1!=0)||(ID.rs2 == MEM.rd&&ID.rs2!=0) ){
                // mem_stall=1;
                System.out.println("entered first");
                stall_counter++;
                cycle_counter++;
                print(flag, read);
                writeBack(MEM); 
                ID = decoder(IF);
                // continue;
            }
            if(ID.which_instruction >= 30 && ID.which_instruction <= 35){
                IF = fetch();
                ID.branch_next_pc = calculateTarget(ID);
                if(ID.branch_next_pc == PC){
                    continue;
                }
                else{
                    // IF = fetch();
                    print(flag,read);
                    writeBack(MEM);     
                    MEM = memory_read_write(EX);    // for ry
                    EX = ALU(ID);
                    ID = decoder(IF);
                    ID.rd = 0;
                    ID.which_instruction = 10;
                    ID.rs1 = 0;
                    ID.rs2 = 0;
                    ID.ra = 0;
                    ID.rb = 0;
                }
            }
            else if(ID.which_instruction == 36 || ID.which_instruction == 12){
                //System.out.println("jal khbkbv");
                IF = fetch();
                // register_file_object.printRegisterFile();
                print(flag,read);
                writeBack(MEM); 
                // register_file_object.printRegisterFile();
                MEM = memory_read_write(EX);    // for ry
                EX = ALU(ID);
                ID = decoder(IF);
                ID.rd = 0;
                ID.which_instruction = 10;
                ID.rs1 = 0;
                ID.rs2 = 0;
                ID.ra = 0;
                ID.rb = 0;
            }

            IF = fetch();
            print(flag, read);
            register_file_object.printRegisterFile();
        }
        register_file_object.printRegisterFile();
        System.out.println("Print Final TextMemory(1) DataMemory(2) Both(3)");
        int c = read.nextInt();
        if(c == 1)
            memory_object.printTextMemory();
        if(c==2)
            memory_object.printDataMemory();
        if(c == 3){
            System.out.println("-------Text Memory--------\n");
            memory_object.printTextMemory();

            System.out.println("-------Data Memory--------\n");
            memory_object.printDataMemory();
        }
    }
}
