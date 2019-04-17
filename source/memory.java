import java.io.*;
import java.util.*;
import java.nio.*;
import java.lang.Math;
import java.lang.*;

public class memory {

    LinkedHashMap<Integer, Byte> memory_linked_hash_map = new LinkedHashMap<Integer, Byte>();
    int code_start = 0x0;
    int data_start = 0x10000000;
    int data_stop = 0x10000000;
    public int stack_start= 0x7FFFFFFC;


    public void storeDataByte(int value, int address){
        // if(address > 0x7FFFFFFF){
        //     System.out.println("Memory Out of Bound\nYou might be growing stack in opposite direction");
        //     System.exit(0);
        // }
        int a2 = 0xff;
        byte b = (byte)(a2 & value);
        memory_linked_hash_map.put(address, b);    
        if(data_stop < address){
            data_stop = address;
        }
    }

    public void storeByte(int value){
        int a2 = 0xff;
        value = value & a2;
        byte b = (byte)(a2 & value);
        memory_linked_hash_map.put(code_start, b);    
        code_start += 1;
    }

    public void storeInitialByte(int value){
        int a2 = 0xff;
        value = value & a2;
        byte b = (byte)(a2 & value);
        memory_linked_hash_map.put(data_start, b);    
        data_start += 1;
        data_stop +=1;
    }

    byte[] toByte(int i){
        byte[] word = new  byte[4];
        for(int j = 0; j < 4; j++){
            word[j] = 0;
        }
        word[3] = (byte)(i>>24);
        word[2] = (byte)(i>>16);
        word[1] = (byte)(i>>8);
        word[0] = (byte)(i>>0);
        return word;
    }
    
    int toInt(String[] hex){
        int a = 0;
        int sum = 0;
        for(int t = 0; t < 4; t++){
            String[] arr = hex[t].split("");
            for(int i = 0; i < hex[t].length(); i++){
                int temp = (int) Math.pow(16, i+sum);
                int val = Integer.parseInt(arr[  hex[t].length() - i - 1  ], 16);
                a+=temp*val;
                // System.out.println(temp+" "+val+" "+ temp*val);
            }
            sum+=2;
        }
        return a;
    }
    
    public void storeInitialWord(int value){
        System.out.println("storing this " + value);
        byte[] bt = toByte(value);
        storeInitialByte(bt[0]);
        storeInitialByte(bt[1]);
        storeInitialByte(bt[2]);
        storeInitialByte(bt[3]);
    }

    public void storeDataWord(int value, int address){
        // if(address >= 0x7FFFFFFF){
        //     System.out.println("Memory Out of Bound\nYou might be growing stack in opposite direction");
        //     System.exit(0);
        // }
        if(address > 0x7FFFFFFC)
        System.out.println("Storing value "+value + " " + address);
        byte[] bt = toByte(value);
        storeDataByte(bt[0], address);
        address++;
        storeDataByte(bt[1], address);
        address++;
        storeDataByte(bt[2], address);
        address++;
        storeDataByte(bt[3], address);
        address++;
        System.out.println(bt[0] + " " + bt[1] + " " + bt[2] + " "+ bt[3]);
        if(data_stop < address){
            data_stop = address;
        }
    }

    public void storeWord(int value){
        byte[] bt = toByte(value);
        storeByte(bt[0]);
        code_start++;
        storeByte(bt[1]);
        code_start++;
        storeByte(bt[2]);
        code_start++;
        storeByte(bt[3]);
        code_start++;
    }

    public int loadByte(int address){
        
        // if(address < 0x0 && address >= 0xffffffff){
        //     System.out.println("Memory Out of bound");
        //     System.exit(0);
        // }
        byte bt = 0;
        if(memory_linked_hash_map.get(address) != null)
            bt = memory_linked_hash_map.get(address);
        int output = bt & 0xff;
        String hex = Integer.toHexString(output);
        String[] arr = hex.split(""); 
        int a = 0;
        for(int i =0; i < hex.length(); i++){
            int temp = (int)Math.pow(16, i);
            int val = Integer.parseInt(arr[hex.length() - i - 1], 16);
            a += temp*val;
        }
        return a;    
    }



    public int loadWord(int address){
        // if(address < 0x0 && address >= 0xffffffff){
        //     System.out.println("Memory Out of bound");
        //     System.exit(0);
        // }

        byte[] bt = new byte[4];
        
        for(int i = 0 ; i < 4; i++){
            if(memory_linked_hash_map.get(address+i) != null){
                bt[i] = memory_linked_hash_map.get(address+i);
                System.out.println("bt "+bt[i]);
            }
        }
        


        int removing_negative[] = new int[4];
        for(int i = 0; i < 4; i++){
            removing_negative[i] = bt[i] & 0xff;
        }
        String[] hex = new String[4];
        hex[0] = Integer.toHexString(removing_negative[0]);
        hex[1] = Integer.toHexString(removing_negative[1]);
        hex[2] = Integer.toHexString(removing_negative[2]);
        hex[3] = Integer.toHexString(removing_negative[3]);
        int b = toInt(hex);
        return b;

    }


    void printTextMemory(){
        for(int i = 0x0; i < code_start; i+=4){
            System.out.println(i + " -> "+memory_linked_hash_map.get(i)
            + " " + memory_linked_hash_map.get(i+1)
            + " " + memory_linked_hash_map.get(i+2)
            + " " + memory_linked_hash_map.get(i+3));
        }
    }

    void printDataMemory(){
        System.out.println(data_stop);

        for(int i = 0x10000000; i <= data_stop; i+=4){
            if(memory_linked_hash_map.get(i)!=null){
                if(i > 0x7FFFFFFC || i < 0x10000000)
                    break;
                System.out.println(i + " -> "+memory_linked_hash_map.get(i)
                + " " + memory_linked_hash_map.get(i+1)
                + " " + memory_linked_hash_map.get(i+2)
                + " " + memory_linked_hash_map.get(i+3));
            }
        }
    }

}
