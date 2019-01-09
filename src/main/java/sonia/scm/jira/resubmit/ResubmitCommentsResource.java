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



package sonia.scm.jira.resubmit;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Resource to resubmit jira comments.
 *
 */
@Path("plugins/jira/resubmit")
public class ResubmitCommentsResource
{

  /** logger for ResubmitCommentsResource */
  private static final Logger logger =
    LoggerFactory.getLogger(ResubmitCommentsResource.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs a new ResubmitCommentsResource.
   *
   * @param resubmitCommentsHandler handler which is able to resubmit comments
   */
  @Inject
  public ResubmitCommentsResource(ResubmitCommentsHandler resubmitCommentsHandler)
  {
    this.resubmitCommentsHandler = resubmitCommentsHandler;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Resubmit all comments that could not be sent at the last time.
   *
   * @return response if the comments could be executed or a returned error 
   *  message.
   *
   * @throws IOException
   */
  @POST
  @Path("all")
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response resubmitAll() throws IOException
  {
    logger.trace("resubmit all stored comments");

    resubmitCommentsHandler.resubmitAll();

    logger.trace("finished sending stored comments");

    return Response.noContent().build();
  }
  
  /**
   * Resubmit all comments ,which are associated with the given repository, 
   * that could not be sent at the last time.
   *
   * @param repositoryId repository id
   * 
   * @return response if the comments could be executed or a returned error 
   *  message.
   *
   * @throws IOException
   */
  @POST
  @Path("repository/{repositoryId}")
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response resubmitAllFromRepository(
    @PathParam("repositoryId") String repositoryId) throws IOException
  {
    logger.trace("resubmit all stored comments of repository {}", repositoryId);

    resubmitCommentsHandler.resubmitAllFromRepository(repositoryId);

    logger.trace("finished sending stored comments of repository {}", repositoryId);

    return Response.noContent().build();
  }
  
  /**
   * Resubmit the comment with the given id that could not be sent at the 
   * last time.
   *
   * @param commentId comment id
   * 
   * @return response if the comment could be executed or a returned error 
   *  message.
   *
   * @throws IOException
   */
  @POST
  @Path("comment/{commentId}")
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response resubmit(@PathParam("commentId") String commentId) throws IOException
  {
    logger.trace("resubmit stored comment {}", commentId);

    Response response;
    try 
    {
      resubmitCommentsHandler.resubmit(commentId);
      response = Response.noContent().build();
    } 
    catch (CommentNotFoundException ex)
    {
      logger.warn("could not find comment with id ".concat(commentId));
      response = Response.status(Status.NOT_FOUND).build();
    }

    logger.trace("finished sending stored comment {}", commentId);

    return response;
  }
  
  /**
   * Remove comment with the given id from the resubmit queue. This method is 
   * triggered from the remove link in the failure e-mail.
   *
   * @param commentId comment id
   * 
   * @return text message response
   */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("comment/{commentId}/remove")
  public Response remove(@PathParam("commentId") String commentId){
    logger.trace("remove stored comment {}", commentId);
    Response response;

    try 
    {
      CommentData data = resubmitCommentsHandler.remove(commentId);
      StringBuilder msg = new StringBuilder("successfully removed comment ");
      msg.append(data.getId()).append(" for jira issue ").append(data.getIssueId());
      
      response = Response.ok(msg.toString()).build();
    } 
    catch (CommentNotFoundException ex)
    {
      String message = "could not find comment with id ".concat(commentId);
      logger.warn(message);
      response = Response.status(Status.NOT_FOUND).entity(message).build();
    }
    return response;
  }

  //~--- fields ---------------------------------------------------------------

  /** resubmit comments handler */
  private final ResubmitCommentsHandler resubmitCommentsHandler;
}
