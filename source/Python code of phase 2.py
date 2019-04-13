from bitstring import BitStream, BitArray


# interface for Instruction Resgister
# memory_data and IR_enable are incoming signals
# content is the outgoing signal
class InstructionRegister:

    def __init__(self):
        # content of InstructionRegister is in content in the form of binary string
        self.content = "00000000000000000000000000000000"

    # input memory_data is connected to output of processor_memory_interface
    # which takes memory address as input and gives content (word) of that address as output in the form of binary string
    def write_to_instruction_register(self, memory_data, IR_enable):
        if(IR_enable == True):
            self.content = memory_data



# interface for RegisterFile
# take care that general_purpose_register[0] is hard wired to 0
# address_A, address_B, address_C, C and rf_write are incoming signals
# A and B are outgoing signals
class RegisterFile:

    def __init__(self):

        self.general_purpose_register = ["00000000000000000000000000000000"] * 32
        self.general_purpose_register[2] = "01111111111111111111111111110000" # stack pointer set to 0x7ffffff0
        self.general_purpose_register[3] = "00010000000000000000000000000000" # global pointer set to 0x10000000

    # returns a two-tuple output of the contents in the register specified by address_A and address_B
    # note that address_A and address_B are hard wired to fields in the IR
    def read_from_register_file(self, address_A, address_B):

        # returning outgoing signals (A,B) in the form of two-tuple
        return (self.general_purpose_register[address_A], self.general_purpose_register[address_B])

    # writes the data (binary string) given in C in the register specified by address_C
    # note that address_C is hard wired to field in the IR
    # input to C is connected to RY
    def write_to_register_file(self, address_C, C, RF_write):

        # extending the binary string to 32 bits if the code doesn't provide it
        temp_uint = BitArray(bin=C).uint
        C = BitArray(uint=temp_uint, length=32)

        # check the control signal
        # if address_C == 0, don't write to it, since general_purpose_register[0] is always 0
        if RF_write == True and address_C != 0:
            self.general_purpose_register[address_C] = C.bin



