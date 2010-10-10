package org.sapia.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.sapia.validator.config.ConfigException;
import org.sapia.validator.config.Def;
import org.sapia.validator.config.VladObjectFactory;

/**
 * An instance of this class encapsulates rule sets and rules. An
 * instance of this class is used as such:
 *
 * <pre>
 * Vlad v = new Vlad()
 *  .loadDefs("someRuleDefs.xml")
 *  .loadDefs("someOtherRuleDefs.xml")
 *  .load("someRuleSets.xml")
 *  .load("someOtherRuleSets.xml");
 * </pre>
 *
 * @see org.sapia.validator.RuleSet
 * @see org.sapia.validator.Rule
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Vlad implements ObjectHandlerIF {
  public static final String VLAD_RULES_XML = "vlad_rules.xml";
  private Map                _rules    = new HashMap();
  private Map                _ruleSets = new HashMap();
  private VladObjectFactory  _factory;
  private Map _globals = new HashMap();
  
  /**
   * Constructor for RuleConfig.
   */
  public Vlad() {
    _factory = new VladObjectFactory(this);
    
    InputStream is = loadResource(VLAD_RULES_XML);
    
    if (is != null) {
      Dom4jProcessor p = new Dom4jProcessor(_factory);
      
      try {
        p.process(is);
      } catch (ProcessingException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Adds a global object to this instance. Global values are kept as
   * part of this instance and passed to every validation context (created
   * when the <code>validate</code> method of this instance is called).
   * <p>
   * Use of this method should be done prior to using this instance for validation,
   * at initialization time.
   *
   * @param name the name under which the given value should be kept.
   * @param value an <code>Object</code>.
   *
   * @see ValidationContext
   */
  public synchronized void addGlobal(String name, Object value){
    Map globals = new HashMap(_globals);
    globals.put(name, value);
    _globals = globals;
  }
  
  /**
   * @param values a <code>Map</code> of values to add to this instance's
   * global values.
   */
  public synchronized void addGlobals(Map values){
    Map globals = new HashMap(_globals);
    globals.putAll(values);
    _globals = globals;
  }
  
  /**
   * @param name the name of an expected global value.
   *
   * @return the <code>Object</code> corresponding to the given name.
   */
  public Object getGlobal(String name){
    return _globals.get(name);
  }
  
  /**
   * @return an unmodifiable <code>Map</code> corresponding to the global values 
   * that this instance holds.
   */
  public Map getGlobals(){
    return Collections.unmodifiableMap(_globals);
  }
  
  
  /**
   * Adds a rule set to this instance.
   *
   * @param set a <code>RuleSet</code>.
   */
  public void addRuleSet(RuleSet set) {
    _ruleSets.put(set.getId(), set);
  }
  
  /**
   * Returns the rule set corresponding to the given identifier.
   *
   * @return a <code>RuleSet</code>.
   * @param id the identifier of the desired <code>RuleSet</code>.
   *
   * @throws IllegalArgumentException if no rule set was found for
   * the given identifier.
   */
  public RuleSet getRuleSet(String id)
  throws IllegalArgumentException {
    RuleSet set = (RuleSet) _ruleSets.get(id);
    
    if (set == null) {
      throw new IllegalArgumentException("No rule set for: " + id);
    }
    
    return set;
  }
  
  /**
   * Adds a rule to this instance.
   *
   * @param a <code>Rule</code>.
   */
  public void add(Rule rule) {
    if (rule.getId() == null) {
      throw new IllegalArgumentException("rule " + rule.getClass().getName()
      + " does not have an ID");
    }
    
    _rules.put(rule.getId(), rule);
  }
  
  /**
   * Adds the given namespace to this instance.
   *
   * @param ns a <code>Namespace</code>.
   */
  public void addNamespace(Namespace ns) {
    if (ns.getPrefix() == null) {
      throw new IllegalArgumentException(
       "Attribute 'prefix' not defined on 'namepsace' element");
    }
    
    Def d;
    
    for (int i = 0; i < ns.getRuleDefs().size(); i++) {
      d = (Def) ns.getRuleDefs().get(i);
      _factory.registerDef(ns.getPrefix(), d.getName(), d);
    }
  }
  
  /**
   * Returns the rule corresponding to the given ID.
   *
   * @param id the identifier of the desired <code>Rule</code>.
   * @return a <code>Rule</code>.
   * @throws IllegalArgumentException if no rule was found for the given
   * identifier.
   */
  public Rule getRule(String id)
  throws IllegalArgumentException {
    Rule rule = (Rule) _rules.get(id);
    
    if (rule == null) {
      throw new IllegalArgumentException("No rule for: " + id);
    }
    
    return rule;
  }
  
  /**
   * Validates the given object, using the ruleset whose identifier
   * is given, for the given <code>Locale</code>. The latter is used
   * to select the proper error messages (in the language that closest
   * match the given Locale).
   *
   * @param ruleSetId the identifier of a <code>RuleSet</code>.
   * @param obj an object to validate.
   * @param locale a <code>Locale</code>.
   *
   * @return a <code>Status</code>.
   */
  public Status validate(String ruleSetId, Object obj, Locale locale) {
    RuleSet           set = getRuleSet(ruleSetId);
    ValidationContext ctx = new ValidationContext(_globals, obj, this, locale);

    set.validate(ctx);
    
    return ctx.getStatus();
  }
  
  /**
  * Validates the given object, using the ruleset whose identifier
  * is given, for the given <code>Locale</code>. The latter is used
  * to select the proper error messages (in the language that closest
  * matches the given Locale).
  *
  * @param ruleSetId the identifier of a <code>RuleSet</code>.
  * @param obj an object to validate.
  * @param locale a <code>Locale</code>.
  * @param contextMap a <code>Map</code> with additional context objects which can be acquired
  * from validation code.
  *
  * @return a <code>Status</code>.
  */
  public Status validate(String ruleSetId, Object obj, Locale locale, Map contextMap) {
    RuleSet           set = getRuleSet(ruleSetId);
    ValidationContext ctx = new ValidationContext(_globals, obj, this, locale, contextMap);
    
    set.validate(ctx);  
    
    return ctx.getStatus();
  }
  /**
   * Loads a ruleset definition file as a resource. The file must in this
   * case be present in the classpath.
   *
   * @param resource the name of a resource.
   * @throws IOException if an IO error occurs while loading the resource, or
   * if no resource exists for the given name.
   * @throws ConfigException if a problem occurs while initializing this
   * instance with the given resource's content.
   */
  public Vlad load(String resource)
  throws IOException, ConfigException {
    InputStream is = loadResource(resource);
    
    if (is == null) {
      throw new FileNotFoundException(resource);
    }
    
    return load(is);
  }
  
  /**
   * Loads a ruleset definition corresponding to the given
   * file.
   *
   * @param f a <code>File</code>
   * @throws IOException if an IO error occurs while loading the file.
   * @throws ConfigException if a problem occurs while initializing this
   * instance with the given file's content.
   */
  public Vlad load(File f) throws IOException, ConfigException {
    return load(new FileInputStream(f));
  }
  
  /**
   * Loads a ruleset definition corresponding to the given
   * stream.
   *
   * @param is an <code>InputStream</code>
   * @throws IOException if an IO error occurs while loading the configuration stream.
   * @throws ConfigException if a problem occurs while initializing this
   * instance with the given stream's content.
   */
  public Vlad load(InputStream is)
  throws IOException, ConfigException {
    Dom4jProcessor p = new Dom4jProcessor(_factory);
    
    try {
      p.process(is);
      
      return this;
    } catch (ProcessingException e) {
      throw new ConfigException("Could not process configuration", e);
    }
  }
  
  /**
   * Loads a rule definition corresponding to the given
   * file.
   *
   * @param f a <code>File</code>
   * @throws IOException if an IO error occurs while loading the file.
   * @throws ConfigException if a problem occurs while initializing this
   * instance with the given file's content.
   */
  public Vlad loadDefs(File f)
  throws IOException, ConfigException {
    return loadDefs(new FileInputStream(f));
  }
  
  
  /**
   * Loads a rule definition file as a resource. The file must in this
   * case be present in the classpath.
   *
   * @param resource the name of a resource.
   * @throws IOException if an IO error occurs while loading the resource, or
   * if no resource exists for the given name.
   * @throws ConfigException if a problem occurs while initializing this
   * instance with the given resource's content.
   */
  
  public Vlad loadDefs(String resource)
  throws IOException, ConfigException {
    InputStream is = loadResource(resource);
    
    if (is == null) {
      throw new FileNotFoundException(resource);
    }
    
    return loadDefs(is);
  }
  
  /**
   * Loads a rule definition corresponding to the given
   * stream.
   *
   * @param is an <code>InputStream</code>
   * @throws IOException if an IO error occurs while loading the configuration stream.
   * @throws ConfigException if a problem occurs while initializing this
   * instance with the given stream's content.
   */
  public Vlad loadDefs(InputStream is)
  throws IOException, ConfigException {
    if (is != null) {
      Dom4jProcessor p = new Dom4jProcessor(_factory);
      
      try {
        p.process(is);
      } catch (ProcessingException e) {
        throw new ConfigException("Could not load definitions", e);
      }
    }
    
    return this;
  }
  
  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String name, Object ruleOrRuleSet)
  throws ConfigurationException {
    if (ruleOrRuleSet instanceof Rule) {
      add((Rule) ruleOrRuleSet);
    } else if (ruleOrRuleSet instanceof RuleSet) {
      addRuleSet((RuleSet) ruleOrRuleSet);
    } else {
      throw new ConfigurationException("Unexpected element: " + name);
    }
  }
  
  private InputStream loadResource(String name) {
    
    InputStream is = Thread.currentThread().getContextClassLoader()
    .getResourceAsStream(name);
    
    if (is == null) {
      is = getClass().getClassLoader().getResourceAsStream(name);
    }
    
    if (is == null) {
      is = Vlad.class.getResourceAsStream(name);
    }
    
    if (is == null) {
      is = ClassLoader.getSystemResourceAsStream(name);
    }
    
    return is;
  }
}
