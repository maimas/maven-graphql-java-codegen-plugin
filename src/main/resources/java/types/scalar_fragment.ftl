<#--SCALAR fragment generation {-->
<#list schema.types as type>
    <#if isScalarType(type, "Boolean")>
        /**
         * Fragment for Boolean scalar type
         */
        public static class BooleanFragment {
            private final ResultFragment resultFragment = new ResultFragment();

            public ResultFragment getFragment() {
                return resultFragment;
            }
        }
    </#if>
</#list>
<#--SCALAR fragment generation }-->