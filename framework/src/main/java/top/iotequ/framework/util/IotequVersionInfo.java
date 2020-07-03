package top.iotequ.framework.util;

import lombok.Getter;
import org.springframework.boot.system.ApplicationHome;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.*;

@Getter
public class IotequVersionInfo {
    private static List<IotequVersionInfo> IotequVersionInfoList = null;
    private String groupId;
    private String module;
    private String version;
    private Date buildTime;
    private boolean isIotequModule=false;
    static public void readVersionInfo(@NotNull Class<?> clazz) {
        ApplicationHome home = new ApplicationHome(clazz);
        String path = home.getSource().getAbsolutePath();
        if (path.endsWith("\\target\\classes") || path.endsWith("/target/classes")) {
            File hd = new File(path).getParentFile();
            File[] jarFiels = hd.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isDirectory()) {
                        return false;
                    }
                    if (file.getName().toLowerCase().endsWith(".jar")) {
                        return true;
                    }
                    return false;
                }
            });
            if (jarFiels.length>0) path = jarFiels[0].getAbsolutePath();
            else return;
        }
        System.out.println("jar path = "+path);
        try {
            JarFile jarfile =  new JarFile(path);
            Manifest manifest = jarfile.getManifest();
            if (manifest==null) return;
            IotequVersionInfoList = new ArrayList<>();
            IotequVersionInfo md = new IotequVersionInfo(manifest);
            if (md.isIotequModule) IotequVersionInfoList.add(md);
            for (Enumeration<JarEntry> e = jarfile .entries(); e.hasMoreElements(); ) {
                JarEntry entry = e.nextElement();
                String name = entry.getName();
                if (name.endsWith(".jar") && name.startsWith("BOOT-INF/lib/")) {
                    JarInputStream ji = new JarInputStream(jarfile.getInputStream(entry));
                    IotequVersionInfo sub = new IotequVersionInfo(ji.getManifest());
                    if (sub.isIotequModule) IotequVersionInfoList.add(sub);
                }
            }
        } catch (Exception e) {
            return;
        }
    }
    public static IotequVersionInfo getVersion(@NotNull String module) {
        if (!Util.isEmpty(IotequVersionInfoList)) {
            for (IotequVersionInfo md : IotequVersionInfoList) {
                if (module.equals(md.module)) return md;
            }
        }
        return null;
    }
    public static List<IotequVersionInfo> getAllVersions() {
        return IotequVersionInfoList;
    }
    public static String getAllVersionJsonInfo() {
        if (!Util.isEmpty(IotequVersionInfoList)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"modules\":[");
            for (int i = 0 ; i<IotequVersionInfoList.size() ; i++) {
                IotequVersionInfo md = IotequVersionInfoList.get(i);
                if (i!=0) sb.append(",");
                sb.append(md.toJsonString());
            }
            sb.append("]}");
            return sb.toString();
        }
        return null;
    }
    /*
    <Iotequ-Module>true</Iotequ-Module>
    <Group-Id>${project.groupId}</Group-Id>
    <Artifact-Id>${project.artifactId}</Artifact-Id>
    <Version>${project.version}</Version>
    <Build-Time>${maven.build.timestamp}</Build-Time>
    */
    public IotequVersionInfo(Manifest manifest) {
        if (Objects.nonNull(manifest)) {
            Attributes attrs = manifest.getMainAttributes();
            if (attrs==null) return;
            isIotequModule = Util.boolValue(attrs.getValue("Iotequ-Module"));
            if (!isIotequModule) return;
            groupId = attrs.getValue("Group-Id");
            module = attrs.getValue("Artifact-Id");
            version = attrs.getValue("Version");
            buildTime = DateUtil.string2Date(attrs.getValue("Build-Time"));
            if (buildTime==null) buildTime=new Date();
            else buildTime.setTime(buildTime.getTime()+8L*3600000L);
        }
    }
    public String toJsonString() {
        if (isIotequModule) {
            return "{\"groupId\":\""  + groupId + "\","
                    + "\"module\":\""  + module + "\","
                    + "\"version\":\""  + version + "\","
                    + "\"buildTime\":\""  + DateUtil.date2String(buildTime, null) + "\"}";
        } else return null;
    }
    @Override
    public String toString() {
        if (isIotequModule) {
            return groupId + "." + module + " : " + version + " (" + DateUtil.date2String(buildTime, null) + ")";
        } else return null;
    }
}
