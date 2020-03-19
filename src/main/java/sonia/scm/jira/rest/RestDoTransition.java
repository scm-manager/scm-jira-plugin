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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper object to handle transition changes.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestDoTransition
{
  
  /**
   * Constructs a new {@link RestDoTransition}.
   */
  RestDoTransition() {}

  /**
   * Constructs a new {@link RestDoTransition}.
   * 
   * @param id transition id
   */
  public RestDoTransition(String id)
  {
    this.transition = new RestTransition(id);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns jira rest transition.
   * 
   * @return jira rest transitions
   */
  public RestTransition getTransition()
  {
    return transition;
  }

  //~--- fields ---------------------------------------------------------------

  /** wrapped transition */
  private RestTransition transition;
}
