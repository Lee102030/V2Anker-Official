public class Crc32 {
    public static AlgoParams Crc32Mpeg2 = new AlgoParams("CRC-32/MPEG-2", 32, 0x04C11DB7L, 0xFFFFFFFFL, false, false, 0x00000000L, 0x0376E6E7L);
    public static final AlgoParams[] Params = new AlgoParams[]{Crc32Mpeg2};
}