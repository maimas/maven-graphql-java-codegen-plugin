<#-- Template for List type fragments -->
<#--LIST fragment generation {-->
<#list schema.types as type>
    <#if isUserObjectType(type)>
        <#if (type.description??) && type.description!="">
            /**
            * ${type.description}
            */
        </#if>
        public static class List<${type.name}>Fragment {
        private final ResultFragment resultFragment = new ResultFragment();

        <#-- Add method to use a nested fragment -->
        public List<${type.name}>Fragment withFragment(Consumer<${type.name}Fragment> fragmentBuilder) {
            ${type.name}Fragment fragment = new ${type.name}Fragment();
            fragmentBuilder.accept(fragment);
            resultFragment.add(FragmentField.of("", fragment.getFragment()));
            return this;
        }

        public ResultFragment getFragment() {
            return resultFragment;
        }
        }
    </#if>
</#list>
<#--LIST fragment generation }-->
