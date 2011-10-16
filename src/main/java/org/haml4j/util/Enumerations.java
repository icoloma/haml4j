package org.haml4j.util;

import java.util.Enumeration;

public class Enumerations {

	public static int size(Enumeration iterator) {
		int count = 0;
	    while (iterator.hasMoreElements()) {
	      iterator.nextElement();
	      count++;
	    }
	    return count;
	}
	
}
