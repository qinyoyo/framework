package top.iotequ.framework.exception;

public class IotequDatabaseException extends IotequException {
    private String sql;
    public String getSql() {
        return sql;
    }
    public IotequDatabaseException(Exception e,String sql) {
        super(IotequException.newInstance(e).getError(), IotequException.newInstance(e).getMessage());
        this.sql = sql;
    }
}
