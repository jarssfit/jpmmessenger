import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class server extends JFrame {
	
	//defining all variable objects
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	//constructor
	public server() {
		super("JPM Messenger | Server");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150); //sets window size
		setVisible(true);
	}
	
	//server set and running
	public void startRunning() {
		try {
			server = new ServerSocket(8008,100); //(port, max users queue)
			while(true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
					
				} catch(EOFException eofException) {
					showMessage("\n Server ended the connection");
					
				} finally {
					closeCon();
				}
			}
			
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//wait for connection and display info
	private void waitForConnection() throws IOException {
		showMessage("waiting for someone...\n");
		connection = server.accept();
		showMessage("connected to"+connection.getInetAddress().getHostName());
	}
	
	//set up stream
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n stream is setup \n");
	}
	
	//allow to have conversation
	private void whileChatting() throws IOException {
		String message = "You are connected";
		sendMessage(message);
		ableToType(true);
		
		do {
			//conversation code
			try {
				message = (String) input.readObject();
				showMessage("\n"+message);
			} catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n invalid message from user");
			}
		} while(!message.equals("CLIENT - END"));
	}
	
	//closing streams and sockets
	private void closeCon( ) {
		showMessage("closing connections....say GoodBye");
		ableToType(false); 
		
		try {
			output.close();
			input.close();
			connection.close();
			
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	 //sending message method
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER :- "+message); //sends message to other person
			output.flush();
			showMessage("\n SERVER :-"+message); //shows message in my chatbox
		} catch(IOException ioException) {
			chatWindow.append("\n cant send this message");
		} 
	}
	
	//updating chats
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}
		);
	}
	
	//allowing user to type
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(tof);
					}
				}
			);
	}
	
}
