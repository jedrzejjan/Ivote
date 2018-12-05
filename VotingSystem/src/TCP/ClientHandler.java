package TCP;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

import ivote.List;
import ivote.VTableRMIInterface;
 
 
public class ClientHandler extends Thread {
 
    private String departmentName;
    private String electionTitle;
    private Socket clientSocket;
    private VTableRMIInterface tcp;
    private boolean check = false;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private int terminalNumber;
    private String numCc;
    private volatile boolean release;
    private boolean isBlank;
    private boolean isNull;
    private String listName;
    private final int timeBetweenRepSvCalls = 5000; //Time between calls to sv calls
	private final int timeBeforeResetingRemInt = 30000; //Time until changing Remote Interface
 
 
    public ClientHandler (Socket clientSocket, String departmentName, int terminalNumber) throws NotBoundException {
        this.departmentName = departmentName;
        this.terminalNumber = terminalNumber;
        try {
            this.clientSocket = clientSocket;
            tcp = (VTableRMIInterface) LocateRegistry.getRegistry(1099).lookup("TCPcomm");
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
 
    //=============================
    public void run(){
 
        while (true) {
            try{
                System.out.println("wait... "+terminalNumber);
                release = false;
                while (!release) {
                    Thread.sleep(2000);
                }
                System.out.println("not waiting any more "+terminalNumber);
                inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToClient = new PrintWriter (clientSocket.getOutputStream(), true);
 
                proceedVoting(); //this handle voting protocol
                
 
            }catch(IOException e){
                System.out.println("IOE:");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
 
    private void proceedVoting() throws IOException {
 
        outToClient.println("Hello in UC Voting Manager. Press enter to start.");
        inFromClient.mark(1000);
        inFromClient.reset();
        inFromClient.readLine();
        outToClient.println("Who are you?");
        check = logIn(outToClient, inFromClient);
 
        if (check) {
            outToClient.println("Password approved\n\nchoose list");
            ArrayList<List> ballotPaper = null;
            int time = 0;
            while(true) {
    			try {
    				ballotPaper = tcp.getAllListsOfElection(electionTitle);
    	            break;
    			}
    			catch(RemoteException e) {
    				try {
    					Thread.sleep(timeBetweenRepSvCalls);
    				} catch (InterruptedException e1) {}
    				time += timeBetweenRepSvCalls;
    			}
    			if(time > timeBeforeResetingRemInt) {
    				updateRemInterface();
    			}
    		}
            printLists(ballotPaper);
            try {
                int chosenListInt = Integer.parseInt(inFromClient.readLine());
                listName = getListName(ballotPaper, chosenListInt);
            } catch (Exception e) {
                isNull = true;
            }
            time = 0;
            while(true) {
    			try {
    				tcp.vote(departmentName, numCc, electionTitle, listName, isBlank, isNull);
    	            break;
    			}
    			catch(RemoteException e) {
    				try {
    					Thread.sleep(timeBetweenRepSvCalls);
    				} catch (InterruptedException e1) {}
    				time += timeBetweenRepSvCalls;
    			}
    			if(time > timeBeforeResetingRemInt) {
    				updateRemInterface();
    			}
    		}
            outToClient.println("Answer detected.\nThank you for voting Bye!");
 
        }
        else {
            outToClient.println("You are not able to vote, sorry. Please leave the voting terminal");
        }
    }
 
    private void printLists(ArrayList<List> ballotPaper) {
 
        int i;
        for (i = 0; i < ballotPaper.size(); i++) {
            outToClient.println(i+" - "+ballotPaper.get(i).getName());
        }
        outToClient.println(i+" - Blank vote");
    }
   
    private String getListName(ArrayList<List> ballotPaper, int i) {
 
        int next;
        if (ballotPaper.isEmpty()) {
            isBlank = false;
            isNull = true;
            return "";
        }
        for (next = 0; next < ballotPaper.size(); next++) {
            if (next == i) {
                isBlank = false;
                isNull = false;
                return ballotPaper.get(i).getName();
            }
            if (next == ballotPaper.size()) {
                isBlank = true;
                isNull = false;
            }
        }
        return "";
    }
 
    //  3 chance to log in
    //  if the Person is recognised and can vote, then return true, otherwise set check to false.
    //   when voter leave loop without logging in check is set on false
    private boolean logIn(PrintWriter outToClient, BufferedReader inFromClient) throws IOException {
 
        for (int i = 0; i < 3; i++) {
            outToClient.println("If you are user with numCc = "+numCc+"\nwrite your password.\nIf not leave this terminal");
            String password = inFromClient.readLine();
            int time = 0;
            boolean check;
            while(true) {
    			try {
    				check = tcp.authenticateUser(numCc, password);  
    	            break;
    			}
    			catch(RemoteException e) {
    				try {
    					Thread.sleep(timeBetweenRepSvCalls);
    				} catch (InterruptedException e1) {}
    				time += timeBetweenRepSvCalls;
    			}
    			if(time > timeBeforeResetingRemInt) {
    				updateRemInterface();
    			}
    		}
            if(check)
                return true;
        }
        return false;
    }
 
    public int getTerminalNumber() {
        return terminalNumber;
    }
 
    public void setTerminalNumber(int terminalNumber) {
        this.terminalNumber = terminalNumber;
    }
 
    public boolean isRelease() {
        return release;
    }
 
    public void setRelease(boolean release) {
        this.release = release;
    }
 
    public String getElectionTitle() {
        return electionTitle;
    }
 
    public void setElectionTitle(String electionTitle) {
        this.electionTitle = electionTitle;
    }
 
    public String getNumCc() {
        return numCc;
    }
 
    public void setNumCc(String numCc) {
        this.numCc = numCc;
    }
    
    private boolean updateRemInterface() {
		try {
			tcp = (VTableRMIInterface) Naming.lookup("TCPcomm");
		} catch (Exception e) {
			System.out.println("Cant connect to server.");
			return false;
		}
		return true;
	}
}