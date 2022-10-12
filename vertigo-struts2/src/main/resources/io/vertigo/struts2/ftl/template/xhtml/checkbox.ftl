<#--
/*
 * $Id: checkbox.ftl,v 1.1 2013/09/23 16:25:43 npiedeloup Exp $
 *
 */
-->
<#assign currentLayout = controlLayout_type?default('none') />
<#if !parameters.labelPosition?? && (parameters.form.labelPosition)??>
<#assign labelPos = parameters.form.labelPosition/>
<#else>
<#assign labelPos = parameters.labelPosition?default("right")/>
</#if>

<#if labelPos == 'left'>
	<#include "/${parameters.templateDir}/${parameters.theme}/controlheader.ftl" />
	<#include "/${parameters.templateDir}/simple/checkbox.ftl" />
	<#include "/${parameters.templateDir}/${parameters.theme}/controlfooter.ftl" />  
<#elseif (labelPos!"") == 'top' && parameters.label??>
	<#if currentLayout == 'table'>
	<tr>
		<#assign tablecolspan = controlLayout_tablecolspan />
	    <th colspan="${parameters.tablecolspan}"><#t/>
		<#include "/${parameters.templateDir}/${parameters.theme}/controllabel.ftl" /> 
	    </th>
	</tr>
	<tr>
	   <td <#t/>
			<#if parameters.inputcolspan??><#t/>
			    colspan="${parameters.inputcolspan}"<#t/>	    
			<#t/></#if>
			<#if parameters.align??><#t/>
			    align="${parameters.align}"<#t/>
			<#t/></#if>
			><#t/>
	        <#include "/${parameters.templateDir}/simple/checkbox.ftl" />
	<#else>
		<#include "/${parameters.templateDir}/${parameters.theme}/controllabel.ftl" />
		<br/>
		<#include "/${parameters.templateDir}/simple/checkbox.ftl" />
	</#if>
	<#include "/${parameters.templateDir}/${parameters.theme}/controlfooter.ftl" />
<#elseif (labelPos!"") == 'right'>
	<#if currentLayout == 'table'>
		<#include "/${parameters.templateDir}/${parameters.theme}/controlheader-trlogic.ftl" />	
		<td class="checkBoxLeft"<#t/>
		<#if parameters.inputcolspan??><#t/>
		    colspan="${parameters.inputcolspan}"<#t/>	    
		</#if><#t/>
		<#if parameters.align??><#t/>
		    align="${parameters.align}"<#t/>
		</#if><#t/>
		><#t/>
		<#include "/${parameters.templateDir}/simple/checkbox.ftl" />
		${parameters.after?if_exists}<#t/>
		</td><#lt/>
		<th class="checkBoxLabelRight" <#t/>
	  <#if parameters.labelcolspan??>
		    colspan="${parameters.labelcolspan}" <#t/>
		</#if>
		><#t/>
		<#if parameters.label?has_content>
		    <#include "/${parameters.templateDir}/${parameters.theme}/controllabel.ftl" />
		</#if> 
		</th><#lt/>
		<#assign columnCount = controlLayout_currentColumnCount + parameters.labelcolspan?default(1) + parameters.inputcolspan?default(1) />	
		${stack.setValue('#controlLayout_currentColumnCount', columnCount)}
		<#include "/${parameters.templateDir}/${parameters.theme}/controlfooter-trlogic.ftl" />
	<#else>
		<#include "/${parameters.templateDir}/simple/checkbox.ftl" />
		<#if parameters.label??> 
		    <#include "/${parameters.templateDir}/${parameters.theme}/controllabel.ftl" />
		</#if>
	</#if>
</#if>