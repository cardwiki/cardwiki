cd "$(dirname "$0")"
mvn -q spring-boot:run -Dspring-boot.run.profiles=dev "$@"
