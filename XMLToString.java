import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public final class XMLToString {
    public static String xmlParser(String s) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(s)));
            String strLine;
            while( (strLine = reader.readLine()) != null ) {
                sb.append(strLine);
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        } finally {

            try {

                if( reader != null )
                    reader.close();

            } catch(Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }
        return sb.toString();
    }

}
