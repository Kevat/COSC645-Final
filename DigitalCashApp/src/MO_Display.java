import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;

import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class MO_Display {

	protected Shell shell;
	public String Name;
	public ArrayList<MoneyOrder> GeneratedMOs;
	public ArrayList<RSABlindingParameters> BlindingFactors;
	private Table table;
	
	public MO_Display(String input){
		Name = input;
	}
	
	public MO_Display(String input, ArrayList<MoneyOrder> GeneratedMOsIn,
			ArrayList<RSABlindingParameters> BlindedMOsIn){
		Name = input;
		GeneratedMOs = GeneratedMOsIn;
		BlindingFactors = BlindedMOsIn;
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MO_Display window = new MO_Display("");
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
		shell.setSize(450, 300);
		shell.setText("MO Display");
		
		Label lblMoDisplay = new Label(shell, SWT.CENTER);
		lblMoDisplay.setBounds(139, 10, 174, 15);
		lblMoDisplay.setText(Name);
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 35, 414, 217);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnGeneratedMo = new TableColumn(table, SWT.NONE);
		tblclmnGeneratedMo.setWidth(150);
		tblclmnGeneratedMo.setText("Generated MO");
		
		TableColumn tblclmnLidentity = new TableColumn(table, SWT.NONE);
		tblclmnLidentity.setWidth(250);
		tblclmnLidentity.setText("Blinding Factor");

	    for (int i = 0; i < GeneratedMOs.size(); i++) {
	        TableItem item = new TableItem(table, SWT.NONE);
	        item.setText(new String[] { GeneratedMOs.get(i).toString(), BlindingFactors.get(i).getBlindingFactor().toString() });
	    }
	}
}
