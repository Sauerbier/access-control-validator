package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResourceTestNode extends ConfigurationNode
{

    public static final String SIMULATE = "simulate";
    public static final String PROPERTY_NAMES_MODIFY = "propertyNamesModify";
    public static final String PERMISSION = "permission";
    public static final String ACTIONS = "actions";
    public static final String PATH = "path";
    public static final String DEACTIVATE = "isDeactivate";

    Map<String, Property> properties = new LinkedHashMap();

    public ResourceTestNode() {
        super();
        properties.put(PATH, new Property(PATH));
        properties.put(ACTIONS, new Property(ACTIONS));
        properties.put(PERMISSION, new Property(PERMISSION));
        properties.put(PROPERTY_NAMES_MODIFY, new Property(PROPERTY_NAMES_MODIFY));
        properties.put(SIMULATE, new Property(SIMULATE));
        properties.put(DEACTIVATE, new Property(DEACTIVATE));
    }

    /**
     * Copy constructor.
     *
     * @param node original node.
     */
    public ResourceTestNode(ResourceTestNode node) {
        super();
        List<Property> copiedProperties = copyProperties(node);
        properties = new LinkedHashMap<>();
        for (Property property : copiedProperties) {
            properties.put(property.getName(), property);
        }
    }

    @Override
    public List<Class> getAllowedSubnodeClasses() {
        return new ArrayList<>();
    }

    @Override
    public List<Property> getProperties() {
        return new ArrayList<>(properties.values());
    }
    public Map<String, Property> getPropertiesMap() {
        return this.properties;
    }

    @Override
    public String getNodeName() {
        return null;
    }

    /**
     * Adds the properties from the Yaml.
     *
     * @param properties properties
     * @throws YamlParserException error while parsing
     */
    public void setPropertiesFromYaml(LinkedHashMap properties) throws YamlParserException {
        for (Object key : properties.keySet()) {
            // TODO validate input (actions, ...)
            String keyString = (String) key;
            String value = properties.get(keyString).toString();
            setProperty(keyString, value);
        }
    }

    private void setProperty(String name, String value) throws YamlParserException {
        if (properties.containsKey(name)) {
            properties.get(name).setValue(value);
            return;
        }
        throw new YamlParserException("Unknown property: " + name);
    }

    /**
     * Returns if the actions contain a create action.
     *
     * @return is isPageCreateTest
     * @throws YamlParserException error getting actions
     */
    public boolean isNodeCreateTest() throws YamlParserException {
        return getActions().contains(ResourceTestAction.CREATE);
    }

    /**
     * Returns if the actions contain a read action.
     *
     * @return is isPageReadTest
     * @throws YamlParserException error getting actions
     */
    public boolean isNodeReadTest() throws YamlParserException {
        return getActions().contains(ResourceTestAction.READ);
    }

    /**
     * Returns if the actions contain a delete action.
     *
     * @return is isPageDeleteTest
     * @throws YamlParserException error getting actions
     */
    public boolean isNodeDeleteTest() throws YamlParserException {
        return getActions().contains(ResourceTestAction.DELETE);
    }

    /**
     * Returns if the actions contain a modify action.
     *
     * @return is isPageModifyTest
     * @throws YamlParserException error getting actions
     */
    public boolean isNodeModifyTest() throws YamlParserException
    {
        return getActions().contains(ResourceTestAction.MODIFY);
    }

    /**
     * Returns if the actions contain a modify action.
     *
     * @return is isAclReadTest
     * @throws YamlParserException error getting actions
     */
    public boolean isAclReadTest() throws YamlParserException {
        return getActions().contains(ResourceTestAction.READ_ACL);
    }

    /**
     * Returns if the actions contain a acl write action.
     *
     * @return is isAclWriteTest
     * @throws YamlParserException error getting actions
     */
    public boolean isAclWriteTest() throws YamlParserException {
        return getActions().contains(ResourceTestAction.WRITE_ACL);
    }

    /**
     * Returns the list of actions.
     *
     * @return actions
     * @throws YamlParserException invalid actions
     */
    private List<ResourceTestAction> getActions() throws YamlParserException {
        Property actionProperty = properties.get(ACTIONS);
        String[] actionLabels = actionProperty.getValue().split("[ ,]+");
        List<ResourceTestAction> actions = new ArrayList<>();
        for (String label : actionLabels) {
            actions.add(ResourceTestAction.fromLabel(label));
        }
        return actions;
    }

    /**
     * Returns the path property.
     *
     * @return path
     */
    public String getPath() {
        return properties.get(PATH).getValue();
    }

    /**
     * Returns if this is an allow or deny type.
     *
     * @return is allow type
     * @throws YamlParserException invalid type
     */
    public boolean isAllow() throws YamlParserException {
        return PageTestPermission.fromLabel(properties.get(PERMISSION).getValue()).equals(PageTestPermission.ALLOW);
    }

    /**
     * Returns if this is a test to be simulated.
     *
     * @return run simulation
     * @throws YamlParserException invalid type
     */
    public boolean isSimulate() throws YamlParserException {
        String value = properties.get(SIMULATE).getValue();
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        if ("true".equals(value)) {
            return true;
        }
        else if ("false".equals(value)) {
            return false;
        }
        throw new YamlParserException("Invalid value for " + SIMULATE + ": " + value);
    }

    public boolean isDeactivate() throws YamlParserException {
        String value = properties.get(DEACTIVATE).getValue();
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        if ("true".equals(value)) {
            return true;
        }
        else if ("false".equals(value)) {
            return false;
        }
        throw new YamlParserException("Invalid value for " + DEACTIVATE + ": " + value);
    }

}
