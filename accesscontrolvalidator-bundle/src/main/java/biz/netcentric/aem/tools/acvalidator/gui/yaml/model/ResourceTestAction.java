package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

public enum ResourceTestAction
{

    READ("read"),
    WRITE("write"),
    CREATE("create"),
    MODIFY("modify"),
    DELETE("delete"),
    READ_ACL("readACL"),
    WRITE_ACL("writeACL");

    private String label;

    /**
     * Constructor
     *
     * @param label label
     */
    private ResourceTestAction(String label) {
        this.label = label;
    }

    /**
     * Returns the label.
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the action by checking the label.
     *
     * @param label label
     * @return action
     * @throws YamlParserException invalid label
     */
    public static ResourceTestAction fromLabel(String label) throws YamlParserException
    {
        for (ResourceTestAction action : values()) {
            if (action.label.equals(label)) {
                return action;
            }
        }
        throw new YamlParserException("Invalid page test action: " + label);
    }
}
