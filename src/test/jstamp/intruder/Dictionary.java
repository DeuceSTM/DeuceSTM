package jstamp.intruder;

public class Dictionary {
	String global_defaultSignatures[];
	int global_numDefaultSignature;

	public Dictionary() {
		global_numDefaultSignature = 71;
		global_defaultSignatures = new String[71];

		global_defaultSignatures[0] = "about";
		global_defaultSignatures[1] = "after";
		global_defaultSignatures[2] = "all";
		global_defaultSignatures[3] = "also";
		global_defaultSignatures[4] = "and";
		global_defaultSignatures[5] = "any";
		global_defaultSignatures[6] = "back";
		global_defaultSignatures[7] = "because";
		global_defaultSignatures[8] = "but";
		global_defaultSignatures[9] = "can";
		global_defaultSignatures[10] = "come";
		global_defaultSignatures[11] = "could";
		global_defaultSignatures[12] = "day";
		global_defaultSignatures[13] = "even";
		global_defaultSignatures[14] = "first";
		global_defaultSignatures[15] = "for";
		global_defaultSignatures[16] = "from";
		global_defaultSignatures[17] = "get";
		global_defaultSignatures[18] = "give";
		global_defaultSignatures[19] = "good";
		global_defaultSignatures[20] = "have";
		global_defaultSignatures[21] = "him";
		global_defaultSignatures[22] = "how";
		global_defaultSignatures[23] = "into";
		global_defaultSignatures[24] = "its";
		global_defaultSignatures[25] = "just";
		global_defaultSignatures[26] = "know";
		global_defaultSignatures[27] = "like";
		global_defaultSignatures[28] = "look";
		global_defaultSignatures[29] = "make";
		global_defaultSignatures[30] = "most";
		global_defaultSignatures[31] = "new";
		global_defaultSignatures[32] = "not";
		global_defaultSignatures[33] = "now";
		global_defaultSignatures[34] = "one";
		global_defaultSignatures[35] = "only";
		global_defaultSignatures[36] = "other";
		global_defaultSignatures[37] = "out";
		global_defaultSignatures[38] = "over";
		global_defaultSignatures[39] = "people";
		global_defaultSignatures[40] = "say";
		global_defaultSignatures[41] = "see";
		global_defaultSignatures[42] = "she";
		global_defaultSignatures[43] = "some";
		global_defaultSignatures[44] = "take";
		global_defaultSignatures[45] = "than";
		global_defaultSignatures[46] = "that";
		global_defaultSignatures[47] = "their";
		global_defaultSignatures[48] = "them";
		global_defaultSignatures[49] = "then";
		global_defaultSignatures[50] = "there";
		global_defaultSignatures[51] = "these";
		global_defaultSignatures[52] = "they";
		global_defaultSignatures[53] = "think";
		global_defaultSignatures[54] = "this";
		global_defaultSignatures[55] = "time";
		global_defaultSignatures[56] = "two";
		global_defaultSignatures[57] = "use";
		global_defaultSignatures[58] = "want";
		global_defaultSignatures[59] = "way";
		global_defaultSignatures[60] = "well";
		global_defaultSignatures[61] = "what";
		global_defaultSignatures[62] = "when";
		global_defaultSignatures[63] = "which";
		global_defaultSignatures[64] = "who";
		global_defaultSignatures[65] = "will";
		global_defaultSignatures[66] = "with";
		global_defaultSignatures[67] = "work";
		global_defaultSignatures[68] = "would";
		global_defaultSignatures[69] = "year";
		global_defaultSignatures[70] = "your";
	}

	public String get(int i) {
		if (i < 0 || i >= global_numDefaultSignature) {
			System.out.print("dictionary_get: Index out of bounds");
		}
		return global_defaultSignatures[i];
	}

	public String match(String str) {
		int i;

		//    System.out.println("str= " + str);
		for (i = 0; i < global_numDefaultSignature; i++) {
			//       System.out.println("global_numDefaultSignature= " + global_numDefaultSignature + " str= " + str + " global_defaultSignatures[" +i+"]= " + global_defaultSignatures[i]);
			if (global_defaultSignatures[i].equals(str)) {
				return str;
			}
		}
		return null;
	}
}
