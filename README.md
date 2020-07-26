Http Request Error Handling Library
===========================
Library helps validate retrofit http errors. It has retorfit dependency.

How to add to your project
--------------

Add this to your gradle file and sync
```groovy

allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
    implementation 'com.github.gbksoft:android-error-parser:v1.0.0
}
```
Usage
---------
First of all you need to create **HttpRequestErrorParser** instance.
```kotlin
val errorParser = HttpRequestErrorParser(
    defaultErrorParser = { 
        //Insert parsing logic here.
        //It will be used as default for all error codes
        //This function receive String from errorBody
    },
    defaultErrorHandler = {
        //Insert handling logic here
        //It will be used as default for all error codes
        //This function receive Error object
    }
)
```
If you want one error code is parsed in some specific way 
you can set unique parser for some error code
```kotlin
errorParser.setParser(401) {
    //Do special parsing of 401 error
}
```
You can declare unique handler for some error code as well
```kotlin
errorParser.setHandler(401) {
    //Do special handling of 401 error
}
```
**HttpRequestErrorParser** instance is planed to be used for one logical module.
Like one **HttpRequestErrorParser** instance for one viewModel instance.

What if it is needed to override some error parser or handler? It is possible.
```kotlin
kotlin.runCatching {
    //Call request that might throw exception
}.onFailure {
    httpRequestErrorParser.uniqueParser()
        .setParser(401) { 
            //Override parsing of 401 error here
        }
        .setHandler(401) { 
            //Override handling 401 error here
        }
        .parse(it)
}
```

Non HTTP errors
---------
There are a lot of other errors might occure 
during a request apart from http errors. Library can hadle them.

The most pupular of them are **JsonSyntaxException** and **network connection problems**

If you want to handle **JsonSyntaxException** jsut do:
```kotlin
errorParser.setParser(NonHttpErrorCode.JSON_SYNTAX_ERROR) {}
errorParser.setHandler(NonHttpErrorCode.JSON_SYNTAX_ERROR) {}
```

If you want to handle network connection problems 
you need to add Interceptor to OkHttpClient
```kotlin
val connectivityManager = ConnectivityManager(context)
val httpClient = OkHttpClient.Builder()
httpClient.addInterceptor(ConnectivityInterceptor(connectivityManager))
```
After that you can handle connectivity problems in such way:
```kotlin
errorParser.setParser(NonHttpErrorCode.CODE_CONNECTION_ERROR) {}
errorParser.setHandler(NonHttpErrorCode.CODE_CONNECTION_ERROR) {}
```