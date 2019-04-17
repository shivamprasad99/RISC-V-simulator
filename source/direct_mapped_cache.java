import java.lang.Math;
import java.lang.*;
import java.util.*;

public class direct_mapped_cache {
    byte b = 0;
    int cache_size;
    int cache_block_size;
    int no_of_blocks_in_cache;
    int no_of_blocks_in_main_memory;
    int no_of_bits_in_block_offset;
    int no_of_bits_in_index;
    int no_of_bits_in_tag;
    int no_of_bytes_in_one_block;
    int hits;
    int misses;
    int cold_misses;
    int conflict_misses; // will be 0 in fully_associative_instruction_cache
    int capacity_misses; // will be 0 in direct_mapped_instruction_cache

    public class block {
        
        boolean is_valid;
        int tag;
        byte data[];

        public block() {
            this.is_valid = false;
            this.tag = 0;
            this.data = new byte[no_of_bytes_in_one_block];
        }
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

    // provide index as row and block offset as column
    block instruction_cache[];

    // assuming that main memory is of size 2**32
    // int in Java is 32 bits, so we can use int for cache size

    // provide cache_size in number of words, where each word is 4 bytes (standard of RISC-V)
    // provide cache_block_size in number of words, where each word is 4 bytes
    // note that cache_size and cache_block_size must be multiple of 2
    public direct_mapped_cache(int cache_size, int cache_block_size) {
        System.out.println("entered cons");
        this.hits = this.misses = this.cold_misses = this.conflict_misses = this.capacity_misses = 0;

        this.cache_size = cache_size;
        this.cache_block_size = cache_block_size;

        this.no_of_blocks_in_cache = cache_size / cache_block_size;
        this.no_of_blocks_in_main_memory = 1073741824 / (cache_block_size/4);
        int i_temp_1, i_itr_1;

        this.no_of_bits_in_block_offset = 2; // because each word is 4 bytes
        i_temp_1 = cache_block_size;

        while(i_temp_1 != 1) {
            this.no_of_bits_in_block_offset++;
            i_temp_1/=2;
        }

        this.no_of_bits_in_index = 0;
        i_temp_1 = this.no_of_blocks_in_cache;

        while(i_temp_1 != 1) {
            this.no_of_bits_in_index++;
            i_temp_1/=2;
        }

        this.no_of_bits_in_tag = 0;
        i_temp_1 = this.no_of_blocks_in_main_memory / this.no_of_blocks_in_cache;

        while(i_temp_1 != 1) {
            this.no_of_bits_in_tag++;
            i_temp_1/=2;
        }

        this.instruction_cache = new block[this.no_of_blocks_in_cache];
        this.no_of_bytes_in_one_block = (int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset));

        for(i_itr_1=0; i_itr_1<this.no_of_blocks_in_cache; i_itr_1++) {
            instruction_cache[i_itr_1] = new block();
        }
        System.out.println("entere cons");

    }

