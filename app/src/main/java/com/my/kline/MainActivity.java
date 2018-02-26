package com.my.kline;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.my.kline.widget.LineView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineView my_line_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        my_line_view = findViewById(R.id.my_line_view);

        // 坐标点位置
        List<PointF> pointFs = new ArrayList<PointF>();
        pointFs.add(new PointF(0.5F, 3.5F));
        pointFs.add(new PointF(1.5F, 2.3F));
        pointFs.add(new PointF(2.5F, 3.5F));
        pointFs.add(new PointF(3.5F, 3.2F));
        pointFs.add(new PointF(4.5F, 6.8F));
        pointFs.add(new PointF(5.5F, 3.0F));
        pointFs.add(new PointF(6.5F, 3.1F));
//        pointFs.add(new PointF(7.5F, 3.5F));

        my_line_view.setData(pointFs, "X轴提示文字", "Y轴提示文字",MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
