package org.haml4j.parser;

import static org.junit.Assert.fail;

import org.haml4j.exception.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// engine_test.rb
public class ParseTest {
	
	private Parser parser;

	private static Logger log = LoggerFactory.getLogger(RenderTest.class);

	@Before
	public void setupParserFactory() {
		ParserFactory factory = new ParserFactory();
		factory.setDoctypeHandler(new XmlDoctypeHandler());
		parser = factory.createParser();
	}
	
	@Test
	public void testMalformedDocuments() {
		assertFail("!!!\n  a", "Illegal nesting: nesting within a header command is illegal.");
		assertFail("a\n  b", "Illegal nesting: nesting within plain text is illegal.");
		assertFail("/ a\n  b", "Illegal nesting: nesting within a tag that already has content is illegal.");
		assertFail("% a", "Invalid tag: \"% a\".");
		assertFail("%p a\n  b", "Illegal nesting: content can't be both given on the same line as %p and nested within it.");
		assertFail("%p=", "There's no Ruby code for = to evaluate.");
		assertFail("%p~", "There's no Ruby code for ~ to evaluate.");
		assertFail("~", "There's no Ruby code for ~ to evaluate.");
		assertFail("=", "There's no Ruby code for = to evaluate.");
		assertFail("%p/\n  a", "Illegal nesting: nesting within a self-closing tag is illegal.");
		//assertFail(":a\n  b", "Filter \"a\" is not defined.");
		//assertFail(":a= b" => "Invalid filter name ":a= b".",
		assertFail(".", "Illegal element: classes and ids must have values.");
		assertFail(".#", "Illegal element: classes and ids must have values.");
		assertFail(".{} a", "Illegal element: classes and ids must have values.");
		assertFail(".() a", "Illegal element: classes and ids must have values.");
		assertFail(".= a", "Illegal element: classes and ids must have values.");
		assertFail("%p..a", "Illegal element: classes and ids must have values.");
		assertFail("%a/ b", "Self-closing tags can't have content.");
		assertFail("%p{:a => 'b',\n:c => 'd'}/ e", "Self-closing tags can't have content.");
		assertFail("%p{:a => 'b',\n:c => 'd'}=", "There's no Ruby code for = to evaluate.");
		assertFail("%p.{:a => 'b',\n:c => 'd'} e", "Illegal element: classes and ids must have values.");
		assertFail("%p{:a => 'b',\n:c => 'd',\n:e => 'f'}\n%p/ a", "Self-closing tags can't have content.");
		assertFail(" %p foo", "Indenting at the beginning of the document is illegal.");
		assertFail("  %p foo", "Indenting at the beginning of the document is illegal.");
	}
	
	private void assertFail(String document, String expectedMessage) {
		try {
			parser.parse(document);
			fail("Document accepted as good: " + document);
		} catch (ParseException e) {
			log.debug(document + " => " + e.getMessage());
		}
	}
	
