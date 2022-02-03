<#--
/*
 * See Struts 2.5.28.3
 * Merge 18/01/2022 Npi2loup
 */
-->
<#assign hasFieldErrors = parameters.name?? && fieldErrors?? && fieldErrors.get(parameters.widgetname!parameters.name)??/>
<#if parameters.cssClass?has_content || hasFieldErrors || (appendedCssClass?has_content) >
 class="<#rt/>
</#if>
<#if parameters.cssClass?has_content>
 ${parameters.cssClass?html}<#rt/>
</#if>
<#if hasFieldErrors >
 ${parameters.cssErrorClass!' error'?html}<#rt/>
</#if>
<#if (appendedCssClass?has_content)>
 ${appendedCssClass?trim?html}<#rt/>
</#if>
<#if parameters.cssClass?has_content || hasFieldErrors || (appendedCssClass?has_content) >
"<#rt/>
</#if>
<#if parameters.cssStyle?has_content && !(hasFieldErrors && (parameters.cssErrorStyle?has_content || parameters.cssErrorClass?has_content))>
 style="${parameters.cssStyle?html}"<#rt/>
<#elseif hasFieldErrors && parameters.cssErrorStyle?has_content>
 style="${parameters.cssErrorStyle?html}"<#rt/>
</#if>
