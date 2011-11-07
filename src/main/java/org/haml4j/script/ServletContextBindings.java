package org.haml4j.script;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;

import org.haml4j.util.Enumerations;

import com.google.common.collect.Sets;

/**
 * Adapter that transforms {@link ServletContext} attributes into a {@link Bindings} interface.
 * This implementation is not efficient if anything else than put(), get() and remove() is invoked.
 * In that case, an alternate implementation based on {@link ServletContextListener} should be considered instead.
 * @author icoloma
 *
 */
public class ServletContextBindings implements Bindings {

	private ServletContext servletContext;
	
	public ServletContextBindings(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

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
		for (Enumeration<String> e = servletContext.getAttributeNames(); e.hasMoreElements(); ) {
			servletContext.removeAttribute(e.nextElement());
		}
	}

	@Override
	public Set<String> keySet() {
		// ouch
		Set<String> keys = Sets.newHashSet();
		for (Enumeration<String> e = servletContext.getAttributeNames(); e.hasMoreElements(); ) {
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
		Object oldValue = servletContext.getAttribute(name);
		servletContext.setAttribute(name, value);
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		for (java.util.Map.Entry<? extends String, ? extends Object> entry : toMerge.entrySet()) {
			servletContext.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return servletContext.getAttribute(key.toString()) != null;
	}

	@Override
	public Object get(Object key) {
		return servletContext.getAttribute(key.toString());
	}

	@Override
	public Object remove(Object key) {
		String name = key.toString();
		Object oldValue = servletContext.getAttribute(name);
		servletContext.removeAttribute(name);
		return oldValue;
	}

}
