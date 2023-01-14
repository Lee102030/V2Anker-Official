import java.util.ArrayList;

public class Main {
    public static void main(String[] firmware) throws InterruptedException {
        System.out.println("Anker V2 firmware upgrade utility / v1.0");
        System.out.println();
        ArrayList<Thread> threadList = new ArrayList<>();
        ArrayList<String> listToRun = new ArrayList<>();
        ArrayList<String> fullList = new ArrayList<>();
        //String arg = "C:\\anker\\AnkerV1.1.3.bin";
        String stringBuffer = XMLToString.xmlParser("config.xml");
        //String stringBuffer = XMLToString.xmlParser("C:\\anker\\config.xml");
        stringBuffer = stringBuffer.replaceAll("\\s+", " ");
        String[] stringArrayBuffer = null;
        stringArrayBuffer = stringBuffer.split(" ");
        for(String s : stringArrayBuffer) {
            if(s.length() < 34) {
                continue;
            }
            fullList.add(s.substring(s.indexOf("<device_ip>") + 11 , s.indexOf("</device_ip>")));
        }
        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        while(fullList.size() > 0) {
             if(fullList.size() > numberOfProcessors ) {
                 listToRun.addAll(fullList.subList(0, numberOfProcessors));
                 System.out.println("LOADING FIRMWARE " + firmware[0] + " TO DEVICES:");
                 System.out.println(listToRun);
                 System.out.println();
                 for(String ip : listToRun) {
                     threadList.add(new ThreadDemo(ip, firmware[0]));
                 }
                 for(Thread th : threadList) {
                     th.start();
                 }
                 while(threadList.get(listToRun.size() - 1).isAlive()) {
                     System.out.print("#");
                     Thread.sleep(1000);
                 }
                 for(Thread th : threadList) {
                     th.join();
                 }
                 Thread.sleep(1000);
                 fullList.subList(0, numberOfProcessors).clear();
                 listToRun.clear();
             } else {
                 System.out.println("LOADING FIRMWARE " + firmware[0] + " TO DEVICES:");
                 listToRun.addAll(fullList);
                 System.out.println(listToRun);
                 System.out.println();
                 for(String ip : listToRun) {
                     threadList.add(new ThreadDemo(ip, firmware[0]));
                 }
                 for(Thread th : threadList) {
                     th.start();
                 }
                 while(threadList.get(listToRun.size() - 1).isAlive()) {
                     System.out.print("#");
                     Thread.sleep(1000);
                 }
                 for(Thread th : threadList) {
                     th.join();
                 }
                 Thread.sleep(1000);
                 fullList.clear();
             }
        }
    }
}
