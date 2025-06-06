/*
 *
 * $URL$
 * $Id$
 *
 * Copyright (c) 2008-2016 Charles R. Severance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.tsugi.lti;

import static org.tsugi.lti.LTIConstants.LTI_MESSAGE_TYPE_BASICLTILAUNCHREQUEST;
import static org.tsugi.lti.LTIConstants.LTI_MESSAGE_TYPE_CONTENTITEMSELECTIONREQUEST;
import static org.tsugi.lti.LTIConstants.LTI_VERSION;
import static org.tsugi.lti.LTIConstants.LTI_VERSION_1;
import static org.tsugi.lti.LTIConstants.CUSTOM_PREFIX;
import static org.tsugi.lti.LTIConstants.LTI_MESSAGE_TYPE;
import static org.tsugi.lti.LTIConstants.TOOL_CONSUMER_INSTANCE_CONTACT_EMAIL;
import static org.tsugi.lti.LTIConstants.TOOL_CONSUMER_INSTANCE_DESCRIPTION;
import static org.tsugi.lti.LTIConstants.TOOL_CONSUMER_INSTANCE_GUID;
import static org.tsugi.lti.LTIConstants.TOOL_CONSUMER_INSTANCE_NAME;
import static org.tsugi.lti.LTIConstants.TOOL_CONSUMER_INSTANCE_URL;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;
import net.oauth.signature.OAuthSignatureMethod;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
/* Leave out until we have JTidy 0.8 in the repository
 import org.w3c.tidy.Tidy;
 import java.io.ByteArrayOutputStream;
 */

/**
 * Some Utility code for IMS LTI
 * http://www.anyexample.com/programming/java
 * /java_simple_class_to_compute_sha_1_hash.xml
 * <p>
 * Sample Descriptor
 *
 * <pre>
 * &lt;?xml&nbsp;version=&quot;1.0&quot;&nbsp;encoding=&quot;UTF-8&quot;?&gt;
 * &lt;basic_lti_link&nbsp;xmlns=&quot;http://www.imsglobal.org/xsd/imsbasiclti_v1p0&quot;&nbsp;xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;
 *   &lt;title&gt;generated&nbsp;by&nbsp;tp+user&lt;/title&gt;
 *   &lt;description&gt;generated&nbsp;by&nbsp;tp+user&lt;/description&gt;
 *   &lt;custom&gt;
 *	 &lt;parameter&nbsp;key=&quot;keyname&quot;&gt;value&lt;/parameter&gt;
 *   &lt;/custom&gt;
 *   &lt;extensions&nbsp;platform=&quot;www.lms.com&quot;&gt;
 *	 &lt;parameter&nbsp;key=&quot;keyname&quot;&gt;value&lt;/parameter&gt;
 *   &lt;/extensions&gt;
 *   &lt;launch_url&gt;url&nbsp;to&nbsp;the&nbsp;basiclti&nbsp;launch&nbsp;URL&lt;/launch_url&gt;
 *   &lt;secure_launch_url&gt;url&nbsp;to&nbsp;the&nbsp;basiclti&nbsp;launch&nbsp;URL&lt;/secure_launch_url&gt;
 *   &lt;icon&gt;url&nbsp;to&nbsp;an&nbsp;icon&nbsp;for&nbsp;this&nbsp;tool&nbsp;(optional)&lt;/icon&gt;
 *   &lt;secure_icon&gt;url&nbsp;to&nbsp;an&nbsp;icon&nbsp;for&nbsp;this&nbsp;tool&nbsp;(optional)&lt;/secure_icon&gt;
 *   &lt;cartridge_icon&nbsp;identifierref=&quot;LTI001_Icon&quot;/&gt;
 *   &lt;vendor&gt;
 *	 &lt;code&gt;vendor.com&lt;/code&gt;
 *	 &lt;name&gt;Vendor&nbsp;Name&lt;/name&gt;
 *	 &lt;description&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This&nbsp;is&nbsp;a&nbsp;Grade&nbsp;Book&nbsp;that&nbsp;supports&nbsp;many&nbsp;column&nbsp;types.
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/description&gt;
 *	 &lt;contact&gt;
 *	   &lt;email&gt;support@vendor.com&lt;/email&gt;
 *	 &lt;/contact&gt;
 *	 &lt;url&gt;http://www.vendor.com/product&lt;/url&gt;
 *   &lt;/vendor&gt;
 * &lt;/basic_lti_link&gt;
 * </pre>
 */

@Slf4j
public class LTIUtil {

	// How to make ISO8601 Dates
	// https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date
	// https://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
	public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";

	public static final String EXTRA_BUTTON_HTML = "button_html";
	public static final String EXTRA_ERROR_TIMEOUT = "error_timeout";
	public static final String EXTRA_HTTP_POPUP = "http_popup";
	public static final String EXTRA_HTTP_POPUP_FALSE = "false";
	public static final String EXTRA_JAVASCRIPT = "extra_javascript";
	public static final String EXTRA_FORM_ID = "extra_form_id";


	private static final Pattern CUSTOM_REGEX = Pattern.compile("[^A-Za-z0-9]");
	private static final String UNDERSCORE = "_";

	// Returns true if this is a LTI message with minimum values to meet the protocol
	public static boolean isRequest(HttpServletRequest request) {

		String message_type = request.getParameter(LTI_MESSAGE_TYPE);
		if ( message_type == null ) return false;
		if ( message_type.equals(LTI_MESSAGE_TYPE_BASICLTILAUNCHREQUEST) ||
		     message_type.equals(LTI_MESSAGE_TYPE_CONTENTITEMSELECTIONREQUEST) ) {
			// Seems plausible
		} else {
			return false;
		}

		String version = request.getParameter(LTI_VERSION);
		if ( version == null ) return true;
		if ( !version.equals(LTI_VERSION_1) ) return false;

		return true;
	}

	// expected_oauth_key can be null - if it is non-null it must match the key in the request
	public static Object validateMessage(HttpServletRequest request, String URL,
		String oauth_secret, String expected_oauth_key)
	{
		OAuthMessage oam = OAuthServlet.getMessage(request, URL);
		String oauth_consumer_key = null;
		try {
			oauth_consumer_key = oam.getConsumerKey();
		} catch (Exception e) {
			return "Unable to find consumer key in message";
		}

		if ( expected_oauth_key != null && ! expected_oauth_key.equals(oauth_consumer_key) ) {
			log.warn("LTIUtil.validateMessage Incorrect consumer key={} expected key={}", oauth_consumer_key, expected_oauth_key);
			return "Incorrect consumer key "+oauth_consumer_key;
		}

		OAuthValidator oav = new SimpleOAuthValidator();
		OAuthConsumer cons = new OAuthConsumer("about:blank#OAuth+CallBack+NotUsed", oauth_consumer_key,oauth_secret, null);

		OAuthAccessor acc = new OAuthAccessor(cons);

		String base_string = null;
		try {
			base_string = OAuthSignatureMethod.getBaseString(oam);
		} catch (Exception e) {
			return "Unable to find base string";
		}

		try {
			oav.validateMessage(oam, acc);
		} catch (Exception e) {
			if (base_string != null) {
				return "Failed to validate: "+e.getLocalizedMessage()+"\nBase String\n"+base_string;
			}
			return "Failed to validate: "+e.getLocalizedMessage();
		}
		return Boolean.TRUE;
	}

