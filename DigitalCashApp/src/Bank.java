import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.eclipse.swt.widgets.Label;

public class Bank {
	private static AsymmetricCipherKeyPair m_keys = CommonFunctions.generateKeyPair();
	private static MoneyOrder m_signedMO = null;
	
		
	public Bank() {
	}
	
	public static RSAKeyParameters getPublic() {
		return (RSAKeyParameters)m_keys.getPublic();
	}
	
	private static RSAPrivateCrtKeyParameters getPrivate() {
		return (RSAPrivateCrtKeyParameters)m_keys.getPrivate();
	}
	
	public static MoneyOrder VerifyAndSignMOs(int agreedAmount, ArrayList<MoneyOrder> encryptedMOs,
			ArrayList<RSABlindingParameters> blindParams, ArrayList<Integer> indexMO) throws Exception {
		MoneyOrder toSignBlindMO = null;
		RSABlindingParameters toUseBlindParams = null;

		Integer MOToSign = (int)((encryptedMOs.size() - 1) * Math.random());
		indexMO.add(MOToSign);
		for(int i = 0; i < encryptedMOs.size(); ++i){
			if(i != MOToSign) {
				boolean status = VerifyMO(encryptedMOs.get(i).getEncrypted(), agreedAmount);
				if(false == status) {
					// Alice cheated
					System.out.println("Alice cheated!");
					return null;
				}
			} else {
				toSignBlindMO = encryptedMOs.get(i);
				toUseBlindParams = blindParams.get(i);
			}
		}
		if(null != toSignBlindMO) {
			toSignBlindMO.setSignature(signMO(CommonFunctions.blindData(toSignBlindMO.getEncrypted(), toUseBlindParams)));
			m_signedMO = toSignBlindMO;
			return m_signedMO;
		} else {
			return null;
		}
	}
	
	public static boolean VerifyMO(byte[] moneyOrder, int amount) {
		int tmpAmount = decryptAmount(moneyOrder);
		if(tmpAmount != amount) {
			System.out.println("Not verified");
			return false;
		}
		return true;
	}
	
	public static byte[] decrypt(byte[] encrypted) {
		RSAEngine decryptEngine = new RSAEngine();
		decryptEngine.init(false, getPrivate());
		return decryptEngine.processBlock(encrypted, 0, encrypted.length);
	}

	public static int decryptAmount(byte[] encrypted) {
		String str = new String(decrypt(encrypted));
		String [] parts = str.split("~");

		return Integer.parseInt(parts[1]);
	}
	
	public static byte[] signMO(byte[] blindedData) {
		RSAEngine signEngine = new RSAEngine();
		signEngine.init(true, (RSAKeyParameters)getPrivate());
		byte[] blindSign = signEngine.processBlock(blindedData, 0, blindedData.length);
		return blindSign;
	}
	
	public static boolean verifySignature( byte[] message, byte[] signature) {
		PSSSigner signer = new PSSSigner(new RSAEngine(), new SHA1Digest(), 20);
		signer.init(false,  getPublic());
		signer.update(message, 0, message.length);
		return signer.verifySignature(signature);
	}
	
	public static boolean process(MoneyOrder bobOrder) {
		//Validate the MO checks out
		if(bobOrder.getSerialNumber() != m_signedMO.getSerialNumber()) {
			System.out.println("Serialnumber not matching?!");
			return false;
		}
		if(bobOrder.getSignature() != m_signedMO.getSignature()) {
			System.out.println("Signature not matching?!");
			return false;
		}
		if(bobOrder.getEncrypted() != m_signedMO.getEncrypted()) {
			System.out.println("Encryption not matching?!");
			return false;
		}
		if(false == verifySignature(bobOrder.getEncrypted(), bobOrder.getSignature())) {
			System.out.println("Signature not validated?!");
			return false;
		}
		//Validate Ls and Rs from Bob match
		//If same serial number was used before, try to find Alice using Ls and Rs
		return true;
	}
	
//added in process - Andy	
//
//  Why would Alice send the Bank all the blinding factors, trusting the bank to only unblind 99 of the 100 money orders?	
	
	private static ArrayList<MoneyOrder> MoneyOrderRequestReceived;
	private static ArrayList<MoneyOrder> MoneyOrderOutstanding;
	
	private class MoneyOrderRequestLog{
		ArrayList<MoneyOrder> moneyOrderRequests;
		String moneyOrderRequestNumber;
		
		MoneyOrderRequestLog(ArrayList<MoneyOrder> moneyOrderRequests, String moneyOrderRequestNumber){
			this.moneyOrderRequests = moneyOrderRequests;
			this.moneyOrderRequestNumber = moneyOrderRequestNumber;
		}
	
	private ArrayList<MoneyOrderRequestLog> moneyOrderRequestLogArray;
	
	
	
