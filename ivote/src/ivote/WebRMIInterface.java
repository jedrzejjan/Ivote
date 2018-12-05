package ivote;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface WebRMIInterface extends Remote {
	//The point is to get all active elections of that day but only show the active ones
	////Everyday at 00:00 if on it authenticates again
	public Person authenticateAndGetUser(String numCc, String password) throws RemoteException;
}
