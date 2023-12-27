package hcmute.edu.vn.store.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.store.Interface.ItemClickDeleteListener;
import hcmute.edu.vn.store.R;
import hcmute.edu.vn.store.adapter.DonHangAdapter;
import hcmute.edu.vn.store.model.DonHang;
import hcmute.edu.vn.store.model.DonHangModel;
import hcmute.edu.vn.store.model.EventBus.DonHangEvent;
import hcmute.edu.vn.store.model.NotiSendData;
import hcmute.edu.vn.store.retrofit.ApiBanHang;
import hcmute.edu.vn.store.retrofit.ApiPushNofication;
import hcmute.edu.vn.store.retrofit.RetrofitClient;
import hcmute.edu.vn.store.retrofit.RetrofitClientNoti;
import hcmute.edu.vn.store.utils.Utils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView redonhang;
    Toolbar toolbar;
    DonHang donHang;
    int tinhtrang;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_don);
        initView();
        initToolbar();
        getOrder();}

    private void getOrder() {
//        apiBanHang= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable.add(apiBanHang.xemDonHang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult(), new ItemClickDeleteListener() {
                                @Override
                                public void onClickDelete(int iddonhang) {
                                    showDeleteOrder(iddonhang);
                                }
                            });
                            redonhang.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        },
                        throwable -> {
                            Log.d("logg", throwable.getMessage());

                        }
                ));
    }

    private void showDeleteOrder(int iddonhang) {
        PopupMenu popupMenu = new PopupMenu(this, redonhang.findViewById(R.id.trangthaidon));
        popupMenu.inflate(R.menu.menu_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.deleteOrder){
                    if(Utils.trangthaidonhang == 0) {
                        deleteOrder(iddonhang);
                    }else {
                        Toast.makeText(XemDonActivity.this, "Không thể hủy đơn hàng này", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteOrder(int iddonhang) {
        compositeDisposable.add(apiBanHang.deleteOrder(iddonhang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if(messageModel.isSuccess()){
                                getOrder();
                            }


                        },
                        throwable -> {
                            Log.d("logg", throwable.getMessage());
                        }
                ));

    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        apiBanHang= RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        redonhang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        redonhang.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    private void pushNotiToUser() {
        //gettoken, donHang.getIduser()
        compositeDisposable.add(apiBanHang.gettoken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(

                        userModel -> {
                            if(userModel.isSuccess()){
                                for(int i=0; i<userModel.getResult().size(); i++){
                                    Map<String, String> data = new HashMap<>();
                                    data.put("title","thongbao");
                                    data.put("body",trangThaiDon(tinhtrang));
                                    NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(), data);
                                    ApiPushNofication apiPushNofication = RetrofitClientNoti.getInstance().create(ApiPushNofication.class);
                                    compositeDisposable.add(apiPushNofication.sendNofitication(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiRespone -> {

                                                    },
                                                    throwable -> {
                                                        Log.d("logg",throwable.getMessage());
                                                    }
                                            ));
                                }
                            }

                        },
                        throwable -> {
                            Log.d("logg", throwable.getMessage());
                        }
                ));

    }


    private String trangThaiDon(int status){
        String result ="";
        switch (status){
            case 0:
                result= "Đơn hàng đang được xử lí";
                break;
            case  1:
                result ="Đơn hàng đã được chấp nhận";
                break;
            case 2:
                result = "Đơn hàng đã giao cho đơn vị vận chuyển";
                break;
            case 3:
                result = "Thành công";
                break;
            case 4:
                result = "Đơn hàng đã hủy";
                break;

        }
        return result;
    }



}