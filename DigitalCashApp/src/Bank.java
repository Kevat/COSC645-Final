import java.util.ArrayList;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;

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

		int number = encryptedMOs.size();
		Integer MOToSign = (int)((number - 1) * Math.random());
		indexMO.add(MOToSign);
		for(int i = 0; i < number; ++i){
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
	public Integer ReceiveMoneyOrderRequest(int agreedAmount, ArrayList<MoneyOrder> encryptedMOs){

		Integer MOToSign = (int)((encryptedMOs.size() - 1) * Math.random());

	    return MOToSign;
	}//end receiveMoneyOrderRequest


	public MoneyOrder UnblindMoneyOrderRequest(ArrayList<RSABlindingParameters> blindParams, ArrayList<Integer> indexMO){


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