<#--
/*
 * $Id: select.ftl,v 1.2 2014/01/17 09:53:30 npiedeloup Exp $
 *
 */
-->
<#setting number_format="#.#####">
<select<#rt/>
 name="${(parameters.name!"")}"<#rt/>
<#if parameters.get("size")?has_content>
 size="${parameters.get("size")}"<#rt/>
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
<#if parameters.multiple!false>
 multiple="multiple"<#rt/>
</#if>
<#include "/${parameters.templateDir}/${parameters.expandTheme}/scripting-events.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/common-attributes.ftl" />
<#include "/${parameters.templateDir}/${parameters.expandTheme}/dynamic-attributes.ftl" />
>
<#if parameters.headerKey?? && parameters.headerValue??>
    <option value="${parameters.headerKey}"<#rt/>
    <#if tag.contains(parameters.nameValue, parameters.headerKey) == true>
 selected="selected"<#rt/>
    </#if>
    >${parameters.headerValue}</option><#lt/>
</#if>
<#if parameters.emptyOption!false>
    <option value=""></option>
</#if>
<#if parameters.list.getById??>
	<#assign paramListKey = (parameters.listKey != 'top' && parameters.listKey != 'key')?then(parameters.listKey, util.getIdField(parameters.list)) />
	<#assign paramListValue = (parameters.listValue != 'top' && parameters.listValue != 'value')?then(parameters.listValue, util.getDisplayField(parameters.list)) />
<#else>
	<#assign paramListKey = parameters.listKey />
	<#assign paramListValue = parameters.listValue />
</#if>
<@s.iterator value="parameters.list">
        <#if paramListKey??>
            <#assign itemKey = stack.findValue(paramListKey)!''/>
            <#assign itemKeyStr = stack.findString(paramListKey)!''/>
        <#else>
            <#assign itemKey = stack.findValue('top')/>
            <#assign itemKeyStr = stack.findString('top')>
        </#if>
        <#if parameters.listValueKey??>
          <#-- checks the valueStack for the 'valueKey.' The valueKey is then looked-up in the locale file for it's 
             localized value.  This is then used as a label -->
          <#assign valueKey = stack.findString(parameters.listValueKey!'') />
          <#if valueKey?has_content>
              <#assign itemValue = struts.getText(valueKey) />
          <#else>
              <#assign itemValue = parameters.listValueKey />
          </#if>
        <#elseif paramListValue??>
            <#assign itemValue = stack.findString(paramListValue)/>
        <#else>
            <#assign itemValue = stack.findString('top')/>
        </#if>
        <#if parameters.listCssClass??>
            <#if stack.findString(parameters.listCssClass)??>
              <#assign itemCssClass= stack.findString(parameters.listCssClass)/>
            <#else>
              <#assign itemCssClass = ''/>
            </#if>
        </#if>
        <#if parameters.listCssStyle??>
            <#if stack.findString(parameters.listCssStyle)??>
              <#assign itemCssStyle= stack.findString(parameters.listCssStyle)/>
            <#else>
              <#assign itemCssStyle = ''/>
            </#if>
        </#if>
        <#if parameters.listTitle??>
            <#if stack.findString(parameters.listTitle)??>
              <#assign itemTitle= stack.findString(parameters.listTitle)/>
            <#else>
              <#assign itemTitle = ''/>
            </#if>
        </#if>
    <option value="${itemKeyStr}"<#rt/>
        <#if tag.contains(parameters.nameValue, itemKey) == true>
 selected="selected"<#rt/>
        </#if>
        <#if itemCssClass?has_content>
 class="${itemCssClass}"<#rt/>
        </#if>
        <#if itemCssStyle?has_content>
 style="${itemCssStyle}"<#rt/>
        </#if>
        <#if itemTitle?has_content>
 title="${itemTitle}"<#rt/>
        </#if>
    >${itemValue}</option><#lt/>
</@s.iterator>

<#include "/${parameters.templateDir}/${parameters.expandTheme}/optgroup.ftl" />

</select><#t/>
<#if parameters.multiple!false>
<input type="hidden" id="__multiselect_${parameters.id}" name="__multiselect_${parameters.name}" value=""<#rt/>
<#if parameters.disabled!false>
 disabled="disabled"<#rt/>
</#if>
/><#t/>
</#if>