	public static String validateDescriptor(String descriptor) {
		if (descriptor == null)
			return null;
		if (descriptor.indexOf("<basic_lti_link") < 0)
			return null;

		Map<String, Object> tm = XMLMap.getFullMap(descriptor.trim());
		if (tm == null)
			return null;

		// We demand at least an endpoint
		String ltiSecureLaunch = XMLMap.getString(tm,
				"/basic_lti_link/secure_launch_url");
		// We demand at least an endpoint
		if (ltiSecureLaunch != null && ltiSecureLaunch.trim().length() > 0)
			return ltiSecureLaunch;
		String ltiLaunch = XMLMap.getString(tm, "/basic_lti_link/launch_url");
		if (ltiLaunch != null && ltiLaunch.trim().length() > 0)
			return ltiLaunch;
		return null;
	}

	/**
	 * A simple utility method which implements the specified semantics of custom
	 * properties.
	 * <p>
	 * i.e. The parameter names are mapped to lower case and any character that is
	 * neither a number nor letter in a parameter name is replaced with an
	 * "underscore".
	 * <p>
	 * e.g. Review:Chapter=1.2.56 would map to custom_review_chapter=1.2.56.
	 *
	 * @param propertyName
	 * @return
	 */
	public static String adaptToCustomPropertyName(final String propertyName) {
		if (propertyName == null || "".equals(propertyName)) {
			throw new IllegalArgumentException("propertyName cannot be null");
		}
		String customName = propertyName.toLowerCase();
		customName = CUSTOM_REGEX.matcher(customName).replaceAll(UNDERSCORE);
		if (!customName.startsWith(CUSTOM_PREFIX)) {
			customName = CUSTOM_PREFIX + customName;
		}
		return customName;
	}

	/**
	 * Add the necessary fields and sign.
	 *
	 * @deprecated See:
	 *	 {@link LTIUtil#signProperties(Map, String, String, String, String, String, String, String, String, String, Map)}
	 *
	 * @param postProp
	 * @param url
	 * @param method
	 * @param oauth_consumer_key
	 * @param oauth_consumer_secret
	 * @param extra
	 * @return
	 */
	@Deprecated
	public static Properties signProperties(Properties postProp, String url,
			String method, String oauth_consumer_key, String oauth_consumer_secret,
			Map<String,String> extra) {
		final Map<String, String> signedMap = signProperties(
				convertToMap(postProp), url, method, oauth_consumer_key,
				oauth_consumer_secret, null, null, null, null, null, extra);
		return convertToProperties(signedMap);
	}

	/**
	 * Add the necessary fields and sign.
	 *
	 * @deprecated See:
	 *	 {@link LTIUtil#signProperties(Map, String, String, String, String, String, String, String, String, String, Map)}
	 *
	 * @param postProp
	 * @param url
	 * @param method
	 * @param oauth_consumer_key
	 * @param oauth_consumer_secret
	 * @param org_id
	 *		  See: {@link LTIConstants#TOOL_CONSUMER_INSTANCE_GUID}
	 * @param org_desc
	 *		  See: {@link LTIConstants#TOOL_CONSUMER_INSTANCE_DESCRIPTION}
	 * @param org_url
	 *		  See: {@link LTIConstants#TOOL_CONSUMER_INSTANCE_URL}
	 * @param extra
	 * @return
	 */
	@Deprecated
	public static Properties signProperties(Properties postProp, String url,
			String method, String oauth_consumer_key, String oauth_consumer_secret,
			String org_id, String org_desc, String org_url, Map<String,String> extra) {
		final Map<String, String> signedMap = signProperties(
				convertToMap(postProp), url, method, oauth_consumer_key,
				oauth_consumer_secret, org_id, org_desc, org_url, null, null, extra);
		return convertToProperties(signedMap);
	}

	/**
	 * Add the necessary fields and sign.
	 *
	 * @param postProp
	 * @param url
	 * @param method
	 * @param oauth_consumer_key
	 * @param oauth_consumer_secret
	 * @param tool_consumer_instance_guid
	 *		  See: {@link LTIConstants#TOOL_CONSUMER_INSTANCE_GUID}
	 * @param tool_consumer_instance_description
	 *		  See: {@link LTIConstants#TOOL_CONSUMER_INSTANCE_DESCRIPTION}
	 * @param tool_consumer_instance_url
	 *		  See: {@link LTIConstants#TOOL_CONSUMER_INSTANCE_URL}
	 * @param tool_consumer_instance_name
	 *		  See: {@link LTIConstants#TOOL_CONSUMER_INSTANCE_NAME}
	 * @param tool_consumer_instance_contact_email
	 *		  See:
	 *		  {@link LTIConstants#TOOL_CONSUMER_INSTANCE_CONTACT_EMAIL}
	 * @param extra
	 * @return
	 */
	public static Map<String, String> signProperties(
			Map<String, String> postProp, String url, String method,
			String oauth_consumer_key, String oauth_consumer_secret,
			String tool_consumer_instance_guid,
			String tool_consumer_instance_description,
			String tool_consumer_instance_url, String tool_consumer_instance_name,
			String tool_consumer_instance_contact_email,
			Map<String, String> extra) {

		if (tool_consumer_instance_guid != null)
			postProp.put(TOOL_CONSUMER_INSTANCE_GUID, tool_consumer_instance_guid);
		if (tool_consumer_instance_description != null)
			postProp.put(TOOL_CONSUMER_INSTANCE_DESCRIPTION, tool_consumer_instance_description);
		if (tool_consumer_instance_url != null)
			postProp.put(TOOL_CONSUMER_INSTANCE_URL, tool_consumer_instance_url);
		if (tool_consumer_instance_name != null)
			postProp.put(TOOL_CONSUMER_INSTANCE_NAME, tool_consumer_instance_name);
		if (tool_consumer_instance_contact_email != null)
			postProp.put(TOOL_CONSUMER_INSTANCE_CONTACT_EMAIL, tool_consumer_instance_contact_email);

		return signProperties(postProp, url, method, oauth_consumer_key, oauth_consumer_secret, extra);
	}

