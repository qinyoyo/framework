package top.iotequ.framework.service.utils;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;

public class UploadDownUtil {
    protected static String getFileNames(String generatorName, String entityName, String pkId, boolean multiple, boolean nullable) {
        File dir = Util.uploadFileDir(generatorName);
        String prefix = Util.uploadFilename(entityName, pkId,null) + "_";
        if (!dir.exists()) dir.mkdirs();
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });
        if (multiple) {
            if (files!=null && files.length>0) {
                String ff="";
                for (File f: files) {
                    ff += (ff.isEmpty()?"":",")+f.getName().substring(prefix.length());
                }
                return ff;
            } else return (nullable?null:"");
        } else {
            if (files!=null && files.length>0) {
                for (int i=1;i<files.length;i++) files[i].delete();
                return files[0].getName().substring(prefix.length());
            } else return (nullable?null:"");
        }
    }
    public static String uploadFile(String generatorName, String entityName, String pkId, boolean multiple, boolean nullable, HttpServletRequest request) throws Exception {
        File dir = Util.uploadFileDir(generatorName);
        String prefix = Util.uploadFilename(entityName, pkId,null)+"_";
        if (!dir.exists()) dir.mkdirs();
        Util.uploadFile(request, "filepart_"+entityName, dir, prefix, null,multiple);
        return getFileNames(generatorName,entityName, pkId, multiple,nullable);
    }
    public static String removeFilesExclusive(String generatorName,String entityName,String pkId,String fileNames,boolean nullable) {
        try {
            if (Util.isEmpty(generatorName) || Util.isEmpty(entityName) || Util.isEmpty(pkId)) return fileNames;
            String [] names = (fileNames==null ? new String[] {} : fileNames.split(","));
            File dir = Util.uploadFileDir(generatorName);
            String prefix = Util.uploadFilename(entityName, pkId,null);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return  name.startsWith(prefix);
                }
            });
            fileNames="";
            for (File f: files) {
                String name=f.getName().substring(prefix.length());
                if (name.startsWith("_")) name=name.substring(1);
                boolean matched=false;
                for (String n:names) {
                    if (n.equals(name)) {
                        matched=true;
                        break;
                    };
                }
                if (matched) {
                    fileNames += (fileNames.isEmpty()?"":",")+name;
                } else f.delete();
            }
            if (nullable && fileNames.isEmpty()) return null;
            else return fileNames;
        } catch (Exception e) {
            return fileNames;
        }
    }
    public static void removeFilesWithId(String generatorName,String pkId) {
        try {
            if (Util.isEmpty(generatorName) || Util.isEmpty(pkId)) return;
            File dir = Util.uploadFileDir(generatorName);
            String [] pkIdList = pkId.split(",");
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    for (String s: pkIdList) {
                        if (name.indexOf("_"+s+"_") > 0 ) return true;
                    }
                    return  false;
                }
            });
            for (File f: files) {
                f.delete();
            }
        } catch (Exception e) {  }
    }

    public static void downloadFile(String generatorName,String entityName,String pkId,String fileName, HttpServletResponse response) throws IotequException {
        if (Util.isEmpty(entityName) || Util.isEmpty(pkId)) throw new IotequException(IotequThrowable.NULL_OBJECT,"参数不全");
        File dir = Util.uploadFileDir(generatorName);
        String prefix = Util.uploadFilename(entityName, pkId,null) +"_";
        Util.downloadFile(new File(dir,prefix+fileName), response,true);
    }
}
