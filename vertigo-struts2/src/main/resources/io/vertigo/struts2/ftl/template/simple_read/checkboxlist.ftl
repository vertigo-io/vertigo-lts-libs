<#--
/*
 * $Id: checkboxlist.ftl,v 1.1 2014/02/26 17:49:02 npiedeloup Exp $
 */
-->
<#if parameters.nameValue?? && parameters.nameValue?has_content >
	<#assign itemCount = 0/>
	<#list parameters.nameValue as selectedValue>
		<#assign itemCount = itemCount + 1/>
<span<#rt/>
		<#if parameters.id??>
 id="${parameters.id}-${itemCount}"<#rt/>
		</#if>
		<#if parameters.title??>
 title="${parameters.title}"<#rt/>
		</#if>
		<#assign previousCssClass = appendedCssClass!''/>
		<#assign appendedCssClass = previousCssClass +' checkbox-checked'/>
		<#include "/${parameters.templateDir}/simple/css.ftl" /><#t/>
		<#assign appendedCssClass = previousCssClass/>
		<#include "/${parameters.templateDir}/simple/scripting-events.ftl" /><#t/>
		<#include "/${parameters.templateDir}/simple/common-attributes.ftl" /><#t/>
		<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" /><#t/>
		><#t/>
		<#if selectedValue?? && selectedValue!='' >
			<#if parameters.list.getById??>
				<#assign paramListKey = (parameters.listKey != 'top' && parameters.listKey != 'key')?then(parameters.listKey, util.getIdField(parameters.list)) />
				<#assign paramListValue = (parameters.listValue != 'top')?then(parameters.listValue, util.getDisplayField(parameters.list)) />
				<#if (parameters.listKey != 'top' && parameters.listKey != 'key') >
					<#assign uiObject = parameters.list.getById(parameters.listKey, selectedValue) />
				</#if>
				<#if uiObject??>
				 ${uiObject.get(parameters.listValue)?replace("\n", "<br/>")}<#t/>
				</#if>
			<#else> <#-- si pas de getById : liste ou map brute -->
				<#list parameters.list as entry>
					<#if entry.key = selectedValue>
					 ${entry.value?replace("\n", "<br/>")}<#t/>
					</#if>
				</#list>
			</#if>
		</#if>
		</span><#t/>
		<br/><#lt/>
	</#list>
</#if>