# interface for Arithmetic and Logic Unit
# InA, InB and ALU_op are incoming signals
# Out and condition signals are outgoing signals
class ALU:

    def __init__(self):
        # these specify the condition signals
        self.condition_signal_beq = False
        self.condition_signal_bge = False
        self.condition_signal_bgeu = False
        self.condition_signal_blt = False
        self.condition_signal_bltu = False
        self.condition_signal_bne = False

    # returns Out signal (binary string)
    def perform_operation(self, InA, InB, ALU_op):

        # just in case given binary string is not 32 bit
        temp_uint = BitArray(bin=InA).uint
        InA = BitArray(uint=temp_uint, length=32)

        # just in case given binary string is not 32 bit
        # remember thay even InB is bit extended to 32 bit, so the sign is already taken into account
        temp_uint = BitArray(bin=InB).uint
        InB = BitArray(uint=temp_uint, length=32)


        if InA.int == InB.int: # doesn't matter if we compare signed or unsigned
            self.condition_signal_beq = True
            self.condition_signal_bne = False
        else:
            self.condition_signal_beq = False
            self.condition_signal_bne = True


        if InA.int >= InB.int:
            self.condition_signal_bge = True
        else:
            self.condition_signal_bge = False


        if InA.uint >= InB.uint:
            self.condition_signal_bgeu = True
        else:
            self.condition_signal_bgeu = False


        if InA.int < InB.int:
            self.condition_signal_blt = True
        else:
            self.condition_signal_blt = False


        if InA.uint < InB.uint:
            self.condition_signal_bltu = True
        else:
            self.condition_signal_bltu = False


        # lb, lh, lw, lbu, lhu, addi, sb, sh, sw, add, jalr, jal
        # operation name add
        if ALU_op == 0:

            temp_uint = InA.uint + InB.uint # add as unsigned int
            temp_str = bin(temp_uint)[2:] # binary representation of positive numbers starts with 0b and not -0b

            if len(temp_str) > 32: # underflow or overflow
                temp_str = temp_str[-32:] # only keep last 32 bits

            # but still, temp_str might be less than 32 bits
            temp_uint = BitArray(bin=temp_str).uint
            Out = BitArray(uint=temp_uint, length=32).bin

        # slli, sll
        # operation name sll
        elif ALU_op == 1:

            temp_uint = (InA.uint)<<(InB.uint)
            temp_str = bin(temp_uint)[2:] # binary representation of positive numbers starts with 0b and not -0b

            if len(temp_str) > 32: # underflow or overflow
                temp_str = temp_str[-32:] # only keep last 32 bits

            # but still, temp_str might be less than 32 bits
            temp_uint = BitArray(bin=temp_str).uint
            Out = BitArray(uint=temp_uint, length=32).bin

        # slti, slt
        # operation name slt
        elif ALU_op == 2:

            if self.condition_signal_blt == True:
                Out = "00000000000000000000000000000001"
            else:
                Out = "00000000000000000000000000000000"

        # sltiu, sltu
        # operation name sltu
        elif ALU_op == 3:

            if self.condition_signal_bltu == True:
                Out = "00000000000000000000000000000001"
            else:
                Out = "00000000000000000000000000000000"

        # xor, xori
        # operation name xor
        elif ALU_op == 4:

            temp_uint = (InA.uint)^(InB.uint)
            temp_str = bin(temp_uint)[2:] # binary representation of positive numbers starts with 0b and not -0b

            if len(temp_str) > 32: # underflow or overflow
                temp_str = temp_str[-32:] # only keep last 32 bits

            # but still, temp_str might be less than 32 bits
            temp_uint = BitArray(bin=temp_str).uint
            Out = BitArray(uint=temp_uint, length=32).bin

        # srli, srl
        # operation name srl
        elif ALU_op == 5:

            temp_uint = (InA.uint)>>(InB.uint)
            temp_str = bin(temp_uint)[2:] # binary representation of positive numbers starts with 0b and not -0b

            if len(temp_str) > 32: # underflow or overflow
                temp_str = temp_str[-32:] # only keep last 32 bits

            # but still, temp_str might be less than 32 bits
            temp_uint = BitArray(bin=temp_str).uint
            Out = BitArray(uint=temp_uint, length=32).bin

        # srai, sra
        # operation name sra
        elif ALU_op == 6:

            temp_int = (InA.int)>>(InB.uint) # note the difference InA.int instead of InA.uint

            if temp_int < 0:
                temp_str = bin(temp_uint)[3:] # binary representation of negative numbers starts with -0b
            else:
                temp_str = bin(temp_uint)[2:] # binary representation of positive numbers starts with 0b

            if len(temp_str) > 32: # underflow or overflow
                temp_str = temp_str[-32:] # only keep last 32 bits

            # but still, temp_str might be less than 32 bits
            temp_uint = BitArray(bin=temp_str).uint
            temp_Out = BitArray(uint=temp_uint, length=32).bin
            Out = ""

            # now we have logically right shifted answer, but we wanted arithmetic shift

            if (InA.bin)[0] == '1':

                for word in temp_Out:


            return BitArray(uint=temp_uint, length=32).bin

            # ---------------------------------------------------

            if InA.bin[0] == '1':
                temp_int = InA>>InB
                temp_str = BitArray(uint=temp_int, length=32).bin
                i = 0
                while i < InB:
                    temp_str[i] = '1'
                    i = i + 1
                return int(temp_str[-32:],2)
            else:
                temp_str = bin(InA>>InB)[2:]
                return int(temp_str[-32:],2)

        # ori, or
        # operation name or
        elif ALU_op == 7:
            temp_str = bin(InA|InB)[2:]
            return int(temp_str[-32:],2)

        # andi, and
        # operation name and
        elif ALU_op == 8:
            temp_str = bin(InA&InB)[2:]
            return int(temp_str[-32:],2)

        # comparison might be on the basis of subtraction, so internally, these might return the difference
        # sub, subw, beq, bne, bltu, bgeu
        # operation name sub
        elif ALU_op == 9:
            temp_str = bin(InA-InB)[2:]
            return int(temp_str[-32:],2)

        # comparison might be on the basis of subtraction, so internally, these might return the difference
        # blt, bge
        # operation name sub_sig_comp
        elif ALU_op == 10:
            return InA_bitarray.int()-InB_bitarray.int()

        return Out



