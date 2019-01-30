package sonia.scm.jira;

public interface CommentTemplateHandlerFactory {

  CommentTemplateHandler create(CommentTemplate template);
}
