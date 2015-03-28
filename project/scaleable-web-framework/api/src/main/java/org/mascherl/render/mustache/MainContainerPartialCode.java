package org.mascherl.render.mustache;

import com.github.mustachejava.TemplateContext;
import com.github.mustachejava.codes.PartialCode;
import org.mascherl.MascherlConstants;

/**
 * Special {@link PartialCode}, which includes the current pageTemplate instead of a static resource.
 *
 * @author Jakob Korherr
 */
public class MainContainerPartialCode extends PartialCode {

    private final MascherlMustacheFactory mascherlMustacheFactory;

    public MainContainerPartialCode(TemplateContext tc, MascherlMustacheFactory cf) {
        super(tc, cf, MascherlConstants.MAIN_CONTAINER);
        mascherlMustacheFactory = cf;
    }

    @Override
    protected String partialName() {
        return mascherlMustacheFactory.getPageTemplate();
    }
}
