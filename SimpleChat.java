
public class SimpleChat {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createGUIAndShow();
			}
		});
	}

	public static void createGUIAndShow() {
		new ChatFrame();
	}
}
