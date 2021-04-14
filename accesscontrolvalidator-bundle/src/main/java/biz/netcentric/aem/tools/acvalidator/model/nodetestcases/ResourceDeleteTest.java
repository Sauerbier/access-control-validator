/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model.nodetestcases;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;
import com.day.cq.wcm.api.WCMException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import java.util.HashMap;

/**
 * Checks if a page can(not) be deleted.
 *
 * @author Roland Gruber
 */
public class ResourceDeleteTest extends ResourceTestCase implements SimulatableTest, Testable {

    private final Logger LOG = LoggerFactory.getLogger(ResourceDeleteTest.class);

    private String path;
    private Authorizable authorizable;
    private String errorMessage;
    private Resource tmpFolder;

    /**
     * Constructor
     *
     * @param path     page path
     * @param isAllow  test for allow
     * @param simulate simulate write
     */
    public ResourceDeleteTest(String path, boolean isAllow, boolean simulate) {
        super(path, isAllow, "delete");
        this.path = path;
        this.simulate = simulate;
        this.errorMessage = StringUtils.EMPTY;
    }

    @Override
    public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
        boolean isOk = false;

        if (isNodeExisting(serviceResourcerResolver)) {
            if (this.simulate) {
                tmpFolder = serviceResourcerResolver.getResource("/tmp/ac-tests");
                try {
                    isSimulateSuccess = deletePage(serviceResourcerResolver, testUserResolver);
                } catch (WCMException e) {
                    LOG.error("Exception in PageDeleteTest: ", e);
                    throw new RepositoryException(e);
                }
            }
            isOk = isOk(serviceResourcerResolver, authorizable);
        }
        return new TestResult(authorizable.getID(), "Resource Delete Test", " path: " + this.path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk, this.errorMessage);
    }

    @Override
    public boolean isSimulateSuccess() {
        return this.isSimulateSuccess;
    }

    private boolean isNodeExisting(ResourceResolver serviceResourcerResolver) {
        Resource resource = serviceResourcerResolver.getResource(this.path);
        if (resource == null) {
            this.errorMessage = "testpage: " + this.path + " is not existing in repository!";
            return false;
        }
        return true;
    }

    private boolean deletePage(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver) throws WCMException, RepositoryException {
        Resource resource = serviceResourcerResolver.getResource(this.path);
        try {
            // TODO: clarify if param 'shallow' should be influenceable
            // autosave set to false - we don't want to really delete the testpage
            //somehow testUserResolver.delete() auto commits and actually deletes nodes...
            saveCopy(serviceResourcerResolver, resource);
            testUserResolver.delete(resource);
            revertCopy(serviceResourcerResolver, resource);
            return true;
        } catch (Exception e) {
            this.errorMessage = (e.getLocalizedMessage() != null) ? e.getLocalizedMessage() : e.toString();
            return false;
        }
    }


    private void saveCopy(ResourceResolver serviceResourcerResolver, Resource resource) throws PersistenceException, RepositoryException {
        Workspace workspace = serviceResourcerResolver.adaptTo(Session.class).getWorkspace();
        HashMap<String, Object> map = new HashMap<>();
        map.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        map.put("srcPath", resource.getPath());
        Resource wrapper = serviceResourcerResolver.create(this.tmpFolder, resource.getName(), map);
        serviceResourcerResolver.commit();
        workspace.copy(this.path, wrapper.getPath() + "/revert");
    }

    private void revertCopy(ResourceResolver serviceResourcerResolver, Resource resource) throws RepositoryException, PersistenceException {
        Workspace workspace = serviceResourcerResolver.adaptTo(Session.class).getWorkspace();
        Resource wrapper = serviceResourcerResolver.getResource(this.tmpFolder.getPath() + "/" + resource.getName());
        if(wrapper != null){
            Resource revert = wrapper.getChild("revert");
            workspace.copy(revert.getPath(), this.path);
            serviceResourcerResolver.delete(wrapper);
        }
    }
}
