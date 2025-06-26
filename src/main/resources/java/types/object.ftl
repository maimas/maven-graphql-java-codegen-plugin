<#-- Import the base template -->
<#include "../fragments/type_base.ftl">

<#-- Override the renderTypeContent macro -->
<#macro renderTypeContent type>
    public static class ${type.name} {
    <#list type.fields as field>
        public ${getFieldType(field, "")}  ${field.name};
    </#list>
    public static final class Fields {
    <#list type.fields as field>
        public static String ${field.name} = "${field.name}";
    </#list>
    }
    }
</#macro>

<#--OBJECT generation {-->
<#list schema.types as type>
    <#if isUserObjectType(type)>
        <@renderType type/>
    </#if>
</#list>
<#--OBJECT generation }-->
