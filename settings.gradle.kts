rootProject.name = "auth-forge"

include("examples:spring-boot-example")
include("core")

include("impls:impl-dynamodb")
include("impls:impl-postgres")