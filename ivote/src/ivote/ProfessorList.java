package ivote;

import java.io.Serializable;

public class ProfessorList extends List implements Serializable {

	private static final long serialVersionUID = 1L;

	public ProfessorList(String name, Election election) {
		super(name, election);
	}

	@Override
	public boolean addPerson(Person p) {
		if(p.isProfessor()) {
			list.add(p);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isProfessorList() {
		return true;
	}

}
