package tk.wenop.XiangYu.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by zysd on 15/10/20.
 */
public class AreaEntity extends BmobObject{

    private String area;

    private BmobRelation relation;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public BmobRelation getRelation() {
        return relation;
    }

    public void setRelation(BmobRelation relation) {
        this.relation = relation;
    }
}
