package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import user.User;

public class Client {
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final int portNumber = 8888;
    private String hostName = "localhost";

    /**
     * Sets up a connection with the server.
     */
    public void setupConnection() {
	try {
	    System.out.println(startMessage() + " attempting to start a connection to [" 
	    		+ hostName + "] on port [" + portNumber + "]...");
	    clientSocket = new Socket(hostName, portNumber);
	    setupStreams();
	    System.out.println(startMessage() + " connection accepted by [" + hostName + "]\n");
	} catch (IOException e) {
	    System.out.println(startMessage() + " uh oh something went wrong...");
	    System.out.println(startMessage() + " maybe the server is taking a break");
	}
    }

    /**
     * Sets up the input/output streams for sending and receiving information
     * with the server
     * 
     * @throws IOException
     */
    public void setupStreams() throws IOException {
	output = new ObjectOutputStream(clientSocket.getOutputStream());
	output.flush();
	input = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * To transfer information to the server.
     * 
     * @param message 
     */
    public void transferToServer(User traffic) {
    	if(output != null){
    		try {
    		    System.out.println(startMessage() + " attempting to send user [" + traffic.getName() + "]...");
    		    output.writeObject(traffic);
    		    readConfirmation();
    		    System.out.println(startMessage() + " Transfer was successful!\n");
    		} catch (IOException e) {
    		    System.out.println(startMessage() + " Transfer FAILED!!\n");
    		}	
    	}
    	
    }

    /**
     * Reads and displays the confirmation that the server has correctly
     * received the information sent from the client
     * 
     * @throws IOException
     */
    public void readConfirmation() throws IOException {
	String buf;
	try {
	    buf = (String) input.readObject();
	    if (buf != null) {
		System.out.println("[" + getCurrentTime()
			+ "]<Server> Responded it receieved user [" + buf + "].");
	    }
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Closes the I/O Streams and the connection to the server
     */
    public void closeStuff() {
	if(output != null || input != null || clientSocket != null){
		try {
		    System.out.println(startMessage() + " closing connection...");
		    output.writeObject("END");
		    input.close();
		    output.close();
		    clientSocket.close();
		    System.out.println(startMessage() + " connection successfully closed!\n");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
    }

    /**
     * Gets the current time on the client formated to HH:mm:SS
     * 
     * @return String of the current time of the client.
     */
    public String getCurrentTime() {
    	SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    	Calendar cal = Calendar.getInstance();
    	return df.format(cal.getTime());
    }

    /**
     * Creates a message consisting of [currentTime]<Client> for the client
     * output messages.
     * 
     * @return The opening string for client output messages.
     */
    public String startMessage() {
    	return "[" + getCurrentTime() + "]<Client>";
    }	
}
