/**
 * Copyright (c) 2014, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided with the distribution. 3. Neither the
 * name of SCM-Manager; nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira.rest;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jira rest api comment.
 *
 * @author Sebastian Sdorra
 *
 * TODO remove the JsonIgnoreProperties, with the release of SCM-Manager 1.47.
 */
@XmlRootElement(name = "comment")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestComment
{
  /**
   * Constructs a new {@link RestComment}.
   */
  RestComment() {}

  /**
   * Constructs a new {@link RestComment}.
   * 
   * @param body comment body
   */
  public RestComment(String body)
  {
    this(body, null);
  }

  /**
   * Constructs a new {@link RestComment}.
   * 
   * @param body comment body
   * @param role name of role for visibility
   */
  public RestComment(String body, String role)
  {
    this.body = body;
    if (!Strings.isNullOrEmpty(role)){
      this.visibility = new RestVisibility(role);
    }
  }
  
  //~--- methods --------------------------------------------------------------

  @Override
  public String toString()
  {
    //J-
    return Objects.toStringHelper(this)
                  .add("id", id)
                  .add("body", body)
                  .add("visibility", visibility)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns body of the comment.
   *
   * @return body comment
   */
  public String getBody()
  {
    return body;
  }

  /**
   * Returns the id of the comment.
   *
   * @return comment id
   */
  public String getId()
  {
    return id;
  }

  /**
   * Returns visibility of comment.
   * 
   * @return visibility of comment
   */
  public RestVisibility getVisibility()
  {
    return visibility;
  }

  //~--- fields ---------------------------------------------------------------

  /** comment body */
  private String body;

  /** id of comment */
  private String id;
  
  /** visibility of comment */
  private RestVisibility visibility;
}
