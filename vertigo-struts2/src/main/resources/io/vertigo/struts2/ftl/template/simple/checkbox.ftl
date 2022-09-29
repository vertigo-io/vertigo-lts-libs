<#--
/*
 * $Id: checkbox.ftl,v 1.3 2015/02/04 npiedeloup$
 * Support multiselect.
 */
-->
<#assign hiddenPrefix = '__checkbox_'>
<#assign itemValue = stack.findValue(parameters.name)?default('')/>

<input type="checkbox" name="${parameters.name}" value="${parameters.fieldValue}"<#rt/>
<#if itemValue?is_enumerable && parameters.fieldValue?? >
	<#assign hiddenPrefix = '__multiselect_'>
	<#if itemValue?seq_contains(parameters.fieldValue) >
 checked="checked"<#rt/>
 	</#if>
<#else>
	<#if parameters.nameValue?? && parameters.nameValue>
 checked="checked"<#rt/>
	</#if>
</#if>
<#if parameters.disabled!false>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.tabindex?has_content>
 tabindex="${parameters.tabindex}"<#rt/>
</#if>
<#if parameters.id?has_content>
 id="${parameters.id}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/css.ftl" />
<#if parameters.title?has_content>
 title="${parameters.title}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
/><#t/>
<input type="hidden" id="${hiddenPrefix}${parameters.id}" name="${hiddenPrefix}${parameters.name}" value="${parameters.fieldValue}"<#rt/>
<#if parameters.disabled!false>
 disabled="disabled"<#rt/>
</#if>
/><#t/>