	public Integer[] ReceiveMoneyOrderRequest(int agreedAmount, ArrayList<MoneyOrder> encryptedMOs){

		Integer MOToSign = (int)((encryptedMOs.size() - 1) * Math.random());
		Integer[] requestResponse = new Integer[2];  //position 0 is money order that will be signed and position 1 is request number
		AtomicInteger requestNumber;

		requestResponse[0] = MOToSign;
		//requestResponse[1] = requestNumber.getAndIncrement();
		//requestResponse[1] = requestNumber.addAndGet((int)(10 * Math.random()));		
		
		MoneyOrderRequestLog moneyOrderRequestLog = new MoneyOrderRequestLog(encryptedMOs, requestResponse[1].toString()); //should request number be randomly generated or sequential?
		
		
		moneyOrderRequestLogArray.add(moneyOrderRequestLog);
		

	    return requestResponse;
	    //Alice sends Bank an array of blinding factors with blinding factor at the index of the money order to be signed voided and the request number.
	    
	}//end receiveMoneyOrderRequest

	

	

	public MoneyOrder UnblindMoneyOrderRequest(ArrayList<RSABlindingParameters> blindParams, ArrayList<Integer> indexMO){


		MoneyOrderOutstanding.add(m_signedMO);
		 return m_signedMO;


	}




/*
	public MoneyOrder unblindMoneyOrderRequest(blindingFactors[], int amount){

	int i = 0;

	for (i = 0; i < blindingFactors.length; i++){
	    if (!moneyOrderRequest[i].unblind(amount){
	    //if amount of money order request does not equal unblinded money order, fraud attempt
	        return void moneyOrder
	    }

	//if all 99 money orders are good, sign money order and add to moneyOrdersOut



	}


	}//end unblind  */

	}

	public static boolean process(MoneyOrder m_signedMO, byte[] m_aliceIDReturned, BitSet bobBitVector,
			ArrayList<String> m_usedSerialNumbers, ArrayList<byte[]> m_usedMoneyOrders,
			ArrayList<byte[]> m_previousIdentities, Label m_statusLabel, ArrayList<BitSet> m_previousBitVectors) {

		//First, validate the MO (encryption, signature, etc.)
		boolean isValid = Bank.process(m_signedMO);

		//If not valid, then check if same MO was used before
		boolean isSameMO = false;
		for (int i = 0; i < m_usedMoneyOrders.size(); i++)
		{
			//When checking if the MO is same, check the encrypted MO AND the ID strings
			if (Arrays.equals(m_signedMO.getEncrypted(), m_usedMoneyOrders.get(i)) && Arrays.equals(m_aliceIDReturned, m_previousIdentities.get(i))) {
				isSameMO = true;
			}
		}
		
		boolean isSameSerialNumber = false;
		for (int i = 0; i < m_usedSerialNumbers.size(); i++)
		{
			if (m_signedMO.getSerialNumber().equals(m_usedSerialNumbers.get(i))) {
				isSameSerialNumber = true;
			}
		}
		
		if (isSameMO)
		{
			//Bob cheated
			m_statusLabel.setText("The same MO was used twice! Bob cheated!");
			isValid = false;
		}
		else if (isSameSerialNumber)
		{
			//If not valid and the same MO was not used before, use Ls and Rs to find identity string
			
			for (int i = 0; i < m_usedSerialNumbers.size(); i++) {
				if (m_usedSerialNumbers.get(i).equals(m_signedMO.getSerialNumber())) {
					//For each matching serial number
					String cheaterID = "";
					for (int j = 0; j < 4; j++)
					{
						boolean previousBit = m_previousBitVectors.get(i).get(j);
						boolean currentBit = bobBitVector.get(j);
						byte IDByte;
						if (previousBit != currentBit){
							IDByte = (byte)(0xff & (int)m_previousIdentities.get(i)[j] ^ (int)m_aliceIDReturned[j]);
							cheaterID += Integer.toBinaryString(IDByte & 0xFF).replace(' ', '0');
						}
						else {
							cheaterID += "XXXXXXX";
						}
						cheaterID +="-";
					}

					m_statusLabel.setText("The same serial number was used twice! The ID of the offending user is " + cheaterID);
				}
			}
			
			isValid = false;
		}
		else {
			//No one cheated
			//Add MO to usedMOs
			m_usedMoneyOrders.add(m_signedMO.getEncrypted());
			
			//Add serial number to used serial numbers
			m_usedSerialNumbers.add(m_signedMO.getSerialNumber());
			
			//Add identity to previous identity
			m_previousIdentities.add(m_aliceIDReturned);
			
			//Add bit vector to previously used bit vectors
			m_previousBitVectors.add(bobBitVector);

			m_statusLabel.setText("Money order successfully validated; Bob's account adjusted accodingly");
			isValid = true;
		}
		
		return isValid;
	}
}