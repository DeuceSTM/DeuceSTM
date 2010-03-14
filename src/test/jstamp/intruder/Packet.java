package jstamp.intruder;

public class Packet {
	int flowId;
	int fragmentId;
	int numFragment;
	int length;
	byte[] data;

	public Packet(int numDataBytes) {
		data = new byte[numDataBytes];
	}

	public static int compareFlowID(Packet aPtr, Packet bPtr) {
		return aPtr.flowId - bPtr.flowId;
	}

	public static int compareFragmentID(Packet aPtr, Packet bPtr) {
		return aPtr.fragmentId - bPtr.fragmentId;
	}
}
