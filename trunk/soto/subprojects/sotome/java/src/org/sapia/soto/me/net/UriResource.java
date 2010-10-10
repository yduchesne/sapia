package org.sapia.soto.me.net;

import java.io.IOException;
import java.io.InputStream;

import javolution.text.Text;
import javolution.text.TextBuilder;

/**
 * Defines an URI resource that follows the format:
 * <blockquote>
 * [<i>scheme</i><tt><b>:</b></tt>][<tt><b>//</b></tt><i>authority</i>][<i>path</i>][<tt><b>?</b></tt><i>query</i>][<tt><b>#</b></tt><i>fragment</i>]
 * </blockquote>
 *
 * @author Jean-Cédric Desrochers
 */
public class UriResource implements Resource {

  public static final String SCHEME_RESOURCE = "resource";

  /** The scheme of the URI. */
  private String _scheme;
  
  /** The authority of the URI. */
  private String _authority;
  
  /** The path of the URI. */
  private String _path;
  
  /** The query of the URI. */
  private String _query;
  
  /** The fragment of the URI. */
  private String _fragment;
  
  /** Indicates if this URI has an absolute parh. */
  private boolean _isAbsolute;
  
  /**
   * Utility method that returns true if the string URI passed in 
   * is define as a resource scheme URI, false otherwise.
   * 
   * @param anUri The string URI to validate.
   * @return True if the string URI is a resource scheme URI.
   */
  public static boolean isResourceUri(String anUri) {
    return (anUri != null &&
            anUri.startsWith(SCHEME_RESOURCE) &&
            anUri.charAt(8) == ':');
  }
  
  /**
   * Factory method that parse a string URI and creates an UriResource instance.
   * 
   * @param anUri The String URI to parse.
   * @return The created {@link UriResource} instance.
   */
  public static UriResource parseString(String anUri) {
    UriResource resource = new UriResource();
    
    int schemeIndex = anUri.indexOf(":");
    if (schemeIndex >= 0) {
      resource.setScheme(anUri.substring(0, schemeIndex));
    }
    
    int authorityIndex = anUri.indexOf("//", schemeIndex+1);
    int pathIndex = anUri.indexOf("/", authorityIndex+2);
    if (authorityIndex >= 0) {
      resource.setAuthority(anUri.substring(authorityIndex+2, pathIndex));
      resource.setAbsolute(true);
    } else {
      pathIndex = anUri.indexOf("/");
      if ((schemeIndex+1) == pathIndex) {
        resource.setAbsolute(true);        
      } else {
        resource.setAbsolute(false);
        pathIndex = schemeIndex+1;
      }
    }
    
    int queryIndex = anUri.indexOf("?", pathIndex);
    if (queryIndex == -1) {
      int fragmentIndex = anUri.indexOf("#", queryIndex);
      if (fragmentIndex == -1) {
        if (pathIndex == -1) {
          resource.setPath(anUri);
        } else {
          resource.setPath(anUri.substring(pathIndex));
        }
      } else {
        resource.setPath(anUri.substring(pathIndex, fragmentIndex));
        resource.setFragment(anUri.substring(fragmentIndex+1));
      }
    } else {
      resource.setPath(anUri.substring(pathIndex, queryIndex));
      
      int fragmentIndex = anUri.indexOf("#", queryIndex);
      if (fragmentIndex == -1) {
        resource.setQuery(anUri.substring(queryIndex+1));
      } else {
        resource.setQuery(anUri.substring(queryIndex+1, fragmentIndex));
        resource.setFragment(anUri.substring(fragmentIndex+1));
      }
    }
    
    return resource;
  }
  
  /**
   * Creates a relative URI resource based on the 
   *  
   * @param aRootResource The resource from which the absolute root will be taken.
   * @param anUri The string URI from which
   * @return
   */
  public static UriResource parseRelative(UriResource aRootResource, String anUri) {
    UriResource resource = parseString(anUri);
    if (!resource.isAbsolute() && aRootResource != null) {
      Text rootPath = Text.valueOf(aRootResource.getPath());
      int index = rootPath.lastIndexOf(Text.intern("/"));
      resource.setPath(
              rootPath.subtext(0, index+1).toString() + resource.getPath());
      resource.setAuthority(aRootResource.getAuthority());
    }
    
    return resource;
  }
  
  /**
   * Creates a new UriResource instance.
   * 
   * @param aUri The strin URI of the resource.
   */
  public UriResource() {
  }

  /**
   * Returns the authority value.
   *
   * @return The authority value.
   */
  public String getAuthority() {
    return _authority;
  }

  /**
   * Changes the value of the authority.
   *
   * @param aAuthority The new authority value.
   */
  public void setAuthority(String aAuthority) {
    _authority = aAuthority;
  }

  /**
   * Returns the fragment value.
   *
   * @return The fragment value.
   */
  public String getFragment() {
    return _fragment;
  }

  /**
   * Changes the value of the fragment.
   *
   * @param aFragment The new fragment value.
   */
  public void setFragment(String aFragment) {
    _fragment = aFragment;
  }

  /**
   * Returns the path value.
   *
   * @return The path value.
   */
  public String getPath() {
    return _path;
  }

  /**
   * Changes the value of the path.
   *
   * @param aPath The new path value.
   */
  public void setPath(String aPath) {
    _path = aPath;
  }

  /**
   * Returns the query value.
   *
   * @return The query value.
   */
  public String getQuery() {
    return _query;
  }

  /**
   * Changes the value of the query.
   *
   * @param aQuery The new query value.
   */
  public void setQuery(String aQuery) {
    _query = aQuery;
  }

  /**
   * Returns the scheme value.
   *
   * @return The scheme value.
   */
  public String getScheme() {
    return _scheme;
  }

  /**
   * Changes the value of the scheme.
   *
   * @param aScheme The new scheme value.
   */
  public void setScheme(String aScheme) {
    _scheme = aScheme;
  }
  
  public boolean isAbsolute() {
    return _isAbsolute;
  }
  
  public void setAbsolute(boolean isAbsolute) {
    _isAbsolute = isAbsolute;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    TextBuilder builder = new TextBuilder();
    if (_scheme != null) {
      builder.append(_scheme).append(":");
    }
    if (_authority != null) {
      builder.append("//").append(_authority);
    }
    builder.append(_path);
    if (_query != null) {
      builder.append("?").append(_query);
    }
    if (_fragment != null) {
      builder.append("#").append(_fragment);
    }
    
    return builder.toString();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.net.Resource#getInputStream()
   */
  public InputStream getInputStream() throws IOException {
    if (_scheme == null || _scheme.equals(SCHEME_RESOURCE)) {
      return getClass().getResourceAsStream(_path);
    } else {
      throw new ResourceNotFoundException("Could not open an input stream over the resource '" + toString() + "'");
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.net.Resource#getURI()
   */
  public String getURI() {
    return toString();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.net.Resource#toAbsoluteFrom(org.sapia.soto.me.net.Resource)
   */
  public Resource toAbsoluteFrom(Resource aRootResource) {
    if (!(aRootResource instanceof UriResource)) {
      throw new IllegalArgumentException("The root resource passed in is not a UriResource but was " + aRootResource);
    }
    
    return UriResource.parseRelative((UriResource) aRootResource, toString());
  }
}