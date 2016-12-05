import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class Bank {
	private RSAKeyPairGenerator m_generator;
	private AsymmetricCipherKeyPair m_keys;
		
	public Bank() {
		m_generator = new RSAKeyPairGenerator();
		m_generator.init(new RSAKeyGenerationParameters(
				new BigInteger("10001", 16), new SecureRandom(), 2048, 80));
		m_keys = m_generator.generateKeyPair();
	}
		
	public RSAKeyParameters getPublic() {
		return (RSAKeyParameters)m_keys.getPublic();
	}
		
	public byte[] Sign(byte[] message) {
		RSAEngine engine = new RSAEngine();
		engine.init(true, m_keys.getPrivate());

		return engine.processBlock(message, 0, message.length);
	}

	public byte [] VerifyAndSignMOs(ArrayList<MoneyOrder> generatedMOs, ArrayList<byte []> blindedMOs,
			ArrayList<RSABlindingParameters> blindingParams, ArrayList<Integer> toSignMO) {
		//Bank decides which MO to sign
		byte [] toSignBlindMO = null;
		Integer MOToSign = (int)(99 * Math.random());
		boolean isValid = true;
		for (int i = 0; i<100; i++)
		{
			MoneyOrder unblindedMO = null;
			if (i != MOToSign)
			{
				// Unblind MO and verify data
				/*
				unblindedMO = new MoneyOrder(CommonFunctions.unblindMessage(blindedMOs.get(i), blindingParams.get(i)));
				isValid = Arrays.equals(unblindedMO.getData(), generatedMOs.get(i).getData());
				if (false == isValid) {
					System.out.println("Alice is a cheater");
					return null;
				}*/
			} else {
				toSignMO.add(i);
				toSignBlindMO = blindedMOs.get(i);
			}
		}

		if(null == toSignBlindMO) {
			return null;
		}
		
		//Bank signs the final MO (blinded)
		return Sign(toSignBlindMO);
	}
}
