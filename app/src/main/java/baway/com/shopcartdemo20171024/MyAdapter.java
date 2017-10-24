package baway.com.shopcartdemo20171024;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by admin on 2017/10/24/024.
 */

public class MyAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<GroupBean> grouplist;
    private List<List<ChildBean>> childlist;
    private int count;

    public MyAdapter(Context context, List<GroupBean> grouplist, List<List<ChildBean>> childlist) {
        this.context = context;
        this.grouplist = grouplist;
        this.childlist = childlist;

    }

    @Override
    public int getGroupCount() {
        return grouplist.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childlist.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return grouplist.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childlist.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View v = null;
        GroupViewHolder holder;
        if (view == null) {
            holder = new GroupViewHolder();
            v = View.inflate(context, R.layout.item, null);
            holder.cb = v.findViewById(R.id.cb);
            holder.tv = v.findViewById(R.id.tvName);
            v.setTag(holder);
        } else {
            v = view;
            holder = (GroupViewHolder) v.getTag();
        }
        //赋值
        GroupBean bean = grouplist.get(i);
        holder.cb.setChecked(bean.isChecked());
        holder.tv.setText(bean.getGroupName());
        //给group的checkbox设置点击事件
        holder.cb.setOnClickListener(new GroupCbOnClick(i));
        return v;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        View v=null;
        ChildViewHolder holder;
        if(view==null){
            holder=new ChildViewHolder();
            v=View.inflate(context,R.layout.child_item,null);
            holder.cb=v.findViewById(R.id.cb);
            holder.tv=v.findViewById(R.id.tvName);
            v.setTag(holder);
        }else{
            v=view;
            holder= (ChildViewHolder) v.getTag();
        }
        //赋值
        ChildBean childBean = childlist.get(i).get(i1);
        holder.cb.setChecked(childBean.isChecked());
        holder.tv.setText(childBean.getChildName());

        holder.cb.setOnClickListener(new ChildCbOnClick(i, i1));

        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    class GroupViewHolder {
        CheckBox cb;
        TextView tv;
    }

    class ChildViewHolder {
        CheckBox cb;
        TextView tv;
    }

    class ChildCbOnClick implements View.OnClickListener {

        private int groupPosition;
        private int childPosition;

        public ChildCbOnClick(int groupPosition, int childPosition) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                List<ChildBean> cBean = childlist.get(groupPosition);
                ChildBean cbean = cBean.get(childPosition);
                cbean.setChecked(cb.isChecked());
                //计算选中的商品数，并发送到主界面进行显示
                MessageCounEvent msgCount = new MessageCounEvent();
                msgCount.setCount(totalCount());
                EventBus.getDefault().post(msgCount);
                //判断该商家的所有商品的checkbox是否都选中
                if(isChildChecked(cBean)){
                    grouplist.get(groupPosition).setChecked(true);
                    MessageEvent msg=new MessageEvent();
                    msg.setFlag(isGroupChecked());
                    EventBus.getDefault().post(msg);
                    notifyDataSetChanged();
                }else{
                    grouplist.get(groupPosition).setChecked(false);
                    MessageEvent msg=new MessageEvent();
                    msg.setFlag(false);
                    EventBus.getDefault().post(msg);
                    notifyDataSetChanged();
                }

            }
        }
    }

    class GroupCbOnClick implements View.OnClickListener{

        private int groupPostion;

        public GroupCbOnClick(int groupPostion){
            this.groupPostion=groupPostion;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof CheckBox){
                //多态，因为我是给checkbox设置的点击事件，所以可以强转成checkbox
                CheckBox cb= (CheckBox) view;
                //根据cb.isChecked()是否选中，给一级列的checkbox改变状态\
                grouplist.get(groupPostion).setChecked(cb.isChecked());
                List<ChildBean> cbList=childlist.get(groupPostion);
                for (ChildBean cbean:cbList){
                    cbean.setChecked(cb.isChecked());
                }
                //计算选中的商品数，并发送到主界面进行显示
                MessageCounEvent msgCount=new MessageCounEvent();
                msgCount.setCount(totalCount());
                EventBus.getDefault().post(msgCount);

                MessageEvent msg=new MessageEvent();
                msg.setFlag(isGroupChecked());
                EventBus.getDefault().post(msg);
                notifyDataSetChanged();
            }

        }
    }

    /**
     * 判断该商家的所有商品的checkbox是否都选中
     *
     * @return
     */
    private boolean isChildChecked(List<ChildBean> childBeen) {
        for (int i = 0; i < childBeen.size(); i++) {
            ChildBean childBean = childBeen.get(i);
            if (!childBean.isChecked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断其它的商家是否选中
     *
     * @return
     */
    private boolean isGroupChecked() {
        for (GroupBean groupBean : grouplist) {
            if (!groupBean.isChecked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 主界面全选按钮的操作
     *
     * @param bool
     */
    public void allChecked(boolean bool) {
        for (int i = 0; i < grouplist.size(); i++) {
            grouplist.get(i).setChecked(bool);
            for (int j = 0; j < childlist.get(i).size(); j++) {
                childlist.get(i).get(j).setChecked(bool);
            }
        }
        //计算选中的商品数，并发送到主界面进行显示
        MessageCounEvent msgCount = new MessageCounEvent();
        msgCount.setCount(totalCount());
        EventBus.getDefault().post(msgCount);
        notifyDataSetChanged();

    }

    private int totalCount() {
        count = 0;
        for (int i = 0; i < grouplist.size(); i++) {
            for (int j = 0; j < childlist.get(i).size(); j++) {
                if (childlist.get(i).get(j).isChecked()) {
                    //遍历所有的商品，只要是选中状态的，就加1
                    count++;
                }
            }
        }
        return count;
    }
}
