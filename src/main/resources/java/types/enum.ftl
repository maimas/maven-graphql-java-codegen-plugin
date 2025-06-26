<#-- Import the base template -->
<#include "../fragments/type_base.ftl">

<#-- Override the renderTypeContent macro -->
<#macro renderTypeContent type>
    public enum ${type.name} {
    <#list type.enumValues as enum>
        ${enum.name}<#sep>,</#sep>
    </#list>
    }
</#macro>

<#--ENUM generation {-->
<#list schema.types as type>
    <#if type.name!="" && !type.name?starts_with("__") && typeKindEquals(type,"ENUM")>
        <@renderType type/>
    </#if>
</#list>
<#--ENUM generation }-->
