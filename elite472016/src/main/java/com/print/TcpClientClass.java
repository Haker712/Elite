package com.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

public class TcpClientClass {

	private static final String TAG = "TcpClientClass";
	static Socket client;
	public OutputStream outputStream=null;
	public InputStream inputStream=null;
	public void Connect(String serverAddr,int port)
	{
		SocketAddress my_sockaddr = new InetSocketAddress(serverAddr, port);

		client = new Socket();
		try {
			client.connect(my_sockaddr,5000);
			outputStream = client.getOutputStream();
			inputStream = client.getInputStream();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.getMessage());		
		}
	}
	public void DisConnect()
	{
		try {
			if(outputStream!=null)
			{
			outputStream.close();
			}
			if(inputStream!=null)
			{
			inputStream.close();
			}
			if(client!=null)
			{
			client.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.getMessage());	
		}
	}
	
	
	public void SendData(byte[] bt)
    {
    	try 
		{
			outputStream.write(bt);
			outputStream.flush();				
		} 
		catch (NumberFormatException e) 
		{
					// TODO Auto-generated catch block
			Log.d(TAG, e.getMessage());
		} 
		catch (IOException e) {
			Log.d(TAG, e.getMessage());			
		}
    }
}
