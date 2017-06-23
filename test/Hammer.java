import jcifs.Config;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import jcifs.util.LogStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;

public class Hammer {
    private static final String baseUrl = "smb://MBOX:MBOX@nas.zartras.com/";
    private static final String folder = "MBOX/";
    private static final String baseUrlAndFolder = baseUrl + folder;

    public static void main(String argv[]) {
//       LogStream.setLevel(3);
        jcifs.Config.setProperty("jcifs.resolveOrder", "DNS");
        jcifs.Config.setProperty("jcifs.smb.client.connTimeout", "30000");
        jcifs.Config.setProperty("jcifs.smb.client.dfs.disabled", "true");
        jcifs.Config.setProperty("jcifs.smb.client.soTimeout", "30000");
        jcifs.Config.setProperty("jcifs.util.loglevel", "3");

        for (int i = 1; i <= 100000; i++) {
            System.out.println("====== START ======: " + i);
            try {
                connectAndTest();
                connectAndPut(i);
                connectAndDelete(i);
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
//         System.out.println("====== READY ======");
//         hitEnter();
        }
    }

    private static void connectAndTest() throws IOException {
        SmbFile f = new SmbFile(baseUrl);
        f.forceNewConnectionOnNewSmbTransport();
        if (!f.exists()) {
            throw new IOException("Share not found.");
        }
        System.out.println("TCP Port: " + f.getPortOnlyValidAfterAnAction());
    }

    private static void connectAndPut(int i) throws IOException {
        SmbFile f = new SmbFile(String.format("%s%s%06d%s", baseUrlAndFolder, "jcifs", i, ".txt"));
        SmbFileOutputStream out = new SmbFileOutputStream(f);
        String content = "Hello World!";
        out.write(content.getBytes());
        out.close();
    }

    private static void connectAndDelete(int i) throws IOException {
        if (i <= 10) {
            return;
        }
        SmbFile f = new SmbFile(String.format("%s%s%06d%s", baseUrlAndFolder, "jcifs", i - 10, ".txt"));
        f.delete();
    }

    private static void hitEnter() {
        System.out.print("Hit [Enter] to continue.");
        try {
            System.in.read();
        } catch (IOException e) {
            // Ignore
        }
        System.out.println(" done.");
    }
}
