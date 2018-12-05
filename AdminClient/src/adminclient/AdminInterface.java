package adminclient;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import ivote.*;

public class AdminInterface extends UnicastRemoteObject implements AdminCInterface {
	
	private static final long serialVersionUID = 1L;

	public static enum State {
		MAIN_MENU, ADD_ELECTION_NUCLEO, EXIT, SHOW_ALL_ELECTIONS, ADD_ELECTION_CONSELHO_GERAL, 
		ADD_MEMBER, SHOW_ALL_DEPARTMENTS, ADD_DEPARTMENT, DELETE_DEPARTMENT, UPDATE_ELECTION, 
		ADD_LIST, SHOW_ALL_LISTS, ADD_VOTING_TABLE, REMOVE_VOTING_TABLE, CHECK_ACTIVE_ELECTIONS_STATUS
	};
	
	private AdminRMIInterface remInterface;
	private Scanner sc = new Scanner(System.in);
	private final int timeBetweenRepSvCalls = 5000; //Time between calls to sv calls
	private final int timeBeforeResetingRemInt = 30000; //Time until changing Remote Interface
	State state;
	private ArrayList<Election> activeElections = new ArrayList<Election>();
	private ArrayList<Integer> votesPerElection = new ArrayList<Integer>();
	

	public AdminInterface(AdminRMIInterface remInterface) throws RemoteException {
		super();
		this.remInterface = remInterface;
		state = State.MAIN_MENU;
	}
	
	public void execute() {
		while(true) {
			try {
				switch(state) {
				case MAIN_MENU:
					mainMenu();
					break;
				case ADD_ELECTION_NUCLEO:
					createElectionNucleoMenu();
					state = State.MAIN_MENU;
					break;
				case ADD_ELECTION_CONSELHO_GERAL:
					createElectionConselhoGeralMenu();
					state = State.MAIN_MENU;
					break;
				case SHOW_ALL_ELECTIONS:
					printAllElections();
					state = State.MAIN_MENU;
					break;
				case ADD_MEMBER:
					createMemberMenu();
					state = State.MAIN_MENU;
					break;
				case SHOW_ALL_DEPARTMENTS:
					printAllDepartments();
					state = State.MAIN_MENU;
					break;
				case ADD_DEPARTMENT:
					createDepartmentMenu();
					state = State.MAIN_MENU;
					break;
				case DELETE_DEPARTMENT:
					removeDepartmentMenu();
					state = State.MAIN_MENU;
					break;
				case UPDATE_ELECTION:
					updateElectionMenu();
					state = State.MAIN_MENU;
					break;
				case ADD_LIST:
					createListMenu();
					state = State.MAIN_MENU;
					break;
				case SHOW_ALL_LISTS:
					printAllLists();
					state = State.MAIN_MENU;
					break;
				case ADD_VOTING_TABLE:
					createVotingTableMenu();
					state = State.MAIN_MENU;
					break;
				case REMOVE_VOTING_TABLE:
					removeVotingTableMenu();
					state = State.MAIN_MENU;
					break;
				case CHECK_ACTIVE_ELECTIONS_STATUS:
					printActiveElectionsStatus();
					state = State.MAIN_MENU;
					break;
				case EXIT:
					return;
				}
			}
			catch(RemoteException e) {
				e.printStackTrace();
				state = State.MAIN_MENU;
			}
		}
	}

