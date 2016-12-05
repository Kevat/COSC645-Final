import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;

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
		
	public boolean Verify(byte[] message, byte[] signature, RSAKeyParameters pubKey) {
		PSSSigner signer = new PSSSigner(new RSAEngine(), new SHA1Digest(), 20);
		signer.init(false,  pubKey);
		signer.update(message, 0, message.length);
		return signer.verifySignature(signature);
	}

	public Integer VerifyAndSignMOs(ArrayList<BigInteger> generatedMOs, ArrayList<BigInteger> textMOs, ArrayList<RSABlindingParameters> blindingParams,
			MoneyOrder m_signedBlindMO) {
		//Bank decides which MO to sign
		Integer MOToSign = (int)(99 * Math.random());
		boolean isValid = true;
		for (int i = 0; i<100; i++)
		{
			if (i != MOToSign)
			{
				//
				BigInteger unblindedMO =  new BigInteger(CommonFunctions.unblindMessage(generatedMOs.get(i).toByteArray(), blindingParams.get(i)));
				if (unblindedMO.compareTo(textMOs.get(i)) == 0)
				{
					String test = "";
				}
			}
		}
		
		//Bank signs the final MO (blinded)
		//RSABlindingParameters blindingParams = CommonFunctions.blindFactor(m_bank.getPublic());
		//m_unsignedBlindMO = new MoneyOrder(CommonFunctions.blindMessage(m_unsignedMO.getData(), blindingParams, m_bank.getPublic()));
		//m_signedBlindMO = new MoneyOrder(this.Sign(m_unsignedBlindMO.getData()));
		
		return null;
	}
}
