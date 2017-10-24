package baway.com.shopcartdemo20171024;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView elv;
    private CheckBox cbAll;
    private TextView tvSum;
    private List<GroupBean> grouoplist = new ArrayList<>();
    private List<List<ChildBean>> childlist = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        //给设置ExpandableListView设置数据
        //模拟数据
        initData();

        adapter=new MyAdapter(this,grouoplist,childlist);
        elv.setGroupIndicator(null);
        elv.setAdapter(adapter);
        //全部展开
        for (int i=0;i<grouoplist.size();i++){
            elv.expandGroup(i);
        }
        //给全选设置点击事件
        cbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.allChecked(cbAll.isChecked());
            }
        });

    }

    private void initData() {
        for (int i=0;i<3;i++){
            GroupBean gBaen=new GroupBean("商家"+i,false);
            grouoplist.add(gBaen);
            List<ChildBean> cList=new ArrayList<>();
            for (int j=0;j<2;j++){
                ChildBean cBean=new ChildBean("商品"+i,false);
                cList.add(cBean);
            }
            childlist.add(cList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void messageCountEvent(MessageCounEvent msg){
        tvSum.setText("总计："+msg.getCount()+"个");
    }
    @Subscribe
    public void messageEvent(MessageEvent msg){
        cbAll.setChecked(msg.isFlag());
    }

    private void initView() {
        elv = (ExpandableListView) findViewById(R.id.elv);
        cbAll = (CheckBox) findViewById(R.id.cbAll);
        tvSum = (TextView) findViewById(R.id.tvSum);
    }
}
