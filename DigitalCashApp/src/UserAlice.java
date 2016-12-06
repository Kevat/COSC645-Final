import java.util.ArrayList;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class UserAlice {
	private static AsymmetricCipherKeyPair m_keyPair;
	        
    public UserAlice() {
        m_keyPair = CommonFunctions.generateKeyPair();
    }    

    public RSAKeyParameters getPublicKey() {
		return (RSAKeyParameters)m_keyPair.getPublic();
    }

    public static void generateMOs(int amount, int num, ArrayList<MoneyOrder> generatedMOs,
    		ArrayList<RSABlindingParameters> blindParams) throws Exception {
        for(int i = 0; i < num; ++i) {
        	MoneyOrder tmpMO = new MoneyOrder();
        	tmpMO.encrypt(amount, Bank.getPublic());
            generatedMOs.add(tmpMO);
            RSABlindingParameters tmpBlindParams = CommonFunctions.generateParams(Bank.getPublic());
            blindParams.add(tmpBlindParams);
        }
    }
}
