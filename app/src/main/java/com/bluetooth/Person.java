package com.bluetooth;

public class Person
{
	public int _id;  
    public String name;
    public int age;  
    public String info;
    public Person() {  
    }
    public Person(String name, int age, String info)
    {  
        this.name = name;  
        this.age = age;  
        this.info = info;  
    } 
    public int getID(){
        return this._id;
    }
    
    public void setID(int id){
        this._id = id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getPhoneNumber(){
        return this.info;
    }
    public void setPhoneNumber(String info){
        this.info =info;
    }
}
