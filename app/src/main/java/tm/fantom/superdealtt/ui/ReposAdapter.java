package tm.fantom.superdealtt.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tm.fantom.superdealtt.R;
import tm.fantom.superdealtt.api.model.RepoItem;

/**
 * Created by fantom on 27-Sep-17.
 */

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.RepoHolder> {

    private ItemClickedListener itemClickedListener;

    private List<RepoItem> repoItems = new ArrayList<>();

    @Override public RepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_item, parent, false);
        return new RepoHolder(v);
    }

    @Override public void onBindViewHolder(RepoHolder holder, int position) {
        if(position != holder.getAdapterPosition()) return;

        RepoItem repo = repoItems.get(position);
        holder.name.setText(repo.getName());
        holder.details.setText(repo.getDescription());
    }

    public void setRepoItems(List<RepoItem> repoItems){
        this.repoItems = repoItems;
        notifyDataSetChanged();
    }

    public void appendRepoItems(List<RepoItem> repoItems){
        this.repoItems.addAll(repoItems);
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return repoItems.size();
    }

    public void setItemClickedListener(ItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    interface ItemClickedListener{
        void onItemClicked(int position);
    }

    class RepoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tvRepoName) TextView name;
        @BindView(R.id.tvRepoDetails) TextView details;

        public RepoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
            if(itemClickedListener!=null) itemClickedListener.onItemClicked(getAdapterPosition());
        }
    }
}
