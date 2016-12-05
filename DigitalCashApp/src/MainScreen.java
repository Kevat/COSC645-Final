import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.math.BigInteger;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;

public class MainScreen {
	private DataBindingContext m_bindingContext;
	private Bank m_bank = new Bank();

	//Variables to show on the screen
	//Used for generating MOs
	public ArrayList<MoneyOrder> GeneratedMOs = new ArrayList<MoneyOrder>();
	public ArrayList<RSABlindingParameters> BlindingFactors = new ArrayList<RSABlindingParameters>();
	public ArrayList<byte []> BlindedMOs = new ArrayList<byte []>();  
	public ArrayList<MoneyOrder> UsedMOs = new ArrayList<MoneyOrder>();
	public ArrayList<BigInteger> Identity_L_List = new ArrayList<BigInteger>();
	//Stores the index of the MO which was signed
	Integer m_signedMOIndex;
	
	//Used for bank signing MOs
	private byte [] m_unsignedMO = null;
	private byte [] m_signedBlindMO = null;
	private byte [] m_signedMO = null;
	RSABlindingParameters m_blindParams = null;
	
	private MoneyOrder m_moneyOrder = null;
	//Used to send MO to Bob
	public String BitVector;
	
	//Used to Submit MO
	public boolean IsMOValid;
	public String WhoCheated;
	public int Bob_Bank_Balance = 500;
	
	protected Shell shell;
	private Text Alice_Identity;
	private Text Alice_Balance;

	Label Alice_MO_Number;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(DisplayRealm.getRealm(display), new Runnable() {
			public void run() {
				try {
					MainScreen window = new MainScreen();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
/*				MoneyOrder newMO = UserAlice.GenerateTextMO();
			
				//Get blinding factor
				RSABlindingParameters blindingParams = CommonFunctions.blindFactor(m_bank.getPublic());
							
				//Blind the MO
				byte[] unsignedBlindedMO = CommonFunctions.blindMessage(newMO.getData(), blindingParams, m_bank.getPublic());
				byte[] signedBlindedMO = m_bank.Sign(unsignedBlindedMO);
				byte[] signedUnBlindedMO = CommonFunctions.unblindMessage(signedBlindedMO, blindingParams);
				boolean success = CommonFunctions.Verify(newMO.getData(), signedUnBlindedMO, m_bank.getPublic());
				if(false == success) {
					System.out.println("Error");
				} else {
					System.out.println("It worked?!");					
				}
*/
				
				UserAlice.GenerateMOs(Alice_Identity.getText(), GeneratedMOs, BlindingFactors, BlindedMOs, Identity_L_List, m_bank.getPublic());
				Alice_MO_Number.setText(String.valueOf(GeneratedMOs.size()));
				//Add blinding factor and send to bank
			}
		});
		btnAliceSendsMos.setBounds(171, 142, 203, 34);
		btnAliceSendsMos.setText("Alice Sends MOs to Bank");
		
		Button btnBankSignsMo = new Button(shell, SWT.NONE);
		btnBankSignsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Bank verifies 99 out of the 100 MOs and signs and returns the final MO
				ArrayList<Integer> toSignMO = new ArrayList<Integer>();
				m_signedBlindMO = m_bank.VerifyAndSignMOs(GeneratedMOs, BlindedMOs, BlindingFactors, toSignMO);
				m_signedMOIndex = toSignMO.get(0);
				m_unsignedMO = GeneratedMOs.get(m_signedMOIndex).getData();
				m_blindParams = new RSABlindingParameters(BlindingFactors.get(m_signedMOIndex).getPublicKey(), BlindingFactors.get(m_signedMOIndex).getBlindingFactor());
				
				//Show validated MOs and the signed MO on UI
								
				
				//Unblind the bank signature
				m_signedMO = CommonFunctions.unblindMessage(m_signedBlindMO, m_blindParams);
				//Verify bank signature
				boolean sigVerified = CommonFunctions.Verify(m_unsignedMO, m_signedMO, m_bank.getPublic());
				if(false == sigVerified) {
					System.out.println("Error");
				} else {
					System.out.println("It worked?!");					
				}
			}
		});
		btnBankSignsMo.setBounds(171, 258, 203, 34);
		btnBankSignsMo.setText("Bank Signs MO and returns to Alice");
		
		Button btnAliceSendsMo = new Button(shell, SWT.NONE);
		btnAliceSendsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Alice sends MO to Bob
				//Bob sends bit vector to Alice
//				SendMO.Send();
				//Alice sends the corresponding Ls and Rs to Bob
				//Show what Bob receives on UI
			}
		});
		btnAliceSendsMo.setBounds(171, 348, 203, 34);
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
				m_moneyOrder.Submit();
			}
		});
		btnBobSendsMo.setBounds(171, 443, 203, 34);
		btnBobSendsMo.setText("Bob Sends MO to Bank");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(27, 45, 84, 15);
		lblNewLabel.setText("Alice's Identity:");
		
		Alice_Identity = new Text(shell, SWT.BORDER);
		Alice_Identity.setBounds(117, 42, 106, 21);
		Alice_Identity.setText("12345");
		
		Label lblAlicesBalance = new Label(shell, SWT.NONE);
		lblAlicesBalance.setBounds(27, 74, 84, 15);
		lblAlicesBalance.setText("Alice's Balance:");
		
		Alice_Balance = new Text(shell, SWT.BORDER);
		Alice_Balance.setBounds(117, 68, 106, 21);
		Alice_Balance.setText("500");
		
		Label lblAlicesMos = new Label(shell, SWT.NONE);
		lblAlicesMos.setBounds(257, 45, 136, 15);
		lblAlicesMos.setText("Number of Alice's MOs:");
		
		Button btnClickHereTo = new Button(shell, SWT.NONE);
		btnClickHereTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MO_Display window = new MO_Display("Alice's MOs", GeneratedMOs, BlindedMOs, Identity_L_List, BlindingFactors);
				window.open();
			}
		});
		btnClickHereTo.setBounds(257, 74, 230, 42);
		btnClickHereTo.setText("Click here to view Alice's MO Details");
		
		Alice_MO_Number = new Label(shell, SWT.NONE);
		Alice_MO_Number.setBounds(395, 45, 55, 15);
		Alice_MO_Number.setText("0");
		initDataBindings();
		
	}
	protected void initDataBindings() {
		if(null != m_bindingContext) {
			m_bindingContext.dispose();
			m_bindingContext = null;
		}
		m_bindingContext = new DataBindingContext();
	}

}
