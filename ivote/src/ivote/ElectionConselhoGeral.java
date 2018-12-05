package ivote;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ElectionConselhoGeral extends Election implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public ElectionConselhoGeral(LocalDateTime begin, LocalDateTime end, String title, String description) {
		super(begin, end, title, description);
	}

	public boolean isElectionConselhoGeral() {
		return true;
	}
	
	public String toString() {
		return "Eleição para o conselho geral\n" + super.toString();
	}
}
