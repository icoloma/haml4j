package org.haml4j.el;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.servlet.ServletContext;

import org.haml4j.util.Enumerations;

public class ServletContextBindings implements Bindings {

	private ServletContext servletContext;
	
	@Override
	public int size() {
	    return Enumerations.size(servletContext.getAttributeNames());
	}

	@Override
	public boolean isEmpty() {
		return !servletContext.getAttributeNames().hasMoreElements();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		TODP seguir here
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object put(String name, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

}
