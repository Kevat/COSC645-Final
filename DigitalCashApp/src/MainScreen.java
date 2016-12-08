import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;

import javax.swing.SwingConstants;

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

	//Variables to show on the screen
	//Used for generating MOs
	public ArrayList<MoneyOrder> GeneratedMOs = new ArrayList<MoneyOrder>();
	public ArrayList<RSABlindingParameters> BlindParams = new ArrayList<RSABlindingParameters>();  
	public ArrayList<byte[]> BlindedMOs = new ArrayList<byte[]>();  
	//Stores the index of the MO which was signed
	Integer m_signedMOIndex;
	
	//Used for bank signing MOs
	private MoneyOrder m_signedBlindMO = null;
	private MoneyOrder m_unsignedMO = null;
	private MoneyOrder m_signedMO = null;
	
	//Used to send MO to Bob
	public static BitSet m_bobBitVector;
	public static byte[] AliceIdentityBits;
	public static String aliceIDAsBits = "";
	public static String LBytesAsBits = "";
	
	//Used to Submit MO
	public boolean IsMOValid;
	public String m_cheatMessage;
	private int m_bobBalance = 500;
	private int m_aliceBalance = 500;
	private byte[] m_aliceIDReturned;
	private ArrayList<String> m_usedSerialNumbers = new ArrayList<String>();
	private ArrayList<byte[]> m_usedMoneyOrders = new ArrayList<byte[]>();
	private ArrayList<byte[]> m_previousIdentities = new ArrayList<byte[]>();
	private ArrayList<BitSet> m_previousBitVectors = new ArrayList<BitSet>();

	protected Shell shell;
	private Text Alice_Identity;
	private Text Alice_Balance;
	private Text Bob_Balance;
	private Label m_generateStatusLabel;
	private Label m_bankVerifySignStatusLabel;
	private Label m_bobVerifySignStatusLabel;
	private Label m_endStatusLabel;
	private Label m_overallStatusLabel;
	private Label Bob_Bit_Vector_Label;

	Label Alice_MO_Number;
	private Text Identity_L;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(DisplayRealm.getRealm(display), new Runnable() {
			public void run() {
				try {
					//Store Alice's identity
					AliceIdentityBits = ByteBuffer.allocate(4).putInt(1234567).array();
					
					for (int i = 0; i < 4; i++)
					{
						aliceIDAsBits += Integer.toBinaryString(AliceIdentityBits[i] & 0xFF).replace(' ', '0') + "-";
					}

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
		shell.setSize(552, 855);
		shell.setText("Digital Cash Application");
		
		Button btnBobSendsMo = new Button(shell, SWT.CENTER);
		btnBobSendsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Send MO received from Alice to Bank
				boolean success = Bank.process(m_signedMO, m_aliceIDReturned, m_bobBitVector, m_usedSerialNumbers, m_usedMoneyOrders, 
						m_previousIdentities, m_endStatusLabel, m_previousBitVectors);
				
				//Show output on UI
				String successText = "";
				
				if(false == success) {
					successText = "Someone cheated!";
				} else {
					m_bobBalance = (m_bobBalance + CommonFunctions.AMOUNT);
					Bob_Balance.setText(Integer.toString(m_bobBalance));
					successText = "SUCCESS!!";
				}
								
				m_overallStatusLabel.setText(successText);
			}
		});
		btnBobSendsMo.setBounds(151, 610, 275, 34);
		btnBobSendsMo.setText("Bob Sends MO to Bank");
		
		Label lblDigitalCash = new Label(shell, SWT.NONE);
		lblDigitalCash.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblDigitalCash.setBounds(151, 10, 182, 21);
		lblDigitalCash.setText("Digital Cash - COSC 645 Final");
		
		Button btnAliceSendsMos = new Button(shell, SWT.CENTER);
		btnAliceSendsMos.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Generate 100 MOs, store into a local variable and show on UI
				try {
					UserAlice.generateMOs(CommonFunctions.AMOUNT, CommonFunctions.NUMBER_MOS, GeneratedMOs, BlindParams, AliceIdentityBits);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Alice_MO_Number.setText(String.valueOf(GeneratedMOs.size()));
				//Add blinding factor and send to bank
				String successText = CommonFunctions.AMOUNT + " money orders successfully generated";
				m_generateStatusLabel.setText(successText);
			}
		});
		btnAliceSendsMos.setBounds(151, 192, 275, 34);
		btnAliceSendsMos.setText("Alice Generates MOs; Sends to Bank");
		
		m_generateStatusLabel = new Label(shell, SWT.CENTER);
		m_generateStatusLabel.setBounds(151, 242, 275, 34);
		m_generateStatusLabel.setText("");
		
		Button btnBankSignsMo = new Button(shell, SWT.NONE);
		btnBankSignsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Bank verifies 99 out of the 100 MOs and signs and returns the final MO
				ArrayList<Integer> indexMO = new ArrayList<Integer>();
				try {
					m_signedBlindMO = Bank.VerifyAndSignMOs(CommonFunctions.AMOUNT, GeneratedMOs, BlindParams, indexMO);
					m_aliceBalance = (m_aliceBalance - CommonFunctions.AMOUNT);
					Alice_Balance.setText(Integer.toString(m_aliceBalance));
					byte[] leftCommitment = m_signedBlindMO.getLeftCommitments();
					
					for (int i = 0; i < 4; i++)
					{
						LBytesAsBits += Integer.toBinaryString(leftCommitment[i] & 0xFF).replace(' ', '0') + "-";
					}
					Identity_L.setText(LBytesAsBits);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				m_signedMOIndex = indexMO.get(0);
				m_unsignedMO = GeneratedMOs.get(m_signedMOIndex);
				//Show validated MOs and the signed MO on UI
				String successText = "Bank verified " + (CommonFunctions.AMOUNT-1) + " money orders and signed the last successfully; Alice's account adjusted accordingly";
				m_bankVerifySignStatusLabel.setText(successText);
			}
		});
		btnBankSignsMo.setBounds(151, 301, 275, 34);
		btnBankSignsMo.setText("Bank Verifies; Sends signed MO to Alice");
		
		m_bankVerifySignStatusLabel = new Label(shell, SWT.CENTER);
		m_bankVerifySignStatusLabel.setBounds(151, 351, 275, 34);
		m_bankVerifySignStatusLabel.setText("");
		
		Button btnAliceSendsMo = new Button(shell, SWT.CENTER);
		btnAliceSendsMo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Alice sends MO to Bob
				//Unblind the bank signature
				m_signedMO = m_signedBlindMO;
				m_signedMO.setSignature(CommonFunctions.unblindSignature(m_signedBlindMO.getSignature(), BlindParams.get(m_signedMOIndex)));
				

				/// Get Bob's bit vector
				m_bobBitVector = UserBob.getBitVector(4);
				
				//Show bob's bit vector
				String bitVectorStr = "";
				for (int i = 0; i < 4; i++) {
					if (m_bobBitVector.get(i)){
						bitVectorStr += "1";
					}
					else {
						bitVectorStr += 0;
					}
				}
				Bob_Bit_Vector_Label.setText(bitVectorStr);
				
				/// Get Alice's identity from bit vector
				m_aliceIDReturned = UserAlice.GetIdentityFromBitVector(AliceIdentityBits, m_signedMO.getLeftCommitments(), m_signedMO.getRightCommitments(), m_bobBitVector);
				
				
				//Verify bank signature
				boolean sigVerified = Bank.verifySignature(m_unsignedMO.getEncrypted(), m_signedMO.getSignature());
				if(false == sigVerified) {
					System.out.println("Signature not verified");
				}
								
				String successText = "Bob received the money order and the ID!";
				m_bobVerifySignStatusLabel.setText(successText);
			}
		});
		btnAliceSendsMo.setBounds(151, 421, 275, 34);
		btnAliceSendsMo.setText("Alice sends to Bob; Bob verifies Bank signature");
		
		m_bobVerifySignStatusLabel = new Label(shell, SWT.CENTER);
		m_bobVerifySignStatusLabel.setBounds(151, 650, 275, 34);
		m_bobVerifySignStatusLabel.setText("");
		
		m_endStatusLabel = new Label(shell, SWT.CENTER);
		m_endStatusLabel.setBounds(151, 482, 275, 98);
		m_endStatusLabel.setText("");

		m_overallStatusLabel = new Label(shell, SWT.CENTER);
		m_overallStatusLabel.setBounds(151, 669, 275, 34);
		m_overallStatusLabel.setText("");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(27, 43, 84, 15);
		lblNewLabel.setText("Alice's Identity:");
		
		Alice_Identity = new Text(shell, SWT.BORDER);
		Alice_Identity.setBounds(117, 42, 296, 21);
		Alice_Identity.setText(aliceIDAsBits);
		
		Label lblAlicesBalance = new Label(shell, SWT.NONE);
		lblAlicesBalance.setBounds(27, 99, 84, 15);
		lblAlicesBalance.setText("Alice's Balance:");
		
		Alice_Balance = new Text(shell, SWT.BORDER);
		Alice_Balance.setBounds(117, 96, 106, 21);
		Alice_Balance.setText(Integer.toString(m_aliceBalance));

		Label lblBobsBalance = new Label(shell, SWT.NONE);
		lblBobsBalance.setBounds(27, 125, 84, 15);
		lblBobsBalance.setText("Bob's Balance:");
		
		Bob_Balance = new Text(shell, SWT.BORDER);
		Bob_Balance.setBounds(117, 122, 106, 21);
		Bob_Balance.setText(Integer.toString(m_bobBalance));
		
		Label lblAlicesMos = new Label(shell, SWT.NONE);
		lblAlicesMos.setBounds(252, 99, 136, 15);
		lblAlicesMos.setText("Number of Alice's MOs:");
		
		Button btnClickHereTo = new Button(shell, SWT.NONE);
		btnClickHereTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MO_Display window = new MO_Display("Alice's MOs", GeneratedMOs, BlindParams);
				window.open();
			}
		});
		btnClickHereTo.setBounds(239, 115, 213, 34);
		btnClickHereTo.setText("Click here to view Alice's MO Details");
		
		Alice_MO_Number = new Label(shell, SWT.NONE);
		Alice_MO_Number.setBounds(394, 99, 55, 15);
		Alice_MO_Number.setText("0");
		
		Label lblIdentityL = new Label(shell, SWT.NONE);
		lblIdentityL.setBounds(27, 75, 55, 15);
		lblIdentityL.setText("Identity L:");
		
		Identity_L = new Text(shell, SWT.BORDER);
		Identity_L.setBounds(117, 69, 296, 21);
		
		Label lblBobsBitVector = new Label(shell, SWT.NONE);
		lblBobsBitVector.setBounds(151, 461, 97, 15);
		lblBobsBitVector.setText("Bob's Bit Vector:");
		
		Bob_Bit_Vector_Label = new Label(shell, SWT.NONE);
		Bob_Bit_Vector_Label.setBounds(252, 458, 90, 15);
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
