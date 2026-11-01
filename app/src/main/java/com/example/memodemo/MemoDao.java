package com.example.memodemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class MemoDao {
    private DBHelper helper;

    public MemoDao(Context context) {
        helper = new DBHelper(context);
    }

    // 添加备忘录，带重要标记
    public long addMemo(String title, String content, boolean isImportant) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_TITLE, title);
        cv.put(DBHelper.COL_CONTENT, content);
        cv.put(DBHelper.COL_IMPORTANT, isImportant ? 1 : 0);
        long id = db.insert(DBHelper.TABLE_MEMO, null, cv);
        db.close();
        return id;
    }

    // 更新备忘录
    public int updateMemo(long id, String title, String content, boolean isImportant) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_TITLE, title);
        cv.put(DBHelper.COL_CONTENT, content);
        cv.put(DBHelper.COL_IMPORTANT, isImportant ? 1 : 0);
        int rows = db.update(DBHelper.TABLE_MEMO, cv, DBHelper.COL_ID + "=?", new String[]{id + ""});
        db.close();
        return rows;
    }

    // 删除备忘录
    public int deleteMemo(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete(DBHelper.TABLE_MEMO, DBHelper.COL_ID + "=?", new String[]{id + ""});
        db.close();
        return rows;
    }

    // 查询全部备忘录
    public List<Memo> queryAllMemo() {
        return queryMemoByKey("");
    }

    // 【搜索功能】根据关键词搜索标题/内容
    public List<Memo> queryMemoByKey(String keyword) {
        List<Memo> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        if(keyword.isEmpty()){
            cursor = db.query(DBHelper.TABLE_MEMO, null, null, null, null, null, DBHelper.COL_IMPORTANT + " DESC, " + DBHelper.COL_ID + " DESC");
        }else{
            // 模糊查询，匹配标题或内容
            cursor = db.query(DBHelper.TABLE_MEMO, null,
                    DBHelper.COL_TITLE + " LIKE ? OR " + DBHelper.COL_CONTENT + " LIKE ?",
                    new String[]{"%"+keyword+"%", "%"+keyword+"%"},
                    null, null, DBHelper.COL_IMPORTANT + " DESC, " + DBHelper.COL_ID + " DESC");
        }
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CONTENT));
            boolean important = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_IMPORTANT)) == 1;
            list.add(new Memo(id, title, content, important));
        }
        cursor.close();
        db.close();
        return list;
    }
}
