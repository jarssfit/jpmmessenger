package jpmmessenger;

import javax.swing.JFrame;

public class clientTest {
	public static void main(String args[]) {
		client rishan = new client("127.0.0.1");
		rishan.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rishan.startRunning();
	}
}
