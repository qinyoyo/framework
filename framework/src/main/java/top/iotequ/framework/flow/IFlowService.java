package top.iotequ.framework.flow;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.pojo.CgEntity;
import top.iotequ.framework.util.EntityUtil;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IFlowService<T extends CgEntity> {
    String APPROVE = "approve";
    String DENY = "deny";
    String REVIEW = "review";

    String flowAction = "flowAction";
    String flowNextOperator = "flowNextOperator";
    String flowRegisterBy = "flowRegisterBy";
    String flowCopyToList = "flowCopyToList";
    String flowNote = "flowNote";
    String flowSelection = "flowSelection";
    String flowRegisterTime = "flowRegisterTime";

    /**
     * 用值创建一个状态列表
     *
     * @param allStateValues 所有状态值
     * @param allStateNames  所有名称
     * @return 状态表
     */
    static List<Map<String, Object>> buildStates(int[] allStateValues, String[] allStateNames) {
        List<Map<String, Object>> allStates = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < allStateValues.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", allStateValues[i]);
            map.put("text", allStateNames[i]);
            allStates.add(map);
        }
        return allStates;
    }

    /**
     * 获得当前对象的所有可执行操作
     * @param request HttpServletRequest
     * @param flow 流程服务对象
     * @param pojo  实体类对象
     * @param userId 用户id，null表示当前登录用户
     * @param <C> 实体类型
     * @return 当前用户对特定流程对象的可执行操作序列（add,edit,view,delete,以及流程操作的子集）
     */
    static <C extends CgEntity> String getAllOperations(HttpServletRequest request, IFlowService<C> flow, C pojo, String userId) {
        if (userId==null) userId = Util.getUser().getId();
        String r="",sp="";
        try {
            flow.checkPrivilege(null, "add", userId);
            r="add";
            sp=",";
        } catch (IotequException e) {}
        try {
            flow.checkPrivilege(pojo, "update", userId);
            r=r+sp+"edit,view";
            sp=",";
        } catch (IotequException e) {
            r=r+sp+"view";
            sp=",";
        }
        try {
            String id = EntityUtil.getStringPkValueFrom(pojo);
            if (id!=null) {
                flow.checkDeletePrivilege(id, userId);
                r = r + sp + "delete";
                sp = ",";
            }
        } catch (IotequException e) {}
        if (userId.equals(flow.getNextOperator(pojo))) {
            String o = flow.getNextOperations(pojo, userId);
            if (!Util.isEmpty(o)) r = r + sp + o;
        }
        return r;
    }

    /**
     * 获得该操作对应的实体类
     *
     * @return 实体类
     */
    Class<T> getEntityClass();

    /**
     * 获得流程处理的详细列表数据，可以自定义表进行保存
     * @param flowId 流程id
     * @return 处理流程过程列表
     */
    List<FlowRecord> getFlowProcess(String flowId);

    /**
     * 保存流程处理数据，可以自定义表进行保存
     * @param flowProcess 流程处理数据
     * @throws IotequException 异常
     */
    void saveFlowProcess(Object flowProcess) throws IotequException;

    /**
     * 删除流程处理过程数据
     * @param ids 流程id的字符串列表
     * @throws IotequException 异常
     */
    void  deleteFlowProcess(String ids) throws IotequException;


    /**
     * 从一个事务对象生成一个流程对象，一般在状态迁移transfer 后调用生成记录在调用saveFlowProcess保存
     *
     * @param request   HttpServletRequest对象,获取参数
     * @param obj0      原流程数据对象，对象已经经过状态迁移，新建对象时该值为 null
     * @param obj1      经过状态迁移后的流程数据对象
     * @param operation 流程操作操作
     * @return 流程对象
     * @throws IotequException 异常
     */
    Object generateFlowProcess(HttpServletRequest request, T obj0, T obj1, String operation) throws IotequException;

    /**
     * 执行一个操作后发送消息
     *
     * @param request   HttpServletRequest对象,获取参数
     * @param obj0      原流程数据对象，对象已经经过状态迁移，新建对象时该值为 null
     * @param obj1      经过状态迁移后的流程数据对象
     * @param operation 流程操作操作,add,update,delete,flowOperation ...
     * @throws IotequException 异常
     */
    void sendMessage(HttpServletRequest request, T obj0, T obj1, String operation) throws IotequException;

    /**
     * 获得流程处理路由
     * @param operation 操作
     * @param flowId 流程id
     * @return 处理路由
     */
    String flowRoute(String operation,String flowId);

    /**
     * 流程对象浏览路由
     * @param flowId 流程id
     * @return 路由
     */
    String recordViewRoute(String flowId);

    /**
     * 获得模块名称
     *
     * @return 模块名称
     */
    String getModuleName();

    /**
     * 获得流程名称
     *
     * @return 流程名称
     */
    String getName();

    /**
     * 获得流程项目名称
     *
     * @param pojo pojo对象,不能为空
     * @return 流程项目名称
     */
    String getItemName(T pojo);

    /**
     * 获得状态列表
     *
     * @return 所有的状态有序序列表, map 键值 ： value ： Integer，状态值，text：String 状态名称
     */
    List<Map<String, Object>> getStates();

    /**
     * 获得某一操作的可选操作
     *
     * @param operation 操作代码，通常为表定义code
     * @return 所有的可选操作序列表, map 键值 ： value ： String，选择代码，text：String 选择名称
     */
    List<Map<String, Object>> getSelections(String operation);

    /**
     * 获得指定操作的标题
     *
     * @param operation 操作代码，通常为表定义code
     * @return 标题
     */
    String getOperationName(String operation);

    /**
     * 获得所有可以执行的操作
     * @return 所有的操作
     */
    String [] getAllOperations();

    /**
     * 指定用户可以对指定对象所能执行的操作code序列
     *
     * @param pojo   pojo对象
     * @param userId user id
     * @return 逗号分隔的流程控制表单的code
     */
    default String getNextOperations(T pojo, String userId) {
        if (userId == null) userId = Util.getUser().getId();
        String r = "";
        for (String op :  getAllOperations()) {
            try {
                checkPrivilege(pojo, op, userId);
                r = (r.isEmpty() ? "" : ",") + op;
            } catch (Exception e) {
            }
        }
        if (r.isEmpty()) return null;
        else return r;
    }

    /**
     * 判断用户是否可以执行该操作
     *
     * @param pojo      pojo对象,新建时为空
     * @param operation 不能为空，操作代码，add,update，以及流程代码，流程代码不能与系统操作代码相同
     * @param userId    用户，null表示当前用户
     * @throws IotequException 没有权限时抛出异常
     */
    void checkPrivilege(T pojo, String operation, String userId) throws IotequException;

    /**
     * 检查删除记录权限
     *
     * @param ids    以逗号分隔的id值
     * @param userId null 表示当前用户
     * @throws IotequException 没有权限时抛出异常
     */
    void checkDeletePrivilege(String ids, String userId) throws IotequException;

    /**
     * 获得状态
     * @param pojo 流程对象
     * @return 状态
     */
    int getState(T pojo);

    /**
     * 设置状态
     * @param pojo 对象
     * @param state 状态
     */
    void setState(T pojo,int state);
    /**
     * 获得流程发起人
     *
     * @param pojo 流程对象
     * @return 发起人id
     */
    String getRegisterBy(T pojo);

    /**
     * 获得下一步处理人
     * @param pojo 流程对象
     * @return 下一步处理人id
     */
    String getNextOperator(T pojo);

    /**
     * 设置下一步处理人员
     * @param pojo 对象
     * @param nextOperator 下一步处理人
     */
    void setNextOperator(T pojo,String nextOperator);

    /**
     * 获得下一步处理人员的字典
     * @param pojo 对象
     * @param operation 流程操作,null表示编辑状态
     * @param selection 当前选择,null表示编辑状态
     * @return 下一步处理人员的字典
     */
    List<Map<String, Object>> getDictionaryOfNextOperator(T pojo,String operation,String selection);
    default List<Map<String, Object>> getDictionaryOfNextOperator(HttpServletRequest request) {
        try {
            T obj = EntityUtil.createEntity(getEntityClass(), request);
            String opertion = request.getParameter(flowAction);
            String selection = request.getParameter(flowSelection);
            return getDictionaryOfNextOperator(obj, opertion,selection);
        } catch (Exception e) { return null; }
    }

    /**
     * 获得默认下一步处理人员
     * @param pojo 对象
     * @param operation 流程操作,null表示编辑状态
     * @param selection 当前选择,null表示编辑状态
     * @return 默认下一步处理人员id
     */
    String defaultNextOperator(T pojo,String operation,String selection);
    default String defaultNextOperator(HttpServletRequest request) {
        try {
            T obj = EntityUtil.createEntity(getEntityClass(), request);
            String opertion = request.getParameter(flowAction);
            String selection = request.getParameter(flowSelection);
            return defaultNextOperator(obj, opertion,selection);
        } catch (Exception e) { return null; }
    }

    /**
     * 获得对象的抄送对象列表
     * @param pojo 流程对象
     * @return 抄送人员id列表
     */
    String getCopyToList(T pojo);

    /**
     * 设置抄送人员清单
     * @param pojo 对象
     * @param copyToList 抄送名单
     */
    void setCopyToList(T pojo,String copyToList);

    /**
     * 获得抄送人员可以选择的字典
     * @param pojo 对象
     * @param operation 流程操作,null表示编辑状态
     * @param selection 当前选择,null表示编辑状态
     * @return 抄送人员字典
     */
    List<Map<String, Object>> getDictionaryOfCopyToList(T pojo,String operation,String selection);
    default List<Map<String, Object>> getDictionaryOfCopyToList(HttpServletRequest request) {
        try {
            T obj = EntityUtil.createEntity(getEntityClass(), request);
            String opertion = request.getParameter(flowAction);
            String selection = request.getParameter(flowSelection);
            return getDictionaryOfCopyToList(obj, opertion,selection);
        } catch (Exception e) { return null; }
    }
    /**
     * 获得抄送人员默认值
     * @param pojo 对象
     * @param operation 流程操作
     * @param selection 当前选择,null表示编辑状态
     * @return 默认抄送人员
     */
    String defaultCopyToList(T pojo,String operation,String selection);
    default String defaultCopyToList(HttpServletRequest request) {
        try {
            T obj = EntityUtil.createEntity(getEntityClass(), request);
            String opertion = request.getParameter(flowAction);
            String selection = request.getParameter(flowSelection);
            return defaultCopyToList(obj, opertion,selection);
        } catch (Exception e) { return null; }
    }


    /**
     * 状态迁移函数。
     *
     * @param pojo      操作的对象，对象不同，可能选择不一样
     * @param operation 操作代码，通常为表定义code
     * @param selection 操作选择
     * @return 新的状态
     * @throws IotequException 无法迁移，返回异常
     */
    int stateTransfer(T pojo, String operation, String selection) throws IotequException;

    /**
     * 检查参数设置是否正确，该函数在状态迁移后执行
     *
     * @param pojo 实体类对象
     * @throws IotequException 存在参数错误抛出异常
     */
    void checkParameters(T pojo) throws IotequException;
}