	/**
	 * Add the necessary fields and sign.
	 *
	 * In general, is is somewhat undefined to send \r or \n through <input type="text" >
	 * fields in a form.  Per ChatGPT on 17-Jul-2024:
	 *
	 *   For input type="text" tags, newlines and carriage returns are generally not
	 *   applicable since these elements are typically used for single-line text input.
	 *
	 * So for safe serialization and transport through form data, before we compute
	 * the base string and make the HTML form, we will remove \r, \n and \t
	 * from the values in postPropRaw.
	 *
	 * @param postPropRaw a Map<String, String> of the properties we are to prepare for HTML and sign
	 * @param url
	 * @param method
	 * @param oauth_consumer_key
	 * @param oauth_consumer_secret
	 * @param extra A Map<string, String> to return extra data, in particular the base string for debugging
	 * @return a Map<String, String> copy of the signed and cleaned up properties - ready for serialization into a form.
	 */
	public static Map<String, String> signProperties(
			Map<String, String> postPropRaw, String url, String method,
			String oauth_consumer_key, String oauth_consumer_secret,
			Map<String, String> extra) {


		Map<String, String> postProp = new HashMap<>();
		postPropRaw.forEach((key, value) -> {
			String newValue = value.replaceAll("[\\r\\n\\t]", " ");
			postProp.put(key, newValue);
		});

		if ( postProp.get(LTI_VERSION) == null ) postProp.put(LTI_VERSION, "LTI-1p0");
		if ( postProp.get(LTI_MESSAGE_TYPE) == null ) postProp.put(LTI_MESSAGE_TYPE, "basic-lti-launch-request");

		if (postProp.get("oauth_callback") == null)
			postProp.put("oauth_callback", "about:blank");

		if (oauth_consumer_key == null || oauth_consumer_secret == null) {
			log.debug("No signature generated in signProperties");
			return postProp;
		}

		OAuthMessage oam = new OAuthMessage(method, url, postProp.entrySet());
		OAuthConsumer cons = new OAuthConsumer("about:blank", oauth_consumer_key,
				oauth_consumer_secret, null);
		OAuthAccessor acc = new OAuthAccessor(cons);
		try {
			oam.addRequiredParameters(acc);
			String base_string = OAuthSignatureMethod.getBaseString(oam);
			log.debug("Base Message String\n{}\n", base_string);
			if ( extra != null ) {
				extra.put("BaseString", base_string);
			}

			List<Map.Entry<String, String>> params = oam.getParameters();

			Map<String, String> nextProp = new HashMap<String, String>();
			// Convert to Map<String, String>
			for (final Map.Entry<String, String> entry : params) {
				nextProp.put(entry.getKey(), entry.getValue());
			}
			return nextProp;
		} catch (net.oauth.OAuthException e) {
			log.warn("LTIUtil.signProperties OAuth Exception {}", e.getMessage());
			throw new Error(e);
		} catch (java.io.IOException e) {
			log.warn("LTIUtil.signProperties IO Exception {}", e.getMessage());
			throw new Error(e);
		} catch (java.net.URISyntaxException e) {
			log.warn("LTIUtil.signProperties URI Syntax Exception {}", e.getMessage());
			throw new Error(e);
		}

	}

	/**
	 * Check if the properties are properly signed
	 *
	 * @deprecated See:
	 *			 {@link LTIUtil#checkProperties(Map, String, String, String, String)}
	 *
	 * @param postProp
	 * @param url
	 * @param method
	 * @param oauth_consumer_key
	 * @param oauth_consumer_secret
	 * @return
	 */
	@Deprecated
	public static boolean checkProperties(Properties postProp, String url,
			String method, String oauth_consumer_key, String oauth_consumer_secret)
	{

		return checkProperties( convertToMap(postProp), url, method,
				oauth_consumer_key, oauth_consumer_secret);
	}

	/**
	 * Check if the fields are properly signed
	 *
	 * @param postProp
	 * @param url
	 * @param method
	 * @param oauth_consumer_key
	 * @param oauth_consumer_secret

	 * @return
	 */
	public static boolean checkProperties(
			Map<String, String> postProp, String url, String method,
			String oauth_consumer_key, String oauth_consumer_secret) {

		OAuthMessage oam = new OAuthMessage(method, url, postProp.entrySet());
		OAuthConsumer cons = new OAuthConsumer("about:blank", oauth_consumer_key,
				oauth_consumer_secret, null);
		OAuthValidator oav = new SimpleOAuthValidator();


		OAuthAccessor acc = new OAuthAccessor(cons);

		String base_string = null;
		try {
			base_string = OAuthSignatureMethod.getBaseString(oam);
		} catch (Exception e) {
			log.warn(e.getLocalizedMessage());
			base_string = null;
			return false;
		}

		try {
			oav.validateMessage(oam, acc);
		} catch (Exception e) {
			log.warn("Provider failed to validate message");
			log.warn(e.getLocalizedMessage());
			if (base_string != null) {
				log.warn(base_string);
			}
			return false;
		}
		return true;
	}

	/**
	 * Create the HTML to render a POST form and then automatically submit it.
	 *
	 * @deprecated Moved to {@link #postLaunchHTML(Map, String, String, boolean, Map)}
	 * @param cleanProperties
	 * @param endpoint
	 *		  The LTI launch url.
	 * @param launchtext
	 *		  The LTI launch text. Used if javascript is turned off.
	 * @param debug
	 *		  Useful for viewing the HTML before posting to end point.
	 * @param extra
	 * @return the HTML ready for IFRAME src = inclusion.
	 */
	@Deprecated
	public static String postLaunchHTML(final Properties cleanProperties,
			String endpoint, String launchtext, boolean debug, Map<String,String> extra) {
		Map<String, String> map = convertToMap(cleanProperties);
		return postLaunchHTML(map, endpoint, launchtext, debug, extra);
	}

	/**
	 * Create the HTML to render a POST form and then automatically submit it.
	 *
	 * @deprecated Moved to {@link #postLaunchHTML(Map, String, String, boolean, boolean, Map)}
	 * @param cleanProperties
	 * @param endpoint
	 *		  The LTI launch url.
	 * @param launchtext
	 *		  The LTI launch text. Used if javascript is turned off.
	 * @param autosubmit
	 *		  Whether or not we want the form autosubmitted
	 * @param debug
	 *		  Useful for viewing the HTML before posting to end point.
	 * @param extra
	 * @return the HTML ready for IFRAME src = inclusion.
	 */
	@Deprecated
	public static String postLaunchHTML(final Properties cleanProperties,
			String endpoint, String launchtext, boolean autosubmit, boolean debug, Map<String,String> extra) {
		Map<String, String> map = convertToMap(cleanProperties);
		return postLaunchHTML(map, endpoint, launchtext, autosubmit, debug, extra);
	}

