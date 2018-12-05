package ivote;

import java.io.Serializable;

public class VotingTable implements Serializable {

	private static final long serialVersionUID = 1L;
	private Department department;
	private Election election;
	
	public VotingTable(Election e, Department dep) {
		this.department = dep;
		this.election = e;
	}
	
	public Department getDepartment() {
		return department;
	}
	
	public Election getElection() {
		return election;
	}
	
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
	    VotingTable vt = (VotingTable) l;
		if(vt.getElection().getTitle().equals(election.getTitle()) && department.getName().equals(vt.getDepartment().getName())) {
			return true;
		}
		return false;
	}
}
