<#-- Base template for type generation -->
<#macro renderType type>
    <#if (type.description??) && type.description!="">
        /**
        * ${type.description}
        */
    </#if>
    <@renderTypeContent type/>
</#macro>

<#-- This macro should be overridden by specific type templates -->
<#macro renderTypeContent type>
    <#-- Default implementation, should be overridden -->
</#macro>