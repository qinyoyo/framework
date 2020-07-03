package top.iotequ.framework.service.utils;

import lombok.NonNull;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.util.EntityUtil;
import top.iotequ.framework.util.OrgUtil;
import top.iotequ.framework.util.SqlUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class QueryUtil {
    private static boolean charsIn(String s,String matches) {
        for (int i=0;i<s.length();i++) {
            if (matches.indexOf(s.substring(i, i+1))<0) return false;
        }
        return true;
    }
    public static boolean dateLikeString(String s) {
        return charsIn(s,"0123456789-: %?:");
    }
    public static boolean numberLikeString(String s) {
        return charsIn(s,"0123456789-+.%?");
    }

    public static void checkParentValid(@NonNull String table, @NonNull String parentField, @NonNull String idField, Object parent, Object id) throws IotequException {
        if (Util.isEmpty(id)) return;
        while (!Util.isEmpty(parent)) {
            if (parent.toString().equals(id.toString()))
                throw new IotequException(IotequThrowable.PARENT_CIRCULAR_REFERENCE, parentField);
            parent = SqlUtil.sqlQueryField(false,"select "+parentField+" from "+table + " where "+idField+" = ? ",parent);
        }
    }
    // 拼接排序字符串，字段均为数据库字段,sort order 支持排序序列
    public static String getOrderString(Class<?> clazz,String sort,String order,String treeParent,String[] groupEntityFields,String defaultOrder) {
        List<String> orderList = new ArrayList<String>();
        List<Map<String,String>> orderList0 = new ArrayList<Map<String,String>>();
        if (!Util.isEmpty(sort)) {
            String [] ss=sort.split(","),  oo=order.split(",");
            for (int i=0;i<ss.length;i++) {
                Map<String,String> m=new HashMap<String,String>();
                m.put("sort", EntityUtil.getDBFieldNameFrom(clazz,ss[i].trim()));
                m.put("order", i>=oo.length?"asc":oo[i].trim());
                orderList0.add(m);
            }
        }
        sort = sort==null ? "": sort.toLowerCase();
        if (!Util.isEmpty(defaultOrder)) {
            String []  oo= defaultOrder.split(",");
            for (String o : oo) {
                if (Util.isEmpty(o)) continue;
                o=o.trim();
                String[] osplit=o.split(" ");
                if (osplit.length<1 || osplit[0].trim().isEmpty() || sort.indexOf(osplit[0].trim().toLowerCase())>=0) continue;
                Map<String,String> m=new HashMap<String,String>();
                m.put("sort",osplit[0].trim());
                m.put("order", osplit.length<2?"asc":(osplit[osplit.length-1].isEmpty()?"asc":osplit[osplit.length-1]));
                orderList0.add(m);
            }
        }
        if (!Util.isEmpty(treeParent)) {
            treeParent=treeParent.trim();
            boolean found=false;
            for (Map<String,String> m : orderList0) {
                if (treeParent.equalsIgnoreCase(m.get("sort"))) {
                    orderList.add(treeParent+" "+m.get("order"));
                    orderList0.remove(m);
                    found=true;
                    break;
                }
            }
            if (!found) {
                orderList.add(treeParent);
            }
        }
        if (groupEntityFields!=null && groupEntityFields.length>0) {
            for (String group:groupEntityFields) {
                if (Util.isEmpty(group)) continue;
                group=EntityUtil.getDBFieldNameFrom(clazz,group);
                if (Util.isEmpty(group)) continue;
                boolean found=false;
                for (Map<String,String> m : orderList0) {
                    if (group.equalsIgnoreCase(m.get("sort"))) {
                        orderList.add(group+" "+m.get("order"));
                        orderList0.remove(m);
                        found=true;
                        break;
                    }
                }
                if (!found) {
                    orderList.add(group);
                }
            }
        }
        if (orderList0.size()>0) {
            for (Map<String,String> m : orderList0) {
                orderList.add(m.get("sort")+" "+m.get("order"));
            }
        }
        return orderList.size()>0 ? String.join(",", orderList) : null;
    }

    public static String getFilterOrg(Class<?> clazz) {
        String orgPrivilege = OrgUtil.getOrgPrivilege(clazz);
        if (!Util.isEmpty(orgPrivilege)) {    // 判断部门权限
            orgPrivilege = orgPrivilege.trim();
            String tableName = EntityUtil.getDBTableNameFrom(clazz);
            if (!Util.isEmpty(tableName) && !orgPrivilege.startsWith(tableName + ".")) orgPrivilege = tableName + "." + orgPrivilege;
            return orgPrivilege;
        }
        return null;
    }
    private static String[] getTypeSignOf(Class<?> clazz, String entity) {
        String type="S";
        String showType="text";
        String jdbcType=EntityUtil.getJdbcTypeFrom(clazz,entity);
        if (jdbcType.equals("TIMESTAMP")) {
            type="D";
            showType="datetime";
        }
        else if (jdbcType.equals("DATE")) {
            type="D";
            showType="date";
        }
        else if (jdbcType.equals("TIME")) {
            type="D";
            showType="time";
        } else if (jdbcType.equals("INTEGER") || jdbcType.equals("SMALLINT") ||jdbcType.equals("BIGINT") ||jdbcType.equals("TINYINT")
                || jdbcType.equals("DECIMAL") ||jdbcType.equals("DOUBLE") ||jdbcType.equals("REAL")) {
            type="N";
        }
        return new String[]{type, showType};
    }
    /**
     * 拼接查询条件，所有字段均为entity字段
     * @param clazz entity 类
     * @param search   模糊查询串，多个模糊条件以空格分隔。如果某字段设置了值，则不匹配模糊查询值
     * @param entities  需要判断条件的字段名列表
     * @param request  request
     * @return 条件字符串
     */
    public static String getWhereString(Class<?> clazz,String orgPrivilege, String search,String[] entities,HttpServletRequest request) {
        List<String>  whereList = new ArrayList<String>();
        String tableName=EntityUtil.getDBTableNameFrom(clazz);
        if (!Util.isEmpty(orgPrivilege)) {
            whereList.add(orgPrivilege);
        }
        String additinal=request.getParameter(Util._addtional_condition);  // 处理join和data-list的f:动态条件
        if (!Util.isEmpty(additinal) && !additinal.trim().toLowerCase().startsWith("select ")) {
            whereList.add(additinal);
        }
        List<String> entitiesForSearch = new ArrayList<>();
        if (!Util.isEmpty(entities)) {
            for (String f : entities) {
                f=f.trim();
                if (f.isEmpty()) continue;
                Class<?> fieldClazz = null;
                try {
                    fieldClazz = EntityUtil.getFieldTypeName(clazz,f);
                } catch (IotequException e) {
                    continue;
                }
                String expression = EntityUtil.getDBFieldExpressionFrom(clazz,f);
                String fieldAlias=null;
                if (Util.isEmpty(expression)) {
                    fieldAlias = EntityUtil.getDBFieldNameFrom(clazz, f);
                    if (Util.isEmpty(fieldAlias)) continue;
                    if (fieldAlias.indexOf(".") < 0) fieldAlias = tableName + "." + fieldAlias;
                } else fieldAlias="("+expression+")"; // mysql 不支持别名作为查询条件，必须使用对应的表达式
                String[] typeSign = getTypeSignOf(clazz, f);
                String type=typeSign[0];
                String showType=typeSign[1];
                String o=request.getParameter(f);
                if (Util.isEmpty(o)) {
                    String o0=request.getParameter(f+"_start"),o1=request.getParameter(f+"_end"),o2=request.getParameter(f+"_exclusive_end");
                    if (!Util.isEmpty(o0) || !Util.isEmpty(o1) || !Util.isEmpty(o1)) {
                        if (type.equals("S") || type.equals("N")) {
                            String yh=(type.equals("S") ? "'" : "");
                            if (!Util.isEmpty(o0) && !Util.isEmpty(o1) ) {
                                whereList.add( fieldAlias +" between "+yh+o0+yh+" and "+yh+o1+yh);
                            } else {
                                if (!Util.isEmpty(o0) ) {
                                    whereList.add( fieldAlias +" >= "+yh+o0+yh);
                                }
                                if (!Util.isEmpty(o1) ) {
                                    whereList.add( fieldAlias +" <= "+yh+o1+yh);
                                }
                                if (!Util.isEmpty(o2) ) {
                                    whereList.add( fieldAlias +" < "+yh+o2+yh);
                                }
                            }
                        } 	else if (showType.equals("date") || showType.equals("time") || showType.equals("datetime")) {
                            String dt0=Util.isEmpty(o0)?null:o0.substring(showType.equals("time") ? 11 : 0 ,showType.equals("time") ? 16 : (showType.equals("date") ?10 :16)) + (showType.equals("date")? "" : ":00"),
                                    dt1=Util.isEmpty(o1)?null:o1.substring(showType.equals("time") ? 11 : 0 ,showType.equals("time") ? 16 : (showType.equals("date") ?10 :16)) + (showType.equals("date")? "" : ":59"),
                                    dt2=Util.isEmpty(o2)?null:o2.substring(showType.equals("time") ? 11 : 0 ,showType.equals("time") ? 16 : (showType.equals("date") ?10 :16)) + (showType.equals("date")? "" : ":00");
                            String yh="'";
                            if (!Util.isEmpty(dt0) && !Util.isEmpty(dt1) ) {
                                whereList.add( fieldAlias +" between "+yh+dt0+yh+" and "+yh+dt1+yh);
                            } else {
                                if (!Util.isEmpty(dt0) ) {
                                    whereList.add( fieldAlias +" >= "+yh+dt0+yh);
                                }
                                if (!Util.isEmpty(dt1) ) {
                                    whereList.add( fieldAlias +" <= "+yh+dt1+yh);
                                }
                                if (!Util.isEmpty(dt2) ) {
                                    whereList.add( fieldAlias +" < "+yh+dt2+yh);
                                }
                            }
                        }
                    } else entitiesForSearch.add(f);  // 字段没有设置任何查询值，由模糊查询判断
                }
                else {
                    o=o.trim();
                    if ("is null".equals(o) || "is not null".equals(o)) {
                        whereList.add(fieldAlias + " "+o);
                    } else {
                        String[] oo = o.split(",");
                        if (fieldClazz == Boolean.class) {
                            for (String b : oo) {
                                if (Util.boolValue(b)) whereList.add(fieldAlias + " = " + ("N".equals(type) ? "1" : b));
                                else whereList.add(fieldAlias + " = " + ("N".equals(type) ? "0" : b));
                            }
                        } else {
                            if (oo.length > 1) {
                                String fis = SqlUtil.findInSet(fieldAlias, false, "S".equals(type), o, true);
                                if (!Util.isEmpty(o)) whereList.add(fis);
                            } else if (o.indexOf("%") >= 0 || o.indexOf("?") >= 0 || o.indexOf("*") >= 0)
                                whereList.add(fieldAlias + " like '" + o.replace("*", "%") + "'");
                            else whereList.add(fieldAlias + " = '" + o + "'");
                        }
                    }
                }
            }
        }
        if (!Util.isEmpty(search) && !Util.isEmpty(entitiesForSearch)) {  // 通过search单一模糊查询,空格分割多个模糊条件（and）
            String [] ss=search.split(" ");
            for (String s:ss) {
                s=s.trim();
                if (s.isEmpty()) continue;
                s=s.replace("*","%");
                String cs="";
                for (String f : entitiesForSearch) {
                    f = f.trim();
                    if (f.isEmpty()) continue;
                    String[] typeSign = getTypeSignOf(clazz, f);
                    String type=typeSign[0];

                    String expression = EntityUtil.getDBFieldExpressionFrom(clazz,f);
                    String fieldAlias=null;
                    if (Util.isEmpty(expression)) {
                        fieldAlias = EntityUtil.getDBFieldNameFrom(clazz, f);
                        if (Util.isEmpty(fieldAlias)) continue;
                        if (fieldAlias.indexOf(".") < 0) fieldAlias = tableName + "." + fieldAlias;
                    } else fieldAlias="("+expression+")"; // mysql 不支持别名作为查询条件，必须使用对应的表达式
                    if (type.equals("D")) {
                        if (dateLikeString(s))
                            cs += (cs.isEmpty() ? "" : " or ") + fieldAlias + " like '%" + s + "%'";
                    } else if (type.equals("I")) {
                        if (numberLikeString(s))
                            cs += (cs.isEmpty() ? "" : " or ") + fieldAlias + " like '%" + s + "%'";
                    } else {
                        cs += (cs.isEmpty() ? "" : " or ") + fieldAlias + " like '%" + s + "%'";
                    }
                }
                if (!cs.isEmpty()) whereList.add("("+cs+")");
            }
        }
        return whereList.size()>0 ? String.join(" and ", whereList) : null;
    }

    public static Object getSqlField(String sql, Object pojoObject) {
        if (sql == null || pojoObject==null)	return null;
        List<Map<String, Object>> list=null;
        try {
            list = SqlUtil.sqlQueryUsePojo(false,sql, pojoObject);
        } catch (Exception e) {}
        if (list!=null && list.size()>0) {
            Map<String, Object> map=list.get(0);
            if (map==null) return null;
            for (String p:map.keySet()) {
                return map.get(p);
            }
        }
        return null;
    }
}
