package com.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager
{
    private DBHelper helper;
    private SQLiteDatabase db;
    public DBManager(Context context)
    {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }
    // batch insert
    public void add(List<Person> persons)
    {
        db.beginTransaction();  //开始事务
        try {
            for (Person person : persons) {
                db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?)", new Object[]{person.name, person.age, person.info});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    // select
    public void select(Person person)
    {
        db.execSQL("select name from person where name = ?", new Object[]{person.name});
    }

    //update person's age
    public void updateAge(Person person)
    {
        ContentValues cv = new ContentValues();
        cv.put("age", person.age);
        db.update("person", cv, "name = ?", new String[]{person.name});
    }

    //updatePerson
    public void updatePerson(Person person)
    {
        ContentValues cv = new ContentValues();
        cv.put("age", person.age);
        cv.put("info", person.info);
        db.update("person", cv, "name = ?", new String[]{person.name});
    }

    // delete old person
    public void deleteOldPerson(Person person)
    {
        db.delete("person", "age >= ?", new String[]{String.valueOf(person.age)});
    }
    public void deletePersonAge(int age)
    {
        db.delete("person", "age == ?", new String[]{String.valueOf(age)});
    }
    public void deletePerson(Person person)
    {
        db.delete("person", "name = ?", new String[]{person.name});
    }

    //query all persons, return list
    public List<Person> query() {
        ArrayList<Person> persons = new ArrayList<Person>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            Person person = new Person();
            person._id = c.getInt(c.getColumnIndex("_id"));
            person.name = c.getString(c.getColumnIndex("name"));
            person.age = c.getInt(c.getColumnIndex("age"));
            person.info = c.getString(c.getColumnIndex("info"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public Person findName(String name)
    {
        Person person =null;
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            if (name.equals(c.getString(c.getColumnIndex("name"))))
            {
                person._id = c.getInt(c.getColumnIndex("_id"));
                person.name = c.getString(c.getColumnIndex("name"));
                person.age = c.getInt(c.getColumnIndex("age"));
                person.info = c.getString(c.getColumnIndex("info"));
            }
        }
        c.close();
        return person;
    }

    //query all persons, return cursor
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM person", null);
        return c;
    }

    //close database
    public void closeDB() {
        db.close();
    }
}
