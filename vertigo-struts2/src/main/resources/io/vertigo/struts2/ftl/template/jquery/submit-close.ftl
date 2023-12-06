<#--
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<#assign escapedOptionId="${parameters.id?string?replace('.', '_')}">
<#include "/${parameters.templateDir}/xhtml/submit-close.ftl" />
<@s.script type='text/javascript'>
jQuery(document).ready(function () { 
	var options_${escapedOptionId} = {};
	<#if parameters.openDialog?if_exists != ""> 
	options_${escapedOptionId}.opendialog = "${parameters.openDialog}";
	</#if>
	<#if parameters.openDialogTitle?if_exists != ""> 
	options_${escapedOptionId}.opendialogtitle = "${parameters.openDialogTitle}";
	</#if>
  <#include "/${parameters.templateDir}/jquery/base.ftl" />
  <#include "/${parameters.templateDir}/jquery/interactive.ftl" />
  <#include "/${parameters.templateDir}/jquery/topics.ftl" />
  <#include "/${parameters.templateDir}/jquery/action.ftl" />
  <#include "/${parameters.templateDir}/jquery/button.ftl" />
  <#include "/${parameters.templateDir}/jquery/validation.ftl" />

  <#include "/${parameters.templateDir}/jquery/jquery-bind.ftl" />
 });  
</@s.script>
