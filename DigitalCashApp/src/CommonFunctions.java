import java.math.BigInteger;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;

public class CommonFunctions {
	public static int GetIntFromLetter(char letter)
	{
		return Character.getNumericValue(letter);
	}
	
	public static int GetIntFromString(String input)
	{
		int output = -1;
		try {
			output = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			// do something here
		}
		return output;
	}
	
	public static char GetLetterFromInt(int input)
	{
		char output = '0';
		
		try {
			output = Integer.toString(input).charAt(0);
		} catch (IndexOutOfBoundsException e) {
			// do something here
		}
		return output;
	}
	
	public static String GetStringFromInt(int input)
	{
		return Integer.toString(input);
	}
	
	public static RSABlindingParameters blindFactor(RSAKeyParameters pubKey) {
		RSABlindingFactorGenerator blindingFactorGenerator = new RSABlindingFactorGenerator();
		blindingFactorGenerator.init(pubKey);
		BigInteger blindingFactor = blindingFactorGenerator.generateBlindingFactor();
		RSABlindingParameters blindingParams = new RSABlindingParameters(pubKey, blindingFactor);
		return blindingParams;
	}
	
	public static byte[] blindMessage(byte[] message, RSABlindingParameters blindingParams, RSAKeyParameters pubKey) {
		byte[] blindedMsg = null;
		PSSSigner signer = new PSSSigner(new RSABlindingEngine(), new SHA1Digest(), 20);
		signer.init(true, blindingParams);
		signer.update(message, 0, message.length);
		try {
			blindedMsg = signer.generateSignature();
		} catch (DataLengthException | CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return blindedMsg;
	}
	
	public static byte[] unblindMessage(byte[] signature, RSABlindingParameters blindingParams) {
		RSABlindingEngine blindingEngine = new RSABlindingEngine();
		blindingEngine.init(false, blindingParams);
		return blindingEngine.processBlock(signature, 0, signature.length);
	}
}
