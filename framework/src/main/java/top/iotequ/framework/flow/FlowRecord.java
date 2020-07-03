package top.iotequ.framework.flow;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FlowRecord {
    private String icon;
    private String type;
    private String size;
    private String state;
    private Date time;
    private String operator;
    private String operation;
    private String selection;
    private String note;
    private String nextOperator;
}
