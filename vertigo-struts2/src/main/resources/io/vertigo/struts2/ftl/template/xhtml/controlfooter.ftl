${parameters.after!}<#t/>
<#if (parameters.unit)??><#t/>
	<span>${parameters.unit?html}</span>
</#if>
<#if parameters.dynamicAttributes.get('tooltipPosition')?? && parameters.dynamicAttributes.get('tooltipPosition') = 'field'>
	<#include "/${parameters.templateDir}/xhtml/tooltip.ftl" /> 
</#if>
<#assign currentLayout = controlLayout_type?default('none') />
<#if currentLayout = 'table'>
	</td><#lt/>
	<#-- Write out the closing td for the html input -->
<#include "/${parameters.templateDir}/xhtml/controlfooter-trlogic.ftl" />
</#if>