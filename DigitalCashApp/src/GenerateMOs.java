import java.util.ArrayList;

public class GenerateMOs {
	public static void Generate(String Alice_Identity, String BlindingFactor, ArrayList<Integer> textMOs, ArrayList<Integer> generatedMOs, ArrayList<Integer> identity_L_List) {
		for (int i = 0; i < 100; i++)
		{
			textMOs.add(i);
			generatedMOs.add(i);
			identity_L_List.add(i);
		}
	}
}
