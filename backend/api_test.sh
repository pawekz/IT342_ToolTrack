.#!bin/bash

echo "This is a test file for User Api"


BASE_URL="http://localhost:8080"

#-X DELETE sends a DELETE request.
#-X POST sends a POST request with JSON data.
#-X GET sends a GET request.

#-H "Content-Type: application/json" sets the content type to JSON for POST requests.

#-d option specifies the JSON data for the request body in POST.
#-s hides certain output from curl that would normally be printed to the terminal..
#-o response.txt saves the response body to a file.
#-w "%{http_code}" extracts and prints the HTTP status cod

create_user() {

    email=$1       # First argument is assigned to EMAIL
    first_name=$2  # Second argument is assigned to FIRST_NAME
    last_name=$3   # Third argument is assigned to LAST_NAME
    password_hash=$4

	echo "Testing GET /addUser"

	# Send POST request and capture the HTTP response and body
	RESPONSE=$(curl -s -w "\nHTTP Status: %{http_code}\n" -X POST "$BASE_URL/addUser" \
	-H "Content-Type: application/json" \
	-d '{
	    "email": "'"$email"'",
	    "first_name": "'"$first_name"'",
	    "last_name": "'"$last_name"'",
	    "password_hash":"'"$password_hash"'"
	}')
	echo ""
   	echo "RESULT: $RESPONSE"
	# Check for HTTP Status Codes and print relevant messages
        HTTP_CODE=$(echo "$RESPONSE" | tail -n 1 | awk '{print $3}')
    
        if [ "$HTTP_CODE" -eq 201 ]; then
           echo "Success: User created."
        elif [ "$HTTP_CODE" -eq 400 ]; then
           echo "Error: Invalid input data."
        else
           echo "Unexpected response: $HTTP_CODE"
        fi
    echo ""

}

update_user() {

    email=$1       # First argument is assigned to EMAIL
    first_name=$2  # Second argument is assigned to FIRST_NAME
    last_name=$3   # Third argument is assigned to LAST_NAME
    password_hash=$4    #...
        echo "Testing PUT /updateUser"

        # Send POST request and capture the HTTP response and body
        RESPONSE=$(curl -s -w "\nHTTP Status: %{http_code}\n" -X PUT "$BASE_URL/updateUser" \
        -H "Content-Type: application/json" \
        -d '{
            "email": "'"$email"'",
            "first_name": "'"$first_name"'",
            "last_name": "'"$last_name"'",
	    "password_hash":"'"$password_hash"'"
        }')

        echo "RESULT: $RESPONSE"

        # Check for HTTP Status Codes and print relevant messages
        HTTP_CODE=$(echo "$RESPONSE" | tail -n 1 | awk '{print $3}')

        if [ "$HTTP_CODE" -eq 200 ]; then
           echo "User updated Successfully."
        elif [ "$HTTP_CODE" -eq 400 ]; then
           echo "Invalid Input data"
        else
           echo "Unexpected response: $HTTP_CODE"
        fi
    echo ""

}

delete_user() {

    email=$1  # First argument is assigned to EMAIL
        echo "Testing DELETE /deleteUser/$email"

        # Send POST request and capture the HTTP response and body
        RESPONSE=$(curl -s -w "\nHTTP Status: %{http_code}\n" -X DELETE "$BASE_URL/deleteUser/$email" \
        -H "Content-Type: application/json")

        echo "RESULT: $RESPONSE"

        # Check for HTTP Status Codes and print relevant messages
        HTTP_CODE=$(echo "$RESPONSE" | tail -n 1 | awk '{print $3}')

        if [ "$HTTP_CODE" -eq 200 ]; then
           echo "Deleted Successfully"
        elif [ "$HTTP_CODE" -eq 404 ]; then
           echo "User Not Found!"
        else
           echo "Unexpected response: $HTTP_CODE"
        fi
    echo ""

}



# Main script

create_user "johndoe26@gmail.com" "John" "Doe" "doe12345"

update_user "johndoe26@gmail.com" "John" "Vencent" "vencent1235"

delete_user "johndoe26@gmail.com"

echo "End Of Api Testing"
