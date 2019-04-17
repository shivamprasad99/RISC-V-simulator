class Buffer3{
        String IR;
        int ra;
        int rb;
        int rs1;
        int rs2;
        int rd;
        int ry;
        int rz;
        int rm;
        int pc_temp;
        int memoryData;
        int branch_next_pc;
        int branch_prediction;
        int immediate;
        int which_instruction;
        int forwarding1; // 1 for rz to ra, 2 for rz to rb, 3 for ry to ra , 4 for ry to rb,  5 for ry to rz , 6 ry to rm
        int forwarding2;
        int is_flush = 0;
        Buffer3(){
                forwarding1=0;
                forwarding2=0;
                rd=-2;
        }
}