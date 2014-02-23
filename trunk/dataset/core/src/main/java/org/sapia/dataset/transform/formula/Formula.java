package org.sapia.dataset.transform.formula;

import org.sapia.dataset.func.ArgFunction;

/**
 * Implements a {@link Formula}. 
 *  
 * @author yduchesne
 *
 * @param <RowResult> the {@link org.sapia.dataset.RowResult} that this function 
 * takes as input.
 */
public interface Formula<RowResult> extends ArgFunction<RowResult, Object> {

}
