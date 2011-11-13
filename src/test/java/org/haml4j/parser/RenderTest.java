package org.haml4j.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.haml4j.core.StringBackedContext;
import org.haml4j.exception.ParseException;
import org.haml4j.model.Document;
import org.haml4j.script.MapBackedScriptEngineFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

/**
 * Inspired by template_test.rb
 * @author icoloma
 *
 */
public class RenderTest {

	private static String[] filenames = new String[] { 
		"simple", "very_basic", "standard", "helpers", "whitespace_handling",
		"original_engine", "list", "helpful", "silent_script", "tag_parsing", "just_stuff  partials", 
		"filters", "nuke_outer_whitespace", "nuke_inner_whitespace", "render_layout",
		"partial_layout"
	};

	private ParserFactory factory;

	private ScriptEngine scriptEngine;

	private static Logger log = LoggerFactory.getLogger(RenderTest.class);
	
	private Map<String, Object> requestContext;

	/* todo:
Haml::Template.options[:ugly] = true
    render @base, 'haml/templates/standard_ugly'
    haml_ugly { render @base, 'haml/templates/standard_ugly' }
  end

  report "ActionView with deep partials" do
    @base = view

    @base.unmemoize_all
    Haml::Template.options[:ugly] = false
    # To cache the template
    render @base, 'haml/templates/action_view'
    render @base, 'haml/erb/action_view'

    haml { render @base, 'haml/templates/action_view' }
    erb  { render @base, 'haml/erb/action_view' }

    Haml::Template.options[:ugly] = true
    render @base, 'haml/templates/action_view_ugly'
    haml_ugly { render @base, 'haml/templates/action_view_ugly' }
  end
	 */
	
	@Before
	public void setupParserFactory() {
		factory = new ParserFactory();
		factory.setDoctypeHandler(new XmlDoctypeHandler());
		MapBackedScriptEngineFactory factory = new MapBackedScriptEngineFactory(
				null);
		factory.setScriptEngineName("groovy");
		HashMap<Object, Object> map = Maps.newHashMap();
		map.put("item", Maps.newHashMap());
		scriptEngine = factory.createEngine(map);
		requestContext = Maps.newHashMap();
	}
	
	protected StringBackedContext initContext() {
		StringBackedContext context = new StringBackedContext();
		context.setIndentChars("  ");
		context.setAttributeWrapper('\'');
		context.setScriptEngine(scriptEngine);
		return context;
	}

	@Test
	public void testRenderFiles() throws Exception {
		for (String filename: filenames) {
			log.info("Reading " + filename);

			StringBackedContext context = initContext();

			String contents = readFile("src/test/resources/templates/" + filename + ".haml");
			Document document = factory.createParser().parse(contents);
			document.render(context);
			String expected = readFile("src/test/resources/results/" + filename + ".html");
			assertEquals(expected, context.getContents());
		}
	}
	
	@Test
	public void testSnippets() throws Exception {
		
		assertRender("", "");
		assertRender("2\n", "= 1+1");
		
		assertRender("<p>something</p>", "%p= content_for_layout", "content_for_layout", "something");
		
	    assertRender("<p class='hcatlin'>foo</p>", "%p{:class => @author} foo", "author", "hcatlin");
	    
	    assertRender("<p>\n  foo\n</p>\n<q>\n  bar\n  <a>\n    baz\n  </a>\n</q>\n", "%p\n foo\n%q\n bar\n %a\n  baz");
	    assertRender("<p>\n  foo\n</p>\n<q>\n  bar\n  <a>\n    baz\n  </a>\n</q>\n", "%p\n\tfoo\n%q\n\tbar\n\t%a\n\t\tbaz");
	    assertRender("<p>\n      \t \t bar\n   baz\n</p>\n", "%p\n  :plain\n        \t \t bar\n     baz");
	    
	    // strings_should_get_stripped_inside_tags
	    assertRender("<div class='stripped'>This should have no spaces in front of it</div>", ".stripped    This should have no spaces in front of it");
	    
	    // one_liner_should_be_one_line
	    assertRender("<p>Hello</p>", "%p Hello");
	    
	    // one_liner_with_newline_shouldnt_be_one_line
	    assertRender("<p>\n  foo\n  bar\n</p>", "%p= \"foo\nbar\"");
	    
	    // CR
		assertRender("<p>foo</p>\n<p>bar</p>\n<p>baz</p>\n<p>boom</p>\n", "%p foo\r%p bar\r\n%p baz\n\r%p boom");
		assertRender("\\n", ":plain\n\t\\n\\#{\"\"}");

	}

