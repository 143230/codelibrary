package com.bigfile.readwrite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 * @author Administrator
 *	大数据处理(80.2G文件用时813727ms，一共1261676971行数据，CPU，i5-4590:3.3GHZ,缓存10KB)
 */

public class BigFileNioReader {
	private int BUFFER_SIZE = 10240;
	private byte[] bt;
	private FileChannel fileChannel = null;
	private RandomAccessFile file = null;
	private ByteBuffer buffer = null;
	private String bufferString = "";
	private StringBuffer strBuffer = null;
	private int fromIndex=0;
	public BigFileNioReader(String path) throws IOException {
		this(path,-1);
	}
	@SuppressWarnings("resource")
	public BigFileNioReader(String path,int size) throws IOException {
		if(size>0){
			BUFFER_SIZE = size;
		}
		file = new RandomAccessFile(path, "r");
		fileChannel = file.getChannel();
		buffer = ByteBuffer.allocate(BUFFER_SIZE);
		bt = new byte[BUFFER_SIZE];
		strBuffer = new StringBuffer("");
	}
	private String getLine(){
		int endIndex=-1;
		if((endIndex = bufferString.indexOf("\n",fromIndex))!=-1){
			strBuffer.append(bufferString,fromIndex,endIndex+1);
			fromIndex = endIndex+1;
			String line = strBuffer.toString();
			strBuffer.delete(0, strBuffer.length());
			return line;
		}
		strBuffer.append(bufferString,fromIndex,bufferString.length());
		bufferString="";
		fromIndex=0;
		return null;
	}
	public String readLine() throws IOException{
		String line=null;
		if((line = getLine())!=null){
			return line;
		}
		while((fileChannel.read(buffer))!=-1){
			int pos = buffer.position();
			buffer.rewind();
			buffer.get(bt);
			buffer.clear();
			bufferString = new String(bt, 0, pos);
			
			if((line = getLine())!=null){
				return line;
			}
		}
		if(strBuffer.length()==0)return null;
		line = strBuffer.toString();
		strBuffer.delete(0, strBuffer.length());
		return line;
	}
	public void close() throws IOException{
		if(fileChannel!=null){
			fileChannel.close();
		}
		if(file!=null){
			file.close();
		}
	}
	public static void main(String[] args) {
		try {
			BigFileNioReader reader = new BigFileNioReader("E:\\freebase\\freebase");
			String line = null;
			long start = System.currentTimeMillis();
			int i=0;
			while( (line = reader.readLine())!=null ){
				i++;
				if(i%10000000==0){
					System.out.println(i+"\t"+(System.currentTimeMillis()-start));
					System.out.print(line);
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("Using time(minutes): "+(end - start));
			System.out.println(i);
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
