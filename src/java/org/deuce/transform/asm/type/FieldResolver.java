package org.deuce.transform.asm.type;

import java.lang.Class;
import java.lang.Exception;
import java.lang.String;
import java.lang.System;
import java.lang.reflect.Field;
import java.util.Vector;

import org.deuce.reflection.UnsafeHolder;

public class FieldResolver {
	
	public final static Vector<FieldResolver> resolvers = new Vector<FieldResolver> ();
	
	private final java.lang.String name;
	private final Class<?> class1;
	public FieldResolver(Class<?> class1, String name){
		this.class1 = class1;
		this.name = name;
	}
	
	public static void add(Class<?> class1, String name){
		resolvers.add(new FieldResolver(class1, name));
	}
	
	public static void process(){
		for( FieldResolver resolver : resolvers){
			try {
				Field fieldAdrr = resolver.class1.getDeclaredField(resolver.name + "_address");
				Field field = resolver.class1.getDeclaredField(resolver.name);
				long off = UnsafeHolder.getUnsafe().objectFieldOffset( field);
				fieldAdrr.set(null, off);
				System.out.println( String.hash_address);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
}
