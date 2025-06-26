<#--LIST type fragments generation {-->
<#list schema.types as type>
    <#if isUserObjectType(type)>
        <#if (type.description??) && type.description!="">
            /**
            * List fragment for ${type.description}
            */
        </#if>
        public static class List${type.name}Fragment {
            private final ResultFragment resultFragment = new ResultFragment();

            public List${type.name}Fragment withFragment(Consumer<${type.name}Fragment> fragmentBuilder) {
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
<#--LIST type fragments generation }-->
