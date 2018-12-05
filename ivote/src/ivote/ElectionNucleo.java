package ivote;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ElectionNucleo extends Election implements Serializable {
    
	private static final long serialVersionUID = 1L;
	protected Department department;

    public ElectionNucleo(LocalDateTime begin, LocalDateTime end, String title, String description, Department department) {
        super(begin, end, title, description);
        this.department = department;
    }
    
    public String toString() {
    	return "Eleição de Nucleo\n" + super.toString();
    }
    
    public boolean isEleicaoNucleo() {
    	return true;
    }
    
    public Department getDepartment() {
    	return department;
    }
}
