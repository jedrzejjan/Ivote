import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

public class FailoverMec extends Thread {
	private MainServer sv;
	private final int portPrim = 5555;
	private final int portSec = 5556;
	private int otherPort;
	private InetAddress backupSvIp;
	private DatagramSocket ds;
	private int bufferSize = 1024;
	private byte[] buffer = new byte[bufferSize];
	private final static int timeBetweenMessages = 2000;
	private final static int timeAllowedBeforeBackup = 10000;
	
	public FailoverMec(MainServer sv) {
		this.sv = sv;
		try {
			backupSvIp = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			try { 
				ds = new DatagramSocket(portPrim);
				otherPort = portSec;
			}
			catch(BindException e) {
				ds = new DatagramSocket(portSec);
				otherPort = portPrim;
			}
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			if(sv.getIsPrimary()) {
				doMainSvStuff();
			}
			else {
				doBackupSvStuff();
			}
		}
	}
	
	private void doMainSvStuff() {
		//Write Date to the buffer
		try {
			ds.setSoTimeout(timeBetweenMessages);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		String s = sv.getTimeBecomeMain().toString();
		for(int i=0; i<s.length(); i++) {
			buffer[i] = (byte) s.charAt(i);
		}
		while(true) {
			try {
				//System.out.println(buffer.length + " length: " + bufferSize);
				ds.send(new DatagramPacket(buffer, s.length(), backupSvIp, otherPort));
				//System.out.println("Sent message");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				DatagramPacket p = new DatagramPacket(buffer, buffer.length);
				ds.receive(p);
				String received = new String(p.getData(), 0, p.getLength());
				LocalDateTime rec = LocalDateTime.parse(received);
				if(rec.isAfter(sv.getTimeBecomeMain())) {
					break;
				}
			} catch(SocketTimeoutException e) {
				continue;
			} 	
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		sv.setAsSecondary();
	}
	
	private void doBackupSvStuff() {
		try {
			ds.setSoTimeout(timeAllowedBeforeBackup);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while(true) {
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			try {
				ds.receive(p);
				//System.out.println("Received message!");
			}
			catch(SocketTimeoutException e) {
				break;
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		sv.setAsPrimary();
	}
}

