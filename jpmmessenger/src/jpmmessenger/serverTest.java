package jpmmessenger;

import javax.swing.JFrame; 

public class serverTest {
	public static void main(String args[]) {
		/*server suraj = new server();
		suraj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		suraj.startRunning();*/
		Thread suraj = new Thread(new server());
		suraj.start();
	}
}
