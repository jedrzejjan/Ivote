import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ivote.*;

public class Database {
	private final static String electionsPath = "./database/elections/";
    private final static String departmentsPath = "./database/departments/";
    private final static String peoplePath = "./database/people/";
    private final static String listsPath = "./database/lists/";
    private final static String votingtablesPath = "./database/votingtables/";
    private final static String votesPath ="./database/votes/";
    
    static final Object lock = new Object();
    public Database() {}
    
    public boolean createElection(Election e) {
    	synchronized(lock) {
	    	if(getElection(e.getTitle()) != null) {
	    		return false;
	    	}
	    	try {
		        FileOutputStream f = new FileOutputStream(new File(electionsPath + e.getTitle()));
		        ObjectOutputStream o = new ObjectOutputStream(f);
		        o.writeObject(e);
		        o.close();
		        f.close();
		    }
		    catch(Exception exc) {
		        System.out.println("Exception creating Election");
		        return false;
		    }
			return true;
    	}
    }
    
    public Election getElection(String title) {
    	synchronized(lock) {
	    	File dir = new File(electionsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Election e = (Election) readObjectFromFile(child);
				if(e.getTitle().equals(title)){
					return e;
				}
			}
			return null;
    	}
    }
    
    public ArrayList<Election> getAllElections() {
    	synchronized(lock) {
	    	ArrayList<Election> res = new ArrayList<Election>();
	    	File dir = new File(electionsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Election e = (Election) readObjectFromFile(child);
				res.add(e);
			}
			return res;
    	}
    }
    
    public boolean updateElection(String titulo, Election newElection) {
    	synchronized(lock) {
	    	if(!newElection.getTitle().equals(titulo) && getElection(newElection.getTitle()) != null) {
	    		return false;
	    	}
		    File dir = new File(electionsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Election e = (Election)readObjectFromFile(child);
				if(e.getTitle().equals(titulo)) {
					if(!child.delete()) {
						return false;
					}
					try {
						FileOutputStream f = new FileOutputStream(new File(electionsPath + newElection.getTitle()));
			            ObjectOutputStream o = new ObjectOutputStream(f);
			            o.writeObject(newElection);
			            o.close();
			            f.close();
		
					}
					catch(Exception exc) {
						System.out.println("Exception writing Election to file");
						return false;
					}
				}
				return true;
		
			}
			return false;
    	}
    }
    
    public boolean createPerson(Person p) {
    	synchronized(lock) {
	    	if(getPerson(p.getNumCc()) != null) {
	    		return false;
	    	}
	    	try {
		        FileOutputStream f = new FileOutputStream(new File(peoplePath + p.getNumCc()));
		        ObjectOutputStream o = new ObjectOutputStream(f);
		        o.writeObject(p);
		        o.close();
		        f.close();
		    }
		    catch(Exception exc) {
		        System.out.println("Exception creating Student");
		        return false;
		    }
			return true;
    	}
    }
    
    public Person getPerson(String numCc) {
    	synchronized(lock) {
	    	File dir = new File(peoplePath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Person p = (Person) readObjectFromFile(child);
				if(p.getNumCc().equals(numCc)){
					return p;
				}
			}
			return null;
    	}
    }
    
    public boolean createDepartment(Department d) {
    	synchronized(lock) {
	    	if(getDepartment(d.getName()) != null) {
	    		return false;
	    	}
	    	try {
		        FileOutputStream f = new FileOutputStream(new File(departmentsPath + d.getName()));
		        ObjectOutputStream o = new ObjectOutputStream(f);
		        o.writeObject(d);
		        o.close();
		        f.close();
		    }
		    catch(Exception exc) {
		        System.out.println("Exception creating Department");
		        return false;
		    }
			return true;
    	}
    }
    
    public boolean removeDepartment(String descriptor) {
    	synchronized(lock) {
	    	File dir = new File(departmentsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Department d = (Department) readObjectFromFile(child);
				if(d.getName().equals(descriptor)) {
					return child.delete();
				}
			}
			
			return false;
    	}
    }
    
    public Department getDepartment(String descriptor) {
    	synchronized(lock) {
    		File dir = new File(departmentsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Department d = (Department) readObjectFromFile(child);
				if(d.getName().equals(descriptor)){
					return d;
				}
			}
			return null;
    	}
    }
    
    public ArrayList<Department> getAllDepartments() {
    	synchronized(lock) {
    		ArrayList<Department> res = new ArrayList<Department>();
	    	
	    	File dir = new File(departmentsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Department d = (Department) readObjectFromFile(child);
				res.add(d);
			}
			return res;
    	}
    }
    
    public boolean createList(List list) {
    	synchronized(lock) {
	    	if(getElection(list.getElection().getTitle()) == null) {
	    		System.out.println("Impossivel criar lista devido a eleiçao não existir");
	    		return false;
	    	}
	    	
	    	if(getList(list.getName(), list.getElection().getTitle()) != null) {
	    		System.out.println("Impossivel criar lista devido a lista já existir");
	    		return false;
	    	}
	    	try {
		        FileOutputStream f = new FileOutputStream(new File(listsPath + list.getElection().getTitle() + "_" + list.getName()));
		        ObjectOutputStream o = new ObjectOutputStream(f);
		        o.writeObject(list);
		        o.close();
		        f.close();
		    }
		    catch(Exception exc) {
		        exc.printStackTrace();
		        return false;
		    }
			return true;
    	}
    }
    
