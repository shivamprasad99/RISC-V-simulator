import java.io.*;
import java.util.*;

class instructions{
    PC pc_object;
    instructions(PC pc_object_value){
        pc_object = pc_object_value;
    }

    public int add(int ra, int rb){
        return ra+rb;
    }
    public int and(int ra, int rb){
        return ra + rb;
    }
    public int addi(int ra, int rb){
        return ra+rb;
    }
    public int or_(int ra, int rb){
        return ra|rb;
    }
    public int sll(int ra, int rb){
        return ra<<rb;
    }
    public int slt(int ra, int rb){
        if(ra<rb)
            return 1;
        return 0;
    }
    public int sltu(int ra, int rb){
        if(ra-rb<0) // of course you're smart DB, but not sure about this one
            return 1;
        return 0;
    }
    public int sra(int ra, int rb){
        return ra>>rb;
    }
    public int sub(int ra, int rb){
        return ra-rb;
    }
    public int xor(int ra, int rb){
        return ra^rb;
    }
    public int srl(int ra, int rb){
        return ra>>>rb;
    }
    public int wide_immediate_addition(int ra, int rb) {
        return ra+(rb<<12);
    }
    public int lw(int ra, int rb){
        return ra+rb;
    }
    public int sw(int ra, int rb){
        return ra+rb;
    }
    public int mul(int ra,int rb){
        // System.out.println("ENTERED MULTIPLY" + ra + rb);
        return ra*rb;
    }
    public int div(int ra,int rb){
        return ra/rb;
    }
}