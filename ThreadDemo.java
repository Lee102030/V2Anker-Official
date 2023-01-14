import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class ThreadDemo extends Thread {
    String ip;
    int port = 10001;
    String firmware;
    String reset = "2";
    String startBootloader = "B0";
    String flashBegin = "B3";
    String flashErase = "B5";
    String flashEnd = "B4";
    String jumpToApp = "B9";
    public ThreadDemo(String ip, String firmware) {
        this.ip = ip;
        this.firmware = firmware;
    }

    public void run() {
        boolean flag = false;
        Date startDate = new Date();
        byte[] verifyFrame = FrameBuilder.verifyFrame(this.firmware);

        try {
            Socket startSocket = new Socket(this.ip, this.port);
            if(startSocket.isConnected()) {
                flag = true;
            }

            OutputStream out = new DataOutputStream(startSocket.getOutputStream());
            InputStream in = new DataInputStream(startSocket.getInputStream());

            while(flag) {
                byte[] tcpBuffer = new byte[64];
                in.read(tcpBuffer);
                String frameValidator186_3 = BufferPrinter.printTCPValidator(tcpBuffer);
                if(frameValidator186_3.equals("B0")) {
                    byte[] toSendTCP = FrameBuilder.commandBuilder(this.startBootloader);
                    out.write(toSendTCP);
                    flag = false;
                } else if (frameValidator186_3.equals("2")){
                    byte[] toSendTCP = FrameBuilder.commandBuilder(this.reset);
                    out.write(toSendTCP);
                    flag = false;
                }
            }
            out.close();
            in.close();
            startSocket.close();
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }


        try {
            Thread.sleep(3000);
            Socket mainSocket = new Socket(this.ip, this.port);
            if(mainSocket.isConnected()) {
                flag = true;
            }
            OutputStream out = new DataOutputStream(mainSocket.getOutputStream());
            InputStream in = new DataInputStream(mainSocket.getInputStream());
            while(flag) {
                byte[] tcpBuffer = new byte[64];
                in.read(tcpBuffer);
                BufferPrinter.printTCP(tcpBuffer);
                byte[] toSendTCP = FrameBuilder.commandBuilder(this.flashBegin);
                out.write(toSendTCP);
                flag = false;
            }
            flag = true;
            while(flag) {
                byte[] tcpBuffer = new byte[64];
                in.read(tcpBuffer);
                String validator_184 = BufferPrinter.printTCPValidator(tcpBuffer);
                if(validator_184.equals("B8")) {
                    flag = false;
                }
            }
            flag = true;
            while(flag) {
                byte[] toSendTCP = FrameBuilder.commandBuilder(this.flashErase);
                out.write(toSendTCP);
                Thread.sleep(5000);
                flag = false;
            }
            flag = true;
            while(flag) {
                byte[] tcpBuffer = new byte[64];
                in.read(tcpBuffer);
                String validator_184 = BufferPrinter.printTCPValidator(tcpBuffer);
                if(validator_184.equals("B8")) {
                    flag = false;
                }
            }

            FrameBuilder.dataFrameSender(mainSocket, in ,out, this.firmware);
            flag = true;
            while(flag) {
                out.write(verifyFrame);

                byte[] tcpBuffer = new byte[64];
                in.read(tcpBuffer);
                String validator_184 = BufferPrinter.printTCPValidator(tcpBuffer);
                if(validator_184.equals("B8")) {
                    flag = false;
                }
            }
            flag = true;
            while(flag) {
                byte[] toSendTCP = FrameBuilder.commandBuilder(this.flashEnd);
                out.write(toSendTCP);
                byte[] tcpBuffer = new byte[64];
                in.read(tcpBuffer);
                String validator_184 = BufferPrinter.printTCPValidator(tcpBuffer);
                if(validator_184.equals("B8")) {
                    flag = false;
                }
            }
            flag = true;
            while(flag) {
                byte[] toSendTCP = FrameBuilder.commandBuilder(this.jumpToApp);
                out.write(toSendTCP);
                mainSocket.close();
                out.close();
                in.close();
                Thread.sleep(3000);
                flag = false;
            }

            Socket finalSocket = new Socket(this.ip, this.port);
            if(finalSocket.isConnected()) {
                System.out.println();
                System.out.print("[" + this.ip + "]" + ": OK / ");
            }
            InputStream finalIn = new DataInputStream(finalSocket.getInputStream());
            Date endDate = new Date();
            System.out.print("TIME ELAPSED: ");
            System.out.print((int)((endDate.getTime() - startDate.getTime()) / 1000) + " seconds");
            finalSocket.close();
            finalIn.close();
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
