# Delivery Options API

This application is developped with Spring Boot and provides 2 REST API for customers to choose their preferred delivery methods(DRIVE, DELIVERY, DELIVERY_TODAY, and DELIVERY_ASAP) and specific time slots for their deliveries.

## Features

- **Choose Delivery Method**: Customers can select from DRIVE, DELIVERY, DELIVERY_TODAY, and DELIVERY_ASAP.
- **Book Time Slots**: Customers can choose specific time slots for the selected delivery method. Time slots availability updates in real-time and is specific to each delivery method.
- **Swagger openapi documentation**: Fully documented API accessible via Swagger UI.
- **In-Memory Database**: Using H2 database for persistence, facilitating quick setup and testing.
- **Kafka**: Using Kafka to post an event when a customer choose a slot.

## Technologies

- Spring Boot 3.2.2
- Spring Security
- Spring Reactive (Webflux) for non-blocking apis
- DDD Architecture
- H2 Database
- Swagger UI
- Java JDK 21
- Docker (for containerization and deployment)


## Setup

### Prerequisites

- Java 21 or above
- Maven
- Docker

### Running Locally

Clone the repository and navigate to the project directory:

```bash
git clone git@gitlab.com:mmkbennani/delivery-service.git
cd c4-SF-0310-SQ01
docker-compose up -d
mvn spring-boot:run

first call to choose option : 

curl --request POST \
  --url http://localhost:8082/v1/chooseOption \
  --header 'Authorization: Bearer ' \
  --header 'Content-Type: application/json' \
  --header 'X-Correlation-ID: 58b58376-d988-41e5-867a-afe24fa4fa5f' \
  --data '{
	  "customerId": 1546418,
	  "deliveryOption": "DRIVE"
	}'
we get 200 status code with a return the object : 
	{
	  "customerId": 1546418,
	  "deliveryOption": "DRIVE"
	}
When we use a option that is not permitted; we get error 400 	
curl --request POST \
  --url http://localhost:8082/v1/chooseOption \
  --header 'Authorization: Bearer ' \
  --header 'Content-Type: application/json' \
  --header 'X-Correlation-ID: 58b58376-d988-41e5-867a-afe24fa4fa5f' \
  --data '{
	  "customerId": 1546418,
	  "deliveryOption": "DRIVEB"
	}'
error 400 : 	
{
	"timestamp": "2024-05-04T13:22:26.124+00:00",
	"path": "/v1/chooseOption",
	"status": 400,
	"error": "Bad Request",
	"requestId": "f9c259dd-5"
}


second call to choose slot of delivery : 

curl --request POST \
  --url http://localhost:8082/v1/bookTimeSlot \
  --header 'Authorization: Bearer ' \
  --header 'Content-Type: application/json' \
  --header 'X-Correlation-ID: 58b58376-d988-41e5-867a-afe24fa4fa5f' \
  --data '{
      "timeSlotId": 18,
      "customerId": 1546418
    }'
** if  it's ok : we will get 200 status : 


{
	"startTime": "2024-05-05 14:00:00",
	"endTime": "2024-05-05 18:00:00",
	"customerId": 1546418,
	"slotId": 18
}

if the customer has already choosen the same slot with same delivery method,
we will get 409 error : 

{
	"title": "Time Slot already reserved",
	"details": "Customer 1546418 has already done reservation with id slot : 18"
}
