<#--
/*
 * $Id: radiomap.ftl,v 1.1 2013/09/23 16:25:43 npiedeloup Exp $
 */
-->

<span<#t/>
<#if parameters.id??>
 id="${parameters.id}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/css.ftl" />
<#if parameters.title??>
 title="${parameters.title}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/css.ftl" />
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" />
><#t/>
<#if parameters.headerKey?? && parameters.headerValue?? && tag.contains(parameters.nameValue, parameters.headerKey) == true>
 ${parameters.headerValue}<#t/>
<#else>
<#if parameters.nameValue?? && parameters.nameValue!='' >
<#if parameters.list.getById??>
<#assign paramListKey = (parameters.listKey != 'top' && parameters.listKey != 'key')?then(parameters.listKey, util.getIdField(parameters.list)) />
<#assign paramListValue = (parameters.listValue != 'top' && parameters.listValue != 'value')?then(parameters.listValue, util.getDisplayField(parameters.list)) />
<#assign uiObject = parameters.list.getById(paramListKey, parameters.nameValue) />
<#if uiObject??>
 ${uiObject.get(paramListValue)?replace("\n", "<br/>")}<#t/>
</#if>
<#else><#-- si pas de getById : liste ou map brute -->
<#list parameters.list as entry>
<#if entry.key = parameters.nameValue>
 ${entry.value?replace("\n", "<br/>")}<#t/>
</#if>
</#list>
</#if>
</#if>
</#if>
</span><#t/>