	@Test
	public void testFor() throws Exception {
		assertRender("<p>0</p>\n<p>1</p>\n<p>2</p>\n", "- for i in (0...3)\n  %p= |\n   i |");
	}

	@Test
	public void testTextAreas() throws Exception {
	    assertRender("<textarea>Foo&#x000A;  bar&#x000A;   baz</textarea>\n", "%textarea= \"Foo\n  bar\n   baz\"");
	    assertRender("<pre>Foo&#x000A;  bar&#x000A;   baz</pre>\n", "%pre= \"Foo\n  bar\n   baz\"");
	    assertRender("<textarea>#{'a' * 100}</textarea>\n", "%textarea #{'a' * 100}");
	    assertRender("<p>\n  <textarea>Foo\n  Bar\n  Baz</textarea>\n</p>\n", "%p\n\t%textarea\n\t\tFoo\n\t\tBar\n\t\tBaz\n");
	    assertRender("<pre><code>Foo&#x000A;  bar&#x000A;    baz</code></pre>", "%pre\n\t%code\n\t\t:preserve\n\t\t\tFoo\n\t\t\t\tbar\n\t\t\t\t\tbaz");
	}
	
	@Test
	public void testInterpolation() throws Exception {
		
		assertRender("<p>Hello World</p>\n", "%p Hello #{who}", "who", "World");
		assertRender("<p>\n  Hello World\n</p>\n", "%p\n  Hello \\#{who}", "who", "World");
		
		// interpolation_in_the_middle_of_a_string
	    assertRender("\"title 'Title'. \"\n", "\"title '#{\"Title\"}'. \"");
		
	    // interpolation_at_the_beginning_of_a_line
	    assertRender("<p>2</p>\n", "%p #{1 + 1}");
	    assertRender("<p>\n  2\n</p>\n", "%p\n  #{1 + 1}");
	    
	    // escaped_interpolation
	    assertRender("<p>Foo &amp; Bar & Baz</p>\n", "%p& Foo #{\"&\"} Bar & Baz");
	    
	    // nil_tag_value_should_render_as_empty
	    assertRender("<p></p>\n", "%p= nil");
	}
	
	@Test
	public void testIf() throws Exception {
		// tag_with_failed_if_should_render_as_empty
	    assertRender("<p></p>\n", "%p= 'Hello' if false");
	    assertRender("<p>One</p><p></p><p>Three</p>", "- for name in [\"One\", \"Two\", \"Three\"]\n\t%p= name unless name == \"Two\"");
	    
	    // if_without_content_and_else
	    assertRender("foo", "- if false\n- else\n\tfoo\n");
	    assertRender("foo", "- if true\n\t- if false\n\t- else\n\tfoo");
	    
	    // if_assigned_to_var
		assertRender("foo", "- var = if false\n- else\n\t- \"foo\"\n= var");
		assertRender("foo", "- var = if false\n- elsif 12\n\t- \"foo\"\n- elsif 14; \"bar\"\n- else\n\t- \"baz\"\n= var");
		assertRender("foo", "- var = if false\n\t- \"bar\"\n- else\n\t- \"foo\"\n= var");

	}
	 
	@Test
	public void testMethodCall() throws Exception {
		assertRender("2|3|4\nb-a-r", "= [1, 2, 3].map do |i|\n\t- i + 1\n- end.join(\"|\")\n= \"bar\".gsub(/./) do |s|\n\t- s + \"-\"\n- end.gsub(/-$/) do |s|\n\t- ''");
		assertRender("e\nd\nc\nb\na", "- str = \"abcde\"\n- if true\n\t= str.slice!(-1).chr\n- end until str.empty?");
		assertRender("<p>foo-end</p>\n<p>bar-end</p>", "- (\"foo-end-bar-end\".gsub(/\\\\w+-end/) do |s|\n\t%p= s\n- end; nil");
	}

