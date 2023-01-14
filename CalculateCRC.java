import java.util.ArrayList;

public final class CalculateCRC {
    public static byte[] converListtoByteArray(ArrayList<Integer> list) {
        byte[] byteArrayBuffer = new byte[list.size()];
        int counter = 0;
        for(Integer b : list) {
            byteArrayBuffer[counter] = b.byteValue();
            counter++;
        }
        return byteArrayBuffer;
    }
}
