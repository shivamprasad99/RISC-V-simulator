import java.io.*;
import java.util.*;
// import java.nio.*;
import java.lang.Math;

public class control2{ 
    static String IR = "";
    static int ra, rb, rd, rm, pc_value = 0, immediate, pc_temp;
    static boolean condition_signal_beq = false; // control unit must set these accordingly
    static boolean condition_signal_bge = false; // control unit must set these accordingly
    static boolean condition_signal_bgeu = false; // control unit must set these accordingly
    static boolean condition_signal_blt = false; // control unit must set these accordingly
    static boolean condition_signal_bne = false; // control unit must set these accordingly
    static boolean condition_signal_bltu = false; // control unit must set these accordingly
    static int ry, rz;
    static int muxA,muxB, muxY, muxPc, muxMa, muxInc;
    static int memoryData;
    static register_file register_file_object = new register_file();
    static instructions instruction_object;
    static memory memory_object = new memory();
    static number_to_instrucions_function control_and_name_of_instruction = new number_to_instrucions_function();;
    static PC pc_object = new PC();
    static int line_no;
    static stageTwoDecode decoder_object = new stageTwoDecode(); 
    static int which_instruction;
    static controlUnit controlUnitObject = new controlUnit();
    static int control_inst = 0, alu_inst = 0, data_inst = 0;
    
