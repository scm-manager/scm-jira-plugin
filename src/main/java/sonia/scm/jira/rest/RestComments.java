/*
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
 * Wrapper object for jira rest comments.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "comments")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestComments implements Iterable<RestComment>
{

  /**
   * Constructs a new {@link RestComment}.
   */
  RestComments() {}

  //~--- methods --------------------------------------------------------------

  /**
   * Returns {@link Iterator} for comments.
   *
   *
   * @return iterator for the list of comments
   */
  @Override
  public Iterator<RestComment> iterator()
  {
    return getComments().iterator();
  }

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("comments", comments)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns list of jira comments.
   *
   *
   * @return list of comments
   */
  public List<RestComment> getComments()
  {
    if (comments == null)
    {
      comments = Lists.newArrayList();
    }

    return comments;
  }

  //~--- fields ---------------------------------------------------------------

  /** list of comments */
  private List<RestComment> comments;
}
