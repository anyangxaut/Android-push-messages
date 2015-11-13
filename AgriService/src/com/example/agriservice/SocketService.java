package com.example.agriservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.activity.LoginActivity;

public class SocketService extends Service {
	
	private static final String TAG = "SocketService";
	private static final long HEART_BEAT_RATE = 30 * 1000;

	public static final String HOST = "202.200.119.162";
	public static final int PORT = 9999;
	
	public static final String MESSAGE_ACTION="com.example.message_ACTION";
	public static final String HEART_BEAT_ACTION="com.example.heart_beat_ACTION";
	
	private ReadThread mReadThread;

	private LocalBroadcastManager mLocalBroadcastManager;

	private WeakReference<Socket> mSocket;

	// For heart Beat
	private Handler mHandler = new Handler();
	
	private Runnable heartBeatRunnable = new Runnable() {

		@Override
		public void run() {

			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
				
				JSONObject jsonmsg = new JSONObject();
				
				try {
					jsonmsg.put("Type", 3);
					jsonmsg.put("MessageContent", "");
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				boolean isSuccess = sendMsg("#" + jsonmsg.toString() + "#");//就发送一个\r\n过去 如果发送失败，就重新初始化一个socket
				Log.d("Json3", jsonmsg.toString());
				if (!isSuccess) {
					//将线程heartBeatRunnable与当前handler解除绑定 
					mHandler.removeCallbacks(heartBeatRunnable);
					//清理对象所占用内存
					mReadThread.release();
					//关闭mSocket，释放资源
					releaseLastSocket(mSocket);
					new InitSocketThread().start();
				}
			}
			 //每HEART_BEAT_RATE秒执行一次
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};

	private long sendTime = 0L;
	private IBackService.Stub iBackService = new IBackService.Stub() {

		@Override
		public boolean sendMessage(String message) throws RemoteException {
			return sendMsg(message);
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind");
		return iBackService;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		new InitSocketThread().start();
		mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
		
	}
	

	private void initSocket() {//初始化Socket
		try {
			Socket so = new Socket(HOST, PORT);
			mSocket = new WeakReference<Socket>(so);
			JSONObject jsonmsg = new JSONObject();
			
			try {
				jsonmsg.put("Type", 2);
				jsonmsg.put("MessageContent", LoginActivity.username);
				sendMsg("#" + jsonmsg.toString() + "#");
				Log.d("Json2", jsonmsg.toString());
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mReadThread = new ReadThread(so);
			mReadThread.start();
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean sendMsg(String msg) {
		if (null == mSocket || null == mSocket.get()) {
			return false;
		}
		Socket soc = mSocket.get();
		try {
			if (!soc.isClosed() && !soc.isOutputShutdown()) {
				OutputStream os = soc.getOutputStream();
				String message = msg ;
				os.write(message.getBytes());
				os.flush();
				sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (!sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			
//			new Thread(new Runnable() {  
//		        @Override  
//		        public void run() {  
//		            // 开始执行后台任务  
//		        }  
//		    }).start();
			initSocket();
		}
	}

	// Thread to read content from Socket
	class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			releaseLastSocket(mWeakSocket);
		}

		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			if (null != socket) {
				try {
					InputStream is = socket.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown()
							&& isStart && ((length = is.read(buffer)) != -1)) {
						if (length > 0) {
							String message = new String(Arrays.copyOf(buffer,
									length)).trim();
							Log.e(TAG, message);
							//收到服务器过来的消息，就通过Broadcast发送出去
							if(message.equals("ok")){//处理心跳回复
								Intent intent=new Intent(HEART_BEAT_ACTION);
								mLocalBroadcastManager.sendBroadcast(intent);
							}else{
								//其他消息回复
								Intent intent=new Intent(MESSAGE_ACTION);
								intent.putExtra("message", message);
								mLocalBroadcastManager.sendBroadcast(intent);
								
								 try {
					                 //将msg转化为json对象
					        	     JSONObject json = new JSONObject(message);
					                 // ContentValues对象的方法
								    String messageid = json.getString("msgId");
								    
								    JSONObject jsonmsg = new JSONObject();
									
									try {
										jsonmsg.put("Type", 4);
										jsonmsg.put("MessageId", messageid);
									
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									sendMsg(jsonmsg.toString());

						          
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
								
							}
							
//							Log.d("messageinfo", message);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		Log.d(TAG, "onDestroy");
		//将线程heartBeatRunnable与当前handler解除绑定 
		mHandler.removeCallbacks(heartBeatRunnable);
		//清理对象所占用内存
		mReadThread.release();
		//关闭mSocket，释放资源
		releaseLastSocket(mSocket);
	}

}
