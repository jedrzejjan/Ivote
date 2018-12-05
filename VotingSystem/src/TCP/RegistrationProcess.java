package TCP;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ivote.Election;
import ivote.VTableRMIInterface;

public class RegistrationProcess extends Thread{

	private String departmentName;
	private Scanner keyboardMessage  = new Scanner(System.in);
	private VTableRMIInterface tcp;
	private int numberOfTerminals;
	private List<ClientHandler> clientHandlers = new ArrayList<>();
	private String chosenElectionTitle;
	private String numCc;
	
	public RegistrationProcess(String departmentName) throws AccessException, RemoteException, NotBoundException {
		tcp = (VTableRMIInterface) LocateRegistry.getRegistry(1099).lookup("TCPcomm");
		this.departmentName = departmentName;
		this.start();
	}
	
	@Override
	public void run() {

		System.out.println("Welcome in RegistrationTCP console. What would you like to do?");
		while (true) {
			System.out.println("1. Write person numCc");
			System.out.println("2. Send person to voting terminal");
			String choice = keyboardMessage.nextLine();
			
			switch (choice) {
			case "1":
				try {
					checkPersonPrivileges();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}

	private void checkPersonPrivileges() throws RemoteException {
		System.out.println("writePersonName");
		System.out.println("Please write Person numCc:");
		numCc = keyboardMessage.nextLine();
		ArrayList<Election> vTableElections = tcp.getVTableElections(departmentName);
		System.out.println("Please choose election:");
		for (int j = 0; j < vTableElections.size(); j++) {
			System.out.println(j + " - " + vTableElections.get(j).getTitle());
		}
		try {
			int chosenElectionInt = Integer.parseInt(keyboardMessage.nextLine());
			chosenElectionTitle = vTableElections.get(chosenElectionInt).getTitle();
			System.out.println("ELECTION choosen:");
			System.out.println(chosenElectionTitle);
//			check if person can vote
			boolean checkIfCanVote = tcp.userCanVote(numCc, chosenElectionTitle, departmentName);
			if (checkIfCanVote) {
				System.out.println("This Person can vote!!!");
				unlockTerminal();
			}
			else 
				System.out.println("Sorry, you can't vote in this election");
		} catch (Exception e) {
			return;
		}
	}

//	unlock terminal chosen by person in registration table
	private void unlockTerminal() {
		try {
			System.out.println("Which terminal would you like to release?");
			for (int i = 1; i < numberOfTerminals; i++) {
				System.out.println("("+i+")");
			}
			int terminalToRelease = Integer.parseInt(keyboardMessage.nextLine());
			for (ClientHandler clientHandler : clientHandlers) {
				if (clientHandler.getTerminalNumber()==terminalToRelease) {
					clientHandler.setRelease(true);
					clientHandler.setElectionTitle(chosenElectionTitle);
					clientHandler.setNumCc(numCc);
					System.out.println("Releasing terminal number T["+ terminalToRelease+"]");
				}
			}
		} catch (Exception e) {
			System.out.println("Sorry, something went wrong. Repete procedure");
		}
	}

	public int getNumberOfTerminals() {
		return numberOfTerminals;
	}

	public void setNumberOfTerminals(int numberOfTerminals) {
		this.numberOfTerminals = numberOfTerminals;
	}

	public void insert(ClientHandler clientHandler) {
		this.clientHandlers.add(clientHandler);
	}
}
