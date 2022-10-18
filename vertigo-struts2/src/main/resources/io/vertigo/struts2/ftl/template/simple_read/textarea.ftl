<#--
/*
 * $Id: textarea.ftl,v 1.2 2014/01/17 09:33:28 npiedeloup Exp $
 *
 */
-->
<textarea<#rt/>
<#if parameters.id??>
 id="${parameters.id}"<#rt/>
</#if>
 readonly="readonly"<#rt/>
 disabled="disabled"<#rt/>
 cols="${parameters.cols?default("")}"<#rt/>
 rows="${parameters.rows?default("")}"<#rt/>
<#if parameters.wrap??>
 wrap="${parameters.wrap}"<#rt/>
</#if>
<#if parameters.tabindex??>
 tabindex="${parameters.tabindex}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/css.ftl" />
<#if parameters.title??>
 title="${parameters.title}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" />
><#t/>
<#if parameters.nameValue??>
<@s.property value="parameters.nameValue"/><#t/>
</#if>
</textarea><#t/>