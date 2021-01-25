package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ResourcesNode extends ConfigurationNode
{
    public static final String NAME = "resources";

    @Override
    public List<Class> getAllowedSubnodeClasses() {
        return Arrays.asList(new Class[] { ResourceTestNode.class});
    }

    @Override
    public List<Property> getProperties() {
        return new ArrayList<>();
    }

    /**
     * Adds the nodes from the Yaml.
     *
     * @param subnodes subnodes
     * @throws YamlParserException error while parsing
     */
    public void addNodesFromYaml(List<LinkedHashMap> subnodes) throws YamlParserException
    {
        for (LinkedHashMap subnode : subnodes) {
            if (ForLoopNode.isValidForLoopName(subnode)) {
                addForLoopNode(subnode);
            }
            else {
                addTestNode(subnode);
            }
        }
    }

    /**
     * Adds a new for-loop node.
     *
     * @param subnodes subnodes
     * @param name node name
     * @throws YamlParserException error during parsing
     */
    private void addForLoopNode(LinkedHashMap properties) throws YamlParserException {
        ForLoopNode loopNode = new ForLoopNode(properties, this);
        addSubnode(loopNode);
    }

    /**
     * Adds a new page test node.
     *
     * @param subnodes subnodes
     * @param name node name
     * @throws YamlParserException
     */
    private void addTestNode(LinkedHashMap properties) throws YamlParserException {
        ResourceTestNode pageTestNode = new ResourceTestNode();
        pageTestNode.setPropertiesFromYaml(properties);
        addSubnode(pageTestNode);
    }

    @Override
    public String getNodeName() {
        return NAME;
    }
}
