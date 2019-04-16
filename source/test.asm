.data
var1: .word 0x25 37
var2: .word 8
.text
addi x4 x4 4
la x10 var2
lw x11 0(x10)