<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.PortletMode" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.List" %>
<%@ page import="org.sakaiproject.util.ResourceLoader" %>
<%@ page import="org.sakaiproject.util.Validator" %>

<%@ page session="false" %>
<%!

private static ResourceLoader rb = new ResourceLoader("basiclti");

// Allow the user to set unless final.launch etc. is true
public boolean allow(Properties props, String s) {
  if ( props == null || s == null ) return false;
  String prop = props.getProperty("final."+s); 
  if ( prop == null ) return true;
  if ( "true".equals(prop) ) return false;
  return true;
}
%>
<%
RenderResponse rResp = (RenderResponse)request.getAttribute("javax.portlet.response");
RenderRequest rReq = (RenderRequest)request.getAttribute("javax.portlet.request");
PortletSession pSession = rReq.getPortletSession(true);

PortletURL viewURL = rResp.createActionURL();
viewURL.setParameter("sakai.action","main");

PortletURL clearURL = rResp.createActionURL();
clearURL.setParameter("sakai.action","edit.clear");

PortletURL resetURL = rResp.createActionURL();
resetURL.setParameter("sakai.action","edit.reset");

PortletURL launchURL = rResp.createActionURL();
launchURL.setParameter("sakai.action","edit.save");

PortletURL actionURL = rResp.createActionURL();

String errorMsg = (String) rReq.getAttribute("error.message");

Properties ov = (Properties) rReq.getAttribute("imsti.oldvalues");

Properties sp = (Properties) rReq.getAttribute("imsti.properties");

List<String> assignments = (List<String>) rReq.getAttribute("assignments");

Boolean allowOutcomes = (Boolean) rReq.getAttribute("allowOutcomes");

Boolean isGradebookGroupEnabled = (Boolean) rReq.getAttribute("isGradebookGroupEnabled");

Boolean allowRoster = (Boolean) rReq.getAttribute("allowRoster");

