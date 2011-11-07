package org.haml4j.model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.haml4j.core.Context;
import org.haml4j.core.HtmlWriter;

import com.google.common.collect.Lists;

/**
 * A String that contains parameters #{...}
 * @author icoloma
 *
 */
public class Text implements Renderizable {

	private static Pattern parameterPattern = Pattern.compile("#\\{([^}]+)}");
	
	private Part[] parts;
	
	public Text(String contents) {
		List<Part> parts = Lists.newArrayList();
		int index = 0;
		Matcher m = parameterPattern.matcher(contents);
		while (m.find()) {
			if (index < m.start()) {
				parts.add(new Part(contents.substring(index, m.start()), false));
			}
			parts.add(new Part(m.group(1), true));
			index = m.end();
		}
		if (index < contents.length()) {
			parts.add(new Part(contents.substring(index), false));
		}
		this.parts = parts.toArray(new Part[parts.size()]);
	}
	
	public Text(String contents, boolean evaluate) {
		this.parts = new Part[] { new Part(contents, evaluate) };
	}
	
	@Override
	public void render(Context context) throws ScriptException {
		HtmlWriter writer = context.getWriter();
		ScriptEngine engine = context.getScriptEngine();
		for (Part part : parts) {
			writer.print(part.isEvaluate()? engine.eval(part.getText()) : part.getText());
		}
	}
	
	private static class Part {
		
		private String text;
		
		private boolean evaluate;

		public Part(String text, boolean evaluate) {
			this.text = text;
			this.evaluate = evaluate;
		}

		public String getText() {
			return text;
		}

		public boolean isEvaluate() {
			return evaluate;
		}
		
	}
	
}
