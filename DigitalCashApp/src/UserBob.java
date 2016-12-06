import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class UserBob {
	private static AsymmetricCipherKeyPair m_keyPair;
	        
    public UserBob() {
        m_keyPair = CommonFunctions.generateKeyPair();
    }    

    public RSAKeyParameters getPublicKey() {
		return (RSAKeyParameters)m_keyPair.getPublic();
    }

    public boolean verifySignature(MoneyOrder verifyMO) {
	        return Bank.verifySignature(verifyMO.getEncrypted(), verifyMO.getSignature());
    }
}
