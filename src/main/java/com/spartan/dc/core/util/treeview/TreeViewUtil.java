package com.spartan.dc.core.util.treeview;

import com.spartan.dc.model.SysResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeViewUtil {

    public static List<Treeview> getTreeviewData(List<SysResource> list, Long parentId, List<Long> resouceIds) {

        List<Treeview> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        Treeview treeview = null;

        for (int i = 0; i < list.size(); i++) {
            SysResource resouce = list.get(i);

            if (resouce.getParentId() == null || (parentId == null || (parentId != null && resouce.getParentId().equals(parentId)))) {
                treeview = new Treeview(resouce.getRsucId() + "", resouce.getRsucName());

                Treeview.State s = treeview.new State();
                boolean isChecked = false;
                if (resouceIds != null && resouceIds.size() > 0) {
                    for (Long str : resouceIds) {
                        if (resouce != null && str != null && resouce.getRsucId() != null && str.equals(resouce.getRsucId())) {
                            isChecked = true;
                            break;
                        }
                    }
                }
                treeview.setType(resouce.getRsucType() + "");
                s.setChecked(isChecked);
                treeview.setState(s);

                List<Treeview> temp = getTreeviewData(list, resouce.getRsucId(), resouceIds);

                if (temp != null && temp.size() > 0) {
                    treeview.setNodes(temp);
                } else {
                    treeview.setNodes(new ArrayList<Treeview>());
                }
                result.add(treeview);
            }
        }
        return result;
    }
    public static List<Treeview> getTreeviewData(List<Map<String, Object>> list, String parentId, List<String> checkedList) {
        List<Treeview> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        Treeview treeview = null;

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> resouce = list.get(i);
            if (resouce.get("parentId") == null || parentId == null || (parentId != null && parentId.length() > 0 && resouce.get("parentId").toString().equals(parentId))) {

                treeview = new Treeview(resouce.get("id") + "", resouce.get("name").toString());

                Treeview.State s = treeview.new State();
                boolean isChecked = false;
                if (checkedList != null && checkedList.size() > 0) {
                    for (String str : checkedList) {
                        if (str != null && resouce.get("id") != null && resouce.get("id").toString().length() > 0 && str.equals(resouce.get("id"))) {
                            isChecked = true;
                            break;
                        }
                    }
                }
                s.setChecked(isChecked);
                treeview.setState(s);
                List list1 = new ArrayList<String>();

                list1.addAll(resouce.get("tags") == null ? new ArrayList<String>() : (List) resouce.get("tags"));
                treeview.setTags(list1);
                treeview.setInfos(resouce.get("tags") == null ? new ArrayList<String>() : (List) resouce.get("tags"));
                if(resouce.containsKey("info")){
                    ArrayList<String> info = (ArrayList<String>)resouce.get("info");
                    treeview.setInfos(info);
                }
                List<Treeview> temp = getTreeviewData(list, resouce.get("id").toString(), checkedList);

                if (temp != null && temp.size() > 0) {
                    treeview.setNodes(temp);
                } else {
                    treeview.setNodes(new ArrayList<Treeview>());
                }
                result.add(treeview);
            }
        }
        return result;
    }

    public static List<Treeview> getEntTreeviewData(List<Map<String, String>> list, String parentId,List<Map<String, String>> checkedList) {
        List<Treeview> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        Treeview treeview = null;
        for (int i = 0; i < list.size(); i++) {
            Map<String, String> resouce = list.get(i);
            if (resouce.get("pid") == null || parentId == null || (parentId != null && parentId.length() > 0 && resouce.get("pid").toString().equals(parentId))) {

                treeview = new Treeview(resouce.get("id") + "", resouce.get("name"));
                treeview.setType(resouce.get("type"));
                Treeview.State s = treeview.new State();
                boolean isChecked = false;
                if (checkedList != null && checkedList.size() > 0) {
                    for (Map<String, String> strMap : checkedList) {
                        String str=strMap.get("id");
                        if (str != null && resouce.get("id") != null && resouce.get("id").toString().length() > 0 && str.equals(resouce.get("id"))) {
                            isChecked = true;
                            break;
                        }
                    }
                }
                s.setChecked(isChecked);
                treeview.setState(s);
                List<Treeview> temp = getEntTreeviewData(list, resouce.get("id").toString(), checkedList);

                if (temp != null && temp.size() > 0) {
                    treeview.setNodes(temp);
                } else {
                    treeview.setNodes(new ArrayList<Treeview>());
                }
                result.add(treeview);
            }
        }

        return result;
    }
}
