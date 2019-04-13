/*
    to use this class
    first set_pc, set_muxPc, set_muxInc
    then use adder which will return the next pc
*/
// import class containg RA and RB;

class PC {
    public int pc, pc_temp;  // default value of PC set to 0
    public int muxPc, muxInc;  // default value of MuxINC is 4

    PC(){
        pc = 0x0;
        muxInc = 4;
    }
    public int get_pc_temp(){
        return pc_temp;
    }
    public int adder(int pc_value){
        pc_temp = pc_value;
        pc_value = muxPc;
        pc_value = pc_value+muxInc;
        return pc_value;
    }
}
