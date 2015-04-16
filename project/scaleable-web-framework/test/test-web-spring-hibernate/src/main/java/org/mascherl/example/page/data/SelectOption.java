package org.mascherl.example.page.data;

/**
 * Model class for a select option.
 *
 * @author Jakob Korherr
 */
public class SelectOption {

    private final String label;
    private final String value;
    private final boolean selected;

    public SelectOption(String label, String value, boolean selected) {
        this.label = label;
        this.value = value;
        this.selected = selected;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public boolean isSelected() {
        return selected;
    }

}
