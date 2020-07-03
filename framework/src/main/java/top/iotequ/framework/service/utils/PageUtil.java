package top.iotequ.framework.service.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.http.ResponseEntity;
import top.iotequ.framework.util.RestJson;

import java.util.HashMap;
import java.util.Map;

public class PageUtil {
    /**
     * 设置分页查询参数
     * @param pageSize  分页大小
     * @param pageNumber 查询页码，从1开始
     */
    public static void setStartPageNumber(Integer pageSize,Integer pageNumber) {
        if (pageSize!=null && pageSize>0) {
            if (pageNumber==null || pageNumber<=1) pageNumber=1;
            PageHelper.startPage(pageNumber, pageSize);
        }
    }
    public ResponseEntity<Map<String, Object>> getTableDataEntity(PageInfo<?> pi, Map<String,Object>... params) {
        Map<String, Object> obj = getPagedTableData(pi);
        RestJson j = new RestJson();
        j.put("data", obj);
        for (Map<String,Object> map : params) j.setAttributes(map);
        return j.toResponse();
    }

    public static Map<String, Object> getPagedTableData(PageInfo<?> pi) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("total", pi.getTotal());
        obj.put("fixedScroll", true);
        obj.put("data", pi.getList());
        obj.put("page", pi.getPageNum());
        obj.put("pages", pi.getPages());
        obj.put("pageSize", pi.getPageSize());
        obj.put("success", true);
        obj.put("message", null);
        return obj;
    }

}
