package codelibrary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BigFileNioWriter {
	private int BUFFER_SIZE = 10240000;
	private FileChannel fileChannel = null;
	private ByteBuffer buffer = null;
	private RandomAccessFile file = null;
	private int fromIndex  = 0;
	private int endIndex = 0;
	/**
	 * "r"	以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。
	 * "rw"	打开以便读取和写入。如果该文件尚不存在，则尝试创建该文件。
	 * "rws"	打开以便读取和写入，对于 "rw"，还要求对文件的内容或元数据的每个更新都同步写入到底层存储设备。
	 * "rwd"  	打开以便读取和写入，对于 "rw"，还要求对文件内容的每个更新都同步写入到底层存储设备。
	 * @param path
	 * @throws IOException 
	 */
	public BigFileNioWriter(String path) throws IOException {
		this(path, 10240000, "rw");
	}
	public BigFileNioWriter(String path,int size) throws IOException{
		this(path, size, "rw");
	}
	public BigFileNioWriter(String path,int size,String open_method) throws IOException{
		BUFFER_SIZE = size;
		file = new RandomAccessFile(path,open_method);
		fileChannel = file.getChannel();
		fileChannel.truncate(0);
		buffer = ByteBuffer.allocate(BUFFER_SIZE);
	}
	
	public void write(String data) throws IOException{
		write(data.getBytes());
	}
	public void write(byte[] data) throws IOException{
		fromIndex=0;
		endIndex = data.length;
		while(fromIndex < endIndex){
			int capacity = buffer.capacity();
			int position = buffer.position();
			if(capacity-position <= endIndex-fromIndex){
				buffer.put(data, fromIndex, capacity-position);
				fromIndex+=capacity-position;
				buffer.flip();
				fileChannel.write(buffer);
				buffer.clear();
			}else{
				buffer.put(data,fromIndex,endIndex - fromIndex);
				fromIndex = endIndex;
			}
		}
	}
	public void close() throws IOException{
		if(fileChannel!=null){
			fileChannel.force(true);
			fileChannel.close();
		}
		if(file!=null){
			file.close();
		}
	}
	public static void main(String[] args) {
		try {
			int nums=0;
			BigFileNioReader reader = new BigFileNioReader("E:\\freebase\\freebase");
			BigFileNioWriter writer = null;
			String line = null;
			long start = System.currentTimeMillis();
			int i=0;
			while( (line = reader.readLine())!=null ){
				if(i%10000000==0){
					if(writer!=null)writer.close();
					writer = new BigFileNioWriter("E:\\freebase\\split\\"+String.format("%08d", nums));
					nums++;
					System.out.println(i+"\t"+(System.currentTimeMillis()-start));
					System.out.print(line);
				}
				i++;
				writer.write(line);
			}
			long end = System.currentTimeMillis();
			System.out.println("Using time(minutes): "+(end - start));
			System.out.println(i);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
