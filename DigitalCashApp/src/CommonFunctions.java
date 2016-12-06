import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;

public class CommonFunctions {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final BigInteger SIGNING_EXP = BigInteger.valueOf(3);
	private static final int RSA_KEY_BITS = 2048;
	public static final int AMOUNT = 100;
	public static final int NUMBER_MOS = 100;
	
	static AsymmetricCipherKeyPair generateKeyPair() {
		try {
			RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
			generator.init(new RSAKeyGenerationParameters(SIGNING_EXP, SECURE_RANDOM, RSA_KEY_BITS, 12));
			return generator.generateKeyPair();
		} catch(Exception e) {
			throw new RuntimeException("Error generating keys!");
		}
	}
	
	public static RSABlindingParameters generateParams(RSAKeyParameters pubKey){
		RSABlindingFactorGenerator blindGenerator = new RSABlindingFactorGenerator();
		blindGenerator.init(pubKey);
		BigInteger blindFactor = blindGenerator.generateBlindingFactor();
		return new RSABlindingParameters(pubKey, blindFactor);
	}
	
    public static byte [] blindData(byte[] msg, RSABlindingParameters blindParams) throws Exception {
        PSSSigner signer = new PSSSigner(new RSABlindingEngine(), new SHA1Digest(), 20);
        signer.init(true, blindParams);
        signer.update(msg, 0, msg.length);
        byte[] sig = signer.generateSignature();
        return sig;
    }
		
    public static byte[] unblindSignature(byte [] signature, RSABlindingParameters blindParams) {
        RSABlindingEngine blindingEngine = new RSABlindingEngine();
        blindingEngine.init(false, blindParams);
        byte[] s = blindingEngine.processBlock(signature, 0, signature.length);
        return s;
    }
}
