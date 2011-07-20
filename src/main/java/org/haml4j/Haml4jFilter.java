package org.haml4j;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class Haml4jFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO
	}
	
	@Override
	public void destroy() {
		// empty on purpose
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		// empty on purpose
	}

	
	
}
