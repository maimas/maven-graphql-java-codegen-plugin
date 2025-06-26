<#--OBJECT fragment generation {-->
<#list schema.types as type>
    <#if isUserObjectType(type)>
        <#if (type.description??) && type.description!="">
            /**
            * ${type.description}
            */
        </#if>
        public static class ${type.name}Fragment {
        private final ResultFragment resultFragment = new ResultFragment();

        <#list type.fields as field>
            public ${type.name}Fragment ${field.name}() {
            resultFragment.add(FragmentField.of("${field.name}"));
            return this;
            }
        </#list>

        public ResultFragment getFragment() {
        return resultFragment;
        }
        }
    </#if>
</#list>
<#--OBJECT fragment generation }-->