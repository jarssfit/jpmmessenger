import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class client extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	//constructor
	public client(String host) {
		super("JPM Messenger | Client");
		serverIP = host;
		userText  = new JTextField();
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
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}
		
		//connection to server and stream setup
		public void startRunning() {
			try {
				connectToServer();
				setupStreams();
				whileChatting();
				
			} catch(EOFException eofException) {
				showMessage("\n client terminated connection");
			} catch(IOException ioException) {
				ioException.printStackTrace();
			} finally {
				closeCon();
			}
		}
		
		//connect to server
		private void connectToServer() throws IOException {
			showMessage("Trying to connect.....");
			connection = new Socket(InetAddress.getByName(serverIP), 8008);
			showMessage("connected to "+connection.getInetAddress().getHostName());
		}
		
		//set up streams for sending receiving data
		private void setupStreams() throws IOException {
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage("\n connected now \n");
		}
		
		//allow client to have conversations
		private void whileChatting() throws IOException {
			ableToType(true);
			
			do {
				try {
					message = (String) input.readObject();
					showMessage("\n"+message);
				} catch(ClassNotFoundException classNotFoundException) {
					showMessage("Invalid message");
				}
				
			} while(!message.equals("SERVER - END"));
		}

		//closing streams and connections
		private void closeCon() {
			showMessage("closing all connections....");
			ableToType(false);
			
			try {
				output.close();
				input.close();
				connection.close();
			} catch(IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		//sending messages
		private void sendMessage(String message) {
			try {
				output.writeObject("CLIENT :-"+message);
				output.flush();
				showMessage("\n CLIENT:-"+message);
			} catch(IOException ioException) {
				chatWindow.append("\n somethings wrong or invalid message");
			}
		}
		
		//updating chat window (showing messages)
		private void showMessage(final String message) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							chatWindow.append(message);
						}
					}
			);
		}
		
		//user able to type
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
