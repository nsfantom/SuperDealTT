package tm.fantom.superdealtt.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import tm.fantom.superdealtt.R;
import tm.fantom.superdealtt.db.OrgItem;

/**
 * Created by fantom on 27-Sep-17.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultHolder>
        implements Consumer<List<OrgItem>> {

    private ItemClickedListener itemClickedListener;

    private List<OrgItem> orgList = new ArrayList<>();

    @Override public ResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_item, parent, false);
        return new ResultHolder(v);
    }

    @Override public void onBindViewHolder(ResultHolder holder, int position) {
        if(position != holder.getAdapterPosition()) return;

        OrgItem orgItem = orgList.get(position);
        Glide.with(holder.view.getContext()).load(orgItem.avatarUrl())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.avatar);
        holder.tvName.setText(orgItem.name());
        holder.tvLocation.setText(orgItem.location());
        holder.tvBlog.setText(orgItem.blog());
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

        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvBlog) TextView tvBlog;
        @BindView(R.id.tvLocation) TextView tvLocation;
        @BindView(R.id.avatar) SquareImageView avatar;
        View view;

        public ResultHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
            if(itemClickedListener!=null)
                itemClickedListener.onItemClicked(orgList.get(getAdapterPosition()).name());
        }
    }
}
