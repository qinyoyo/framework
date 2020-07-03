package top.iotequ.framework.service.impl;
import top.iotequ.framework.pojo.DataDict;
import top.iotequ.framework.dao.DataDictDao;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.flow.IFlowService;
import top.iotequ.framework.service.impl.CgService;
import top.iotequ.framework.service.IDaoService;
import org.springframework.stereotype.Service;
import top.iotequ.framework.service.utils.DictionaryUtil;
import top.iotequ.framework.service.utils.UploadDownUtil;
import top.iotequ.framework.service.utils.QueryUtil;
import top.iotequ.framework.util.*;
import java.util.*;

/**************************************************
Create by iotequ codegenerator 3.0.0
Author : Qinyoyo
"**************************************************/
@ConditionalOnMissingClass({"top.iotequ.framework.service.impl.SysDataDictService"})
@Service(value="sysDataDictService")
public class CgSysDataDictService extends CgService<DataDict>  {
private static final Logger log = LoggerFactory.getLogger(CgSysDataDictService.class);
    @Autowired
    private DataDictDao dataDictDao;
    @Override
    public Class<DataDict> getEntityClass() {
        return DataDict.class;
    }
    @Override
    public IDaoService<DataDict> getDaoService() {
        return dataDictDao ;
    }
    @Override
    public IFlowService<DataDict> getFlowService() {
        return null;
    }
    @Override
    public Map<String,Object> getDictionary(DataDict obj, Boolean useTree, String dynaFields) {
        Map<String,Object> map=new HashMap<>();
        return map;
    }
    @Override
    public DataDict getDefaultObject(DataDict dataDict) throws IotequException {
        checkAvailable();
        if (dataDict==null)  dataDict=new DataDict();
        else dataDict.setId(null);
        return dataDict;
    }
    @Override
    public void changeEmpty2Null(DataDict dataDict) {
        if (Objects.nonNull(dataDict)) {
        }
    }
}