	@Test
	public void testHtmlAttributes() throws Exception {
		// dynamic_attributes_with_empty_attr
		assertRender("<img alt='' src='/foo.png' />\n", "%img{:width => nil, :src => '/foo.png', :alt => String.new}");
		assertRender("<a href='#' rel='top'>Foo</a>\n", "%a(href=\"#\" rel=\"top\") Foo");
		assertRender("<a href='#'>Foo</a>\n", "%a(href=\"#\") #{\"Foo\"}");
		assertRender("<a href='#\"'></a>\n", "%a(href=\"#\\\"\")");
	}
	  
	@Test
	public void testHashAttributes() throws Exception {
		def test_attrs_parsed_correctly
	    assert_equal("<p boom=>biddly='bar =&gt; baz'></p>\n", render("%p{'boom=>biddly' => 'bar => baz'}"))
	    assert_equal("<p foo,bar='baz, qux'></p>\n", render("%p{'foo,bar' => 'baz, qux'}"))
	    assert_equal("<p escaped='quo&#x000A;te'></p>\n", render("%p{ :escaped => \"quo\\nte\"}"))
	    assert_equal("<p escaped='quo4te'></p>\n", render("%p{ :escaped => \"quo\#{2 + 2}te\"}"))
	  end

	  def test_correct_parsing_with_brackets
	    assert_equal("<p class='foo'>{tada} foo</p>\n", render("%p{:class => 'foo'} {tada} foo"))
	    assert_equal("<p class='foo'>deep {nested { things }}</p>\n", render("%p{:class => 'foo'} deep {nested { things }}"))
	    assert_equal("<p class='bar foo'>{a { d</p>\n", render("%p{{:class => 'foo'}, :class => 'bar'} {a { d"))
	    assert_equal("<p foo='bar'>a}</p>\n", render("%p{:foo => 'bar'} a}"))
	    
	    foo = []
	    foo[0] = Struct.new('Foo', :id).new
	    assert_equal("<p class='struct_foo' id='struct_foo_new'>New User]</p>\n",
	                 render("%p[foo[0]] New User]", :locals => {:foo => foo}))
	    assert_equal("<p class='prefix_struct_foo' id='prefix_struct_foo_new'>New User]</p>\n",
	                 render("%p[foo[0], :prefix] New User]", :locals => {:foo => foo}))

	    foo[0].id = 1
	    assert_equal("<p class='struct_foo' id='struct_foo_1'>New User]</p>\n",
	                 render("%p[foo[0]] New User]", :locals => {:foo => foo}))
	    assert_equal("<p class='prefix_struct_foo' id='prefix_struct_foo_1'>New User]</p>\n",
	                 render("%p[foo[0], :prefix] New User]", :locals => {:foo => foo}))
	  end
	  
	  def test_empty_attrs
	    assert_equal("<p attr=''>empty</p>\n", render("%p{ :attr => '' } empty"))
	    assert_equal("<p attr=''>empty</p>\n", render("%p{ :attr => x } empty", :locals => {:x => ''}))
	  end
	  
	  def test_nil_attrs
	    assert_equal("<p>nil</p>\n", render("%p{ :attr => nil } nil"))
	    assert_equal("<p>nil</p>\n", render("%p{ :attr => x } nil", :locals => {:x => nil}))
	  end
		
	    assertRender("<div class='atlantis' style='ugly'></div>", ".atlantis{:style => 'ugly'}");
	    
	    assertRender("<div id='my_id_1'></div>", "#my_id{:id => '1'}");
	    assertRender("<div id='my_id_1'></div>", "#my_id{:id => 1}");
	    
	    requestContext.put("author", "hcatlin");
	    assertRender("<p class='3'>foo</p>", "%p{:class => 1+2} foo");
	    
		// colon in class or id
	    assertRender("<p class='foo:bar' />\n", "%p.foo:bar/");
		assertRender("<p id='foo:bar' />\n", "%p#foo:bar/");
		
		// attributes_with_to_s
		assertRender("<p id='foo_2'></p>", "%p#foo{:id => 1+1}");
		assertRender("<p class='2 foo'></p>", "%p.foo{:class => 1+1}");
		assertRender("<p blaz='2'></p>", "%p{:blaz => 1+1}");
		assertRender("<p 2='2'></p>", "%p{(1+1) => 1+1}");
		
		// static_attributes_with_empty_attr
	    assertRender("<img alt='' src='/foo.png' />\n", "%img{:src => '/foo.png', :alt => ''}");
	    
	    // attribute_hash_with_newlines
	    assertRender("<p a='b' c='d'>foop</p>\n", "%p{:a => 'b',\n   :c => 'd'} foop");
	    assertRender("<p a='b' c='d'>\n  foop\n</p>\n", "%p{:a => 'b',\n   :c => 'd'}\n  foop");
	    assertRender("<p a='b' c='d' />\n", "%p{:a => 'b',\n   :c => 'd'}/");
	    assertRender("<p a='b' c='d' e='f'></p>\n", "%p{:a => 'b',\n   :c => 'd',\n   :e => 'f'}");
	    
	    // attr_hashes_not_modified
		assertRender("<div color='red'></div>\n<div class='special' color='red'></div>\n<div color='red'></div>", 
				"%div{hash}\n.special{hash}\n%div{hash}", 
				"hash", ImmutableMap.of("color", "red"));
		
	}
	
