
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
}
