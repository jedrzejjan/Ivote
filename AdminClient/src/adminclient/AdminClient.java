package adminclient;

import java.net.MalformedURLException;
import java.rmi.*;

import ivote.*;

public class AdminClient {

	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		
		try {
			AdminRMIInterface ci = (AdminRMIInterface) Naming.lookup("admin");
			
			AdminInterface inter = new AdminInterface(ci);
			ci.subscribeAdmin(inter);
			inter.execute();
			//ci.createElectionNucleo(LocalDateTime.now(), LocalDateTime.now(), "Eleiçoes Nucleo Eng.Info.", "Pequena descriçao", new Department("DEI"));
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Can't connect to server. IT'S IMPERATIVE THAT YOU DONT MOVE A FINGER!");
		}
	}
}
