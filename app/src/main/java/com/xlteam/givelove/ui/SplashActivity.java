package com.xlteam.givelove.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.xlteam.givelove.R;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xong_thi_xoa);
        List<String> strings = new ArrayList<>();
        strings.add("11111111111");
        strings.add("222222");
        strings.add("33333333333");
        strings.add("444444444");
        strings.add("55555");
        strings.add("6666");
        strings.add("777777");
        strings.add("888888");
        strings.add("99");
        strings.add("10");
        strings.add("11_11_11_11");
        strings.add("12_12_4fsefk3_");
        strings.add("deo can quan tam index 13 13 13");
        strings.add("deo can quan tam index 14 14");
        strings.add("15");
        strings.add("16");
        strings.add("fgsjkf");
        strings.add("fgsjkf");
        strings.add("fgsjkf");
        strings.add("fgsjkf");
        strings.add("fgsjkf");
        strings.add("fgsjkf");
        strings.add("fgsjkf");
        RecyclerView r = findViewById(R.id.xong_Thi_xoa);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        r.setLayoutManager(layoutManager);
        r.setAdapter(new XongThiXoaAdapter(this, strings));
    }
}