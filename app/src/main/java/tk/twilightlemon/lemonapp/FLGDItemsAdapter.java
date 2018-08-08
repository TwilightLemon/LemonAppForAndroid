package tk.twilightlemon.lemonapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FLGDItemsAdapter extends RecyclerView.Adapter<FLGDItemsAdapter.BeautyViewHolder> {
    private Context mContext;
    private List<String> data;
    private View.OnClickListener onClick;

    public FLGDItemsAdapter(List<String> data, Context context,View.OnClickListener OnClick) {
        this.data = data;
        this.mContext = context;
        this.onClick=OnClick;
    }

    @Override
    public BeautyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_flgditem, parent, false);
        return new BeautyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeautyViewHolder holder, int position) {
        holder.nameTv.setText(data.get(position));
        holder.item.setOnClickListener(onClick);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class BeautyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        View item;

        public BeautyViewHolder(View itemView) {
            super(itemView);
            item=itemView;
            nameTv = itemView.findViewById(R.id.name_item);
        }
    }
}