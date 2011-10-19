/**
 * 
 */
package org.sapia.soto.me.net;

import junit.framework.TestCase;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class UriResourceTest extends TestCase {

  public void testParse_fullUri() {
    assertUriResource("resource", "sapia-oss.org", "/sna/foo/image", true, "load", "preview",
            UriResource.parseString("resource://sapia-oss.org/sna/foo/image?load#preview"));
  }

  public void testParse_withoutFragment() {
    assertUriResource("resource", "sapia-oss.org", "/sna/foo/image", true, "load", null,
            UriResource.parseString("resource://sapia-oss.org/sna/foo/image?load"));
  }

  public void testParse_withEmptyFragment() {
    assertUriResource("resource", "sapia-oss.org", "/sna/foo/image", true, "load", "",
            UriResource.parseString("resource://sapia-oss.org/sna/foo/image?load#"));
  }

  public void testParse_withoutQuery() {
    assertUriResource("resource", "sapia-oss.org", "/sna/foo/image", true, null, "splash",
            UriResource.parseString("resource://sapia-oss.org/sna/foo/image#splash"));
  }

  public void testParse_withEmptyQuery() {
    assertUriResource("resource", "sapia-oss.org", "/sna/foo/image", true, "", "splash",
            UriResource.parseString("resource://sapia-oss.org/sna/foo/image?#splash"));
  }

  public void testParse_withoutAuthority() {
    assertUriResource("resource", null, "/sna/foo/image", true, "get", "splash",
            UriResource.parseString("resource:/sna/foo/image?get#splash"));
  }

  public void testParse_withNullAuthority() {
    assertUriResource("resource", "", "/sna/foo/image", true, "get", "splash",
            UriResource.parseString("resource:///sna/foo/image?get#splash"));
  }

  public void testParse_withoutScheme() {
    assertUriResource(null, "sapia-oss.org", "/sna/foo/image", true, "get", "splash",
            UriResource.parseString("//sapia-oss.org/sna/foo/image?get#splash"));
  }

  public void testParse_withNullScheme() {
    assertUriResource("", "sapia-oss.org", "/sna/foo/image", true, "get", "splash",
            UriResource.parseString("://sapia-oss.org/sna/foo/image?get#splash"));
  }

  public void testParse_onlyAbsolutePath() {
    assertUriResource(null, null, "/sna/foo/image", true, null, null,
            UriResource.parseString("/sna/foo/image"));
  }

  public void testParse_onlyRelativePath() {
    assertUriResource(null, null, "image.html", false, null, null,
            UriResource.parseString("image.html"));
  }

  public void testParse_schemeAndRelativePath() {
    assertUriResource("resource", null, "config/main.xml", false, null, null,
            UriResource.parseString("resource:config/main.xml"));
  }

  public void testParse_schemeAndAbsolutePath() {
    assertUriResource("resource", null, "/root/config/main.xml", true, null, null,
            UriResource.parseString("resource:/root/config/main.xml"));
  }

  public void testToString_fullUri() {
    assertEquals("The string of the URI resource is invalid", "resource://sapia-oss.org/sna/foo/image?load#preview",
            UriResource.parseString("resource://sapia-oss.org/sna/foo/image?load#preview").toString());
  }

  public void testToString_onlyPathAndFragment() {
    assertEquals("The string of the URI resource is invalid", "/sna/foo/image#preview",
            UriResource.parseString("/sna/foo/image#preview").toString());
  }

  public void testToString_onlySchemeAndPath() {
    assertEquals("The string of the URI resource is invalid", "resource:/sna/foo/image",
            UriResource.parseString("resource:/sna/foo/image").toString());
  }
  
  public void testParseRelative_absoluteURI() {
    assertUriResource("resource", null, "/root/config/main.xml", true, null, null,
            UriResource.parseRelative(null, "resource:/root/config/main.xml"));
  }
  
  public void testParseRelative_noRootResource() {
    assertUriResource("resource", null, "config/main.xml", false, null, null,
            UriResource.parseRelative(null, "resource:config/main.xml"));
  }
  
  public void testParseRelative_relativeRootResource() {
    assertUriResource("resource", null, "relative/path/config/main.xml", false, null, null,
            UriResource.parseRelative(UriResource.parseString("resource:relative/path/item"), "resource:config/main.xml"));
  }
  
  public void testParseRelative_absoluteRootResource() {
    assertUriResource("resource", null, "/org/sapia/soto/me/config/main.xml", false, null, null,
            UriResource.parseRelative(UriResource.parseString("resource:/org/sapia/soto/me/loads.xml"), "resource:config/main.xml"));
  }
  
  public void testParseRelative_absoluteRootResourceWithAuthority() {
    assertUriResource("resource", "www.sapia-oss.org", "/org/sapia/soto/me/config/main.xml", false, null, null,
            UriResource.parseRelative(UriResource.parseString("resource://www.sapia-oss.org/org/sapia/soto/me/loads.xml?q=1&w=2#machup"), "resource:config/main.xml"));
  }
  
  public void testGetInputStream() throws Exception {
    UriResource resource = UriResource.parseString("/org/sapia/soto/me/net/SotoSample.soto.xml");
    assertNotNull("The input stream of the resource should not be null", resource.getInputStream());
  }
  
  
  public static void assertUriResource(String eScheme, String eAuthority, String ePath,
          boolean eIsAbsolute, String eQuery, String eFragment, UriResource actual) {
    assertNotNull("The actual URI resource should not be null", actual);
    assertEquals("The scheme of the URI resource is invalid", eScheme, actual.getScheme());
    assertEquals("The authority of the URI resource is invalid", eAuthority, actual.getAuthority());
    assertEquals("The path of the URI resource is invalid", ePath, actual.getPath());
    assertEquals("The is absolute indicator of the URI resource is invalid", eIsAbsolute, actual.isAbsolute());
    assertEquals("The query of the URI resource is invalid", eQuery, actual.getQuery());
    assertEquals("The fragment of the URI resource is invalid", eFragment, actual.getFragment());
  }
}
