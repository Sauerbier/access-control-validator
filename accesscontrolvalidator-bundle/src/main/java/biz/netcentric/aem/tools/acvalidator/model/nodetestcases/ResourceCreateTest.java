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
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Collections;

/**
 * Checks if a page can(not) be created.
 * 
 * @author jochen.koschorke
 */
public class ResourceCreateTest extends ResourceTestCase implements SimulatableTest, Testable {

	private final Logger LOG = LoggerFactory.getLogger(ResourceCreateTest.class);

	private String path;
	private String errorMessage;

	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate simulate write
	 */
	public ResourceCreateTest(String path, boolean isAllow, boolean simulate) {
		super(path, isAllow, "create");
		this.path = path;
		this.simulate = simulate;
		this.errorMessage = StringUtils.EMPTY;
	}

	@Override
	public boolean isSimulateSuccess() {
		return this.isSimulateSuccess;
	}
	
	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			try {
				isSimulateSuccess = createNode(testUserResolver);
			} catch (WCMException | PersistenceException e) {
				LOG.error("Exception in PageCreateTest: ", e);
				throw new RepositoryException(e);
			}
		}
		boolean isOk = isOk(serviceResourcerResolver, authorizable);
		return new TestResult(authorizable.getID(), "Resource Create Test", " path: " + this.path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk, errorMessage);
	}

	private boolean createNode(ResourceResolver resolver) throws WCMException, PersistenceException
	{
		Resource parent = resolver.resolve(path.substring(path.lastIndexOf("/") + 1));
		Resource testNode = null;
		try {
			testNode = resolver.create(parent, "testNode", Collections.<String, Object>emptyMap());
		} catch (Exception e) {
			errorMessage = e.getLocalizedMessage();
			return false;
		}finally{
			if(testNode != null && testNode.getName().equals("testNode")){
				resolver.delete(testNode);
			}
		}
		return true;
	}
}
