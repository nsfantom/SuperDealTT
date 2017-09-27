package tm.fantom.superdealtt.api;

import io.reactivex.Maybe;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tm.fantom.superdealtt.api.response.OrgResponse;
import tm.fantom.superdealtt.api.response.ReposResponse;


/**
 * Created by fantom on 22-May-17.
 */

public interface ApiService {
//    @GET("data.php?id=1")
//    Maybe<DataResponse> getInitData();

    // https://api.github.com/search/repositories?q=org:facebook

    // https://api.github.com/orgs/facebook

    @GET("orgs/{name}")
    Maybe<OrgResponse> getOrg(@Path("name") String name);

    @GET("search/repositories")
    Maybe<ReposResponse> getPublicRepos(@Query("q") String name, @Query("page") int page, @Query("per_page") int perPage);
}
