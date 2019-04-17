addi x10 x0 5
jal x1 8 
jal x0 64 
addi x2 x2 -8
sw x1 x2 4 
sw x10 x2 0 
addi x5 x10 -1
bge x5 x0 12 
addi x10 x0 1
jalr x0 x1 0 
addi x10 x10 -1
jal x1 -32 
addi x2 x2 8
addi x6 x10 0
lw x10 x2 0
lw x1 x2 4
mul x10 x10 x6 
jalr x0 x1 0 
