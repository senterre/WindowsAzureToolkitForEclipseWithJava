/**
* Copyright 2015 Microsoft Open Technologies, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*	 http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.gigaspaces.azure.runnable;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import waeclipseplugin.Activator;
import com.gigaspaces.azure.propertypage.Messages;
import com.gigaspaces.azure.util.PreferenceUtil;
import com.microsoftopentechnologies.azurecommons.deploy.util.PublishData;
import com.microsoftopentechnologies.azurecommons.exception.RestAPIException;
import com.persistent.util.MessageUtil;

public class LoadAccountWithProgressWindow extends AccountActionRunnable implements Runnable {
	
	public LoadAccountWithProgressWindow(PublishData data, Shell shell) {
		super(data, shell);
	}

	@Override
	public void run() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, this);
			dialog.close();
		} catch (InvocationTargetException e) {
            // special check for Java 1.6 and bouncycastle not in classpath error
            if (com.gigaspaces.azure.util.Messages.importDlgMsgJavaVersion.equals(e.getMessage())) {
                MessageUtil.displayErrorDialog(shell, com.gigaspaces.azure.propertypage.Messages.loadingCred,
                        com.gigaspaces.azure.util.Messages.importDlgMsgJavaVersion);
            } else {
                MessageUtil.displayErrorDialog(shell, com.gigaspaces.azure.propertypage.Messages.loadingCred, Messages.loadingAccountError);
            }
			Activator.getDefault().log(Messages.error, e);
		} catch (InterruptedException e) {
		}
	}
	
	@Override
	public void doTask() {
		try {
			PreferenceUtil.load(this);
		} catch (RestAPIException e) {
			Activator.getDefault().log(Messages.error, e);
		}
	}

}
