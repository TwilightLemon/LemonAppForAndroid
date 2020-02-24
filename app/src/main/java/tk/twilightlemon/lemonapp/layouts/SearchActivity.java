package tk.twilightlemon.lemonapp.layouts;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import tk.twilightlemon.lemonapp.Helpers.MusicLib;
import tk.twilightlemon.lemonapp.Helpers.SearchView.ICallBack;
import tk.twilightlemon.lemonapp.Helpers.SearchView.SearchView;
import tk.twilightlemon.lemonapp.Helpers.SearchView.bCallBack;
import tk.twilightlemon.lemonapp.R;

public class SearchActivity extends AppCompatActivity {

    // 1. 初始化搜索框变量
    private SearchView searchView;
    public static MainActivity ma;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. 绑定视图
        setContentView(R.layout.activity_search);

        // 3. 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                MusicLib.Search(ma, string,true,0);
                finish();
            }
        });
        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });

    }
}