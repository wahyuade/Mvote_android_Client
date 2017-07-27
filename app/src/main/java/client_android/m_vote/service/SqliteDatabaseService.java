package client_android.m_vote.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.util.UUID;

import client_android.m_vote.model.DptModel;
import client_android.m_vote.model.VerifyModel;

/**
 * Created by wahyuade on 20/07/17.
 */

public class SqliteDatabaseService extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "m-vote";


    private static final String TABLE_MY_DPT = "my_dpt";

    private static final String KEY_ID = "id";
    private static final String KEY_NRP = "nrp";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PRIVAT = "privat";
    private static final String KEY_N = "n";
    private static final String KEY_UUID = "x";

    public SqliteDatabaseService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_MY_DPT = "CREATE TABLE " + TABLE_MY_DPT + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NRP + " TEXT," +
                KEY_TOKEN + " TEXT," +
                KEY_PRIVAT + " TEXT," +
                KEY_N + " TEXT," +
                KEY_UUID + " TEXT )";
        sqLiteDatabase.execSQL(CREATE_MY_DPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_DPT);
        onCreate(sqLiteDatabase);
    }

    public boolean verifyDpt(VerifyModel data_dpt){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues on_verify = new ContentValues();
        on_verify.put(KEY_NRP, data_dpt.getData().getNrp());
        on_verify.put(KEY_TOKEN, data_dpt.getData().getToken());
        on_verify.put(KEY_PRIVAT, data_dpt.getData().getPrivat());
        on_verify.put(KEY_N, data_dpt.getData().getN());

        db.insert(TABLE_MY_DPT, null, on_verify);
        db.close();
        return true;
    }

    public boolean checkVerifiedDevice(){
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, TABLE_MY_DPT);
        db.close();
        if(cnt == 1){
            return true;
        }else{
            return false;
        }
    }

    public VerifyModel getVerifiedData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query(TABLE_MY_DPT, new String[]{KEY_ID, KEY_NRP, KEY_TOKEN, KEY_PRIVAT, KEY_N}, KEY_ID+"= 1",null, null, null, null);
        cur.moveToFirst();
        VerifyModel data_dpt = new VerifyModel(true, "Important Data", new DptModel(cur.getString(1),cur.getString(2),cur.getString(3),cur.getString(4)));

        return data_dpt;
    }

    public String getKeyN(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query(TABLE_MY_DPT, new String[]{KEY_N}, KEY_ID+"=1",null,null,null,null);
        cur.moveToFirst();
        db.close();
        return  cur.getString(0);
    }

    public String getKeyNrp(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query(TABLE_MY_DPT, new String[]{KEY_NRP}, KEY_ID+"=1",null,null,null,null);
        cur.moveToFirst();
        db.close();
        return  cur.getString(0);
    }

    public String getKeyPrivat(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query(TABLE_MY_DPT, new String[]{KEY_PRIVAT}, KEY_ID+"=1",null,null,null,null);
        cur.moveToFirst();
        db.close();
        return  cur.getString(0);
    }

    public String getUUID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.query(TABLE_MY_DPT, new String[]{KEY_UUID}, KEY_ID+"=1",null,null,null,null);
        cur.moveToFirst();
        db.close();
        return  cur.getString(0);
    }

    public void generateUUID(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_UUID, UUID.randomUUID().toString());
        db.update(TABLE_MY_DPT,contentValues,KEY_ID+"=1", null);
        db.close();
    }
}
