package hcmute.edu.vn.store.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import hcmute.edu.vn.store.R;
import hcmute.edu.vn.store.retrofit.ApiBanHang;
import hcmute.edu.vn.store.retrofit.RetrofitClient;
import hcmute.edu.vn.store.utils.Utils;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InformationUserActivity extends AppCompatActivity {
    TextInputEditText username, phone , address;
    AppCompatButton btnupdate;
    ApiBanHang apiBanHang;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        initView();
        initData();
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfor();
            }
        });


    }

    private void updateInfor() {
        String str_username = username.getText().toString().trim();
        String str_phone = phone.getText().toString().trim();
        String str_address = address.getText().toString().trim();

        String email = Utils.user_current.getEmail();
        String pass = "1";


        compositeDisposable.add(apiBanHang.updateinfor(str_username,str_phone,str_address, Utils.user_current.getId() )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if(messageModel.isSuccess()){
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                                Paper.book().delete("user");

                                Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                                compositeDisposable.add(apiBanHang.dangNhap(email,pass)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                userModel -> {

                                                    if(userModel.isSuccess()){
                                                        Utils.user_current = userModel.getResult().get(0);
                                                        //luu lai thong tin nguoi dung
                                                        Paper.book().write("user",userModel.getResult().get(0));
                                                    }
                                                },
                                                throwable -> {
                                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                        ));
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initView() {
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        btnupdate = findViewById(R.id.btnthem);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
    }

    private void initData() {
        phone.setText(Utils.user_current.getMobile());
        username.setText(Utils.user_current.getUsername());
        address.setText(Utils.user_current.getAddress());


    }
}