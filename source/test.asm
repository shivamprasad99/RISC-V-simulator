addi x10 x0 5
jal x1,fact
jal x0 EXIT

fact:
addi x2, x2, -8 #// adjust stack for 2 items
sw x1, 4(x2)#// save the return address
sw x10, 0(x2)#// save the argument n
addi x5, x10, -1 #// x5 = n - 1
bge x5, x0, L1 #// if (n - 1) >= 0, go to L1
addi x10, x0, 1 #// return 1

jalr x0, 0(x1) #// return to caller

L1: addi x10, x10, -1 #// n >= 1: argument gets (n −1)
jal x1, fact #// call fact with (n −1)

addi x2, x2, 8 #// pop 2 items off stack
addi x6, x10, 0 #// return from jal: move result of fact(n -1) to x6:
lw x10, 0(x2) #// restore argument n
lw x1, 4(x2) #// restore the return address

mul x10, x10, x6 #// return n * fact (n −1)
jalr x0, 0(x1) #// return to the caller

EXIT: