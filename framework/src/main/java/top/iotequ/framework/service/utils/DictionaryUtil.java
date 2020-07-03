package top.iotequ.framework.service.utils;

import top.iotequ.framework.util.EntityUtil;
import top.iotequ.framework.util.SqlUtil;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryUtil {
    static final String selectX = "^\\s*select\\s+([^,*]+,)*\\s*\\*(\\s+|\\s*,[^,*]+)from.*$";
    public static String getValuesFromDict(List<Map<String, Object>> list ,String ts) {
        String [] tt=ts.split(",");
        String vs="";
        for (Map<String, Object> m : list) {
            for (int j=0;j<tt.length;j++) {
                if (EntityUtil.entityEquals(m.get("text"),tt[j])) {
                    vs += (vs.isEmpty()?"":",") + m.get("value").toString();
                }
            }
        }
        return vs;
    }
    public static String getTextsFromDict(List<Map<String, Object>> list ,String vs) {
        String [] vv=vs.split(",");
        String text="";
        for (Map<String, Object> m : list) {
            for (int j=0;j<vv.length;j++) {
                if (EntityUtil.entityEquals(m.get("value"),vv[j])) {
                    text += (text.isEmpty()?"":",") + m.get("text").toString();
                }
            }
        }
        return text;
    }
    public static void setDictDataFromMap(HttpServletRequest request,Map<String,Object> map) {
        if (map==null || request==null) return;
        for(String key : map.keySet())
        {
            request.setAttribute(key,map.get(key));
        }
    }
    static public List<Map<String, Object>> getDictList(Object[] values, String[] texts) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (values == null || values.length == 0) return list;
        for (int i = 0; i < values.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", StringUtil.toString(values[i]));
            if (texts == null || i >= texts.length) map.put("text", StringUtil.toString(values[i]));
            else map.put("text", texts[i]);
            list.add(map);
        }
        return list;
    }

    // 获取数据库的字典数据
    public static List<Map<String, Object>> getDictListFromDatabase(Object pojo, String tab, String value, String text,
                                                             String pid, boolean fullname,String dynaCondition) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (Util.isEmpty(tab))
            return result;
        List<Map<String, Object>> list=null;
        String sql = "";
        if (!Util.isEmpty(tab)) tab = SqlUtil.sqlStringUsePojo(tab, pojo);
        if (pojo==null) dynaCondition = null; // list 不适用动态条件
        if (!Util.isEmpty(dynaCondition)) dynaCondition = SqlUtil.sqlStringUsePojo(dynaCondition, pojo);
        tab=tab.trim();
        if (tab.toLowerCase().startsWith("select")) {
            if (Util.isEmpty(dynaCondition)) sql = tab ;
            else sql = SqlUtil.sqlAddCondition(tab,dynaCondition);
            if (!Util.isEmpty(value) && sql.toLowerCase().matches(selectX)) {
                String fields = value + " as \"value\", " + (Util.isEmpty(text) ? value : text) + " as \"text\"";
                value="value";
                text="text";
                if (!Util.isEmpty(pid)) {
                    fields += ", " + pid + " as \"pId\"";
                    pid = "pId";
                }
                sql = "select distinct" + sql.replaceFirst("\\*",fields).substring(6);
            } else {
                if (Util.isEmpty(value)) value="value";
                if (Util.isEmpty(text)) text="text";
            }

            /*
            if (!Util.isEmpty(value) && !Util.isEmpty(text))
                sql = StringUtil.replaceBetween(sql, "select", "from", " distinct "+value + " as \"value\"," + text + " as \"text\" "
                        + (Util.isEmpty(pid) ? "" : "," + pid + " as \"pId\" "), true, false);
             */
            sql=SqlUtil.sqlAddOrgCondition(sql);
            try {
                list=SqlUtil.sqlQuery(sql);
            } catch (Exception e) {}
        } else if (Util.isEmpty(value)) { // 从sys_data_dict中读取，tab为分类代码
            list=Util.getDataDict(tab);
            if (list != null && list.size()>0) {
                for (Map<String, Object> map : list) {
                    map.put("text", map.get("text").toString());
                }
            }
            value="value";
            text="text";
            pid=null;
        } else {
            String [] tt=tab.split(":");   // tab 采用 table:a,b的样式引入新的查询字段
            sql = "select distinct " +(tt.length>1 && !tt[1].isEmpty() ? tt[1]+", " : "")+ value + " as \"value\"," + text + " as \"text\" "
                    + (Util.isEmpty(pid) ? "" : "," + pid + " as \"pId\" ") + "from " + tt[0];
            if (!Util.isEmpty(dynaCondition)) sql = SqlUtil.sqlAddCondition(tab,dynaCondition);
            value="value";
            text="text";
            if (!Util.isEmpty(pid)) pid = "pId";
            try {
                list=SqlUtil.sqlQuery(sql);
            } catch (Exception e) {}
        }
        if (list != null && list.size()>0) {
            if (!Util.isEmpty(pid) && fullname) {
                Object obj;
                for (Map<String, Object> map : list) {
                    int level = 0; // 设置层级控制，放置数据错误导致死循环
                    obj = map.get(pid);
                    String pKey = (obj == null ? null : obj.toString());
                    obj = map.get(text);
                    String name = (obj == null ? "" : obj.toString());
                    if (name == null)
                        name = "";
                    while (!Util.isEmpty(pKey) && !pKey.equals("0") && level < 10) {
                        boolean foundParent = false;
                        for (Map<String, Object> par : list) {
                            obj = par.get(value);
                            String myId = (obj == null ? "" : obj.toString());
                            if (myId.equals(pKey)) {
                                obj = par.get(text);
                                name = (obj == null ? "" : obj.toString()) + "-" + name;
                                obj = par.get(pid);
                                pKey = (obj == null ? null : obj.toString());
                                foundParent = true;
                                level++;
                                break;
                            }
                        }
                        if (!foundParent)
                            pKey = null;// 循环完成，没有找到parent，跳出循环，避免死循环
                    }
                    map.put("fullname", name);
                }
            }
            for (Map<String, Object> map : list) { // 支持,分隔的快速定义方式
                String [] tt = StringUtil.toString(map.get(text)).toString().split(",");
                String [] vv = StringUtil.toString(map.get(value)).split(","); // 字典值全部统一为String，否者前端显示有错
                for (int i=0;i<vv.length && i<tt.length;i++) {
                    Map<String, Object> m =new HashMap<>();
                    m.put("text",tt[i].trim());
                    m.put("value",vv[i].trim());
                    result.add(m);
                }
            }
        }
        return result;
    }

    /*
     * 参数 ： table: 表名，可以包含多表join，以及可以包含where 和order等参数
     * valueField: 取值字段
     * textField: 列表显示字段
     * pKey: 父级记录的键值
     * key: 键值，一般为id，为空时，该值 = valueField。key与pKey对应实现数机构
     * where :条件
     * order: 排序
     * initValue：初始值，用以初始化json的state值
     * 返回值：treeView可用的列表，包含
     * value,text,fullname,state,nodes等字段
     */
    public static List<Map<String, Object>> getTreeViewData(Object pojo, String table, String valueField, String textField, String pKey,
                                                            String key, String where, String order, String dynaCondition) {
        if (table == null || table.equals("null"))
            return null;
        String sql ;
        if (!Util.isEmpty(dynaCondition)) dynaCondition = SqlUtil.sqlStringUsePojo(dynaCondition, pojo);
        if (table.trim().toLowerCase().startsWith("select"))   {
            sql = table;
            if (!Util.isEmpty(valueField) && sql.matches(selectX)) {
                String fields = "distinct " + valueField + " as value, " + (Util.isEmpty(textField) ? valueField : textField) + " as text";
                valueField="value";
                textField="text";
                if (!Util.isEmpty(pKey)) {
                    fields += ", " + pKey + " as pKey";
                    pKey = "pKey";
                } else pKey = "pKey";
                if (!Util.isEmpty(key)) {
                    fields += ", " + key + " as keyId";
                    key = "keyId";
                } else key = "value";
                sql = sql.replaceFirst("\\*",fields);
            } else {
                if (Util.isEmpty(valueField) || Util.isEmpty(pKey)) return null;
                sql = SqlUtil.sqlReplaceSelectFields(sql, valueField + " as \"value\","
                        + (Util.isEmpty(key) ? valueField + " as \"keyId\"," : key + " as \"keyId\",") + (Util.isEmpty(textField) ? valueField : textField) + " as \"text\" "
                        + (Util.isEmpty(pKey) ? "" : (" ," + pKey + " as \"pKey\" ")));
                valueField="value";
                textField="text";
                if (!Util.isEmpty(pKey)) pKey="pKey";
                key="keyId";
            }
        }
        else {
            String [] tt=table.split(":");
            if (tt.length>1 && !tt[1].isEmpty()) sql="select  "+tt[1]+", * from " + tt[0];
            else sql="select  * from " + tt[0];
        }
        if (!Util.isEmpty(where))
            sql = sql + " where " + where;
        if (!Util.isEmpty(dynaCondition)) sql = SqlUtil.sqlAddCondition(sql,dynaCondition);
        sql=SqlUtil.sqlAddOrgCondition(sql);
        if (!Util.isEmpty(order))
            sql = sql + " order by " + order;
        return getTreeViewData(sql, valueField, textField, pKey,key);
    }

    /*
     * 参数 ： sql: sql语句，必须为select * 的格式
     * valueField: 取值字段 textField:
     * 列表显示字段 pKey: 父级记录的键值
     * key: 键值，一般为id，为空时，该值取valueField。key与pKey对应实现数机构
     * where :	 * 条件
     * order: 排序
     * 返回值：treeView可用的列表，包含 value,text,fullname,state,nodes等字段
     */
    private static List<Map<String, Object>> getTreeViewData(String sql, String valueField, String textField, String pKey, String key) {
        if (sql == null)
            return null;
        List<Map<String, Object>> list=null;
        try {
            list = SqlUtil.sqlQuery(sql);
        } catch (Exception e) {}
        if (list!=null && list.size()>0) {
            for ( Map<String, Object> map : list ) {
                Object parent = Util.isEmpty(pKey) ? null : map.get(pKey);
                if (!Util.isEmpty(parent)) {
                    boolean parentExists=false;
                    for ( Map<String, Object> m : list ) {
                        if (EntityUtil.entityEquals(parent,m.get(key))) {
                            parentExists=true;
                            break;
                        }
                    }
                    if (!parentExists) map.put("pKey", null);   // 没有匹配的上级，设为空
                }
            }
            List<Map<String, Object>> result = getTreeViewData(list, "", valueField, textField, pKey, key, null);
            return result;
        } else return list;
    }

    private static List<Map<String, Object>> getTreeViewData(List<Map<String, Object>> list, String pName,
                                                             String value,String text,String pKey,String keyId,
                                                             Object pValue) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                Object parent = Util.isEmpty(pKey) ? null : map.get(pKey);
                if ((Util.isEmpty(parent) && Util.isEmpty(pValue)) || (!Util.isEmpty(parent)
                        && !Util.isEmpty(pValue) && parent.toString().equals(pValue.toString()))) {
                    String name = StringUtil.toString(map.get(text));
                    if (name == null)
                        name = "";
                    String fullName = (pName.isEmpty() ? name : pName + "-" + name);
                    Map<String, Object> m = new HashMap<String, Object>();
                    Object v = map.get(value);
                    m.put("value", StringUtil.toString(v));  // value 统一为string
                    m.put("text", map.get(text));
                    m.put("fullname", fullName);
                    List<Map<String, Object>> children = getTreeViewData(list, fullName, value,text,pKey,keyId, map.get(keyId));
                    if (children != null && children.size() > 0)
                        m.put("nodes", children);
                    result.add(m);
                }
            }
        }
        return result;
    }

}
