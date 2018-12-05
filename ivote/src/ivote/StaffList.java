package ivote;

import java.io.Serializable;

public class StaffList extends List implements Serializable {

	private static final long serialVersionUID = 1L;

	public StaffList(String name, Election election) {
		super(name, election);
	}

	@Override
	public boolean addPerson(Person p) {
		if(p.isStaff()) {
			list.add(p);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isStaffList() {
		return true;
	}
}
