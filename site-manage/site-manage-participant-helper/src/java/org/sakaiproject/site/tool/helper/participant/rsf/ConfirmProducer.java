/**
 * Copyright (c) 2003-2016 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.site.tool.helper.participant.rsf;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.site.tool.helper.participant.impl.UserRoleEntry;
import org.sakaiproject.site.tool.helper.participant.impl.SiteAddParticipantHandler;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import org.sakaiproject.rsf.util.SakaiURLUtil;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.RawViewParameters;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * Assign same role while adding participant
 */
@Slf4j
public class ConfirmProducer implements ViewComponentProducer, NavigationCaseReporter, ActionResultInterceptor {

	public static final String VIEW_ID = "Confirm";

	@Setter private SiteAddParticipantHandler handler;
	@Setter private MessageLocator messageLocator;
	@Setter private SessionManager sessionManager;
	@Setter private TargettedMessageList targettedMessageList;
	@Setter private UserDirectoryService userDirectoryService;

    public String getViewID() {
        return VIEW_ID;
    }

    public void fillComponents(UIContainer tofill, ViewParameters arg1, ComponentChecker arg2) {

    	String siteTitle = handler.getSiteTitle();
        UIMessage.make(tofill, "addconf.confirming", "addconf.confirming", new Object[] {siteTitle});

        UIBranchContainer content = UIBranchContainer.make(tofill, "content:");

        String emailChoice = handler.getEmailNotiChoice();
        if (emailChoice != null && emailChoice.equals(Boolean.TRUE.toString())) {
        	// email notification will be sent out to added participants
    		UIMessage.make(content, "emailnoti", "addconf.theywill");
        } else {
        	// email notification will NOT be sent out to added participants
    		UIMessage.make(content, "emailnoti", "addconf.theywillnot");
        }

    	UIForm confirmForm = UIForm.make(content, "confirm", "");
    	// csrf token
    	UIInput.make(confirmForm, "csrfToken", "#{siteAddParticipantHandler.csrfToken}", handler.csrfToken);
    	List<UserRoleEntry> userTable = handler.userRoleEntries;
    	// list of users
        for (UserRoleEntry userRoleEntry:userTable) {
        	String userEId = userRoleEntry.getEid();
        	// default to userEid
        	String userName = userEId;
        	String displayId = userEId;
        	// if there is last name or first name specified, use it
        	if (userRoleEntry.getLastName() != null && !userRoleEntry.getLastName().isEmpty()
                    || userRoleEntry.getFirstName() != null && !userRoleEntry.getFirstName().isEmpty())
        		userName = userRoleEntry.getLastName() + "," + userRoleEntry.getFirstName();

			try {
        		// get user from directory
        		User u = userDirectoryService.getUserByEid(userEId);
        		userName = u.getSortName();
        		displayId = u.getDisplayId();
        	} catch (Exception e) {
        		log.debug("cannot find user with eid={}", userEId);
        	}
            UIBranchContainer userRow = UIBranchContainer.make(confirmForm, "user-row:", userEId);
            UIOutput.make(userRow, "user-name", userName);
            UIOutput.make(userRow, "user-eid", displayId);
            UIOutput.make(userRow, "user-role", userRoleEntry.getRole());
            UIOutput.make(userRow, "user-status", handler.statusChoice.equals("active") ? messageLocator.getMessage("sitegen.siteinfolist.active") : messageLocator.getMessage("sitegen.siteinfolist.inactive"));
        }

    	UICommand.make(confirmForm, "continue", messageLocator.getMessage("gen.finish"), "#{siteAddParticipantHandler.processConfirmContinue}");
    	UICommand.make(confirmForm, "back", messageLocator.getMessage("gen.back"), "#{siteAddParticipantHandler.processConfirmBack}");
    	UICommand.make(confirmForm, "cancel", messageLocator.getMessage("gen.cancel"), "#{siteAddParticipantHandler.processCancel}");

    	// process any messages
    	targettedMessageList = handler.getTargettedMessageList();
        if (targettedMessageList != null && targettedMessageList.size() > 0) {
			for (int i = 0; i < targettedMessageList.size(); i++ ) {
				TargettedMessage msg = targettedMessageList.messageAt(i);
				if (msg.severity == TargettedMessage.SEVERITY_ERROR) {
					UIBranchContainer errorRow = UIBranchContainer.make(tofill,"error-row:", Integer.toString(i));

			    	if (msg.args != null ) {
			    		UIMessage.make(errorRow,"error", msg.acquireMessageCode(), msg.args);
			    	} else {
			    		UIMessage.make(errorRow,"error", msg.acquireMessageCode());
			    	}
				} else if (msg.severity == TargettedMessage.SEVERITY_INFO) {
					UIBranchContainer errorRow = UIBranchContainer.make(tofill,"info-row:", Integer.toString(i));

			    	if (msg.args != null ) {
			    		UIMessage.make(errorRow,"info", msg.acquireMessageCode(), msg.args);
			    	} else {
			    		UIMessage.make(errorRow,"info", msg.acquireMessageCode());
			    	}
				}

			}
        }
    }

    public ViewParameters getViewParameters() {
    	AddViewParameters params = new AddViewParameters();
        params.setId(null);
        return params;
    }

    public List<NavigationCase> reportNavigationCases() {
        List<NavigationCase> togo = new ArrayList<>();
        togo.add(new NavigationCase("back", new SimpleViewParameters(EmailNotiProducer.VIEW_ID)));
        return togo;
    }

    public void interceptActionResult(ARIResult result, ViewParameters incoming, Object actionReturn) {
        if ("done".equals(actionReturn)) {
          Tool tool = handler.getCurrentTool();
           result.resultingView = new RawViewParameters(SakaiURLUtil.getHelperDoneURL(tool, sessionManager));
        }
    }
}
