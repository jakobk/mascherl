package org.mascherl.render.mustache.wrapper;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheVisitor;
import com.github.mustachejava.TemplateContext;

/**
 * Wrapper for {@link MustacheVisitor}.
 *
 * @author Jakob Korherr
 */
public class MustacheVisitorWrapper implements MustacheVisitor {

    private final MustacheVisitor delegate;

    public MustacheVisitorWrapper(MustacheVisitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mustache mustache(TemplateContext templateContext) {
        return delegate.mustache(templateContext);
    }

    @Override
    public void iterable(TemplateContext templateContext, String variable, Mustache mustache) {
        delegate.iterable(templateContext, variable, mustache);
    }

    @Override
    public void notIterable(TemplateContext templateContext, String variable, Mustache mustache) {
        delegate.notIterable(templateContext, variable, mustache);
    }

    @Override
    public void partial(TemplateContext templateContext, String variable) {
        delegate.partial(templateContext, variable);
    }

    @Override
    public void value(TemplateContext templateContext, String variable, boolean encoded) {
        delegate.value(templateContext, variable, encoded);
    }

    @Override
    public void write(TemplateContext templateContext, String text) {
        delegate.write(templateContext, text);
    }

    @Override
    public void pragma(TemplateContext templateContext, String pragma, String args) {
        delegate.pragma(templateContext, pragma, args);
    }

    @Override
    public void eof(TemplateContext templateContext) {
        delegate.eof(templateContext);
    }

    @Override
    public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
        delegate.extend(templateContext, variable, mustache);
    }

    @Override
    public void name(TemplateContext templateContext, String variable, Mustache mustache) {
        delegate.name(templateContext, variable, mustache);
    }

    @Override
    public void comment(TemplateContext templateContext, String comment) {
        delegate.comment(templateContext, comment);
    }
}
