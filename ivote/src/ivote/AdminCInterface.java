package ivote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminCInterface extends Remote {
	
	public static final String NAME = "AdminCInterfaceName";
	
	void pushVotingTableState(Department vt, boolean isOn) throws RemoteException;
	void pushElectionVotes(Election e, int nVotes) throws RemoteException;
}