# interface for Instruction Decoder
# takes IR as input
# generates INS1, INS2, ... , INSm output signals for ControlCircuitry
# but here, output is opcode, func3, func7 as a three-tuple for simplicity
class InstructionDecoder:

    def __init__(self):

        self.opcode = "0000000"
        self.func3 = "000"
        self.func7 = "0000000"

    def decode(self, opcode, func3, func7):

        self.opcode = opcode
        self.func3 = func3
        self.func7 = func7



# interface for Control Circuitry
# opcode, func3, func7, step_counter, condition_signal_beq, condition_signal_bge, condition_signal_bgeu,
# condition_signal_blt, condition_signal_bltu, condition_signal_bne are incoming signals
# control signals are outgoing signals
class ControlCircuitry:

    def __init__(self):

        # counter_enable is always true, since in our code, there is no need to wait for memory
        self.counter_enable = True

        self.PC_select = 1
        self.PC_enable = False
        self.INC_select = 0
        # for immediate block
        self.extend = 0
        self.IR_enable = False
        self.MA_select = 0
        self.MEM_read = False
        self.MEM_write = False
        self.RF_write = False
        self.B_select = 0
        self.ALU_op = 0
        self.Y_select = 0

    # some signals can be given one value throughout the execution of one instruction,
    # while some need to be changed within stages, hence step_counter is needed as input
    def generate_control_signals(self, opcode, func3, func7, step_counter, condition_signal_beq, condition_signal_bge, condition_signal_bgeu, condition_signal_blt, condition_signal_bltu, condition_signal_bne):
        
        # no matter what the instruction is, it is only just fetched in cycle 1, hence these control signals are generated
        if step_counter == 1:

            self.PC_select = 1
            self.PC_enable = True
            self.INC_select = 0
            self.extend = 0 # don't care
            self.IR_enable = True
            self.MA_select = 1
            self.MEM_read = True
            self.MEM_write = False
            self.RF_write = False
            self.B_select = 0 # don't care
            self.ALU_op = 0 # don't care
            self.Y_select = 0 # don't care

        # lb, lh, lw, lbu, lhu (I-type)
        elif opcode == '0000011':

            self.PC_select = 1
            self.PC_enable = False
            self.INC_select = 0
            self.extend = 0
            self.IR_enable = False
            
            if step_counter == 4:
                self.MA_select = 0 # select to read memory address RZ
                self.MEM_read = True
                self.MEM_write = False
            else:
                self.MA_select = 1
                self.MEM_read = False
                self.MEM_write = False
            
            self.RF_write = True
            self.B_select = 1
            self.ALU_op = 0 # addition
            self.Y_select = 1

        # addi, slli, slti, sltiu, xori, srli, srai, ori, andi (I-type)
        elif opcode == '0010011':

            self.PC_select = 1
            self.PC_enable = False
            self.INC_select = 0
            self.extend = 0
            self.IR_enable = False
            self.MA_select = 1
            self.MEM_read = False
            self.MEM_write = False
            self.RF_write = True
            self.B_select = 1

            if func3 == '000': # addi
                self.ALU_op = 0
            elif func3 == '001': # slli
                self.ALU_op = 1
            elif func3 == '010': # slti
                self.ALU_op = 2
            elif func3 == '011': # sltiu
                self.ALU_op = 3
            elif func3 == '100': # xori
                self.ALU_op = 4
            elif func3 == '101' and func7 == '0000000': # srli
                self.ALU_op = 5
            elif func3 == '101' and func7 == '0100000': # srai
                self.ALU_op = 6
            elif func3 == '110': # ori
                self.ALU_op = 7
            elif func3 == '111': # andi
                self.ALU_op = 8

            self.Y_select = 0

        # jalr (I-type)
        # remember to save PC + 4 to destination register
        elif opcode == '1100111':

            self.PC_select = 0 # select register RA

            # we can do it better by taking the jump in decode itself, but that part was covered in pipelining
            # so i will do it there
            if step_counter == 3:
                self.PC_enable = True
            else:
                self.PC_enable = False

            self.INC_select = 1 # select immediate value to be added to contents of register RA
            self.extend = 0
            self.IR_enable = False
            self.MA_select = 1
            self.MEM_read = False
            self.MEM_write = False
            self.RF_write = True
            self.B_select = 1
            self.ALU_op = 0 # don't care
            self.Y_select = 2

        # sb, sh, sw (S-type)
        elif opcode == '0100011':

            self.PC_select = 1
            self.PC_enable = False
            self.INC_select = 0
            self.extend = 1
            self.IR_enable = False

            if step_counter == 4:
                self.MA_select = 0 # select to write to memory address RZ
                self.MEM_read = False
                self.MEM_write = True
            else:
                self.MA_select = 1
                self.MEM_read = False
                self.MEM_write = False

            self.RF_write = False
            self.B_select = 1
            self.ALU_op = 0 # addition
            self.Y_select = 1

        # add, sub, sll, slt, sltu, xor, srl, sra, or, and (R-type)
        elif opcode == '0110011':

            self.PC_select = 1
            self.PC_enable = False
            self.INC_select = 0
            self.extend = 0 # don't care
            self.IR_enable = False
            self.MA_select = 1
            self.MEM_read = False
            self.MEM_write = False
            self.RF_write = True
            self.B_select = 0
            
            if func3 == '000' and func7 == '0000000': # add
                self.ALU_op = 0
            elif func3 == '000' and func7 == '0100000': # sub
                self.ALU_op = 9
            elif func3 == '001': # sll
                self.ALU_op = 1
            elif func3 == '010': # slt
                self.ALU_op = 2
            elif func3 == '011': # sltu
                self.ALU_op = 3
            elif func3 == '100': # xor
                self.ALU_op = 4
            elif func3 == '101' and func7 == '0000000': # srl
                self.ALU_op = 5
            elif func3 == '101' and func7 == '0100000': # sra
                self.ALU_op = 6
            elif func3 == '110': # or
                self.ALU_op = 7
            elif func3 == '111': # and
                self.ALU_op = 8
            
            self.Y_select = 0

        # beq, bne, blt, bge, bltu, bgeu (SB-type)
        elif opcode == '1100011':

            self.PC_select = 1

            if step_counter == 3:
                if (func3 == '000' and condition_signal_beq == True) or (func3 == '001' and condition_signal_bne == True) or (func3 == '100' and condition_signal_blt == True) or (func3 == '101' and condition_signal_bge == True) or (func3 == '110' and condition_signal_bltu == True) or (func3 == '111' and condition_signal_bgeu == True):
                    self.PC_enable = True
                else:
                    self.PC_enable = False
            else:
                self.PC_enable = False

            self.INC_select = 1
            self.extend = 2
            self.IR_enable = False
            self.MA_select = 1
            self.MEM_read = False
            self.MEM_write = False
            self.RF_write = True
            self.B_select = 0

            # actually, these are don't care
            if func3 == '000' or func3 == '001' or func3 == '110' or func3 == '111': # beq, bne, bltu, bgeu
                self.ALU_op = 9 # sub
            elif func3 == '100' or func3 == '101': # blt, bge
                self.ALU_op = 10 # sub_sig_comp

            self.Y_select = 0

        # jal (UJ-type)
        elif opcode == '1101111':

            self.PC_select = 1

            if step_counter == 3:
                self.PC_enable = True
            else:
                self.PC_enable = False

            self.INC_select = 1
            self.extend = 2
            self.IR_enable = False
            self.MA_select = 1
            self.MEM_read = False
            self.MEM_write = False
            self.RF_write = True
            self.B_select = 0 # don't care
            self.ALU_op = 0 # don't care
            self.Y_select = 2 # return address



