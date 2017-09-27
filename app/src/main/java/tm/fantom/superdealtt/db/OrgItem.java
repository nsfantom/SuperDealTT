package tm.fantom.superdealtt.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import io.reactivex.functions.Function;

/**
 * Created by fantom on 27-Sep-17.
 */
@AutoValue
public abstract class OrgItem implements Parcelable{
    public static final String TABLE = "org_item";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String BLOG = "blog";
    public static final String LOCATION = "location";
    public static final String AVATAR = "avatar_url";

    public abstract long id();
    public abstract String name();
    @Nullable public abstract String blog();
    @Nullable public abstract String location();
    @Nullable public abstract String avatarUrl();

    public static final Function<Cursor, OrgItem> MAPPER = cursor -> {
        long id = Db.getLong(cursor, ID);
        String name = Db.getString(cursor, NAME);
        String blog = Db.getString(cursor, BLOG);
        String location = Db.getString(cursor, LOCATION);
        String avatarUrl = Db.getString(cursor, AVATAR);
        return new AutoValue_OrgItem(id, name, blog, location, avatarUrl);
    };

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public OrgItem.Builder id(long id) {
            values.put(ID, id);
            return this;
        }

        public OrgItem.Builder name(String name) {
            values.put(NAME, name);
            return this;
        }

        public OrgItem.Builder blog(String blog) {
            values.put(BLOG, blog);
            return this;
        }

        public OrgItem.Builder location(String location) {
            values.put(LOCATION, location);
            return this;
        }

        public OrgItem.Builder avatarUrl(String avatarUrl) {
            values.put(AVATAR, avatarUrl);
            return this;
        }

        public ContentValues build() {
            return values; // TODO defensive copy?
        }
    }

}
