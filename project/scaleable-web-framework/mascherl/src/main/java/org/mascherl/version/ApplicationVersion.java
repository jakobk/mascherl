package org.mascherl.version;

/**
 * Type-safe reference to the deployed version of the application.
 *
 * @author Jakob Korherr
 */
public class ApplicationVersion {

    private final String version;

    public ApplicationVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ApplicationVersion{" +
                "version='" + version + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationVersion that = (ApplicationVersion) o;

        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return version != null ? version.hashCode() : 0;
    }
}
