package listview.example.x.slidelistview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Created by xww.
 * @Creation time 2018/8/21.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.lv_contact)
    ListView lvContact;

    private ArrayList<ContactEntity> mData;
    private ListViewAdapter mAdapter;

    private final int[] PHOTO = new int[]{
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4, R.drawable.img_5, R.drawable.img_6,
            R.drawable.img_7, R.drawable.img_8, R.drawable.img_9, R.drawable.img_10, R.drawable.img_11, R.drawable.img_12,
            R.drawable.img_13, R.drawable.img_14, R.drawable.img_15, R.drawable.img_16, R.drawable.img_17, R.drawable.img_18,
    };

    private final String[] NAME = new String[]{
            "李白", "露娜", "韩信", "貂蝉", "孙悟空", "关羽",
            "吕布", "阿珂", "王昭君", "武则天", "花木兰", "橘右京",
            "雅典娜", "诸葛亮", "元歌", "老夫子", "虞姬", "公孙离"
    };

    private final String[] MESSAGE = new String[]{
            "你好，我是李白", "你好，我是露娜", "你好，我是韩信", "你好，我是貂蝉", "你好，我是孙悟空", "你好，我是关羽",
            "你好，我是吕布", "你好，我是阿珂", "你好，我是王昭君", "你好，我是武则天", "你好，我是花木兰", "你好，我是橘右京",
            "你好，我是雅典娜", "你好，我是诸葛亮", "你好，我是元歌", "你好，我是老夫子", "你好，我是虞姬", "你好，我是公孙离"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mData = new ArrayList<>();
        for (int i = 0; i < NAME.length; i++) {
            mData.add(new ContactEntity(PHOTO[i], NAME[i], MESSAGE[i]));
        }
        mAdapter = new ListViewAdapter(mData);
        lvContact.setAdapter(mAdapter);

//        lvContact.setVerticalScrollBarEnabled(false);
//        lvContact.setDivider(null);
//        lvContact.setDividerHeight(15);
//        lvContact.setDivider(ContextCompat.getDrawable(this, R.drawable.item_divider));
//        lvContact.setSelector(R.color.colorSelector);
//        ImageView view = findViewById(R.id.empty_view);
//        lvContact.setEmptyView(view);

//        View headerView = LayoutInflater.from(this).inflate(R.layout.header_view, null);
//        View footerView = LayoutInflater.from(this).inflate(R.layout.footer_view, null);
//        lvContact.addHeaderView(headerView);
//        lvContact.addFooterView(footerView);

//        lvContact.setSelection(17);

        lvContact.smoothScrollBy(17,1000);
        lvContact.smoothScrollByOffset(1);
        lvContact.smoothScrollToPosition(17);
    }
}
