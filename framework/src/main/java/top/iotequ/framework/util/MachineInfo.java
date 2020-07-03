package top.iotequ.framework.util;

import top.iotequ.framework.exception.IotequException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MachineInfo {
	public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }
    private static String getMainBordId_windows() throws Exception {
        String result = "";
        File file = File.createTempFile("realhowto", ".vbs");
        file.deleteOnExit();
        FileWriter fw = new java.io.FileWriter(file);

        String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                + "Set colItems = objWMIService.ExecQuery _ \n"
                + "   (\"SELECT * FROM Win32_ComputerSystemProduct\") \n"
                + "For Each objItem in colItems \n"
                + "    Wscript.Echo objItem.UUID \n"
                + "    exit for  ' do the first cpu only! \n" + "Next \n";

        fw.write(vbs);
        fw.close();
        Process p = Runtime.getRuntime().exec(
                "cscript //NoLogo " + file.getPath());
        BufferedReader input = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            result += line;
        }
        input.close();
        return result;
    }
    private static String getMainBordId_linux() throws IotequException {
	    try {
            String result = "";
            String maniBord_cmd = "cat /sys/class/dmi/id/product_uuid 2>&1"; // ""echo 67378826|sudo -S dmidecode system | grep -i uuid | sed 's/^\\s*uuid\\s*:*\\s*//i' 2>&1";
            Process p;
            p = Runtime.getRuntime().exec(
                    new String[]{"sh", "-c", maniBord_cmd});// 管道
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
                break;
            }
            br.close();
            return result;
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
    private static String getMAC_linux() throws IotequException {
	    try {
            String mac = null;
            BufferedReader bufferedReader = null;
            Process process = null;
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index = line.toLowerCase().indexOf("hwaddr");
                if (index >= 0) {
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
            bufferedReader.close();
            return mac;
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
    private static String getMAC_windows() throws IotequException {
	    try {
            InetAddress ip = null;
            NetworkInterface ni = null;
            List<String> macList = new ArrayList<String>();
            Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
                    .getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = (InetAddress) ips.nextElement();
                    if (!ip.isLoopbackAddress() // 非127.0.0.1
                            && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                        macList.add(getMacFromBytes(ni.getHardwareAddress()));
                    }
                }
            }
            if (macList.size() > 0) {
                return macList.get(0);
            } else {
                return "";
            }
        } catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }

    private static String getMacFromBytes(byte[] bytes) {
        StringBuffer mac = new StringBuffer();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toUpperCase();
    }
    
    public static String getMainBordId() throws IotequException {
	    try {
            String os = getOSName();
            String mainBordId = "";
            if (os.startsWith("windows")) {
                mainBordId = getMainBordId_windows();
            } else if (os.startsWith("linux")) {
                mainBordId = getMainBordId_linux();
            }
            return mainBordId;
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
    public static String getMAC() throws IotequException {
	    try {
            String os = getOSName();
            String mac = "";
            if (os.startsWith("windows")) {
                mac = getMAC_windows();
            } else if (os.startsWith("linux")) {
                mac = getMAC_linux();
            }
            return mac;
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
    private static byte[] hexToByte(String hex,int maxlen){
    	hex=hex.replace("-", "").replaceAll(" ", "");
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; 
        byte[] ret = new byte[Math.min(byteLen,maxlen)];
        for (int i = 0; i < ret.length; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }
    private static String  byte2hex(byte[] s, int byteLen, int spLen) {
    	char [] tab = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    	StringBuilder sb=new StringBuilder();
    	if (byteLen>s.length)  byteLen = s.length;
    	for (int i = 0; i < byteLen; i++) {
    		sb.append(tab[(s[i] & 0xf0) >> 4]);
    		sb.append(tab[s[i] & 0xf]);
    		if (spLen>0 && i < byteLen - 1 && i%spLen == spLen - 1) sb.append('-');
    	}
    	return sb.toString();
    }
    private static int unsigned(byte b) {
		int u = (int)b;
		u=u&0xff;
		return u;
    }
    private static void  shiftcode(byte[] vv, boolean encode) {
    	byte [] tt=new byte[16];
    	byte bt = (byte) (unsigned(vv[15]) % 15);
    	for (int i = 0; i < 15; i++) {
    		if (encode)	tt[i] = vv[(i + bt) % 15];
    		else        tt[i] = vv[(i + 15 - bt) % 15];
    	}
    	for (int i = 0; i < 15; i++)  vv[i] = tt[i];
    }
    static void AES_Decrypt(byte[] sn,byte[] sc) throws IotequException {
    	try {
            SecretKeySpec skeySpec = new SecretKeySpec(sc, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] result = cipher.doFinal(sn);
            for (int i = 0; i < 16; i++) sn[i] = result[i];
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
    public static int getLicence(String serialNumber) throws IotequException {
	    try {
            String setupCode = getSetupCode();
            byte[] sn = hexToByte(serialNumber, 16),
                    sc = hexToByte(setupCode, 16),
                    vv = new byte[16];
            AES_Decrypt(sn, sc);

            for (int i = 0; i < 16; i++)
                vv[i] = (byte) (((unsigned(sn[i]) >> 3) | (unsigned(sn[i]) << 5)) ^ unsigned(sc[i]));
            shiftcode(vv, false);
            byte[] p = {(byte) 0x87, 5, 8, 7};
            for (int i = 0; i < 4; i++) {
                vv[12 + i] = (byte) ~(vv[12 + i] ^ p[i]);
            }
            if (unsigned(vv[0]) != 0x8a || vv[1] != 0x30 || unsigned(vv[2]) != 0xd3 || vv[3] != 0x04) return 0;
            int[] cp = new int[4];
            for (int i = 0; i < 4; i++) cp[3 - i] = unsigned(vv[12 + i]);
            int licence = (cp[3] << 24) + (cp[2] << 16) + (cp[1] << 8) + cp[0];
            return licence;
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
    public static int getLicence(String serialNumber,String module) throws IotequException {
	    try {
            if (module == null || module.trim().isEmpty()) return getLicence(serialNumber);
            byte[] bb = module.getBytes();
            byte[] sn = hexToByte(serialNumber, 16);
            for (int i = 0; i < 16; i++) sn[i] = (byte) (sn[i] ^ bb[i % bb.length]);
            serialNumber = byte2hex(sn, 16, 2);
            return getLicence(serialNumber);
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
    public static String getSetupCode() throws IotequException {
	    try {
            String sn = getMainBordId();
            byte[] uuid = hexToByte(sn, 16);
            byte[] key = "AoTang1996-08-02".getBytes();
            int xk = 0;
            for (int i = 0; i < 16; i++) xk = xk + unsigned(uuid[i]);
            for (int i = 0; i < 16; i++) uuid[i] = (byte) (uuid[i] ^ key[(xk + i) % 16]);
            sn = byte2hex(uuid, 16, 2);
            return sn;
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
}
