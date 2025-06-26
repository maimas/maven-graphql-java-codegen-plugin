<#--QUERY & MUTATION generation {-->
<#list schema.types as type>
    <#if typeKindEquals(type,"OBJECT") && (typeNameEquals(type,"QUERY") || typeNameEquals(type,"MUTATION"))>

        ${getTypeDescription(type)}
        public static class ${type.name} {

        <#list type.fields as field>
            /**
            * Generates GraphQL query string to perform "${field.name}" operation.
            *
            * @return - graphql query string.
            */
            public GQLQuery ${field.name}(Consumer< ${getAsFirstCapitalized(field.name)}Args> input,
                                          Consumer< ${getFragmentType(field, "Types.")}> output){

            final ${getAsFirstCapitalized(field.name)}Args args = new ${getAsFirstCapitalized(field.name)}Args();
            input.accept(args);
            final Arguments arguments = args.getArguments();

            final ${getFragmentType(field, "Types.")} fragment = ${createFragmentInstance(field, "Types.")};
            output.accept(fragment);
            final ResultFragment resultFragment = fragment.getFragment();


            GQLFunction function = new GQLFunction(GQLFunctionType.${getTypeName(type)}, "${field.name}")
                    .arguments(arguments)
                    .resultFragment(resultFragment)
                    .returnType(new TypeReference<${getFieldType(field, "Types.")}>() {});

            return GQLQuery.from(function);
            }

            /**
            * Arguments provider.
            *
            * @return - ${getAsFirstCapitalized(field.name)}Args for "${field.name}" operation.
            */
            public class ${getAsFirstCapitalized(field.name)}Args {
            private Arguments arguments = new Arguments();

            ${buildArgumentMethods(field)}

            private Arguments getArguments() {
            return arguments;
            }
            }

        </#list>
        }
    </#if>
</#list>
<#--QUERY & MUTATION generation }-->
