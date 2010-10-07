package org.deuce;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
* Used to mark a method as Irrevocable, in this case 
* a transaction context will be informed on accessing an Irrevocable method.
*  
* @author Guy Korland
* @since 2.0
*/
@Target(METHOD)
@Retention(CLASS)
public @interface Irrevocable {

}
