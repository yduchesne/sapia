package org.sapia.dataset.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the chain-of-responsability design pattern in a generic way: an instance
 * of this class is composed of {@link Link} instances. A link is provided with an
 * application-provided context. The link is expected to indicate if it accepts the context
 * or not. The first link that accepts the context is returned to the caller.
 * 
 * @see #select(Object)
 * @see #selectNotNull(Object)
 * @see Link#accepts(Object)
 * 
 * @author yduchesne
 *
 */
public class ChainOR<C> {

  /**
   * Models a link in a chain of responsability.
   */
  public interface Link<C> {
    
    /**
     * @param context an arbitrary context.
     * @return <code>true</code> if this instance accepts the given context.
     */
    public boolean accepts(C context);
  }

  // ==========================================================================
  
  private List<Link<C>> links = new ArrayList<>();
  
  /**
   * @param link {@link Link} to add to this instance.
   * @return this instance.
   */
  public ChainOR<C> addLink(Link<C> link) {
    this.links.add(link);
    return this;
  }
  
  /**
   * @param context a context.
   * @return the {@link Link} that accepts the given context, or <code>null</code>
   * if no such link exists.
   */
  public Link<C> select(C context) {
    for (Link<C> l : links) {
      if (l.accepts(context)) {
        return l;
      }
    }
    return null;
  }
  
  /**
   * @param context a context to match.
   * @return the {@link Link} that accepted the given context.
   * @throws IllegalArgumentException if no such link could be found.
   */
  public Link<C> selectNotNull(C context) {
    return Checks.notNull(select(context), "No instance matches the given context");
  }
  
}
