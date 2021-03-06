import java.util.List;
import java.util.UUID;

import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class MoneyOrder {
	private String serialNumber;
	
	private byte[] leftCommitments;
	private byte[] rightCommitments;
	
	private byte [] encrypted;
	private byte [] signature;

	public MoneyOrder() {
		this.serialNumber = UUID.randomUUID().toString();
	}

	public MoneyOrder(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public byte[] getLeftCommitments() {
		return leftCommitments;
	}

	public void setLeftCommitments(byte[] commitment) {
		leftCommitments = commitment;
	}

	public byte[] getRightCommitments() {
		return rightCommitments;
	}

	public void setRightCommitments(byte[] commitment) {
		rightCommitments = commitment;
	}
	
	public byte[] getEncrypted() {
		return this.encrypted;
	}
	
	public String getSerialNumber() {
		return this.serialNumber;
	}

	public byte[] getSignature() {
		return this.signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public void encrypt(int amount, RSAKeyParameters pubKey) {
		StringBuilder sb = new StringBuilder(this.serialNumber);
		sb.append("~");
		sb.append(Integer.toString(amount));
		byte [] inputData = sb.toString().getBytes();
		
		RSAEngine encryptEngine = new RSAEngine();
		encryptEngine.init(true, pubKey);
		this.encrypted = encryptEngine.processBlock(inputData, 0, inputData.length);
	}
}
