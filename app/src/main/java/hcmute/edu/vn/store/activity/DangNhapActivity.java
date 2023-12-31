package hcmute.edu.vn.store.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import hcmute.edu.vn.store.R;
import hcmute.edu.vn.store.model.User;
import hcmute.edu.vn.store.retrofit.ApiBanHang;
import hcmute.edu.vn.store.retrofit.RetrofitClient;
import hcmute.edu.vn.store.utils.Utils;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DangNhapActivity extends AppCompatActivity {


    TextView txtdangki,txtresetpass;
    EditText email,pass;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControll();
    }

    private void initView() {

        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangki = findViewById(R.id.txtdangki);
        txtresetpass = findViewById(R.id.txtresetpass);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);
        firebaseAuth = FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();

        //read data
        if(Paper.book().read("email")!= null && Paper.book().read("pass") != null){
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
            if(Paper.book().read("islogin") != null) {
                boolean flag = Paper.book().read("islogin");
                if (flag){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //dangNhap(Paper.book().read("email"),Paper.book().read("pass"));
                        }
                    },1000);
                }
            }

        }
    }

    private void dangNhap(String email, String pass) {


        compositeDisposable.add(apiBanHang.dangNhap(email,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {

                            if(userModel.isSuccess()){
                                isLogin = true;
                                Paper.book().write("islogin",isLogin);
                                Utils.user_current = userModel.getResult().get(0);
                                //luu lai thong tin nguoi dung
                                Paper.book().write("user",userModel.getResult().get(0));






                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initControll() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });
        txtresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
                startActivity(intent);
            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString().trim();
                String str_pass= pass.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)){
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(str_pass)){
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập pass", Toast.LENGTH_SHORT).show();
                }else {
                    //save
                    Paper.book().write("email",str_email);
                    Paper.book().write("pass",str_pass);
                    if(user != null){
                        //user da co dang nhap firebase
                        dangNhap(str_email,str_pass);
                    }else{
                        //user da signout
                        firebaseAuth.signInWithEmailAndPassword(str_email, str_pass)
                                .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            dangNhap(str_email,str_pass);
                                        } else {
                                            // Đăng nhập thất bại
                                            Exception exception = task.getException();
                                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                                // Mật khẩu không chính xác
                                                Toast.makeText(DangNhapActivity.this, "Mật khẩu không chính xác. Vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
                                            } else if (exception instanceof FirebaseAuthInvalidUserException && ((FirebaseAuthInvalidUserException) exception).getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
                                                // Người dùng không tồn tại
                                                Toast.makeText(DangNhapActivity.this, "Tài khoản không tồn tại. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                                            } else if (exception instanceof FirebaseAuthInvalidUserException && ((FirebaseAuthInvalidUserException) exception).getErrorCode().equals("ERROR_USER_DISABLED")) {
                                                // Tài khoản đã bị vô hiệu hóa
                                                Toast.makeText(DangNhapActivity.this, "Tài khoản đã bị vô hiệu hóa vì nhập sai quá nhiều lần", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Xử lý các trường hợp lỗi đăng nhập khác
                                                Toast.makeText(DangNhapActivity.this, "Đăng nhập thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }




                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null){
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}