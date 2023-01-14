public class AlgoParams {
    public long Check;
    public int HashSize;
    public long Init;
    public String Name;
    public long Poly;
    public boolean RefIn;
    public boolean RefOut;
    public long XorOut;
    public AlgoParams(String name, int hashSize, long poly, long init, boolean refIn, boolean refOut, long xorOut, long check)
    {
        Name = name;
        Check = check;
        Init = init;
        Poly = poly;
        RefIn = refIn;
        RefOut = refOut;
        XorOut = xorOut;
        HashSize = hashSize;
    }
}
