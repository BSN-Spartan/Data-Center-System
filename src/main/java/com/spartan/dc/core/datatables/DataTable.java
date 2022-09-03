package com.spartan.dc.core.datatables;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataTable<T> {


    private T condition;


    private PageInfo param;


    public Map<String, Object> getReturnData(List list) {
        Map<String, Object> maps = new HashMap<>();


        com.github.pagehelper.PageInfo<Map<String, Object>> pageInfo = new com.github.pagehelper.PageInfo<Map<String, Object>>(list);


        // maps.put("sEcho", System.currentTimeMillis());


        maps.put("iTotalRecords", getTotalPage(pageInfo.getTotal()));

        maps.put("iTotalDisplayRecords", pageInfo.getTotal());

        maps.put("data", list == null ? new ArrayList<Map<String, Object>>() : list);

        return maps;
    }

    long getTotalPage(long total) {
        if (total % param.getPageSize() == 0) {
            return total % param.getPageSize();
        } else {
            return total % param.getPageSize() + 1;
        }
    }


    public T getCondition() {
        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }

    public PageInfo getParam() {
        return param;
    }

    public void setParam(PageInfo param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "condition=" + condition +
                ", param=" + param +
                '}';
    }
}
