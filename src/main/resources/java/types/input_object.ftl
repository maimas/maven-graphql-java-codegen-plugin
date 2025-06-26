<#-- Import the base template -->
<#include "../fragments/type_base.ftl">

<#-- Override the renderTypeContent macro -->
<#macro renderTypeContent type>
    public static class ${type.name} {
    <#list type.inputFields as field>
        public ${getFieldType(field, "")}  ${field.name};
    </#list>
    }
</#macro>

<#--INPUT_OBJECT generation {-->
<#list schema.types as type>
    <#if type.name!="" && !type.name?starts_with("__") && typeKindEquals(type,"INPUT_OBJECT") && !type.name?upper_case?matches("QUERY|MUTATION")>
        <@renderType type/>
    </#if>
</#list>
<#--INPUT_OBJECT generation }-->
