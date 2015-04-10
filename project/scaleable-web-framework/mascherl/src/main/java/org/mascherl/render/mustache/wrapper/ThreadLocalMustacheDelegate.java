package org.mascherl.render.mustache.wrapper;

import com.github.mustachejava.Mustache;

/**
 * A wrapper for {@link Mustache} that delegates to a Mustache from a ThreadLocal
 * (i.e. it delegates to a different Mustache instance for every thread).
 *
 * @author Jakob Korherr
 */
public class ThreadLocalMustacheDelegate extends MustacheInterceptorWrapper {

    private final ThreadLocal<Mustache> mustacheThreadLocal = new ThreadLocal<>();

    @Override
    public void init() {
        // do nothing, the delegates must be initialised separately before the are set here
    }

    @Override
    public Mustache getDelegate() {
        return mustacheThreadLocal.get();
    }

    public void setMustache(Mustache mustache) {
        mustacheThreadLocal.set(mustache);
    }

    public void removeMustache() {
        mustacheThreadLocal.remove();
    }

}
