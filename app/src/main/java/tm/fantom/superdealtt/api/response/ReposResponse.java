package tm.fantom.superdealtt.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import tm.fantom.superdealtt.api.model.RepoItem;

/**
 * Created by fantom on 27-Sep-17.
 */

public final class ReposResponse {
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("incomplete_results")
    @Expose
    private Boolean incompleteResults;
    @SerializedName("items")
    @Expose
    private List<RepoItem> items = null;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Boolean getIncompleteResults() {
        return incompleteResults;
    }

    public void setIncompleteResults(Boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    public List<RepoItem> getItems() {
        return items;
    }

    public void setItems(List<RepoItem> items) {
        this.items = items;
    }

}
