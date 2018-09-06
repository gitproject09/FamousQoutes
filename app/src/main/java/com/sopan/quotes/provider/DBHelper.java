package com.sopan.quotes.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper{

    SQLiteDatabase db = null;
    Context context = null;

    static String packageName = "com.sopan.quotes";
    private static String DB_PATH = "/data/data/" + packageName + "/databases/";
    private static String DB_NAME ="quoter.db";
    private static final int VERSION = 1;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        try{
            this.context = context;
            createDataBase();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public Cursor getQuotes(int tag, int author){

        db = this.getReadableDatabase();
        String authorFilter = (author == 0)?"":" and author._id=" + author;
        String tagFilter = (tag == 0)?"":" INNER JOIN quotes_tags tag ON tag.quote=quote._id AND tag.tag=" + tag + " ";
        String sql = "SELECT quote.quote, author.author, author._id, quote._id from quotes quote, authors author " +  tagFilter + "where quote.author=author._id and quote.status=1 " + authorFilter;// + " ORDER BY RANDOM()";
        return db.rawQuery(sql, null);

    }

    public String[] getRandomQuote(){

        db = this.getReadableDatabase();
        String sql = "SELECT quote.quote, author.author, author._id, quote._id from quotes quote, authors author where quote.author=author._id and quote.status=1 ORDER BY RANDOM() LIMIT 1";
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()) {
            return new String[]{c.getString(1), c.getString(0), c.getString(2), c.getString(3)};
        }else{
            return new String[]{"", "", "", ""};
        }

    }

    public String[] getQuote(int id){

        db = this.getReadableDatabase();
        String sql = "SELECT quote.quote, author.author, author._id, quote._id from quotes quote, authors author where quote.author=author._id and quote.status=1 and quote._id=" + id;
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()) {
            return new String[]{c.getString(1), c.getString(0), c.getString(2), c.getString(3)};
        }else{
            return new String[]{"", "", "", ""};
        }

    }

    public void createDataBase() throws IOException {

        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist) {
            try  {
                copyDataBase();
                Log.e("DB", "createDatabase database created");
            }
            catch (IOException mIOException)  {
                Log.d("Error", Log.getStackTraceString(mIOException));
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException {
        InputStream mInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        File dir = new File(DB_PATH);
        if(!dir.exists()){
            dir.mkdir();
        }
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public Cursor getTags() {
        db = this.getReadableDatabase();
        String sql = "SELECT _id, tag from tags where status > 0";
        return db.rawQuery(sql, null);

    }
}
