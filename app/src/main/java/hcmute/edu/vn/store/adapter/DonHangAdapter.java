package hcmute.edu.vn.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hcmute.edu.vn.store.Interface.ItemClickDeleteListener;
import hcmute.edu.vn.store.Interface.ItemClickListener;
import hcmute.edu.vn.store.R;
import hcmute.edu.vn.store.model.DonHang;
import hcmute.edu.vn.store.model.EventBus.DonHangEvent;
import hcmute.edu.vn.store.utils.Utils;


public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> listdonhang;
    ItemClickDeleteListener deleteListener;

    public DonHangAdapter(Context context, List<DonHang> listdonhang, ItemClickDeleteListener itemClickDeleteListener) {
        this.context = context;
        this.listdonhang = listdonhang;
        this.deleteListener = itemClickDeleteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        holder.txtdonhang.setText("Đơn hàng: "+donHang.getId() );
        holder.txttrangthai.setText(Utils.statusOrder(donHang.getTrangthai()));


        Utils.trangthaidonhang = donHang.getTrangthai();



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteListener.onClickDelete(donHang.getId());
                return false;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.reChitiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        //adapter chi tiet
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(context,donHang.getItem());
        holder.reChitiet.setLayoutManager(layoutManager);
        holder.reChitiet.setAdapter(chiTietAdapter);
        holder.reChitiet.setRecycledViewPool(viewPool);


    }

    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtdonhang, txttrangthai;
        RecyclerView reChitiet;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            txttrangthai = itemView.findViewById(R.id.trangthaidon);
            reChitiet = itemView.findViewById(R.id.recycleview_chitiet);
        }
    }

}


