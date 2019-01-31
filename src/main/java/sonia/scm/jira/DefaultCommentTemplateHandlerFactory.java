package sonia.scm.jira;

import sonia.scm.issuetracker.LinkHandler;
import sonia.scm.template.TemplateEngineFactory;

import javax.inject.Inject;

public class DefaultCommentTemplateHandlerFactory implements CommentTemplateHandlerFactory {

  private final TemplateEngineFactory templateEngineFactory;
  private final LinkHandler linkHandler;

  @Inject
  public DefaultCommentTemplateHandlerFactory(TemplateEngineFactory templateEngineFactory, LinkHandler linkHandler) {
    this.templateEngineFactory = templateEngineFactory;
    this.linkHandler = linkHandler;
  }

  @Override
  public CommentTemplateHandler create(CommentTemplate template) {
    return new DefaultCommentTemplateHandler(templateEngineFactory, linkHandler){
      @Override
      CommentTemplate createTemplate() {
        return template;
      }
    };
  }
}
