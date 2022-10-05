<#--
/*
 * $Id: form-common.ftl,v 1.1 2014/03/18 11:08:57 npiedeloup Exp $
 */
-->
<#if (parameters.validate!false == false)><#rt/>
    <#if parameters.onsubmit?has_content><#rt/>
        ${tag.addParameter('onsubmit', "${parameters.onsubmit}") }
    </#if>
</#if>
<form<#rt/>
<#if parameters.id?has_content>
 id="${parameters.id}"<#rt/>
</#if>
<#if parameters.name?has_content>
 name="${parameters.name}"<#rt/>
</#if>
<#if parameters.onsubmit?has_content>
 onsubmit="<#outputformat 'JavaScript'>${parameters.onsubmit}</#outputformat>"<#rt/>
</#if>
<#if parameters.onreset?has_content>
 onreset="<#outputformat 'JavaScript'>${parameters.onreset}</#outputformat>"<#rt/>
</#if>
<#if parameters.action?has_content>
 action="${parameters.action}"<#rt/>
</#if>
<#if parameters.target?has_content>
 target="${parameters.target}"<#rt/>
</#if>
<#if parameters.method?has_content>
 method="${parameters.method}"<#rt/>
<#else>
 method="post"<#rt/>
</#if>
<#if parameters.enctype?has_content>
 enctype="${parameters.enctype}"<#rt/>
</#if>
 class="form-inline<#rt/>
<#if parameters.cssClass?has_content>
 ${parameters.cssClass}<#rt/>
</#if>
"<#rt/>
<#if parameters.cssStyle?has_content>
 style="${parameters.cssStyle}"<#rt/>
</#if>
<#if parameters.title?has_content>
 title="${parameters.title}"<#rt/>
</#if>
<#if parameters.acceptcharset?has_content>
 accept-charset="${parameters.acceptcharset}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />