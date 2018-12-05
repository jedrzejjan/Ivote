import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import ivote.*;

public class MainServer extends UnicastRemoteObject implements AdminRMIInterface, VTableRMIInterface, WebRMIInterface {

    private static final long serialVersionUID = 1L;
    private Database db;
    private static boolean isPrimary;
    private static Registry r = null;
    private static ArrayList<AdminCInterface> adminClients = new ArrayList<AdminCInterface>();
    private static LocalDateTime timeBecomeMain = null;
    private static ArrayList<Department> activeDepartments = new ArrayList<>();
    private static ArrayList<LocalDateTime> votingTablePing = new ArrayList<>();
    
    protected MainServer() throws RemoteException {
    	super();
    	db = new Database();
    }

    public static void main(String args[]) throws RemoteException {
    	//fillDepartments();
        MainServer adminInter = new MainServer();
        System.out.println("binding registry");
    	r = LocateRegistry.getRegistry(1099);
        try {
        	r.bind("admin", (AdminRMIInterface) adminInter);
        	r.bind("TCPcomm", (VTableRMIInterface) adminInter);
        	r.bind("WebComm", (WebRMIInterface) adminInter);
        	isPrimary = true;
        	timeBecomeMain = LocalDateTime.now();
        	System.out.println("Main server ready......");
        }
        catch(AlreadyBoundException abe) {
        	isPrimary = false;
        	System.out.println("Backup server ready......");
        }
        
        
        new Thread() {
            public void run() {
                try {
                    while (true) {
                    	//System.out.println(adminInter.getAdminClients().size());
                        Thread.sleep(5000);
                        LocalDateTime now = LocalDateTime.now();
                        for (int i = 0; i < activeDepartments.size(); i++) {
                            int secAllowedToPass = 10;
                            if (votingTablePing.get(i).isBefore(now.minusSeconds(secAllowedToPass))) {
                            	for(AdminCInterface c : adminClients) {
                            		try {
                            		c.pushVotingTableState(activeDepartments.get(i), false);
                            		activeDepartments.remove(i);
                            		i--;
                            		}
                            		catch(RemoteException exp) {
                            			adminClients.remove(c);
                            		}
                            	}
                            }
                        }
                    }
                }
                catch (Exception e) {
                	e.printStackTrace();
                }
            }
        }.start();
        
        FailoverMec f = new FailoverMec(adminInter);
        f.start();
    }
    
    public boolean createElectionNucleo(LocalDateTime begin, LocalDateTime end, String title, String description, Department department) {
        ElectionNucleo el = new ElectionNucleo(begin, end, title, description, department);
        return db.createElection(el);
    }
    
    public boolean createElectionConselhoGeral(LocalDateTime begin, LocalDateTime end, String title, String description) throws RemoteException {
    	ElectionConselhoGeral el = new ElectionConselhoGeral(begin, end, title, description);
        return db.createElection(el);
    }
    	
    public boolean createStudent(String name, String mobile_phone, String address, String num_cc, String password, LocalDate expire_date_cc, Department department) {
		Student s = new Student(name, mobile_phone, address, num_cc, password, expire_date_cc, department);
		return db.createPerson(s);
    }
    
    public boolean createStaff(String name, String mobile_phone, String address, String num_cc, String password, LocalDate expire_date_cc, Department department) {
		Student s = new Student(name, mobile_phone, address, num_cc, password, expire_date_cc, department);
		return db.createPerson(s);
    }
    
    public boolean createProfessor(String name, String mobile_phone, String address, String num_cc, String password, LocalDate expire_date_cc, Department department) {
		Professor s = new Professor(name, mobile_phone, address, num_cc, password, expire_date_cc, department);
		return db.createPerson(s);
    }
    
    public Person getPerson(String numCc) throws RemoteException {
    	return db.getPerson(numCc);
    }
    
