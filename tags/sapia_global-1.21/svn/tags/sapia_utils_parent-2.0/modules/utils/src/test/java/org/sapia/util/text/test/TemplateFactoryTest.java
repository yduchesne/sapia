package org.sapia.util.text.test;

// Import of Sun's JDK classes
// ---------------------------
import java.util.Iterator;

// Import of Junit classes
// ---------------------------
import junit.framework.TestCase;
import junit.textui.TestRunner;

// Import of Sapia's classes
// -------------------------
import org.sapia.util.text.ConstantElement;
import org.sapia.util.text.CompositeElement;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateFactory;
import org.sapia.util.text.VariableElement;


/**
 *
 *
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class TemplateFactoryTest extends TestCase {

  private TemplateFactory _theFactory;

  public static void main(String[] args) {
    TestRunner.run(TemplateFactoryTest.class);
  }

  public TemplateFactoryTest(String aName) {
    super(aName);
  }

  public void setUp() {
    _theFactory = new TemplateFactory(
            TemplateFactory.DEFAULT_STARTING_DELIMITER,
            TemplateFactory.DEFAULT_ENDING_DELIMITER);
  }

  public void testParseSimpleText() throws Exception {
    String aContent = "This is a simple text";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;

    assertEquals("The composite element size is not valid ", 1, aCompositeElement.getElements().size());
    assertTrue("The template element is not of a valid type", aCompositeElement.getElements().get(0) instanceof ConstantElement);

    ConstantElement aConstant =  (ConstantElement) aCompositeElement.getElements().get(0);
    assertEquals("The content of the constant element is invalid", aContent, aConstant.getContent());
  }

  public void testParseEmptyText() throws Exception {
    String aContent = "";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;

    assertEquals("The composite element size is not valid ", 0, aCompositeElement.getElements().size());
  }

  public void testParseNullText() throws Exception {
    try {
      TemplateElementIF aTemplate = _theFactory.parse(null);
      fail("Should not parse a null content. Result of parse(): " + aTemplate);
    } catch (IllegalArgumentException iae) {
    }
  }

  public void testParseSimpleVariable() throws Exception {
    String aContent = "${simple.variable}";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 1, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof VariableElement);
    assertEquals("The name of the variable element is invalid", "simple.variable", ((VariableElement) anElement).getName());
  }

  public void testParseEmptyVariable() throws Exception {
    String aContent = "${}";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 1, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element is invalid", aContent, ((ConstantElement) anElement).getContent());
  }

  public void testParseUnclosedVariable() throws Exception {
    String aContent = "${ This is a test";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 1, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element is invalid", aContent, ((ConstantElement) anElement).getContent());
  }

  public void testParseUnclosedEmptyVariable() throws Exception {
    String aContent = "${";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 1, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element is invalid", aContent, ((ConstantElement) anElement).getContent());
  }

  public void testParseUnclosedVariableWithVariable() throws Exception {
    String aContent = "${ This is a text and ${this.a.variable}";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 2, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element is invalid", "${ This is a text and ", ((ConstantElement) anElement).getContent());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof VariableElement);
    assertEquals("The name of the variable element is invalid", "this.a.variable", ((VariableElement) anElement).getName());
  }

  public void testParseSimpleComposite() throws Exception {
    String aContent = "This is a simple ${variable} example";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 3, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element 1 is invalid", "This is a simple ", ((ConstantElement) anElement).getContent());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof VariableElement);
    assertEquals("The name of the variable element 2 is invalid", "variable", ((VariableElement) anElement).getName());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the element 3 is invalid", " example", ((ConstantElement) anElement).getContent());
  }

  public void testParseCompositeWithEmptyVariable() throws Exception {
    String aContent = "This is a simple ${} example";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 3, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element 1 is invalid", "This is a simple ", ((ConstantElement) anElement).getContent());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element 2 is invalid", "${}", ((ConstantElement) anElement).getContent());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the element 3 is invalid", " example", ((ConstantElement) anElement).getContent());
  }

  public void testParseCompositeWithUnclosedVariable() throws Exception {
    String aContent = "This is a complex ${ mixed up example ${for.the} fun of it}";
    TemplateElementIF aTemplate = _theFactory.parse(aContent);

    assertTrue("The template is not of a valid type", aTemplate instanceof CompositeElement);
    CompositeElement aCompositeElement = (CompositeElement) aTemplate;
    assertEquals("The composite element size is not valid ", 4, aCompositeElement.getElements().size());
    Iterator someElements = aCompositeElement.getElements().iterator();

    TemplateElementIF anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element 1 is invalid", "This is a complex ", ((ConstantElement) anElement).getContent());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the constant element 2 is invalid", "${ mixed up example ", ((ConstantElement) anElement).getContent());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof VariableElement);
    assertEquals("The name of the variable element 3 is invalid", "for.the", ((VariableElement) anElement).getName());

    anElement = (TemplateElementIF) someElements.next();
    assertTrue("The template element is not of a valid type", anElement instanceof ConstantElement);
    assertEquals("The content of the element 4 is invalid", " fun of it}", ((ConstantElement) anElement).getContent());
  }

}