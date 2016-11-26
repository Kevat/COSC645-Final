import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainScreen {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainScreen window = new MainScreen();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(552, 547);
		shell.setText("Digital Cash Application");
		
		Label lblDigitalCash = new Label(shell, SWT.NONE);
		lblDigitalCash.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblDigitalCash.setBounds(171, 10, 182, 21);
		lblDigitalCash.setText("Digital Cash - COSC 645 Final");
	}
}
