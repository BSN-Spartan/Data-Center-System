package com.spartan.dc.core.util.treeview;

import java.io.Serializable;
import java.util.List;

public class Treeview implements Serializable {

    private static final long serialVersionUID = -8977708032141300452L;
    protected String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String text;

    private String href = "javascript:;";

    private List<Treeview> nodes;

    private State state;

    private String type;

    private List<String> tags;

    private List<String> infos;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<Treeview> getNodes() {
        return nodes;
    }

    public void setNodes(List<Treeview> nodes) {
        this.nodes = nodes;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


    public Treeview() {
    }

    public Treeview(String fuid, String text) {
        this.id = fuid;
        this.text = text;
    }


    public Treeview(String text, String href, List<Treeview> nodes) {
        this.text = text;
        this.href = href;
        this.nodes = nodes;
    }

    public List<String> getInfos() {
        return infos;
    }

    public void setInfos(List<String> infos) {
        this.infos = infos;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


    public class State {
        private boolean checked = false;
        private boolean disabled;
        private boolean expanded = true;
        private boolean selected;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

    }
}
