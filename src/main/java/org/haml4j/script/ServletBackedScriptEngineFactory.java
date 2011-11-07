package org.haml4j.script;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Creates a {@link ScriptEngine} backed by the current servlet request and
 * context
 * 
 * @author icoloma
 * 
 */
public class ServletBackedScriptEngineFactory {

	/** the JSR223 engine manager */
	private ScriptEngineManager engineManager;

	/** the name of the ScriptEngine to use */
	private String scriptEngineName;

	public ServletBackedScriptEngineFactory(ServletContext servletContext) {
		engineManager = new ScriptEngineManager();
		engineManager.setBindings(new ServletContextBindings(servletContext));
	}

	public ScriptEngine createEngine(HttpServletRequest servletRequest) {
		HttpServletRequestBindings requestBindings = new HttpServletRequestBindings( servletRequest);
		ScriptEngine engine = engineManager.getEngineByName(scriptEngineName);
		engine.setBindings(requestBindings, ScriptContext.ENGINE_SCOPE);
		return engine;
	}

	public void setScriptEngineName(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
	}

}
