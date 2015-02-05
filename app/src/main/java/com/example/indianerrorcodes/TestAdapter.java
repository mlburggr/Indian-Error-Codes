package com.example.indianerrorcodes;

import java.io.IOException;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TestAdapter 
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public TestAdapter(Context context) 
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public TestAdapter createDatabase() throws SQLException 
    {
        try 
        {
            mDbHelper.createDataBase();
        } 
        catch (IOException mIOException) 
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public TestAdapter open() throws SQLException 
    {
        try 
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } 
        catch (SQLException mSQLException) 
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() 
    {
        mDbHelper.close();
    }

     public Cursor getTestData()
     {
         try
         {
             String sql ="SELECT * FROM IndianErrorCodes2014";

             Cursor mCur = mDb.rawQuery(sql, null);
             if (mCur!=null)
             {
                mCur.moveToNext();
             }
             return mCur;
         }
         catch (SQLException mSQLException) 
         {
             Log.e(TAG, "getTestData >>"+ mSQLException.toString());
             throw mSQLException;
         }
     }
     public Cursor getFromSPNandFMI(int spn, int fmi)
     {
    	 try
         {
             String sql ="SELECT * FROM IndianErrorCodes2014 WHERE SPN='"+spn+"' AND FMI='"+fmi+"'";

             Cursor mCur = mDb.rawQuery(sql, null);
             if (mCur!=null)
             {
                mCur.moveToFirst();
             }
             return mCur;
         }
         catch (SQLException mSQLException) 
         {
             Log.e(TAG, "getFromSPNandFMI >>"+ mSQLException.toString());
             throw mSQLException;
         }
     }
     public Cursor getPossibleFMIsfromSPN(int spn)
     {
    	 try{
    	 String sql = "SELECT FMI FROM IndianErrorCodes2014 WHERE SPN='"+spn+"'";
    	 Cursor mCur = mDb.rawQuery(sql, null);
    	 if(mCur!=null)
    	 {
    		 mCur.moveToFirst();
    	 }
    	 return mCur;
    	 }catch(SQLException mSQLException)
    	 {
    		 Log.e(TAG, "getPossibleFMIsfromSPN >>"+ mSQLException.toString());
             throw mSQLException;
    	 }
     }
     public int[] convertFMIstoString(Cursor cursor){
  		// Reset cursor to start, checking to see if there's data:
  		int rowCount = cursor.getCount();
  		int[] fmiArray = new int[rowCount];
  		for(int i=0;i<rowCount;i++){
  			fmiArray[i]=cursor.getInt(0);
  			cursor.moveToNext();
  		}
  		
  		return fmiArray;
     }
     public String convertRowtoString(Cursor cursor) {
 		String message = "";
 		// Reset cursor to start, checking to see if there's data:
 		if (cursor.moveToFirst()) {
 			do {
 				// Process the data:
 				int spn = cursor.getInt(1);
 				String component = cursor.getString(2);
 				int fmi = cursor.getInt(3);
 				String condition = cursor.getString(4);
 				String mil = cursor.getString(5);
 				
 				// Append data to the message:
 				message += 
 						   " <b>SPN:</b> " + spn
 						   +"<br> <b>FMI:</b> " + fmi
 						   +"<br> <b>Component:</b> " + component
 						   +"<br> <b>Condition:</b> " + condition
 						   +"<br> <b>MIL:</b> "+mil
 						   +"<br>";
 			} while(cursor.moveToNext());
 		}
 		
 		
 		//cursor.close();
 		//i sure do love java garbage collection
 		return message;
 	}
}