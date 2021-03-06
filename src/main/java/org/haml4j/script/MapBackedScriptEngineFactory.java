package org.haml4j.script;

import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

/**
 * Creates a {@link ScriptEngine} backed by two Map instances (request and application context)
 * 
 * @author icoloma
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapBackedScriptEngineFactory {

	/** the JSR223 engine manager */
	private ScriptEngineManager engineManager;

	/** the name of the ScriptEngine to use */
	private String scriptEngineName;

	public MapBackedScriptEngineFactory(Map applicationContext) {
		engineManager = new ScriptEngineManager();
		if (applicationContext != null) {
			engineManager.setBindings(new SimpleBindings(applicationContext));
		}
	}

	public ScriptEngine createEngine(Map requestContext) {
		SimpleBindings requestBindings = new SimpleBindings(requestContext);
		ScriptEngine engine = engineManager.getEngineByName(scriptEngineName);
		engine.setBindings(requestBindings, ScriptContext.ENGINE_SCOPE);
		return engine;
	}

	public void setScriptEngineName(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
	}

}
