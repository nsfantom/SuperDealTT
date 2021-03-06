/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tm.fantom.superdealtt.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private static final String CREATE_ORGS = ""
            + "CREATE TABLE " + OrgItem.TABLE + "("
            + OrgItem.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + OrgItem.NAME + " TEXT UNIQUE NOT NULL,"
            + OrgItem.BLOG + " TEXT,"
            + OrgItem.LOCATION + " TEXT,"
            + OrgItem.AVATAR + " TEXT"
            + ")";

    public DbOpenHelper(Context context) {
        super(context, "perdeal.db", null /* factory */, VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ORGS);
//        db.execSQL(CREATE_STATISTICS);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
