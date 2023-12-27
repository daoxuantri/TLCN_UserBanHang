package hcmute.edu.vn.store.retrofit;

import hcmute.edu.vn.store.model.DonHangModel;
import hcmute.edu.vn.store.model.KhuyenMaiModel;
import hcmute.edu.vn.store.model.LoaiSpModel;
import hcmute.edu.vn.store.model.MessageModel;
import hcmute.edu.vn.store.model.SanPhamMoiModel;
import hcmute.edu.vn.store.model.UserModel;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiBanHang {
    @GET("getloaisp.php")
    Observable<LoaiSpModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    @GET("khuyenmai.php")
    Observable<KhuyenMaiModel> getKhuyenMai();

    //POST DATA
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai

    );

    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid

    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap(
            @Field("email") String email,
            @Field("pass") String pass

    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email

    );


    @POST("donhang.php")
    @FormUrlEncoded
    Observable<MessageModel> createOder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet



    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id

    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search(
            @Field("search") String search

    );

//    @POST("xoa.php")
//    @FormUrlEncoded
//    Observable<MessageModel> xoaSanPham(
//            @Field("id") int id
//
//    );

    @POST("insertsp.php")
    @FormUrlEncoded
    Observable<MessageModel> insertSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int id
    );
    @POST("updatesp.php")
    @FormUrlEncoded
    Observable<MessageModel> updatesp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int idloai,
            @Field("id") int id
    );

    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );

        @POST("updatemomo.php")
    @FormUrlEncoded
    Observable<MessageModel> updateMomo(
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> gettoken(
            @Field("status") int status
//                        ,
//            @Field("iduser") int iduser

    );

    @POST("deleteorder.php")
    @FormUrlEncoded
    Observable<MessageModel> deleteOrder(
            @Field("iddonhang") int id

    );

    @POST("updateinformation.php")
    @FormUrlEncoded
    Observable<MessageModel> updateinfor(
            @Field("username") String username,
            @Field("mobile") String gia,
            @Field("address") String hinhanh,
            @Field("id") int id
    );


}
