package tm.fantom.superdealtt.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import tm.fantom.superdealtt.R;
import tm.fantom.superdealtt.databinding.OrgItemBinding;
import tm.fantom.superdealtt.db.OrgItem;

/**
 * Created by fantom on 27-Sep-17.
 */

public final class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultHolder>
        implements Consumer<List<OrgItem>> {

    private ItemClickedListener itemClickedListener;

    private List<OrgItem> orgList = new ArrayList<>();

    @Override public ResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        OrgItemBinding binding = OrgItemBinding.inflate(inflater, parent, false);
        return new ResultHolder(binding.getRoot());
    }

    @Override public void onBindViewHolder(ResultHolder holder, int position) {
        if(position != holder.getAdapterPosition()) return;

        OrgItem orgItem = orgList.get(position);
        Glide.with(holder.view.getContext()).load(orgItem.avatarUrl())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.avatar);
        holder.binding.setOrgItem(orgItem);
        holder.itemView.setBackgroundResource(R.color.colorItemBackground);
    }

    @Override public int getItemCount() {
        return orgList.size();
    }

    public void setItemClickedListener(ItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    @Override
    public void accept(List<OrgItem> orgItems) throws Exception {
        this.orgList = orgItems;
        notifyDataSetChanged();
    }

    interface ItemClickedListener{
        void onItemClicked(String name);
    }

    class ResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OrgItemBinding binding;
        View view;

        public ResultHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            binding = DataBindingUtil.bind(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
            if(itemClickedListener!=null)
                itemClickedListener.onItemClicked(orgList.get(getAdapterPosition()).name());
        }
    }
}
