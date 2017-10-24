package baway.com.shopcartdemo20171024;

/**
 * Created by admin on 2017/10/24/024.
 */

public class ChildBean {
    private String childName;
    private boolean checked;

    public ChildBean() {
    }

    public ChildBean(String childName, boolean checked) {
        this.childName = childName;
        this.checked = checked;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
