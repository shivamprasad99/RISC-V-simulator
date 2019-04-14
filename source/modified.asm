.text 
auipc x10 65536 
lw x10 x10 0
jal x1 8 
jal x0 120 
addi x2 x2 -4 
sw x1 x2 0 
beq x10 x0 16 
addi x5 x0 1 
beq x10 x5 20 
jal x0 28 
add x11 x0 x0 
addi x2 x2 4 
jalr x0 x1 0 
addi x11 x0 1 
addi x2 x2 4 
jalr x0 x1 0 
addi x10 x10 -1 
addi x2 x2 -4 
sw x10 x2 0 
jal x1 -60 
lw x10 x2 0
addi x2 x2 4 
addi x5 x11 0 
addi x10 x10 -1 
addi x2 x2 -4 
sw x5 x2 0 
jal x1 -88 
lw x5 x2 0
addi x2 x2 4 
add x11 x5 x11 
lw x1 x2 0
addi x2 x2 4 
jalr x0 x1 0 
