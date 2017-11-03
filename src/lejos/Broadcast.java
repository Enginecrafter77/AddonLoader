package lejos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Deprecated
public class Broadcast {
	
	public static void broadcast(String message)
	{
		try
		{
			DatagramSocket c = new DatagramSocket();
			c.setBroadcast(true);
			
			byte[] sendData = message.getBytes();
			
			try
			{
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), Reference.BROADCAST_PORT);
				c.send(sendPacket);
				//System.out.println("Request packet sent to: 255.255.255.255");
			}
			catch(Exception e){}
			
			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements())
			{
				NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();
				
				if(networkInterface.isLoopback() || !networkInterface.isUp())
				{
					continue;//Don't want to broadcast to the loopback interface or interface that is down
				}
				
				for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
				{
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if(broadcast == null) continue;
					
					// Send the broadcast packet.
					try
					{
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Reference.BROADCAST_PORT);
						c.send(sendPacket);
					}
					catch (Exception e)
					{
						System.err.println("Exception sending to : " + networkInterface.getDisplayName() + " : "+ e);
					}
					
					//System.out.println("Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				}
			}
			c.close();
		}
		catch(IOException ex)
		{
			System.err.println("Exception opening socket : " + ex);
		}
	}
}
