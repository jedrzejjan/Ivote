package ivote;
import java.io.Serializable;

public class Department implements Serializable{
	
	static final long serialVersionUID = 1L;
	protected String name;

    public Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
    	return name;
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
	    Department dep = (Department) l;
		if(dep.getName().equals(name)) {
			return true;
		}
		return false;
	}
}
