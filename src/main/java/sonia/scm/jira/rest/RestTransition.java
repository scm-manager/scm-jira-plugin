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

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jira Rest Transition.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "transition")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestTransition
{

  /**
   * Constructs a new {@link RestTransition}.
   */
  RestTransition() {}

  /**
   * Constructs a new {@link RestTransition}.
   *
   * @param id transition id
   */
  public RestTransition(String id)
  {
    this.id = id;
  }

  //~--- methods --------------------------------------------------------------

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("id", id)
                  .add("name", name)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns id of rest transition.
   *
   * @return id of transition
   */
  public String getId()
  {
    return id;
  }

  /**
   * Returns name of rest transition.
   *
   * @return name of transition
   */
  public String getName()
  {
    return name;
  }

  //~--- fields ---------------------------------------------------------------

  /** transition id */
  private String id;

  /** transition name */
  private String name;
}
