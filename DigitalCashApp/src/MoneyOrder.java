import org.bouncycastle.util.encoders.Hex;

public class MoneyOrder {
	byte[] serialNumber = new byte[10];
	byte[] amount = new byte[10];
	private byte[] data = new byte[20];
	byte[] hex = null;

	public MoneyOrder(byte[] newHexData) {
		hex = newHexData;
		// Populate data
		byte [] tmpData = Hex.decode(newHexData);
		System.arraycopy(tmpData, 0, data, 0, 20);
		// Populate serialNumber
		System.arraycopy(newHexData, 0, serialNumber, 0, 10);
		// Populate amount
		System.arraycopy(newHexData, 10, amount, 0, 10);
		hex = Hex.decode(newHexData);
	}

	public MoneyOrder(byte[] newSN, byte[] newAmount) {
		// Populate serialNumber
		System.arraycopy(newSN, 0, serialNumber, 0, 10);
		// Populate amount
		System.arraycopy(newAmount, 0, amount, 0, 10);
		// Populate data
		System.arraycopy(newSN, 0, data, 0, 10);
		System.arraycopy(newAmount, 0, data, 10, 10);
		hex = Hex.encode(data);
	}

	public byte[] getData() {
		return hex;
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
