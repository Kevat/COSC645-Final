import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
		
		Button btnAliceSendsMos = new Button(shell, SWT.NONE);
		btnAliceSendsMos.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Generate 100 MOs, store into a local variable and show on UI
				GenerateMOs.Generate();
			}
		});
		btnAliceSendsMos.setBounds(38, 72, 203, 34);
		btnAliceSendsMos.setText("Alice Sends MOs to Bank");
		
		Button btnBankSignsMo = new Button(shell, SWT.NONE);
		btnBankSignsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Should use Alice's Ls and Rs for 99 MOs and validate them
				//Should sign the final MO
				//Show validated MOs and the signed MO on UI
				SignMO.Sign();
			}
		});
		btnBankSignsMo.setBounds(38, 112, 203, 34);
		btnBankSignsMo.setText("Bank Signs MO and returns to Alice");
		
		Button btnAliceSendsMo = new Button(shell, SWT.NONE);
		btnAliceSendsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Should ask Bob for bit vector
				//Send the corresponding Ls and Rs along with MO
				//Show what Bob receives on UI
				SendMO.Send();
			}
		});
		btnAliceSendsMo.setBounds(38, 152, 203, 34);
		btnAliceSendsMo.setText("Alice Sends MO to Bob");
		
		Button btnBobSendsMo = new Button(shell, SWT.NONE);
		btnBobSendsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Send MO received from Alice to Bank
				//Decrypt MO
				//Validate that the same MO was not used before
				//Validate that the same serial number was not used before
				//If same serial number was used before, try to find Alice using Ls and Rs
				//Show output on UI
				SubmitMO.Submit();
			}
		});
		btnBobSendsMo.setBounds(38, 192, 203, 34);
		btnBobSendsMo.setText("Bob Sends MO to Bank");
		
	}
}
