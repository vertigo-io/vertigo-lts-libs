<#--
/*
 * $Id: controllabel.ftl,v 1.1 2013/09/23 16:25:43 npiedeloup Exp $
 * Label pour les controls.
 */
-->
<#assign fieldName = parameters.widgetname!parameters.name!""/><#-- for jquery component -->
<#assign hasFieldErrors = parameters.name?? && fieldErrors?? && fieldErrors[fieldName]??/>
<label<#t/>
<#if parameters.id??>
 for="${parameters.id?html}"<#rt/>
</#if>
<#if hasFieldErrors>
 class="errorLabel"<#rt/>
</#if>
><#t/>
<#if parameters.label = "default">    
	${util.label(fieldName)?html}<#t/>
<#else>
	${parameters.label?html}<#t/>
</#if>
<#if parameters.required!false>
 		<em class="required">*</em><#t/>
<#else>
	<#-- No required mark if : not required field or if label invisible (trim return an empty string) --> 
	<#if (parameters.label?trim?length > 0) && (!(parameters.nameValue?? && parameters.nameValue?is_boolean) && util.required(fieldName))>
		<em class="required">*</em><#t/>
	</#if>
</#if>
<#assign tooltipPosition = parameters.dynamicAttributes['tooltipPosition']!'label' />
<#if tooltipPosition = 'label'>
<#include "/${parameters.templateDir}/${parameters.theme}/tooltip.ftl" /> 
</#if>
</label><#t/>