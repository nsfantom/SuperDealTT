package tm.fantom.superdealtt.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tm.fantom.superdealtt.api.model.RepoItem;
import tm.fantom.superdealtt.databinding.RepoItemBinding;

/**
 * Created by fantom on 27-Sep-17.
 */

public final class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.RepoHolder> {

    private ItemClickedListener itemClickedListener;

    private List<RepoItem> repoItems = new ArrayList<>();

    @Override public RepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RepoItemBinding binding = RepoItemBinding.inflate(inflater, parent, false);
        return new RepoHolder(binding.getRoot());
    }

    @Override public void onBindViewHolder(RepoHolder holder, int position) {
        if(position != holder.getAdapterPosition()) return;

        RepoItem repo = repoItems.get(position);
        holder.binding.setRepoItem(repo);
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

        RepoItemBinding binding;

        public RepoHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View view) {
            if(itemClickedListener!=null) itemClickedListener.onItemClicked(getAdapterPosition());
        }
    }
}
