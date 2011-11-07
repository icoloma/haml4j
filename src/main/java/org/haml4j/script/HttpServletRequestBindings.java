package org.haml4j.script;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.haml4j.util.Enumerations;

import com.google.common.collect.Sets;

/**
 * Adapter that transforms {@link HttpServletRequest} attributes into a {@link Bindings} interface.
 * This implementation is not efficient if anything else than put(), get() and remove() is invoked.
 * In that case, an alternate implementation based on {@link ServletRequestListener} should be considered instead.
 * @author icoloma
 *
 */
public class HttpServletRequestBindings implements Bindings {

	private HttpServletRequest servletRequest;
	
	public HttpServletRequestBindings(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	@Override
	public int size() {
	    return Enumerations.size(servletRequest.getAttributeNames());
	}

	@Override
	public boolean isEmpty() {
		return !servletRequest.getAttributeNames().hasMoreElements();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		for (Enumeration<String> e = servletRequest.getAttributeNames(); e.hasMoreElements(); ) {
			servletRequest.removeAttribute(e.nextElement());
		}
	}

	@Override
	public Set<String> keySet() {
		// ouch
		Set<String> keys = Sets.newHashSet();
		for (Enumeration<String> e = servletRequest.getAttributeNames(); e.hasMoreElements(); ) {
			keys.add(e.nextElement());
		}
		return keys;
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object put(String name, Object value) {
		Object oldValue = servletRequest.getAttribute(name);
		servletRequest.setAttribute(name, value);
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		for (java.util.Map.Entry<? extends String, ? extends Object> entry : toMerge.entrySet()) {
			servletRequest.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return servletRequest.getAttribute(key.toString()) != null;
	}

	@Override
	public Object get(Object key) {
		return servletRequest.getAttribute(key.toString());
	}

	@Override
	public Object remove(Object key) {
		String name = key.toString();
		Object oldValue = servletRequest.getAttribute(name);
		servletRequest.removeAttribute(name);
		return oldValue;
	}

}
