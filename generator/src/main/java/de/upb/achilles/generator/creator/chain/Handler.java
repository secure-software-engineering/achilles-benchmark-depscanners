package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.TestFixtureModel;

import javax.annotation.Nonnull;

public abstract class Handler {
  protected Handler nextHandler;

  public void setNext(Handler successor) {
    nextHandler = successor;
  }

  public void handleRequest(@Nonnull TestFixtureModel request) throws JarModificationException {
    if (this.canHandle(request)) {
      handle(request);
    }
    if (nextHandler != null) {
      nextHandler.handleRequest(request);
    }
  }

  protected abstract boolean canHandle(TestFixtureModel request);

  protected abstract void handle(TestFixtureModel requTestFixtureModel)
      throws JarModificationException;
}
