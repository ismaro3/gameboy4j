package com.ismaelrh.gameboy.cpu.instructions;

import com.ismaelrh.gameboy.Instruction;
import com.ismaelrh.gameboy.Memory;
import com.ismaelrh.gameboy.cpu.Registers;
import com.ismaelrh.gameboy.cpu.instructions.Load8b;
import org.junit.Before;
import org.junit.Test;

import static com.ismaelrh.gameboy.TestUtils.*;
import static com.ismaelrh.gameboy.cpu.Registers.*;
import static org.junit.Assert.assertEquals;

public class Load8BTest {

    private Load8b load8B;
    private Registers registers;
    private Memory memory;

    @Before
    public void setUp() {
        registers = new Registers();
        memory = new Memory();
        load8B = new Load8b(registers, memory);
        registers.setA((byte) 0xFF);
        registers.setB((byte) 0xFF);
        registers.setC((byte) 0xFF);
        registers.setD((byte) 0xFF);
        registers.setE((byte) 0xFF);
        registers.setH((byte) 0xFF);
        registers.setL((byte) 0xFF);
    }

    @Test
    public void loadRR() {
        //b <- a, h <- b
        registers.setA((byte) 0x12);

        short cycles = load8B.loadRR(new Instruction(getOpcode(0x3, B, A)));
        load8B.loadRR(new Instruction(getOpcode(0x3, H, B)));
        assertEquals8(0x12, registers.getB());
        assertEquals8(0x12, registers.getH());
        assertEquals(4, cycles);

    }

    @Test
    public void loadRImmediate() {
        //b <- 0xBE
        //h <- 0xEF
        Instruction inst1 = new Instruction(getOpcode(0x3, B, NONE), (byte) 0xBE);
        Instruction inst2 = new Instruction(getOpcode(0x3, H, NONE), (byte) 0xEF);
        short cycles = load8B.loadRImmediate(inst1);
        load8B.loadRImmediate(inst2);
        assertEquals8(0xBE, registers.getB());
        assertEquals8(0xEF, registers.getH());
        assertEquals(8, cycles);
    }

    @Test
    public void loadRHL() {
        //HL = 0xC003, (HL) = 0x12, b <- 0x12
        memory.write((char) 0xC003, (byte) 0x12);
        registers.setHL((char) 0xC003);

        Instruction inst = new Instruction(getOpcode(0x3, B, NONE));
        short cycles = load8B.loadRHL(inst);
        assertEquals8(0x12, registers.getB());
        assertEquals(8, cycles);
    }

    @Test
    public void loadHLR() {
        //HL = 0xC003, b = 0x12, (HL) <- 0x12
        registers.setHL((char) 0xC003);
        registers.setB((byte) 0x12);
        Instruction inst = new Instruction(getOpcode(0x3, NONE, B));
        short cycles = load8B.loadHLR(inst);
        assertEquals8(0x12, memory.read((char) 0xC003));
        assertEquals(8, cycles);
    }

    @Test
    public void loadHLN() {
        //HL = 0xC003, (HL) <- 0x12
        registers.setHL((char) 0xC003);
        Instruction inst = new Instruction((byte) 0x0, (byte) 0x12); //Don't care about operands
        short cycles = load8B.loadHLN(inst);
        assertEquals8(0x12, memory.read((char) 0xC003));
        assertEquals(12, cycles);
    }

    @Test
    public void loadA_BC() {
        //BC = 0xC003, (BC) = 0x2F, A <- 0x2F
        registers.setBC((char) 0xC003);
        memory.write((char) 0xC003, (byte) 0x2F);
        short cycles = load8B.loadA_BC();

        assertEquals8(0x2F, registers.getA());
        assertEquals(8, cycles);
    }

    @Test
    public void loadA_DE() {
        //BC = 0xC003, (BC) = 0x5F, A <- 0x5F
        registers.setDE((char) 0xC003);
        memory.write((char) 0xC003, (byte) 0x5F);
        short cycles = load8B.loadA_DE();

        assertEquals8(0x5F, registers.getA());
        assertEquals(8, cycles);
    }

    @Test
    public void loadA_nn() {
        //(0xC003) <- 0x12, A <- 0x12
        memory.write((char) 0xC003, (byte) 0x12);

        Instruction inst = new Instruction((byte) 0x0, (char) 0xC003); //Don't care about operands

        short cycles = load8B.loadA_nn(inst);

        assertEquals8(0x12, registers.getA());
        assertEquals(16, cycles);
    }

    @Test
    public void loadBC_A() {
        //BC = 0xC003, A = 0x3F, (BC) <- 0x3F
        registers.setBC((char) 0xC003);
        registers.setA((byte) 0x3F);
        short cycles = load8B.loadBC_A();
        assertEquals8(0x3F, memory.read((char) 0xc003));
        assertEquals(8, cycles);
    }


    @Test
    public void loadDE_A() {
        //de = 0xC003, A = 0x3F, (de) <- 0x3F
        registers.setDE((char) 0xC003);
        registers.setA((byte) 0x3F);
        short cycles = load8B.loadDE_A();
        assertEquals8(0x3F, memory.read((char) 0xc003));
        assertEquals(8, cycles);
    }

