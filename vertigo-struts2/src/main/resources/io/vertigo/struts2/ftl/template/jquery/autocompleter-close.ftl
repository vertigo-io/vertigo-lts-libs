<#--
/*
 */
-->
<#if parameters.parentTheme = 'xhtml_read'>
   <#-- rien -->
<#else>
<#assign escapedOptionId="${parameters.escapedId}">
<@s.script type='text/javascript'>
jQuery(document).ready(function () {
  <#if parameters.valueWidget?if_exists != "">
	jQuery("#${parameters.id}").val("${parameters.valueWidget}");
  </#if>
	var options_${escapedOptionId} = {};
  <#if parameters.widgetid?if_exists != "">
	options_${escapedOptionId}.hiddenid = "${parameters.widgetid}";
  </#if>
  <#if parameters.delay??>
	options_${escapedOptionId}.delay = ${parameters.delay};
  </#if>
  <#if parameters.loadMinimumCount??>
	options_${escapedOptionId}.minLength = ${parameters.loadMinimumCount};
  </#if>
  <#if parameters.autoFocus?default(false) >
	options_${escapedOptionId}.autoFocus = true;
  </#if>
  <#if parameters.selectBox?default(false) || (parameters.list?? && parameters.listKey?? && !parameters.hrefUrl??) >
	options_${escapedOptionId}.selectBox = true;
  <#else>
	options_${escapedOptionId}.selectBox = false;
  </#if>
  <#if parameters.selectBoxIcon?default(false) >
	options_${escapedOptionId}.selectBoxIcon = true;
  </#if>
  <#if parameters.onSearchTopics?exists>
	options_${escapedOptionId}.onsearchtopics = "${parameters.onSearchTopics}";
  </#if>
  <#if parameters.forceValidOption?default(true) >
	options_${escapedOptionId}.forceValidOption = true;
  <#else>
	options_${escapedOptionId}.forceValidOption = false;
  </#if>
  <#if parameters.onFocusTopics?exists>
	options_${escapedOptionId}.onfocustopics = "${parameters.onFocusTopics}";
  </#if>
  <#if parameters.onSelectTopics?exists>
	options_${escapedOptionId}.onselecttopics = "${parameters.onSelectTopics}";
  </#if>
  <#if parameters.requestType?exists>
	options_${escapedOptionId}.requesttype = "${parameters.requestType}";
  </#if>
  <#if parameters.list?? && !parameters.listKey?? && !parameters.selectBox?? &&  !parameters.hrefUrl??>
	options_${escapedOptionId}.list = new Array();
<@s.iterator value="parameters.list">
        <#if parameters.listValue??>
            <#if stack.findString(parameters.listValue)??>
              <#assign itemValue = stack.findString(parameters.listValue)/>
            <#else>
              <#assign itemValue = ''/>
            </#if>
        <#else>
            <#assign itemValue = stack.findString('top')/>
        </#if>
	options_${escapedOptionId}.list.push("${itemValue}");
</@s.iterator>
  </#if>
  <#if parameters.remoteList?? && parameters.hrefUrl?? && !parameters.selectBox??>
	options_${escapedOptionId}.hrefparameter = "list=${parameters.remoteList}<#rt/>
	<#if parameters.remoteListKey??>
		&listKey=${parameters.remoteListKey}<#t/>
	</#if>
	<#if parameters.remoteListValue??>
		&listValue=${parameters.remoteListValue}<#t/>
	</#if>
	&CTX=${CTX}";<#lt/>
	<#-- Must be removed when hrefparameter is used
	options_${escapedOptionId}.list = "${parameters.remoteList}";
	-->
	<#if parameters.remoteListKey??>
	options_${escapedOptionId}.listkey = "${parameters.remoteListKey}";
	</#if>
	<#if parameters.remoteListValue??>
	options_${escapedOptionId}.listvalue = "${parameters.remoteListValue}";
	</#if>
	<#if parameters.listLabel??>
	options_${escapedOptionId}.listlabel = "${parameters.listLabel}";
	</#if>
  </#if>
  <#include "/${parameters.templateDir}/jquery/base.ftl" />
  <#include "/${parameters.templateDir}/jquery/interactive.ftl" />
  <#include "/${parameters.templateDir}/jquery/topics.ftl" />
  <#include "/${parameters.templateDir}/jquery/action.ftl" />
  <#include "/${parameters.templateDir}/jquery/container.ftl" />
  <#include "/${parameters.templateDir}/jquery/draggable.ftl" />
  <#include "/${parameters.templateDir}/jquery/droppable.ftl" />
  <#include "/${parameters.templateDir}/jquery/resizable.ftl" />
  <#include "/${parameters.templateDir}/jquery/selectable.ftl" />
  <#include "/${parameters.templateDir}/jquery/sortable.ftl" />

  <#include "/${parameters.templateDir}/jquery/jquery-ui-bind.ftl" />
  <#assign escapedIconId="icon_${parameters.id?string?replace('.', '_')}">
  initAutocompleter('#${parameters.widgetid}', '#${escapedOptionId}', '#${escapedIconId}', ${parameters.loadMinimumCount!1});
 });
</@s.script>
</#if>