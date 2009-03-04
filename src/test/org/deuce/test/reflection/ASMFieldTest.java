package org.deuce.test.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.deuce.objectweb.asm.Type;
import org.deuce.reflection.ASMFieldFactory;
import org.deuce.reflection.BooleanField;
import org.deuce.reflection.ByteField;
import org.deuce.reflection.CharField;
import org.deuce.reflection.DoubleField;
import org.deuce.reflection.FloatField;
import org.deuce.reflection.IntField;
import org.deuce.reflection.LongField;
import org.deuce.reflection.ObjectField;
import org.deuce.reflection.ShortField;
import org.deuce.transform.asm.Loader;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test the ASM field access
 * @author Guy Korland
 */
public class ASMFieldTest extends TestCase
{
	
	/**
	 * Test Object field
	 */
	@Test
	public void testFieldAccess() throws Exception
    {
		ObjectField objectField = ASMFieldFactory.getObjectField( MyClass.class, "getField", "setField");
		MyClass mc = new MyClass();
		Object value = new Object();
		objectField.set(mc, value);
		Object get = objectField.get(mc);
		Assert.assertSame(value, get);
		
    }
	
	/**
	 * Test Object field
	 */
	@Test
	public void testBoolenaFieldAccess() throws Exception
    {
		BooleanField objectField = ASMFieldFactory.getBooleanField( MyClass.class, "getBooleanField", "setBooleanField");
		MyClass mc = new MyClass();
		objectField.set(mc, true);
		boolean get = objectField.get(mc);
		Assert.assertEquals(true, get);
    }
	
	/**
	 * Test Object field
	 */
	@Test
	public void testByteFieldAccess() throws Exception
    {
		ByteField objectField = ASMFieldFactory.getByteField( MyClass.class, "getByteField", "setByteField");
		MyClass mc = new MyClass();
		objectField.set(mc, (byte)12);
		byte get = objectField.get(mc);
		Assert.assertEquals(12, get);
    }
	
	/**
	 * Test Object field
	 */
	@Test
	public void testCharFieldAccess() throws Exception
    {
		CharField objectField = ASMFieldFactory.getCharField( MyClass.class, "getCharField", "setCharField");
		MyClass mc = new MyClass();
		objectField.set(mc, 'c');
		char get = objectField.get(mc);
		Assert.assertEquals('c', get);
    }
	
	/**
	 * Test Object field
	 */
	@Test
	public void testShortFieldAccess() throws Exception
    {
		ShortField objectField = ASMFieldFactory.getShortField( MyClass.class, "getShortField", "setShortField");
		MyClass mc = new MyClass();
		objectField.set(mc, (short)2342);
		short get = objectField.get(mc);
		Assert.assertEquals(2342, get);
    }
	
	
	/**
	 * Test Object field
	 */
	@Test
	public void testIntFieldAccess() throws Exception
    {
		IntField objectField = ASMFieldFactory.getIntField( MyClass.class, "getIntField", "setIntField");
		MyClass mc = new MyClass();
		objectField.set(mc, 1234);
		int get = objectField.get(mc);
		Assert.assertEquals(1234, get);
    }
	
	/**
	 * Test Object field
	 */
	@Test
	public void testLongFieldAccess() throws Exception
    {
		LongField objectField =  ASMFieldFactory.getLongField( MyClass.class, "getLongField", "setLongField");
		MyClass mc = new MyClass();
		objectField.set(mc, 134234234234324L);
		long get = objectField.get(mc);
		Assert.assertEquals(134234234234324L, get);
    }
	
	/**
	 * Test Object field
	 */
	@Test
	public void testFloatFieldAccess() throws Exception
    {
		FloatField objectField = ASMFieldFactory.getFloatField( MyClass.class, "getFloatField", "setFloatField");
		MyClass mc = new MyClass();
		objectField.set(mc, 1.343f);
		float get = objectField.get(mc);
		Assert.assertEquals(1.343f, get,0);
    }
	
	/**
	 * Test Object field
	 */
	@Test
	public void testDoubleFieldAccess() throws Exception
    {
		DoubleField objectField = ASMFieldFactory.getDoubleField( MyClass.class, "getDoubleField", "setDoubleField");
		MyClass mc = new MyClass();
		objectField.set(mc, 12.4234453432);
		double get = objectField.get(mc);
		Assert.assertEquals(12.4234453432, get,0);
    }
	
	
	
	public static class MyClass
	{
		private Object field;
	    private int boolenaField;
	    private boolean booleanField;
	    private byte byteField;
	    private char charField;
	    private short shortField;
	    private int intField;
	    private long longField;
	    private float floatField;
	    private double doubleField;
		
	    static public byte getByteField(MyClass mc) {
			return mc.byteField;
		}
	    static public void setByteField(MyClass mc, byte byteField) {
	    	mc.byteField = byteField;
		}
	    static public char getCharField(MyClass mc) {
			return mc.charField;
		}
	    static public void setCharField(MyClass mc,char charField) {
			mc.charField = charField;
		}
	    static public short getShortField(MyClass mc) {
			return mc.shortField;
		}
	    static public void setShortField(MyClass mc,short shortField) {
	    	mc.shortField = shortField;
		}
	    static public int getIntField(MyClass mc) {
			return mc.intField;
		}
	    static public void setIntField(MyClass mc,int intField) {
	    	mc.intField = intField;
		}
	    static public long getLongField(MyClass mc) {
			return mc.longField;
		}
		static public void setLongField(MyClass mc,long longField) {
			mc.longField = longField;
		}
		static public float getFloatField(MyClass mc) {
			return mc.floatField;
		}
		static public void setFloatField(MyClass mc,float floatField) {
			mc.floatField = floatField;
		}
		static public double getDoubleField(MyClass mc) {
			return mc.doubleField;
		}
		static public void setDoubleField(MyClass mc,double doubleField) {
			mc.doubleField = doubleField;
		}
		
		static public void setField(MyClass mc, Object field) {
			mc.field = field;
		}
		static public Object getField(MyClass mc) {
			return mc.field;
		}
		static public void setBoolenaField(MyClass mc, int boolenaField) {
			mc.boolenaField = boolenaField;
		}
		static public int getBoolenaField(MyClass mc) {
			return mc.boolenaField;
		}
		static public void setBooleanField(MyClass mc,boolean booleanField) {
			mc.booleanField = booleanField;
		}
		static public boolean getBooleanField(MyClass mc) {
			return mc.booleanField;
		}
	    
	}
	
	private static Object newInstance(String className, Class clzz) throws Exception 
	{
		return clzz.newInstance();
	}
	
	static private class MYClassLoader extends ClassLoader{
		public Class defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
	}

}
