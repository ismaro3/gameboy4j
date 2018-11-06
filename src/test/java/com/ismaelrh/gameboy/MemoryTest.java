package com.ismaelrh.gameboy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MemoryTest {

	private final static byte ZERO = 0x00;

	private Memory memory = new Memory();

	@Before
	public void setUp() {
		memory.clear();
	}

	@Test
	public void accessCartridge() {
		assertDataIsStoredAndReadCorrectly((char) 0x0000, memory.getCartridge(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0x0003, memory.getCartridge(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0x7FFF, memory.getCartridge(), (byte) 0x20, 32767);
	}

	@Test
	public void accessVideoRAM() {
		assertDataIsStoredAndReadCorrectly((char) 0x8000, memory.getVideoRAM(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0x8003, memory.getVideoRAM(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0x9FFF, memory.getVideoRAM(), (byte) 0x20, 8191);
	}

	@Test
	public void accessExternalRAM() {
		assertDataIsStoredAndReadCorrectly((char) 0xA000, memory.getExternalRAM(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0xA003, memory.getExternalRAM(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0xBFFF, memory.getExternalRAM(), (byte) 0x20, 8191);
	}

	@Test
	public void accessInternalRAM() {
		assertDataIsStoredAndReadCorrectly((char) 0xC000, memory.getInternalRAM(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0xC003, memory.getInternalRAM(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0xDFFF, memory.getInternalRAM(), (byte) 0x20, 8191);
	}

	@Test
	public void accessEchoInternalRAM() {
		assertDataIsStoredAndReadCorrectly((char) 0xE000, memory.getInternalRAM(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0xE003, memory.getInternalRAM(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0xFDFF, memory.getInternalRAM(), (byte) 0x20, 7679);
	}

	@Test
	public void accessSpriteRAM() {
		assertDataIsStoredAndReadCorrectly((char) 0xFE00, memory.getSpriteRAM(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0xFE03, memory.getSpriteRAM(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0xFE9F, memory.getSpriteRAM(), (byte) 0x20, 159);
	}

	@Test
	public void accessIORAM() {
		assertDataIsStoredAndReadCorrectly((char) 0xFF00, memory.getIoRAM(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0xFF03, memory.getIoRAM(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0xFF7F, memory.getIoRAM(), (byte) 0x20, 127);
	}

	@Test
	public void accessHighRAM() {
		assertDataIsStoredAndReadCorrectly((char) 0xFF80, memory.getHighRAM(), (byte) 0x18, 0);
		assertDataIsStoredAndReadCorrectly((char) 0xFF83, memory.getHighRAM(), (byte) 0x19, 3);
		assertDataIsStoredAndReadCorrectly((char) 0xFFFE, memory.getHighRAM(), (byte) 0x20, 126);
	}

	@Test
	public void accessInterruptEnable() {
		assertEquals(ZERO, memory.read((char) 0xFFFF));
		memory.write((char) 0xFFFF, (byte) 0x19);
		assertEquals(0x19, memory.getInterruptEnable());
		assertEquals(0x19, memory.read((char) 0xFFFF));
	}

	@Test
	public void fullRAMWriteAndRead() {

		//Write from 0000 until E000 (echo RAM)
		for (char address = 0x0000; address < 0xE000; address++) {
			//Store less-significant byte
			byte toWrite = (byte) (address & 0xFF);
			memory.write(address, toWrite);
		}

		//Write from FE00 until FE9F (until unusable zone)
		for (char address = 0xFE00; address < 0xFE9F; address++) {
			//Store less-significant byte
			byte toWrite = (byte) (address & 0xFF);
			memory.write(address, toWrite);
		}

		//Write from FF00 until FFFF (until unusable zone)
		for (char address = 0xFF00; address < 0xFFFF; address++) {
			//Store less-significant byte
			byte toWrite = (byte) (address & 0xFF);
			memory.write(address, toWrite);
		}

		//Read from 0000 until E000
		for (char address = 0x0000; address < 0xE000; address++) {
			//Store less-significant byte
			byte toRead = (byte) (address & 0xFF);
			assertEquals(toRead, memory.read(address));
		}

		//Read from FE00 until FE9F (until unusable zone)
		for (char address = 0xFE00; address < 0xFE9F; address++) {
			//Store less-significant byte
			byte toRead = (byte) (address & 0xFF);
			assertEquals(toRead, memory.read(address));
		}

		//Read from FF00 until FFFF (until unusable zone)
		for (char address = 0xFF00; address < 0xFFFF; address++) {
			//Store less-significant byte
			byte toRead = (byte) (address & 0xFF);
			assertEquals(toRead, memory.read(address));
		}

	}

	private void assertDataIsStoredAndReadCorrectly(char absoluteAddress, byte[] internalStructure, byte valueToWrite, int relativeIndex) {
		assertEquals(ZERO, memory.read(absoluteAddress));
		memory.write(absoluteAddress, valueToWrite);
		assertEquals(valueToWrite, internalStructure[relativeIndex]);
		assertEquals(valueToWrite, memory.read(absoluteAddress));
	}
}
