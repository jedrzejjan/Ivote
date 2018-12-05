package ivote;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class List implements Serializable {

	private static final long serialVersionUID = 1L;
	protected String name;
	protected Election election;
	protected ArrayList<Person> list = new ArrayList<Person>();
	protected int nVotes = 0;
	
	public List(String name, Election election) {
		this.name = name;
		this.election = election;
	}
	
	public void removePerson(Person p) {
		for(int i=0; i<list.size(); i++) {
			if(p.getNumCC() == list.get(i).getNumCC()) {
				list.remove(i);
				return;
			}
		}
	}
	
	public void addVote() {
		nVotes++;
	}
	
	public int getNVotes() {
		return nVotes;
	}
	
	public String toString() {
		String res = name + "\n";
		for(Person p : list) {
			res += p;
		}
		return res;
	}
	
	public ArrayList<Person> getList() {
		return list;
	}
	
	public Election getElection() {
		return election;
	}
	
	public abstract boolean addPerson(Person p);
	
	public String getName() {
		return name;
	}
	
	public boolean isProfessorList() {
		return false;
	}
	
	public boolean isStaffList() {
		return false;
	}
	
	public boolean isStudentList() {
		return false;
	}
	
	@Override
	public boolean equals(Object l) {
		// self check
	    if (this == l)
	        return true;
	    // null check
	    if (l == null)
	        return false;
	    // type check and cast
	    if (getClass() != l.getClass())
	        return false;
	    List list = (List) l;
		if(list.getName().equals(name) && election.equals(list.getElection())) {
			return true;
		}
		return false;
	}
}