	private void mainMenu() {
		System.out.println("0-Exit client:");
		System.out.println("1-Create nucleo election:");
		System.out.println("2-Create general board election:");
		System.out.println("3-Update Election:");
		System.out.println("4-List all elections:");
		System.out.println("5-Create a voter:");
		System.out.println("6-List all departments:");
		System.out.println("7-Create a department:");
		System.out.println("8-Remove a department:");
		System.out.println("9-Create a list:");
		System.out.println("10-List all lists:");
		System.out.println("11-Create a voting table:");
		System.out.println("12-Remove voting table");
		System.out.println("13-Check active elections Status");
		switch(getNextInt()) {
		case -1:
			state = State.MAIN_MENU;
			break;
		case 0:
			state = State.EXIT;
			break;
		case 1:
			state = State.ADD_ELECTION_NUCLEO;
			break;
		case 2:
			state = State.ADD_ELECTION_CONSELHO_GERAL;
			break;
		case 3:
			state = State.UPDATE_ELECTION;
			break;
		case 4:
			state = State.SHOW_ALL_ELECTIONS;
			break;
		case 5:
			state = State.ADD_MEMBER;
			break;
		case 6:
			state = State.SHOW_ALL_DEPARTMENTS;
			break;
		case 7:
			state = State.ADD_DEPARTMENT;
			break;
		case 8:
			state = State.DELETE_DEPARTMENT;
			break;
		case 9:
			state = State.ADD_LIST;
			break;
		case 10:
			state = State.SHOW_ALL_LISTS;
			break;
		case 11: 
			state = State.ADD_VOTING_TABLE;
			break;
		case 12: 
			state = State.REMOVE_VOTING_TABLE;
			break;
		case 13:
			state = State.CHECK_ACTIVE_ELECTIONS_STATUS;
			break;
		}
	}
	
	private void createMemberMenu() throws RemoteException {
		System.out.println("Type the kind of user you want to create:");
		System.out.println("Student: 0 | Professor: 1 | Staff: 2");
		int option = getNextInt();
		if(option < 0 || option > 3) {
			System.out.println("That option doesn't exist");
			return;
		}
		System.out.println("Type your name");
		String name = sc.nextLine();
		System.out.println("Type you ID card number");
		String numCC = sc.nextLine();
		System.out.println("Type your phone number");
		String mobilePhone = sc.nextLine();
		System.out.println("Type your address");
		String address = sc.nextLine();
		System.out.println("Type the expiration date of your ID card: <yyyy-mm-dd>");
		LocalDate expireDate;
		try {
			expireDate = LocalDate.parse(sc.nextLine());
		}
		catch(Exception exc) {
			System.out.println("Cant parse date");
			return;
		}
		System.out.println("Type your department initials");
		String dep_str = sc.nextLine();
		Department dep = getDepartmentFromServer(dep_str);
		if(dep == null) {
			System.out.println("That department doesnt exist");
			return;
		}
		System.out.println("Type your password");
		String password = sc.nextLine();
		createPersonInServer(name, mobilePhone, address, numCC, password,
				expireDate, dep, option);
	}
	