	/**
	 * Create the HTML to render a POST form and then automatically submit it.
	 *
	 * @param cleanProperties
	 * @param endpoint
	 *		  The LTI launch url.
	 * @param launchtext
	 *		  The LTI launch text. Used if javascript is turned off.
	 * @param debug
	 *		  Useful for viewing the HTML before posting to end point.
	 * @param extra
	 *		  Useful for viewing the HTML before posting to end point.
	 * @return the HTML ready for IFRAME src = inclusion.
	 */
	public static String postLaunchHTML(
			final Map<String, String> cleanProperties, String endpoint,
			String launchtext, boolean debug, Map<String,String> extra) {
		// Assume autosubmit is true for backwards compatibility
		boolean autosubmit = true;
		return postLaunchHTML(cleanProperties, endpoint, launchtext, autosubmit, debug, extra);
	}
	/**
	 * Create the HTML to render a POST form and then automatically submit it.
	 *
	 * @param cleanProperties
	 * @param endpoint
	 *		  The LTI launch url.
	 * @param launchtext
	 *		  The LTI launch text. Used if javascript is turned off.
	 * @param autosubmit
	 *		  Whether or not we want the form autosubmitted
	 * @param extra
	 *		  Useful for viewing the HTML before posting to end point.
	 * @return the HTML ready for IFRAME src = inclusion.
	 */
	public static String postLaunchHTML(
			final Map<String, String> cleanProperties, String endpoint,
			String launchtext, boolean autosubmit, boolean debug,
			Map<String,String> extra) {

		if (cleanProperties == null || cleanProperties.isEmpty()) {
			throw new IllegalArgumentException(
					"cleanProperties == null || cleanProperties.isEmpty()");
		}
		if (endpoint == null) {
			throw new IllegalArgumentException("endpoint == null");
		}
		Map<String, String> newMap = null;
		if (debug) {
			// sort the properties for readability
			newMap = new TreeMap<String, String>(cleanProperties);
		} else {
			newMap = cleanProperties;
		}
		if ( extra == null ) extra = new TreeMap<String, String>();

		StringBuilder text = new StringBuilder();
		// paint form
		String submit_uuid = UUID.randomUUID().toString().replace("-","_");
		String submit_form_id = extra.get(EXTRA_FORM_ID);
		if ( submit_form_id == null ) submit_form_id = "ltiLaunchForm_"+submit_uuid;

		text.append("<div id=\"ltiLaunchFormArea_");
		text.append(submit_uuid);
		text.append("\">\n");
		text.append("<form action=\"");
		text.append(endpoint);
		text.append("\" name=\"ltiLaunchForm\" id=\""+submit_form_id+"\" method=\"post\" ");
		text.append(" encType=\"application/x-www-form-urlencoded\" accept-charset=\"utf-8\">\n");
		if ( debug ) {
		}
		for (Entry<String, String> entry : newMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value == null)
				continue;
			// This will escape the contents pretty much - at least
			// we will be safe and not generate dangerous HTML
			key = htmlspecialchars(key);
			value = htmlspecialchars(value);
			text.append("<input type=\"hidden\" name=\"");
			text.append(key);
			text.append("\" value=\"");
			text.append(value);
			text.append("\"/>\n");
		}

		// Paint the submit button
		if ( debug ) {
			text.append("<input type=\"submit\" value=\"");
			text.append(htmlspecialchars(launchtext));
			text.append("\">\n");

			text.append(" <input type=\"Submit\" value=\"Show Launch Data\" onclick=\"document.getElementById('ltiLaunchDebug_");
			text.append(submit_uuid);
			text.append("').style.display = 'block';return false;\">\n");
		} else {
			text.append("<input type=\"submit\" style=\"display: none\" value=\"");
			text.append(htmlspecialchars(launchtext));
			text.append("\">\n");
		}

		if ( extra != null ) {
			String button_html = extra.get(EXTRA_BUTTON_HTML);
			if ( button_html != null ) text.append(button_html);
		}

		text.append("</form>\n");
		text.append("</div>\n");

		// Paint the auto-pop up if we are transitioning from https: to http:
		// and are not already the top frame...
		String error_timeout = null;
		String http_popup = null;
		if ( extra != null ) {
			error_timeout = extra.get(EXTRA_ERROR_TIMEOUT);
			http_popup = extra.get(EXTRA_HTTP_POPUP);
		}
		if ( extra == null ) error_timeout = "Unable to send launch to remote URL";
		text.append("<script type=\"text/javascript\">\n");
		text.append("var open_in_new_window = false;\n");
		if ( ! EXTRA_HTTP_POPUP_FALSE.equals(http_popup) ) {
			text.append("if (window.top!=window.self) {\n");
			text.append("  var theform = document.getElementById('");
			text.append(submit_form_id);
			text.append("');\n");
			text.append("  if ( theform && theform.action ) {\n");
			text.append("    var formAction = theform.action;\n");
			text.append("    var ourUrl = window.location.href;\n");
			text.append("    if ( formAction.indexOf('http://') == 0 && ourUrl.indexOf('https://') == 0 ) {\n");
			text.append("      theform.target = '_blank';\n");
			text.append("      window.console && console.log('Launching http from https in new window!');\n");
			text.append("      open_in_new_window = true;\n");
			text.append("    }\n");
			text.append("  }\n");
			text.append("}\n");
		}
		text.append("</script>\n");

		// paint debug output
		if (debug) {
			text.append("<pre id=\"ltiLaunchDebug_");
			text.append(submit_uuid);
			text.append("\" style=\"display: none\">\n");
			text.append("<b>LTI Endpoint</b>\n");
			text.append(endpoint);
			text.append("\n\n");
			text.append("<b>LTI Parameters:</b>\n");
			for (Entry<String, String> entry : newMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (value == null)
					continue;
				text.append(htmlspecialchars(key));
				text.append("=");
				text.append(htmlspecialchars(value));
				text.append("\n");
			}
			text.append("</pre>\n");
			if ( extra != null ) {
				String base_string = extra.get("BaseString");
				if ( base_string != null ) {
					text.append("<!-- Base String\n");
					text.append(base_string.replaceAll("-->","__>"));
					text.append("\n-->\n");
				}
			}
		} else if ( autosubmit ) {
			// paint auto submit script
			text.append("<script language=\"javascript\"> \n");
			text.append("    document.getElementById('ltiLaunchFormArea_");
			text.append(submit_uuid);
			text.append("').style.display = \"none\";\n");
			text.append("    document.getElementById('");
			text.append(submit_form_id);
			text.append("').submit(); \n");
			text.append("if ( ! open_in_new_window ) {\n");
			text.append("   setTimeout(function() { alert(\""+htmlspecialchars(error_timeout)+"\"); }, 4000);\n");
			text.append("}\n");
			text.append("</script> \n");
		}

		String extraJavaScript = extra.get(EXTRA_JAVASCRIPT);
		if ( extraJavaScript != null ) {
			text.append("<script> \n");
			text.append(extraJavaScript);
			text.append("</script> \n");
		}

