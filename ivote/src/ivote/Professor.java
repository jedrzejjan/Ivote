package ivote;

import java.io.Serializable;
import java.time.LocalDate;

public class Professor extends Person implements Serializable {

	private static final long serialVersionUID = 1L;

	public Professor(String name, String mobile_phone, String address, String num_cc, String password,
			LocalDate expire_date_cc, Department department) {
		
		super(name, mobile_phone, address, num_cc, password, expire_date_cc, department);
	}

	public boolean isProfessor() {
		return true;
	}
}