	private void createElectionConselhoGeralMenu() throws RemoteException {
		System.out.println("Type the date and time of the begining: <yyyy-mm-ddThh:mm:ss>");
		String dateBeg = sc.nextLine();
		System.out.println("Type the date and time of the end: <yyyy-mm-ddThh:mm:ss>");
		String dateEnd = sc.nextLine();
		LocalDateTime b, e;
		
		try {
			b = LocalDateTime.parse(dateBeg, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			e = LocalDateTime.parse(dateEnd, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		catch(Exception exc) {
			System.out.println("Cant parse dates");
			return;
		}
		System.out.println("Type the election title");
		String titulo = sc.nextLine();
		System.out.println("Type the election description");
		String desc = sc.nextLine();
		createElectionConselhoGeralInServer(b, e, titulo, desc);
	}
	
	private void createElectionNucleoMenu() throws RemoteException {
		System.out.println("Type the date and time of the begining: <yyyy-mm-ddThh:mm:ss>");
		String dateBeg = sc.nextLine();
		System.out.println("Type the date and time of the end: <yyyy-mm-ddThh:mm:ss>");
		String dateEnd = sc.nextLine();
		LocalDateTime b, e;
		
		try {
			b = LocalDateTime.parse(dateBeg, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			e = LocalDateTime.parse(dateEnd, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		catch(Exception exc) {
			System.out.println("Cant parse dates");
			return;
		}
		System.out.println("Type the department initials");
		String dep_str = sc.nextLine();
		Department dep = getDepartmentFromServer(dep_str);
		if(dep == null) {
			System.out.println("That department doesnt exist");
			return;
		}
		System.out.println("Type the election title");
		String titulo = sc.nextLine();
		System.out.println("Type the election description");
		String desc = sc.nextLine();
		createElectionNucleoInServer(b, e, titulo, desc, dep);
	}
	
	private void updateElectionMenu() throws RemoteException {
		System.out.println("Type the title of the election you want to change");
		String title = sc.nextLine();
		Election oldElection = getElectionFromServer(title);
		if(oldElection == null) {
			System.out.println("That election doesnt exist");
			return;
		}
		System.out.println("Do you want to change the title\n" + oldElection.getTitle() + "\n" + "<y/n>");
		String newTitle = oldElection.getTitle();
		if(sc.nextLine().equals("y")) {
			System.out.println("Type the new title");
			newTitle = sc.nextLine();
		}
		System.out.println("Do you want to change the description?\n" + oldElection.getDescription() + "\n" + "<y/n>");
		String newDescription = oldElection.getDescription();
		if(sc.nextLine().equals("y")) {
			System.out.println("Type the new description");
			newDescription = sc.nextLine();
		}
		System.out.println("Do you want to change the beggining date/time?\n" + oldElection.getBegin() + "\n" + "<y/n>");
		LocalDateTime newBegin= oldElection.getBegin();
		if(sc.nextLine().equals("y")) {
			System.out.println("Type the new beggining date/time: <yyyy-mm-ddThh:mm:ss>");
			try {
				newBegin = LocalDateTime.parse(sc.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			}
			catch(Exception exc) {
				System.out.println("Cant parse dates");
				return;
			}
		}
		System.out.println("Do you want to change the end date/time?\n" + oldElection.getEnd() + "\n" + "<y/n>");
		LocalDateTime newEnd= oldElection.getEnd();
		if(sc.nextLine().equals("y")) {
			System.out.println("Type the new end date/time: <yyyy-mm-ddThh:mm:ss>");
			try {
				newEnd = LocalDateTime.parse(sc.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			}
			catch(Exception exc) {
				System.out.println("Cant parse dates");
				return;
			}
		}
		Election newElection;
		if(oldElection.isElectionConselhoGeral()) {
			newElection = new ElectionConselhoGeral(newBegin, newEnd, newTitle, newDescription);
		}
		else {
			newElection = new ElectionNucleo(newBegin, newEnd, newTitle, newDescription, ((ElectionNucleo)oldElection).getDepartment());
		}
		if(!updateElectionInServer(oldElection.getTitle(), newElection)) {
			System.out.println("Cant change election");
		};
		
	}
	
	private void createDepartmentMenu() throws RemoteException {
		System.out.println("Type the new department initials");
		if(createDepartmentInServer(sc.nextLine())) {
			System.out.println("Department created");
		}
		else {
			System.out.println("Wasnt able to create Department");
		}
	}
	
	private void createListMenu() throws RemoteException {
		System.out.println("Type the election title you want to associate the list");
		String electionTitle = sc.nextLine();
		Election e = getElectionFromServer(electionTitle);
		if(e == null) {
			System.out.println("Cant find that election");
			return;
		}
		System.out.println("Type the name of the list");
		String lTitle = sc.nextLine();
		List l = null;
		int listType; //0-Students, 1-Professors, 2-Staff
		if(e.isEleicaoNucleo()) {
			listType = 0;
		}
		else {
			System.out.println("What is the kind of list you want to form?");
			System.out.println("0-Students, 1-Professors, 2-Staff");
			listType = getNextInt();
		}
		if(listType == 0) {
			l = new StudentList(lTitle, e);
		}
		else if(listType == 1) {
			l = new ProfessorList(lTitle, e);
		}
		else if(listType == 2) {
			l = new ProfessorList(lTitle, e);
		}
		else {
			System.out.println("Not an option");
			return;
		}
		System.out.println("How many elements has the list?");
		int nElements = getNextInt();
		if(nElements <= 0) {
			System.out.println("Cant do it");
			return;
		}
		for(int i=0; i<nElements; i++) {
			System.out.println("Write the number of the ID card of the element " + i+1);
			String cc = sc.nextLine();
			Person p = getPersonFromServer(cc);
			if(p == null) {
				System.out.println("That person doesnt exist");
				return;
			}
			if(!l.addPerson(p)) {
				System.out.println("Cant add that person");
				return;
			}
		}
		if(!createListInServer(electionTitle, l)) {
			System.out.println("Cant add list");
		}
		return;
	}
	
	private void createVotingTableMenu() throws RemoteException {
		System.out.println("Type the title of the election");
		String electionTitle = sc.nextLine();
		Election e = getElectionFromServer(electionTitle);
		if(e == null) {
			System.out.println("No election with that title");
			return;
		}
		System.out.println("Type the department initials");
		String department = sc.nextLine();
		Department d = getDepartmentFromServer(department);
		if(d == null) {
			System.out.println("No department with that initials");
			return;
		}
		VotingTable vt = new VotingTable(e, d);
		if(!createVotingTableInServer(vt)) {
			System.out.println("Cant add that table");
		}
	}
	
	private void removeVotingTableMenu() throws RemoteException {
		System.out.println("Type the title of that election");
		String electionTitle = sc.nextLine();
		Election e = getElectionFromServer(electionTitle);
		if(e == null) {
			System.out.println("No election with that title");
			return;
		}
		System.out.println("Digite o departamento");
		String department = sc.nextLine();
		Department d = getDepartmentFromServer(department);
		if(d == null) {
			System.out.println("No department with that initials");
			return;
		}
		VotingTable vt = new VotingTable(e, d);
		if(!removeVotingTableInServer(vt)) {
			System.out.println("Cant remove that table");
		}
	}
	
	private void removeDepartmentMenu() throws RemoteException {
		System.out.println("Type the initials of the department you want to remove");
		if(removeDepartmentInServer(sc.nextLine())) {
			System.out.println("It was successfuly deleted");
		}
		else {
			System.out.println("Can't delete that department");
		}
	}
	
	private void printAllElections() throws RemoteException {
		ArrayList<Election> allElections = remInterface.getAllElections();
		for(Election e : allElections) {
			System.out.println(e+"\n");
		}
	}
	
	private void printAllLists() throws RemoteException {
		ArrayList<List> lists = remInterface.getAllLists();
		for(List l : lists) {
			System.out.println(l);
		}
	}
	
	private void printAllDepartments() throws RemoteException {
		ArrayList<Department> allDepartments = getAllDepartmentsFromServer();
		for(Department d: allDepartments) {
			System.out.println(d);
		}
	}
	
	private boolean createPersonInServer(String name, String mobilePhone, String address, String numCC, String password,
			LocalDate expireDate, Department dep, int option) throws RemoteException {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				if(option == 0)
					return remInterface.createStudent(name, mobilePhone, address, numCC, password,
						expireDate, dep);
				else if(option == 1) {
					return remInterface.createProfessor(name, mobilePhone, address, numCC, password,
							expireDate, dep);
				}
				else if(option == 2){
					return remInterface.createStaff(name, mobilePhone, address, numCC, password,
							expireDate, dep);
				}
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			if(option == 0)
				return remInterface.createStudent(name, mobilePhone, address, numCC, password,
					expireDate, dep);
			else if(option == 1) {
				return remInterface.createProfessor(name, mobilePhone, address, numCC, password,
						expireDate, dep);
			}
			else if(option == 2){
				return remInterface.createStaff(name, mobilePhone, address, numCC, password,
						expireDate, dep);
			}
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
		return false;
	}
	
	Person getPersonFromServer(String numCc) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.getPerson(numCc);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return null;
		}
		try {
			return remInterface.getPerson(numCc);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return null;
		}
	}
	
	private boolean createElectionNucleoInServer(LocalDateTime beg, LocalDateTime end, String titulo, String desc, Department dep) throws RemoteException {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.createElectionNucleo(beg, end, titulo, desc, dep);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.createElectionNucleo(beg, end, titulo, desc, dep);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
	}
	
	private boolean createElectionConselhoGeralInServer(LocalDateTime beg, LocalDateTime end, String titulo, String desc) throws RemoteException {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.createElectionConselhoGeral(beg, end, titulo, desc);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.createElectionConselhoGeral(beg, end, titulo, desc);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
	}
	
	private Election getElectionFromServer(String electionTitle) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.getElection(electionTitle);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return null;
		}
		try {
			return remInterface.getElection(electionTitle);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return null;
		}
	}
	
	private boolean updateElectionInServer(String oldElectionTitle, Election newElection) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.updateElection(oldElectionTitle, newElection);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.updateElection(oldElectionTitle, newElection);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}

	}

	private boolean createVotingTableInServer(VotingTable vt) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.createVotingTable(vt);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.createVotingTable(vt);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
	}
	
	private boolean removeVotingTableInServer(VotingTable vt) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.removeVotingTable(vt);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.removeVotingTable(vt);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
	}
	
