<#--
/*
 * $Id: checkboxlist.ftl,v 1.2 2014/03/18 10:52:48 npiedeloup Exp $
 */
-->
<#assign itemCount = 0/>
<#if parameters.list??>
<#assign paramListKey = (parameters.listKey != 'top' &&  parameters.listKey != 'key')?then(parameters.listKey, util.getIdField(parameters.list)) />
<#assign paramListValue = (parameters.listValue != 'top')?then(parameters.listValue, util.getDisplayField(parameters.list)) />
<@s.iterator value="parameters.list">
    <#assign itemCount = itemCount + 1/>
    <#if paramListKey??>
        <#assign itemKey = stack.findValue(paramListKey)/>
        <#assign itemKeyStr = stack.findString(paramListKey)/>
    <#else>
        <#assign itemKey = stack.findValue('top')/>
        <#assign itemKeyStr = stack.findString('top')>
    </#if>
    <#if parameters.listLabelKey??>
    <#-- checks the valueStack for the 'valueKey.' The valueKey is then looked-up in the locale 
       file for it's localized value.  This is then used as a label -->
        <#assign itemValue = struts.getText(stack.findString(parameters.listLabelKey))/>
    <#elseif paramListValue??>
    	<#assign itemValue = stack.findString(paramListValue)!''/>
    <#else>
        <#assign itemValue = stack.findString('top')/>
    </#if>
    <#if parameters.listCssClass?has_content>
        <#assign itemCssClass= stack.findString(parameters.listCssClass)!''/>
    </#if>
    <#if parameters.listCssStyle?has_content>
        <#assign itemCssStyle= stack.findString(parameters.listCssStyle)!''/>
    </#if>
    <#if parameters.listTitle?has_content>
        <#assign itemTitle= stack.findString(parameters.listTitle)!''/>
    </#if>
<#assign previousCssClass = appendedCssClass!''/>
<#assign appendedCssClass = previousCssClass +' checkboxLabel'/>
<label<#rt/> 
    <#if parameters.id?has_content>
        for="${parameters.id}-${itemCount}"<#rt/>
    <#else>
        for="${parameters.name}-${itemCount}"<#rt/>
    </#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/css.ftl"/>>

<#assign appendedCssClass = previousCssClass/>
<input type="checkbox" name="${parameters.name}" value="${itemKeyStr}"<#rt/>
    <#if parameters.id?has_content>
 id="${parameters.id}-${itemCount}"<#rt/>
    <#else>
 id="${parameters.name}-${itemCount}"<#rt/>
    </#if>
    <#if tag.contains(parameters.nameValue, itemKey)>
 checked="checked"<#rt/>
    </#if>
    <#if parameters.disabled!false>
 disabled="disabled"<#rt/>
    </#if>
    <#if itemCssClass?has_content>
 class="${itemCssClass}"<#rt/>   <#-- parameter.cssClass, and other are included by css.ftl -->
    </#if>
    <#if itemCssStyle?has_content>
 style="${itemCssStyle}"<#rt/>
    </#if>
    <#if itemTitle?has_content>
 title="${itemTitle}"<#rt/>
    <#elseif parameters.title?has_content>
 title="${parameters.title}"<#rt/>
    </#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/css.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#global evaluate_dynamic_attributes = true/>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
/><#rt/>
${itemValue}</label><br/>
</@s.iterator>
    <#else>
</#if>
<input type="hidden" id="__multiselect_${parameters.id}" name="__multiselect_${parameters.name}"<#rt/>
 value=""<#rt/>
<#if parameters.disabled!false>
 disabled="disabled"<#rt/>
</#if>
/><#t/>