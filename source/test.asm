.data
arr: .word 4 5 6 8 12
size: .word 5

.text
la x5 arr
addi x5 x5 32
addi x6 x0 6
sw x6 0(x5)
lw x10 0(x5)