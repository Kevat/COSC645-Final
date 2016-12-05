import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.bouncycastle.crypto.params.RSABlindingParameters;

public class UserAlice {
	
	private static Integer Min = 1;
	private static Integer Max = 100000;
	
	public static void GenerateMOs(String aliceIdentity, ArrayList<BigInteger> TextMOs, ArrayList<BigInteger> GeneratedMOs,
			ArrayList<RSABlindingParameters> BlindingFactors_List, ArrayList<BigInteger> Identity_L_List, Bank m_bank) {

		for (int i = 0; i < 100; i++)
		{
			MoneyOrder newMO = GenerateTextMO();
			BigInteger newMOInt = new BigInteger(newMO.getData());
			TextMOs.add(newMOInt);
			
			
			//NOTE: These were written before I figured out the bouncy castle functions
			//Everything below should be replaced with the bouncy castle functions
			//We should still create a new array list to hold the blinding factors.
			
			
			//Get blinding factor
			RSABlindingParameters blindingParams = CommonFunctions.blindFactor(m_bank.getPublic());
						
			//Store blinding factor in its own array
			BlindingFactors_List.add(blindingParams);
			
			//Blind the MO
			MoneyOrder m_unsignedBlindMO = new MoneyOrder(CommonFunctions.blindMessage(newMO.getData(), blindingParams, m_bank.getPublic()));
			MoneyOrder m_unsignedTextMO = new MoneyOrder(CommonFunctions.unblindMessage(m_unsignedBlindMO.getData(), blindingParams));
			//Add to list of generated MOs
			GeneratedMOs.add(new BigInteger(m_unsignedBlindMO.getData()));
			
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
