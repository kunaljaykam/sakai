/**
 * Copyright (c) 2008-2012 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.profile2.tool.pages.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import org.sakaiproject.profile2.model.MyProfilePanelState;
import org.sakaiproject.profile2.model.UserProfile;

/**
 * Container for viewing user's own profile.
 */
public class MyProfilePanel extends Panel {
	
	private static final long serialVersionUID = 1L;

	public MyProfilePanel(String id, UserProfile userProfile,
			MyProfilePanelState panelState) {
		
		super(id);

		add(new Label("myDisplayName", new ResourceModel(userProfile.getDisplayName())));
		
		//info panel - load the display version by default
		Panel myInfoDisplay = new MyInfoDisplay("myInfo", userProfile);
		myInfoDisplay.setOutputMarkupId(true);
		add(myInfoDisplay);

		//name pronunciation panel - load the display version by default
		Panel myNamePronunciationDisplay;
		if (panelState.showNamePronunciationDisplay) {
			myNamePronunciationDisplay = new MyNamePronunciationDisplay("myNamePronunciation", userProfile);
			myNamePronunciationDisplay.setOutputMarkupId(true);
		} else {
			myNamePronunciationDisplay = new EmptyPanel("myNamePronunciation");
		}
		add(myNamePronunciationDisplay);

		//contact panel - load the display version by default
		Panel myContactDisplay = new MyContactDisplay("myContact", userProfile);
		myContactDisplay.setOutputMarkupId(true);
		add(myContactDisplay);
		
		//social networking panel
		Panel mySocialNetworkingDisplay;
		if (panelState.showSocialNetworkingDisplay) {
			mySocialNetworkingDisplay = new MySocialNetworkingDisplay("mySocialNetworking", userProfile);
			mySocialNetworkingDisplay.setOutputMarkupId(true);
		} else {
			mySocialNetworkingDisplay = new EmptyPanel("mySocialNetworking");
		}
		add(mySocialNetworkingDisplay);
	}

}
