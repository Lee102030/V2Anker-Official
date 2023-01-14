import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public final class FrameBuilder {
    public static byte[] verifyFrame(String arg) {
        CrcCalculator calculator8 = new CrcCalculator(Crc8.Crc8);
        CrcCalculator calculator16 = new CrcCalculator(Crc16.Crc16Xmodem);
        CrcCalculator calculator32 = new CrcCalculator(Crc32.Crc32Mpeg2);
        Integer[] firstFrame = {90, 1, 0, 0, 0, 0, 183, 0, 0, 0, 0, 0, 8};
        ArrayList<Integer> bufferArrayList = new ArrayList<>(Arrays.asList(firstFrame));
        ArrayList<Integer> bufferForCRC32 = new ArrayList<>();
        byte[] bufferToCRC8 = CalculateCRC.converListtoByteArray(bufferArrayList);
        //File path = new File("C:\\anker\\AnkerV1.1.3.bin");
        File path = new File(arg);
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] arr = new byte[(int) path.length()];
            inputStream.read(arr);
            inputStream.close();
            long sizeOfData = arr.length;
            long totalSizeOfData = 0;
            if(sizeOfData % 32 > 0) {
                totalSizeOfData = sizeOfData + (32 - (sizeOfData % 32));
            } else {
                totalSizeOfData = sizeOfData;
            }

            bufferArrayList.add((int) calculator8.Calc(bufferToCRC8, 0, bufferToCRC8.length));
            byte[] bigEndianBuffer = BigEndianConverter.toBytesConverter4(totalSizeOfData);
            bufferArrayList.add((int) bigEndianBuffer[0]);
            bufferArrayList.add((int) bigEndianBuffer[1]);
            bufferArrayList.add((int) bigEndianBuffer[2]);
            bufferArrayList.add((int) bigEndianBuffer[3]);

            for(byte b : arr) {
                bufferForCRC32.add((int) b);
            }
            for(int i  = 0; i < (32 - (arr.length % 32)); i++) {
                bufferForCRC32.add(0xFF);
            }
            byte[] toCRC32 = CalculateCRC.converListtoByteArray(bufferForCRC32);
            long returnFromCRC32 = calculator32.Calc(toCRC32, 0 ,toCRC32.length);
            byte[] crc32Buffer = BigEndianConverter.toBytesConverter4(returnFromCRC32);
            bufferArrayList.add((int) crc32Buffer[0]);
            bufferArrayList.add((int) crc32Buffer[1]);
            bufferArrayList.add((int) crc32Buffer[2]);
            bufferArrayList.add((int) crc32Buffer[3]);

            byte[] finalFrame = CalculateCRC.converListtoByteArray(bufferArrayList);
            long returnFromCRC16 = calculator16.Calc(finalFrame, 0, finalFrame.length);
            byte[] crc16Buffer = BigEndianConverter.toBytesConverter2(returnFromCRC16);
            bufferArrayList.add((int) crc16Buffer[0]);
            bufferArrayList.add((int) crc16Buffer[1]);
            return CalculateCRC.converListtoByteArray(bufferArrayList);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
    public static byte[] commandBuilder(String arg) {
        CrcCalculator calculator8 = new CrcCalculator(Crc8.Crc8);
        CrcCalculator calculator16 = new CrcCalculator(Crc16.Crc16Xmodem);

        Integer[] buildFrame = {90, 1, 0, 0, 0, 0, Integer.parseInt(arg, 16), 0, 0, 0, 0, 0, 0};
        ArrayList<Integer> arrayToReturn = new ArrayList<>(Arrays.asList(buildFrame));

        byte[] toCRC8 = CalculateCRC.converListtoByteArray(arrayToReturn);
        arrayToReturn.add((int) calculator8.Calc(toCRC8, 0, toCRC8.length));

        byte[] toCRC16 = CalculateCRC.converListtoByteArray(arrayToReturn);
        byte[] bigEndian2BytesReturn = BigEndianConverter.toBytesConverter2(calculator16.Calc(toCRC16,0,toCRC16.length));

        for(byte b : bigEndian2BytesReturn) {
            arrayToReturn.add((int) b);
        }
        return CalculateCRC.converListtoByteArray(arrayToReturn);
    }

    public static void dataFrameSender(Socket mainSocket, InputStream in, OutputStream out, String arg) {
        long indexCounterLong = 0;
        CrcCalculator calculator8 = new CrcCalculator(Crc8.Crc8);
        CrcCalculator calculator16 = new CrcCalculator(Crc16.Crc16Xmodem);
        try {
            //if(mainSocket.isConnected()) {
            //    System.out.println("\nSocket connected to " + mainSocket.getInetAddress());
            //}
            Integer[] buildFrame = {90, 1, 0, 0, 0, 0, 182, 0, 0, 0, 0, 0, 36};
            ArrayList<Integer> firstFrameWithCRC8 = new ArrayList<>(Arrays.asList(buildFrame));
            byte[] toCRC8 = CalculateCRC.converListtoByteArray(firstFrameWithCRC8);
            firstFrameWithCRC8.add((int) calculator8.Calc(toCRC8, 0, toCRC8.length));
            // firstFrameWithCRC8

            //File path = new File("C:\\anker\\AnkerV1.1.3.bin");
            File path = new File(arg);
            FileInputStream inputStream = new FileInputStream(path);
            byte[] bufferFullBinaryData  = new byte[(int) path.length()];
            inputStream.read(bufferFullBinaryData);
            inputStream.close();

            ArrayList<Integer> binaryDataList = new ArrayList<>();
            for(byte data : bufferFullBinaryData) {
                binaryDataList.add((int)data);
            }
            //binaryDataList

            if(binaryDataList.size() % 32 > 0) {
                int numberOfPads = 32 - (binaryDataList.size() % 32);
                for(int i = 0; i < numberOfPads; i++) {
                    binaryDataList.add(0xFF);
                }
            }
            ArrayList<Integer> fullPayLoadList = new ArrayList<>();
            ArrayList<Integer> chunks32Bytes = new ArrayList<>();

            while(binaryDataList.size() >= 32) {
                for(int i = 0; i < 32; i++) {
                    chunks32Bytes.add(binaryDataList.get(i));
                }
                byte[] chunks32ToCrc = CalculateCRC.converListtoByteArray(chunks32Bytes);
                byte[] chunks32CRC16BigEndian = BigEndianConverter.toBytesConverter2(calculator16.Calc(chunks32ToCrc,0,chunks32ToCrc.length));
                fullPayLoadList.addAll(firstFrameWithCRC8);
                byte[] indexArray = BigEndianConverter.toBytesConverter2(indexCounterLong);
                fullPayLoadList.add((int) indexArray[0]);
                fullPayLoadList.add((int) indexArray[1]);
                fullPayLoadList.addAll(chunks32Bytes);
                fullPayLoadList.add((int) chunks32CRC16BigEndian[0]);
                fullPayLoadList.add((int) chunks32CRC16BigEndian[1]);
                byte[] lastFrameCRC16 = CalculateCRC.converListtoByteArray(fullPayLoadList);
                byte[] buffer = BigEndianConverter.toBytesConverter2(calculator16.Calc(lastFrameCRC16,0,lastFrameCRC16.length));
                fullPayLoadList.add((int) buffer[0]);
                fullPayLoadList.add((int) buffer[1]);

                //System.out.print("SENDING " + indexCounterLong + " " + fullPayLoadList.size() + " bytes PAYLOAD: ");
                //System.out.println(fullPayLoadList);
                //System.out.println(binaryDataList.size());
                byte[] toTCP = CalculateCRC.converListtoByteArray(fullPayLoadList);
                out.write(toTCP);
                while (true) {
                    byte[] tcpBuffer = new byte[64];
                    in.read(tcpBuffer);
                    String validator_184 = BufferPrinter.printTCPValidator(tcpBuffer);
                    if(validator_184.equals("B8")) {
                        break;
                    }
                }
                binaryDataList.subList(0, 32).clear();
                chunks32Bytes.clear();
                fullPayLoadList.clear();
                indexCounterLong++;
            }
            inputStream.close();
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
