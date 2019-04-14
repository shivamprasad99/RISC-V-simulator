.data 
var1: .word 7 5 16 78
var2: .word 23 32
var3: .word 75
.text

main:
lw x10 var1
jal x1, fibo
jal x0 exit
fibo:

addi x2, x2, -4 # adjust stack (duty of callee)
sw x1, 0(x2) # adjust stack (duty of callee)

beq x10, x0, fibo_of_0
addi x5, x0, 1
beq x10, x5, fibo_of_1
jal x0 fibo_recur

fibo_of_0:

add x11, x0, x0
addi x2, x2, 4
jalr x0, 0(x1)

fibo_of_1:

addi x11, x0, 1
addi x2, x2, 4
jalr x0, 0(x1)

fibo_recur:

addi x10, x10, -1

addi x2, x2, -4
sw x10, 0(x2) # duty of caller to save argument if needed afterwards in the program, if callee uses that register

jal x1, fibo

lw x10, 0(x2)
addi x2, x2, 4

addi x5, x11, 0
addi x10, x10, -1

addi x2, x2, -4
sw x5, 0(x2) # duty of caller to save temporaries if needed afterwards in the program, if callee uses that register

jal x1, fibo

lw x5, 0(x2)
addi x2, x2, 4

add x11, x5, x11

lw x1, 0(x2) # adjust stack (duty of callee)
addi x2, x2, 4 # adjust stack (duty of callee)

jalr x0, 0(x1)

exit: # fall through