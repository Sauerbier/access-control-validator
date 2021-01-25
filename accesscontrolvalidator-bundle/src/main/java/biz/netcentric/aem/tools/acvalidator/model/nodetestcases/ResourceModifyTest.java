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
import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Set;

/**
 * Checks if a page can(not) be modified.
 * 
 * @author Roland Gruber
 */
public class ResourceModifyTest extends ResourceTestCase implements SimulatableTest, Testable {
	
	private final Logger LOG = LoggerFactory.getLogger(ResourceModifyTest.class);

	private String path;
	private String errorMessage;
	private Set<String> propertyNames;

	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate simulate write
	 * @param propertyNames propertyNames
	 */
	public ResourceModifyTest(String path, boolean isAllow, boolean simulate, Set<String> propertyNames) {
		super(path, isAllow, "modify");
		this.path = path;
		this.simulate = simulate;
		this.errorMessage = StringUtils.EMPTY;
		this.propertyNames = propertyNames;
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(simulate){
			try {
				isSimulateSuccess = modifyPage(serviceResourcerResolver, testUserResolver);
			} catch (WCMException e) {
				LOG.error("Exception in PageModifyTest: ", e);
				throw new RepositoryException(e);
			}
		}
		boolean isOk = isOk(serviceResourcerResolver, authorizable);
		return new TestResult(authorizable.getID(), "Resource Modify Test", " path: " + this.path + ", isSimulate:" + simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk, errorMessage);
	}

	private boolean modifyPage(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver) throws WCMException  {
		Resource resource = serviceResourcerResolver.getResource(this.path);
		if(resource == null){
			errorMessage = "testpage: " + path + " is not existing in repository!";
			return false;
		} 


		try {
			resource = testUserResolver.getResource(this.path);
			if(resource == null){
				errorMessage = "no access to page";
				return false;
			}

			ValueMap vm = resource.adaptTo(ModifiableValueMap.class);
			if(vm == null){
				return false;
			}
			for(String propertyName : propertyNames){
				vm.put(propertyName, "testvalue");
			}
			Node node = resource.adaptTo(Node.class);
			if(node == null){
				throw new IllegalStateException("node is null");
			}
			for(String propertyName : propertyNames){
				node.setProperty(propertyName, "test");
			}
			
		} catch (Exception e) {
			this.errorMessage = e.getLocalizedMessage();
			return false;
		}
		testUserResolver.revert();
		return true;
	}

}