	/*
	  # A map of erroneous Haml documents to the error messages they should produce.
	  # The error messages may be arrays;
	  # if so, the second element should be the line number that should be reported for the error.
	  # If this isn't provided, the tests will assume the line number should be the last line of the document.
	  EXCEPTION_MAP = {
	    "- end" => <<MESSAGE.rstrip,
	You don't need to use "- end" in Haml. Un-indent to close a block:
	- if foo?
	  %strong Foo!
	- else
	  Not foo.
	%p This line is un-indented, so it isn't part of the "if" block
	MESSAGE
	    " \n\t\n %p foo" => ["Indenting at the beginning of the document is illegal.", 3],
	    "\n\n %p foo" => ["Indenting at the beginning of the document is illegal.", 3],
	    "%p\n  foo\n foo" => ["Inconsistent indentation: 1 space was used for indentation, but the rest of the document was indented using 2 spaces.", 3],
	    "%p\n  foo\n%p\n foo" => ["Inconsistent indentation: 1 space was used for indentation, but the rest of the document was indented using 2 spaces.", 4],
	    "%p\n\t\tfoo\n\tfoo" => ["Inconsistent indentation: 1 tab was used for indentation, but the rest of the document was indented using 2 tabs.", 3],
	    "%p\n  foo\n   foo" => ["Inconsistent indentation: 3 spaces were used for indentation, but the rest of the document was indented using 2 spaces.", 3],
	    "%p\n  foo\n  %p\n   bar" => ["Inconsistent indentation: 3 spaces were used for indentation, but the rest of the document was indented using 2 spaces.", 4],
	    "%p\n  :plain\n     bar\n   \t  baz" => ['Inconsistent indentation: "   \t  " was used for indentation, but the rest of the document was indented using 2 spaces.', 4],
	    "%p\n  foo\n%p\n    bar" => ["The line was indented 2 levels deeper than the previous line.", 4],
	    "%p\n  foo\n  %p\n        bar" => ["The line was indented 3 levels deeper than the previous line.", 4],
	    "%p\n \tfoo" => ["Indentation can't use both tabs and spaces.", 2],
	    "%p(" => "Invalid attribute list: \"(\".",
	    "%p(foo=\nbar)" => ["Invalid attribute list: \"(foo=\".", 1],
	    "%p(foo=)" => "Invalid attribute list: \"(foo=)\".",
	    "%p(foo 'bar')" => "Invalid attribute list: \"(foo 'bar')\".",
	    "%p(foo 'bar'\nbaz='bang')" => ["Invalid attribute list: \"(foo 'bar'\".", 1],
	    "%p(foo='bar'\nbaz 'bang'\nbip='bop')" => ["Invalid attribute list: \"(foo='bar' baz 'bang'\".", 2],
	    "%p{:foo => 'bar' :bar => 'baz'}" => :compile,
	    "%p{:foo => }" => :compile,
	    "%p{=> 'bar'}" => :compile,
	    "%p{:foo => 'bar}" => :compile,
	    "%p{'foo => 'bar'}" => :compile,
	    "%p{:foo => 'bar\"}" => :compile,

	    # Regression tests
	    "- raise 'foo'\n\n\n\nbar" => ["foo", 1],
	    "= 'foo'\n-raise 'foo'" => ["foo", 2],
	    "\n\n\n- raise 'foo'" => ["foo", 4],
	    "%p foo |\n   bar |\n   baz |\nbop\n- raise 'foo'" => ["foo", 5],
	    "foo\n\n\n  bar" => ["Illegal nesting: nesting within plain text is illegal.", 4],
	    "%p/\n\n  bar" => ["Illegal nesting: nesting within a self-closing tag is illegal.", 3],
	    "%p foo\n\n  bar" => ["Illegal nesting: content can't be both given on the same line as %p and nested within it.", 3],
	    "/ foo\n\n  bar" => ["Illegal nesting: nesting within a tag that already has content is illegal.", 3],
	    "!!!\n\n  bar" => ["Illegal nesting: nesting within a header command is illegal.", 3],
	    "foo\n:ruby\n  1\n  2\n  3\n- raise 'foo'" => ["foo", 6],
	    "foo\n:erb\n  1\n  2\n  3\n- raise 'foo'" => ["foo", 6],
	    "foo\n:plain\n  1\n  2\n  3\n- raise 'foo'" => ["foo", 6],
	    "foo\n:plain\n  1\n  2\n  3\n4\n- raise 'foo'" => ["foo", 7],
	    "foo\n:plain\n  1\n  2\n  3\#{''}\n- raise 'foo'" => ["foo", 6],
	    "foo\n:plain\n  1\n  2\n  3\#{''}\n4\n- raise 'foo'" => ["foo", 7],
	    "foo\n:plain\n  1\n  2\n  \#{raise 'foo'}" => ["foo", 5],
	    "= raise 'foo'\nfoo\nbar\nbaz\nbang" => ["foo", 1],
	    "- case 1\n\n- when 1\n  - raise 'foo'" => ["foo", 4],
	  }




	 


	  def test_both_whitespace_nukes_work_together
	    assert_equal(<<RESULT, render(<<SOURCE))
	<p><q>Foo
	  Bar</q></p>
	RESULT
	%p
	  %q><= "Foo\\nBar"
	SOURCE
	  end

	  def test_nil_option
	    assert_equal("<p foo='bar'></p>\n", render('%p{:foo => "bar"}', :attr_wrapper => nil))
	  end

	  def test_comment_with_crazy_nesting
	    assert_equal(<<HTML, render(<<HAML))
	foo
	bar
	HTML
	foo
	-#
	  ul
	    %li{
	  foo
	bar
	HAML
	  end



	  def test_equals_block_with_ugly
	    assert_equal("foo\n", render(<<HAML, :ugly => true))
	= capture_haml do
	  foo
	HAML
	  end

	  def test_plain_equals_with_ugly
	    assert_equal("foo\nbar\n", render(<<HAML, :ugly => true))
	= "foo"
	bar
	HAML
	  end



	  
	  end

	  def 

	  def test_new_attrs_with_hash
	    assert_equal("<a href='#'></a>\n", render('%a(href="#")'))
	  end

	  def test_javascript_filter_with_dynamic_interp_and_escape_html
	    assert_equal(<<HTML, render(<<HAML, :escape_html => true))
	<script type='text/javascript'>
	  //<![CDATA[
	    & < > &
	  //]]>
	</script>
	HTML
	:javascript
	 & < > \#{"&"}
	HAML
	  end

	  def test_html5_javascript_filter
	    assert_equal(<<HTML, render(<<HAML, :format => :html5))
	<script>
	  //<![CDATA[
	    foo bar
	  //]]>
	</script>
	HTML
	:javascript
	  foo bar
	HAML
	  end

	  def test_html5_css_filter
	    assert_equal(<<HTML, render(<<HAML, :format => :html5))
	<style>
	  /*<![CDATA[* /
	    foo bar
	  /*]]>* /
	</style>
	HTML
	:css
	  foo bar
	HAML
	  end

	  def test_erb_filter_with_multiline_expr
	    assert_equal(<<HTML, render(<<HAML))
	foobarbaz
	HTML
	:erb
	  <%= "foo" +
	      "bar" +
	      "baz" %>
	HAML
	  end

	  
	 


	  


	  # HTML escaping tests



	  def test_script_with_if_shouldnt_output
	    assert_equal(<<HTML, render(<<HAML))
	<p>foo</p>
	<p></p>
	HTML
	%p= "foo"
	%p= "bar" if false
	HAML
	  end

	  # Options tests

	  def test_filename_and_line
	    begin
	      render("\n\n = abc", :filename => 'test', :line => 2)
	    rescue Exception => e
	      assert_kind_of Haml::SyntaxError, e
	      assert_match(/test:4/, e.backtrace.first)
	    end

	    begin
	      render("\n\n= 123\n\n= nil[]", :filename => 'test', :line => 2)
	    rescue Exception => e
	      assert_kind_of NoMethodError, e
	      assert_match(/test:6/, e.backtrace.first)
	    end
	  end

	  def test_stop_eval
	    assert_equal("", render("= 'Hello'", :suppress_eval => true))
	    assert_equal("", render("- haml_concat 'foo'", :suppress_eval => true))
	    assert_equal("<div id='foo' yes='no' />\n", render("#foo{:yes => 'no'}/", :suppress_eval => true))
	    assert_equal("<div id='foo' />\n", render("#foo{:yes => 'no', :call => a_function() }/", :suppress_eval => true))
	    assert_equal("<div />\n", render("%div[1]/", :suppress_eval => true))
	    assert_equal("", render(":ruby\n  Kernel.puts 'hello'", :suppress_eval => true))
	  end

	  def test_doctypes
	    assert_equal('<!DOCTYPE html>',
	      render('!!!', :format => :html5).strip)
	    assert_equal('<!DOCTYPE html>', render('!!! 5').strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">',
	      render('!!! strict').strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">',
	      render('!!! frameset').strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN" "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd">',
	      render('!!! mobile').strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML Basic 1.1//EN" "http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd">',
	      render('!!! basic').strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">',
	      render('!!! transitional').strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">',
	      render('!!!').strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">',
	      render('!!! strict', :format => :html4).strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">',
	      render('!!! frameset', :format => :html4).strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">',
	      render('!!! transitional', :format => :html4).strip)
	    assert_equal('<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">',
	      render('!!!', :format => :html4).strip)
	  end

	  def test_attr_wrapper
	    assert_equal("<p strange=*attrs*></p>\n", render("%p{ :strange => 'attrs'}", :attr_wrapper => '*'))
	    assert_equal("<p escaped='quo\"te'></p>\n", render("%p{ :escaped => 'quo\"te'}", :attr_wrapper => '"'))
	    assert_equal("<p escaped=\"quo'te\"></p>\n", render("%p{ :escaped => 'quo\\'te'}", :attr_wrapper => '"'))
	    assert_equal("<p escaped=\"q'uo&quot;te\"></p>\n", render("%p{ :escaped => 'q\\'uo\"te'}", :attr_wrapper => '"'))
	    assert_equal("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n", render("!!! XML", :attr_wrapper => '"'))
	  end

	  def test_autoclose_option
	    assert_equal("<flaz foo='bar' />\n", render("%flaz{:foo => 'bar'}", :autoclose => ["flaz"]))
	    assert_equal(<<HTML, render(<<HAML, :autoclose => [/^flaz/]))
	<flaz />
	<flaznicate />
	<flan></flan>
	HTML
	%flaz
	%flaznicate
	%flan
	HAML
	  end

	  

	  def test_nil_id_with_syntactic_id
	    assert_equal("<p id='foo'>nil</p>\n", render("%p#foo{:id => nil} nil"))
	    assert_equal("<p id='foo_bar'>nil</p>\n", render("%p#foo{{:id => 'bar'}, :id => nil} nil"))
	    assert_equal("<p id='foo_bar'>nil</p>\n", render("%p#foo{{:id => nil}, :id => 'bar'} nil"))
	  end

	  def test_nil_class_with_syntactic_class
	    assert_equal("<p class='foo'>nil</p>\n", render("%p.foo{:class => nil} nil"))
	    assert_equal("<p class='bar foo'>nil</p>\n", render("%p.bar.foo{:class => nil} nil"))
	    assert_equal("<p class='bar foo'>nil</p>\n", render("%p.foo{{:class => 'bar'}, :class => nil} nil"))
	    assert_equal("<p class='bar foo'>nil</p>\n", render("%p.foo{{:class => nil}, :class => 'bar'} nil"))
	  end

	  def test_locals
	    assert_equal("<p>Paragraph!</p>\n", render("%p= text", :locals => { :text => "Paragraph!" }))
	  end

	  def test_dynamic_attrs_shouldnt_register_as_literal_values
	    assert_equal("<p a='b2c'></p>\n", render('%p{:a => "b#{1 + 1}c"}'))
	    assert_equal("<p a='b2c'></p>\n", render("%p{:a => 'b' + (1 + 1).to_s + 'c'}"))
	  end

	  def test_dynamic_attrs_with_self_closed_tag
	    assert_equal("<a b='2' />\nc\n", render("%a{'b' => 1 + 1}/\n= 'c'\n"))
	  end

	  EXCEPTION_MAP.each do |key, value|
	    define_method("test_exception (#{key.inspect})") do
	      begin
	        render(key, :filename => __FILE__)
	      rescue Exception => err
	        value = [value] unless value.is_a?(Array)
	        expected_message, line_no = value
	        line_no ||= key.split("\n").length

	        if expected_message == :compile
	          if Haml::Util.ruby1_8?
	            assert_match(/^compile error\n/, err.message, "Line: #{key}")
	          else
	            assert_match(/^#{Regexp.quote __FILE__}:#{line_no}: syntax error,/, err.message, "Line: #{key}")
	          end
	        else
	          assert_equal(expected_message, err.message, "Line: #{key}")
	        end

	        if Haml::Util.ruby1_8?
	          # Sometimes, the first backtrace entry is *only* in the message.
	          # No idea why.
	          bt =
	            if expected_message == :compile && err.message.include?("\n")
	              err.message.split("\n", 2)[1]
	            else
	              err.backtrace[0]
	            end
	          assert_match(/^#{Regexp.escape(__FILE__)}:#{line_no}/, bt, "Line: #{key}")
	        end
	      else
	        assert(false, "Exception not raised for\n#{key}")
	      end
	    end
	  end

	  def test_exception_line
	    render("a\nb\n!!!\n  c\nd")
	  rescue Haml::SyntaxError => e
	    assert_equal("(test_exception_line):4", e.backtrace[0])
	  else
	    assert(false, '"a\nb\n!!!\n  c\nd" doesn\'t produce an exception')
	  end

	  def test_exception
	    render("%p\n  hi\n  %a= undefined\n= 12")
	  rescue Exception => e
	    assert_match("(test_exception):3", e.backtrace[0])
	  else
	    # Test failed... should have raised an exception
	    assert(false)
	  end

	  def test_compile_error
	    render("a\nb\n- fee)\nc")
	  rescue Exception => e
	    assert_match(/\(test_compile_error\):3: syntax error/i, e.message)
	  else
	    assert(false,
	           '"a\nb\n- fee)\nc" doesn\'t produce an exception!')
	  end

	  def test_unbalanced_brackets
	    render('foo #{1 + 5} foo #{6 + 7 bar #{8 + 9}')
	  rescue Haml::SyntaxError => e
	    assert_equal("Unbalanced brackets.", e.message)
	  end

	  def test_balanced_conditional_comments
	    assert_equal("<!--[if !(IE 6)|(IE 7)]> Bracket: ] <![endif]-->\n",
	                 render("/[if !(IE 6)|(IE 7)] Bracket: ]"))
	  end

	  def test_empty_filter
	    assert_equal(<<END, render(':javascript'))
	<script type='text/javascript'>
	  //<![CDATA[
	    
	  //]]>
	</script>
	END
	  end

	  def test_ugly_filter
	    assert_equal(<<END, render(":sass\n  #foo\n    bar: baz", :ugly => true))
	#foo {
	  bar: baz; }
	END
	  end

	  def test_css_filter
	    assert_equal(<<HTML, render(<<HAML))
	<style type='text/css'>
	  /*<![CDATA[* /
	    #foo {
	      bar: baz; }
	  /*]]>* /
	</style>
	HTML
	:css
	  #foo {
	    bar: baz; }
	HAML
	  end

	  def test_local_assigns_dont_modify_class
	    assert_equal("bar\n", render("= foo", :locals => {:foo => 'bar'}))
	    assert_equal(nil, defined?(foo))
	  end

	  def test_object_ref_with_nil_id
	    user = User.new
	    assert_equal("<p class='struct_user' id='struct_user_new'>New User</p>\n",
	                 render("%p[user] New User", :locals => {:user => user}))
	  end

	  def test_object_ref_before_attrs
	    user = User.new 42
	    assert_equal("<p class='struct_user' id='struct_user_42' style='width: 100px;'>New User</p>\n",
	                 render("%p[user]{:style => 'width: 100px;'} New User", :locals => {:user => user}))
	  end

	  def test_object_ref_with_custom_haml_class
	    custom = CustomHamlClass.new 42
	    assert_equal("<p class='my_thing' id='my_thing_42' style='width: 100px;'>My Thing</p>\n",
	                 render("%p[custom]{:style => 'width: 100px;'} My Thing", :locals => {:custom => custom}))
	  end

	  def test_non_literal_attributes
	    assert_equal("<p a1='foo' a2='bar' a3='baz' />\n",
	                 render("%p{a2, a1, :a3 => 'baz'}/",
	                        :locals => {:a1 => {:a1 => 'foo'}, :a2 => {:a2 => 'bar'}}))
	  end

	  def test_render_should_accept_a_binding_as_scope
	    string = "This is a string!"
	    string.instance_variable_set("@var", "Instance variable")
	    b = string.instance_eval do
	      var = "Local variable"
	      binding
	    end

	    assert_equal("<p>THIS IS A STRING!</p>\n<p>Instance variable</p>\n<p>Local variable</p>\n",
	                 render("%p= upcase\n%p= @var\n%p= var", :scope => b))
	  end

	  def test_yield_should_work_with_binding
	    assert_equal("12\nFOO\n", render("= yield\n= upcase", :scope => "foo".instance_eval{binding}) { 12 })
	  end

	  def test_yield_should_work_with_def_method
	    s = "foo"
	    engine("= yield\n= upcase").def_method(s, :render)
	    assert_equal("12\nFOO\n", s.render { 12 })
	  end

	  def test_def_method_with_module
	    engine("= yield\n= upcase").def_method(String, :render_haml)
	    assert_equal("12\nFOO\n", "foo".render_haml { 12 })
	  end

	  def test_def_method_locals
	    obj = Object.new
	    engine("%p= foo\n.bar{:baz => baz}= boom").def_method(obj, :render, :foo, :baz, :boom)
	    assert_equal("<p>1</p>\n<div baz='2' class='bar'>3</div>\n", obj.render(:foo => 1, :baz => 2, :boom => 3))
	  end

	  def test_render_proc_locals
	    proc = engine("%p= foo\n.bar{:baz => baz}= boom").render_proc(Object.new, :foo, :baz, :boom)
	    assert_equal("<p>1</p>\n<div baz='2' class='bar'>3</div>\n", proc[:foo => 1, :baz => 2, :boom => 3])
	  end

	  def test_render_proc_with_binding
	    assert_equal("FOO\n", engine("= upcase").render_proc("foo".instance_eval{binding}).call)
	  end

	  def test_haml_buffer_gets_reset_even_with_exception
	    scope = Object.new
	    render("- raise Haml::Error", :scope => scope)
	    assert(false, "Expected exception")
	  rescue Exception
	    assert_nil(scope.send(:haml_buffer))
	  end

	  def test_def_method_haml_buffer_gets_reset_even_with_exception
	    scope = Object.new
	    engine("- raise Haml::Error").def_method(scope, :render)
	    scope.render
	    assert(false, "Expected exception")
	  rescue Exception
	    assert_nil(scope.send(:haml_buffer))
	  end

	  def test_render_proc_haml_buffer_gets_reset_even_with_exception
	    scope = Object.new
	    proc = engine("- raise Haml::Error").render_proc(scope)
	    proc.call
	    assert(false, "Expected exception")
	  rescue Exception
	    assert_nil(scope.send(:haml_buffer))
	  end

	  def test_ugly_true
	    assert_equal("<div id='outer'>\n<div id='inner'>\n<p>hello world</p>\n</div>\n</div>\n",
	                 render("#outer\n  #inner\n    %p hello world", :ugly => true))

	    assert_equal("<p>#{'s' * 75}</p>\n",
	                 render("%p #{'s' * 75}", :ugly => true))

	    assert_equal("<p>#{'s' * 75}</p>\n",
	                 render("%p= 's' * 75", :ugly => true))
	  end

	  def test_auto_preserve_unless_ugly
	    assert_equal("<pre>foo&#x000A;bar</pre>\n", render('%pre="foo\nbar"'))
	    assert_equal("<pre>foo\nbar</pre>\n", render("%pre\n  foo\n  bar"))
	    assert_equal("<pre>foo\nbar</pre>\n", render('%pre="foo\nbar"', :ugly => true))
	    assert_equal("<pre>foo\nbar</pre>\n", render("%pre\n  foo\n  bar", :ugly => true))
	  end

	  def test_xhtml_output_option
	    assert_equal "<p>\n  <br />\n</p>\n", render("%p\n  %br", :format => :xhtml)
	    assert_equal "<a />\n", render("%a/", :format => :xhtml)
	  end

	  def test_arbitrary_output_option
	    assert_raise_message(Haml::Error, "Invalid output format :html1") do
	      engine("%br", :format => :html1)
	    end
	  end

	  def test_static_hashes
	    assert_equal("<a b='a =&gt; b'></a>\n", render("%a{:b => 'a => b'}", :suppress_eval => true))
	    assert_equal("<a b='a, b'></a>\n", render("%a{:b => 'a, b'}", :suppress_eval => true))
	    assert_equal("<a b='a\tb'></a>\n", render('%a{:b => "a\tb"}', :suppress_eval => true))
	    assert_equal("<a b='a\#{foo}b'></a>\n", render('%a{:b => "a\\#{foo}b"}', :suppress_eval => true))
	  end

	  def test_dynamic_hashes_with_suppress_eval
	    assert_equal("<a></a>\n", render('%a{:b => "a #{1 + 1} b", :c => "d"}', :suppress_eval => true))
	  end

	  def test_utf8_attrs
	    assert_equal("<a href='héllo'></a>\n", render("%a{:href => 'héllo'}"))
	    assert_equal("<a href='héllo'></a>\n", render("%a(href='héllo')"))
	  end

	  # HTML 4.0

	  def test_html_has_no_self_closing_tags
	    assert_equal "<p>\n  <br>\n</p>\n", render("%p\n  %br", :format => :html4)
	    assert_equal "<br>\n", render("%br/", :format => :html4)
	  end

	  def test_html_renders_empty_node_with_closing_tag
	    assert_equal "<div class='foo'></div>\n", render(".foo", :format => :html4)
	  end

	  def test_html_doesnt_add_slash_to_self_closing_tags
	    assert_equal "<a>\n", render("%a/", :format => :html4)
	    assert_equal "<a foo='2'>\n", render("%a{:foo => 1 + 1}/", :format => :html4)
	    assert_equal "<meta>\n", render("%meta", :format => :html4)
	    assert_equal "<meta foo='2'>\n", render("%meta{:foo => 1 + 1}", :format => :html4)
	  end

	  def test_html_ignores_xml_prolog_declaration
	    assert_equal "", render('!!! XML', :format => :html4)
	  end

	  def test_html_has_different_doctype
	    assert_equal %{<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">\n},
	    render('!!!', :format => :html4)
	  end

	  # because anything before the doctype triggers quirks mode in IE
	  def test_xml_prolog_and_doctype_dont_result_in_a_leading_whitespace_in_html
	    assert_no_match(/^\s+/, render("!!! xml\n!!!", :format => :html4))
	  end

	  # HTML5
	  def test_html5_doctype
	    assert_equal %{<!DOCTYPE html>\n}, render('!!!', :format => :html5)
	  end

	  # HTML5 custom data attributes
	  def test_html5_data_attributes
	    assert_equal("<div data-author_id='123' data-biz='baz' data-foo='bar'></div>\n",
	      render("%div{:data => {:author_id => 123, :foo => 'bar', :biz => 'baz'}}"))

	    assert_equal("<div data-one_plus_one='2'></div>\n",
	      render("%div{:data => {:one_plus_one => 1+1}}"))

	    assert_equal("<div data-foo='Here&apos;s a \"quoteful\" string.'></div>\n",
	      render(%{%div{:data => {:foo => %{Here's a "quoteful" string.}}}})) #'
	  end

	  def test_html5_data_attributes_with_multiple_defs
	    # Should always use the more-explicit attribute
	    assert_equal("<div data-foo='second'></div>\n",
	      render("%div{:data => {:foo => 'first'}, 'data-foo' => 'second'}"))
	    assert_equal("<div data-foo='first'></div>\n",
	      render("%div{'data-foo' => 'first', :data => {:foo => 'second'}}"))
	  end

	  def test_html5_data_attributes_with_attr_method
	    Haml::Helpers.module_eval do
	      def data_hash
	        {:data => {:foo => "bar", :baz => "bang"}}
	      end

	      def data_val
	        {:data => "dat"}
	      end
	    end

	    assert_equal("<div data-baz='bang' data-brat='wurst' data-foo='blip'></div>\n",
	      render("%div{data_hash, :data => {:foo => 'blip', :brat => 'wurst'}}"))
	    assert_equal("<div data-baz='bang' data-foo='blip'></div>\n",
	      render("%div{data_hash, 'data-foo' => 'blip'}"))
	    assert_equal("<div data-baz='bang' data-foo='bar' data='dat'></div>\n",
	      render("%div{data_hash, :data => 'dat'}"))
	    assert_equal("<div data-brat='wurst' data-foo='blip' data='dat'></div>\n",
	      render("%div{data_val, :data => {:foo => 'blip', :brat => 'wurst'}}"))
	  end

	  # New attributes

	  def test_basic_new_attributes
	    assert_equal("<a>bar</a>\n", render("%a() bar"))
	    assert_equal("<a href='foo'>bar</a>\n", render("%a(href='foo') bar"))
	    assert_equal("<a b='c' c='d' d='e'>baz</a>\n", render(%q{%a(b="c" c='d' d="e") baz}))
	  end

	  def test_new_attribute_ids
	    assert_equal("<div id='foo_bar'></div>\n", render("#foo(id='bar')"))
	    assert_equal("<div id='foo_baz_bar'></div>\n", render("#foo{:id => 'bar'}(id='baz')"))
	    assert_equal("<div id='foo_baz_bar'></div>\n", render("#foo(id='baz'){:id => 'bar'}"))
	    foo = User.new(42)
	    assert_equal("<div class='struct_user' id='foo_baz_bar_struct_user_42'></div>\n",
	      render("#foo(id='baz'){:id => 'bar'}[foo]", :locals => {:foo => foo}))
	    assert_equal("<div class='struct_user' id='foo_baz_bar_struct_user_42'></div>\n",
	      render("#foo(id='baz')[foo]{:id => 'bar'}", :locals => {:foo => foo}))
	    assert_equal("<div class='struct_user' id='foo_baz_bar_struct_user_42'></div>\n",
	      render("#foo[foo](id='baz'){:id => 'bar'}", :locals => {:foo => foo}))
	    assert_equal("<div class='struct_user' id='foo_baz_bar_struct_user_42'></div>\n",
	      render("#foo[foo]{:id => 'bar'}(id='baz')", :locals => {:foo => foo}))
	  end

	  def test_new_attribute_classes
	    assert_equal("<div class='bar foo'></div>\n", render(".foo(class='bar')"))
	    assert_equal("<div class='bar baz foo'></div>\n", render(".foo{:class => 'bar'}(class='baz')"))
	    assert_equal("<div class='bar baz foo'></div>\n", render(".foo(class='baz'){:class => 'bar'}"))
	    foo = User.new(42)
	    assert_equal("<div class='bar baz foo struct_user' id='struct_user_42'></div>\n",
	      render(".foo(class='baz'){:class => 'bar'}[foo]", :locals => {:foo => foo}))
	    assert_equal("<div class='bar baz foo struct_user' id='struct_user_42'></div>\n",
	      render(".foo[foo](class='baz'){:class => 'bar'}", :locals => {:foo => foo}))
	    assert_equal("<div class='bar baz foo struct_user' id='struct_user_42'></div>\n",
	      render(".foo[foo]{:class => 'bar'}(class='baz')", :locals => {:foo => foo}))
	  end

	  def test_dynamic_new_attributes
	    assert_equal("<a href='12'>bar</a>\n", render("%a(href=foo) bar", :locals => {:foo => 12}))
	    assert_equal("<a b='12' c='13' d='14'>bar</a>\n", render("%a(b=b c='13' d=d) bar", :locals => {:b => 12, :d => 14}))
	  end

	  def test_new_attribute_interpolation
	    assert_equal("<a href='12'>bar</a>\n", render('%a(href="1#{1 + 1}") bar'))
	    assert_equal("<a href='2: 2, 3: 3'>bar</a>\n", render(%q{%a(href='2: #{1 + 1}, 3: #{foo}') bar}, :locals => {:foo => 3}))
	    assert_equal(%Q{<a href='1\#{1 + 1}'>bar</a>\n}, render('%a(href="1\#{1 + 1}") bar'))
	  end

	  def test_truthy_new_attributes
	    assert_equal("<a href='href'>bar</a>\n", render("%a(href) bar"))
	    assert_equal("<a bar='baz' href>bar</a>\n", render("%a(href bar='baz') bar", :format => :html5))
	    assert_equal("<a href='href'>bar</a>\n", render("%a(href=true) bar"))
	    assert_equal("<a>bar</a>\n", render("%a(href=false) bar"))
	  end

	  def test_new_attribute_parsing
	    assert_equal("<a a2='b2'>bar</a>\n", render("%a(a2=b2) bar", :locals => {:b2 => 'b2'}))
	    assert_equal(%Q{<a a='foo"bar'>bar</a>\n}, render(%q{%a(a="#{'foo"bar'}") bar})) #'
	    assert_equal(%Q{<a a="foo'bar">bar</a>\n}, render(%q{%a(a="#{"foo'bar"}") bar})) #'
	    assert_equal(%Q{<a a='foo"bar'>bar</a>\n}, render(%q{%a(a='foo"bar') bar}))
	    assert_equal(%Q{<a a="foo'bar">bar</a>\n}, render(%q{%a(a="foo'bar") bar}))
	    assert_equal("<a a:b='foo'>bar</a>\n", render("%a(a:b='foo') bar"))
	    assert_equal("<a a='foo' b='bar'>bar</a>\n", render("%a(a = 'foo' b = 'bar') bar"))
	    assert_equal("<a a='foo' b='bar'>bar</a>\n", render("%a(a = foo b = bar) bar", :locals => {:foo => 'foo', :bar => 'bar'}))
	    assert_equal("<a a='foo'>(b='bar')</a>\n", render("%a(a='foo')(b='bar')"))
	    assert_equal("<a a='foo)bar'>baz</a>\n", render("%a(a='foo)bar') baz"))
	    assert_equal("<a a='foo'>baz</a>\n", render("%a( a = 'foo' ) baz"))
	  end

	  def test_new_attribute_escaping
	    assert_equal(%Q{<a a='foo " bar'>bar</a>\n}, render(%q{%a(a="foo \" bar") bar}))
	    assert_equal(%Q{<a a='foo \\" bar'>bar</a>\n}, render(%q{%a(a="foo \\\\\" bar") bar}))

	    assert_equal(%Q{<a a="foo ' bar">bar</a>\n}, render(%q{%a(a='foo \' bar') bar}))
	    assert_equal(%Q{<a a="foo \\' bar">bar</a>\n}, render(%q{%a(a='foo \\\\\' bar') bar}))

	    assert_equal(%Q{<a a='foo \\ bar'>bar</a>\n}, render(%q{%a(a="foo \\\\ bar") bar}))
	    assert_equal(%Q{<a a='foo \#{1 + 1} bar'>bar</a>\n}, render(%q{%a(a="foo \#{1 + 1} bar") bar}))
	  end

	  def test_multiline_new_attribute
	    assert_equal("<a a='b' c='d'>bar</a>\n", render("%a(a='b'\n  c='d') bar"))
	    assert_equal("<a a='b' b='c' c='d' d='e' e='f' f='j'>bar</a>\n",
	      render("%a(a='b' b='c'\n  c='d' d=e\n  e='f' f='j') bar", :locals => {:e => 'e'}))
	  end

	  def test_new_and_old_attributes
	    assert_equal("<a a='b' c='d'>bar</a>\n", render("%a(a='b'){:c => 'd'} bar"))
	    assert_equal("<a a='b' c='d'>bar</a>\n", render("%a{:c => 'd'}(a='b') bar"))
	    assert_equal("<a a='b' c='d'>bar</a>\n", render("%a(c='d'){:a => 'b'} bar"))
	    assert_equal("<a a='b' c='d'>bar</a>\n", render("%a{:a => 'b'}(c='d') bar"))

	    # Old-style always takes precedence over new-style,
	    # because theoretically old-style could have arbitrary end-of-method-call syntax.
	    assert_equal("<a a='b'>bar</a>\n", render("%a{:a => 'b'}(a='d') bar"))
	    assert_equal("<a a='b'>bar</a>\n", render("%a(a='d'){:a => 'b'} bar"))

	    assert_equal("<a a='b' b='c' c='d' d='e'>bar</a>\n",
	      render("%a{:a => 'b',\n:b => 'c'}(c='d'\nd='e') bar"))

	    locals = {:b => 'b', :d => 'd'}
	    assert_equal("<p a='b' c='d'></p>\n", render("%p{:a => b}(c=d)", :locals => locals))
	    assert_equal("<p a='b' c='d'></p>\n", render("%p(a=b){:c => d}", :locals => locals))
	  end

	  # Ruby Multiline

	  def test_silent_ruby_multiline
	    assert_equal(<<HTML, render(<<HAML))
	bar, baz, bang
	<p>foo</p>
	HTML
	- foo = ["bar",
	         "baz",
	         "bang"]
	= foo.join(", ")
	%p foo
	HAML
	  end

	  def test_loud_ruby_multiline
	    assert_equal(<<HTML, render(<<HAML))
	bar, baz, bang
	<p>foo</p>
	<p>bar</p>
	HTML
	= ["bar",
	   "baz",
	   "bang"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  def test_escaped_loud_ruby_multiline
	    assert_equal(<<HTML, render(<<HAML))
	bar&lt;, baz, bang
	<p>foo</p>
	<p>bar</p>
	HTML
	&= ["bar<",
	    "baz",
	    "bang"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  def test_unescaped_loud_ruby_multiline
	    assert_equal(<<HTML, render(<<HAML, :escape_html => true))
	bar<, baz, bang
	<p>foo</p>
	<p>bar</p>
	HTML
	!= ["bar<",
	    "baz",
	    "bang"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  def test_flattened_loud_ruby_multiline
	    assert_equal(<<HTML, render(<<HAML))
	<pre>bar&#x000A;baz&#x000A;bang</pre>
	<p>foo</p>
	<p>bar</p>
	HTML
	~ "<pre>" + ["bar",
	             "baz",
	             "bang"].join("\\n") + "</pre>"
	%p foo
	%p bar
	HAML
	  end

	  def test_loud_ruby_multiline_with_block
	    assert_equal(<<HTML, render(<<HAML))
	#{%w[far faz fang]}
	<p>foo</p>
	<p>bar</p>
	HTML
	= ["bar",
	   "baz",
	   "bang"].map do |str|
	  - str.gsub("ba",
	             "fa")
	%p foo
	%p bar
	HAML
	  end

	  def test_silent_ruby_multiline_with_block
	    assert_equal(<<HTML, render(<<HAML))
	far
	faz
	fang
	<p>foo</p>
	<p>bar</p>
	HTML
	- ["bar",
	   "baz",
	   "bang"].map do |str|
	  = str.gsub("ba",
	             "fa")
	%p foo
	%p bar
	HAML
	  end

	  def test_ruby_multiline_in_tag
	    assert_equal(<<HTML, render(<<HAML))
	<p>foo, bar, baz</p>
	<p>foo</p>
	<p>bar</p>
	HTML
	%p= ["foo",
	     "bar",
	     "baz"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  def test_escaped_ruby_multiline_in_tag
	    assert_equal(<<HTML, render(<<HAML))
	<p>foo&lt;, bar, baz</p>
	<p>foo</p>
	<p>bar</p>
	HTML
	%p&= ["foo<",
	      "bar",
	      "baz"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  def test_unescaped_ruby_multiline_in_tag
	    assert_equal(<<HTML, render(<<HAML, :escape_html => true))
	<p>foo<, bar, baz</p>
	<p>foo</p>
	<p>bar</p>
	HTML
	%p!= ["foo<",
	      "bar",
	      "baz"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  def test_ruby_multiline_with_normal_multiline
	    assert_equal(<<HTML, render(<<HAML))
	foobarbar, baz, bang
	<p>foo</p>
	<p>bar</p>
	HTML
	= "foo" + |
	  "bar" + |
	  ["bar", |
	   "baz",
	   "bang"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  def test_ruby_multiline_after_filter
	    assert_equal(<<HTML, render(<<HAML))
	foo
	bar
	bar, baz, bang
	<p>foo</p>
	<p>bar</p>
	HTML
	:plain
	  foo
	  bar
	= ["bar",
	   "baz",
	   "bang"].join(", ")
	%p foo
	%p bar
	HAML
	  end

	  # Encodings

	  def test_utf_8_bom
	    assert_equal <<HTML, render(<<HAML)
	<div class='foo'>
	  <p>baz</p>
	</div>
	HTML
	\xEF\xBB\xBF.foo
	  %p baz
	HAML
	  end

	  unless Haml::Util.ruby1_8?
	    def test_default_encoding
	      assert_equal(Encoding.find("utf-8"), render(<<HAML.encode("us-ascii")).encoding)
	%p bar
	%p foo
	HAML
	    end

	    def test_fake_ascii_encoding
	      assert_encoded_equal(<<HTML.force_encoding("ascii-8bit"), render(<<HAML, :encoding => "ascii-8bit"))
	<p>bâr</p>
	<p>föö</p>
	HTML
	%p bâr
	%p föö
	HAML
	    end

	    def test_convert_template_render_proc
	      assert_converts_template_properly {|e| e.render_proc.call}
	    end

	    def test_convert_template_render
	      assert_converts_template_properly {|e| e.render}
	    end

	    def test_convert_template_def_method
	      assert_converts_template_properly do |e|
	        o = Object.new
	        e.def_method(o, :render)
	        o.render
	      end
	    end

	    def test_encoding_error
	      render("foo\nbar\nb\xFEaz".force_encoding("utf-8"))
	      assert(false, "Expected exception")
	    rescue Haml::Error => e
	      assert_equal(3, e.line)
	      assert_equal('Invalid UTF-8 character "\xFE"', e.message)
	    end

	    def test_ascii_incompatible_encoding_error
	      template = "foo\nbar\nb_z".encode("utf-16le")
	      template[9] = "\xFE".force_encoding("utf-16le")
	      render(template)
	      assert(false, "Expected exception")
	    rescue Haml::Error => e
	      assert_equal(3, e.line)
	      assert_equal('Invalid UTF-16LE character "\xFE"', e.message)
	    end

	    def test_same_coding_comment_as_encoding
	      assert_renders_encoded(<<HTML, <<HAML)
	<p>bâr</p>
	<p>föö</p>
	HTML
	-# coding: utf-8
	%p bâr
	%p föö
	HAML
	    end

	    def test_coding_comments
	      assert_valid_encoding_comment("-# coding: ibm866")
	      assert_valid_encoding_comment("-# CodINg: IbM866")
	      assert_valid_encoding_comment("-#coding:ibm866")
	      assert_valid_encoding_comment("-# CodINg= ibm866")
	      assert_valid_encoding_comment("-# foo BAR FAOJcoding: ibm866")
	      assert_valid_encoding_comment("-# coding: ibm866 ASFJ (&(&#!$")
	      assert_valid_encoding_comment("-# -*- coding: ibm866")
	      assert_valid_encoding_comment("-# coding: ibm866 -*- coding: blah")
	      assert_valid_encoding_comment("-# -*- coding: ibm866 -*-")
	      assert_valid_encoding_comment("-# -*- encoding: ibm866 -*-")
	      assert_valid_encoding_comment('-# -*- coding: "ibm866" -*-')
	      assert_valid_encoding_comment("-#-*-coding:ibm866-*-")
	      assert_valid_encoding_comment("-#-*-coding:ibm866-*-")
	      assert_valid_encoding_comment("-# -*- foo: bar; coding: ibm866; baz: bang -*-")
	      assert_valid_encoding_comment("-# foo bar coding: baz -*- coding: ibm866 -*-")
	      assert_valid_encoding_comment("-# -*- coding: ibm866 -*- foo bar coding: baz")
	    end

	    def test_different_coding_than_system
	      assert_renders_encoded(<<HTML.encode("IBM866"), <<HAML.encode("IBM866"))
	<p>тАЬ</p>
	HTML
	%p тАЬ
	HAML
	    end
	  end

	  private

	  def assert_valid_encoding_comment(comment)
	    assert_renders_encoded(<<HTML.encode("IBM866"), <<HAML.encode("IBM866").force_encoding("UTF-8"))
	<p>ЖЛЫ</p>
	<p>тАЬ</p>
	HTML
	#{comment}
	%p ЖЛЫ
	%p тАЬ
	HAML
	  end

	  def assert_converts_template_properly
	    engine = Haml::Engine.new(<<HAML.encode("iso-8859-1"), :encoding => "macRoman")
	%p bâr
	%p föö
	HAML
	    assert_encoded_equal(<<HTML.encode("macRoman"), yield(engine))
	<p>bâr</p>
	<p>föö</p>
	HTML
	  end

	  def assert_renders_encoded(html, haml)
	    result = render(haml)
	    assert_encoded_equal html, result
	  end

	  def assert_encoded_equal(expected, actual)
	    assert_equal expected.encoding, actual.encoding
	    assert_equal expected, actual
	  end
	  
	  */
}