	private boolean createDepartmentInServer(String initials) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.createDepartment(initials);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.createDepartment(initials);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
	}
	
	private boolean removeDepartmentInServer(String name) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.removeDepartment(sc.nextLine());
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.removeDepartment(sc.nextLine());
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
	}
	
	private Department getDepartmentFromServer(String descriptor) throws RemoteException {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.getDepartment(descriptor);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return null;
		}
		try {
			return remInterface.getDepartment(descriptor);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return null;
		}
	}
	
	private ArrayList<Department> getAllDepartmentsFromServer() throws RemoteException {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.getAllDepartments();
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return null;
		}
		try {
			return remInterface.getAllDepartments();
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return null;
		}
	}
	
	
	private boolean createListInServer(String electionTitle, List l) {
		int time = 0;
		while(time < timeBeforeResetingRemInt) {
			try {
				return remInterface.createList(electionTitle, l);
			}
			catch(RemoteException e) {
				try {
					Thread.sleep(timeBetweenRepSvCalls);
				} catch (InterruptedException e1) {}
				time += timeBetweenRepSvCalls;
			}
		}
		if(!updateRemInterface()) {
			return false;
		}
		try {
			return remInterface.createList(electionTitle, l);
		} catch (RemoteException e) {
			System.out.println("Can't connect to the sv.");
			return false;
		}
	}
	
	private boolean updateRemInterface() {
		try {
			remInterface = (AdminRMIInterface) Naming.lookup("admin");
		} catch (Exception e) {
			System.out.println("Cant connect to server.");
			return false;
		}
		return true;
	}
	
	
	//Can't read a -1 because -1 is error
	public int getNextInt() {
		try {
			return Integer.parseInt(sc.nextLine());
		}
		catch(Exception e) {
			return -1;
		}
	}
	
	@Override
	public void pushVotingTableState(Department d, boolean isOn) throws RemoteException {
		System.out.print("Voting table "+ d.getName()+ " is ");
		if(isOn) {
			System.out.println("on");
		}
		else {
			System.out.println("off");
		}
	}

	@Override
	public void pushElectionVotes(Election e, int nVotes) throws RemoteException {
		int index = activeElections.indexOf(e);
		if(index == -1) {
			activeElections.add(e);
			//TODO
			//BUG MAKE SET
			votesPerElection.add(1);
		}
		else {
			votesPerElection.set(index, nVotes);
		}
	}
	
	private void printActiveElectionsStatus() {
		for(int i = 0; i<activeElections.size(); i++) {
			System.out.println(activeElections.get(i));
			System.out.println("Number of votes so far: " + votesPerElection.get(i));
		}
	}
}
