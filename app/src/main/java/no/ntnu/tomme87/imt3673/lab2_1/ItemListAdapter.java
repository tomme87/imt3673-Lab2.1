package no.ntnu.tomme87.imt3673.lab2_1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomme on 01.03.2018.
 *
 * The adapter used in my recyclerview that holds my data (RSS items)
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder> {
    private final String TAG = "ItemListAdapter";

    static final String REQUEST_URL = "itemListAdapter.selected.URL";
    static final String REQUEST_DESC = "itemListAdapter.selected.description";

    private final LayoutInflater inflater;
    private List<ItemEntity> data = new ArrayList<>();
    private Context context;

    public ItemListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<ItemEntity> data) {
        this.data = data;
        //notifyItemRangeChanged(0, data.size());
        notifyDataSetChanged();
    }

    public void onClick(int position) {
        ItemEntity item = data.get(position);
        Log.d(TAG, "got item: " + item.getTitle() + " -> " + item.getLink());

        Intent intent = new Intent(this.context, ItemDetailsActivity.class);
        intent.putExtra(REQUEST_URL, item.getLink());
        intent.putExtra(REQUEST_DESC, item.getDescription());
        context.startActivity(intent);
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_item, parent, false);
        return new ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder holder, int position) {
        ItemEntity current = data.get(position);
        holder.title.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ItemListViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public ItemListViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}
