package com.elex.newmq.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import com.elex.newmq.share.ShareVariable;

public class MessageStore {
	
	private FileChannel writer;
	private File file;
	private byte[] buffer = new byte[4];
	private ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
	
	public void init() throws FileNotFoundException{
		file = new File("logs/newmqdata.log");
		writer = (new FileOutputStream(file, true)).getChannel();
	}
	
	public Boolean addValue(String value) throws IOException{
		ByteBuffer buffer = ByteBuffer.allocate(value.length() + 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(value.length());
		buffer.put(value.getBytes());
		buffer.flip();
		int re = writer.write(buffer);
		if(re <= 0)
			throw new IOException("write error");
		//����curFileOffset
	    ShareVariable.curFileOffset.getAndAdd(4 + value.length());
		return true;
	}
	
	public FileChannel getWriter(){
		return this.writer;
	}
	/**
	 * �ӳ־û��ļ��лָ�����
	 * @return
	 */
	public void replay(){
		
	}
	/**
	 * �ӳ־û��ļ���ȡ����������
	 * @param in �ļ���Ӧ��filechannel
	 * @return ����json��ʽ���ַ���
	 * @throws IOException
	 */
	private String readItem(FileChannel in) throws IOException{
		//���Ȼ�ȡ���ݶεĳ��ȣ����ݳ���ȥȡ�ö�Ӧ���ݡ�
		byteBuffer.rewind();
	    byteBuffer.limit(4);
	    int x = 0;
	    do{
	    	x = in.read(byteBuffer);
	    }while(byteBuffer.position() < byteBuffer.limit() && x >=0);
	    if( x < 0 ){
	    	throw new IOException("Unexpected EOF");
	    }
	    byteBuffer.rewind();
	    int dataLength = byteBuffer.getInt();
	    //�������ݳ���ȥȡ�ö�Ӧ�����ݶ�
	    byte[] data = new byte[dataLength];
	    ByteBuffer dataBuffer = ByteBuffer.wrap(data);
	    x = 0;
	    do{
	    	x = in.read(dataBuffer);
	    }while(dataBuffer.position() < dataBuffer.limit() && x >=0);
	    //����curFileOffset
	    ShareVariable.curFileOffset.getAndAdd(4 + dataLength);
	    
		return data.toString();
	}
	//�������е������ύ��Ӳ����
	public void fsync() throws IOException{
		this.writer.force(false);
	}
	//�Ƿ�����fsync������
	public Boolean canFsync(){
		return true;
	}
}
