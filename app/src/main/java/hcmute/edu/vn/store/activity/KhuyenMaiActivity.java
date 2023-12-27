package hcmute.edu.vn.store.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import hcmute.edu.vn.store.R;

public class KhuyenMaiActivity extends AppCompatActivity {
    TextView noidung;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khuyen_mai);
        initView();
        initData();
    }

    private void initView() {
        noidung = findViewById(R.id.km_noidung);
        imageView = findViewById(R.id.km_image);
    }

    private void initData() {
        String nd = getIntent().getStringExtra("noidung");
        String url = getIntent().getStringExtra("url");
        noidung.setText(nd);
        Glide.with(this).load(url).into(imageView);
    }
}