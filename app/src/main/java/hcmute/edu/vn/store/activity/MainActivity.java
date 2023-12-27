package hcmute.edu.vn.store.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.store.R;
import hcmute.edu.vn.store.adapter.LoaiSpAdapter;
import hcmute.edu.vn.store.adapter.SanPhamMoiAdapter;
import hcmute.edu.vn.store.model.LoaiSp;
import hcmute.edu.vn.store.model.SanPhamMoi;
import hcmute.edu.vn.store.model.User;
import hcmute.edu.vn.store.retrofit.ApiBanHang;
import hcmute.edu.vn.store.retrofit.RetrofitClient;
import hcmute.edu.vn.store.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
ImageSlider imageSlider;
    LoaiSpAdapter loaiSpAdapter;

    List<LoaiSp> mangloaisp;

    DrawerLayout drawerLayout;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang ;
    List<SanPhamMoi > mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch, imageMess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         apiBanHang= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if (Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;

        }
        getToken();
        AnhXa();
        ActionBar();
        ActionViewFlipper();
        if(isConnected(this)){

            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else{
            Toast.makeText(getApplicationContext(), "Không có internet, vui lòng kết nối", Toast.LENGTH_LONG).show();

        }
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(), s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            },
                                            throwable -> {
                                                Log.d("log", throwable.getMessage());
                                            }

                                    ));
                        }
                    }
                });

        compositeDisposable.add(apiBanHang.gettoken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                   userModel -> {
                       if (userModel.isSuccess()){
                           Utils.ID_RECEIVED = String.valueOf(userModel.getResult().get(0).getId());
                       }
                   },throwable -> {

                        }

                ));



    }
    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> apdaterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai",1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        laptop.putExtra("loai",2);
                        startActivity(laptop);
                        break;
                    case 3:
                        Intent thongtin = new Intent(getApplicationContext(), InformationUserActivity.class);
                        startActivity(thongtin);
                        break;


                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        //xoa key user
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {

        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()){
                                mangSpMoi=sanPhamMoiModel.getResult();
                                //sp moi
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),"Không kết nối được với server" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }

                ));

    }
    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()){
                                mangloaisp =loaiSpModel.getResult();
                                mangloaisp.add(new LoaiSp("Đăng xuất","https://www.iconpacks.net/icons/2/free-user-logout-icon-3056-thumb.png"));
                                //showviewbar
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }

                        }

                ));


    }
    private void ActionViewFlipper() {
        List<SlideModel> imageList = new ArrayList<>();
//        imageList.add(new SlideModel("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png", null));
//        imageList.add(new SlideModel("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg", null));
//        imageList.add(new SlideModel("http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png", null));

        compositeDisposable.add(apiBanHang.getKhuyenMai()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        khuyenMaiModel -> {
                            if(khuyenMaiModel.isSuccess()){
                                for (int i=0; i< khuyenMaiModel.getResult().size(); i++){
                                    imageList.add(new SlideModel(khuyenMaiModel.getResult().get(i).getUrl(), null));
                                }
                                imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);
                                imageSlider.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemSelected(int i) {
                                        Intent intent = new Intent(getApplicationContext(), KhuyenMaiActivity.class);
                                        intent.putExtra("noidung", khuyenMaiModel.getResult().get(i).getThongtin());
                                        intent.putExtra("url", khuyenMaiModel.getResult().get(i).getUrl());
                                        startActivity(intent);

                                    }

                                    @Override
                                    public void doubleClick(int i) {

                                    }
                                });

                            }
                        },throwable -> {
                            Log.d("logg", throwable.getMessage());
                        }
                ));
//        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);







    }
    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void AnhXa() {
        imgsearch = findViewById(R.id.imgsearch);
        imageMess=findViewById(R.id.image_mess);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        imageSlider = findViewById(R.id.image_slider);
        recyclerViewManHinhChinh =findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh =findViewById(R.id.listviewmanhinhchinh);
        navigationView =findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge =findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        // khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi= new ArrayList<>();
        if(Paper.book().read("giohang") !=null){
            Utils.manggiohang = Paper.book().read("giohang");
        }
        if(Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else {
            int totalItem=0;
            for(int i=0; i<Utils.manggiohang.size();i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang= new Intent(getApplicationContext(),GioHangActivity.class);
                startActivity(giohang);

            }
        });

        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
            }
        });

        imageMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem=0;
        for(int i=0; i<Utils.manggiohang.size();i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected()) ){
            return  true;}
        else { return  false;}
    }
    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
