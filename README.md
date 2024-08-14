## Calculating Loan
### Technologies Used
- Java 21
- Spring Boot
- Lombok
- Docker

### Endpoints
- POST /calcLoan/calculate - Calculo de empréstimo

### Docker Project Execution
```shell
docker pull samoellaureano/calc-emp
docker run -p 8080:8080 calc-emp
```
### Application URL
http://localhost:8080/

### Local Project Execution
```shell
mvn clean install
mvn spring-boot:run
```

### Collection Postman
- [Collection Postman](https://github.com/samoellaureano/Contas_a_pagar/blob/master/Desafio%20Backend.postman_collection.json)

### License
Distributed under the MIT License. See `LICENSE` for more information.

### Autor
Development by [Samoel Laureano Angélica](https://www.linkedin.com/in/samoellaureano/)