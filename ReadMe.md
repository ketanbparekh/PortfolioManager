ReadMe.md

Overview

You can checkout the project and import as a Gradle project in the ide and run the AdvisorApplication class which will start the application. Once the application is running you can hit the rest point apis.

TechStack:- I have used Java11, SpringBoot, Guava rate limiter.

Improvements(TODO)

1. Create a separate class for Validation which can be auto wired. I am right now doing validation inside the controller class.
2. ReBalance api algorithm could be done better. 
3. Rate limiting requires more testing,
4. Junit and mock controller classes should be added.  

Rest api details:

I have created two rest end points

1. Client gives his risk level and rest api provides portfolio allocations as per his risk appetite.
	
	input:- GET: http://localhost:8080/portfolio/v1/riskscore/2
		    header : Content-Type: application/json

	output: AssetAllocation in %
{
    "riskLevel": 2,
    "bonds": 70,
    "largeCap": 15,
    "midCap": 0,
    "foreign": 0,
    "smallCap": 15
}

			 	

2. Client provides his investments in various categories along with his risk level. Rest api provides ideal balanced allocation if required as per his risk level with no of transactions required else returns the same allocation if it is good.
Input
POST: http://localhost:8080/portfolio/v1/rebalance
Content-Type:application/json
{	
	"riskLevel":"1",
	"transactionsToReBalance":"0",
    "bonds":"1000",
    "largeCap":"1000",
    "midCap":"0",
    "foreign":"1000",
    "smallCap":"0"

}

Output:

{
    "riskLevel": 1,
    "transactionsToReBalance": 2,
    "bonds": 2400,
    "largeCap": 600,
    "midCap": 0,
    "foreign": 0,
    "smallCap": 0
}
No of transactions required to reBalance is 2 here. If the input allocation was as per the risk level then No of transactions required to reBalance would be zero.

3. Rate Limiter:-

I have taken two approaches for Rate filter.

1. Create a Filter class RequestRateFilter which intercepts each request and if the clientIPAddress sends more than two requests in one seconds it returns HTTP_TO_MANY_REQUESTS (429)error. To test this a header “X-Forwarded-For”:<ipaddress> is also required. I have commented the few lines of code in  doFilter, you can uncomment it to test it.
2. I have also tried using Guava Rate limiter in Portfolio controller where 2 requests are permitted per second. Due to running short of time I could not give more time testing it.


