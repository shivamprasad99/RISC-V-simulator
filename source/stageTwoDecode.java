import java.io.*;
import java.util.*;

public class stageTwoDecode{
		private static Map<String,Data> ins = new HashMap<String,Data>();
		String instructionType="";
		String instruction="";
		public static String getType(String inst){
			String instr = inst.substring(0, 1);
			/*
			System.out.println();
			System.out.println(instr);
			System.out.println();
			*/
			if(instr.equals("I")){ return "I"; }
			if(instr.equals("R")){ return "R"; }
			if(instr.equals("U")){ return "U"; }
			if(instr.equals("J")){ return "UJ"; }
			if(instr.equals("B")){ return "SB"; }
			if(instr.equals("S")){ return "S"; }
			return "";
		}

		public static int getRs1(String inst){
			String strRs1 = inst.substring(12,17);
			int foo = Integer.parseInt(strRs1, 2);
			return foo;
		}
		public static int getRs2(String inst){
			String strRs1 = inst.substring(7,12);
			int foo = Integer.parseInt(strRs1, 2);
			return foo;
		}
		public static int getRd(String inst){
			String strRs1 = inst.substring(20, 25);
			int foo = Integer.parseInt(strRs1, 2);
			return foo;
		}

		static String addBinary(String a, String b) 
		{ 
			
			// Initialize result 
			String result = "";  
			
			// Initialize digit sum 
			int s = 0;          
	
			// Travers both strings starting  
			// from last characters 
			int i = a.length() - 1, j = b.length() - 1; 
			while (i >= 0 || j >= 0 || s == 1) 
			{ 
				
				// Comput sum of last  
				// digits and carry 
				s += ((i >= 0)? a.charAt(i) - '0': 0); 
				s += ((j >= 0)? b.charAt(j) - '0': 0); 
	
				// If current digit sum is  
				// 1 or 3, add 1 to result 
				result = (char)(s % 2 + '0') + result; 
	
				// Compute carry 
				s /= 2; 
	
				// Move to next digits 
				i--; j--; 
			} 
			
		return result; 
		}

