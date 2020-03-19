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

import com.google.common.base.MoreObjects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jira rest api visibility of a comment.
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "visibility")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestVisibility
{

  /**
   * Constructs a new {@link RestVisibility}.
   */
  RestVisibility()
  {
  }

  /**
   * Constructs a new {@link RestVisibility}.
   * 
   * @param value name of role
   */
  public RestVisibility(String value)
  {
    this.value = value;
  }

  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                  .add("type", type)
                  .add("value", value)
                  .toString();
    //J+
  }

  /**
   * Returns the type of visibility value. The only supported value is currently role.
   * 
   * @return type of visibility value
   */
  public String getType()
  {
    return type;
  }

  /**
   * Returns value of visibility. The only supported values are currently role names.
   * 
   * @return value of visibility
   */
  public String getValue()
  {
    return value;
  }
  
  /** type of visibility */
  private final String type = "role";
  
  /** value of visibility */
  private String value;
}
