rootProject.name = "auth-forge"

include("core")

//include("impls:impl-dynamodb")
include("impls:impl-postgres")
include("impls:impl-postgres-tests")  // Java 17 test module
//include("examples:spring-boot-example")