# interface for Immediate block
# immediate blocks from IR (I-type, S-type, SB-type, U-type, UJ-type) and extend control signal are incoming signals
# depending on the instruction format, it generates values to be provided to MuxB and MuxINC, which are outgoing signals
class Immediate:

    def __init__(self):
        
        self.imm_val_for_MuxB = 0
        self.imm_val_for_MuxINC = 0

    # returns a two-tuple of values to be provided to MuxB and MuxINC respectively
    def generate_imm_values_for_MuxB_and_MuxINC(self, imm_val_I_type, imm_val_S_type, imm_val_SB_type, imm_val_U_type, imm_val_UJ_type, extend):
        # if I-type instruction (sign-extended)
        # account for slli, srli, srai
        if extend==0:
            (self.imm_val_for_MuxB, self.imm_val_for_MuxINC) = (BitArray(bin=imm_val_I_type).int, BitArray(bin=imm_val_I_type).int)
        # if S-type instruction (sign-extended)
        elif extend==1:
            (self.imm_val_for_MuxB, self.imm_val_for_MuxINC) = (BitArray(bin=imm_val_S_type).int, 0)
        # if SB-type instruction (sign-extended)
        elif extend==2:
            (self.imm_val_for_MuxB, self.imm_val_for_MuxINC) = (0, 2*BitArray(bin=imm_val_SB_type).int)
        # if U-type instruction (unsigned)
        elif extend==3:
            (self.imm_val_for_MuxB, self.imm_val_for_MuxINC) = (BitArray(bin=imm_val_U_type).uint, 0)
        # if UJ-type instruction (sign-extended)
        elif extend==4:
            (self.imm_val_for_MuxB, self.imm_val_for_MuxINC) = (0, 2*BitArray(bin=imm_val_UJ_type).int)



