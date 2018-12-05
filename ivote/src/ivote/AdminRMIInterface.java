package ivote;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface AdminRMIInterface extends Remote {
	public boolean createElectionConselhoGeral(LocalDateTime begin, LocalDateTime end, String title, String description) throws RemoteException;
	public boolean createElectionNucleo(LocalDateTime begin, LocalDateTime end, String title, String description, Department department) throws RemoteException;
	public ArrayList<Election> getAllElections() throws RemoteException;
	public Election getElection(String title) throws RemoteException;
	public boolean updateElection(String old_name, Election new_election) throws RemoteException;
	
	public boolean createStudent(String name, String mobile_phone, String address, String num_cc, String password, LocalDate expire_date_cc, Department department) throws RemoteException;
	public boolean createProfessor(String name, String mobile_phone, String address, String num_cc, String password, LocalDate expire_date_cc, Department department) throws RemoteException;
	public boolean createStaff(String name, String mobile_phone, String address, String num_cc, String password, LocalDate expire_date_cc, Department department) throws RemoteException;
	public Person getPerson(String numCc) throws RemoteException;
	
	public boolean createDepartment(String descriptor) throws RemoteException;
    public Department getDepartment(String descriptor) throws RemoteException;  
    public ArrayList<Department> getAllDepartments() throws RemoteException;
    public boolean removeDepartment(String descriptor) throws RemoteException;
    
    public boolean createList(String electionTitle, List list) throws RemoteException;
    public ArrayList<List> getAllLists() throws RemoteException;

    public boolean createVotingTable(VotingTable vt) throws RemoteException;
    
    public boolean removeVotingTable(VotingTable vt) throws RemoteException;

    public void subscribeAdmin(AdminCInterface admin) throws RemoteException;
}