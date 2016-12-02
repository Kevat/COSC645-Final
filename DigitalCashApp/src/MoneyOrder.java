import java.util.ArrayList;

public class MoneyOrder {
	byte[] serialNumber = new byte[10];
	byte[] amount = new byte[10];
	byte[] data = new byte[20];

	public MoneyOrder(byte[] newData) {
		// Populate data
		System.arraycopy(newData, 0, data, 0, 20);
		// Populate serialNumber
		System.arraycopy(newData, 0, serialNumber, 0, 10);
		// Populate amount
		System.arraycopy(newData, 10, amount, 0, 10);
	}

	public MoneyOrder(byte[] newSN, byte[] newAmount) {
		// Populate serialNumber
		System.arraycopy(newSN, 0, serialNumber, 0, 10);
		// Populate amount
		System.arraycopy(newAmount, 0, amount, 0, 10);
		// Populate data
		System.arraycopy(newSN, 0, data, 0, 10);
		System.arraycopy(newAmount, 0, data, 10, 10);
	}

	public static void Generate(String Alice_Identity, String BlindingFactor, ArrayList<Integer> textMOs, ArrayList<Integer> generatedMOs, ArrayList<Integer> identity_L_List) {
		for (int i = 0; i < 100; i++)
		{
			textMOs.add(i);
			generatedMOs.add(i);
			identity_L_List.add(i);
		}
	}
	
	public byte[] getData() {
		return data;
	}
	
	public byte[] getSerialNumber() {
		return serialNumber;
	}

	public byte[] getAmount() {
		return amount;
	}

	public void Submit() {
		
	}
}