class ProcessorMemoryInterface:

    def __init__(self, memory):
        # this is the only memory till now
        # because i have not distinguished between main_memory and cache_memory
        # will do this later
        self.memory = memory
        # memory function completed signal
        self.MFC = False

    # data to be written is coming from register RM
    # this function returns data which is read (one word) and changes MFC_signal to True if Memory function was succesful
    # (MFC_signal makes no sense since clock cannot be implemented in python, and without clock, there is no notion of a cycle,
    # but still, for completeness purposes, i return that signal)
    # data is a binary string of length one word (without any preceeding 0b as start bits)
    def interact_with_memory(self, data, MEM_read, MEM_write, address):

        if MEM_read==True:
            if address in self.memory:
                self.MFC = True
                return self.memory[address+3]+self.memory[address+2]+self.memory[address+1]+self.memory[address]
            else:
                self.MFC = True
                return "00000000000000000000000000000000"

        elif MEM_write==True:
            self.memory[address+3] = bin(data)[0+2:8+2]
            self.memory[address+2] = bin(data)[8+2:16+2]
            self.memory[address+1] = bin(data)[16+2:24+2]
            self.memory[address]   = bin(data)[24+2:32+2]
            self.MFC = True
            # return value is don't care if the code intended to write in memory
            return "00000000000000000000000000000000"

        else:
            self.MFC = True
            # return value is don't care if the code intended to do nothing with memory
            return "00000000000000000000000000000000"



class InstructionAddressGenerator:

    def __init__(self):
        
        self.MuxPC = 0
        self.PC = 0
        self.MuxINC = 0
        self.PC_Temp = 0
        self.Adder = 0

    # remember that branch_offset can be immediate value from either SB-type instruction or UJ-type instruction
    def update_PC_and_PC_Temp(self, RA, branch_offset, PC_select, PC_enable, INC_select):

        if INC_select == 0:
            self.MuxINC = 4
        elif INC_select == 1:
            self.MuxINC = branch_offset

        self.Adder = self.PC + self.MuxINC

        if PC_select == 0:
            self.MuxPC = RA
        elif PC_select == 1:
            self.MuxPC = self.Adder

        if PC_enable == True:
            self.PC_Temp = self.PC
            self.PC = self.MuxPC