    static direct_mapped_cache inst_cache = new direct_mapped_cache(64,16);
    static direct_mapped_cache data_cache = new direct_mapped_cache(64,16);

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
    static void print_record()throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter("./record.txt"));
        writer.write("Total instructions " + (control_inst + alu_inst + data_inst) + "\n");
        writer.write("Number of Data-transfer instructions "+data_inst+"\n");
        writer.write("Number of ALU instructions " + alu_inst + "\n");
        writer.write("Number of Control instructions " +control_inst+"\n");
        writer.close();
    }
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

    static void setMuxValues(){
        change_conditional_signal(which_instruction);
        if(controlUnitObject.pcSelect == 1)
            pc_object.muxPc = pc_value;
        if(controlUnitObject.pcSelect == 0)
            pc_object.muxPc = ra;
        if(controlUnitObject.maSelect == 0)
            muxMa = rz;
        if(controlUnitObject.maSelect == 1)
            muxMa = pc_value;
        if(controlUnitObject.incSelect == 0)
            pc_object.muxInc = 4;
        if(controlUnitObject.incSelect == 1)
            pc_object.muxInc = immediate;
        if(controlUnitObject.bSelect == 0)
            muxB = rb;
        if(controlUnitObject.bSelect == 1)
            muxB = immediate;
        if(controlUnitObject.ySelect == 0)
            ry = rz;
        if(controlUnitObject.ySelect == 1)
            ry = memoryData;
        if(controlUnitObject.ySelect == 2)
            ry = pc_object.pc_temp;
        if(controlUnitObject.aSelect==0)
            muxA=ra;
        if(controlUnitObject.aSelect==1)
            muxA=pc_value-4;              //auipc
        if(controlUnitObject.aSelect==2)
            muxA=0;             //lui
    }


    static void stage1(){
        immediate = 0;
        IR = "";
        for(int i = 0; i < 4; i++){
            int val = inst_cache.loadByte(pc_value,memory_object);
            String currentBinary = Integer.toBinaryString(256 + val);
            String s = currentBinary.substring(currentBinary.length() - 8);
            IR = IR + s;
            pc_value++; 
        }
        System.out.println(IR);
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



    static void decoder(){
        ArrayList<Integer> rs1_rs2_rd_immediate_n =  decoder_object.decode(IR);   
        System.out.println(rs1_rs2_rd_immediate_n);
        // System.out.println(rs1_rs2_rd_immediate_n.get(1));
        
        try{
            which_instruction = rs1_rs2_rd_immediate_n.get(4);
        }catch(Exception e){}

        try{
            ra = register_file_object.load_from_register(rs1_rs2_rd_immediate_n.get(0));
        }catch(Exception e){
            ra = 0;
        }

        try{
            rb = register_file_object.load_from_register(rs1_rs2_rd_immediate_n.get(1));
            rm = rb;
        }catch(Exception e){}
    
        try{
            rd = rs1_rs2_rd_immediate_n.get(2);
        }catch(Exception e){}
        
        try{
            immediate = rs1_rs2_rd_immediate_n.get(3);
        }catch(Exception e){}
    
        // System.out.println(ra + " "+rb+ " "+rd+ " "+immediate);
    }


    /*
        kept ry and rz as string and if function return intger convert it to String.
    */
    static void ALU(){
        if(which_instruction == 12 || which_instruction == 36 || (which_instruction >= 30 && which_instruction <= 35) )
            control_inst++;
        if((which_instruction >= 13 && which_instruction <= 17) || (which_instruction >= 27 && which_instruction <= 29)){
            data_inst++;
        }
        if(which_instruction == 1 || which_instruction == 10)
            alu_inst++;
        if(which_instruction == 1 || which_instruction == 10 || (which_instruction >= 13 && which_instruction <= 17) || (which_instruction >= 27 && which_instruction <= 29)){
            rz = instruction_object.add(muxA, muxB);
        }  
        else if(which_instruction == 37){
            alu_inst++;
            rz = instruction_object.mul(muxA, muxB);
            System.out.println("---------------rz-----------" + rz);
        }  
        else if(which_instruction == 38){
            alu_inst++;
            rz = instruction_object.div(muxA, muxB);
        }           
        else if(which_instruction == 2 || which_instruction == 11){
            alu_inst++;
            rz = instruction_object.and(muxA, muxB);
            
        }
        else if(which_instruction == 3 || which_instruction == 18){
            alu_inst++;
            rz = instruction_object.or_(muxA, muxB);
            
        }
        else if(which_instruction == 4 || which_instruction == 19){
            alu_inst++;
            rz = instruction_object.sll(muxA, muxB);
            
        }
        else if(which_instruction == 5 || which_instruction == 20){
            alu_inst++;
            rz = instruction_object.slt(muxA, muxB);
            
        }
        else if(which_instruction == 6 || which_instruction == 21){
            alu_inst++;
            rz = instruction_object.sltu(muxA, muxB);
            
        }
        else if(which_instruction == 7 || which_instruction == 22){
            alu_inst++;
            rz = instruction_object.sra(muxA, muxB);
            
        }
        else if(which_instruction == 8){
            alu_inst++;
            rz = instruction_object.sub(muxA, muxB);
        }
        else if(which_instruction == 9 || which_instruction == 24){
            alu_inst++;
            rz = instruction_object.xor(muxA, muxB);
        }
        else if(which_instruction == 12){   // jalr
            register_file_object.store_in_register(rd, pc_value);
            pc_value = pc_object.adder(pc_value);
        }
        if(which_instruction == 36){   // jal
            register_file_object.store_in_register(rd, pc_value);
            pc_value = pc_object.adder(pc_value);
            pc_value -= 4;
        }
        // where is srl in which_instruction
        else if(which_instruction == 23){
            alu_inst++;
            rz = instruction_object.srl(muxA, muxB);
        }
        else if(which_instruction == 25 || which_instruction == 26){
            // give pc to ra and immediate value to muxB
            // ALU will do the 12 bit shifting for you
            alu_inst++;
            rz = instruction_object.wide_immediate_addition(muxA, muxB);
            System.out.println("rz " + rz);
        }
        else if(which_instruction == 30){
            if(condition_signal_beq==true){
                if(ra == rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues();
                pc_value = pc_object.adder(pc_value);
                pc_value -= 4;
            }
        }
        else if(which_instruction == 31){
            if(condition_signal_bge==true){
                if(ra >= rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues();
                pc_value = pc_object.adder(pc_value);
                pc_value -= 4;
            }
        }
        else if(which_instruction == 32){
            if(condition_signal_bgeu==true){
                if(ra >= rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues();
                pc_value = pc_object.adder(pc_value);
                pc_value -= 4;
            }
        }
        else if(which_instruction == 33){
            if(condition_signal_blt==true){
                if(ra < rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues();
                pc_value = pc_object.adder(pc_value);
                pc_value -= 4;
            }
        }
        else if(which_instruction == 34){
            if(condition_signal_bne==true){
                if(ra != rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues();
                pc_value = pc_object.adder(pc_value);
                pc_value -= 4;
            }
        }
        else if(which_instruction == 35){
            if(condition_signal_bltu==true){
                if(ra < rb)
                    controlUnitObject.setMuxInc(1);
                else
                    controlUnitObject.setMuxInc(0);
                setMuxValues();
                pc_value = pc_object.adder(pc_value);
                pc_value -= 4;
            }
        }
    }


    static void memory_read_write(){
        // using muxMa
        if(which_instruction == 13){
            memoryData = data_cache.loadByte(muxMa,memory_object);
        }
        if(which_instruction == 14){
            memoryData = data_cache.loadWord(muxMa,memory_object);
        }
        if(which_instruction == 27){
            System.out.println("---------Storing ------------- "+rm);
            data_cache.storeDataByte(rm, muxMa,memory_object);
        }
        if(which_instruction == 29){
            System.out.println("---------Storing-------------- "+rm);
            data_cache.storeDataWord(rm, muxMa,memory_object);
        }
    }


    static void writeBack(){
        if(controlUnitObject.rfWrite == 1)
            register_file_object.store_in_register(rd, ry);
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
        
        int flag = 1;
        System.out.println("Run(1) or Step(2)");
        int a = read.nextInt();
        if(a == 1)
            flag = 1;
        else
            flag = 2;
            
        while(pc_value < memory_object.code_start){
            controlUnitObject.stage1();
            setMuxValues();
            stage1();
            controlUnitObject.stage2();
            setMuxValues();
            decoder();
            
            controlUnitObject.setInstruction(which_instruction);
            controlUnitObject.stage3();
            setMuxValues();
            ALU();
            setMuxValues();         // for muxMa
            controlUnitObject.stage4();
            setMuxValues();
            memory_read_write();    // for ry

            controlUnitObject.stage5();
            setMuxValues();
            writeBack();
            if(flag == 2){
                System.out.println("Print Reg File -> 1");
                int option = read.nextInt();
                if(option == 1)
                    register_file_object.printRegisterFile();
            }
        }
        try{
            print_record();
        }
        catch(Exception e){}
        System.out.println(data_cache.hits + " miss "  + data_cache.misses + " conflict " + data_cache.conflict_misses + " cold " + data_cache.cold_misses); 
        System.out.println(inst_cache.hits + " miss "  + inst_cache.misses + " conflict " + inst_cache.conflict_misses + " cold " + inst_cache.cold_misses); 
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
        read.close();
    }
}