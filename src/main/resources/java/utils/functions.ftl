<#--Template funtions-->
<#function getAsFirstCapitalized value>
    <#if (value??)>
        <#return value?cap_first>
    <#else>
        <#return "">
    </#if>
</#function>


<#function buildArgumentMethods field>
    <#local result="">
    <#if (field.args??)>
        <#assign argsSize = field.args?size>
        <#list field.args as arg>
            <#if (arg.name??)>
                <#if isArgOptional(arg)>
                    <#local result += "public "+getAsFirstCapitalized(field.name)+"Args "+arg.name+"(Optional<"+getFieldType(arg, "Types.")+"> "+arg.name+") {
                    arguments.add(Argument.of(\""+arg.name+"\", "+arg.name+"));
                    return this;
                    }">
                <#else>
                    <#local result += "public "+getAsFirstCapitalized(field.name)+"Args "+arg.name+"("+getFieldType(arg, "Types.")+" "+arg.name+") {
                    arguments.add(Argument.of(\""+arg.name+"\", "+arg.name+"));
                    return this;
                    }">
                </#if>
            </#if>
        </#list>
    </#if>
    <#return result>
</#function>

<#function getFieldType field typePrefix>
    <#return resolveType(field.type, typePrefix)>
</#function>

<#-- Improved function to handle complex nested types with scalar mappings (overridable via templateConfig) -->
<#function resolveType type typePrefix>
    <#assign typeName = "">
    <#assign typeKind = "">

    <#if (type.kind??)>
        <#assign typeKind = type.kind>
    </#if>

    <#-- Handle NON_NULL types -->
    <#if typeKind == "NON_NULL" && type.ofType??>
        <#return resolveType(type.ofType, typePrefix)>
    </#if>

    <#-- Handle LIST types -->
    <#if typeKind == "LIST" && type.ofType??>
        <#assign innerType = resolveType(type.ofType, typePrefix)>
        <#return "List<" + innerType + ">">
    </#if>

    <#-- Handle named types -->
    <#if (type.name??)>
        <#assign typeName = type.name>
        <#-- Prefer template-provided scalar mappings if available -->
        <#assign scalarMappings = (templateConfig.properties.scalarMappings)!{}>
        <#assign mapped = scalarMappings[typeName]?if_exists>
        <#if !(mapped??)><#assign mapped = scalarMappings[typeName?lower_case]?if_exists></#if>
        <#if !(mapped??)><#assign mapped = scalarMappings[typeName?upper_case]?if_exists></#if>
        <#if mapped??>
            <#return mapped>
        </#if>
        <#-- Fallback defaults -->
        <#if typeName?lower_case == "id">
            <#return "String">
        <#elseif typeName?lower_case == "int">
            <#return "Integer">
        <#elseif typeName?lower_case?matches("boolean|string|date|float")>
            <#return typeName>
        </#if>
        <#return typePrefix + typeName>
    </#if>

    <#-- Handle ofType as fallback -->
    <#if (type.ofType??) && (type.ofType.name??)>
        <#assign typeName = type.ofType.name>
        <#assign scalarMappings = (templateConfig.properties.scalarMappings)!{}>
        <#assign mapped = scalarMappings[typeName]?if_exists>
        <#if !(mapped??)><#assign mapped = scalarMappings[typeName?lower_case]?if_exists></#if>
        <#if !(mapped??)><#assign mapped = scalarMappings[typeName?upper_case]?if_exists></#if>
        <#if mapped??>
            <#return mapped>
        </#if>
        <#if typeName?lower_case == "id">
            <#return "String">
        <#elseif typeName?lower_case == "int">
            <#return "Integer">
        <#elseif typeName?lower_case?matches("boolean|string|date|float")>
            <#return typeName>
        </#if>
        <#return typePrefix + typeName>
    </#if>

    <#-- Default fallback -->
    <#return "Object">
</#function>

<#function typeKindEquals type value>
    <#if (type.kind??)>
        <#return type.kind?upper_case?matches(value?upper_case)>
    <#else>
        <#return false>
    </#if>
</#function>

<#function typeNameEquals type value>
    <#if (type.name??)>
        <#return type.name?upper_case?matches(value?upper_case)>
    <#else>
        <#return false>
    </#if>
</#function>

<#function getTypeName type>
    <#if (type.name??)>
        <#return type.name?capitalize>
    <#else>
        <#return "">
    </#if>
</#function>

<#function buildMethodArguments field>
    <#local result="">
    <#if (field.args??)>
        <#assign argsSize = field.args?size>
        <#list field.args as arg>
            <#if (arg.name??)>
                <#if isArgOptional(arg)>
                    <#local result += "Optional<" + getFieldType(arg, "Types.") + "> " + arg.name>
                <#else>
                    <#local result += getFieldType(arg, "Types.") + " " + arg.name>
                </#if>
                <#if arg?index < argsSize-1>
                    <#local result +=", ">
                </#if>
            </#if>
        </#list>
    </#if>
    <#return result>
</#function>

<#function buildFragmentArguments field>
    <#local result="">
    <#if (field.args??)>
        <#assign argsSize = field.args?size>
        <#list field.args as arg>
            <#if (arg.name??)>
                <#local result += "Argument.of(\"${arg.name}\", ${arg.name})">
                <#if arg?index < argsSize-1>
                    <#local result +=", ">
                </#if>
            </#if>
        </#list>
    </#if>
    <#return result>
</#function>

<#function isArgOptional arg>
    <#if (arg.type.kind??) && arg.type.kind == "NON_NULL">
        <#return false>
    </#if>
    <#return true>
</#function>

<#function getTypeDescription type>
    <#assign description="">
    <#if (type.description??) && type.description!="">
        <#assign description="
        /**
        * ${type.description}
        */">
    </#if>
    <#return description>
</#function>

<#function isUserObjectType type>
    <#if type.name!="" && !type.name?starts_with("__") && typeKindEquals(type,"OBJECT") && !type.name?upper_case?matches("QUERY|MUTATION")>
        <#return true>
    </#if>
    <#return false>
</#function>

<#function isScalarType type typeName>
    <#if type.name!="" && !type.name?starts_with("__") && typeKindEquals(type,"SCALAR") && type.name?upper_case?matches(typeName?upper_case)>
        <#return true>
    </#if>
    <#return false>
</#function>

<#-- Function to get the appropriate fragment type for a field -->
<#function getFragmentType field typePrefix>
    <#-- For List types, we still use the per-item fragment type -->
    <#if field.type.kind == "LIST" && field.type.ofType??>
        <#local innerType = resolveType(field.type.ofType, typePrefix)>
        <#return innerType + "Fragment">
    <#else>
        <#return getFieldType(field, typePrefix) + "Fragment">
    </#if>
</#function>

<#-- Function to create a new fragment instance for a field -->
<#function createFragmentInstance field typePrefix>
    <#-- For List types, create the inner fragment instance (per-item selection set) -->
    <#if field.type.kind == "LIST" && field.type.ofType??>
        <#local innerType = resolveType(field.type.ofType, typePrefix)>
        <#return "new " + innerType + "Fragment()">
    <#else>
        <#return "new " + getFieldType(field, typePrefix) + "Fragment()">
    </#if>
</#function>
