package top.iotequ.framework.service;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.flow.IFlowService;
import top.iotequ.framework.pojo.CgEntity;
import top.iotequ.framework.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public interface ICgService<T extends CgEntity> {
    /**
     * 获得服务对应的实体类
     * @return 实体类
     */
    Class<T> getEntityClass();

    /**
     * 获得服务的 Dao service
     * @return dao service
     */
    IDaoService<T> getDaoService();

    /**
     * 获得流程服务
     * @return 流程服务，非流程定义返回null
     */
    IFlowService<T> getFlowService();

    /**
     * 获得licence
     * @return licence，null表示无licence控制
     */
    default Integer getLicence() { return null; }

    /**
     * 获得试用天数
     * @return 试用天数，null表示无试用限制
     */
    default Integer getTrialDays() { return null; }

    /**
     * 检查模块是否可用
     * @return 返回还可以用的天数
     * @throws IotequException 不可用，返回异常
     */
    int checkAvailable() throws IotequException;

    /**
     * 剩余licence数量
     * @return 剩余licence数量
     * @throws IotequException 异常
     */
    int getLicenceLeft() throws IotequException;

    /**
     * service 初始化后的附加操作
     */
    void initial();

    /**
     * 获得一个实体类对象
     * @param id 键值
     * @return 实体类对象
     */
    T select(String id);

    /**
     * 获得部门权限
     * @param request HttpServletRequest
     * @return 权限条件(sql where 子句) ，如 sys_user.rog_code in （1,2,3），如果不限制，返回null
     */
    String orgCodePrivilege(HttpServletRequest request);

    /***
     * 拖动排序的操作
     * @param sortField  需要重新修改的排序字段
     * @param list  需要修改排序的数据列表
     * @param request request
     * @return 是否执行了相关完成
     * @throws IotequException 异常
     */
    boolean dragSort(String sortField, List<T> list, HttpServletRequest request) throws IotequException;

    /**
     * 获得字典
     * @param obj 实体对象，用于传递特定参数,obj=null时为list查询字典忽略动态条件
     * @param useTree 是否试用树形结构
     * @param dynaFields 用串列表来定义需要哪些属性的字典，null表示全部存在的字典
     * @return 所有的数据字典。字典list的map必须包括text,value,可能包含fullname,nodes
     */
    Map<String,Object> getDictionary(T obj, Boolean useTree, String dynaFields);

    /**
     * 获得列表数据。该函数一般不需要重载，使用CgService的定义即可
     * @param pageSize 分页时每页的记录数，null不分页
     * @param pageNumber 页码，从1开始
     * @param sort 排序字段列表
     * @param order 排序方法 desc,asc等
     * @param search 模糊查询条件
     * @param request HttpServletRequest传递附加参数如 defaultOrder，groupByEntityFields等
     * @return 分页数据
     */
    List<T> getDataList(Integer pageSize,Integer pageNumber,String sort,String order, String search, HttpServletRequest request);

    /**
     * 获得restful的分页数据，数据
     * @param needLoadDictionary true表示查询时将字典同时下传
     * @param resortFirstField 先将数据的排序字段重新定义后再返回数据
     * @param pageSize 分页时每页的记录数，null不分页
     * @param pageNumber 页码，从1开始
     * @param sort 排序字段列表
     * @param order 排序方法 desc,asc等
     * @param search 模糊查询条件
     * @param request HttpServletRequest传递附加参数如 defaultOrder，groupByEntityFields等
     * @return { data：{ data: 记录list,total：记录总数,page:当前页,pages:总页数} , dictionary: 字典map，success: true } list的flowAvailableActions字段记录可执行的流程操作
     * @throws IotequException 异常
     */
    RestJson getListPageData(Boolean needLoadDictionary,String resortFirstField, Integer pageSize,Integer pageNumber,String sort,String order, String search,HttpServletRequest request) throws IotequException;

    /**
     * 获得流程处理列表数据
     * @param flowId 流程记录id
     * @return restful的流程处理列表 {data: { data: 列表数据 }}
     */
    default RestJson getFlowProcessData(String flowId) {
        IFlowService<T> flowService = getFlowService();
        if (Objects.nonNull(flowService)) return new RestJson().data(flowService.getFlowProcess(flowId));
        else return new RestJson().setErrorCode(IotequThrowable.INVALID_OPERATION,"flowService is null");
    }

    /**
     * 设置默认记录值
     * @param obj 记录
     * @return 设置了有默认值的字段的obj
     * @throws IotequException 异常
     */
    T getDefaultObject(T obj) throws IotequException;

    /**
     * 查询一个记录
     * @param id 主键值
     * @param request HttpServletRequest
     * @return 对象
     * @throws Exception 异常
     */
    T getRecord(String id, HttpServletRequest request) throws Exception;

    /**
     * 下载记录的文件附件
     * @param field 属性
     * @param id 主键值
     * @param fileName 文件名
     * @param response HttpServletResponse
     * @throws IotequException 异常
     */
    void download(String field, String id,String fileName,HttpServletResponse response) throws IotequException;

    /**
     * 将可能的空值转化为null
     * @param pojo 对象
     */
    default void changeEmpty2Null(T pojo){}

    /**
     * 将不能为空的字段的null修改为默认值
     * @param pojo 对象
     */
    default void changeNull2Default(T pojo) {}

    /**
     * 将附件字段的值根据实际存储值修改
     * @param obj 对象
     */
    default void changeFileField(T obj) {}

    /**
     * 执行保存操作
     * @param isNew 新建记录
     * @param flowCode 流程代码
     * @param totalFilePart 附件总数
     * @param obj 对象
     * @param idSaved 原来的键值
     * @param request HttpServletRequest
     * @return 保存结果
     * @throws Exception 异常
     */
    RestJson doSave(boolean isNew, String flowCode,Integer totalFilePart, T obj, String idSaved, HttpServletRequest request) throws Exception;

    /**
     * 快速修改字段
     * @param list 需要修改或插入的记录列表。记录有主键为修改模式，仅修改值非空的字段。无主键在插入数据
     * @return {savedRecordIds: 修改或插入的主键list，主键为null表示该条记录失败, parameter:{rows:成功记录数}}
     * @throws IotequException 异常
     */
    RestJson updateSelective(List<T> list) throws IotequException ;

    /**
     * 删除记录
     * @param id 键值
     * @param sons 需要同步删除的子表，table:子表数据库名,parent:关联值。没有定义数据库外键的才需要该操作
     * @param request HttpServletRequest
     * @return 操作结果
     * @throws IotequException 异常
     */
    RestJson doDelete(String id,List<Map<String,String>> sons, HttpServletRequest request) throws IotequException;

    /**
     * 批量删除记录
     * @param ids 键值串列表
     * @param sons 需要同步删除的子表，table:子表数据库名,parent:关联值。没有定义数据库外键的才需要该操作
     * @param request HttpServletRequest
     * @return 操作结果
     * @throws IotequException 异常
     */
    RestJson doBatchDelete(String ids,List<Map<String,String>> sons,HttpServletRequest request) throws IotequException ;

    /**
     * 导出到excel表
     * @param workBook poi工作表，null打开一个新工作表
     * @param startProgress 开始进度值（0-99）
     * @param maxProgress 结束进度 （0-100）
     * @param sons 子服务
     * @param fileName 文件名
     * @param sheet 工作簿名
     * @param sort 排序字段列表
     * @param order 排序方法 desc,asc等
     * @param search 模糊查询条件
     * @param request HttpServletRequest传递附加参数如 defaultOrder，groupByEntityFields等
     * @param response HttpServletResponse
     * @throws Exception 异常
     */
    void export(SXSSFWorkbook workBook,int startProgress, int maxProgress,List<ICgService> sons, String fileName, String sheet, String sort, String order,
                String search, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 导入excel表
     * @param file 上传的文件
     * @param request HttpServletRequest传递附加参数
     * @return 操作结果
     * @throws Exception 异常
     */
    RestJson doImport(MultipartFile file,HttpServletRequest request) throws Exception;

    /**
     * 执行一个数据查询，为图表等提供数据,需要根据自己的需求重写
     * @param params 请求参数，根据需求定义
     * @return 操作结果
     * @throws Exception 异常
     */
    RestJson sqlQuery(Map<String,Object> params) throws Exception;

    /**
     * 执行一个自定义操作
     * @param action 操作名
     * @param id 主键值或主键序列
     * @param request request
     * @return 执行结果
     * @throws IotequException 出错抛出异常
     */
    default RestJson doAction(String action, String id, HttpServletRequest request) throws IotequException { return new RestJson(); }

    /**
     * 列表查询的固定条件，此条件与用户的条件一起得到最终的查询条件
     * @param path ：路径参数，路径定义了具体的列表
     * @return 不含where关键字的where子句
     */
    default String listFilter(String path) { return null; };

    /**
     * 列表前处理
     * @param list    列表
     * @param request request
     */
    default void beforeList(List<T> list, HttpServletRequest request) {};

    /**
     * 打开update页面前执行
     * @param obj     实体对象
     * @param request request
     * @throws IotequException 异常
     */
    default void beforeUpdate(T obj, HttpServletRequest request) throws IotequException {};

    /**
     * 保存新建或修改记录前执行检查。抛出异常将结束保存
     *
     * @param obj0    原实体对象
     * @param obj     即将被修改为
     * @param updateSelective   只修改 obj 中值不为 null 的字段，主键不可修改
     * @param request request
     * @throws IotequException 异常
     */
    default void beforeSave(T obj0, T obj, boolean updateSelective, HttpServletRequest request) throws IotequException {};

    /**
     * 设置主键
     * @param obj     实体对象
     */
    default void setPrimaryKey(T obj) {};

    /**
     * 保存记录后执行，用于增加附加操作
     * @param obj0    原来的记录(新加记录,该值为null)
     * @param obj     修改后的记录
     * @param request request
     * @param j       用 j.put 函数向前端传递参数
     * @throws IotequException 抛出异常回滚
     */
    default void afterSave(T obj0, T obj, HttpServletRequest request, RestJson j) throws IotequException {};

    /**
     * 删除记录前执行
     * @param ids     记录的主键序列
     * @param request request
     * @throws IotequException 抛出异常终止删除操作并向前端提示异常消息
     */
    default void beforeDelete(String ids, HttpServletRequest request) throws IotequException {}

    /**
     * 删除记录后的附加操作
     * @param ids     记录的主键序列
     * @param request request
     * @param j       用 j.put 函数向前端传递参数
     * @throws IotequException 抛出异常终止
     */
    default void afterDelete(String ids, HttpServletRequest request, RestJson j) throws IotequException{}

    /**
     * 导出记录前用于对导出记录进行预处理
     * @param list    导出的记录
     * @param request request
     * @throws IotequException 抛出异常终止操作并向前端提示异常消息
     */
    default void beforeExport(List<T> list, HttpServletRequest request) throws IotequException{}

    /**********************
     * 自定义导出操作
     * @param sort            排序参数
     * @param order        desc或asc
     * @param search        模糊查询参数
     * @param request    request
     * @param response    response
     * @return 返回 true 将结束操作，不执行默认的导出操作
     * @throws IotequException        异常
     */
    default boolean customExport(String sort, String order, String search, HttpServletRequest request, HttpServletResponse response) throws IotequException {
        return false;
    }

    /**
     * 导入前的操作，可对上传数据进行处理
     * @param list    从前端上传文件获得的数据
     * @param request request
     * @throws IotequException 抛出异常将终止操作
     */
    default void beforeImport(List<T> list, HttpServletRequest request) throws IotequException {}

    /**
     * 导入后的操作，数据插入成功后进行处理
     * @param list    从前端上传文件获得的数据
     * @param pk      主键
     * @param request request
     * @param j       用 j.put 函数向前端传递参数
     * @throws IotequException 抛出异常终止
     */
    default void afterImport(List<T> list, String pk, HttpServletRequest request, RestJson j)  throws IotequException{}

    /**
     * 自定义导入操作
     * @param file    前端上传的文件
     * @param request request
     * @param j       返回给前端的参数
     * @throws IotequException 异常
     * @return 返回 true 将结束操作，不执行默认的导入操作
     */
    default boolean customImport(MultipartFile file, HttpServletRequest request, RestJson j) throws IotequException {
        return false;
    }
}
