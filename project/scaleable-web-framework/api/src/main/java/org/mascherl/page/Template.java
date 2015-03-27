package org.mascherl.page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the template of a {@link org.mascherl.page.MascherlPage}.
 *
 * @author Jakob Korherr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Template {

    public String value();

}
