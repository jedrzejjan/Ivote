package ivote;

import java.io.Serializable;

public class Vote implements Serializable{

	private static final long serialVersionUID = 1L;
	private Election election;
	private Department department; //Department where it was made
	private Person person;
	
	public Vote(Department dep, Election e, Person p) {
		department = dep;
		election = e;
		person = p;
	}

	public Election getElection() {
		return election;
	}

	public Department getDepartment() {
		return department;
	}

	public Person getPerson() {
		return person;
	}
	
}
