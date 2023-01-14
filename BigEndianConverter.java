public final class BigEndianConverter {
    public static byte[] toBytesConverter2(long n) {
        if(n > 65535) {
            System.out.println("Invalid value for 2 bytes. Please check CRC16 value");
            System.exit(1);
        }
        byte[] valueToConvert = new byte[2];
        valueToConvert[0] = (byte) (n >> 8);
        valueToConvert[1] = (byte) (n);
        return valueToConvert;
    }

    public static byte[] toBytesConverter4(long n)  {
        if(n < 0) { // check mechanism later
            System.out.println("Invalid value for 4 bytes. Please check bin FILE value");
            System.exit(1);
        }
        byte [] valueToConvert = new byte[4];
        valueToConvert[0] = (byte) (n >> 24);
        valueToConvert[1] = (byte) (n >> 16);
        valueToConvert[2] = (byte) (n >> 8);
        valueToConvert[3] = (byte) (n);
        return valueToConvert;
    }
}
