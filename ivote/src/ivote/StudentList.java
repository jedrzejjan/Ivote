package ivote;

import java.io.Serializable;

public class StudentList extends List implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public StudentList(String name, Election election) {
		super(name, election);
	}
	
	public boolean addPerson(Person p) {
		if(p.isStudent()) {
			list.add(p);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isStudentList() {
		return true;
	}

}
