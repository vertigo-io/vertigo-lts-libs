<#--
/*
 * $Id: fielderror.ftl,v 1.2 2014/01/15 15:32:30 npiedeloup Exp $
 *
 */
-->
<#if fieldErrors??>
    <#assign eKeys = fieldErrors.keySet()>
    <#assign eKeysSize = eKeys.size()>
    <#assign doneStartUlTag=false>
    <#assign doneEndUlTag=false>
    <#assign haveMatchedErrorField=false>
    
    <#if (fieldErrorFieldNames?size > 0 || eKeysSize>0) >
    <script>
    	function focusElementByName(eltName) {
    		var elt = document.getElementsByName(eltName)[0];
    		if (elt) { 
    			if (elt.type == 'hidden') {
    				var widgetId = elt.id + '_widget';
    				elt = document.getElementById(widgetId);
    			}
    			elt.focus();
    		}
    	}
    </script>
    </#if>
    
    <#if (fieldErrorFieldNames?size > 0) >
        <#list fieldErrorFieldNames as fieldErrorFieldName>
            <#list eKeys as eKey>
                <#if (eKey = fieldErrorFieldName)>
                    <#assign haveMatchedErrorField=true>
                    <#assign eValue = fieldErrors.get(fieldErrorFieldName)>
                    <#if (haveMatchedErrorField && (!doneStartUlTag))>
	<ul <#rt/>
                        <#if parameters.id?if_exists != "">
                                id="${parameters.id}" <#t/>
                        </#if>
                        class="${(parameters.cssClass!'errorMessage')}" <#t/>
                        <#if parameters.cssStyle??>
                                style="${parameters.cssStyle}" <#t/>
                        </#if>
	><#lt/>
                    <#assign doneStartUlTag=true>
                    </#if>
                    <#list eValue as eEachValue>
		<li><#t/>
			<span class="messageLabel" onclick="focusElementByName('${eKey}');">${util.label(eKey)}: </span><#t/>
	        <span class="message"><#if parameters.escape>${eEachValue!}<#else>${eEachValue!?no_esc}</#if></span><#t/>
	    </li><#rt/>
                    </#list>
                </#if>
            </#list>
        </#list>
        <#if (haveMatchedErrorField && (!doneEndUlTag))>
	</ul>
        <#assign doneEndUlTag=true>
        </#if>
    <#elseif (eKeysSize > 0)>
	<ul <#rt/>
            <#if parameters.cssClass??>
                    class="${parameters.cssClass}" <#t/>
                <#else>
                    class="errorMessage" <#t/>
            </#if>
            <#if parameters.cssStyle??>
                    style="${parameters.cssStyle}" <#t/>
            </#if>
                ><#lt/>
            <#list eKeys as eKey>
                <#assign eValue = fieldErrors.get(eKey)>
                <#list eValue as eEachValue>
		<li><#t/>
        	<span class="messageLabel" onclick="focusElementByName('${eKey}');">${util.label(eKey)}: </span><#t/>
        	<span class="message"><#if parameters.escape>${eEachValue!}<#else>${eEachValue!?no_esc}</#if></span><#t/>
      	</li><#rt/>
            </#list>
		</#list>
	</ul>
    </#if>
</#if>
