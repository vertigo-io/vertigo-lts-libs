<#--
/*
 * $Id: actionmessage-default.ftl,v 1.2 2014/01/15 15:32:30 npiedeloup Exp $
 *
 */
-->
<#if (actionMessages?? && actionMessages?size > 0 && !parameters.isEmptyList)>
<#assign panelRendered = false/>
<#list actionMessages as message>
	<#if message?if_exists != "" && message?starts_with('WARNING:')>
		<#if !panelRendered>
<div class="warningPanel">
	<ul <#rt/>
			<#if parameters.id?if_exists != "">
			 id="${parameters.id}" <#t/>
			</#if>
			<#if parameters.cssClass??>
			 class="${parameters.cssClass}" <#t/>
			<#else>
			 class="actionMessage" <#t/>
			</#if>
			<#if parameters.cssStyle??>
			 style="${parameters.cssStyle}"<#t/>
			</#if>
	><#lt/>
		<#assign panelRendered = true/>
		</#if>
		<#assign warnMessage = message!?substring('WARNING:'?length)>
		<#assign warnFieldMessage = warnMessage?matches("<label>(.+)</label>(.+)$")>
		<#if warnFieldMessage>
			<#list warnFieldMessage as m> 
		<li><span class="messageLabel">${m?groups.get(1)}: </span><span class="message"><#if parameters.escape>${m?groups.get(2)}<#else>${m?groups.get(2)!}</#if></span></li>
	        	</#list> 
		<#else>
		<li><span><#if parameters.escape>${warnMessage}<#else>${warnMessage!}</#if></span></li>
		</#if>				 
	</#if>
</#list>
<#if panelRendered>
	</ul>
</div>
</#if>
<#t/>
<#assign panelRendered = false/>
<#list actionMessages as message>
	<#if message?if_exists != "" && message?starts_with('INFO:')>
		<#if !panelRendered>
<div class="infoPanel">
	<ul <#rt/>
			<#if parameters.id?if_exists != "">
			 id="${parameters.id}" <#t/>
			</#if>
			<#if parameters.cssClass??>
			 class="${parameters.cssClass}" <#t/>
			<#else>
			 class="actionMessage" <#t/>
			</#if>
			<#if parameters.cssStyle??>
			 style="${parameters.cssStyle}"<#t/>
			</#if>
	><#lt/>
		<#assign panelRendered = true/>
		</#if>
		<#assign infoMessage = message!?substring('INFO:'?length)>
		<#assign infoFieldMessage = infoMessage?matches("<label>(.+)</label>(.+)$")>
		<#if infoFieldMessage>
			<#list infoFieldMessage as m> 
		<li><span class="messageLabel">${m?groups.get(1)}: </span><span class="message"><#if parameters.escape>${m?groups.get(2)}<#else>${m?groups.get(2)!}</#if></span></li>
	        	</#list> 
		<#else>
		<li><span><#if parameters.escape>${infoMessage}<#else>${infoMessage!}</#if></span></li>
		</#if>				 
	</#if>
</#list>
<#if panelRendered>
	</ul>
</div>
</#if>
<#t/>
<#assign panelRendered = false/>
<#list actionMessages as message>
	<#if message?if_exists != "" && !message?starts_with('INFO:') && !message?starts_with('WARNING:') && !message?starts_with('ERROR:')>
		<#if !panelRendered>
<div class="errorPanel">
	<ul <#rt/>
			<#if parameters.id?if_exists != "">
			 id="${parameters.id}" <#t/>
			</#if>
			<#if parameters.cssClass??>
			 class="${parameters.cssClass}" <#t/>
			<#else>
			 class="actionMessage" <#t/>
			</#if>
			<#if parameters.cssStyle??>
			 style="${parameters.cssStyle}"<#t/>
			</#if>
	><#lt/>
			<#assign panelRendered = true/>
		</#if>
		<li><span><#if parameters.escape>${message}<#else>${message!}</#if></span></li>
	</#if>
</#list>
<#if panelRendered>
	</ul>
</div>
</#if>
<#t/>
<#t/>
</#if>