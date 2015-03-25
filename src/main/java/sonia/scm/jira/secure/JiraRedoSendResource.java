/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.jira.secure;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.RepositoryException;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource to resent jira comments.
 *
 */
@Path("plugins/jira/redo-send")
public class JiraRedoSendResource
{

  /** logger for JiraRedoSendResource */
  private static final Logger logger =
    LoggerFactory.getLogger(JiraRedoSendResource.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new JiraRedoSendResource.
   *
   * @param replayCommentsHandler handler which is able to replay comments
   */
  @Inject
  public JiraRedoSendResource(ReplayCommentsHandler replayCommentsHandler)
  {
    this.replayCommentsHandler = replayCommentsHandler;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Resent all comments that could not be sent at the last time.
   * Delete the corresponding file in success.
   *
   * @return Response if the comments could be executed or a returned error 
   *  message.
   *
   * @throws IOException
   * @throws RepositoryException
   */
  @POST
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response replayComments() throws IOException, RepositoryException
  {
    logger.trace("redo started by POST command");

    replayCommentsHandler.replayAll();

    logger.trace("redo ended command");

    return Response.noContent().build();
  }

  //~--- fields ---------------------------------------------------------------

  /** replay comments handler */
  private final ReplayCommentsHandler replayCommentsHandler;
}
