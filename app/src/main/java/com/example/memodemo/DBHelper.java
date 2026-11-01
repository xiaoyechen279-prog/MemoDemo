package com.example.memodemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "memo_db";
    public static final String TABLE_MEMO = "memo";
    // 表字段
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "is_important"; // 重要标记

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 2); // 版本号升级为2！
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表，新增is_important，0=false，1=true
        String sql = "CREATE TABLE " + TABLE_MEMO + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT,"
                + COL_CONTENT + " TEXT,"
                + COL_IMPORTANT + " INTEGER DEFAULT 0)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级，给旧表增加字段
        if(oldVersion == 1){
            db.execSQL("ALTER TABLE "+TABLE_MEMO+" ADD COLUMN "+COL_IMPORTANT+" INTEGER DEFAULT 0");
        }
    }
}

