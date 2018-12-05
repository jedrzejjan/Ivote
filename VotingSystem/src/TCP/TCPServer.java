package TCP;
 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Scanner;

import ivote.Department;
import ivote.VTableRMIInterface;
 
public class TCPServer {
 
    private int terminalNumber = 1;
    private static int sleepTime = 2000;
    private final static int timeBetweenRepSvCalls = 5000; //Time between calls to sv calls
	private final static int timeBeforeResetingRemInt = 30000; //Time until changing Remote Interface
	private static VTableRMIInterface tcp;
	private static String departmentName;
	private static Scanner keyboardScanner;
	private static ServerSocket listenSocket;
	
    public static void main(String[] args) throws NotBoundException, AccessException, RemoteException {
        tcp = (VTableRMIInterface) LocateRegistry.getRegistry(1099).lookup("TCPcomm");
        keyboardScanner = new Scanner(System.in);
       
        int terminalNumber = 1;
       
        try{
           
           
            System.out.println("Choose department from list: ");
            
            ArrayList<Department> depList = null;
            int chosenListInt;
            int time = 0;
    		while(true) {
    			try {
    				depList = tcp.getAllDepartments();
    	            printDepartments(depList);
    	            chosenListInt = Integer.parseInt(keyboardScanner.nextLine());
    	            departmentName = getDepName(depList, chosenListInt);
    	            if(!tcp.authenticateVotingTable(departmentName)) {
    	            	System.out.println("There's no voting table in that department");
    	            	return;
    	            }
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
 
            new Thread() {
                public void run() {
                    try {
                        while (true) {
                            Thread.sleep(sleepTime);
                            int time = 0;
                            while(true) {
                    			try {
                    				tcp.messageFromDep(departmentName);
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
                        }
                    }catch (Exception e) {
                    	e.printStackTrace();
                    }
                }
            }.start();
 
            RegistrationProcess rp = new RegistrationProcess(departmentName);
 
            System.out.println("Choose department from list: " + departmentName);
           
            int serverPort = 12345;
            System.out.println("A Escuta no Porto 12345");
            listenSocket = new ServerSocket(serverPort);
 
            while (true) {
 
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
 
                ClientHandler ch = new ClientHandler(clientSocket, departmentName, terminalNumber);
                terminalNumber++;
                rp.setNumberOfTerminals(terminalNumber);
                rp.insert(ch);
            }            
 
        }catch(IOException e)
        {System.out.println("Listen:" + e.getMessage());}
 
    }
 
    private static void printDepartments(ArrayList<Department> depList) {
 
        for (int i = 0; i < depList.size(); i++) {
            System.out.println(i+" - "+depList.get(i).getName());
        }
       
    }
   
    private static String getDepName(ArrayList<Department> depList, int i) {
 
        int next;
        for (next = 0; next < depList.size(); next++) {
            if (next == i) {
                return depList.get(i).getName();
            }
        }
        return "";
    }
 
    public int getTerminalNumber() {
        return terminalNumber;
    }
    
    private static boolean updateRemInterface() {
		try {
			tcp = (VTableRMIInterface) Naming.lookup("TCPcomm");
		} catch (Exception e) {
			System.out.println("Cant connect to server.");
			return false;
		}
		return true;
	}
 
}