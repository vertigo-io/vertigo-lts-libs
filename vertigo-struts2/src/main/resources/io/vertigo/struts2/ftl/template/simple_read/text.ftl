<#--
/*
 * $Id: text.ftl,v 1.1 2013/09/23 16:25:43 npiedeloup Exp $
 *
 */
-->
<span<#rt/>
<#if parameters.id??>
 id="${parameters.id}"<#rt/>
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
  <#if parameters.escape??>
    ${parameters.nameValue}<#t/>
  <#else>
    ${parameters.nameValue?replace("\n", "<br/>")?no_esc}<#t/>
  </#if>
</#if>
</span><#t/>
