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
    		ArrayList<RSABlindingParameters> blindParams, byte[] AliceIdentityBits) throws Exception {
        for(int i = 0; i < num; ++i) {
        	MoneyOrder tmpMO = new MoneyOrder();

			byte[] leftBytes = ByteBuffer.allocate(4).putInt((int)Math.round(2000000 * Math.random())).array();
			//R = L xor with identity
			byte[] rightBytes = new byte[4];
			for (int j = 0; j < 4; j++)
	    	{
				rightBytes[j] = (byte)(0xff & (int)AliceIdentityBits[j] ^ (int)leftBytes[j]);;
	    	}
			tmpMO.setLeftCommitments(leftBytes);
			tmpMO.setRightCommitments(rightBytes);
			
        	tmpMO.encrypt(amount, Bank.getPublic());
            generatedMOs.add(tmpMO);
            RSABlindingParameters tmpBlindParams = CommonFunctions.generateParams(Bank.getPublic());
            blindParams.add(tmpBlindParams);
        }
    }
    
    public static byte[] GetIdentityFromBitVector(byte[] AliceIdentityBits, byte[] leftBytes, byte[] rightBytes, BitSet bitVector)
    {
    	byte[] results = new byte[4];
    	
    	for (int i = 0; i < 4; i++)
    	{
    		if (bitVector.get(i))
    		{
    			//If true, return L, else return R
    			results[i] = leftBytes[i];
    		}
    		else
    		{
    			//If true, return L, else return R
    			results[i] = rightBytes[i];    		
    		}
    	}
    	return results;
    }
}