class ProcessorWithoutPipeline:

    def __init__(self, memory):
        self.register_file = RegisterFile()
        self.arithmetic_logic_unit = ALU()
        self.processor_memory_interface = ProcessorMemoryInterface(memory)
        self.instruction_decoder = InstructionDecoder()
        self.control_circuitry = ControlCircuitry()
        self.instruction_register = InstructionRegister()
        self.instruction_address_generator = InstructionAddressGenerator()
        self.immediate = Immediate()

        # value of step_counter is:
        # 1, if instruction is in stage1 (fetch)
        # 2, if instruction is in stage2 (decode)
        # 3, if instruction is in stage3 (execute)
        # 4, if instruction is in stage4 (memory)
        # 5, if instruction is in stage5 (writeback)
        self.step_counter = 1

        self.MuxMA = 0
        self.RA = 0
        self.RB = 0
        self.MuxB = 0
        self.RZ = 0
        self.RM = 0
        self.MuxY = 0
        self.RY = 0

    # all signals are always active, but it is not possible to simulate that behavior in python
    # control signals are the ones which select the value in a module would be changed or not
    # so i call many signals many times
    def fetch(self):

        # generate control signals for stage 1
        self.control_circuitry.generate_control_signals(self.instruction_decoder.opcode, self.instruction_decoder.func3, self.instruction_decoder.func7, self.step_counter, self.arithmetic_logic_unit.condition_signal_beq, self.arithmetic_logic_unit.condition_signal_bge, self.arithmetic_logic_unit.condition_signal_bgeu, self.arithmetic_logic_unit.condition_signal_blt, self.arithmetic_logic_unit.condition_signal_bltu, self.arithmetic_logic_unit.condition_signal_bne)

        if self.control_circuitry.MA_select == 0:
            self.MuxMA = self.RZ
        elif self.control_circuitry.MA_select == 1:
            self.MuxMA = self.instruction_address_generator.PC

        self.instruction_register.write_to_instruction_register(self.processor_memory_interface.interact_with_memory(self.RM, self.control_circuitry.MEM_read, self.control_circuitry.MEM_write, self.MuxMA), self.control_circuitry.IR_enable)
        self.instruction_address_generator.update_PC_and_PC_Temp(self.RA, 0, self.control_circuitry.PC_select, self.control_circuitry.PC_enable, self.control_circuitry.INC_select)
        self.step_counter = self.step_counter + 1

    def decode(self):

        # ignore these temporary variables, they are only so that code looks good
        # actually these are hard-wired values
        opcode = self.instruction_register.content[25:]
        func3 = self.instruction_register.content[17:20]
        func7 = self.instruction_register.content[0:7]

        self.instruction_decoder.decode(opcode, func3, func7)

        # generate control signals for stage 2
        self.control_circuitry.generate_control_signals(self.instruction_decoder.opcode, self.instruction_decoder.func3, self.instruction_decoder.func7, self.step_counter, self.arithmetic_logic_unit.condition_signal_beq, self.arithmetic_logic_unit.condition_signal_bge, self.arithmetic_logic_unit.condition_signal_bgeu, self.arithmetic_logic_unit.condition_signal_blt, self.arithmetic_logic_unit.condition_signal_bltu, self.arithmetic_logic_unit.condition_signal_bne)

        # hard wired values
        imm_val_I_type = self.instruction_register.content[0:12]
        imm_val_S_type = self.instruction_register.content[0:7] + self.instruction_register.content[20:25]
        imm_val_SB_type = self.instruction_register.content[0] + self.instruction_register.content[24] + self.instruction_register.content[1:7] + self.instruction_register.content[20:24]
        imm_val_U_type = self.instruction_register.content[0:20]
        imm_val_UJ_type = self.instruction_register.content[0] + self.instruction_register.content[12:20] + self.instruction_register.content[11] + self.instruction_register.content[1:11]

        self.immediate.generate_imm_values_for_MuxB_and_MuxINC(imm_val_I_type, imm_val_S_type, imm_val_SB_type, imm_val_U_type, imm_val_UJ_type, self.control_circuitry.extend)

        # hard wired values
        address_A = int(self.instruction_register.content[12:17],2)
        address_B = int(self.instruction_register.content[7:12],2)

        (self.RA, self.RB) = self.register_file.read_from_register_file(address_A,address_B)

        self.step_counter = self.step_counter + 1

    def execute(self):

        if self.control_circuitry.B_select == 0:
            self.MuxB = self.RB
        elif self.control_circuitry.B_select == 1:
            self.MuxB = self.immediate.imm_val_for_MuxB

        # generate control signals for stage 3
        self.control_circuitry.generate_control_signals(self.instruction_decoder.opcode, self.instruction_decoder.func3, self.instruction_decoder.func7, self.step_counter, self.arithmetic_logic_unit.condition_signal_beq, self.arithmetic_logic_unit.condition_signal_bge, self.arithmetic_logic_unit.condition_signal_bgeu, self.arithmetic_logic_unit.condition_signal_blt, self.arithmetic_logic_unit.condition_signal_bltu, self.arithmetic_logic_unit.condition_signal_bne)

        self.RZ = self.arithmetic_logic_unit.perform_operation(self.RA, self.MuxB, self.control_circuitry.ALU_op)
        self.RM = self.RB

        # generate control signals again, because they might have changed within this step
        # and it may change the next steps
        self.control_circuitry.generate_control_signals(self.instruction_decoder.opcode, self.instruction_decoder.func3, self.instruction_decoder.func7, self.step_counter, self.arithmetic_logic_unit.condition_signal_beq, self.arithmetic_logic_unit.condition_signal_bge, self.arithmetic_logic_unit.condition_signal_bgeu, self.arithmetic_logic_unit.condition_signal_blt, self.arithmetic_logic_unit.condition_signal_bltu, self.arithmetic_logic_unit.condition_signal_bne)

        self.instruction_address_generator.update_PC_and_PC_Temp(self.RA, self.immediate.imm_val_for_MuxINC, self.control_circuitry.PC_select, self.control_circuitry.PC_enable, self.control_circuitry.INC_select)

        self.step_counter = self.step_counter + 1

    def memory_access(self):

        # generate control signals for stage 4
        self.control_circuitry.generate_control_signals(self.instruction_decoder.opcode, self.instruction_decoder.func3, self.instruction_decoder.func7, self.step_counter, self.arithmetic_logic_unit.condition_signal_beq, self.arithmetic_logic_unit.condition_signal_bge, self.arithmetic_logic_unit.condition_signal_bgeu, self.arithmetic_logic_unit.condition_signal_blt, self.arithmetic_logic_unit.condition_signal_bltu, self.arithmetic_logic_unit.condition_signal_bne)

        if self.control_circuitry.MA_select == 0:
            self.MuxMA = self.RZ
        elif self.control_circuitry.MA_select == 1:
            self.MuxMA = self.instruction_address_generator.PC

        memory_data = self.processor_memory_interface.interact_with_memory(self.RM, self.control_circuitry.MEM_read, self.control_circuitry.MEM_write, self.MuxMA)
        return_address = self.instruction_address_generator.PC_Temp

        if self.control_circuitry.Y_select == 0:
            self.MuxY = self.RZ
        elif self.control_circuitry.Y_select == 1:
            self.MuxY = memory_data
        elif self.control_circuitry.Y_select == 2:
            self.MuxY = return_address

        self.RY = self.MuxY

        self.step_counter = self.step_counter + 1

    def writeback(self):

        # generate control signals for stage 5
        self.control_circuitry.generate_control_signals(self.instruction_decoder.opcode, self.instruction_decoder.func3, self.instruction_decoder.func7, self.step_counter, self.arithmetic_logic_unit.condition_signal_beq, self.arithmetic_logic_unit.condition_signal_bge, self.arithmetic_logic_unit.condition_signal_bgeu, self.arithmetic_logic_unit.condition_signal_blt, self.arithmetic_logic_unit.condition_signal_bltu, self.arithmetic_logic_unit.condition_signal_bne)

        address_C = int(self.instruction_register.content[20:25],2)
        self.register_file.write_to_register_file(address_C, self.RY, self.control_circuitry.RF_write)

        self.step_counter = 1



# ------------------------------------------------------------------------------------------------------------



# byte addressable memory is represented as a dictionary that maps every address to a byte
# since memory might be very large, it is illogical to assign space to that location which is not in use

# let us follow the convention that the lowest memory address is 0x00000000 and
# the highest memory address is 0x7ffffffc
memory = dict()

i = 0x00000000

# open the file containing the machine code
file_handle = open("machine_code.mc")

# put these instructions in the memory in form of binary strings
for each_line in file_handle:

    str3 = BitArray(uint=int(each_line[2:4],16), length=8).bin
    str2 = BitArray(uint=int(each_line[4:6],16), length=8).bin
    str1 = BitArray(uint=int(each_line[6:8],16), length=8).bin
    str0 = BitArray(uint=int(each_line[8:10],16), length=8).bin

    (memory[i+3], memory[i+2], memory[i+1], memory[i]) = (str3, str2, str1, str0)

    i = i + 4

Intel_Atom = ProcessorWithoutPipeline(memory)

Intel_Atom.fetch()
Intel_Atom.decode()
Intel_Atom.execute()
Intel_Atom.memory_access()
Intel_Atom.writeback()

print(Intel_Atom.register_file.general_purpose_register)
print(Intel_Atom.processor_memory_interface.memory)