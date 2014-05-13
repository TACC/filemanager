/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.client.transfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.globus.io.streams.GlobusOutputStream;

@SuppressWarnings("unused")
public class GlobusRandomFileOutputStream extends GlobusOutputStream {

	private RandomAccessFile randomFile = null;
	
	private final static int BUFFERSIZE = 1024 * 128;
	
	private byte[] buffer = new byte[BUFFERSIZE];
	
	private int startPos = 0;
	
	private int endPos = 0;
	
	int flushcount = 0;
	
	public GlobusRandomFileOutputStream(String filename, boolean append) throws FileNotFoundException, IOException {
		super();
		filename = filename.replaceAll("\\s", "\\ ");
		randomFile = new RandomAccessFile(filename, "rw");
		if (append) {
			File file = new File(filename);
			if (file.exists()) {
				randomFile.seek(file.length());		
			} else {
				randomFile.seek(0);
			}
		}
	}

	
	public void abort() {
		super.abort();
		try {
			randomFile.close();
		} catch (Exception e) {}	
	}

	
	public void write(int b) throws IOException {
		randomFile.write(b);
	}

	
	public void flush() throws IOException {
//		super.flush();
//		System.out.println("write " + this.endPos + "  " + this.flushcount++);
//		randomFile.write(this.buffer, 0, this.endPos);
//		this.startPos = 0;
//		this.endPos = 0;
	}

	
	public void write(byte[] msg, int off, int len) throws IOException {
		randomFile.write(msg, off, len);
//		writeBuffer(msg, off, len);
	}


	public void write(byte[] msg) throws IOException {
		// TODO Auto-generated method stub
		write(msg, 0, msg.length);
	}
	
	
	public void seek(long pos) throws IOException {
		randomFile.seek(pos);
	}


	public void close() throws IOException {
		randomFile.close();
	}

	private void writeBuffer(byte[] msg, int off, int len) throws IOException {
		int lenRemain = len;
		int srcPos = off;		
		int bufRemainLen = BUFFERSIZE - this.startPos;
		
		while (lenRemain >= bufRemainLen) {
//			System.out.println("lenRemain: " + lenRemain + "   bufRemainLen: " + bufRemainLen + "  srcPos: " + srcPos + "  startPos: " + startPos);
			System.arraycopy(msg, srcPos, this.buffer, this.startPos, bufRemainLen);
			this.endPos = BUFFERSIZE;
			this.flush();
			this.startPos = 0;
			lenRemain -= bufRemainLen;
			srcPos += bufRemainLen;
			bufRemainLen = BUFFERSIZE;			
		}
		
		if (lenRemain > 0) {
			System.arraycopy(msg, srcPos, this.buffer, 0, lenRemain);
			this.startPos += lenRemain;
			this.endPos = this.startPos;
			
//			for (int i = 0; i < lenRemain; i++) {
//				if (msg[i] != this.buffer[i])
//					System.out.print(0);
//				else
//					System.out.print(1);
//			}
//			System.out.println("  end " + lenRemain + "  " + this.endPos);
//			
//			sggc.utils.LogManager.debug("msg: " + new String(msg, 0, msg.length) + "    len: " + lenRemain);
//			sggc.utils.LogManager.debug("buf: " + new String(this.buffer, 0, this.startPos) + "    len: " + this.startPos);
			
		}
	}
	
}