    @Test
    public void loadNN_A() {
        //NN= 0xC003, A = 0x3F, (BC) <- 0x3F
        registers.setA((byte) 0x3F);
        Instruction inst = new Instruction((byte) 0x0, (char) 0xC003); //Don't care about operands

        short cycles = load8B.loadNN_A(inst);
        assertEquals8(0x3F, memory.read((char) 0xc003));
        assertEquals(16, cycles);
    }

    @Test
    public void loadA_C() {
        //C = 0x32, (FF32) = 0x12, A <- 0x12
        registers.setC((byte) 0x32);
        memory.write((char) 0xFF32, (byte) 0x12);
        short cycles = load8B.loadA_C();
        assertEquals8(0x12, registers.getA());
        assertEquals(8, cycles);
    }

    @Test
    public void loadA_n() {
        //n = 0x32, (FF32) = 0x12, A <- 0x12
        memory.write((char) 0xFF32, (byte) 0x12);
        Instruction inst = new Instruction((byte) 0x0, (byte) 0x32); //Don't care about operands
        short cycles = load8B.loadA_n(inst);
        assertEquals8(0x12, registers.getA());
        assertEquals(12, cycles);
    }

    @Test
    public void loadC_A() {
        //C = 0x32, A = 0x12, (0xFF32) <- 0x12
        registers.setC((byte) 0x32);
        registers.setA((byte) 0x12);
        short cycles = load8B.loadC_A();
        assertEquals8(0x12, memory.read((char) 0xFF32));
        assertEquals(8, cycles);
    }

    @Test
    public void loadN_A() {
        //n = 0x32, A = 0x12, (0xFF32) <- 0x12
        registers.setA((byte) 0x12);
        Instruction inst = new Instruction((byte) 0x0, (byte) 0x32); //Don't care about operands
        short cycles = load8B.loadN_A(inst);
        assertEquals8(0x12, memory.read((char) 0xFF32));
        assertEquals(12, cycles);
    }

    @Test
    public void loadHLI_A() {
        //HL = 0xC003, A = 0x12, (0xC003) <- 0x12, HL <- 0xC004
        registers.setHL((char) 0xC003);
        registers.setA((byte) 0x12);
        short cycles = load8B.loadHLI_A();
        assertEquals8(0x12, memory.read((char) 0xC003));
        assertEquals16(0xC004, registers.getHL());
        assertEquals(8, cycles);

        //HL = 0xFFFF, A = 0x13, (0xFFFF) <- 0x13, HL <- 0x0000
        registers.setHL((char) 0xFFFF);
        registers.setA((byte) 0x13);
        load8B.loadHLI_A();
        assertEquals8(0x13, memory.read((char) 0xFFFF));
        assertEquals16(0x0000, registers.getHL());
    }

    @Test
    public void loadHLD_A() {
        //HL = 0xC003, A = 0x12, (0xC003) <- 0x12, HL <- 0xC002
        registers.setHL((char) 0xC003);
        registers.setA((byte) 0x12);
        short cycles = load8B.loadHLD_A();
        assertEquals8(0x12, memory.read((char) 0xC003));
        assertEquals16(0xC002, registers.getHL());
        assertEquals(8, cycles);

        //HL = 0x0000, A = 0x13, (0x0000) <- 0x13, HL <- 0xFFFF
        registers.setHL((char) 0x0000);
        registers.setA((byte) 0x13);
        load8B.loadHLD_A();
        assertEquals8(0x13, memory.read((char) 0x0000));
        assertEquals16(0xFFFF, registers.getHL());
    }

    @Test
    public void loadA_HLI() {
        //HL = 0xC003, (HL) = 0x12, A <- 0x12, HL <- 0xC004
        registers.setHL((char) 0xC003);
        memory.write((char) 0xC003, (byte) 0x12);
        short cycles = load8B.loadA_HLI();
        assertEquals8(0x12, registers.getA());
        assertEquals16(0xC004, registers.getHL());
        assertEquals(8, cycles);

        //HL = 0xFFFF, (HL) = 0x13, A <- 0x13, HL <- 0x0000
        registers.setHL((char) 0xFFFF);
        memory.write((char) 0xFFFF, (byte) 0x13);
        load8B.loadA_HLI();
        assertEquals8(0x13, registers.getA());
        assertEquals16(0x0000, registers.getHL());
    }

    @Test
    public void loadA_HLD() {
        //HL = 0xC003, (HL) = 0x12, A <- 0x12, HL <- 0xC002
        registers.setHL((char) 0xC003);
        memory.write((char) 0xC003, (byte) 0x12);
        short cycles = load8B.loadA_HLD();
        assertEquals8(0x12, registers.getA());
        assertEquals16(0xC002, registers.getHL());
        assertEquals(8, cycles);

        //HL = 0x0000, (HL) = 0x13, A <- 0x13, HL <- 0xFFFF
        registers.setHL((char) 0x0000);
        memory.write((char) 0x0000, (byte) 0x13);
        load8B.loadA_HLD();
        assertEquals8(0x13, registers.getA());
        assertEquals16(0xFFFF, registers.getHL());
    }

}