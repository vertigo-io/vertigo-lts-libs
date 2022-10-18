<#--
/*
 * Text simple pour l'autocomplete.
 */
-->
<#assign uiList = stack.findValue(parameters.remoteList) />
<#if parameters.nameValue?? && parameters.nameValue!=''>
	<#assign uiObject = uiList.getById(parameters.remoteListKey, parameters.nameValue) />
</#if>
<input<#rt/>
 type="${parameters.type?default("text")}"<#rt/>
<#-- name="${parameters.name?default("")}"<#rt/> pas de nom car il ne correspond a aucun champs -->
<#if parameters.get("size")??>
 size="${parameters.get("size")}"<#rt/>
</#if>
<#if parameters.maxlength??>
 maxlength="${parameters.maxlength}"<#rt/>
</#if>
<#if uiObject??>
<#-- replace \ en - : doit correspondre au formatage du javascript jQuery.ui.autocomplete.prototype._renderItem -->
 value="${uiObject.get(parameters.remoteListValue)?replace('\n', ' - ')}"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.readonly?default(false)>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex??>
 tabindex="${parameters.tabindex}"<#rt/>
</#if>
<#if parameters.id??>
 id="${parameters.id}"<#rt/>
</#if>
<#assign previousCssClass = appendedCssClass!''/>
<#assign appendedCssClass = previousCssClass +' autocompleter-input'/>
<#include "/${parameters.templateDir}/simple/css.ftl" />
<#assign appendedCssClass = previousCssClass/>
<#if parameters.title??>
 title="${parameters.title}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" />
/>