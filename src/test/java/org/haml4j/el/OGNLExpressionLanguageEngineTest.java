package org.haml4j.el;

import static org.junit.Assert.assertEquals;
import ognl.Node;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

public class OGNLExpressionLanguageEngineTest {

	private MockServletContext servletContext;
	
	private MockHttpServletRequest request;
	
	private OgnlExpressionLanguageEngine engine;
	
	@Before
	public void setup() {
		servletContext = new MockServletContext();
		request = new MockHttpServletRequest(servletContext);
		OgnlExpressionLanguageEngineFactory elFactory = new OgnlExpressionLanguageEngineFactory();
		elFactory.initDefaultContext(servletContext);
		engine = elFactory.createEngine(request);
	}
	
	@Test
	public void testRequestAttributes() {
		request.setAttribute("foo", new Dummy());
		assertAttribute();
	}
	
	@Test
	public void testApplicationAttributes() {
		servletContext.setAttribute("foo", new Dummy());
		assertAttribute();
	}
	
	private void assertAttribute() {
		Node cexpr = engine.parse("foo.id");
		Integer id = (Integer) engine.execute(cexpr);
		assertEquals(5, id.intValue());
	}

	static class Dummy {
		
		int id = 5;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
		
	}
	
}
