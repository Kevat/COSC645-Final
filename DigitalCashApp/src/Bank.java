import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
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
}