    public boolean createDepartment(String descriptor) throws RemoteException {
    	Department d = new Department(descriptor);
        return db.createDepartment(d);
    }
    
    public boolean removeDepartment(String descriptor) throws RemoteException {
    	return db.removeDepartment(descriptor);
    }

	public Department getDepartment(String descriptor) {
		return db.getDepartment(descriptor);
	}	
	
	public ArrayList<Election> getAllElections() throws RemoteException{
		return db.getAllElections();
	}
	
	
	public ArrayList<Department> getAllDepartments() throws RemoteException {
		return db.getAllDepartments();
	}
    
    public Election getElection(String title) throws RemoteException {
    	return db.getElection(title);
    }
    
    public boolean updateElection(String oldName, Election newElection) throws RemoteException {
    	Election e = db.getElection(oldName);
    	if(e.getBegin().isAfter(LocalDateTime.now())) {
    		return false;
    	}
    	return db.updateElection(oldName, newElection);
    }
    
    public boolean createList(String electionTitle, List list) throws RemoteException {
    	return db.createList(list);
    }
    
    public ArrayList<List> getAllLists() throws RemoteException {
		return db.getAllLists();
	}
    
    public ArrayList<List> getAllListsOfElection(String electionTitle) throws RemoteException {
    	ArrayList<List> all = getAllLists();
    	ArrayList<List> res = new ArrayList<List>();
    	for(List l : all) {
    		if(l.getElection().getTitle().equals(electionTitle)) {
    			res.add(l);
    		}
    	}
    	return res;
    }
    
    public boolean createVotingTable(VotingTable vt) throws RemoteException {
    	return db.createVotingTable(vt);
    }
    
    @Override
	public boolean removeVotingTable(VotingTable vt) throws RemoteException {
		return db.removeVotingTable(vt);
	}
    
    public ArrayList<Election> getVTableElections(String depName) throws RemoteException {
    	ArrayList<VotingTable> votingtables = db.getAllVotingTables();
    	ArrayList<Election> res = new ArrayList<Election>();
    	for(VotingTable vt : votingtables) {
    		if(vt.getDepartment().getName().equals(depName) && isActiveElection(vt.getElection())) {
    			res.add(vt.getElection());
    		}
    	}
    	return res;
    }
    
    public boolean userCanVote(String numCc, String electionTitle, String locDepartmentName) throws RemoteException {
    	Election e = db.getElection(electionTitle);
    	Person p = db.getPerson(numCc);
    	if(e == null || p == null) {
    		return false;
    	}
    	if(!isActiveElection(e)) {
    		return false;
    	}
    	if(db.getVotingTable(electionTitle, locDepartmentName) == null) {
    		return false;
    	}
    	if(!personCanVote(p, e)) {
    		return false;
    	}
    	return true;
    }
    
    public boolean authenticateUser(String numCc, String password) throws RemoteException {
    	Person p = db.getPerson(numCc);
    	if(p.checkPassword(password)) {
    		return true;
    	}
    	return false;
    }
    
    public Person authenticateAndGetUser(String numCc, String password) throws RemoteException {
    	System.out.println("Tentativa de autentica�ao: " + numCc + " " + password);
    	Person p = db.getPerson(numCc);
    	if(p.checkPassword(password)) {
    		return p;
    	}
    	return null;
    }
    
    private boolean personCanVote(Person p, List l) {
    	if(!personCanVote(p, l.getElection())) {
    		return false;
    	}
    	if(l.isProfessorList() && p.isProfessor()) {
    		return true;
    	}
    	if(l.isStaffList() && p.isStaff()) {
    		return true;
    	}
    	if(l.isStudentList() && p.isStudent()) {
    		return true;
    	}
    	return false;
    }
    
    private boolean personCanVote(Person p, Election e) {
    	if(e.isElectionConselhoGeral()) {
    		return true;
    	}
    	else if(e.isEleicaoNucleo() && p.isStudent()) {
    		return true;
    	}
    	return false;
    }
    
