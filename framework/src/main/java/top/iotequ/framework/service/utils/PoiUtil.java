package top.iotequ.framework.service.utils;

import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import top.iotequ.framework.service.IGetPagedData;
import top.iotequ.framework.service.IImportPagedData;
import top.iotequ.framework.util.CgFieldAnnotation;
import top.iotequ.framework.util.DateUtil;
import top.iotequ.framework.util.EntityUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;

public class PoiUtil {
    private static final int EXCEL_PAGE_SIZE = 200;
    private static void exportSheet(HttpServletRequest request, SXSSFWorkbook wb, String sheetName, List<?> rows, int progress0, int progress1) throws Exception {
        if (rows==null || rows.size()==0) {
            Util.setProgress(progress1);
            return;
        }
        Util.setProgress(progress0);
        Class<?> clazz=rows.get(0).getClass();
        Field[] ff = clazz.getDeclaredFields();
        List<String> titles = new ArrayList<String>();   //getExcelTitle();
        List<String> formats = new ArrayList<String>();   //getExcelFormat();
        List<String> entitys = new ArrayList<String>();   //getExcelFormat();
        List<Boolean> hiddens=new ArrayList<Boolean>();
        String showEntities = request.getParameter("show_entities");
        if (Objects.isNull(showEntities)) showEntities="";
        else showEntities=","+showEntities;
        for (Field f: ff) {
            if (f.isAnnotationPresent(CgFieldAnnotation.class)) {
                CgFieldAnnotation an=f.getAnnotation(CgFieldAnnotation.class);
                if (an!=null) {
                    entitys.add(f.getName());
                    String ti = request.getParameter("title_"+f.getName());
                    titles.add(Util.isEmpty(ti)?f.getName():ti);
                    formats.add(an.format());
                    hiddens.add(!showEntities.contains(","+f.getName()));
                }
            }
        }
        int rowIndex = 0;

        int totalEntity = entitys.size();
        int [] positions = new int[totalEntity];
        int totalDicts=0;
        for (int i=0;i<entitys.size();i++) {
            positions[i]=i+totalDicts;
            Object o=request.getAttribute("dict_"+entitys.get(i));
            if (o!=null)  totalDicts++;
        }
        SXSSFSheet sheet = wb.getSheet(sheetName);
        if (sheet==null) {  // 新建sheet
            sheet=wb.createSheet(sheetName);
            Font titleFont = wb.createFont();
//			titleFont.setFontName("simsun");
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.BLACK.index);
            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFont(titleFont);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);
            titleStyle.setBorderBottom(BorderStyle.THIN);
            SXSSFRow titleRow = sheet.createRow(rowIndex++);
            SXSSFDrawing draw=sheet.createDrawingPatriarch();
            for (int i = 0; i < totalEntity; i++) {
                String title = (i < titles.size() ? titles.get(i) : "");
                int pos=positions[i];
                Comment ct=draw.createCellComment(new XSSFClientAnchor(0,0,0,0,pos,0,pos+2,2));
                Cell cell = titleRow.createCell(pos);
                cell.setCellValue(title);
                ct.setString(new XSSFRichTextString(entitys.get(i)+"\n!!for import"));
                cell.setCellComment(ct);
                cell.setCellStyle(titleStyle);
                Object o=request.getAttribute("dict_"+entitys.get(i));
                if (hiddens.get(i) || o!=null) {
                    sheet.setColumnHidden(pos, true);
                }
                if (o!=null) {// 使用字典
                    Cell cellD = titleRow.createCell(pos+1);
                    Comment dct=draw.createCellComment(new XSSFClientAnchor(0,0,0,0,pos+1,0,pos+3,2));
                    cellD.setCellValue(title);
                    cellD.setCellStyle(titleStyle);
                    dct.setString(new XSSFRichTextString("dict_"+entitys.get(i)+"\n!!for import"));
                    cellD.setCellComment(dct);
                    if (hiddens.get(i)) {
                        sheet.setColumnHidden(pos, true);
                    }
                }
                CellStyle dataStyle = wb.createCellStyle();
                dataStyle.setAlignment(HorizontalAlignment.LEFT);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);
                dataStyle.setBorderBottom(BorderStyle.THIN);
                DataFormat format = wb.createDataFormat();
                if (i < formats.size() && !Util.isEmpty(formats.get(i))) {
                    dataStyle.setDataFormat(format.getFormat(formats.get(i)));
                }
                String colName = entitys.get(i);
                if (!Util.isEmpty(colName)) {
                    PropertyDescriptor pd = new PropertyDescriptor(colName, clazz);
                    Class<?> ft=pd.getPropertyType();
                    if (ft.equals(Boolean.class) || ft.equals(Date.class))
                        dataStyle.setAlignment(HorizontalAlignment.CENTER);
                    else if (ft.equals(Integer.class) || ft.equals(Short.class) || ft.equals(Byte.class) || ft.equals(Long.class) || ft.equals(Double.class) || ft.equals(Float.class))
                        dataStyle.setAlignment(HorizontalAlignment.RIGHT);
                    else dataStyle.setAlignment(HorizontalAlignment.LEFT);
                }
                sheet.setDefaultColumnStyle(pos, dataStyle);
                if (request.getAttribute("dict_"+entitys.get(i))!=null) sheet.setDefaultColumnStyle(positions[i]+1, dataStyle);
            }
        } else rowIndex=sheet.getLastRowNum()+1;
        for (int rowNum=0;rowNum< rows.size();rowNum++) {
            Object rowData=rows.get(rowNum);
            SXSSFRow dataRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < totalEntity; i++) {
                int pos=positions[i];
                Cell cell = dataRow.createCell(pos);
                cell.setCellStyle(sheet.getColumnStyle(pos));
                Cell cellD=null;
                Object dict=request.getAttribute("dict_"+entitys.get(i));
                if (dict!=null) {
                    cellD = dataRow.createCell(pos+1);
                    cellD.setCellStyle(sheet.getColumnStyle(pos+1));
                }
                String colName = entitys.get(i);
                if (!Util.isEmpty(colName)) {
                    PropertyDescriptor pd = new PropertyDescriptor(colName, clazz);
                    Method rM = pd.getReadMethod();
                    Object o = rM.invoke(rowData);
                    if (o != null) {
                        if (o instanceof Boolean) {
                            cell.setCellValue((Boolean) o ? "1":"0");
                        }
                        else if (o instanceof Date) {
                            cell.setCellValue((Date) o);
                        }
                        else if (o instanceof Double) {
                            cell.setCellValue((Double) o);
                        }
                        else if (o instanceof Float) {
                            cell.setCellValue((Float) o);
                        }
                        else if (o instanceof Integer) {
                            cell.setCellValue((Integer) o);
                        }
                        else if (o instanceof Long) {
                            cell.setCellValue((Long) o);
                        }
                        else if (o instanceof Short) {
                            cell.setCellValue((Short) o);
                        }
                        else if (o instanceof Byte) {
                            cell.setCellValue((Byte) o);
                        }
                        else
                            cell.setCellValue(o.toString());
                    }
                    if (cellD!=null && o!=null) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> list = (List<Map<String, Object>>) dict;
                        String text = DictionaryUtil.getTextsFromDict( list ,o.toString()) ;
                        cellD.setCellValue(text);
                    }
                }
            }
            Util.setProgress(progress0+rowNum*(progress1-progress0)/rows.size());
        }
        sheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < totalEntity + totalDicts; i++) {
            sheet.autoSizeColumn(i, true);
        }
        Util.setProgress(progress1);
    }

    private static Object getCellValue(Class<?> clazz, XSSFCell cell) {
        CellType ct=cell.getCellType();
        if (ct==CellType._NONE || ct==CellType.BLANK || ct==CellType.ERROR || ct==CellType.FORMULA) return null;
        if (ct==CellType.NUMERIC) {
            double v=cell.getNumericCellValue();
            if (clazz.equals(Integer.class)) return (int)v;
            else if (clazz.equals(Long.class)) return (long)v;
            else if (clazz.equals(Short.class)) return (short)v;
            else if (clazz.equals(Byte.class)) return (byte)v;
            else if (clazz.equals(Float.class)) return (float)v;
            else if (clazz.equals(Boolean.class)) return ((int)v) != 0;
            else if (clazz.equals(Date.class)) return HSSFDateUtil.getJavaDate(v);
            else if (clazz.equals(String.class)) return String.valueOf(v);
        } else if (ct==CellType.STRING) {
            String v=cell.getStringCellValue();
            if (clazz.equals(String.class)) return v;
            if (v==null || v.trim().isEmpty()) return null;
            v=v.trim();
            try {
                if (clazz.equals(Integer.class)) return Integer.parseInt(v);
                else if (clazz.equals(Long.class)) return Long.parseLong(v);
                else if (clazz.equals(Short.class)) return Short.parseShort(v);
                else if (clazz.equals(Byte.class)) return Byte.parseByte(v);
                else if (clazz.equals(Float.class)) return Float.parseFloat(v);
                else if (clazz.equals(Boolean.class)) return Util.boolValue(v);
                else if (clazz.equals(Date.class)) return DateUtil.string2Date(v);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static <C> String importExcel(XSSFWorkbook wb, HttpServletRequest request, String sheetName , Class<?> clazz, List<Integer> errorLines, IImportPagedData<C> ipd, int progress0, int progress1) throws Exception {
        XSSFSheet xssfSheet = wb.getSheet(sheetName);
        if (xssfSheet == null) {
            return "没有找到表单 "+sheetName;
        }
//		Class <C> clazz = (Class<C>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        StringBuilder sb=new StringBuilder();
        int totalRows = xssfSheet.getLastRowNum() + 1;
        List<C> list = new ArrayList<C>();
        XSSFRow titleRow = xssfSheet.getRow(0);
        if (titleRow==null) { return null;}
        int totalCells = titleRow.getLastCellNum() + 1;
        if (totalCells<=0) { return null;}
        String[] columns = new String[totalCells];
        int totalColumns = 0;
        Method[] getMethod=new Method[totalCells],setMethod=new Method[totalCells];
        Util.setProgress(progress0);
        for (int i=0;i<totalCells;i++) {
            if (titleRow.getCell(i)!=null) {
                Comment ct=titleRow.getCell(i).getCellComment();
                if (ct!=null && ct.getString()!=null) {
                    String s=ct.getString().getString();
                    if (!Util.isEmpty(s)) {
                        columns[i]=s.split("\n")[0].trim();
                        String colName=null;
                        if (columns[i].startsWith("dict_")) 		colName=columns[i].substring(5);
                        else colName=columns[i];
                        try {
                            PropertyDescriptor pd = new PropertyDescriptor(colName, clazz);
                            getMethod[i] = pd.getReadMethod();
                            setMethod[i] = pd.getWriteMethod();
                        } catch (Exception e) {
                            getMethod[i] = null;
                            setMethod[i] = null;
                        }
                        totalColumns++;
                    } else {
                        columns[i]=null;
                        getMethod[i] = null;
                        setMethod[i] = null;
                    }
                }
            }
        }
        if (totalColumns==0) {
            return null;
        }
        int rowNumStart = 1;
        int proccesed=0;
        StringBuilder errorLinesMsg=new StringBuilder();
        int PageNum = (totalRows + EXCEL_PAGE_SIZE -1) / EXCEL_PAGE_SIZE;
        double pageStep = ((double)(progress1 - progress0)) / (PageNum==0?1:PageNum);
        int pageProgress0=progress0;
        for (int rowNum = 1; rowNum < totalRows; rowNum++) {
            try {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow==null) {
                    if (errorLines!=null) errorLines.add(rowNum);
                    else
                        errorLinesMsg.append(rowNum).append(" ");
                    continue;
                }
                else {
                    @SuppressWarnings("unchecked")
                    C obj = (C) clazz.newInstance();
                    for (int c = 0; c < totalCells; c++) {
                        if (columns[c]==null) continue;
                        XSSFCell cell = xssfRow.getCell(c);
                        if (cell==null) continue;
                        CellType ct = cell.getCellType();
                        if (cell != null && ct != CellType.BLANK && ct != CellType.FORMULA && setMethod[c]!=null) {
                            if (columns[c].startsWith("dict_") && getMethod[c]!=null ) {
                                Object v=getMethod[c].invoke(obj);
                                if (!Util.isEmpty(v)) continue;
                                String text = cell.getStringCellValue();
                                if (text==null) continue;
                                text=text.trim();
                                if (text.isEmpty()) continue;
                                Object od=request.getAttribute(columns[c]);
                                if (od==null) continue;
                                v=null;
                                @SuppressWarnings("unchecked")
                                List<Map<String, Object>> dict = (List<Map<String, Object>>) od;
                                String vs = DictionaryUtil.getValuesFromDict( dict ,text) ;
                                if (Util.isEmpty(vs)) continue;
                                String typ = setMethod[c].getParameterTypes()[0].getName();
                                if (typ.endsWith("String"))
                                    setMethod[c].invoke(obj, vs);
                                else if (typ.endsWith("Integer"))
                                    setMethod[c].invoke(obj, Integer.parseInt(vs));
                                else if (typ.endsWith("Short"))
                                    setMethod[c].invoke(obj,  Short.parseShort(vs));
                                else if (typ.endsWith("Long"))
                                    setMethod[c].invoke(obj,  Long.parseLong(vs));
                                else if (typ.endsWith("Byte"))
                                    setMethod[c].invoke(obj, Byte.parseByte(vs));
                                else if (typ.endsWith("Boolean"))
                                    setMethod[c].invoke(obj, Util.boolValue(vs));
                            } else {
                                String typ = setMethod[c].getParameterTypes()[0].getName();
                                Object v=null;
                                if (typ.endsWith("Date"))
                                    v=getCellValue(Date.class,cell);
                                else if (typ.endsWith("Integer"))
                                    v=getCellValue(Integer.class,cell);
                                else if (typ.endsWith("Long"))
                                    v=getCellValue(Long.class,cell);
                                else if (typ.endsWith("Short"))
                                    v=getCellValue(Short.class,cell);
                                else if (typ.endsWith("Byte"))
                                    v=getCellValue(Byte.class,cell);
                                else if (typ.endsWith("Float"))
                                    v=getCellValue(Float.class,cell);
                                else if (typ.endsWith("Boolean"))
                                    v=getCellValue(Boolean.class,cell);
                                else if (typ.endsWith("String"))
                                    v=getCellValue(String.class,cell);
                                if (v!=null) setMethod[c].invoke(obj, v);
                            }
                        }
                    }
                    if (EntityUtil.isEntityEmpty(obj)) {
                        if (errorLines!=null) errorLines.add(rowNum);
                        else
                            errorLinesMsg.append(rowNum).append(" ");
                    } else {
                        list.add(obj);
                        if (rowNum % EXCEL_PAGE_SIZE == 0)  {
                            try {
                                String msg=ipd.saveData(list,pageProgress0,(int)(pageProgress0+pageStep));
                                pageProgress0 = (int)(pageProgress0+pageStep);
                                if (!Util.isEmpty(msg))  sb.append(msg).append("\n");
                                if (errorLinesMsg.length()>0) {
                                    sb.append("ignore lines:").append(errorLinesMsg).append("\n");
                                }
                                proccesed += list.size();
                            } catch (Exception e) {
                                sb.append(String.format("error lines(%d - %d):%s\n", rowNumStart,rowNum,e.getMessage()));
                            }
                            list.clear();
                            errorLinesMsg=new StringBuilder();
                            rowNumStart = rowNum + 1;
                        }
                    }
                }
            } catch(Exception e) {
                if (errorLines!=null) errorLines.add(rowNum);
                else
                    errorLinesMsg.append(rowNum).append(" ");
            }
        }
        if (list.size()>0) {
            try {
                String msg=ipd.saveData(list,pageProgress0,progress1);
                if (!Util.isEmpty(msg))  sb.append(msg).append("\n");
                if (errorLinesMsg.length()>0) {
                    sb.append("ignore lines:").append(errorLinesMsg).append("\n");
                }
                proccesed += list.size();
            } catch (Exception e) {
                sb.append(String.format("error lines(%d - %d):%s\n", rowNumStart,totalRows-1,e.getMessage()));
            }
        }
        Util.setProgress(progress1);
        return String.format("成功导入 %d 行\n%s", proccesed,sb.toString());
    }


    /**
     * 导出单表
     * @param <C> 实体类
     * @param request request
     * @param response response
     * @param fileName 文件名
     * @param sheetName sheet名，null="Sheet1"
     * @param pd 导出操作代理
     * @throws Exception 错误返回异常
     */
    public static <C> void exportExcel(HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName, IGetPagedData<C> pd) throws Exception {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        OutputStream out = response.getOutputStream();
        XSSFWorkbook wb0 = new XSSFWorkbook();
        SXSSFWorkbook wb = new SXSSFWorkbook(wb0, 100);
        try {
            PageInfo<C> pageInfo=pd.getPage(1,EXCEL_PAGE_SIZE);
            int pages=pageInfo.getPages();
            for (int p=0;p<pages;p++) {
                List<C> rows = pageInfo.getList();
                exportSheet(request,wb,sheetName==null?"Sheet1":sheetName, rows,p*99/pages,(p+1)*99/pages) ;
                pageInfo=pd.getPage(p+2,EXCEL_PAGE_SIZE);
            }
            wb.write(out);
        } finally {
            wb.close();
            Util.setProgress(100);
        }
    }

    public static <C> SXSSFWorkbook exportExcel(SXSSFWorkbook wb,int startProgress, int maxProgress,HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName, IGetPagedData<C> pd) throws Exception {
        OutputStream out = response.getOutputStream();
        if (wb==null) {
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            XSSFWorkbook wb0 = new XSSFWorkbook();
            wb = new SXSSFWorkbook(wb0, 100);
        }
        try {
            PageInfo<C> pageInfo=pd.getPage(1,EXCEL_PAGE_SIZE);
            int pages=pageInfo.getPages();
            int step = (maxProgress - startProgress) / pages;
            for (int p=0;p<pages;p++) {
                List<C> rows = pageInfo.getList();
                exportSheet(request,wb,sheetName==null?"Sheet1":sheetName, rows,startProgress + p*step,startProgress + (p+1)*step) ;
                pageInfo=pd.getPage(p+2,EXCEL_PAGE_SIZE);
            }
            wb.write(out);
        } finally {
            Util.setProgress(maxProgress);
        }
        return wb;
    }
    /**
     * 导出多张表数据
     * @param request	request
     * @param response  response
     * @param fileName	文件名
     * @param sheetNames sheet名称列表，必须输入值，不能为空
     * @param pds	导出代理列表，必须与sheetNames数组大小一致
     * @throws Exception 错误返回异常
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, String fileName, String[] sheetNames, IGetPagedData<?>[] pds) throws Exception {
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        OutputStream out = response.getOutputStream();
        XSSFWorkbook wb0 = new XSSFWorkbook();
        SXSSFWorkbook wb = new SXSSFWorkbook(wb0, 100);
        try {
            int tot=pds.length;
            if (tot>0) {
                for (int i=0;i<tot;i++) {
                    String sheetName = (sheetNames==null || sheetNames.length <=i || sheetNames[i]==null ? String.format("Sheet%d",i+1) : sheetNames[i] );
                    PageInfo<?> pageInfo=pds[i].getPage(1,EXCEL_PAGE_SIZE);
                    int pages=pageInfo.getPages();
                    int process0=(i)*99/tot,process1=(i+1)*99/tot;
                    for (int p=0;p<pages;p++) {
                        List<?> rows = pageInfo.getList();
                        exportSheet(request,wb,sheetName, rows,process0+p*(process1-process0)/pages,process0+(p+1)*(process1-process0)/pages) ;
                        pageInfo=pds[i].getPage(p+2,EXCEL_PAGE_SIZE);
                    }
                }
            }
            wb.write(out);
        } finally {
            wb.close();
            Util.setProgress(100);
        }
    }
    @Deprecated
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, String fileName, String [] sheetNames, Class<?> [] clazzs,
                            List<?>[] rows) throws Exception {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        OutputStream out = response.getOutputStream();
        XSSFWorkbook wb0 = new XSSFWorkbook();
        SXSSFWorkbook wb = new SXSSFWorkbook(wb0, 100);
        int tot=0;
        for (int i=0;i<rows.length;i++)  tot += rows[i].size();
        try {
            if (tot>0) {
                int totExported=0,progress0=0,progress1=0;
                for (int i=0;i<sheetNames.length;i++) {
                    progress1 =100 * (totExported+rows[i].size())/tot;
                    exportSheet(request,wb,sheetNames[i], rows[i],progress0,progress1) ;
                    progress0 = progress1;
                    totExported += rows[i].size();
                }
            }
            wb.write(out);
        } finally {
            wb.close();
        }
    }

    /**
     * 多表单导入excel
     * @param request	request
     * @param file	上传的文件
     * @param sheetNames	包含数据的表单名称列表
     * @param clazzs	导入的数据实体类名称列表
     * @param ipds	导入操作代理
     * @return	成功后的附加信息。null表示无附加信息
     * @throws Exception 失败返回异常
     */
    public static String importExcel(HttpServletRequest request, MultipartFile file, String[] sheetNames, Class<?>[] clazzs, IImportPagedData<?>[] ipds) throws Exception {
        if (file == null || ipds==null || ipds.length==0 || Util.isEmpty(file.getOriginalFilename()))
            return null;
        ZipSecureFile.setMinInflateRatio(-1.0d);
        InputStream input = file.getInputStream();;
        XSSFWorkbook wb = new XSSFWorkbook(input);
        StringBuilder sb=new StringBuilder();
        float step=100/ipds.length;
        for (int i=0;i<ipds.length;i++) {
            String sheetName = (sheetNames==null || sheetNames.length <=i || sheetNames[i]==null ? String.format("Sheet%d",i+1) : sheetNames[i] );
            String msg=importExcel( wb, request,  sheetName, clazzs[i],null,  ipds[i],(int)(i*step),(int)((i+1)*step));
            if (msg!=null) sb.append(msg);
        }
        Util.setProgress(100);
        return sb.toString();
    }

    /**
     * 单表单导入excel
     * @param <C>	类型
     * @param request request
     * @param file	上传的文件
     * @param sheetName	表单名，null是为sheet1
     * @param clazz	实体类
     * @param errorLines	记录错误行号，null表示不记录
     * @param ipd	导入操作代理
     * @return	成功后的附加信息。null表示无附加信息
     * @throws Exception	失败返回异常
     */
    public static <C> String importExcel(HttpServletRequest request,MultipartFile file, String sheetName ,Class<?> clazz,List<Integer> errorLines, IImportPagedData<C> ipd) throws Exception {
        if (file == null || Util.isEmpty(file.getOriginalFilename()))
            return "文件不存在或为空";
        ZipSecureFile.setMinInflateRatio(-1.0d);
        InputStream input = file.getInputStream();;
        XSSFWorkbook wb = new XSSFWorkbook(input);
        String msg=importExcel( wb, request,  sheetName==null?"Sheet1":sheetName , clazz, errorLines, ipd,0,100);
        wb.close();
        return msg;
    }
    public static void setDictDataFromMap(HttpServletRequest request,Map<String,Object> map) {
        if (map==null || request==null) return;
        for(String key : map.keySet())
        {
            request.setAttribute(key,map.get(key));
        }
    }
}
