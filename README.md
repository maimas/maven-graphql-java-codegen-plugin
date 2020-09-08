
# GraphQL Java code generator Plugin
This is a maven plugin that generates TYPE SAFE java code from a remote GraphQL API,
so that you can have all the API methods and types in one generated source and easily build and execute query/mutations to the GQL server. 

It generates:
 - Types - object models including fields constants
 - InputObjects - graphql input objects schema
 - Queries - functions that takes arguments and output fields to build the GQL query or mutation. 

### Why this plugin exists?
Let's assume you have a java service that needs to call a remote GQL API to create a user `create(user: UserInput!): User`.
In order to create the user by calling GQL API, you need to know 3 things:
 1. `UserInput` object schema.
 2. `Output Field names` to be included in the result
 2. `Output object schema` - if you want to deserialize HTTP response body in to an User object.
 - This is how this mutation query string would look like:
 ````
query: {
    mutation create ($userInput: UserInput!) {
      create(user: $userInput) {
        id
        firstName
        lastName
        enabled
        status
      }
    }
}

varaibles : {
  "userInput": {
    "email": "tet2.test@test.com",
    "password": "1234", 
  }
}
````
<img alt="UI" src="https://github.com/maimas/maven-graphql-java-codegen-plugin/blob/master/images/Cdegen_ui_gql.PNG?raw=true">

- This is how you can build it with API generated by the plugin:
````
import static com.maimas.generated.UserServiceGraphQLProvider.*;
.......
Types.UserInput userInput = new Types.UserInput();
        userInput.email = "tet11.test@test.com";
        userInput.password = "1234";

GQLQuery updateQuery = new Mutation().create(
                input
                    .user(userInput),
                output -> output
                    .id()
                    .lastName()
                    .firstName()
                    .enablede()
                    .status());
````
<img alt="IDE" src="https://github.com/maimas/maven-graphql-java-codegen-plugin/blob/master/images/Codegen_ide.png?raw=true">

As you noticed,  ``UserServiceGraphQLProvider`` class, is generated by the plugin and contains all the types ``UserInput``,``GQLQuery``,``UserFragment``...that the schema from remote GQL API supports. 

``GQLQuery`` is a builder for our GQL mutation, it needs a method that we want to execute, parameters of the method, and output fields that we want to return in the result set. 

## How to use.

### Prerequisites
- Java > 1.8
- Maven

In your pom.xml under the ``build>plugins`` section add codegen plugin: 
````
    <build>
        <plugins>
        ....
            <plugin>
                <groupId>com.maimas</groupId>
                <artifactId>graphql-codegen</artifactId>
                <version>2.0</version>
                <configuration>
                    <servers>
                        <server>
                            <!--from where to fetch schema conf-->
                            <url>http://localhost:8080/graphql</url>
                            <headers>
                                <header>jwt:DAAAgadwqcEJTUIU54WFFYUJgsd</header>
                                <header>Content-Type:application/json;charset=UTF-8</header>
                            </headers>
                            <!--where to store generated code-->
                            <resultClassName>UserServiceGraphQLProvider</resultClassName>
                            <resultClassPackage>com.maimas.generated</resultClassPackage>
                            <dir>./src/test/java/com/maimas/generated</dir>
                        </server>
                    </servers>
                </configuration>
            </plugin>
          ....
        </plugins>
    </build>
````

> Configuration

| Tag |Required| Description |
| --- | --- | --- |
| `url`                | required | URL of the server that exposes GraphQL API that we want to generate the schema for. |
| `headers`            | optional | Http headers to be added in the http request while fetching the GQL API JSON schema. Useful in specially when the API is secured and an access token needs to be injected in the request headers.  |
| `resultClassName`    | optional | Name of the class name to be generated, if not provided default value will be used `GeneratedGraphqlAPI`  |
| `resultClassPackage` | optional | Package of the generated class name, if not provided default value will be used `com.maimas.graphql.generated`  |
| `dir`                | optional | Directory where to store the generated class, if not provided default value will be used `./generated`  |

> Generate API
>
Execute plugin from your IDE, or run in your terminal following maven command:
``mvn graphql-codegen:generate``

Expect similar output:
````
[INFO] --- graphql-codegen:1.0-SNAPSHOT:generate (default-cli) @ sr-user-service ---
[INFO] Starting to generate GraphQL API(s) for '1' servers...
[INFO] Server: {"url":"http://localhost:8080/graphql","headers":["jwt:DAAAgadwqcEJTUIU54WFFYUJgsd","Content-Type:application/json;charset=UTF-8"],"la
nguage":"Java","resultClassName":"UserServiceGraphQLProvider","resultClassPackage":"com.maimas.generated","dir":"./src/test/java/com/maimas/generated
","httpHeaders":{"jwt":"DAAAgadwqcEJTUIU54WFFYUJgsd","Content-Type":"application/json;charset=UTF-8"}}

[INFO] GraphQL API class generated at ./src/test/java/com/maimas/generated
````

### How to use generated API:
- Note: generated class name is `UserServiceGraphQLProvider` 

> Mutation - create user
````

//Imports from the Generated API
import com.maimas.generated.UserServiceGraphQLProvider.Types.User;
import com.maimas.generated.UserServiceGraphQLProvider.Types.UserInput;
import static com.maimas.generated.UserServiceGraphQLProvider.*;

public class Examples {

    public void queryByUserId() throws JsonProcessingException {
        UserInput userInput = new UserInput();
        userInput.email = "tet11.test@test.com";
        userInput.password = "1234";

        GQLQuery query = new Mutation().create(input -> 
                input
                    .user(userInput),
                output -> output
                    .id()
                    .lastName()
                    .firstName()
                    .enablede()
                    .status());

        ResponseEntity<String> response = new RestTemplate()
                .postForEntity("http://localhost:8080/graphql", updateQuery.toString(), String.class);

        // User user = (User) new ObjectMapper().readValue(response.getBody(), updateQuery.getReturnType());
        //
        // updateQuery.getReturnType() - represents the TypeReference of the expected result 
        // that can be used if you want to convert result to and object

    }
}
```` 

> Query - get user by id

````
//Imports from the Generated API
import com.maimas.generated.UserServiceGraphQLProvider.Types.User;
import static com.maimas.generated.UserServiceGraphQLProvider.*;

public class Examples {

    public void queryByUserId() throws JsonProcessingException {
        

        GQLQuery query = new Query().findById(input -> 
                input
                    .id("123123"),
                output -> output
                     .email()
                     .roles()
                     .name());

        ResponseEntity<String> response = new RestTemplate()
                .postForEntity("http://localhost:8080/graphql", updateQuery.toString(), String.class);

        // User user = (User) new ObjectMapper().readValue(response.getBody(), updateQuery.getReturnType());
        //
        // updateQuery.getReturnType() - represents the TypeReference of the expected result
        // that can be used if you want to convert result to and object

    }
}
````

## Authors

* **Andrei Maimas**