		String htmltext = text.toString();
		return htmltext;
	}

	/**
	 * getOAuthURL - Form a GET request signed by OAuth
	 * @param method
	 * @param url
	 * @param oauth_consumer_key
	 * @param oauth_secret
	 */
	public static String getOAuthURL(String method, String url,
		String oauth_consumer_key, String oauth_secret)
	{
		return getOAuthURL(method, url, oauth_consumer_key, oauth_secret, null);
	}

	/**
	 * getOAuthURL - Form a GET request signed by OAuth
	 * @param method
	 * @param url
	 * @param oauth_consumer_key
	 * @param oauth_secret
	 * @param signature
	 */
	public static String getOAuthURL(String method, String url,
		String oauth_consumer_key, String oauth_secret, String signature)
	{
		if ( url == null ) return null;
		OAuthMessage om = new OAuthMessage(method, url, null);
		om.addParameter(OAuth.OAUTH_CONSUMER_KEY, oauth_consumer_key);
		if ( signature == null ) signature = OAuth.HMAC_SHA1;
		om.addParameter(OAuth.OAUTH_SIGNATURE_METHOD, signature);
		om.addParameter(OAuth.OAUTH_VERSION, "1.0");
		om.addParameter(OAuth.OAUTH_TIMESTAMP, Long.valueOf((new Date().getTime()) / 1000).toString());
		om.addParameter(OAuth.OAUTH_NONCE, UUID.randomUUID().toString());

		OAuthConsumer oc = new OAuthConsumer(null, oauth_consumer_key, oauth_secret, null);
		try {
		    OAuthSignatureMethod osm = OAuthSignatureMethod.newMethod(signature, new OAuthAccessor(oc));
		    osm.sign(om);
		    url = OAuth.addParameters(url, om.getParameters());
		    return url;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
		 * getOAuthURL - Form a GET request signed by OAuth
	 * @param method
	 * @param url
	 * @param oauth_consumer_key
	 * @param oauth_secret
	 * HttpURLConnection connection = sendOAuthURL('GET', url, oauth_consumer_key, oauth_secret)
	 * int responseCode = connection.getResponseCode();
	 * String data = readHttpResponse(connection)
	 */
	public static HttpURLConnection sendOAuthURL(String method, String url, String oauth_consumer_key, String oauth_secret)
	{
		String oauthURL = getOAuthURL(method, url, oauth_consumer_key, oauth_secret);

		try {
			URL urlConn = new URL(oauthURL);
			HttpURLConnection connection = (HttpURLConnection) urlConn.openConnection();
			connection.setRequestMethod(method);

			// Since Java won't send Content-length unless we really send
			// content - send some data character so we don't
			// send a broken PUT
			if ( ! "GET".equals(method) ) {
				connection.setDoOutput(true);
				OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream());
				out.write("42");
				out.close();
			}
			connection.connect();
			int responseCode = connection.getResponseCode();
			return connection;
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
		 * getResponseCode - Read the HTTP Response
	 * @param connection
	 */
	public static int getResponseCode(HttpURLConnection connection)
	{
		try {
			return connection.getResponseCode();
		} catch(Exception e) {
			return HttpURLConnection.HTTP_INTERNAL_ERROR;
		}
	}


	/**
		 * readHttpResponse - Read the HTTP Response
	 * @param connection
	 */
	public static String readHttpResponse(HttpURLConnection connection)
	{
		try {
			BufferedReader in = new BufferedReader(
			new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @deprecated See: {@link #parseDescriptor(Map, Map, String)}
	 * @param launch_info
	 *		  Variable is mutated by this method.
	 * @param postProp
	 *		  Variable is mutated by this method.
	 * @param descriptor
	 * @return
	 */
	@Deprecated
	public static boolean parseDescriptor(Properties launch_info,
			Properties postProp, String descriptor) {
		// this is an ugly copy/paste of the non-@deprecated method
		// could not convert data types as they variables get mutated (ugh)
		Map<String, Object> tm = null;
		try {
			tm = XMLMap.getFullMap(descriptor.trim());
		} catch (Exception e) {
			log.warn("LTIUtil exception parsing LTI descriptor: {}", e.getMessage());
			return false;
		}
		if (tm == null) {
			log.warn("Unable to parse XML in parseDescriptor");
			return false;
		}

		String launch_url = StringUtils.trimToNull(XMLMap.getString(tm,
					"/basic_lti_link/launch_url"));
		String secure_launch_url = StringUtils.trimToNull(XMLMap.getString(tm,
					"/basic_lti_link/secure_launch_url"));
		if (launch_url == null && secure_launch_url == null)
			return false;

		setProperty(launch_info, "launch_url", launch_url);
		setProperty(launch_info, "secure_launch_url", secure_launch_url);

		// Extensions for hand-authored placements - The export process should scrub
		// these
		setProperty(launch_info, "key", StringUtils.trimToNull(XMLMap.getString(tm,
						"/basic_lti_link/x-secure/launch_key")));
		setProperty(launch_info, "secret", StringUtils.trimToNull(XMLMap.getString(tm,
						"/basic_lti_link/x-secure/launch_secret")));

		List<Map<String, Object>> theList = XMLMap.getList(tm,
				"/basic_lti_link/custom/parameter");
		for (Map<String, Object> setting : theList) {
			log.debug("Setting={}", setting);
			String key = XMLMap.getString(setting, "/!key"); // Get the key attribute
			String value = XMLMap.getString(setting, "/"); // Get the value
			if (key == null || value == null)
				continue;
			key = "custom_" + mapKeyName(key);
			log.debug("key={} val={}", key, value);
			postProp.setProperty(key, value);
		}
		return true;
	}

	/**
	 *
	 * @param launch_info
	 *		  Variable is mutated by this method.
	 * @param postProp
	 *		  Variable is mutated by this method.
	 * @param descriptor
	 * @return
	 */
	public static boolean parseDescriptor(Map<String, String> launch_info,
			Map<String, String> postProp, String descriptor) {
		Map<String, Object> tm = null;
		try {
			tm = XMLMap.getFullMap(descriptor.trim());
		} catch (Exception e) {
			log.warn("LTIUtil exception parsing LTI descriptor: {}", e.getMessage());
			return false;
		}
		if (tm == null) {
			log.warn("Unable to parse XML in parseDescriptor");
			return false;
		}

		String launch_url = StringUtils.trimToNull(XMLMap.getString(tm,
					"/basic_lti_link/launch_url"));
		String secure_launch_url = StringUtils.trimToNull(XMLMap.getString(tm,
					"/basic_lti_link/secure_launch_url"));
		if (launch_url == null && secure_launch_url == null)
			return false;

		setProperty(launch_info, "launch_url", launch_url);
		setProperty(launch_info, "secure_launch_url", secure_launch_url);

		// Extensions for hand-authored placements - The export process should scrub
		// these
		setProperty(launch_info, "key", StringUtils.trimToNull(XMLMap.getString(tm,
						"/basic_lti_link/x-secure/launch_key")));
		setProperty(launch_info, "secret", StringUtils.trimToNull(XMLMap.getString(tm,
						"/basic_lti_link/x-secure/launch_secret")));

		List<Map<String, Object>> theList = XMLMap.getList(tm,
				"/basic_lti_link/custom/parameter");
		for (Map<String, Object> setting : theList) {
			log.debug("Setting={}", setting);
			String key = XMLMap.getString(setting, "/!key"); // Get the key attribute
			String value = XMLMap.getString(setting, "/"); // Get the value
			if (key == null || value == null)
				continue;
			key = "custom_" + mapKeyName(key);
			log.debug("key={} val={}", key, value);
			postProp.put(key, value);
		}
		return true;
	}

	// Remove fields that should not be exported
	public static String prepareForExport(String descriptor) {
		Map<String, Object> tm = null;
		try {
			tm = XMLMap.getFullMap(descriptor.trim());
		} catch (Exception e) {
			log.warn("LTIUtil exception parsing LTI descriptor {}", e.getMessage());
			return null;
		}
		if (tm == null) {
			log.warn("Unable to parse XML in prepareForExport");
			return null;
		}
		XMLMap.removeSubMap(tm, "/basic_lti_link/x-secure");
		String retval = XMLMap.getXML(tm, true);
		return retval;
	}

	/**
	 * The parameter name is mapped to lower case and any character that is
	 * neither a number or letter is replaced with an "underscore". So if a custom
	 * entry was as follows:
	 *
	 * <parameter name="Vendor:Chapter">1.2.56</parameter>
	 *
	 * Would map to: custom_vendor_chapter=1.2.56
	 */
	public static String mapKeyName(String keyname) {
		StringBuffer sb = new StringBuffer();
		if (keyname == null)
			return null;
		keyname = keyname.trim();
		if (keyname.length() < 1)
			return null;
		for (int i = 0; i < keyname.length(); i++) {
			Character ch = Character.toLowerCase(keyname.charAt(i));
			if (Character.isLetter(ch) || Character.isDigit(ch)) {
				sb.append(ch);
			} else {
				sb.append('_');
			}
		}
		return sb.toString();
	}

	/**
	 * Mutates the passed Map<String, String> map variable. Puts the key,value
	 * into the Map if the value is not null and is not empty.
	 *
	 * @param map
	 *		  Variable is mutated by this method.
	 * @param key
	 * @param value
	 */
	public static void setProperty(final Map<String, String> map,
			final String key, final String value) {
		if (value != null && !"".equals(value)) {
			map.put(key, value);
		}
	}

	/**
	 * Mutates the passed Properties props variable. Puts the key,value into the
	 * Map if the value is not null and is not empty.
	 *
	 * @deprecated See: {@link #setProperty(Map, String, String)}
	 * @param props
	 *		  Variable is mutated by this method.
	 * @param key
	 * @param value
	 */
	@Deprecated
	public static void setProperty(Properties props, String key, String value) {
		if (value == null) return;
		if (value.trim().length() < 1) return;
		props.setProperty(key, value);
	}

	// Basic utility to encode form text - handle the "safe cases"
	public static String htmlspecialchars(String input) {
		if (input == null)
			return null;
		String encodedData = StringEscapeUtils.escapeHtml4(input);
		encodedData = encodedData.replace("\r", "&#13;").replace("\n", "&#10;");
		return encodedData;
	}

	/**
	 * Merge two resource_link_id or context_id history strings and add a new one
	 */
	public static String mergeCSV(String old_id_history, String new_id_history, String old_resource_link_id)
	{
		if ( isBlank(old_id_history) ) old_id_history = "";
		if ( isBlank(new_id_history) ) new_id_history = "";
		List<String> old_id_list = Arrays.asList(old_id_history.split(","));
		List<String> new_id_list = Arrays.asList(new_id_history.split(","));

		List<String> new_list = new ArrayList<String>();

		// Pull in the old ids.
		for ( String old_id : old_id_list ) {
			if ( isBlank(old_id) ) continue;
			if ( new_list.contains(old_id) ) continue;
			new_list.add(old_id);
		}

		// Pull in the new ids
		for ( String new_id : new_id_list ) {
			if ( isBlank(new_id) ) continue;
			if ( new_list.contains(new_id) ) continue;
			new_list.add(new_id);
		}

		if ( isNotBlank(old_resource_link_id) && ! new_list.contains(old_resource_link_id) ) new_list.add(old_resource_link_id);
		String id_history = String.join(",", new_list);
		return id_history;
	}

	/**
	 * Simple utility method deal with a request that has the wrong URL when behind
	 * a proxy.
	 *
	 * @param servletUrl
	 * @param extUrl
	 *   The url that the external world sees us as responding to.  This needs to be
	 *   up to but not including the last slash like and not include any path information
	 *   https://www.sakailms.org/ - although we do compensate for extra stuff at the end.
	 * @return
	 *   The full path of the request with extUrl in place of whatever the request
	 *   thinks is the current URL.
	 */
	static public String getRealPath(String servletUrl, String extUrl)
	{
		Pattern pat = Pattern.compile("^https??://[^/]*");
		// Deal with potential bad extUrl formats
		Matcher m = pat.matcher(extUrl);
		if (m.find()) {
			extUrl = m.group(0);
		}

		String retval = pat.matcher(servletUrl).replaceFirst(extUrl);
		return retval;
	}

	static public String getRealPath(HttpServletRequest request, String extUrl)
	{
		String URLstr = request.getRequestURL().toString();
		String retval = getRealPath(URLstr, extUrl);
		return retval;
	}

	/**
	 * Simple utility method to help with the migration from Properties to
	 * Map<String, String>.
	 *
	 * @param properties
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
		public static Map<String, String> convertToMap(final Properties properties) {
			final Map<String, String> map = new HashMap(properties);
			return map;
		}

	/**
	 * Simple utility method to help with the migration from Map<String, String>
	 * to Properties.
	 *
	 * @deprecated Should migrate to Map<String, String> signatures.
	 * @param map
	 * @return
	 */
	@Deprecated
	public static Properties convertToProperties(final Map<String, String> map) {
		final Properties properties = new Properties();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
		}
		return properties;
	}

	/**
	 * <p>
	 * Checks if a String is whitespace, empty ("") or null.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isBlank(null)	  = true
	 * StringUtils.isBlank("")		= true
	 * StringUtils.isBlank(" ")	   = true
	 * StringUtils.isBlank("bob")	 = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param str
	 *		  the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 * @since 2.0
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Checks if a String is not empty (""), not null and not whitespace only.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isNotBlank(null)	  = false
	 * StringUtils.isNotBlank("")		= false
	 * StringUtils.isNotBlank(" ")	   = false
	 * StringUtils.isNotBlank("bob")	 = true
	 * StringUtils.isNotBlank("  bob  ") = true
	 * </pre>
	 *
	 * @param str
	 *		  the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null and not
	 *		 whitespace
	 * @since 2.0
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * <p>
	 * Compares two Strings, returning <code>true</code> if they are equal.
	 * </p>
	 *
	 * <p>
	 * <code>null</code>s are handled without exceptions. Two <code>null</code>
	 * references are considered to be equal. The comparison is case sensitive.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.equals(null, null)   = true
	 * StringUtils.equals(null, "abc")  = false
	 * StringUtils.equals("abc", null)  = false
	 * StringUtils.equals("abc", "abc") = true
	 * StringUtils.equals("abc", "ABC") = false
	 * </pre>
	 *
	 * @see java.lang.String#equals(Object)
	 * @param str1
	 *		  the first String, may be null
	 * @param str2
	 *		  the second String, may be null
	 * @return <code>true</code> if the Strings are equal, case sensitive, or both
	 *		 <code>null</code>
	 */
	public static boolean equals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	/**
	 * <p>
	 * Compares two Strings, returning <code>true</code> if they are equal
	 * ignoring the case.
	 * </p>
	 *
	 * <p>
	 * <code>null</code>s are handled without exceptions. Two <code>null</code>
	 * references are considered equal. Comparison is case insensitive.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.equalsIgnoreCase(null, null)   = true
	 * StringUtils.equalsIgnoreCase(null, "abc")  = false
	 * StringUtils.equalsIgnoreCase("abc", null)  = false
	 * StringUtils.equalsIgnoreCase("abc", "abc") = true
	 * StringUtils.equalsIgnoreCase("abc", "ABC") = true
	 * </pre>
	 *
	 * @see java.lang.String#equalsIgnoreCase(String)
	 * @param str1
	 *		  the first String, may be null
	 * @param str2
	 *		  the second String, may be null
	 * @return <code>true</code> if the Strings are equal, case insensitive, or
	 *		 both <code>null</code>
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	/**
	 * Escapes special characters in a given input string to their corresponding escape sequences.
	 *
	 * This method converts newline characters (\n), carriage return characters (\r),
	 * tab characters (\t), backspace characters (\b), form feed characters (\f),
	 * backslash (\), double quote ("), and single quote (') to their escape sequences.
	 * Additionally, it converts any non-printable ASCII characters (outside the range 32 to 126)
	 * to their Unicode escape sequences (\ uXXXX).
	 *
	 * @param input the string to escape special characters from
	 * @return the escaped string with special characters replaced by their escape sequences
	 *
	 * Note: This code was written by ChatGPT 17-Aug-2024 and Reviewed by Dr. Chuck
	 */
	public static String escapeSpecialCharacters(String input) {
		StringBuilder escapedString = new StringBuilder();
		for (char c : input.toCharArray()) {
			switch (c) {
				case '\n':
					escapedString.append("\\n");
					break;
				case '\r':
					escapedString.append("\\r");
					break;
				case '\t':
					escapedString.append("\\t");
					break;
				case '\b':
					escapedString.append("\\b");
					break;
				case '\f':
					escapedString.append("\\f");
					break;
				case '\\':
					escapedString.append("\\\\");
					break;
				case '\"':
					escapedString.append("\\\"");
					break;
				case '\'':
					escapedString.append("\\'");
					break;
				default:
					if (c < 32 || c > 126) {
						escapedString.append(String.format("\\u%04x", (int) c));
					} else {
						escapedString.append(c);
					}
			}
		}
		return escapedString.toString();
	}

	/**
	 * Return a ISO 8601 formatted date
	 */
	public static String getISO8601() {
		return getISO8601(null);
	}

	/**
	 * Return a ISO 8601 formatted date
	 */
	public static String getISO8601(Date date) {
		if ( date == null ) {
			date = new Date();
		}
		SimpleDateFormat isoFormat = new SimpleDateFormat(ISO_8601_FORMAT);
		isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timestamp = isoFormat.format(date);
		timestamp = timestamp.replace("UTC", "Z");
		return timestamp;
	}

	/**
	 * Parse an IMS 8601 date the strict ISO8601 but at the same time be flexible about it...
	 *
	 * We keep this in our own litte corner in case special adjustments are needed as
	 * we gain experience with the variatios in date formats in actual LTI Advantage tool practice.
	 *
	 * All the IMS examples  use the most common ISO8601/UTC format as in:
	 *
	 *   "startDateTime": "2018-03-06T20:05:02Z",
	 *   "endDateTime": "2018-04-06T22:05:03Z"
	 *
	 * So we make particular effort to make sure this works and gives the right kind of date.  And then
	 * for any other reasonable format, we try our best.
	 */
	// https://www.imsglobal.org/spec/lti-ags/v2p0/#startdatetime
	// https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
	// https://stackoverflow.com/questions/4024544/how-to-parse-dates-in-multiple-formats-using-simpledateformat
	public static Date parseIMS8601(String timestamp) {

		if ( timestamp == null ) return null;

		// Make sure that ISO8601 Z format dates are *perfect*
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date result =  df.parse(timestamp);
			return result;
		} catch(java.text.ParseException e) {
			// Ignore
		}

        String[] possibleDateFormats =
              {
                    "yyyy.MM.dd G 'at' HH:mm:ss z",
                    "EEE, MMM d, ''yy",
                    "h:mm a",
                    "hh 'o''clock' a, zzzz",
                    "K:mm a, z",
                    "yyyyy.MMMMM.dd GGG hh:mm aaa",
                    "EEE, d MMM yyyy HH:mm:ss Z",
                    "yyMMddHHmmssZ",
                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                    "YYYY-'W'ww-u",
                    "EEE, dd MMM yyyy HH:mm:ss z",
                    "EEE, dd MMM yyyy HH:mm zzzz",
                    "yyyy-MM-dd'T'HH:mm:ssX",
                    "yyyy-MM-dd'T'HH:mm:ssZ",
                    "yyyy-MM-dd'T'HH:mm:ss.SSSzzzz",
                    "yyyy-MM-dd'T'HH:mm:sszzzz",
                    "yyyy-MM-dd'T'HH:mm:ss z",
                    "yyyy-MM-dd'T'HH:mm:ssz",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd'T'HHmmss.SSSz",
                    "yyyy-MM-dd",
                    "yyyyMMdd",
                    "dd/MM/yy",
                    "dd/MM/yyyy",
                    "MM/dd/yyyy",
              };

		for( int i = 0; i<possibleDateFormats.length; i++) {
			df = new SimpleDateFormat(possibleDateFormats[i]);
			try {
				Date result =  df.parse(timestamp);
				return result;
			} catch(java.text.ParseException e) {
				continue;
			}
		}
		return null;
	}

	/**
	 * Shift a date from the JVM Timezone to Another Timezone
	 *
	 * Usually the resulting date is then usually moved into UTC for transport
	 */
	public static Date shiftJVMDateToTimeZone(Date date, String timeZone) {
		if ( date == null || timeZone == null ) return null;

		// Get the JVM TimeZone
		TimeZone tzJVM = TimeZone.getDefault();
		TimeZone tzNew = TimeZone.getTimeZone(timeZone);
		long dueTime = date.getTime();
		// Shift into UTC and then back to the destination timezone
		dueTime = dueTime - tzJVM.getRawOffset() + tzNew.getRawOffset();
		Date retval = new Date(dueTime);
		return retval;
	}

	// Parse and return a JSONObject (empty if necessary)
	// Use this when there is no way to recover from broken JSON except start over
	public static JSONObject parseJSONObject(String str)
	{
		JSONObject content_json = null;
		if ( str != null ) {
			content_json = (JSONObject) JSONValue.parse(str);
		}
		if ( content_json == null ) content_json = new JSONObject();
		if ( ! (content_json instanceof JSONObject) ) content_json = new JSONObject();
		return content_json;
	}

	// Parse a provider profile with lots of error checking...
	public static JSONArray forceArray(Object obj)
	{
		if ( obj == null ) return null;
		if ( obj instanceof JSONArray ) return (JSONArray) obj;
		JSONArray retval = new JSONArray();
		retval.add(obj);
		return retval;
	}

	// Return a JSONArray or null. Promote a JSONObject to an array
	public static JSONArray getArray(JSONObject obj, String key)
	{
		if ( obj == null ) return null;
		Object o = obj.get(key);
		if ( o == null ) return null;
		if ( o instanceof JSONArray ) return (JSONArray) o;
		if ( o instanceof JSONObject ) {
			JSONArray retval = new JSONArray();
			retval.add(o);
			return retval;
		}

		// If this is a java.lang (i.e. String, Long, etc)
		String className = o.getClass().getName();
		if ( className.startsWith("java.lang") ) {
			JSONArray retval = new JSONArray();
			retval.add(o);
			return retval;
		}
		return null;
	}

	// Return a JSONObject or null
	public static JSONObject getObject(JSONObject obj, String key)
	{
		if ( obj == null ) return null;
		if ( key == null ) return null;
		Object o = obj.get(key);
		if ( o == null ) return null;
		if ( o instanceof JSONObject ) return (JSONObject) o;
		return null;
	}

	// Return a String or null
	public static String getString(JSONObject obj, String key)
	{
		if ( obj == null ) return null;
		if ( key == null ) return null;
		Object o = obj.get(key);
		if ( o == null ) return null;
		if ( o instanceof String ) return (String) o;
		return null;
	}

	// Return a Long or null
	public static Long getLong(JSONObject obj, String key) {
		if ( obj == null ) return null;
		if ( key == null ) return null;
		Object o = obj.get(key);

		if (o instanceof Number)
			return Long.valueOf(((Number) o).longValue());
		if (o instanceof String) {
			if ( ((String)o).length() < 1 ) return Long.valueOf(-1L);
			try {
				return Long.valueOf((String) o);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	// Return a Double or null
	public static Double getDouble(JSONObject obj, String key) {
		if ( obj == null ) return null;
		if ( key == null ) return null;
		Object o = obj.get(key);

		if (o instanceof Number)
			return Double.valueOf(((Number) o).doubleValue());
		if (o instanceof String) {
			if ( ((String)o).length() < 1 ) return Double.valueOf(-1.0);
			try {
				return Double.valueOf((String) o);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public static String getBrowserSignature(HttpServletRequest request) {
		String [] look_at = { "x-forwarded-proto", "x-forwarded-port", "host",
			"accept-encoding", "cf-ipcountry", "user-agent", "accept", "accept-language"};
		StringBuilder text = new StringBuilder();
		for (String s: look_at) {
			String value = request.getHeader(s);
			if ( isBlank(value) ) continue;
			text.append(":::");
			text.append(s);
			text.append("=");
			text.append(value);
		}
		return text.toString();
	}

	public static void sendHTMLPage(HttpServletResponse res, String body)
	{
		try
		{
			res.setContentType("text/html; charset=UTF-8");
			res.setCharacterEncoding("utf-8");
			res.addHeader("Cache-Control", "no-store");
			java.io.PrintWriter out = res.getWriter();

			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
			out.println("<html>\n<head>");
			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			out.println("</head>\n<body>\n");
			out.println(body);
			out.println("\n</body>\n</html>");
		}
		catch (Exception e)
		{
			log.warn("Failed to send HTML page.", e);
		}

	}

	/**
	 * These routines are used to convert objects to their corresponding primitive types. 
	 * They are inspired by the routines in the Apache Commons Lang library.
	 * 
	 * https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/math/NumberUtils.html
	 * 
	 * The primary difference various types of objects and convert them no matter what.
	 * The reas these are needed is when we get data from different databases, we might get an Integer,
	 * Long, Double, BigDecimal, String, etc. and we want to convert them to the corresponding primitive type regardless
	 * of the type of the object.
	 * 
	 * We would put these into Sakai Kernel except that the tsugi-util cannot have Sakai specific dependencies
	 * as it can be released as a Java library and used in any environment.
	 */

	public static Integer toInteger(Object o, Integer defaultValue) {
		if (o instanceof String) {
			try {
				return Integer.valueOf((String) o);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		if (o instanceof Number) {
			return Integer.valueOf(((Number) o).intValue());
		}
		return defaultValue;
	}

	public static int toInt(Object o) {
		Integer retval = toInteger(o, -1);
		return retval.intValue();
	}

	public static Long toLong(Object o, Long defaultValue) {
		if (o instanceof String) {
			try {
				return Long.valueOf((String) o);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		if (o instanceof Number) {
			return Long.valueOf(((Number) o).longValue());
		}
		return defaultValue;
	}

	public static Long toLong(Object key) {
		return toLong(key, -1L);
	}

	public static Long toLongKey(Object key) {
		return toLong(key, -1L);
	}

	public static Long toLongNull(Object key) {
		return toLong(key, null);
	}

	public static Double toDouble(Object key, Double defaultValue) {
		if (key == null) {
			return defaultValue;
		}
		if (key instanceof Number) {
			return Double.valueOf(((Number) key).doubleValue());
		}
		if (key instanceof String) {
			try {
				return Double.valueOf((String) key);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public static Double toDoubleNull(Object key) {
		return toDouble(key, null);
	}

}