    private boolean isActiveElection(Election e) {
    	if(e.getBegin().isBefore(LocalDateTime.now()) && e.getEnd().isAfter(LocalDateTime.now())) {
    		return true;
    	}
    	return false;
    }
    
    public boolean vote(String departmentName, String numCc, String electionTitle, String listName, boolean isBlank, boolean isNull) throws RemoteException {
    	Person p = db.getPerson(numCc);
    	if(p == null) {
    		return false;
    	}
    	Election e = db.getElection(electionTitle);
    	if(e == null) {
    		return false;
    	}
    	if(!personCanVote(p, e)) {
    		return false;
    	}
    	Department d = db.getDepartment(departmentName);
    	if(d == null) {
    		return false;
    	}
    	if(db.getVote(numCc, electionTitle) != null) {
    		return false;
    	}
    	Vote v = new Vote(d, e, p);
    	//NEEDS TRANSACTION BUT NO TIME. MAYBE LATER?
    	if(isNull) {
    		if(!db.createVote(v)) {
    			return false;
    		}
    		e.addNullVote();
    		pushElectionToAdmins(e);
    		return updateElection(e.getTitle(), e);
    	}
    	if(isBlank) {
    		if(!db.createVote(v)) {
    			return false;
    		}
    		e.addBlankVote();
    		pushElectionToAdmins(e);
    		return updateElection(e.getTitle(), e);
    	}

    	List l = db.getList(listName, electionTitle);
    	if(l == null) {
    		return false;
    	}
    	if(!personCanVote(p, l)) {
    		return false;
    	}
    	if(!db.createVote(v)) {
			return false;
		}
    	l.addVote();
    	pushElectionToAdmins(e);
    	return db.updateList(electionTitle, listName, l);
    }
    
    public boolean getIsPrimary() {
    	return isPrimary;
    }
    
    public void setAsPrimary() {
    	isPrimary = true;
    	timeBecomeMain = LocalDateTime.now();
    	try {
			r.rebind("admin", (AdminRMIInterface) this);
			r.rebind("TCPcomm", (VTableRMIInterface) this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
    
    public void setAsSecondary() {
    	isPrimary = false;
    }
    
    public LocalDateTime getTimeBecomeMain() {
    	return timeBecomeMain;
    }
    
    public void pushElectionToAdmins(Election e) {
    	int nVotes = db.getNElectionVotes(e.getTitle());
    	for(AdminCInterface c : adminClients) {
    		try {
    			c.pushElectionVotes(e, nVotes);
    		}
    		catch(RemoteException exp) {
    			adminClients.remove(c);
    		}
    	}
    }

    public void subscribeAdmin(AdminCInterface admin) throws RemoteException {
    	System.out.println("Subscribing admin...");
    	if(!adminClients.contains(admin)) {
    		adminClients.add(admin);
    	}
    }
    
    @Override
	synchronized public void messageFromDep(String depName) throws RemoteException {
		LocalDateTime now = LocalDateTime.now();
		Department department = db.getDepartment(depName);
		int index = activeDepartments.indexOf(department);
		if (index == -1) {
			activeDepartments.add(department);
			votingTablePing.add(now);
			return;
		}
		else {
			votingTablePing.set(index, now);
		}
		System.out.println(now + "siemano dostałem sygnał od: "+ depName);
		
	}

	@Override
	public boolean authenticateVotingTable(String depName) throws RemoteException {
		ArrayList<VotingTable> vts = db.getAllVotingTables();
		for(VotingTable vt : vts) {
			if(vt.getDepartment().getName().equals(depName)) {
				for(AdminCInterface c : adminClients) {
					try {
						c.pushVotingTableState(vt.getDepartment(), true);
					}
					catch(RemoteException exc) {
						adminClients.remove(c);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<AdminCInterface> getAdminClients() {
		return adminClients;
	}
}