%>
<portlet:defineObjects/>
<div class="portletBody">
<% 
    if ( allow(sp,"launch") || allow(sp,"xml") ||
		allow(sp,"secret") || allow(sp,"key") || 
        allow(sp,"frameheight") || allow(sp, "debug") ||
        allow(sp, "releasename") || allow(sp,"releaseemail")  ||
		allow(sp,"custom") || 
		allow(sp, "allowroster") || 
        allow(sp, "allowoutcomes") || allow(sp, "splash") ||
        allow(sp, "fa_icon")
) { 

    if ( errorMsg != null ) { %>
		<div class="sak-banner-error"><%= errorMsg %></div>
	<% } %>

<script type="text/javascript" src="/library/js/headscripts.js"></script>
<script>
	includeLatestJQuery('portlet edit.jsp');
	includeWebjarLibrary('fontawesome-iconpicker');
</script>

<ul class="navIntraTool actionToolBar">
	<li class="firstToolBarItem">
		<span>
			<a href="<%=viewURL.toString()%>"><%=rb.getString("edit.exit")%></a>
		</span>
	</li>
	<li>
		<span>
			<a href="<%=resetURL.toString()%>"><%=rb.getString("edit.clear.prefs")%></a>
		</span>
	</li>
</ul>

<form method="post" action="<%=launchURL.toString()%>">
<% if ( allow(sp,"launch") || allow(sp,"xml") || allow(sp,"key") || allow(sp,"secret") ) { %>

<h3><%=rb.getString("required.information") %></h3>
<% if ( allow(sp,"launch") && allow(sp,"xml") ) { %>
<script type="text/javascript">
function switchui()
{
  var x=document.getElementById("UISwitcher");
  if ( x.selectedIndex == 0 ) {
    document.getElementById("xml.paste").style.display = "none";
    document.getElementById("url.input").style.display = "block";
  } else {
    document.getElementById("url.input").style.display = "none";
    document.getElementById("xml.paste").style.display = "block";
  }
}
</script>
<select id="UISwitcher" name="imsti.type" onchange="switchui(); return false;">
  <option value="URL"><%=rb.getString("select.url") %></option>
<% if ( ov.getProperty("imsti.xml",null) != null ) { %>
  <option selected="selected" value="XML"><%=rb.getString("select.xml") %></option>
<% } else { %>
  <option value="XML"><%=rb.getString("select.xml")%></option>
<% } %>
</select>
<% } %>
<% if ( allow(sp,"xml") ) { %>
<div id="xml.paste">
<textarea rows="20" cols="60" class="form-control" name="imsti.xml" >
<%=ov.getProperty("imsti.xml","")%>
</textarea>
<%=rb.getString("tool.xml.detail") %>
</div>
<% } %>
<% if ( allow(sp,"launch") ) { %>
<p id="url.input" class="shorttext">
<span class="reqStar">*</span><label for="imsti.launch"><%=rb.getString("tool.url") %></label><br/>
<input type="text" size="80" class="form-control" name="imsti.launch" id="imsti.launch" value="<%=ov.getProperty("imsti.launch","")%>">
<%=rb.getString("tool.url.detail") %>
</p>
<% } %>
<% if ( allow(sp,"key") ) { %>
<p  class="shorttext">
<span class="reqStar">*</span><label for="imsti.key"><%=rb.getString("tool.key") %></label>
<input type="text" name="imsti.key" class="form-control" id="imsti.key" value="<%=ov.getProperty("imsti.key","")%>"> 
<%=rb.getString("tool.key.detail") %>
</p>
<% } %>
<% if ( allow(sp,"secret") ) { %>
<p  class="shorttext">
<span class="reqStar">*</span><label for="imsti.secret"><%=rb.getString("tool.secret") %></label>
<input type="password" name="imsti.secret" class="form-control" id="imsti.secret" value="<%=ov.getProperty("imsti.secret","")%>"> 
<%=rb.getString("tool.secret.detail") %>
</p>
<% } %>

<% } %>
<script type="text/javascript">
if ( document.getElementById("UISwitcher") ) switchui();
</script>

<h3><%=rb.getString("display.information") %></h3>
<% if ( allow(sp,"tooltitle") ) { %>
<p  class="shorttext">
<label for="imsti.tooltitle"><%=rb.getString("tool.title") %></label>
<input type="text" name="imsti.tooltitle" class="form-control" id="imsti.tooltitle" size="40" value="<%=ov.getProperty("imsti.tooltitle","")%>"> 
</p>
<% } %>

<% if ( allow(sp,"fa_icon") ) { %>
<p  class="shorttext">
<div class=form-group">
    <label for="imsti_fa_icon"><%=rb.getString("tool.fa_icon") %></label>&nbsp;
	<input type="text" name="imsti.fa_icon" class="form-control" id="imsti_fa_icon" size="20" value="<%=ov.getProperty("imsti.fa_icon","")%>"> 
</div>
</p>
<script type="text/javascript">$(document).ready(function () { fontawesome_icon_picker('#imsti_fa_icon'); });</script>
</p>
<% } %>

<% if ( allowOutcomes && allow(sp,"allowoutcomes") ) { %>
<h3><%=rb.getString("gradable.information") %></h3>
<p  class="shorttext">
<label for="imsti.newassignment"><%=rb.getString("gradable.newassignment") %></label>
<input type="text" size="10" class="form-control" name="imsti.newassignment" id="imsti.newassignment" value="<%=ov.getProperty("imsti.newassignment","")%>"> 
<span class="textPanelFooter"><%=rb.getString("gradable.newassignment.detail") %></span>
</p>

<% if ( isGradebookGroupEnabled ) { %>
<div class="sak-banner-info">
	<%=rb.getString("multi_gradebook.use_info") %>
</div>
<% } %>

<% } %>

<% if ( allowOutcomes && allow(sp,"allowoutcomes") && assignments != null ) { %>
<p  class="shorttext">
<%=rb.getString("gradable.title") %>
<select name="imsti.assignment">
  <option value=""><%=rb.getString("gradable.nograde") %></option>
<% for ( String assn : assignments ) { 
     if ( Validator.escapeHtml(assn).equals(ov.getProperty("imsti.assignment")) ) { %>
       <option selected="selected" value="<%=Validator.escapeHtml(assn) %>"><%=Validator.escapeHtml(assn) %></option>
<%   } else { %>
       <option value="<%=Validator.escapeHtml(assn) %>"><%=Validator.escapeHtml(assn) %></option>
<%   }
   } %>
</select>
<span class="textPanelFooter"><%=rb.getString("gradable.detail") %></span>
</p>

<% } %>

<% if ( allow(sp,"frameheight") || allow(sp, "debug") || allow(sp, "newpage") ) { %>
<h3><%=rb.getString("launch.information") %></h3>
<% if ( allow(sp,"newpage") ) { %>
<p>
<label for="imsti.newpage">
<input type="checkbox" size="10" class="form-control" name="imsti.newpage" id="imsti.newpage" 
<% if ( ov.getProperty("imsti.newpage",null) != null ) { %>
  checked="yes" />
<% } else { %>
   />
<% } %>
<%=rb.getString("new.page") %></label><%=rb.getString("new.page.detail") %>
</p>
<% } %>
<% if ( allow(sp,"frameheight") ) { %>
<p  class="shorttext">
<label for="imsti.frameheight"><%=rb.getString("iframe.height") %></label>
<input type="text" size="10" class="form-control" name="imsti.frameheight" id="imsti.frameheight" value="<%=ov.getProperty("imsti.frameheight","")%>"> 
<span class="textPanelFooter"><%=rb.getString("iframe.height.detail") %></span>
</p>
<% } %>
<% if ( allow(sp,"debug") ) { %>
<p>
<label for="imsti.debug">
<input type="checkbox" size="10" class="form-control" name="imsti.debug" id="imsti.debug" 
<% if ( ov.getProperty("imsti.debug",null) != null ) { %>
  checked="yes" />
<% } else { %>
   />
<% } %>
<%=rb.getString("debug.launch") %></label>
<span class="textPanelFooter"><%=rb.getString("debug.launch.detail") %></span>
</p>
<% } %>

<% } %>

<% if ( allow(sp,"releasename") || allow(sp, "releaseemail") || 
        ( allow(sp, "allowroster") && allowRoster ) 
) { %>
<h3><%=rb.getString("launch.privacy") %></h3>
<p><%=rb.getString("launch.privacy.detail") %></p>
<% if ( allow(sp,"releasename") ) { %>
	<p>
		<label for="imsti.releasename">
			<input type="checkbox" size="10" class="form-control" name="imsti.releasename" id="imsti.releasename" 
			<% if ( ov.getProperty("imsti.releasename",null) != null ) { %>
  			checked="yes" />
				<% } else { %>
   			/>
			<% } %>
			<%=rb.getString("privacy.releasename") %>
		</label>
	</p>
<% } %>
<% if ( allow(sp,"releaseemail") ) { %>
	<p>
		<label for="imsti.releaseemail">
			<input type="checkbox" size="10" class="form-control" name="imsti.releaseemail" id="imsti.releaseemail" 
			<% if ( ov.getProperty("imsti.releaseemail",null) != null ) { %>
  			checked="yes" />
			<% } else { %>
   			/>
			<% } %>
			<%=rb.getString("privacy.releaseemail") %>
		</label>
</p>
<% } %>
<% if ( allow(sp,"allowroster") && allowRoster ) { %>
<p>
<label for="imsti.allowroster">
<input type="checkbox" size="10" class="form-control" name="imsti.allowroster" id="imsti.allowroster" 
<% if ( ov.getProperty("imsti.allowroster",null) != null ) { %>
  checked="yes" />
<% } else { %>
   />
<% } %>
<%=rb.getString("privacy.allowroster") %></label>
</p>
<% } %>

<% } %>

<% if ( allow(sp,"splash") ) { %>
<h3><%=rb.getString("launch.splash") %></h3>
<p class="longtext">
<label for="imsti.splash" class="textPanelFooter" style="float:none;display:block;width:50%"><%=rb.getString("launch.splash.detail") %></label>
<textarea rows="10" cols="60" class="form-control" name="imsti.splash" id="imsti.splash" >
<%=ov.getProperty("imsti.splash","")%>
</textarea>

</p>
<% } %>

<% if ( allow(sp,"custom") ) { %>
<h3><%=rb.getString("launch.custom") %></h3>
<p class="longtext">
<label for="imsti.custom" class="textPanelFooter" style="float:none;display:block;width:50%"><%=rb.getString("launch.custom.detail") %></label>
<textarea rows="10" cols="60" class="form-control" name="imsti.custom" id="imsti.custom" >
<%=ov.getProperty("imsti.custom","")%>
</textarea>
	
</p>
<% } %>
<p class="act">
<input type="submit" value="<%=rb.getString("update.options")%>" class="active">
<input type="submit" value="<%=rb.getString("edit.exit")%>" 
    onclick="window.location='<%=viewURL.toString()%>'; return false;"/>
</p>
</form>
<% } else { 
    if ( errorMsg != null ) { %>
		<div class="sak-banner-error"><%= errorMsg %></div>
	<% } %>

<ul class="navIntraTool actionToolBar">
	<li class="firstToolBarItem">
		<span>
			<a href="<%=viewURL.toString()%>"><%=rb.getString("edit.exit")%></a>
		</span>
	</li>
</ul>
<p class="instruction"><%=rb.getString("edit.nothing")%></p>
<% } %>
</div>
