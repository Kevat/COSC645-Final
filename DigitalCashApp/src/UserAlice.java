import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class UserAlice {
	
//	private static Integer Min = 1;
	private static Integer Max = 100000;
	
	public static void GenerateMOs(String aliceIdentity, ArrayList<MoneyOrder> GeneratedMOs,
			ArrayList<RSABlindingParameters> BlindingFactors_List, ArrayList<byte []> BlindedMOs,
			ArrayList<BigInteger> Identity_L_List, RSAKeyParameters bankPublicKey) {

		for (int i = 0; i < 100; i++)
		{
			MoneyOrder newMO = GenerateTextMO();
			GeneratedMOs.add(newMO);
			
			//Get blinding factor
			RSABlindingParameters blindingParams = CommonFunctions.blindFactor(bankPublicKey);
						
			//Store blinding factor in its own array
			BlindingFactors_List.add(blindingParams);
			
			//Blind the MO
			byte [] unsignedBlindedMO = CommonFunctions.blindMessage(newMO.getData(), blindingParams, bankPublicKey);
			//Add to list of generated MOs
			BlindedMOs.add(unsignedBlindedMO);
			
			//Add identity strings??
			//identity_L_List.add(i);
			Identity_L_List.add(BigInteger.ZERO);
		}
	}
	
	private static MoneyOrder GenerateTextMO()
	{
		//Serial number
		ByteBuffer b = ByteBuffer.allocate(10);
		//Get a double and multiply by Max to get a number between 0 and Max
		//TODO - Should we track the serial numbers which are already used and not use them?
		//Should we just start at 1 and then increment for the sake of keeping this project simple?
		b.putInt((int)(Max * Math.random()));
		byte[] newSN = b.array();
		
		//Amount
		ByteBuffer b1 = ByteBuffer.allocate(10);
		b1.putInt(100);
		byte[] newAmount = b1.array();
		
		MoneyOrder newMO = new MoneyOrder(newSN, newAmount);
		return newMO;
	}
}
