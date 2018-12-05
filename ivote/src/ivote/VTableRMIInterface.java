package ivote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.Remote;

public interface VTableRMIInterface extends Remote {
	//The point is to get all active elections of that day but only show the active ones
	////Everyday at 00:00 if on it authenticates again
	public boolean authenticateVotingTable(String depName) throws RemoteException;
	
	public ArrayList<Department> getAllDepartments() throws RemoteException;
	
	
	public ArrayList<Election> getVTableElections(String departmentName) throws RemoteException;
	public boolean userCanVote(String numCc, String electionTitle, String locDepartmentName) throws RemoteException;
	
	//Returns null if can't authenticate
	public boolean authenticateUser(String numCc, String password) throws RemoteException;
	public ArrayList<List> getAllListsOfElection(String electionTitle) throws RemoteException;


	public boolean vote(String departmentName, String numCc, String electionTitle, String listName, boolean isBlank, boolean isNull) throws RemoteException;

	public void messageFromDep(String depName) throws RemoteException;
}