    public List getList(String name, String electionName) {
    	synchronized(lock) {
	    	File dir = new File(listsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				List l = (List) readObjectFromFile(child);
				if(l.getName().equals(name) && electionName.equals(l.getElection().getTitle())){
					return l;
				}
			}
			return null;
    	}
    }
    
    public boolean updateList(String electionTitle, String listName, List newList) {
    	synchronized(lock) {
	    	File dir = new File(listsPath);
			File[] directoryListing = dir.listFiles();
			for(File child : directoryListing) {
				List l = (List)readObjectFromFile(child);
				if(l.getElection().getTitle().equals(electionTitle)
						&& l.getName().equals(listName)) {
					if(!child.delete()) {
						return false;
					}
					try {
						FileOutputStream f = new FileOutputStream(new File(listsPath + newList.getElection().getTitle() + "_" + newList.getName()));
			            ObjectOutputStream o = new ObjectOutputStream(f);
			            o.writeObject(newList);
			            o.close();
			            f.close();
		
					}
					catch(Exception exc) {
						System.out.println("Exception writing Election to file");
						return false;
					}
				}
				return true;
			}
			return false;
    	}
    }
    
    public boolean createVotingTable(VotingTable vt) {
    	synchronized(lock) {
	    	if(getDepartment(vt.getDepartment().getName()) == null) {
	    		System.out.println("Impossivel criar mesa pois departamento não existe.");
	    		return false;
	    	}
	    	if(getElection(vt.getElection().getTitle()) == null) {
	    		System.out.println("Impossivel criar mesa pois eleiçao não existe.");
	    		return false;
	    	}
	    	if(getVotingTable(vt.getElection().getTitle(), vt.getDepartment().getName()) != null) {
	    		System.out.println("Impossivel criar messa devido a mesa já existir");
	    		return false;
	    	}
	    	try {
		        FileOutputStream f = new FileOutputStream(new File(votingtablesPath + vt.getElection().getTitle()+ "_" + vt.getDepartment().getName()));
		        ObjectOutputStream o = new ObjectOutputStream(f);
		        o.writeObject(vt);
		        o.close();
		        f.close();
		    }
		    catch(Exception exc) {
		        exc.printStackTrace();
		        return false;
		    }
			return true;
    	}
    }
    
    public VotingTable getVotingTable(String electionTitle, String departmentName) {
    	synchronized(lock) {
	    	File dir = new File(votingtablesPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				VotingTable vt2 = (VotingTable) readObjectFromFile(child);
				if(electionTitle.equals(vt2.getElection().getTitle()) && departmentName.equals(vt2.getDepartment().getName())){
					return vt2;
				}
			}
			return null;
    	}
    }
    
    public ArrayList<VotingTable> getAllVotingTables() {
    	synchronized(lock) {
    		ArrayList<VotingTable> res = new ArrayList<VotingTable>();
	    	File dir = new File(votingtablesPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				VotingTable vl = (VotingTable) readObjectFromFile(child);
				res.add(vl);
			}
			return res;
    	}
    }
    
    public boolean removeVotingTable(VotingTable vt) {
    	synchronized(lock) {
	    	File dir = new File(votingtablesPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				VotingTable aux = (VotingTable) readObjectFromFile(child);
				if(vt.equals(aux)) {
					return child.delete();
				}
			}
			return false;
    	}
    }
    
    public ArrayList<List> getAllLists() {
    	synchronized(lock) {
	    	ArrayList<List> res = new ArrayList<List>();
	    	File dir = new File(listsPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				List l = (List) readObjectFromFile(child);
				res.add(l);
			}
			return res;
    	}
    }
    
    public boolean createVote(Vote v) {
    	synchronized(lock) {
	    	if(getDepartment(v.getDepartment().getName()) == null) {
	    		System.out.println("Impossible to create vote because department doesnt exist");
	    		return false;
	    	}
	    	if(getElection(v.getElection().getTitle()) == null) {
	    		System.out.println("Impossible to create vote because election doesnt exist.");
	    		return false;
	    	}
	    	if(getPerson(v.getPerson().getNumCc()) == null) {
	    		System.out.println("Impossible to cretae vote because person doesnt exist.");
	    		return false;
	    	}
	    	if(getVote(v.getPerson().getNumCC(), v.getElection().getTitle()) != null) {
	    		return false;
	    	}
	    	try {
		        FileOutputStream f = new FileOutputStream(new File(votesPath + v.getPerson().getNumCc() + "_" + v.getElection().getTitle()));
		        ObjectOutputStream o = new ObjectOutputStream(f);
		        o.writeObject(v);
		        o.close();
		        f.close();
		    }
		    catch(Exception exc) {
		        exc.printStackTrace();
		        return false;
		    }
			return true;
    	}
    }
    
    public Vote getVote(String numCc, String electionTitle) {
    	synchronized(lock) {
	    	File dir = new File(votesPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Vote v= (Vote) readObjectFromFile(child);
				if(v.getPerson().getNumCc().equals(numCc) && v.getElection().getTitle().equals(electionTitle)) {
					return v;
				}
			}
			return null;
    	}
    }
    
    private static Object readObjectFromFile(File f) {	
    	try {
			FileInputStream fs = new FileInputStream(f);
			ObjectInputStream objectIn = new ObjectInputStream(fs);
			Object obj = objectIn.readObject();
			objectIn.close();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getNElectionVotes(String electionTitle) {
		synchronized(lock) {
			int count = 0;
	    	File dir = new File(votesPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				Vote v= (Vote) readObjectFromFile(child);
				if(v.getElection().getTitle().equals(electionTitle)) {
					count++;
				}
			}
			return count;
    	}
	}
}