    public int loadByte(int address, memory memory_object) {

        // first divide the PC into tag, index and block offset
        // then return the data if present in cache, otherwise get the data from main memory by checking the tag bit

        int tag, index = 0, block_offset, temp_PC = address;
        int i_itr_1;

        block_offset = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));

        index = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));

        tag = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));

        if(this.instruction_cache[index].is_valid) {
            if(this.instruction_cache[index].tag == tag) {
                this.hits++;
            }
            else {
                this.misses++;
                this.conflict_misses++;
                this.instruction_cache[index].tag = tag;
                temp_PC = address/this.no_of_bytes_in_one_block;
                temp_PC *= this.no_of_bytes_in_one_block;
                for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                    if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                        this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                    }
                    else {
                        this.instruction_cache[index].data[i_itr_1] = new Byte(b); // let it be default value
                    }
                    System.out.println("cache data-------------- >>>>>" + this.instruction_cache[index].data[i_itr_1]);
                }
            }
        }
        else {
            this.misses++;
            this.cold_misses++;
            this.instruction_cache[index].is_valid = true;
            temp_PC = address/this.no_of_bytes_in_one_block;
            temp_PC *= this.no_of_bytes_in_one_block;
            for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                    this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                }
                else {
                    
                    this.instruction_cache[index].data[i_itr_1] = new Byte(b); // let it be default value
                }
                System.out.println("cache data-------------- >>>>>" + this.instruction_cache[index].data[i_itr_1]);
            }
        }
        return memory_object.loadByte(address);
        // int output = this.instruction_cache[index].data[block_offset] & 0xff;
        // String hex = Integer.toHexString(output);
        // String[] arr = hex.split(""); 
        // int a = 0;
        // for(int i =0; i < hex.length(); i++){
        //     int temp = (int)Math.pow(16, i);
        //     int val = Integer.parseInt(arr[hex.length() - i - 1], 16);
        //     a += temp*val;
        // }
        // System.out.println("asli data returned ---->>"+ a);
        // return a;

    }

    public int loadWord(int address, memory memory_object) {

        // first divide the PC into tag, index and block offset
        // then return the data if present in cache, otherwise get the data from main memory by checking the tag bit

        int tag, index, block_offset, temp_PC = address;
        int i_itr_1;

        block_offset = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));

        index = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));

        tag = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));
        System.out.println("index " + index + "  " + temp_PC + "  " + ((int) Math.round(Math.pow(2,this.no_of_bits_in_index))));
        if(temp_PC==0){
            index  = 0;
            block_offset = 0;
        }
        if(this.instruction_cache[index].is_valid) {
            if(this.instruction_cache[index].tag == tag) {
                this.hits++;
            }
            else {
                this.misses++;
                this.conflict_misses++;
                this.instruction_cache[index].tag = tag;
                temp_PC = address/this.no_of_bytes_in_one_block;
                temp_PC *= this.no_of_bytes_in_one_block;
                for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                    if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                        this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                    }
                    else {
                        this.instruction_cache[index].data[i_itr_1] = new Byte(b); // let it be default value
                    }
                }
            }
        }
        else {
            this.misses++;
            this.cold_misses++;
            this.instruction_cache[index].is_valid = true;
            temp_PC = address/this.no_of_bytes_in_one_block;
            temp_PC *= this.no_of_bytes_in_one_block;
            for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                    this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                }
                else {
                    this.instruction_cache[index].data[i_itr_1] = new Byte(b); // let it be default value
                }
            }
        }

        byte[] bt = new byte[4];

        for(int i = 0 ; i < 4; i++) {
            // assuming instructions are word aligned, this will never blow since block_offset will always be 0
            // but take care of this in data_cache
            bt[i] = this.instruction_cache[index].data[block_offset+i];
        }
        return memory_object.loadWord(address);
        // int removing_negative[] = new int[4];
        // for(int i = 0; i < 4; i++){
        //     removing_negative[i] = bt[i] & 0xff;
        // }
        // String[] hex = new String[4];
        // hex[0] = Integer.toHexString(removing_negative[0]);
        // hex[1] = Integer.toHexString(removing_negative[1]);
        // hex[2] = Integer.toHexString(removing_negative[2]);
        // hex[3] = Integer.toHexString(removing_negative[3]);
        // int b = toInt(hex);
        // return b;

    }

    public void storeDataByte(int value, int address, memory memory_object) {

        // first divide the PC into tag, index and block offset
        // then return the data if present in cache, otherwise get the data from main memory by checking the tag bit

        int tag, index, block_offset, temp_PC = address;
        int i_itr_1;

        block_offset = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));

        index = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));

        tag = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));

        if(this.instruction_cache[index].is_valid) {
            if(this.instruction_cache[index].tag == tag) {
                this.hits++;
            }
            else {
                this.misses++;
                this.conflict_misses++;
                this.instruction_cache[index].tag = tag;
                temp_PC = address/this.no_of_bytes_in_one_block;
                temp_PC *= this.no_of_bytes_in_one_block;
                for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                    if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                        this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                    }
                    else {
                        this.instruction_cache[index].data[i_itr_1] = new Byte(b);; // let it be default value
                    }
                }
            }
        }
        else {
            this.misses++;
            this.cold_misses++;
            this.instruction_cache[index].is_valid = true;
            temp_PC = address/this.no_of_bytes_in_one_block;
            temp_PC *= this.no_of_bytes_in_one_block;
            for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                    this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                }
                else {
                    this.instruction_cache[index].data[i_itr_1] = new Byte(b);; // let it be default value
                }
            }
        }

        int a2 = 0xff;
        byte b = (byte)(a2 & value);

        this.instruction_cache[index].data[block_offset] = b;
        memory_object.storeDataByte(value,address); // write-through policy

    }

    public void storeDataWord(int value, int address, memory memory_object) {

        // first divide the PC into tag, index and block offset
        // then return the data if present in cache, otherwise get the data from main memory by checking the tag bit

        int tag, index, block_offset, temp_PC = address;
        int i_itr_1;

        block_offset = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_block_offset)));

        index = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_index)));

        tag = temp_PC % ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));
        temp_PC /= ((int) Math.round(Math.pow(2,this.no_of_bits_in_tag)));
        if(temp_PC==0){
            index  = 0;
            block_offset = 0;
        }
        System.out.println("index " + index + "  " + temp_PC + "  " + ((int) Math.round(Math.pow(2,this.no_of_bits_in_index))));
        if(this.instruction_cache[index].is_valid) {
            if(this.instruction_cache[index].tag == tag) {
                this.hits++;
            }
            else {
                this.misses++;
                this.conflict_misses++;
                this.instruction_cache[index].tag = tag;
                temp_PC = address/this.no_of_bytes_in_one_block;
                temp_PC *= this.no_of_bytes_in_one_block;
                for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                    if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                        this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                    }
                    else {
                        this.instruction_cache[index].data[i_itr_1] = new Byte(b);; // let it be default value
                    }
                }
            }
        }
        else {
            this.misses++;
            this.cold_misses++;
            this.instruction_cache[index].is_valid = true;
            temp_PC = address/this.no_of_bytes_in_one_block;
            temp_PC *= this.no_of_bytes_in_one_block;
            for(i_itr_1=0; i_itr_1<this.no_of_bytes_in_one_block; i_itr_1++) {
                if(memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1) != null) {
                    this.instruction_cache[index].data[i_itr_1] = memory_object.memory_linked_hash_map.get(temp_PC+i_itr_1);
                }
                else {
                    this.instruction_cache[index].data[i_itr_1] = new Byte(b);; // let it be default value
                }
            }
        }

        byte[] bt = toByte(value);

        int a2 = 0xff;
        byte b = (byte)(a2 & (int)bt[0]);

        this.instruction_cache[index].data[block_offset] = b;
        memory_object.storeDataByte(value,address); // write-through policy

        b = (byte)(a2 & (int)bt[1]);

        this.instruction_cache[index].data[block_offset] = b;
        memory_object.storeDataByte(value,address); // write-through policy

        b = (byte)(a2 & (int)bt[2]);

        this.instruction_cache[index].data[block_offset] = b;
        memory_object.storeDataByte(value,address); // write-through policy

        b = (byte)(a2 & (int)bt[3]);

        this.instruction_cache[index].data[block_offset] = b;
        memory_object.storeDataByte(value,address); // write-through policy

    }

}