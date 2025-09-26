rootProject.name = "auth-forge"

include("core")

include("impls:impl-dynamodb")
include("impls:impl-postgres")
include("examples:spring-boot-example")