	@Test
	public void testEscape() throws Exception {
		// ampersand_equals_should_escape
		assertRender("<p>\n  foo &amp; bar\n</p>\n", "%p\n  &= 'foo & bar'"); // , :escape_html => false))
		
		// ampersand_equals_inline_should_escape
		assertRender("<p>foo &amp; bar</p>\n", "%p&= 'foo & bar'"); //, :escape_html => false))

		// ampersand_equals_should_escape_before_preserve
		assertRender("<textarea>foo&#x000A;bar</textarea>\n", "%textarea&= \"foo\nbar\""); //, :escape_html => false))

		// bang_equals_should_not_escape
		assertRender("<p>\n  foo & bar\n</p>\n", "%p\n  != 'foo & bar'"); // , :escape_html => true))

		// bang_equals_inline_should_not_escape
		assertRender("<p>foo & bar</p>\n", "%p!= 'foo & bar'"); //, :escape_html => true))
		  
		// static_attributes_should_be_escaped
		assertRender("<img class='atlantis' style='ugly&amp;stupid' />\n", "%img.atlantis{:style => 'ugly&stupid'}");
		assertRender("<div class='atlantis' style='ugly&amp;stupid'>foo</div>\n", ".atlantis{:style => 'ugly&stupid'} foo");
		assertRender("<p class='atlantis' style='ugly&amp;stupid'>foo</p>\n", "%p.atlantis{:style => 'ugly&stupid'}= 'foo'");
		assertRender("<p class='atlantis' style='ugly&#x000A;stupid'></p>\n", "%p.atlantis{:style => \"ugly\\nstupid\"}");
		
		// dynamic_attributes_should_be_escaped
		assertRender("<img alt='' src='&amp;foo.png' />\n", "%img{:width => nil, :src => '&foo.png', :alt => String.new}");
		assertRender("<p alt='' src='&amp;foo.png'>foo</p>\n", "%p{:width => nil, :src => '&foo.png', :alt => String.new} foo");
		assertRender("<div alt='' src='&amp;foo.png'>foo</div>\n", "%div{:width => nil, :src => '&foo.png', :alt => String.new}= 'foo'");
		assertRender("<img alt='' src='foo&#x000A;.png' />\n", "%img{:width => nil, :src => \"foo\\n.png\", :alt => String.new}");

		// string_double_equals_should_be_esaped
		assertRender("<p>4&&lt;</p>\n", "%p== \\#{2+2}&\\#{'<'}"); // , :escape_html => true))
		assertRender("<p>4&<</p>\n", "%p== \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// escaped_inline_string_double_equals
		assertRender("<p>4&&lt;</p>\n", "%p&== \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>4&&lt;</p>\n", "%p&== \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// unescaped_inline_string_double_equals
		assertRender("<p>4&<</p>\n", "%p!== \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>4&<</p>\n", "%p!== \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// escaped_string_double_equals
		assertRender("<p>\n  4&&lt;\n</p>\n", "%p\n  &== \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>\n  4&&lt;\n</p>\n", "%p\n  &== \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// unescaped_string_double_equals
		assertRender("<p>\n  4&<\n</p>\n", "%p\n  !== \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>\n  4&<\n</p>\n", "%p\n  !== \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// string_interpolation_should_be_esaped
		assertRender("<p>4&&lt;</p>\n", "%p \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>4&<</p>\n", "%p \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// escaped_inline_string_interpolation
		assertRender("<p>4&&lt;</p>\n", "%p& \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>4&&lt;</p>\n", "%p& \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// unescaped_inline_string_interpolation
		assertRender("<p>4&<</p>\n", "%p! \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>4&<</p>\n", "%p! \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// escaped_string_interpolation
		assertRender("<p>\n  4&&lt;\n</p>\n", "%p\n  & \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>\n  4&&lt;\n</p>\n", "%p\n  & \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// unescaped_string_interpolation
		assertRender("<p>\n  4&<\n</p>\n", "%p\n  ! \\#{2+2}&\\#{'<'}"); //, :escape_html => true))
		assertRender("<p>\n  4&<\n</p>\n", "%p\n  ! \\#{2+2}&\\#{'<'}"); //, :escape_html => false))

		// scripts_should_respect_escape_html_option
		assertRender("<p>\n  foo &amp; bar\n</p>\n", "%p\n  = 'foo & bar'"); //, :escape_html => true))
		assertRender("<p>\n  foo & bar\n</p>\n", "%p\n  = 'foo & bar'"); //, :escape_html => false))

		// inline_scripts_should_respect_escape_html_option
		assertRender("<p>foo &amp; bar</p>\n", "%p= 'foo & bar'"); //, :escape_html => true))
		assertRender("<p>foo & bar</p>\n", "%p= 'foo & bar'"); //, :escape_html => false))

		// script_ending_in_comment_should_render_when_html_is_escaped
		assertRender("foo&amp;bar\n", "= 'foo&bar' #comment"); //, :escape_html => true))
		
		// escape_attrs_false
		assertRender("<div class='<?php echo \"&quot;\" ?>' id='foo'>\n\tbar\n</div", "#foo{:class => '<?php echo \"&quot;\" ?>'}\n\tbar");
		assertRender("<div class='\"&amp;lt;&amp;gt;&amp;amp;\"' id='foo'></div>", "#foo{:class => '\"&lt;&gt;&amp;\"'}");

		String html = "&amp;\n&\n&amp;";
		assertRender(html, "&= \"&\"\n!= \"&\"\n= \"&\""); // escape_html = true
		assertRender(html, "&~ \"&\"\n!~ \"&\"\n~ \"&\""); // escape_html = true
		assertRender(html, "& \\#{\"&\"}\n! \\#{\"&\"}\n\\#{\"&\"}");
		assertRender(html, "&== \\#{\"&\"}\n!== \\#{\"&\"}\n== \\#{\"&\"}");

		html = "<p>&amp;</p>\n<p>&</p>\n<p>&amp;</p>";
		assertRender(html, "%p&= \"&\"\n%p!= \"&\"\n%p= \"&\""); // escape_html = true
		assertRender(html, "%p&~ \"&\"\n%p!~ \"&\"\n%p~ \"&\""); // escape_html = true
		assertRender(html, "%p& \\#{\"&\"}\n%p! \\#{\"&\"}\n%p \\#{\"&\"}"); // escape_html = true
		assertRender(html, "%p&== \\#{\"&\"}\n%p!== \\#{\"&\"}\n%p== \\#{\"&\"}"); // escape_html = true

	}
	
	@Test
	public void testBooleanAttributes() throws Exception {
		assertRender("<p bar baz='true' foo='bar'></p>\n", "%p{:foo => 'bar', :bar => true, :baz => 'true'}"); // , :format => :html))
		assertRender("<p bar='bar' baz='true' foo='bar'></p>\n", "%p{:foo => 'bar', :bar => true, :baz => 'true'}"); //, :format => :xhtml))
		assertRender("<p baz='false' foo='bar'></p>\n", "%p{:foo => 'bar', :bar => false, :baz => 'false'}"); //, :format => :html))
		assertRender("<p baz='false' foo='bar'></p>\n", "%p{:foo => 'bar', :bar => false, :baz => 'false'}"); //, :format => :xhtml))
	}
	
	@Test
	public void testClassAttributeWithArray() throws Exception {
		assertRender("<p class='a b'>foo</p>\n", "%p{:class => %w[a b]} foo"); // basic
		assertRender("<p class='a b css'>foo</p>\n", "%p.css{:class => %w[a b]} foo"); // merge with css
		assertRender("<p class='b css'>foo</p>\n", "%p.css{:class => %w[css b]} foo"); // merge uniquely
		assertRender("<p class='a b c d'>foo</p>\n", "%p{:class => [%w[a b], %w[c d]]} foo"); // flatten
		assertRender("<p class='a b'>foo</p>\n", "%p{:class => [:a, :b] } foo"); // stringify
		assertRender("<p>foo</p>\n", "%p{:class => [nil, false] } foo"); // strip falsey
		assertRender("<p class='a'>foo</p>\n", "%p{:class => :a} foo"); // single stringify
		assertRender("<p>foo</p>\n", "%p{:class => false} foo"); // single falsey
		assertRender("<p class='a b html'>foo</p>\n", "%p(class='html'){:class => %w[a b]} foo"); // html attrs
		
	}

	@Test
	public void testIdAttributeWithArray() throws Exception {
	    assertRender("<p id='a_b'>foo</p>\n", "%p{:id => %w[a b]} foo"); // basic
	    assertRender("<p id='css_a_b'>foo</p>\n", "%p#css{:id => %w[a b]} foo"); // merge with css
	    assertRender("<p id='a_b_c_d'>foo</p>\n", "%p{:id => [%w[a b], %w[c d]]} foo"); // flatten
	    assertRender("<p id='a_b'>foo</p>\n", "%p{:id => [:a, :b] } foo"); // stringify
	    assertRender("<p>foo</p>\n", "%p{:id => [nil, false] } foo"); // strip falsey
	    assertRender("<p id='a'>foo</p>\n", "%p{:id => :a} foo"); // single stringify
	    assertRender("<p>foo</p>\n", "%p{:id => false} foo"); // single falsey
	    assertRender("<p id='html_a_b'>foo</p>\n", "%p(id='html'){:id => %w[a b]} foo"); // html attrs
	}

	@Test(expected=ParseException.class)
	public void testInvalidSyntax() throws Exception {
		assertRender("ff", ".#");
	}
	
	@Test
	public void testCase() throws Exception {
		// case_assigned_to_var
		assertRender("bar", "- var = case 12\n\t- when 1; \"foo\"\n\t- when 12; \"bar\"\n= var");
		assertRender("bar", "- var = case 12\n\t- when 1\n\t\t- \"foo\"\n\t- when 12\n- \"bar\"\n= var");
		assertRender("bar", "- var = case 12\n\t- when 1\n\t\t- \"foo\"\n\t- when 12\n\t\t- \"bar\"\n= var");
	}
	
	private void assertRender(String htmlExpected, String hamlContents, Object... requestAttributes) throws ScriptException {
		for (int i = 0; i < requestAttributes.length; ) {
			requestContext.put((String)requestAttributes[i++], requestAttributes[i++]);
		}
		StringBackedContext context = initContext();
		Document document = factory.createParser().parse(hamlContents);
		document.render(context);
		assertEquals(htmlExpected, context.getContents());
	}

	private String readFile(String filename) throws Exception {
		return Files.toString(new File(filename), Charset.forName("utf-8"));
	}
	
	/*
	 	  # Regression tests

	  def test_whitespace_nuke_with_both_newlines
	  assertRender("<p>foo</p>\n", '%p<= "\nfoo\n"'))
	  assertRender(<<HTML, <<HAML))
	<p>
	  <p>foo</p>
	</p>
	HTML
	%p
	  %p<= "\\nfoo\\n"
	HAML
	  end

	  def test_whitespace_nuke_with_tags_and_else
	  assertRender(<<HTML, <<HAML))
	<a>
	  <b>foo</b>
	</a>
	HTML
	%a
	  %b<
	    - if false
	      = "foo"
	    - else
	      foo
	HAML

	  assertRender(<<HTML, <<HAML))
	<a>
	  <b>
	    foo
	  </b>
	</a>
	HTML
	%a
	  %b
	    - if false
	      = "foo"
	    - else
	      foo
	HAML
	  end

	  def test_outer_whitespace_nuke_with_empty_script
	  assertRender(<<HTML, <<HAML))
	<p>
	  foo<a></a></p>
	HTML
	%p
	  foo
	  = "  "
	  %a>
	HAML
	  end

	  def test_both_case_indentation_work_with_deeply_nested_code
	    result = <<RESULT
	<h2>
	  other
	</h2>
	RESULT
	  assertRender(result, <<HAML))
	- case 'other'
	- when 'test'
	  %h2
	    hi
	- when 'other'
	  %h2
	    other
	HAML
	  assertRender(result, <<HAML))
	- case 'other'
	  - when 'test'
	    %h2
	      hi
	  - when 'other'
	    %h2
	      other
	HAML
	  end
	 */
}
