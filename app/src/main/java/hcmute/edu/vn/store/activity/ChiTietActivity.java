    package hcmute.edu.vn.store.activity;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.ImageView;
    import android.widget.Spinner;
    import android.widget.TextView;

    import com.bumptech.glide.Glide;
    import com.nex3z.notificationbadge.NotificationBadge;

    import java.text.DecimalFormat;
    import java.util.ArrayList;
    import java.util.List;

    import hcmute.edu.vn.store.R;
    import hcmute.edu.vn.store.model.GioHang;
    import hcmute.edu.vn.store.model.SanPhamMoi;
    import hcmute.edu.vn.store.utils.Utils;
    import io.paperdb.Paper;


    public class ChiTietActivity extends AppCompatActivity {
        TextView tensp, giasp, mota;
        Button btnthem, btnyoutube;
        ImageView imghinhanh;
        Spinner spinner;
        Toolbar toolbar;
        SanPhamMoi sanPhamMoi;
        NotificationBadge badge;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chi_tiet);
            initView();
            ActionToolBar();
            initData();
            initControl();
        }

        private void initControl() {
            btnthem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    themGioHang();
                    Paper.book().write("giohang",Utils.manggiohang);
                }
            });

            btnyoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youtube = new Intent(getApplicationContext(), YouTubeActivity.class);
                    youtube.putExtra("linkvideo", sanPhamMoi.getLinkvideo());
                    startActivity(youtube);
                }
            });
        }

        private void themGioHang() {
            if(Utils.manggiohang.size() > 0 ){
                boolean flag = false;
                int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
                for(int i=0; i< Utils.manggiohang.size() ; i++){
                    if (Utils.manggiohang.get(i).getIdsp() == sanPhamMoi.getId()){
                        Utils.manggiohang.get(i).setSoluong( soluong + Utils.manggiohang.get(i).getSoluong());
                        flag=true;
                    }
                }
                if(flag == false){
                    long gia = Long.parseLong(sanPhamMoi.getGiasp()) ;
                    GioHang gioHang = new GioHang();
                    gioHang.setGiasp(gia);
                    gioHang.setSoluong(soluong);
                    gioHang.setIdsp(sanPhamMoi.getId());
                    gioHang.setTensp((sanPhamMoi.getTensp()));
                    gioHang.setHinhsp(sanPhamMoi.getHinhanh());
                    gioHang.setSltonkho(sanPhamMoi.getSltonkho());
                    Utils.manggiohang.add(gioHang);
                }


            }else {
                int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
                long gia = Long.parseLong(sanPhamMoi.getGiasp());
                GioHang gioHang = new GioHang();
                gioHang.setGiasp(gia);
                gioHang.setSoluong(soluong);
                gioHang.setIdsp(sanPhamMoi.getId());
                gioHang.setTensp((sanPhamMoi.getTensp()));
                gioHang.setHinhsp(sanPhamMoi.getHinhanh());
                gioHang.setSltonkho(sanPhamMoi.getSltonkho());
                Utils.manggiohang.add(gioHang);

            }
            int totalItem=0;
            for(int i=0; i<Utils.manggiohang.size();i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));

        }

        private void initData() {
            sanPhamMoi = (SanPhamMoi) getIntent().getSerializableExtra("chitiet");
            tensp.setText(sanPhamMoi.getTensp());
            mota.setText(sanPhamMoi.getMota());
            Glide.with(getApplicationContext()).load(sanPhamMoi.getHinhanh()).into(imghinhanh);
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            giasp.setText("Giá: "+ decimalFormat.format(Double.parseDouble(sanPhamMoi.getGiasp()))+ "Đ");
            List<Integer> so = new ArrayList<>();
            for(int i=0; i< sanPhamMoi.getSltonkho()+1; i++){
                so.add(i);
            }
            ArrayAdapter<Integer> adapterspin = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,so);
            spinner.setAdapter(adapterspin);

        }

        private void initView(){
            tensp= findViewById(R.id.txttensp);
            giasp= findViewById(R.id.txtgiasp);
            mota=findViewById(R.id.txtmotachitiet);
            btnthem = findViewById(R.id.btnthemvaogiohang);
            btnyoutube =findViewById(R.id.btnyoutube);
            spinner=findViewById(R.id.spinner);
            imghinhanh =findViewById(R.id.imgchitiet);
            toolbar =findViewById(R.id.toolbar);
            badge = findViewById(R.id.menu_sl);
            FrameLayout frameLayoutgiohang =findViewById(R.id.framegiohang);
            frameLayoutgiohang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                    startActivity(giohang);
                }
            });
            if(Utils.manggiohang != null){

                int totalItem=0;
                for(int i=0; i<Utils.manggiohang.size();i++){
                    totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
                }
                badge.setText(String.valueOf(totalItem));
            }


        }
        private void ActionToolBar() {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        @Override
        protected void onResume() {
            super.onResume();
            if(Utils.manggiohang != null){

                int totalItem=0;
                for(int i=0; i<Utils.manggiohang.size();i++){
                    totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
                }
                badge.setText(String.valueOf(totalItem));
            }
        }
    }