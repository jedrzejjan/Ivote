package ivote;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Person implements Serializable{
	static final long serialVersionUID = 1L;
	private String name, mobilePhone, address, numCc, password;
	private LocalDate expireDateCc;
	private Department department;
	
	public Person(String name, String mobilePhone, String address, String numCc, String password,
			LocalDate expireDateCc, Department department) {
		this.name = name;
		this.mobilePhone = mobilePhone;
		this.address = address;
		this.numCc = numCc;
		this.password = password;
		this.expireDateCc = expireDateCc;
		this.department = department;
	}
	
	@Override
	public String toString() {
		return name + " (" + numCc + ")"; 
	}
	
	public boolean isProfessor() {
		return false;
	}
	
	public boolean isStudent() {
		return false;
	}
	
	public boolean isStaff() {
		return false;
	}
	
	public String getNumCC() {
		return numCc;
	}

	public String getName() {
		return name;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public String getAddress() {
		return address;
	}

	public String getNumCc() {
		return numCc;
	}

	public LocalDate getExpireDateCc() {
		return expireDateCc;
	}

	public Department getDepartment() {
		return department;
	}
	
	public boolean checkPassword(String password) {
		return password.equals(this.password);
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
	    Person person = (Person) l;
		if(person.getNumCC().equals(numCc)) {
			return true;
		}
		return false;
	}

}
