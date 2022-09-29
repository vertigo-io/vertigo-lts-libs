<#--
/*
 * $Id: controllabel.ftl,v 1.1 2013/09/23 16:25:43 npiedeloup Exp $
 * Label pour les controls.
 */
-->
<label<#t/>
<#if parameters.id??>
 for="${parameters.id}"<#rt/>
</#if>
 class="readonly"<#rt/>
><#t/>
<#if parameters.label = "default">
	<#assign fieldName = parameters.widgetname!parameters.name!""/> <#-- for jquery component -->	
	${util.label(fieldName)}<#t/>
<#else>
	${parameters.label}<#t/>
</#if>
<#if parameters.dynamicAttributes.get('forceTooltip')?? && parameters.dynamicAttributes.get('forceTooltip') = 'true' > 
<#include "/${parameters.templateDir}/xhtml/tooltip.ftl" />
</#if>
</label><#t/>
