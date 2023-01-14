import java.util.ArrayList;

public final class BufferPrinter {
    public static String printTCPValidator(byte[] b) {
        ArrayList<Integer> arrayBuffer = new ArrayList<>();
        String s = new String(b);

        for(byte data: b) {
            arrayBuffer.add((int) data & 0xFF);
        }
        //System.out.println(s + " " + arrayBuffer);

        if(arrayBuffer.get(0) == 90 && arrayBuffer.get(6) == 3) {
            return "B0";
        } else if(arrayBuffer.get(0) == 90 && arrayBuffer.get(6) == 186) {
            return "2";
        } else if(arrayBuffer.get(0) == 90 && arrayBuffer.get(6) == 184) {
            return "B8";
        } else {
            return "empty";
        }
    }

    public static void printTCP(byte[] b) {
        ArrayList<Integer> arrayBuffer = new ArrayList<>();
        for(byte data: b) {
            arrayBuffer.add((int) data & 0xFF);
        }
    }
}