		public static int binaryToSigned(String bs){
			if(bs.charAt(0)=='0')
				return Integer.parseInt(bs, 2);
			else{
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<bs.length();i++){
					if(bs.charAt(i)=='0')
						sb.append('1');
					else
						sb.append('0');
				}
				String sum = addBinary(sb.toString(),"1");
				return -1*Integer.parseInt(sum, 2);
			}
		}

		public static int getIm(String insType, String inst){
            if(insType.equals("I")){
					String strRs1 = inst.substring(0, 12);
					int foo = binaryToSigned(strRs1);	// check fo r signed
					return foo;
			}else if(insType.equals("R")){	// dont care
					String strRs1 = inst.substring(0, 12);
					int foo = binaryToSigned(strRs1);
					return foo;
            }else if(insType.equals("UJ")){
				String strRs1 = inst.substring(0,1) + inst.substring(12,20) + inst.substring(11, 12) + inst.substring(1,11);
				int foo = binaryToSigned(strRs1);
				return foo*2;
			}else if(insType.equals("U")){
				String strRs1 = inst.substring(0, 20);
				int foo = Integer.parseInt(strRs1, 2);
				return foo;
			}else if(insType.equals("S")){
				String strRs1 = inst.substring(0, 7)+inst.substring(20, 25);
				int foo = binaryToSigned(strRs1);
				return foo;
			}else if(insType.equals("SB")){
				String strRs1 ="";
				strRs1 = inst.substring(0,1) + inst.substring(24,25) + inst.substring(1,7) + inst.substring(20,24);
				int foo = binaryToSigned(strRs1);
				return foo*2;
			}
			return -1; // wrong input
		}

    	private static void initialize(){
				ins.put("Rmul",new Data("0110011","0000001","000"));
				ins.put("Rdiv",new Data("0110011","0000001","100"));
		        ins.put("Radd",new Data("0110011","0000000","000"));
		        ins.put("Rand",new Data("0110011","0000000","111"));
		        ins.put("Ror",new Data("0110011","0000000","110"));
		        ins.put("Rsll",new Data("0110011","0000000","001"));
		        ins.put("Rslt",new Data("0110011","0000000","010"));
		        ins.put("Rsltu",new Data("0110011","0000000","011"));
		        ins.put("Rsra",new Data("0110011","0100000","101"));
		        ins.put("Rsrl",new Data("0110011","0000000","101"));
		        ins.put("Rsub",new Data("0110011","0100000","000"));
		        ins.put("Rxor",new Data("0110011","0000000","100"));
		        ins.put("Iaddi",new Data("0010011","","000"));
		        ins.put("Iandi",new Data("0010011","","111"));
		        ins.put("Ijalr",new Data("1100111","","000"));
		        ins.put("Ilb",new Data("0000011","","000"));
		        ins.put("Ilw",new Data("0000011","","010"));
		        ins.put("Ilh",new Data("0000011","","001"));
		        ins.put("Ilhu",new Data("0000011","","101"));
		        ins.put("Ilbu",new Data("0000011","","100"));
		        //  ins.put("lwu",new Data(("0010011","","000"));
		        ins.put("Iori",new Data("0010011","","110"));
		        ins.put("Islli",new Data("0010011","","001"));
		        ins.put("Islti",new Data("0010011","","010"));
		        ins.put("Isltiu",new Data("0010011","","011"));
		        ins.put("Israi",new Data("0010011","","101"));
		        ins.put("Isrli",new Data("0010011","","101"));
		        ins.put("Ixori",new Data("0010011","","100"));
		//        ins.put("la",new Data(("0010011","","000"));
		        ins.put("Uauipc",new Data("0010111","",""));
		        ins.put("Ului",new Data("0110111","",""));
		        ins.put("Ssb",new Data("0100011","","000"));
		        ins.put("Ssh",new Data("0100011","","001"));
		        ins.put("Ssw",new Data("0100011","","010"));
		        ins.put("Bbeq",new Data("1100011","","000"));
		        ins.put("Bbge",new Data("1100011","","101"));
		        ins.put("Bbgeu",new Data("1100011","","111"));
		        ins.put("Bblt",new Data("1100011","","100"));
		        ins.put("Bbne",new Data("1100011","","001"));
		        ins.put("Bbltu",new Data("1100011","","110"));
		        ins.put("Jjal",new Data("1101111","",""));
			}

			private int getInstructionNumber(String instruction){
				LinkedHashMap<String, Integer> instruction_to_integer = new LinkedHashMap<String, Integer>();
        		instruction_to_integer.put("add", 1);
                instruction_to_integer.put("and", 2);
                instruction_to_integer.put("or", 3);
                instruction_to_integer.put("sll", 4);
                instruction_to_integer.put("slt", 5);
                instruction_to_integer.put("sltu", 6);
                instruction_to_integer.put("sra", 7);
                instruction_to_integer.put("sub", 8);
                instruction_to_integer.put("xor", 9);
                instruction_to_integer.put("addi", 10);
                instruction_to_integer.put("andi", 11);
                instruction_to_integer.put("jalr", 12);
                instruction_to_integer.put("lb", 13);
                instruction_to_integer.put("lw", 14);
                instruction_to_integer.put("lh", 15);
                instruction_to_integer.put("lhu", 16);
                instruction_to_integer.put("lbu", 17);
                instruction_to_integer.put("ori", 18);
                instruction_to_integer.put("slli", 19);
                instruction_to_integer.put("slti", 20);
                instruction_to_integer.put("sltiu", 21);
                instruction_to_integer.put("srai", 22);
                instruction_to_integer.put("srli", 23);
                instruction_to_integer.put("xori", 24);
                instruction_to_integer.put("auipc", 25);
                instruction_to_integer.put("lui", 26);
                instruction_to_integer.put("sb", 27);
                instruction_to_integer.put("sh", 28);
                instruction_to_integer.put("sw", 29);
                instruction_to_integer.put("beq", 30);
                instruction_to_integer.put("bge", 31);
                instruction_to_integer.put("bgeu", 32);
                instruction_to_integer.put("blt", 33);
                instruction_to_integer.put("bne", 34);
                instruction_to_integer.put("bltu", 35);
				instruction_to_integer.put("jal", 36);
				instruction_to_integer.put("mul",37);
				instruction_to_integer.put("div",38);				
				return instruction_to_integer.get(instruction);

			}

		
		ArrayList<Integer> decode(String input){
			if(input == null){
                System.out.println("String to be decoded is wrong");
                System.exit(0);
            }
            ArrayList<Integer> retVal = new ArrayList<>();
			String inst = "";
			try{
					initialize();
					String tempStr = input; /*Input*/
					////////////////////////////////////
					// System.out.println(tempStr);
					////////////////////////////////////
					String opcode = tempStr.substring(25); /*getting opcode*/
					String func7 = tempStr.substring(0, 7);/*getting function 7*/
					String func3 = tempStr.substring(17, 20);/*getting function 3*/
					///////////////////////////////////
					// System.out.println(opcode);
					// System.out.println(func3);
					// System.out.println(func7);
					///////////////////////////////////
					Data insData = new Data(opcode, "", "");
					Data insData1 = new Data(opcode, "", func3);
					Data insData2 = new Data(opcode, func7, func3);
					for(Map.Entry<String, Data> entry : ins.entrySet()){
						Data tempData = entry.getValue();
						// System.out.println(insData2);
						if(insData.opcode.equals(tempData.opcode) && insData.func3.equals(tempData.func3) && insData.func7.equals(tempData.func7)){
								// System.out.println("1");
								inst = inst + entry.getKey();
								instruction = instruction + inst.substring(1);
								instructionType = instructionType + getType(inst);
						}else if(insData1.opcode.equals(tempData.opcode) && insData1.func3.equals(tempData.func3) && insData1.func7.equals(tempData.func7)){
							// System.out.println("2");
								inst = inst + entry.getKey();
								instruction = instruction + inst.substring(1);
								instructionType = instructionType + getType(inst);
						}else if(insData2.opcode.equals(tempData.opcode) && insData2.func3.equals(tempData.func3) && insData2.func7.equals(tempData.func7)){
							// System.out.println("3");
								inst = inst + entry.getKey();
								instruction = instruction + inst.substring(1);
								instructionType = instructionType + getType(inst);
						}
					}
					///////////////////////////////////
					System.out.println(instruction);
					System.out.println(instructionType);
					///////////////////////////////////
					// retVal.add(opcode);
					// retVal.add(func3);
					// retVal.add(func7);
					int instructionNumber = getInstructionNumber(instruction);
					if(instructionType.equals("R")){
						retVal.add(getRs1(tempStr));
						retVal.add(getRs2(tempStr));
						retVal.add(getRd(tempStr));
						retVal.add(-1);
						retVal.add(instructionNumber);
						// return retVal;
					}
					if(instructionType.equals("I")){
						retVal.add(getRs1(tempStr));
						retVal.add(-1);
						retVal.add(getRd(tempStr));
						retVal.add(getIm(instructionType, tempStr));
						retVal.add(instructionNumber);
						// return retVal;
					}
					if(instructionType.equals("U") || instructionType.equals("UJ")){
						retVal.add(-1);
						retVal.add(-1);
						retVal.add(getRd(tempStr));
						retVal.add(getIm(instructionType, tempStr));
						retVal.add(instructionNumber);
						// return retVal;
					}
					if(instructionType.equals("S") || instructionType.equals("SB")){
						retVal.add(getRs1(tempStr));
						retVal.add(getRs2(tempStr));
						retVal.add(-1);
						retVal.add(getIm(instructionType, tempStr));
						retVal.add(instructionNumber);
						// return retVal;
					}
			}catch(Exception e){
			}
			// System.out.println("Size = "+retVal.get(4));
			instructionType = "";
			// tempStr = "";
			instruction = "";
			return retVal;
		}
}
