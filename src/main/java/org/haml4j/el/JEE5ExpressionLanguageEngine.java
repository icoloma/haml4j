package org.haml4j.el;

import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

/**
 * Delegates EL evaluation to the configured JSP EL resolver.
 * @author icoloma
 *
 */
public class JEE5ExpressionLanguageEngine implements ExpressionLanguageEngine {
	
	@Override
	public Object execute(String scriptlet) {
		JspApplicationContext applicationContext = JspFactory.getDefaultFactory().getJspApplicationContext(context);
		ExpressionFactory factory = applicationContext.getExpressionFactory();
		ELContext elContext = pageContext.getELContext();
		ValueExpression valueExpression = factory.createValueExpression(elContext, scriptlet, Object.class);
		return valueExpression.getValue(elContext);
	}

}
