/**
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package sonia.scm.jira.rest;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

//~--- JDK imports ------------------------------------------------------------

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper object jira rest transitions.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "transitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestTransitions implements Iterable<RestTransition>
{
 
  /**
   * Constructs a new {@link RestTransitions}.
   */
  RestTransitions() {}

  /**
   * Returns an {@link Iterator} over a list of jira rest transitions.
   * 
   * @return {@link Iterator} for transitions
   */
  @Override
  public Iterator<RestTransition> iterator()
  {
    return getTransitions().iterator();
  }

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("transitions", transitions)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns a {@link List} jira rest transitions.
   * 
   * @return {@link List} jira rest transitions
   */
  public List<RestTransition> getTransitions()
  {
    if (transitions == null)
    {
      transitions = Lists.newArrayList();
    }

    return transitions;
  }

  //~--- fields ---------------------------------------------------------------

  /** list of jira rest transitions */
  private List<RestTransition> transitions;
}
