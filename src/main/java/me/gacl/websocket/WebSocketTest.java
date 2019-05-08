package me.gacl.websocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/websocket")
public class WebSocketTest {
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	//concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();

	//与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session){
		this.session = session;
		webSocketSet.add(this);     //加入set中
		addOnlineCount();           //在线数加1
//		try {
////			sendMessage("");
//			RtpTest.session=session;
//			RtpTest.h264Send();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(){
		webSocketSet.remove(this);  //从set中删除
		subOnlineCount();           //在线数减1
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("来自客户端的消息:" + message);
		
//		try {
////			sendMessage("");
//			RtpTest.session=session;
//			RtpTest.h264Send();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//群发消息
//		for(WebSocketTest item: webSocketSet){
			try {
				RtpTest test = new RtpTest();
				test.session = session;
				test.h264Send(message);
			} catch (Exception e) {
				e.printStackTrace();
//				continue;
			}
		}
//	}

	/**
	 * 发生错误时调用
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error){
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * @param message
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public void sendMessage(String message) throws IOException{
		 ByteBuffer buff = ByteBuffer.allocate(102400);
	        FileChannel in = null;
	        try
	        {
	        	File h264 = new File("D:/temp/h264/0_072849.H264");
	            in = new FileInputStream(h264).getChannel();
	            while(in.read(buff) != -1) {
	                buff.flip();
	                this.session.getBasicRemote().sendBinary(buff);
	                buff.clear();
	            }
	        }
	        catch (FileNotFoundException e)
	        {
	            throw e;
	        } finally {
	            try {
	                if(in != null) {
	                    in.close();
	                }
	            } catch(IOException e) {
	                throw e;
	            }
	        }
		
		//this.session.getAsyncRemote().sendText(message);
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocketTest.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocketTest.onlineCount--;
	}
	public static void main(String[] args) {

		 ByteBuffer buff = ByteBuffer.allocate(102400);
		 FileInputStream in = null;
		 int b = 0;
	        try
	        {
	        	File h264 = new File("D:/temp/h264/0_072849.H264");
	            in = new FileInputStream(h264);
	            while((b=in.read()) != -1) {
	            	buff.flip();
	            	System.out.print((char)b);
	                buff.clear();
	            }
	        }
	        catch (Exception e)
	        {
	            System.out.println(e.getMessage());
	        } finally {
	            try {
	                if(in != null) {
	                    in.close();
	                }
	            } catch(IOException e) {
	            	System.out.println(e.getMessage());
	            }
	        }
		
		//this.session.getAsyncRemote().sendText(message);
	
	}
}
