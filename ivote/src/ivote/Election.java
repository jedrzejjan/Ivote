package ivote;
import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Election implements Serializable{

	private static final long serialVersionUID = 1L;
	protected LocalDateTime begin, end;
    protected String title, description;
    private int nBlankVotes = 0;
    private int nNullVotes = 0;

    public Election(LocalDateTime begin, LocalDateTime end, String title, String description) {
        this.begin = begin;
        this.end = end;
        this.title = title;
        this.description = description;
    }
    
    public int getNBlankVotes() {
    	return nBlankVotes;
    }
    
    public int getNNullVotes() {
    	return nNullVotes;
    }
    
    public void addBlankVote() {
    	nBlankVotes++;
    }
    
    public void addNullVote() {
    	nNullVotes++;
    }

    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
    	return description;
    }

	public LocalDateTime getEnd() {
		return end;
	}

	public LocalDateTime getBegin() {
    	return begin;
    }
    
    public boolean isEleicaoNucleo() {
    	return false;
    }
    
    public boolean isElectionConselhoGeral() {
    	return false;
    }
    
    public String toString() {
    	return begin.toString() + " - " + end.toString() + "\n" + title + "\n" + description;
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
	    Election election = (Election) l;
		if(election.getTitle().equals(title)) {
			return true;
		}
		return false;
	}

	public void removeNullVote() {
		nNullVotes--;
		
	}
}
