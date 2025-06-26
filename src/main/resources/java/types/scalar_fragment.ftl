<#--SCALAR fragment generation {-->
<#-- Always generate BooleanFragment regardless of schema -->
/**
 * Fragment for Boolean scalar type
 */
public static class BooleanFragment {
    private final ResultFragment resultFragment = new ResultFragment();

    public ResultFragment getFragment() {
        return resultFragment;
    }
}

<#-- Generate other scalar fragments based on schema -->
<#list schema.types as type>
    <#if isScalarType(type, "String")>
        /**
         * Fragment for String scalar type
         */
        public static class StringFragment {
            private final ResultFragment resultFragment = new ResultFragment();

            public ResultFragment getFragment() {
                return resultFragment;
            }
        }
    </#if>
    <#if isScalarType(type, "Int")>
        /**
         * Fragment for Int scalar type
         */
        public static class IntFragment {
            private final ResultFragment resultFragment = new ResultFragment();

            public ResultFragment getFragment() {
                return resultFragment;
            }
        }
    </#if>
    <#if isScalarType(type, "Float")>
        /**
         * Fragment for Float scalar type
         */
        public static class FloatFragment {
            private final ResultFragment resultFragment = new ResultFragment();

            public ResultFragment getFragment() {
                return resultFragment;
            }
        }
    </#if>
    <#if isScalarType(type, "ID")>
        /**
         * Fragment for ID scalar type
         */
        public static class IDFragment {
            private final ResultFragment resultFragment = new ResultFragment();

            public ResultFragment getFragment() {
                return resultFragment;
            }
        }
    </#if>
</#list>
<#--SCALAR fragment generation }-->
