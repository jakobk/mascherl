package org.mascherl.version;

/**
 * Provider for the current version of the application using Mascherl.
 *
 * An own implementation of this class can be registered via the ServiceLoader mechanism (META-INF/services),
 * and will then be picked up by the initializer of Mascherl.
 *
 * @author Jakob Korherr
 */
public interface ApplicationVersionProvider {

    public ApplicationVersion getApplicationVersion();

}
