package top.iotequ.framework.controller;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.iotequ.framework.util.RestJson;
import top.iotequ.framework.util.SqlUtil;
import top.iotequ.framework.util.Util;
@Controller
public class UploadController {
	private static final Logger log = LoggerFactory.getLogger(UploadController.class);
	@Value("${spring.upload-path}")
	String uploadPath;
	@ResponseBody
	@RequestMapping(value = "/uploadImage")
	public String importFile(String id,String type, String path, String field,String name,MultipartFile file,HttpServletRequest request) throws Exception{
		RestJson j=new RestJson();
		if (Util.isEmpty(file) || Util.isEmpty(id)) {
			j.setSuccess(false);
		}  else	try {
			String ext=null;
			if (Util.isEmpty(type)) {
				String oriN=file.getOriginalFilename();
				int p=oriN.lastIndexOf(".");
				ext=(p>=0 && p>=oriN.length()-5) ? oriN.substring(p).toLowerCase():null;
			} else {
				type=type.toLowerCase();
				if (type.contains("png")) ext=".png";
				else if (type.contains("gif")) ext=".gif";
				else if (type.contains("jpg")) ext=".jpg";
				else if (type.contains("jpeg")) ext=".jpeg";				
			}
			if (ext==null || (!ext.equals(".png") && !ext.equals(".gif")&& !ext.equals(".jpg") && !ext.equals(".jpeg"))) throw new Exception("No suport file type");		
			if (Util.isEmpty(name)) name="0"+ext;
			else	name=name.split("\\.")[0]+ext;
			String dest = Util.uploadFilename(field, id, name);
			
			File dir = Util.uploadFileDir(path);
			File outf=new File(dir,dest);
			log.debug("upload image {}",outf.getAbsolutePath());
			if (!dir.exists()) dir.mkdirs();
			if (outf.exists()) {
				outf.delete();
			} 
			file.transferTo(outf);
			if ("sysUser".equals(path)) {
				String sql="update sys_user set icon=? where id=?";
				SqlUtil.sqlExecute(sql, name,id);
			}
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMessage(e);
		}
		return j.toString();
	}

	private String getImageType(String name) {
		if (Util.isEmpty(name)) return null;
		String [] ss = name.split("\\.");
		if (ss.length==1) return "";
		else return ss[ss.length-1].toLowerCase();
	}

	private InputStream getUploadImagesByName(File path, String id, String field, String name) {
		InputStream image=null;
		if (!Util.isEmpty(name)) {
			File imgFile=new File(path,id==null?name:Util.uploadFilename(field, id,name));
			try {
				image=new FileInputStream(imgFile);
			} catch (FileNotFoundException e) {
				image=null;
			}
		}
		return image;
	}
	@RequestMapping("/res/getUploadImage")
	public void getUploadImages(String id,String def,String path,String field,String name,final HttpServletRequest request, final HttpServletResponse response){
		String type=null;
		InputStream image=null;
		File absPath = Util.uploadFileDir(path);
		if (!Util.isEmpty(name)) {
			image = getUploadImagesByName(absPath, id, field, name);
			if (image!=null) type = getImageType(name);
		}
		else {
			String fn = Util.uploadFilename(field, id,null)+"_";
			File[] files = absPath.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return  name.startsWith(fn);
				}
			});
			if (files!=null && files.length>0) {
				try {
					image=new FileInputStream(files[0]);
					type = getImageType(files[0].getName());
				} catch (FileNotFoundException e) {
					image=null;
				}
			}
		}
		if (image==null && !Util.isEmpty(def) && !"null".equals(def)) {
			image = getUploadImagesByName(absPath, null, null, def);
			if (image!=null) type = getImageType(def);
		}
		if (image==null && !"null".equals(def)) {
			image = getUploadImagesByName(absPath, null, null, "default.png");
			type="png";
		}
		if (image==null && !"null".equals(def)) {
			image = getUploadImagesByName(absPath, null, null, "default.jpg");
			type="jpg";
		}
		if (image!=null && type!=null) {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/"+type);
			try {
				BufferedImage imageData = ImageIO.read(image) ;
				ImageIO.write(imageData, type.toUpperCase(), response.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				image.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
}