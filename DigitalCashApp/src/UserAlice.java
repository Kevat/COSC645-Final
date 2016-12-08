import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;

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
    
    public static byte[] GetIdentityFromBitVector(byte[] AliceIdentityBits, BitSet bitVector)
    {
    	byte[] results = new byte[4];
    	byte[] LBytes = ByteBuffer.allocate(4).putInt((int)Math.round(2000000 * Math.random())).array();
    	byte[] RBytes = new byte[4];
    	
    	for (int i = 0; i < bitVector.length(); i++)
    	{
    		//R = L xor with identity
    		RBytes[i] = (byte)(0xff & (int)AliceIdentityBits[i] ^ (int)LBytes[i]);
    		if (bitVector.get(i))
    		{
    			//If true, return L, else return R
    			results[i] = LBytes[i];
    		}
    		else
    		{
    			//If true, return L, else return R
    			results[i] = RBytes[i];    		
    		}
    	}
    	return results;
    }
}
